package com.gdou.mall.service;

import com.gdou.mall.pojo.ProductSpuImage;
import com.gdou.mall.pojo.ProductSpuInfo;
import com.gdou.mall.pojo.ProductSpuSaleAttr;

import java.util.List;

public interface SpuService {
    List<ProductSpuInfo> spuList(Long catalog3Id);

    Integer saveSpuInfo(ProductSpuInfo productSpuInfo);

    List<ProductSpuImage> spuImageList(Long spuId);

    List<ProductSpuSaleAttr> spuSaleAttrList(Long spuId);

    List<ProductSpuSaleAttr> spuSaleAttrListCheckBySku(Long productId,Long skuId);
}
