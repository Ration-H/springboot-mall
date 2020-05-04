package com.gdou.mall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.gdou.mall.LoginRequired;
import com.gdou.mall.pojo.OrderCartItem;
import com.gdou.mall.pojo.ProductSkuInfo;
import com.gdou.mall.service.CartService;
import com.gdou.mall.service.SkuService;
import com.gdou.mall.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Controller
public class CartController {
    @Reference
    SkuService skuService;

    @Reference
    CartService cartService;

//    @RequestMapping("success")
//    public String success(@ModelAttribute("skuInfo") ProductSkuInfo skuInfo, @ModelAttribute("quantity") String quantity, ModelMap modelMap){
//        modelMap.put("skuInfo",skuInfo);
//        modelMap.put("quantity",quantity);
//        return "success";
//    }

    //勾选购物车商品，重新记价
    @RequestMapping("checkCart")
    @LoginRequired(mustLogin = false)
    public String checkCart(Integer isChecked, Long skuId, ModelMap modelMap, HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        String username = (String) request.getAttribute("username");

        //修改购物车商品状态
        OrderCartItem orderCartItem = new OrderCartItem();
        orderCartItem.setIsChecked(isChecked);
        orderCartItem.setProductSkuId(skuId);
        orderCartItem.setUserId(Long.valueOf(userId));

        cartService.modifyCartByIsCheckedAndSkuId(orderCartItem);

        //从NoSQL中查询用户购物车信息
        List<OrderCartItem> orderCartItemList = cartService.getCartByUserIdFromCache(Long.valueOf(userId));

        modelMap.put("cartList", orderCartItemList);

        //计算购物车商品总价
        BigDecimal totalPrices = getTotalPrices(orderCartItemList);
        modelMap.put("totalPrices", totalPrices);

        return "cartListInner";
    }


    //查看购物车信息
    @RequestMapping("cartList")
    @LoginRequired(mustLogin = false)
    public String cartList(HttpServletRequest request, ModelMap modelMap) {
        List<OrderCartItem> orderCartItemList = new ArrayList<>();

        String userId = (String) request.getAttribute("userId");
        String username = (String) request.getAttribute("username");

        //判断用户是否登录
        if (StringUtils.isBlank(userId)) {//用户未登录，从Cookie查询信息
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if (StringUtils.isNotBlank(cartListCookie)) {
                orderCartItemList = JSON.parseArray(cartListCookie, OrderCartItem.class);
            }
        } else {//用户已登录，从NoSQL查询信息
            orderCartItemList = cartService.getCartByUserIdFromCache(Long.valueOf(userId));
        }

        modelMap.put("cartList", orderCartItemList);

        //计算购物车商品总价
        BigDecimal totalPrices = getTotalPrices(orderCartItemList);
        modelMap.put("totalPrices", totalPrices);

        return "cartList";
    }

    @RequestMapping("addToCart")
    @LoginRequired(mustLogin = false)
    public String addToCart(Integer quantity, Long skuId, HttpServletRequest request, HttpServletResponse response) {
        String userId = (String) request.getAttribute("userId");
        String username = (String) request.getAttribute("username");

        List<OrderCartItem> orderCartItemList = new LinkedList<>();

        ProductSkuInfo skuInfo = skuService.getSkuById(skuId);

        //包装sku Cart信息
        OrderCartItem orderCartItem = new OrderCartItem();
        Date date = new Date();

        orderCartItem.setCreateDate(date);
        orderCartItem.setModifyDate(date);
        orderCartItem.setDeleteStatus(0);
        orderCartItem.setProductAttr("");
        orderCartItem.setProductBrand("");
        orderCartItem.setProductCategoryId(skuInfo.getCatalog3Id());
        orderCartItem.setProductId(skuInfo.getProductId());
        orderCartItem.setProductName(skuInfo.getSkuName());
        orderCartItem.setProductSkuCode("未开发此功能");
        orderCartItem.setPrice(skuInfo.getPrice());
        orderCartItem.setProductSkuId(skuId);
        orderCartItem.setProductPic(skuInfo.getSkuDefaultImg());
        orderCartItem.setQuantity(quantity);
        orderCartItem.setIsChecked(1);
        orderCartItem.setTotalPrice(skuInfo.getPrice().multiply(new BigDecimal(quantity)));

        //判断用户是否登录
        if (StringUtils.isBlank(userId)) {//用户未登录，将商品放入 Cookie
            //获取Cookie
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);

            //判断Cookie是否已存在
            if (StringUtils.isBlank(cartListCookie)) {//Cookie不存在，为新购物车
                orderCartItemList.add(orderCartItem);
            } else {//Cookie已存在，判断Cookie中是否含有此sku
                orderCartItemList = JSON.parseArray(cartListCookie, OrderCartItem.class);

                OrderCartItem orderCartItemExist = isSkuExist(orderCartItemList, skuId);
                //判断Cookie中是否含有此sku
                if (orderCartItemExist != null) {//购物车已经有此Sku
                    //移除已存在的Sku，更新购物车SKu
                    orderCartItemList.remove(orderCartItemExist);
                    orderCartItem.setQuantity(orderCartItemExist.getQuantity() + quantity);
                    orderCartItem.setTotalPrice(orderCartItemExist.getTotalPrice().add(orderCartItem.getTotalPrice()));
                }
                //购物车添加Sku
                orderCartItemList.add(orderCartItem);
            }
            //将购物车添加到Cookie中
            CookieUtil.setCookie(request, response, "cartListCookie", JSON.toJSONString(orderCartItemList), 60 * 60 * 24 * 3, true);
        } else {//用户已登录，合并购物车，并将商品放入DB

            //从DB中查出用户的购物车商品
            OrderCartItem orderCartItemFromDB = cartService.getCartByUserId(Long.valueOf(userId), skuId);

            //判断用户之前是否添加过该商品
            if (orderCartItemFromDB == null) {//未添加
                //为商品添加用户信息
                orderCartItem.setUserId(Long.valueOf(userId));
                orderCartItem.setUserNickname(userId + "的名字");
                int result = cartService.addCart(orderCartItem);
            } else {//已添加,修改该商品的数量及总价
                orderCartItemFromDB.setQuantity(orderCartItemFromDB.getQuantity() + quantity);
                orderCartItemFromDB.setTotalPrice(orderCartItemFromDB.getTotalPrice().add(orderCartItem.getTotalPrice()));
                int result = cartService.modifyCartByPK(orderCartItemFromDB);
            }

            //同步缓存
            cartService.flushCartCache(Long.valueOf(userId));
        }

        return "redirect:/success.html";
    }

    //从购物车中查询此商品是否存在
    private OrderCartItem isSkuExist(List<OrderCartItem> orderCartItemList, Long skuId) {
        OrderCartItem orderCartItemExist = null;

        for (OrderCartItem orderCartItem : orderCartItemList) {
            if (orderCartItem.getProductSkuId().equals(skuId)) {
                orderCartItemExist = orderCartItem;
            }
        }
        return orderCartItemExist;
    }

    //计算购物车商品总价
    private BigDecimal getTotalPrices(List<OrderCartItem> orderCartItemList) {
        BigDecimal totalPrices = new BigDecimal(0);
        for (OrderCartItem orderCartItem : orderCartItemList) {
            if (orderCartItem.getIsChecked() == 1)
                totalPrices = totalPrices.add(orderCartItem.getTotalPrice());
        }
        return totalPrices;
    }
}
