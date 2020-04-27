package com.gdou.mall.service;

import com.gdou.mall.pojo.OrderInfo;

public interface OrderService {
    int addOrderInfo(OrderInfo orderInfo);

    boolean checkTradeCode(String tradeCode,String userId);

    String generateTradeCode(Long userId);

    OrderInfo getOrderInfoByOutTradeNo(String outTradeNo);

    int updateOrderInfo(OrderInfo orderInfo);
}
