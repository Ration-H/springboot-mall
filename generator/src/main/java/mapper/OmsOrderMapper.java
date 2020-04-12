package mapper;

import java.util.List;
import pojo.OmsOrder;

public interface OmsOrderMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OmsOrder record);

    OmsOrder selectByPrimaryKey(Long id);

    List<OmsOrder> selectAll();

    int updateByPrimaryKey(OmsOrder record);
}