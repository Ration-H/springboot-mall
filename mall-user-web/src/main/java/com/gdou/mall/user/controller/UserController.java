package com.gdou.mall.user.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.gdou.mall.pojo.UserInfo;
import com.gdou.mall.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class UserController {

    @Reference
    UserService userService;

    @RequestMapping("getAll")
    @ResponseBody
    public List<UserInfo> getAll(){
        List<UserInfo> userList=userService.getAll();
        return userList;
    }
}
