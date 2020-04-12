package mapper;

import java.util.List;
import pojo.OmsCartItem;

public interface OmsCartItemMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OmsCartItem record);

    OmsCartItem selectByPrimaryKey(Long id);

    List<OmsCartItem> selectAll();

    int updateByPrimaryKey(OmsCartItem record);
}