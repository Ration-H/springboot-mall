package com.gdou.mall;


import com.alibaba.fastjson.JSON;
import com.gdou.mall.pojo.UserInfo;
import com.gdou.mall.utils.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MallUserServiceApplicationTests {
    @Autowired
    RedisUtil redisUtil;


    @Test
    public void contextLoads() {
        Jedis jedis = redisUtil.getJedis();
        String sss = jedis.get("sss");
        UserInfo userInfo = JSON.parseObject(sss, UserInfo.class);
        if(userInfo==null){
            System.out.println("真null");
        }
    }

}
