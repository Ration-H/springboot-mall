package com.gdou.mall.service;

import com.gdou.mall.pojo.PaymentInfo;

import java.util.Map;

public interface PaymentService {
    int savePaymentInfo(PaymentInfo paymentInfo);

    int updatePayment(PaymentInfo paymentInfo);

    void activateDelayQueueForCheckPaymentStatus(String outTradeNo, int count);

    Map<String, Object> chcekAlipayPaymentStatus(String out_trade_no);
}
