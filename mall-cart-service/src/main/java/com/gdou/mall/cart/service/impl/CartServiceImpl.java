package com.gdou.mall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.gdou.mall.cart.mapper.OrderCartItemMapper;
import com.gdou.mall.pojo.OrderCartItem;
import com.gdou.mall.service.CartService;
import com.gdou.mall.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    OrderCartItemMapper orderCartItemMapper;

    @Autowired
    RedisUtil redisUtil;

    //根据用户id与Sku id查询购物车商品
    @Override
    public OrderCartItem getCartByUserId(Long userId, Long skuId) {
        OrderCartItem orderCartItem = new OrderCartItem();
        orderCartItem.setUserId(userId);
        orderCartItem.setProductSkuId(skuId);

        OrderCartItem selectOne = orderCartItemMapper.selectOne(orderCartItem);
        return selectOne;
    }

    //添加购物车商品
    @Override
    public int addCart(OrderCartItem orderCartItem) {
        int result = 0;
        if (orderCartItem.getUserId() != null) {
            result = orderCartItemMapper.insertSelective(orderCartItem);
        }
        return result;
    }

    //通过PK修改购物车商品
    @Override
    public int modifyCartByPK(OrderCartItem orderCartItem) {
        int result = orderCartItemMapper.updateByPrimaryKeySelective(orderCartItem);
        return result;
    }

    //刷新到NoSQL缓存，采用Key ：Map存储
    @Override
    public void flushCartCache(Long userId) {
        Jedis jedis = null;
        try {
            OrderCartItem orderCartItem = new OrderCartItem();
            orderCartItem.setUserId(userId);

            List<OrderCartItem> orderCartItemList = orderCartItemMapper.select(orderCartItem);

            Map<String, String> cartItemMap = new TreeMap<>();
            for (OrderCartItem cartItem : orderCartItemList) {
                cartItemMap.put(cartItem.getId() + "", JSON.toJSONString(cartItem));
            }

            jedis = redisUtil.getJedis();
            jedis.del("user:" + userId + ":cart");
            jedis.hmset("user:" + userId + ":cart", cartItemMap);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
    }

    //从NoSQL中获取用户购物车信息
    @Override
    public List<OrderCartItem> getCartByUserIdFromCache(Long userId) {
        Jedis jedis = null;
        List<OrderCartItem> orderCartItemList = new ArrayList<>();
        try {
            jedis = redisUtil.getJedis();
            List<String> hvals = jedis.hvals("user:" + userId + ":cart");
            for (String hval : hvals) {
                OrderCartItem orderCartItem = JSON.parseObject(hval, OrderCartItem.class);
                orderCartItemList.add(orderCartItem);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;

        } finally {
            jedis.close();
        }

        Collections.sort(orderCartItemList);
        return orderCartItemList;
    }

    //通过isCheck和Sku id修改购物车商品
    @Override
    public int modifyCartByIsCheckedAndSkuId(OrderCartItem orderCartItem) {
        Example example = new Example(OrderCartItem.class);
        example.createCriteria().andEqualTo("userId", orderCartItem.getUserId()).andEqualTo("productSkuId", orderCartItem.getProductSkuId());

        int result = orderCartItemMapper.updateByExampleSelective(orderCartItem, example);

        //同步缓存
        flushCartCache(orderCartItem.getUserId());
        return result;
    }

}
