package mapper;

import java.util.List;
import pojo.ProductBaseAttrValue;

public interface ProductBaseAttrValueMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ProductBaseAttrValue record);

    ProductBaseAttrValue selectByPrimaryKey(Long id);

    List<ProductBaseAttrValue> selectAll();

    int updateByPrimaryKey(ProductBaseAttrValue record);
}