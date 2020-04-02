package com.gdou.mall.pojo;

import javax.persistence.Id;
import java.io.Serializable;

public class ProductBaseSaleAttr implements Serializable {

    @Id
    private Long id;

    private String name;

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
}