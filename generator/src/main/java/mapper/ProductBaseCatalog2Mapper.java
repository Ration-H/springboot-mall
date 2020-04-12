package mapper;

import java.util.List;
import pojo.ProductBaseCatalog2;

public interface ProductBaseCatalog2Mapper {
    int deleteByPrimaryKey(Long id);

    int insert(ProductBaseCatalog2 record);

    ProductBaseCatalog2 selectByPrimaryKey(Long id);

    List<ProductBaseCatalog2> selectAll();

    int updateByPrimaryKey(ProductBaseCatalog2 record);
}