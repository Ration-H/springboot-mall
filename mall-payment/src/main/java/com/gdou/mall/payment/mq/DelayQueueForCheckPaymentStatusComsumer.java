package com.gdou.mall.payment.mq;

import com.gdou.mall.pojo.PaymentInfo;
import com.gdou.mall.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.Date;
import java.util.Map;

@Component
public class DelayQueueForCheckPaymentStatusComsumer {

    @Autowired
    PaymentService paymentService;

    //支付状态检查
    @JmsListener(destination = "PAYMENT_CHECK_QUEUE", containerFactory = "jmsQueueListener")
    public void consumeDelayQueueForCheckPaymentStatus(MapMessage message) {
        try {
            //从队列中获取外部订单号与剩余消费次数
            String out_trade_no = message.getString("out_trade_no");
            int count = Integer.parseInt(message.getString("count"));

            if (count <= 0) {//消费次数用尽，直接返回
                System.out.println("次数用尽");
                return;
            }

            //根据外部订单号调用alipay API查询支付状态
            Map<String, Object> alipayQueryResult = paymentService.chcekAlipayPaymentStatus(out_trade_no);
            if (alipayQueryResult != null && alipayQueryResult.size() != 0) {//API查询到结果
                String trade_status = String.valueOf(alipayQueryResult.get("trade_status"));
                if (trade_status.equals("TRADE_SUCCESS")) {//支付成功
                    PaymentInfo paymentInfo = new PaymentInfo();
                    paymentInfo.setOutTradeNo(out_trade_no);
                    paymentInfo.setPaymentStatus("已支付");
                    paymentInfo.setAlipayTradeNo((String) alipayQueryResult.get("trade_no"));// 支付宝的交易凭证号
                    paymentInfo.setCallbackContent((String) alipayQueryResult.get(("call_back_content")));//回调请求字符串
                    paymentInfo.setCallbackTime(new Date());
                    paymentInfo.setPayType(1);
                    System.out.println("已经支付成功，调用支付服务，修改支付信息和发送支付成功的队列");
                    paymentService.updatePayment(paymentInfo);
                    return;
                }

                //用户未支付
                System.out.println("没有支付成功("+trade_status+") 检查剩余次数为"+count+",继续发送延迟检查任务");
                count--;
                paymentService.activateDelayQueueForCheckPaymentStatus(out_trade_no,count);

            }

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
