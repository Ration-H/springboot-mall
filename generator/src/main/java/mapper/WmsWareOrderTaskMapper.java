package mapper;

import java.util.List;
import pojo.WmsWareOrderTask;

public interface WmsWareOrderTaskMapper {
    int deleteByPrimaryKey(Long id);

    int insert(WmsWareOrderTask record);

    WmsWareOrderTask selectByPrimaryKey(Long id);

    List<WmsWareOrderTask> selectAll();

    int updateByPrimaryKey(WmsWareOrderTask record);
}