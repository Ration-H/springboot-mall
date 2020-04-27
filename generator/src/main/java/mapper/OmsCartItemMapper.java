package mapper;

import pojo.OmsCartItem;

import java.util.List;

public interface OmsCartItemMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OmsCartItem record);

    OmsCartItem selectByPrimaryKey(Long id);

    List<OmsCartItem> selectAll();

    int updateByPrimaryKey(OmsCartItem record);
}