package com.gdou.mall.interceptors;

import com.alibaba.fastjson.JSON;
import com.gdou.mall.LoginRequired;
import com.gdou.mall.utils.CookieUtil;
import com.gdou.mall.utils.HttpclientUtil;
import com.gdou.mall.utils.Status;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取方法上的LoginRequire注解
        HandlerMethod method = (HandlerMethod ) handler;
        LoginRequired annotation = method.getMethodAnnotation(LoginRequired.class);

        if (annotation == null) {//方法上没有@LoginRequired，直接放行
            return true;
        }

        //获取token
        String token = "";
        String oldtoken = CookieUtil.getCookieValue(request, "oldToken", true);
        if (StringUtils.isNotBlank(oldtoken)) {
            token = oldtoken;
        }
        String newtoken = request.getParameter("token");
        if (StringUtils.isNotBlank(newtoken)) {
            token = newtoken;
        }

        Map<String, String> verifyResultMap = new HashMap<>();
        String status = Status.FAIL;
        //若客户端存在，解析token
        if (StringUtils.isNotBlank(token)) {//获取客户端ip
            String ip = request.getHeader("x-forwarded-for");
            if (StringUtils.isBlank(ip)) {
                ip = request.getRemoteAddr();
            }

            String verifyUrl = "http://localhost:8085/verify?token=" + token + "&currentIp=" + ip;
            String verifyJson = HttpclientUtil.doGet(verifyUrl);

            verifyResultMap = JSON.parseObject(verifyJson, Map.class);
            //获取结果
            status = verifyResultMap.get("status");

        }

        //判断方法是否必须要求登录
        if (annotation.mustLogin()) {//要求用户必须登录
            //检查status
            if (status.equals(Status.FAIL)) {//token不合法
                //重定向到登录界面
                StringBuffer ReturnUrl = request.getRequestURL();
                response.sendRedirect("http://localhost:8085/index?ReturnUrl=" + ReturnUrl);
                return false;
            }

            //token合法，将用户信息保存到服务器并将token写入Cookie
            request.setAttribute("userId", verifyResultMap.get("userId"));
            request.setAttribute("username", verifyResultMap.get("username"));
            CookieUtil.setCookie(request, response, "oldToken", token, 60 * 60 * 24, true);
        } else {//有@LoginRequired，但不是必须登录
            //判断token是否合法
            if (status.equals(Status.SUCCESS)) {//token合法,将用户数据写入服务器并将token写入Cookie
                request.setAttribute("userId", verifyResultMap.get("userId"));
                request.setAttribute("username", verifyResultMap.get("username"));
                CookieUtil.setCookie(request, response, "oldToken", token, 60 * 60 * 24, true);
            }
        }
        return true;
    }
}