package com.gdou.mall.pojo;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

public class ProductBaseCatalog1 implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Transient
    private List<ProductBaseCatalog2> catalog2s;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProductBaseCatalog2> getCatalog2s() {
        return catalog2s;
    }

    public void setCatalog2s(List<ProductBaseCatalog2> catalog2s) {
        this.catalog2s = catalog2s;
    }
}