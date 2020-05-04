package com.gdou.mall.service;

import com.gdou.mall.pojo.OrderInfo;

import java.util.List;

public interface OrderService {
    int addOrderInfo(OrderInfo orderInfo);

    boolean checkTradeCode(String tradeCode,String userId);

    String generateTradeCode(Long userId);

    OrderInfo getOrderInfoByOutTradeNo(String outTradeNo);

    int updateOrderInfo(OrderInfo orderInfo);

    List<OrderInfo> getOrderList();

    List<OrderInfo> getOrderByCondition(String username, String startDate, String endDate);
}
