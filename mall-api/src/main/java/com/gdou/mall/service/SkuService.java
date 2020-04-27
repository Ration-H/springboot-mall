package com.gdou.mall.service;

import com.gdou.mall.pojo.ProductSkuInfo;

import java.math.BigDecimal;
import java.util.List;

public interface SkuService {
    Integer saveSkuInfo(ProductSkuInfo productSkuInfo);

    ProductSkuInfo getSkuById(Long skuId);

    List<ProductSkuInfo> getSkuSaleAttrValueListBySpu(Long productId);

    List<ProductSkuInfo> getAllSku();

    boolean checkPrice(Long productSkuId, BigDecimal price);
}
