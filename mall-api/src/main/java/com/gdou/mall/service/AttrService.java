package com.gdou.mall.service;

import com.gdou.mall.pojo.ProductBaseAttrInfo;
import com.gdou.mall.pojo.ProductBaseAttrValue;

import java.util.List;

public interface AttrService {
    List<ProductBaseAttrInfo> attrInfoList(Long catalog3Id);

    Integer saveAttrInfo(ProductBaseAttrInfo productBaseAttrInfo);

    List<ProductBaseAttrValue> getAttrValueList(Long attrId);
}
