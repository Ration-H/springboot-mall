package com.gdou.mall.manage.mapper;

import com.gdou.mall.pojo.ProductBaseAttrInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ProductBaseAttrInfoMapper extends Mapper<ProductBaseAttrInfo> {
    List<ProductBaseAttrInfo> selectAttrValueListByValueId(@Param("valueIds") String valueIds);
}
