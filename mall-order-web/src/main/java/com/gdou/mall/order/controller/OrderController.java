package com.gdou.mall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gdou.mall.LoginRequired;
import com.gdou.mall.pojo.OrderCartItem;
import com.gdou.mall.pojo.OrderInfo;
import com.gdou.mall.pojo.OrderItem;
import com.gdou.mall.pojo.UserReceiveAddress;
import com.gdou.mall.service.CartService;
import com.gdou.mall.service.OrderService;
import com.gdou.mall.service.SkuService;
import com.gdou.mall.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
public class OrderController {
    @Reference
    UserService userService;

    @Reference
    CartService cartService;

    @Reference
    SkuService skuService;

    @Reference
    OrderService orderService;

    //提交订单
    @RequestMapping("submitOrder")
    @LoginRequired(mustLogin = true)
    public ModelAndView submitOrder(HttpServletRequest request, Long deliveryAddressId, String paymentWay, String orderComment, String tradeCode,ModelMap modelMap) {
        String username = (String) request.getAttribute("username");
        String userId = (String) request.getAttribute("userId");
        ModelAndView modelAndView = new ModelAndView();
        //核对交易码
        boolean checkTradeCode = orderService.checkTradeCode(tradeCode, userId);
        if (!checkTradeCode) {//交易码非法，跳转tradeFail
            modelAndView.setViewName("tradeFail");
            modelMap.put("errMsg","重复提交订单");
            return modelAndView;
        }
        OrderInfo orderInfo = new OrderInfo();
        List<OrderItem> orderItemList = new ArrayList<>();
        BigDecimal totalPrices = new BigDecimal("0");

        //封装订单信息
        orderInfo.setAutoConfirmDay(7);
        orderInfo.setCreateTime(new Date());
        orderInfo.setDiscountAmount(null);
        orderInfo.setUserId(Long.valueOf(userId));
        orderInfo.setUserUsername(username);
        orderInfo.setNote(orderComment);
        String outTradeNo = "mall";
        outTradeNo = outTradeNo + System.currentTimeMillis();// 将毫秒时间戳拼接到外部订单号
        SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMDDHHmmss");
        outTradeNo = outTradeNo + sdf.format(new Date());// 将时间字符串拼接到外部订单号

        orderInfo.setOrderSn(outTradeNo);//外部订单号
        orderInfo.setStatus(0);//订单状态，0待付款
        orderInfo.setPayType(0);//支付方式，0未付款，1支付宝，2微信
        orderInfo.setPayAmount(totalPrices);
        orderInfo.setOrderType(0);
        UserReceiveAddress userReceiveAddress = userService.getReceiveAddressById(deliveryAddressId);
        orderInfo.setReceiverCity(userReceiveAddress.getCity());
        orderInfo.setReceiverDetailAddress(userReceiveAddress.getDetailAddress());
        orderInfo.setReceiverName(userReceiveAddress.getName());
        orderInfo.setReceiverPhone(userReceiveAddress.getPhoneNumber());
        orderInfo.setReceiverPostCode(userReceiveAddress.getPostCode());
        orderInfo.setReceiverProvince(userReceiveAddress.getProvince());
        orderInfo.setReceiverRegion(userReceiveAddress.getRegion());
        // 当前日期加一天，一天后配送
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 1);
        Date time = c.getTime();
        orderInfo.setReceiveTime(time);
        orderInfo.setSourceType(0);
        orderInfo.setTotalAmount(totalPrices);
        orderInfo.setModifyTime(new Date());

