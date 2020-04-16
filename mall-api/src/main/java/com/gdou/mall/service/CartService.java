package com.gdou.mall.service;

import com.gdou.mall.pojo.OrderCartItem;

import java.util.List;

public interface CartService {
    OrderCartItem getCartByUserId(Long userId, Long skuId);

    int addCart(OrderCartItem orderCartItem);

    int modifyCartByPK(OrderCartItem orderCartItemFromDB);

    void flushCartCache(Long userId);

    List<OrderCartItem> getCartByUserIdFromCache(Long userId);

    int modifyCartByIsCheckedAndSkuId(OrderCartItem orderCartItem);
}
