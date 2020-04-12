package mapper;

import java.util.List;
import pojo.ProductBaseCatalog1;

public interface ProductBaseCatalog1Mapper {
    int deleteByPrimaryKey(Long id);

    int insert(ProductBaseCatalog1 record);

    ProductBaseCatalog1 selectByPrimaryKey(Long id);

    List<ProductBaseCatalog1> selectAll();

    int updateByPrimaryKey(ProductBaseCatalog1 record);
}