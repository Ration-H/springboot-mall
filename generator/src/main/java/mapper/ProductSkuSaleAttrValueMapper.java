package mapper;

import java.util.List;
import pojo.ProductSkuSaleAttrValue;

public interface ProductSkuSaleAttrValueMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ProductSkuSaleAttrValue record);

    ProductSkuSaleAttrValue selectByPrimaryKey(Long id);

    List<ProductSkuSaleAttrValue> selectAll();

    int updateByPrimaryKey(ProductSkuSaleAttrValue record);
}