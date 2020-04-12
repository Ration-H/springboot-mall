package mapper;

import java.util.List;
import pojo.ProductBaseCatalog3;

public interface ProductBaseCatalog3Mapper {
    int deleteByPrimaryKey(Long id);

    int insert(ProductBaseCatalog3 record);

    ProductBaseCatalog3 selectByPrimaryKey(Long id);

    List<ProductBaseCatalog3> selectAll();

    int updateByPrimaryKey(ProductBaseCatalog3 record);
}