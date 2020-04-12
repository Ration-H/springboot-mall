package mapper;

import java.util.List;
import pojo.WmsWareInfo;

public interface WmsWareInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(WmsWareInfo record);

    WmsWareInfo selectByPrimaryKey(Long id);

    List<WmsWareInfo> selectAll();

    int updateByPrimaryKey(WmsWareInfo record);
}