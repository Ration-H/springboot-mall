package mapper;

import java.util.List;
import pojo.ProductBaseSaleAttr;

public interface ProductBaseSaleAttrMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ProductBaseSaleAttr record);

    ProductBaseSaleAttr selectByPrimaryKey(Long id);

    List<ProductBaseSaleAttr> selectAll();

    int updateByPrimaryKey(ProductBaseSaleAttr record);
}