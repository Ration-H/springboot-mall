package com.gdou.mall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.gdou.mall.manage.mapper.ProductSkuAttrValueMapper;
import com.gdou.mall.manage.mapper.ProductSkuImageMapper;
import com.gdou.mall.manage.mapper.ProductSkuInfoMapper;
import com.gdou.mall.manage.mapper.ProductSkuSaleAttrValueMapper;
import com.gdou.mall.pojo.ProductSkuAttrValue;
import com.gdou.mall.pojo.ProductSkuImage;
import com.gdou.mall.pojo.ProductSkuInfo;
import com.gdou.mall.pojo.ProductSkuSaleAttrValue;
import com.gdou.mall.service.SkuService;
import com.gdou.mall.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.List;

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

    @Autowired
    RedisUtil redisUtil;

    //获取所有Sku
    @Override
    public List<ProductSkuInfo> getAllSku() {
        //获取所有Sku
        List<ProductSkuInfo> productSkuInfoList = productSkuInfoMapper.selectAll();

        //获取Sku的平台属性值
        for (ProductSkuInfo productSkuInfo:productSkuInfoList){
            Long productSkuInfoId = productSkuInfo.getId();

            ProductSkuAttrValue productSkuAttrValue = new ProductSkuAttrValue();
            productSkuAttrValue.setSkuId(productSkuInfoId);
            List<ProductSkuAttrValue> productSkuAttrValueList = productSkuAttrValueMapper.select(productSkuAttrValue);

            productSkuInfo.setSkuAttrValueList(productSkuAttrValueList);
        }

        return productSkuInfoList;
    }

    //获取当前Sku 同系列的Sku
    @Override
    public List<ProductSkuInfo> getSkuSaleAttrValueListBySpu(Long productId) {
        List<ProductSkuInfo> productSkuInfoList = productSkuInfoMapper.selectSkuSaleAttrValueListBySpu(productId);
        return productSkuInfoList;
    }

    //根据Sku id查询Sku
    public ProductSkuInfo getSkuByIdFromDB(Long skuId) {
        //查询Sku
        ProductSkuInfo productSkuInfo = productSkuInfoMapper.selectByPrimaryKey(skuId);

        //查询Sku图片
        ProductSkuImage productSkuImage = new ProductSkuImage();
        productSkuImage.setSkuId(skuId);
        List<ProductSkuImage> productSkuImages = productSkuImageMapper.select(productSkuImage);
        productSkuInfo.setSkuImageList(productSkuImages);

        return productSkuInfo;
    }

    @Override
    public ProductSkuInfo getSkuById(Long skuId) {
        ProductSkuInfo productSkuInfo =new ProductSkuInfo();
        //获取Jedis对象
        Jedis jedis = redisUtil.getJedis();
        //从Redis获取Sku
        String skuInfoJsonStr = jedis.get("sku:" + skuId + ":info");

        if (StringUtils.isNotBlank(skuInfoJsonStr)) {
            productSkuInfo = JSON.parseObject(skuInfoJsonStr, ProductSkuInfo.class);
        } else {
            productSkuInfo = getSkuByIdFromDB(skuId);
        }

        jedis.close();
        return productSkuInfo;
    }

    //添加Sku
    @Override
    public Integer saveSkuInfo(ProductSkuInfo productSkuInfo) {
        Integer result = productSkuInfoMapper.insertSelective(productSkuInfo);

        Long productSkuInfoId = productSkuInfo.getId();

        //添加Sku图片
        for (ProductSkuImage productSkuImage : productSkuInfo.getSkuImageList()) {
            productSkuImage.setSkuId(productSkuInfoId);
            result += productSkuImageMapper.insertSelective(productSkuImage);
        }

        //添加Sku平台属性
        for (ProductSkuAttrValue productSkuAttrValue : productSkuInfo.getSkuAttrValueList()) {
            productSkuAttrValue.setSkuId(productSkuInfoId);
            result += productSkuAttrValueMapper.insertSelective(productSkuAttrValue);
        }

        //添加Sku销售属性
        for (ProductSkuSaleAttrValue productSkuSaleAttrValue : productSkuInfo.getSkuSaleAttrValueList()) {
            productSkuSaleAttrValue.setSkuId(productSkuInfoId);
            result += productSkuSaleAttrValueMapper.insertSelective(productSkuSaleAttrValue);
        }

        return result;
    }

}
