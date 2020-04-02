package com.gdou.mall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gdou.mall.manage.mapper.Catalog1Mapper;
import com.gdou.mall.manage.mapper.Catalog2Mapper;
import com.gdou.mall.manage.mapper.Catalog3Mapper;
import com.gdou.mall.pojo.ProductBaseCatalog1;
import com.gdou.mall.pojo.ProductBaseCatalog2;
import com.gdou.mall.pojo.ProductBaseCatalog3;
import com.gdou.mall.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class CatalogServiceImpl implements CatalogService {

    @Autowired
    Catalog1Mapper catalog1Mapper;

    @Autowired
    Catalog2Mapper catalog2Mapper;

    @Autowired
    Catalog3Mapper catalog3Mapper;

    @Override
    public List<ProductBaseCatalog1> getCatalog1() {
        return catalog1Mapper.selectAll();
    }

    @Override
    public List<ProductBaseCatalog2> getCatalog2(Long catalog1Id) {
        ProductBaseCatalog2 productBaseCatalog2 = new ProductBaseCatalog2();
        productBaseCatalog2.setCatalog1Id(catalog1Id);
        return catalog2Mapper.select(productBaseCatalog2);
    }

    @Override
    public List<ProductBaseCatalog3> getCatalog3(Long catalog2Id) {
        ProductBaseCatalog3 productBaseCatalog3 = new ProductBaseCatalog3();
        productBaseCatalog3.setCatalog2Id(catalog2Id);
        return catalog3Mapper.select(productBaseCatalog3);
    }
}
