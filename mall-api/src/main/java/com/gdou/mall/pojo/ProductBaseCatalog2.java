package com.gdou.mall.pojo;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

public class ProductBaseCatalog2 implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long catalog1Id;

    @Transient
    private List<ProductBaseCatalog3> catalog3List;


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

    public Long getCatalog1Id() {
        return catalog1Id;
    }

    public void setCatalog1Id(Long catalog1Id) {
        this.catalog1Id = catalog1Id;
    }

    public List<ProductBaseCatalog3> getCatalog3List() {
        return catalog3List;
    }

    public void setCatalog3List(List<ProductBaseCatalog3> catalog3List) {
        this.catalog3List = catalog3List;
    }
}