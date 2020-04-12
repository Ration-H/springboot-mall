package com.gdou.mall.manage.mapper;

import com.gdou.mall.pojo.ProductSpuSaleAttr;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ProductSpuSaleAttrMapper extends Mapper<ProductSpuSaleAttr> {
    List<ProductSpuSaleAttr> selectSpuSaleAttrListCheckBySku(@Param("productId") Long productId, @Param("skuId") Long skuId);
}
