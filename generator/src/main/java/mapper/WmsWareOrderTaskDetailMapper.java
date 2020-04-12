package mapper;

import java.util.List;
import pojo.WmsWareOrderTaskDetail;

public interface WmsWareOrderTaskDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(WmsWareOrderTaskDetail record);

    WmsWareOrderTaskDetail selectByPrimaryKey(Long id);

    List<WmsWareOrderTaskDetail> selectAll();

    int updateByPrimaryKey(WmsWareOrderTaskDetail record);
}