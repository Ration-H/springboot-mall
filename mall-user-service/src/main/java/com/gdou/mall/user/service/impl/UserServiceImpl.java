package com.gdou.mall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.gdou.mall.pojo.UserInfo;
import com.gdou.mall.pojo.UserReceiveAddress;
import com.gdou.mall.service.UserService;
import com.gdou.mall.user.mapper.UserMapper;
import com.gdou.mall.user.mapper.UserReceiveAddressMapper;
import com.gdou.mall.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    UserReceiveAddressMapper userReceiveAddressMapper;

    //通过用户Id获取收获地址
    @Override
    public List<UserReceiveAddress> getUserReceiveAddressByUserId(Long userId) {
        UserReceiveAddress userReceiveAddress = new UserReceiveAddress();
        userReceiveAddress.setUserId(userId);
        List<UserReceiveAddress> userReceiveAddressList = userReceiveAddressMapper.select(userReceiveAddress);
        return userReceiveAddressList;
    }

    //根据地址Id查询用户收货地址信息
    @Override
    public UserReceiveAddress getReceiveAddressById(Long deliveryAddressId) {
        UserReceiveAddress userReceiveAddress = userReceiveAddressMapper.selectByPrimaryKey(deliveryAddressId);
        return userReceiveAddress;
    }

    //根据Uid查询社交登录账号
    @Override
    public UserInfo getOauthUserInfoByUid(String uid) {
        UserInfo userInfo = new UserInfo();
        userInfo.setSourceUid(uid);
        userInfo = userMapper.selectOne(userInfo);
        return userInfo;
    }

    //添加社交登录用户
    @Override
    public UserInfo addOauthUser(UserInfo userInfo) {
        userMapper.insertSelective(userInfo);
        return userInfo;
    }

    @Override
    public List<UserInfo> getAll() {
        List<UserInfo> users = userMapper.selectAll();
        return users;
    }

    //通过用户名及密码从NoSQL中查询用户
    @Override
    public UserInfo login(UserInfo userInfo) {
        Jedis jedis = null;
        UserInfo isUserInfoExist = null;

        try {
            //从NoSQL中获取用户信息
            jedis = redisUtil.getJedis();
            String username = userInfo.getUsername();
            String password = userInfo.getPassword();
            String userInfoStr = jedis.get("user:" + username + " " + password + ":info");
            if (StringUtils.isNotBlank(userInfoStr)) {//NoSQL存在该用户
                isUserInfoExist = JSON.parseObject(userInfoStr, UserInfo.class);
                return isUserInfoExist;
            }
            //NoSQL不存在或者连接失败，从DB中查询
            isUserInfoExist = loginFromDB(userInfo);
            //判断该用户存不存在
            if (isUserInfoExist != null) {//存在，存入NoSQL
                String userInfoJson = JSON.toJSONString(isUserInfoExist);
                jedis.setex("user:" + username + " " + password + ":info", 60 * 60 * 24, userInfoJson);
            }

            return isUserInfoExist;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

    //通过用户名及密码从NoSQL中查询用户
    public UserInfo loginFromDB(UserInfo userInfo) {
        UserInfo isUserInfoExist = userMapper.selectOne(userInfo);
        return isUserInfoExist;
    }

    //登录成功，NoSQL添加Token
    @Override
    public String addUserToken(Long userId, String token) {
        Jedis jedis = null;
        String reuslt = "error";
        try {
            jedis = redisUtil.getJedis();
            String result = jedis.setex("user:" + userId + ":token", 60 * 60 * 2, token);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return reuslt;
    }

}
