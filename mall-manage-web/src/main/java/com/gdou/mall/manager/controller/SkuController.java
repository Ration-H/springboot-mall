package com.gdou.mall.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gdou.mall.pojo.ProductSkuInfo;
import com.gdou.mall.service.SkuService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@CrossOrigin
public class SkuController {
    @Reference
    SkuService skuService;

    //添加Sku
    @RequestMapping("saveSkuInfo")
    @ResponseBody
    public String saveSkuInfo(@RequestBody ProductSkuInfo productSkuInfo) {

        //处理默认图片
        if(StringUtils.isBlank(productSkuInfo.getSkuDefaultImg())){
            productSkuInfo.setSkuDefaultImg(productSkuInfo.getProductSkuImageList().get(0).getImgUrl());
            productSkuInfo.getProductSkuImageList().get(0).setIsDefault("1");
        }

        Integer result = skuService.saveSkuInfo(productSkuInfo);
        return result>0?"success":"failed";
    }

}
