package com.gdou.mall.service;

import com.gdou.mall.pojo.ProductSkuInfo;

import java.util.List;

public interface SkuService {
    Integer saveSkuInfo(ProductSkuInfo productSkuInfo);

    ProductSkuInfo getSkuById(Long skuId);

    List<ProductSkuInfo> getSkuSaleAttrValueListBySpu(Long productId);

    List<ProductSkuInfo> getAllSku();
}
