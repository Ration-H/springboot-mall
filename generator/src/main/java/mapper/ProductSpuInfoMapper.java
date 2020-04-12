package mapper;

import java.util.List;
import pojo.ProductSpuInfo;

public interface ProductSpuInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ProductSpuInfo record);

    ProductSpuInfo selectByPrimaryKey(Long id);

    List<ProductSpuInfo> selectAll();

    int updateByPrimaryKey(ProductSpuInfo record);
}