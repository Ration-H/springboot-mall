package com.gdou.mall.order.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.gdou.mall.order.mapper.OrderInfoMapper;
import com.gdou.mall.order.mapper.OrderItemMapper;
import com.gdou.mall.pojo.OrderInfo;
import com.gdou.mall.pojo.OrderItem;
import com.gdou.mall.service.CartService;
import com.gdou.mall.service.OrderService;
import com.gdou.mall.utils.ActiveMQUtil;
import com.gdou.mall.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import javax.jms.Connection;
import javax.jms.JMSException;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderInfoMapper orderInfoMapper;

    @Autowired
    OrderItemMapper orderItemMapper;

    @Reference
    CartService cartService;//购物删除已购买的商品，还未实现，

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    ActiveMQUtil activeMQUtil;

    @Override
    public int addOrderInfo(OrderInfo orderInfo) {
        int result = 0;
        result += orderInfoMapper.insertSelective(orderInfo);
        Long orderInfoId = orderInfo.getId();
        List<OrderItem> orderItemList = orderInfo.getOrderItemList();
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderId(orderInfoId);
            result += orderItemMapper.insertSelective(orderItem);

            //删除购物车相应的商品
            //result += cartService.delCartInfo(orderItem.getProductSkuId());
        }

        return result;
    }

    //核对交易码
    @Override
    public boolean checkTradeCode(String tradeCode, String userId) {
        Jedis jedis = null;

        try {
            jedis = redisUtil.getJedis();
            String tradeKey = "user:" + userId + ":tradeCode";
            // 使用lua脚本在发现key的同时将key删除，防止并发订单攻击
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Long eval = (Long) jedis.eval(script, Collections.singletonList(tradeKey), Collections.singletonList(tradeCode));
            if (eval != null && eval != 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
        }
    }

    //生成交易码
    @Override
    public String generateTradeCode(Long userId) {
        Jedis jedis = null;

        try {
            jedis = redisUtil.getJedis();
            String tradeCode = UUID.randomUUID().toString();
            jedis.setex("user:" + userId + ":tradeCode", 60 * 15, tradeCode);
            return tradeCode;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    //根据外部订单号查询订单
    @Override
    public OrderInfo getOrderInfoByOutTradeNo(String outTradeNo) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderSn(outTradeNo);
        orderInfo = orderInfoMapper.selectOne(orderInfo);
        return orderInfo;
    }

    //更新订单信息，及生产 ORDER_PAYED_QUEUE，消费者库存服务
    @Override
    public int updateOrderInfo(OrderInfo orderInfo) {
        int result = 0;
        Connection connection=null;

        Example example = new Example(OrderInfo.class);
        example.createCriteria().andEqualTo("orderSn", orderInfo.getOrderSn());

       String message="订单已支付，叫物流尽快发货";

        try {
            result += orderInfoMapper.updateByExampleSelective(orderInfo, example);
            connection=activeMQUtil.getConnectionFactory().createConnection();
            activeMQUtil.producerSendText(connection,"ORDER_PAYED_QUEUE",message);
        } catch (JMSException e) {
            e.printStackTrace();
        }finally {
            if (connection!=null){
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    //获取所有订单信息
    @Override
    public List<OrderInfo> getOrderList() {
        return orderInfoMapper.selectAll();
    }

    //根据查询条件查询订单
    @Override
    public List<OrderInfo> getOrderByCondition(String username, String startDate, String endDate) {
        Example example = new Example(OrderInfo.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(username)){
            criteria.andLike("userUsername", "%"+username+"%");
        }
        if (StringUtils.isNotBlank(startDate)&&StringUtils.isNotBlank(endDate)){
            criteria.andBetween("modifyTime",startDate,endDate);
        }
        List<OrderInfo> orderInfoList = orderInfoMapper.selectByExample(example);
        return orderInfoList;
    }

}
