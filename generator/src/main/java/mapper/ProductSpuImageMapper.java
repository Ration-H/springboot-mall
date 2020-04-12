package mapper;

import java.util.List;
import pojo.ProductSpuImage;

public interface ProductSpuImageMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ProductSpuImage record);

    ProductSpuImage selectByPrimaryKey(Long id);

    List<ProductSpuImage> selectAll();

    int updateByPrimaryKey(ProductSpuImage record);
}