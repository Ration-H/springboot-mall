package com.gdou.mall.user.service.impl;

import com.gdou.mall.service.UserService;
import com.gdou.mall.user.mapper.UserMapper;
import com.gdou.mall.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public List<User> getAll() {
        List<User> users = userMapper.selectAll();
        return users;
    }
}
