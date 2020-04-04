package com.gdou.mall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gdou.mall.manage.mapper.ProductSkuAttrValueMapper;
import com.gdou.mall.manage.mapper.ProductSkuImageMapper;
import com.gdou.mall.manage.mapper.ProductSkuInfoMapper;
import com.gdou.mall.manage.mapper.ProductSkuSaleAttrValueMapper;
import com.gdou.mall.pojo.ProductSkuAttrValue;
import com.gdou.mall.pojo.ProductSkuImage;
import com.gdou.mall.pojo.ProductSkuInfo;
import com.gdou.mall.pojo.ProductSkuSaleAttrValue;
import com.gdou.mall.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    ProductSkuInfoMapper productSkuInfoMapper;

    @Autowired
    ProductSkuImageMapper productSkuImageMapper;

    @Autowired
    ProductSkuAttrValueMapper productSkuAttrValueMapper;

    @Autowired
    ProductSkuSaleAttrValueMapper productSkuSaleAttrValueMapper;


    //添加Sku
    @Override
    public Integer saveSkuInfo(ProductSkuInfo productSkuInfo) {
        Integer result = productSkuInfoMapper.insertSelective(productSkuInfo);

        Long productSkuInfoId = productSkuInfo.getId();

        //添加Sku图片
        for (ProductSkuImage productSkuImage : productSkuInfo.getProductSkuImageList()) {
            productSkuImage.setSkuId(productSkuInfoId);
            result += productSkuImageMapper.insertSelective(productSkuImage);
        }

        //添加Sku平台属性
        for (ProductSkuAttrValue productSkuAttrValue : productSkuInfo.getProductSkuAttrValueList()) {
            productSkuAttrValue.setSkuId(productSkuInfoId);
            result += productSkuAttrValueMapper.insertSelective(productSkuAttrValue);
        }

        //添加Sku销售属性
        for (ProductSkuSaleAttrValue productSkuSaleAttrValue : productSkuInfo.getProductSkuSaleAttrValueList()) {
            productSkuSaleAttrValue.setSkuId(productSkuInfoId);
            result += productSkuSaleAttrValueMapper.insertSelective(productSkuSaleAttrValue);
        }

        return result;
    }
}
