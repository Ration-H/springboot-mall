package mapper;

import java.util.List;
import pojo.ProductSpuSaleAttr;

public interface ProductSpuSaleAttrMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ProductSpuSaleAttr record);

    ProductSpuSaleAttr selectByPrimaryKey(Long id);

    List<ProductSpuSaleAttr> selectAll();

    int updateByPrimaryKey(ProductSpuSaleAttr record);
}