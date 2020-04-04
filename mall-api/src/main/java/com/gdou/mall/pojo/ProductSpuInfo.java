package com.gdou.mall.pojo;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

public class ProductSpuInfo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;

    private String description;

    private Long catalog3Id;

    private Long tmId;

    @Transient
    @JSONField(name = "spuSaleAttrList")
    private List<ProductSpuSaleAttr> productSpuSaleAttrList;

    @Transient
    @JSONField(name = "spuImageList")
    private List<ProductSpuImage> productSpuImageList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCatalog3Id() {
        return catalog3Id;
    }

    public void setCatalog3Id(Long catalog3Id) {
        this.catalog3Id = catalog3Id;
    }

    public Long getTmId() {
        return tmId;
    }

    public void setTmId(Long tmId) {
        this.tmId = tmId;
    }

    public List<ProductSpuSaleAttr> getProductSpuSaleAttrList() {
        return productSpuSaleAttrList;
    }

    public void setProductSpuSaleAttrList(List<ProductSpuSaleAttr> productSpuSaleAttrList) {
        this.productSpuSaleAttrList = productSpuSaleAttrList;
    }

    public List<ProductSpuImage> getProductSpuImageList() {
        return productSpuImageList;
    }

    public void setProductSpuImageList(List<ProductSpuImage> productSpuImageList) {
        this.productSpuImageList = productSpuImageList;
    }
}