package com.gdou.mall.order.mq;

import com.gdou.mall.pojo.OrderInfo;
import com.gdou.mall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.math.BigDecimal;

@Component
public class OrderServiceMqComsumer {
    @Autowired
    OrderService orderService;

    //消费"PAYMENT_SUCCESS_QUEUE"，及更新订单状态、支付类型
    @JmsListener(destination = "PAYMENT_SUCCESS_QUEUE", containerFactory = "jmsQueueListener")
    public void consumePaymentResult(MapMessage mapMessage) throws JMSException {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderSn(mapMessage.getString("out_trade_no"));
        orderInfo.setPayType(Integer.valueOf(mapMessage.getString("pay_type")));
        orderInfo.setStatus(1);
        orderInfo.setPayAmount(new BigDecimal(mapMessage.getString("total_amount")));
        int result = orderService.updateOrderInfo(orderInfo);
    }
}
