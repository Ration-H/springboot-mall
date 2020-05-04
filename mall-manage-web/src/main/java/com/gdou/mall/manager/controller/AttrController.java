package com.gdou.mall.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gdou.mall.pojo.ProductBaseAttrInfo;
import com.gdou.mall.pojo.ProductBaseAttrValue;
import com.gdou.mall.pojo.ProductBaseSaleAttr;
import com.gdou.mall.service.AttrService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@CrossOrigin
public class AttrController {

    @Reference
    AttrService attrService;

    //查询所有销售属性
    @RequestMapping("baseSaleAttrList")
    @ResponseBody
    public List<ProductBaseSaleAttr> baseSaleAttrList() {
        List<ProductBaseSaleAttr> productBaseSaleAttrList = attrService.baseSaleAttrList();
        return productBaseSaleAttrList;
    }

    //添加、更新类目的属性信息
    @RequestMapping("saveAttrInfo")
    @ResponseBody
    public String saveAttrInfo(@RequestBody ProductBaseAttrInfo productBaseAttrInfo) {
        Integer result = attrService.saveAttrInfo(productBaseAttrInfo);
        return result > 0 ? "success" : "fail";
    }

    //查询类目属性值信息
    @RequestMapping("getAttrValueList")
    @ResponseBody
    public List<ProductBaseAttrValue> getAttrValueList(Long attrId) {
        List<ProductBaseAttrValue> attrValues = attrService.getAttrValueList(attrId);
        return attrValues;
    }

    //根据三级分类查询该类目属性信息
    @RequestMapping("attrInfoList")
    @ResponseBody
    public List<ProductBaseAttrInfo> attrInfoList(Long catalog3Id) {
        List<ProductBaseAttrInfo> attrInfos = attrService.attrInfoList(catalog3Id);
        return attrInfos;
    }

    //删除属性信息
    @RequestMapping("deleteAttrInfo")
    @ResponseBody
    public void deleteAttrInfo(Long attrId) {
        System.out.println("删除属性");
        attrService.delAttrInfo(attrId);
    }

    //批量删除属性信息
    @RequestMapping("multiDeleteAttrInfo")
    @ResponseBody
    public void multiDeleteAttrInfo(String attrIds) {
        System.out.println("删除属性");
        attrService.delAttrInfoBash(attrIds);
    }
}
