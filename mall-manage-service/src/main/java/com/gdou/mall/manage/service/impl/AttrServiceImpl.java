package com.gdou.mall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gdou.mall.manage.mapper.AttrInfoMapper;
import com.gdou.mall.manage.mapper.AttrValueMapper;
import com.gdou.mall.pojo.ProductBaseAttrInfo;
import com.gdou.mall.pojo.ProductBaseAttrValue;
import com.gdou.mall.service.AttrService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class AttrServiceImpl implements AttrService {

    @Autowired
    AttrInfoMapper attrInfoMapper;

    @Autowired
    AttrValueMapper attrValueMapper;

    @Override
    public List<ProductBaseAttrInfo> attrInfoList(Long catalog3Id) {
        ProductBaseAttrInfo productBaseAttrInfo = new ProductBaseAttrInfo();
        productBaseAttrInfo.setCatalog3Id(catalog3Id);
        return attrInfoMapper.select(productBaseAttrInfo);
    }

    //新增和修改类目属性值信息
    @Override
    public Integer saveAttrInfo(ProductBaseAttrInfo productBaseAttrInfo) {

        List<ProductBaseAttrValue> attrValueList = getAttrValueList(productBaseAttrInfo.getId());

        int count=0;

        /*判断该属性是否存在
        存在则更新
         */
        if(attrInfoMapper.existsWithPrimaryKey(productBaseAttrInfo)){
            count = attrInfoMapper.updateByPrimaryKeySelective(productBaseAttrInfo);
            //不存在，新增属性
        }else {
            count = attrInfoMapper.insert(productBaseAttrInfo);
        }

        //删除属性值
        for (ProductBaseAttrValue productBaseAttrValue : attrValueList) {
            if(!productBaseAttrInfo.getAttrValueList().contains(productBaseAttrValue)){
                attrValueMapper.delete(productBaseAttrValue);
            }
        }

        //根据属性Id新增或更新属性值
        for (ProductBaseAttrValue productBaseAttrValue : productBaseAttrInfo.getAttrValueList()
        ) {
            if(attrValueMapper.existsWithPrimaryKey(productBaseAttrValue)){
                //存在，更新信息
                count = attrValueMapper.updateByPrimaryKeySelective(productBaseAttrValue);
                //不存在，新增属性
            }else {
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
}
