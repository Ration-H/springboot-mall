package com.gdou.mall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.gdou.mall.pojo.UserInfo;
import com.gdou.mall.service.UserService;
import com.gdou.mall.utils.JwtUtil;
import com.gdou.mall.utils.Status;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PassportController {
    @Reference
    UserService userService;

    //核查客户端发送的token是否合法
    @RequestMapping("verify")
    @ResponseBody
    public String verify(String token, String currentIp) {

        Map<String, String> resultMap = new HashMap<>();
        if (StringUtils.isNotBlank(token) && StringUtils.isNotBlank(currentIp)) {
            //解析token
            Map<String, Object> decodeTokenMap = JwtUtil.decode(token, "com.gdou.mall", currentIp);
            //判断解析结果
            if (decodeTokenMap != null && decodeTokenMap.size() != 0) {//token合法
                resultMap.put("username", (String) decodeTokenMap.get("username"));
                resultMap.put("userId", decodeTokenMap.get("userId")+"");
                resultMap.put("status", Status.SUCCESS);
            } else {//token不合法
                resultMap.put("status", Status.FAIL);
            }
        }

        System.out.println(JSON.toJSONString(resultMap));
        return JSON.toJSONString(resultMap);
    }

    //登录首页
    @RequestMapping("index")
    public String index(String ReturnUrl, ModelMap modelMap) {
        modelMap.put("ReturnUrl", ReturnUrl);
        return "index";
    }

    //用户登录，检查账户是否存在
    @RequestMapping("login")
    @ResponseBody
    public String login(UserInfo userInfo, HttpServletRequest request) {
        String token = "fail";

        //查询用户
        UserInfo isUserInfoExist = userService.login(userInfo);

        //判断该用户是否存在
        if (null != isUserInfoExist) {//用户存在，为用户生成token
            //使用username、userId、客户端ip、"com.gdou.mall"加密
            HashMap<String, Object> userInfoMap = new HashMap<>();
            userInfoMap.put("username", isUserInfoExist.getUsername());
            userInfoMap.put("userId", isUserInfoExist.getId());
            //使用客户端IP组成salt
            String ip = request.getHeader("x-forwarded-for");//使用Nginx代理，获取客户端IP
            if (StringUtils.isBlank(ip)) {//未使用Nginx代理
                ip = request.getRemoteAddr();
            }
            //使用Jwt加密，生成token
            token = JwtUtil.encode("com.gdou.mall", userInfoMap, ip);
            //将token存入NoSQL中
            String isOK = userService.addUserToken(isUserInfoExist.getId(), token);
        }

        return token;
    }
}
