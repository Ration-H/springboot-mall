package mapper;

import java.util.List;
import pojo.ProductSkuImage;

public interface ProductSkuImageMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ProductSkuImage record);

    ProductSkuImage selectByPrimaryKey(Long id);

    List<ProductSkuImage> selectAll();

    int updateByPrimaryKey(ProductSkuImage record);
}