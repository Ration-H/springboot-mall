package com.gdou.mall.service;

import com.gdou.mall.pojo.ProductBaseAttrInfo;
import com.gdou.mall.pojo.ProductBaseAttrValue;
import com.gdou.mall.pojo.ProductBaseSaleAttr;

import java.util.List;
import java.util.Set;

public interface AttrService {
    List<ProductBaseAttrInfo> attrInfoList(Long catalog3Id);

    Integer saveAttrInfo(ProductBaseAttrInfo productBaseAttrInfo);

    List<ProductBaseAttrValue> getAttrValueList(Long attrId);

    List<ProductBaseSaleAttr> baseSaleAttrList();

    List<ProductBaseAttrInfo> getAttrValueListByValueId(Set<Long> valueIdSet);

    void delAttrInfo(Long attrId);
}
