package com.gdou.mall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gdou.mall.manage.mapper.ProductBaseAttrInfoMapper;
import com.gdou.mall.manage.mapper.ProductBaseAttrValueMapper;
import com.gdou.mall.manage.mapper.ProductBaseSaleAttrMapper;
import com.gdou.mall.pojo.ProductBaseAttrInfo;
import com.gdou.mall.pojo.ProductBaseAttrValue;
import com.gdou.mall.pojo.ProductBaseSaleAttr;
import com.gdou.mall.service.AttrService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class AttrServiceImpl implements AttrService {

    @Autowired
    ProductBaseAttrInfoMapper attrInfoMapper;

    @Autowired
    ProductBaseAttrValueMapper attrValueMapper;

    @Autowired
    ProductBaseSaleAttrMapper productBaseSaleAttrMapper;


    //根据三级分类查询平台属性
    @Override
    public List<ProductBaseAttrInfo> attrInfoList(Long catalog3Id) {
        ProductBaseAttrInfo productBaseAttrInfo = new ProductBaseAttrInfo();
        productBaseAttrInfo.setCatalog3Id(catalog3Id);
        List<ProductBaseAttrInfo> productBaseAttrInfoList =attrInfoMapper.select(productBaseAttrInfo);
        for (ProductBaseAttrInfo baseAttrInfo: productBaseAttrInfoList) {
            ProductBaseAttrValue productBaseAttrValue=new ProductBaseAttrValue();
            productBaseAttrValue.setAttrId(baseAttrInfo.getId());
            List<ProductBaseAttrValue> productBaseAttrValueList = attrValueMapper.select(productBaseAttrValue);
            baseAttrInfo.setAttrValueList(productBaseAttrValueList);
        }
        return productBaseAttrInfoList;
    }

    //新增和修改类目属性值信息

    /***
     * 分三个步骤：1.新增还是更新属性
     *             2.判断是否删除属性值
     *             3.新增还是更新属性值
     */
    @Override
    public Integer saveAttrInfo(ProductBaseAttrInfo productBaseAttrInfo) {

        List<ProductBaseAttrValue> attrValueList = getAttrValueList(productBaseAttrInfo.getId());
        int count = 0;

        /*判断该属性是否存在
        存在则更新
         */
        if (attrInfoMapper.existsWithPrimaryKey(productBaseAttrInfo)) {
            count = attrInfoMapper.updateByPrimaryKeySelective(productBaseAttrInfo);
            //不存在，新增属性
        } else {
            count = attrInfoMapper.insert(productBaseAttrInfo);
        }

        //删除属性值
        for (ProductBaseAttrValue productBaseAttrValue : attrValueList) {
            if (!productBaseAttrInfo.getAttrValueList().contains(productBaseAttrValue)) {
                attrValueMapper.delete(productBaseAttrValue);
            }
        }

        //根据属性Id新增或更新属性值
        for (ProductBaseAttrValue productBaseAttrValue : productBaseAttrInfo.getAttrValueList()
        ) {
            if (attrValueMapper.existsWithPrimaryKey(productBaseAttrValue)) {
                //存在，更新信息
                count = attrValueMapper.updateByPrimaryKeySelective(productBaseAttrValue);
                //不存在，新增属性
            } else {
                //设置属性值信息的属性id
                productBaseAttrValue.setAttrId(productBaseAttrInfo.getId());
                count = attrValueMapper.insert(productBaseAttrValue);
            }
        }
        return count;
    }


    //根据属性Id查询属性值
    @Override
    public List<ProductBaseAttrValue> getAttrValueList(Long attrId) {
        ProductBaseAttrValue productBaseAttrValue = new ProductBaseAttrValue();
        productBaseAttrValue.setAttrId(attrId);
        List<ProductBaseAttrValue> attrValues = attrValueMapper.select(productBaseAttrValue);
        return attrValues;
    }

    @Override
    public List<ProductBaseSaleAttr> baseSaleAttrList() {
        return productBaseSaleAttrMapper.selectAll();
    }
}
