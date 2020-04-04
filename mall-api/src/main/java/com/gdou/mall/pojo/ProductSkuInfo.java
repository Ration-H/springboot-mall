package com.gdou.mall.pojo;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

public class ProductSkuInfo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JSONField(name = "spuId")
    private Long productId;

    private Double price;

    private String skuName;

    private String skuDesc;

    private Double weight;

    private Long tmId;

    private Long catalog3Id;

    private String skuDefaultImg;

    @Transient
    @JSONField(name="skuImageList")
    List<ProductSkuImage> productSkuImageList;

    @Transient
    @JSONField(name="skuAttrValueList")
    List<ProductSkuAttrValue> productSkuAttrValueList;

    @Transient
    @JSONField(name="skuSaleAttrValueList")
    List<ProductSkuSaleAttrValue> productSkuSaleAttrValueList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getSkuDesc() {
        return skuDesc;
    }

    public void setSkuDesc(String skuDesc) {
        this.skuDesc = skuDesc;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Long getTmId() {
        return tmId;
    }

    public void setTmId(Long tmId) {
        this.tmId = tmId;
    }

    public Long getCatalog3Id() {
        return catalog3Id;
    }

    public void setCatalog3Id(Long catalog3Id) {
        this.catalog3Id = catalog3Id;
    }

    public String getSkuDefaultImg() {
        return skuDefaultImg;
    }

    public void setSkuDefaultImg(String skuDefaultImg) {
        this.skuDefaultImg = skuDefaultImg;
    }

    public List<ProductSkuImage> getProductSkuImageList() {
        return productSkuImageList;
    }

    public void setProductSkuImageList(List<ProductSkuImage> productSkuImageList) {
        this.productSkuImageList = productSkuImageList;
    }

    public List<ProductSkuAttrValue> getProductSkuAttrValueList() {
        return productSkuAttrValueList;
    }

    public void setProductSkuAttrValueList(List<ProductSkuAttrValue> productSkuAttrValueList) {
        this.productSkuAttrValueList = productSkuAttrValueList;
    }

    public List<ProductSkuSaleAttrValue> getProductSkuSaleAttrValueList() {
        return productSkuSaleAttrValueList;
    }

    public void setProductSkuSaleAttrValueList(List<ProductSkuSaleAttrValue> productSkuSaleAttrValueList) {
        this.productSkuSaleAttrValueList = productSkuSaleAttrValueList;
    }
}