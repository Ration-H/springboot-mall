package com.gdou.mall.pojo;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

public class ProductSpuSaleAttr implements Serializable {
    @Id
    private Long id;

    private Long productId;

    private Long saleAttrId;

    private String saleAttrName;

    @Transient
    @JSONField(name="spuSaleAttrValueList")
    List<ProductSpuSaleAttrValue> productSpuSaleAttrValueList;

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

    public Long getSaleAttrId() {
        return saleAttrId;
    }

    public void setSaleAttrId(Long saleAttrId) {
        this.saleAttrId = saleAttrId;
    }

    public String getSaleAttrName() {
        return saleAttrName;
    }

    public void setSaleAttrName(String saleAttrName) {
        this.saleAttrName = saleAttrName;
    }

    public List<ProductSpuSaleAttrValue> getProductSpuSaleAttrValueList() {
        return productSpuSaleAttrValueList;
    }

    public void setProductSpuSaleAttrValueList(List<ProductSpuSaleAttrValue> productSpuSaleAttrValueList) {
        this.productSpuSaleAttrValueList = productSpuSaleAttrValueList;
    }
}