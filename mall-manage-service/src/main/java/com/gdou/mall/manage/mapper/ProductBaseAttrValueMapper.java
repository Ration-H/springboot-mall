package com.gdou.mall.manage.mapper;

import com.gdou.mall.pojo.ProductBaseAttrValue;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface ProductBaseAttrValueMapper extends Mapper<ProductBaseAttrValue> {
    void deleteByAttrIdBash(@Param("ids") String ids);
}
