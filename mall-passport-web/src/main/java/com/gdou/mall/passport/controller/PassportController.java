package com.gdou.mall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.gdou.mall.pojo.OrderCartItem;
import com.gdou.mall.pojo.UserInfo;
import com.gdou.mall.service.CartService;
import com.gdou.mall.service.UserService;
import com.gdou.mall.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PassportController {
    @Reference
    UserService userService;

    @Reference
    CartService cartService;

    //微博社交登录
    @RequestMapping("weiboLogin")
    public String weiboLogin(String code, HttpServletRequest request, HttpServletResponse response) {
        //使用code交换access_token
        String access_tokenUrl = "https://api.weibo.com/oauth2/access_token?";
        //根据API封装请求参数
        Map<String, String> access_tokenMap = new HashMap<>();
        access_tokenMap.put("client_id", "316634737");
        access_tokenMap.put("client_secret", "d1df83a94b6a3037eaea5948b1b7e970");
        access_tokenMap.put("grant_type", "authorization_code");
        access_tokenMap.put("code", code);
        access_tokenMap.put("redirect_uri", "http://127.0.0.1:8085/weiboLogin");

        //发送请求，并获取access_token与uid
        String access_token_json = HttpclientUtil.doPost(access_tokenUrl, access_tokenMap);
        System.out.println(access_token_json);
        Map<String, String> access_map = JSON.parseObject(access_token_json, Map.class);

        // 根据API封装请求参数，access_token换取用户信息
        String uid = access_map.get("uid");
        String access_token = access_map.get("access_token");
        //发送请求，获取用户信息
        String show_user_url = "https://api.weibo.com/2/users/show.json?access_token=" + access_token + "&uid=" + uid;
        String weiboUser_json = HttpclientUtil.doGet(show_user_url);
        System.out.println(weiboUser_json);
        Map<String, Object> weiboUser_map = JSON.parseObject(weiboUser_json, Map.class);

        UserInfo userInfo = new UserInfo();
        userInfo.setSourceUid(String.valueOf(weiboUser_map.get("idstr")));
        userInfo.setAccessCode(code);
        userInfo.setAccessToken(access_token);
        //从数据库查询该用户之前是否登录过，未登录则将用户信息包装到数据库；登录过则使用数据库信息
        UserInfo userInfoFromDB = userService.getOauthUserInfoByUid(uid);
        if (userInfoFromDB == null) {//该微博用户第一次登录本网站,将用户信息保存到DB
            userInfo.setUsername(String.valueOf(weiboUser_map.get("screen_name")));
            userInfo.setNickname(String.valueOf(weiboUser_map.get("name")));
            userInfo.setSourceType(1);
            userInfo.setHeadImg((String) weiboUser_map.get("profile_image_url"));
            userInfo.setCreateTime(new Date());
            userInfo.setCity((String) weiboUser_map.get("location"));
            String gender = String.valueOf(weiboUser_map.get("gender"));
            if (gender.equals("n")) {
                userInfo.setGender(0);
            } else if (gender.equals("m")) {
                userInfo.setGender(1);
            } else if (gender.equals("f")) {
                userInfo.setGender(2);
            }
            userInfo = userService.addOauthUser(userInfo);
        } else {//该微博用户之前登录过，不必添加信息
            userInfo = userInfoFromDB;
        }


        // 生成jwt的token，并且重定向到首页，携带该token
        String token = null;
        Long userId = userInfo.getId();// rpc的主键返回策略失效
        String username = userInfo.getUsername();
        Map<String,Object> userMap = new HashMap<>();
        userMap.put("username",username);// 是保存数据库后主键返回策略生成的id
        userMap.put("userId",userId);


        String ip = request.getHeader("x-forwarded-for");//使用Nginx代理，获取客户端IP
        if (StringUtils.isBlank(ip)) {//未使用Nginx代理
            ip = request.getRemoteAddr();
            if(StringUtils.isBlank(ip)||ip.equals("0:0:0:0:0:0:0:1")){
                ip = "127.0.0.1";
            }
        }

        // 按照设计的算法对参数进行加密后，生成token
        token = JwtUtil.encode("com.gdou.mall", userMap, ip);

        // 将token存入redis一份
        userService.addUserToken(userId,token);

        //合并购物车
        combatCart(userId,request,response);

        //同步缓存
        cartService.flushCartCache(userId);

        return "redirect:http://localhost:8083/index?token="+token;
    }

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
                resultMap.put("userId", decodeTokenMap.get("userId") + "");
                resultMap.put("status", Status.SUCCESS);
            } else {//token不合法
                resultMap.put("status", Status.FAIL);
            }
        }

        //System.out.println(JSON.toJSONString(resultMap));
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
    public String login(UserInfo userInfo, HttpServletRequest request,HttpServletResponse response) {
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
                if(StringUtils.isBlank(ip)||ip.equals("0:0:0:0:0:0:0:1")){
                    ip = "127.0.0.1";
                }
            }
            //使用Jwt加密，生成token
            token = JwtUtil.encode("com.gdou.mall", userInfoMap, ip);
            //将token存入NoSQL中
            String isOK = userService.addUserToken(isUserInfoExist.getId(), token);

            //合并购物车
            combatCart(isUserInfoExist.getId(),request,response);

            //同步缓存
            cartService.flushCartCache(Long.valueOf(isUserInfoExist.getId()));
        }

        return token;
    }

    //合并购物车
    private void combatCart(Long userId,HttpServletRequest request,HttpServletResponse response){
        //获取购物车Cookie
        String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
        if(StringUtils.isNotBlank(cartListCookie)){//购物车不为空，清空Cookie并将Cookie购物车商品添加到用户购物车中
            List<OrderCartItem> orderCartItemList=JSON.parseArray(cartListCookie,OrderCartItem.class);
            //清空购物车Cookie
            CookieUtil.setCookie(request, response, "cartListCookie", "", 0, true);
            for (OrderCartItem cartItem : orderCartItemList) {//合并购物车
                //从DB中查出用户的购物车商品
                OrderCartItem orderCartItemFromDB = cartService.getCartByUserId(Long.valueOf(userId), cartItem.getProductSkuId());
                //判断用户之前是否添加过该商品
                if (orderCartItemFromDB == null) {//未添加
                    //为商品添加用户信息
                    cartItem.setUserId(Long.valueOf(userId));
                    cartItem.setUserNickname(userId + "的名字");
                    int result = cartService.addCart(cartItem);
                } else {//已添加,修改该商品的数量及总价
                    orderCartItemFromDB.setQuantity(orderCartItemFromDB.getQuantity() + cartItem.getQuantity());
                    orderCartItemFromDB.setTotalPrice(orderCartItemFromDB.getTotalPrice().add(cartItem.getTotalPrice()));
                    int result = cartService.modifyCartByPK(orderCartItemFromDB);
                }
            }
        }
    }

}
