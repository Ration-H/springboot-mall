package com.gdou.mall.user.service.impl;

import com.gdou.mall.pojo.UserInfo;
import com.gdou.mall.user.mapper.UserMapper;
import com.gdou.mall.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl{

    @Autowired
    UserMapper userMapper;

    @Autowired
    RedisUtil redisUtil;

    public List<UserInfo> getAll() {
        List<UserInfo> users = userMapper.selectAll();
        return users;
    }


}
