package com.gdou.mall.payment.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.gdou.mall.payment.mapper.PaymentInfoMapper;
import com.gdou.mall.pojo.PaymentInfo;
import com.gdou.mall.service.PaymentService;
import com.gdou.mall.utils.ActiveMQUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import javax.jms.Connection;
import javax.jms.JMSException;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    PaymentInfoMapper paymentInfoMapper;

    @Autowired
    ActiveMQUtil activeMQUtil;

    @Autowired
    AlipayClient alipayClient;

    //添加支付信息
    @Override
    public int savePaymentInfo(PaymentInfo paymentInfo) {
        return paymentInfoMapper.insertSelective(paymentInfo);
    }

    //更新支付信息,及发送"PAYMENT_SUCCESS_QUEUE"消息，消费者为orderService
    @Override
    public int updatePayment(PaymentInfo paymentInfo) {
        int reslut = 0;
        // 幂等性检查
        PaymentInfo paymentInfoParam = new PaymentInfo();
        paymentInfoParam.setOutTradeNo(paymentInfo.getOutTradeNo());
        PaymentInfo paymentInfoResult = paymentInfoMapper.selectOne(paymentInfoParam);
        if (StringUtils.isNotBlank(paymentInfoResult.getPaymentStatus()) && paymentInfoResult.getPaymentStatus().equals("已支付")) {//已修改，不再修改
            return reslut;
        }

        Connection connection = null;


        String out_trade_no = paymentInfo.getOutTradeNo();
        Example example = new Example(PaymentInfo.class);
        example.createCriteria().andEqualTo("outTradeNo", out_trade_no);

        Map<String, String> message = new HashMap<>();
        message.put("out_trade_no", out_trade_no);
        message.put("pay_type", paymentInfo.getPayType() + "");
        message.put("total_amount",paymentInfo.getTotalAmount().toString());

        try {
            //更新支付信息并发送消息
            connection = activeMQUtil.getConnectionFactory().createConnection();
            reslut += paymentInfoMapper.updateByExampleSelective(paymentInfo, example);
            activeMQUtil.producerSendMap(connection, "PAYMENT_SUCCESS_QUEUE", message);
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
        return reslut;
    }

    //发起付款查询的延迟队列 "PAYMENT_CHECK_QUEUE"
    @Override
    public void activateDelayQueueForCheckPaymentStatus(String outTradeNo, int count) {
        Connection connection = null;

        Map<String, String> message = new HashMap<>();
        message.put("out_trade_no", outTradeNo);
        message.put("count", String.valueOf(count));

        try {
            connection = activeMQUtil.getConnectionFactory().createConnection();
            activeMQUtil.producerSendMap(connection, "PAYMENT_CHECK_QUEUE", message, true, 1000 * 20);
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //根据外部订单号调用AliPay API查询支付状态
    @Override
    public Map<String, Object> chcekAlipayPaymentStatus(String out_trade_no) {
        AlipayTradeQueryResponse queryResult = null;
        Map<String, Object> resultMap = new HashMap<>();
        AlipayTradeQueryRequest queryRequest = new AlipayTradeQueryRequest();
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("out_trade_no", out_trade_no);
        queryRequest.setBizContent(JSON.toJSONString(paramMap));
        try {
            queryResult = alipayClient.execute(queryRequest);
            if (queryResult.isSuccess()) {
                resultMap.put("out_trade_no", queryResult.getOutTradeNo());
                resultMap.put("trade_no", queryResult.getTradeNo());
                resultMap.put("trade_status", queryResult.getTradeStatus());
                resultMap.put("call_back_content", queryResult.getMsg());
            } else {
                System.out.println("有可能交易未创建，调用失败");
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return resultMap;
    }

}
