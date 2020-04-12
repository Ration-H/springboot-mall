package mapper;

import java.util.List;
import pojo.WmsWareSku;

public interface WmsWareSkuMapper {
    int deleteByPrimaryKey(Long id);

    int insert(WmsWareSku record);

    WmsWareSku selectByPrimaryKey(Long id);

    List<WmsWareSku> selectAll();

    int updateByPrimaryKey(WmsWareSku record);
}