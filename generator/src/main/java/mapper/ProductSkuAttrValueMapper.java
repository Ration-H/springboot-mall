package mapper;

import java.util.List;
import pojo.ProductSkuAttrValue;

public interface ProductSkuAttrValueMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ProductSkuAttrValue record);

    ProductSkuAttrValue selectByPrimaryKey(Long id);

    List<ProductSkuAttrValue> selectAll();

    int updateByPrimaryKey(ProductSkuAttrValue record);
}