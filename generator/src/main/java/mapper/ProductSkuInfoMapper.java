package mapper;

import java.util.List;
import pojo.ProductSkuInfo;

public interface ProductSkuInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ProductSkuInfo record);

    ProductSkuInfo selectByPrimaryKey(Long id);

    List<ProductSkuInfo> selectAll();

    int updateByPrimaryKey(ProductSkuInfo record);
}