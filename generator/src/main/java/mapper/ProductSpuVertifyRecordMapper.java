package mapper;

import java.util.List;
import pojo.ProductSpuVertifyRecord;

public interface ProductSpuVertifyRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ProductSpuVertifyRecord record);

    ProductSpuVertifyRecord selectByPrimaryKey(Long id);

    List<ProductSpuVertifyRecord> selectAll();

    int updateByPrimaryKey(ProductSpuVertifyRecord record);
}