package com.gdou.mall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.gdou.mall.pojo.ProductSkuInfo;
import com.gdou.mall.pojo.ProductSkuSaleAttrValue;
import com.gdou.mall.pojo.ProductSpuSaleAttr;
import com.gdou.mall.service.SkuService;
import com.gdou.mall.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ItemController {
    @Reference
    SkuService skuService;

    @Reference
    SpuService spuService;

    @RequestMapping("{skuId}.html")
    public String item(@PathVariable Long skuId, ModelMap map, HttpServletRequest request) {
        String username = String.valueOf(request.getAttribute("username"));
        ProductSkuInfo productSkuInfo = skuService.getSkuById(skuId);
        map.put("skuInfo", productSkuInfo);
        Long productId = productSkuInfo.getProductId();

        //获取Sku
        List<ProductSpuSaleAttr> productSpuSaleAttrList = spuService.spuSaleAttrListCheckBySku(productId, skuId);
        map.put("spuSaleAttrListCheckBySku", productSpuSaleAttrList);

        //获取Sku 的同系列Spu
        List<ProductSkuInfo> productSkuInfoList = skuService.getSkuSaleAttrValueListBySpu(productId);

        Map<String, Long> productSkuInfoListMap = new HashMap<>();
        for (ProductSkuInfo skuInfo : productSkuInfoList) {
            Long value = skuInfo.getId();
            String key = "";
            for (ProductSkuSaleAttrValue productSkuSaleAttrValue : skuInfo.getSkuSaleAttrValueList()) {
                key += productSkuSaleAttrValue.getId() + "|";
            }
            productSkuInfoListMap.put(key,value);
        }

        String skuSaleAttrHashJsonStr  = JSON.toJSONString(productSkuInfoListMap);
        map.put("skuSaleAttrHashJsonStr",skuSaleAttrHashJsonStr);
        return "item";
    }
}
