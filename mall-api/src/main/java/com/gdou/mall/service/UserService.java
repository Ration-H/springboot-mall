package com.gdou.mall.service;

import com.gdou.mall.pojo.UserInfo;
import com.gdou.mall.pojo.UserReceiveAddress;

import java.util.List;

public interface UserService {
    List<UserInfo> getAll();

    UserInfo login(UserInfo userInfo);

    String addUserToken(Long userId, String token);

    List<UserReceiveAddress> getUserReceiveAddressByUserId(Long userId);

    UserReceiveAddress getReceiveAddressById(Long deliveryAddressId);
}
