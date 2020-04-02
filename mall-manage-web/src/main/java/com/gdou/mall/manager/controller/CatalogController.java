package com.gdou.mall.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gdou.mall.pojo.ProductBaseCatalog1;
import com.gdou.mall.pojo.ProductBaseCatalog2;
import com.gdou.mall.pojo.ProductBaseCatalog3;
import com.gdou.mall.service.CatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@CrossOrigin
public class CatalogController {

    @Reference
    CatalogService catalogService;

    //根据二级Id查询三级分类
    @RequestMapping("getCatalog3")
    @ResponseBody
    public List<ProductBaseCatalog3> getCatalog3(Long catalog2Id){
        List<ProductBaseCatalog3> catalog3s= catalogService.getCatalog3(catalog2Id);
        return catalog3s;
    }

    //根据一级Id查询二级分类
    @RequestMapping("getCatalog2")
    @ResponseBody
    public List<ProductBaseCatalog2> getCatalog2(Long catalog1Id){
        List<ProductBaseCatalog2> catalog2s= catalogService.getCatalog2(catalog1Id);
        return catalog2s;
    }

    //查询所有一级分类
    @RequestMapping("getCatalog1")
    @ResponseBody
    public List<ProductBaseCatalog1> getCatalog1(){
        List<ProductBaseCatalog1> catalog1s= catalogService.getCatalog1();
        return catalog1s;
    }
}
