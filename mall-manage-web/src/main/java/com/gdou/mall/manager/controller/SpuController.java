package com.gdou.mall.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gdou.mall.pojo.ProductSpuImage;
import com.gdou.mall.pojo.ProductSpuInfo;
import com.gdou.mall.pojo.ProductSpuSaleAttr;
import com.gdou.mall.service.SpuService;
import com.gdou.mall.utils.FileUploadUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@CrossOrigin
public class SpuController {
    @Reference
    SpuService spuService;

    //根据Spu id查询图片
    @RequestMapping("spuImageList")
    @ResponseBody
    public List<ProductSpuImage> spuImageList(Long spuId) {
        List<ProductSpuImage> productSpuImageList = spuService.spuImageList(spuId);
        return productSpuImageList;
    }

    //根据Spu id查询销售属性
    @RequestMapping("spuSaleAttrList")
    @ResponseBody
    public List<ProductSpuSaleAttr> spuSaleAttrList(Long spuId) {
        List<ProductSpuSaleAttr> productSpuSaleAttrList = spuService.spuSaleAttrList(spuId);
        return productSpuSaleAttrList;
    }

    //上传图片
    @RequestMapping("fileUpload")
    @ResponseBody
    public String fileUpload(@RequestParam("file") MultipartFile multipartFile) {
        String imgUrl = FileUploadUtils.uploadImg(multipartFile);
        return imgUrl;
    }

    //添加商品Spu
    @RequestMapping("saveSpuInfo")
    @ResponseBody
    public String saveSpuInfo(@RequestBody ProductSpuInfo productSpuInfo) {
        Integer result = spuService.saveSpuInfo(productSpuInfo);
        return result > 0 ? "success" : "failed";
    }

    //根据三级分类获取商品Spu
    @RequestMapping("spuList")
    @ResponseBody
    public List<ProductSpuInfo> spuList(Long catalog3Id) {
        List<ProductSpuInfo> productSpuInfoList = spuService.spuList(catalog3Id);
        return productSpuInfoList;
    }

}
