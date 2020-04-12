package mapper;

import java.util.List;
import pojo.ProductSpuSaleAttrValue;

public interface ProductSpuSaleAttrValueMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ProductSpuSaleAttrValue record);

    ProductSpuSaleAttrValue selectByPrimaryKey(Long id);

    List<ProductSpuSaleAttrValue> selectAll();

    int updateByPrimaryKey(ProductSpuSaleAttrValue record);
}