package com.gdou.mall.manage.mapper;

import com.gdou.mall.pojo.ProductSkuInfo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ProductSkuInfoMapper extends Mapper<ProductSkuInfo> {
    List<ProductSkuInfo> selectSkuSaleAttrValueListBySpu(Long productId);
}
