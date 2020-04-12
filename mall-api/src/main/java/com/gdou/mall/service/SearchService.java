package com.gdou.mall.service;

import com.gdou.mall.pojo.ProductSearchParam;
import com.gdou.mall.pojo.ProductSkuInfoSearch;

import java.util.List;

public interface SearchService {
    List<ProductSkuInfoSearch> list(ProductSearchParam productSearchParam);
}
