package com.gdou.mall.pojo;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;

public class ProductBaseAttrValue implements Serializable {

    @Id
    private Long id;

    private String valueName;

    private Long attrId;

    private String isEnabled;

    @Transient
    private String urlParam;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    public Long getAttrId() {
        return attrId;
    }

    public void setAttrId(Long attrId) {
        this.attrId = attrId;
    }

    public String getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(String isEnabled) {
        this.isEnabled = isEnabled;
    }

    public String getUrlParam() {
        return urlParam;
    }

    public void setUrlParam(String urlParam) {
        this.urlParam = urlParam;
    }
}