package com.gdou.mall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gdou.mall.manage.mapper.ProductSpuImageMapper;
import com.gdou.mall.manage.mapper.ProductSpuInfoMapper;
import com.gdou.mall.manage.mapper.ProductSpuSaleAttrMapper;
import com.gdou.mall.manage.mapper.ProductSpuSaleAttrValueMapper;
import com.gdou.mall.pojo.ProductSpuImage;
import com.gdou.mall.pojo.ProductSpuInfo;
import com.gdou.mall.pojo.ProductSpuSaleAttr;
import com.gdou.mall.pojo.ProductSpuSaleAttrValue;
import com.gdou.mall.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class SpuServiceImpl implements SpuService {
    @Autowired
    ProductSpuInfoMapper productSpuInfoMapper;

    @Autowired
    ProductSpuSaleAttrMapper productSpuSaleAttrMapper;

    @Autowired
    ProductSpuSaleAttrValueMapper productSpuSaleAttrValueMapper;

    @Autowired
    ProductSpuImageMapper productSpuImageMapper;


    //根据Spu id 查询销售属性
    @Override
    public List<ProductSpuSaleAttr> spuSaleAttrList(Long spuId) {
        ProductSpuSaleAttr productSpuSaleAttr = new ProductSpuSaleAttr();
        productSpuSaleAttr.setProductId(spuId);
        List<ProductSpuSaleAttr> productSpuSaleAttrList = productSpuSaleAttrMapper.select(productSpuSaleAttr);
        for (ProductSpuSaleAttr spuSaleAttr : productSpuSaleAttrList) {
            ProductSpuSaleAttrValue productSpuSaleAttrValue = new ProductSpuSaleAttrValue();
            productSpuSaleAttrValue.setProductId(spuId);
            productSpuSaleAttrValue.setSaleAttrId(spuSaleAttr.getSaleAttrId());
            List<ProductSpuSaleAttrValue> productSpuSaleAttrValueList = productSpuSaleAttrValueMapper.select(productSpuSaleAttrValue);
            spuSaleAttr.setProductSpuSaleAttrValueList(productSpuSaleAttrValueList);
        }
        return productSpuSaleAttrList;
    }

    //根据Spu id查询图片
    @Override
    public List<ProductSpuImage> spuImageList(Long spuId) {
        ProductSpuImage productSpuImage = new ProductSpuImage();
        productSpuImage.setProductId(spuId);
        List<ProductSpuImage> spuImageList = productSpuImageMapper.select(productSpuImage);
        return spuImageList;
    }

    //根据三级分类获取商品Spu
    @Override
    public List<ProductSpuInfo> spuList(Long catalog3Id) {
        ProductSpuInfo productSpuInfo = new ProductSpuInfo();
        productSpuInfo.setCatalog3Id(catalog3Id);
        return productSpuInfoMapper.select(productSpuInfo);
    }

    //保存Spu
    @Override
    public Integer saveSpuInfo(ProductSpuInfo productSpuInfo) {
        int result = 0;

        result += productSpuInfoMapper.insertSelective(productSpuInfo);

        Long productSpuInfoId = productSpuInfo.getId();

        //保存图片
        for (ProductSpuImage productSpuImage : productSpuInfo.getProductSpuImageList()) {
            productSpuImage.setProductId(productSpuInfoId);
            result += productSpuImageMapper.insertSelective(productSpuImage);
        }

        //保存Spu 销售属性与销售属性值
        for (ProductSpuSaleAttr productSpuSaleAttr : productSpuInfo.getProductSpuSaleAttrList()) {
            productSpuSaleAttr.setProductId(productSpuInfoId);
            result += productSpuSaleAttrMapper.insertSelective(productSpuSaleAttr);
            for (ProductSpuSaleAttrValue productSpuSaleAttrValue : productSpuSaleAttr.getProductSpuSaleAttrValueList()) {
                productSpuSaleAttrValue.setProductId(productSpuInfoId);
                // productSpuSaleAttrValue.setSaleAttrId(productSpuSaleAttr.getId());
                result += productSpuSaleAttrValueMapper.insertSelective(productSpuSaleAttrValue);
            }
        }
        return result;
    }


}