        //根据用户Id从NoSQL中查询出购物车信息
        List<OrderCartItem> orderCartItemList = cartService.getCartByUserIdFromCache(Long.valueOf(userId));
        //将购物车商品封装成订单商品
        for (OrderCartItem orderCartItem : orderCartItemList) {
            if (orderCartItem.getIsChecked().equals(1)) {
                OrderItem orderItem = new OrderItem();
                //查价
                boolean checkPrice = skuService.checkPrice(orderCartItem.getProductSkuId(), orderCartItem.getPrice());
                if (!checkPrice) {//价格不一致,跳转失败页面
                    modelAndView.setViewName("tradeFail");
                    modelMap.put("errMsg","商品价格变了");
                    return modelAndView;
                }
                //计总价
                totalPrices = totalPrices.add(orderCartItem.getTotalPrice());

                //TODO 核对库存
                //查库存

                //将购物车商品封装成订单商品
                orderItem.setProductPic(orderCartItem.getProductPic());
                orderItem.setProductName(orderCartItem.getProductName());
                orderItem.setOrderSn(outTradeNo);// 外部订单号，用来和其他系统进行交互，防止重复
                orderItem.setProductCategoryId(orderCartItem.getProductCategoryId());
                orderItem.setProductPrice(orderCartItem.getPrice());
                orderItem.setRealAmount(orderCartItem.getTotalPrice());
                orderItem.setProductQuantity(orderCartItem.getQuantity());
                orderItem.setProductSkuCode("未开发此功能");
                orderItem.setProductSkuId(orderCartItem.getProductSkuId());
                orderItem.setProductId(orderCartItem.getProductId());
                orderItemList.add(orderItem);
            }
        }
        //将订单商品封装到订单中
        orderInfo.setOrderItemList(orderItemList);
        orderInfo.setTotalAmount(totalPrices);

        //在DB中生成订单,并删除购物车相应的商品 TODO 开发阶段，暂不删除购物车上的商品
        int result = orderService.addOrderInfo(orderInfo);


        //重定向到支付页面
        modelAndView.setViewName("redirect:http://localhost:8087/index");
        modelAndView.addObject("outTradeNo",outTradeNo);
        modelAndView.addObject("totalPrices",totalPrices);
        if(orderItemList.size()>1) {
            modelAndView.addObject("subject", orderItemList.get(0).getProductName()+"等");
        }else{
            modelAndView.addObject("subject", orderItemList.get(0).getProductName());
        }

        return modelAndView;
    }

    //去结算页面
    @RequestMapping("toTrade")
    @LoginRequired(mustLogin = true)
    public String toTrade(HttpServletRequest request, ModelMap modelMap) {
        //查询结算的商品与用户地址信息
        String userId = (String) request.getAttribute("userId");
        String username = String.valueOf(request.getAttribute("username"));
        List<OrderItem> orderItemList = new ArrayList<>();
        BigDecimal totalPrices = new BigDecimal("0");
        //查询用户地址信息
        List<UserReceiveAddress> userReceiveAddressList = userService.getUserReceiveAddressByUserId(Long.valueOf(userId));
        //查询用户购物车所有的商品信息
        List<OrderCartItem> cartByUserIdFromCache = cartService.getCartByUserIdFromCache(Long.valueOf(userId));


        for (OrderCartItem orderCartItem : cartByUserIdFromCache) {
            //将勾选的添加到orderItemList中
            if (orderCartItem.getIsChecked().equals(1)) {
                OrderItem orderItem = new OrderItem();
                orderItem.setProductName(orderCartItem.getProductName());
                orderItem.setProductPic(orderCartItem.getProductPic());
                orderItem.setProductPrice(orderCartItem.getPrice());
                orderItem.setProductQuantity(orderCartItem.getQuantity());
                orderItemList.add(orderItem);
                totalPrices = totalPrices.add(orderCartItem.getTotalPrice());
            }
        }

        //生成交易码
        String tradeCode = orderService.generateTradeCode(Long.valueOf(userId));

        modelMap.put("tradeCode", tradeCode);
        modelMap.put("userAddressList", userReceiveAddressList);
        modelMap.put("orderDetailList", orderItemList);
        modelMap.put("totalPrices", totalPrices);
        return "trade";
    }
}
