package com.gdou.mall.service;

import com.gdou.mall.pojo.ProductBaseCatalog1;
import com.gdou.mall.pojo.ProductBaseCatalog2;
import com.gdou.mall.pojo.ProductBaseCatalog3;

import java.util.List;

public interface CatalogService {

    List<ProductBaseCatalog1> getCatalog1();

    List<ProductBaseCatalog2> getCatalog2(Long catalog1Id);

    List<ProductBaseCatalog3> getCatalog3(Long catalog2Id);
}
