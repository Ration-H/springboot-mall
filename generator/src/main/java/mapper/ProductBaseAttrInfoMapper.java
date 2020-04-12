package mapper;

import java.util.List;
import pojo.ProductBaseAttrInfo;

public interface ProductBaseAttrInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ProductBaseAttrInfo record);

    ProductBaseAttrInfo selectByPrimaryKey(Long id);

    List<ProductBaseAttrInfo> selectAll();

    int updateByPrimaryKey(ProductBaseAttrInfo record);
}