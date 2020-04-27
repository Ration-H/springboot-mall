package com.gdou.mall.payment.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.gdou.mall.LoginRequired;
import com.gdou.mall.payment.config.AlipayConfig;
import com.gdou.mall.pojo.OrderInfo;
import com.gdou.mall.pojo.PaymentInfo;
import com.gdou.mall.service.OrderService;
import com.gdou.mall.service.PaymentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PaymentController {
    @Autowired
    AlipayClient alipayClient;

    @Autowired
    PaymentService paymentService;

    @Reference
    OrderService orderService;

    //支付成功，回调页面
    @RequestMapping("alipay/callback/return")
    @LoginRequired(mustLogin = true)
    public String aliPayCallBackReturn(HttpServletRequest request){

        // 回调请求中获取支付宝参数
        String sign = request.getParameter("sign");
        String trade_no = request.getParameter("trade_no");
        String out_trade_no = request.getParameter("out_trade_no");
        String trade_status = request.getParameter("trade_status");
        String total_amount = request.getParameter("total_amount");
        String subject = request.getParameter("subject");
        String call_back_content = request.getQueryString();


        // 通过支付宝的paramsMap进行签名验证，2.0版本的接口将paramsMap参数去掉了，导致同步请求没法验签
        if(StringUtils.isNotBlank(sign)){
            // 验签成功
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setOutTradeNo(out_trade_no);
            paymentInfo.setPaymentStatus("已支付");
            paymentInfo.setAlipayTradeNo(trade_no);// 支付宝的交易凭证号
            paymentInfo.setCallbackContent(call_back_content);//回调请求字符串
            paymentInfo.setCallbackTime(new Date());
            paymentInfo.setPayType(1);
            // 更新用户的支付成功状态
            paymentService.updatePayment(paymentInfo);

        }

        return "finish";
    }

    //支付宝支付
    @RequestMapping("alipay/submit")
    @LoginRequired(mustLogin = true)
    @ResponseBody
    public String alipay(String outTradeNo, BigDecimal totalPrices, String subject) {
        int result = 0;

        //alipay调用
        String form = null;
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();

        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);

        Map<String, Object> map = new HashMap<>();
        map.put("out_trade_no", outTradeNo);
        map.put("product_code", "FAST_INSTANT_TRADE_PAY");
        map.put("total_amount", 0.01);
        map.put("subject", subject);

        String param = JSON.toJSONString(map);

        alipayRequest.setBizContent(param);

        try {
            form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        //根据outTradeNo查询订单信息
        OrderInfo orderInfo = orderService.getOrderInfoByOutTradeNo(outTradeNo);

        //封装PaymentInfo信息
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(orderInfo.getId());
        paymentInfo.setOutTradeNo(outTradeNo);
        paymentInfo.setPaymentStatus("未付款");
        paymentInfo.setSubject(subject);
        paymentInfo.setTotalAmount(totalPrices);
        //DB添加支付信息
        result +=paymentService.savePaymentInfo(paymentInfo);

        //发起一个支付的延迟队列 "DelayPaymentResultCheckQueue"
        paymentService.activateDelayQueueForCheckPaymentStatus(outTradeNo,5);

        // 提交请求到支付宝
        return form;
    }

    //微信支付，未开发
    @RequestMapping("wechatpay/submit")
    @LoginRequired(mustLogin = true)
    public String wechatpay() {
        return null;
    }

    //跳转到支付页面
    @RequestMapping("index")
    @LoginRequired(mustLogin = true)
    public String index(String outTradeNo, BigDecimal totalPrices, String subject, ModelMap modelMap, HttpServletRequest request) {
        String username = String.valueOf(request.getAttribute("username"));

        modelMap.put("username", username);
        modelMap.put("outTradeNo", outTradeNo);
        modelMap.put("totalPrices", totalPrices);
        modelMap.put("subject", subject);
        return "index";
    }
}
