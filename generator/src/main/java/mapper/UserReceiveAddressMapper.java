package mapper;

import java.util.List;
import pojo.UserReceiveAddress;

public interface UserReceiveAddressMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserReceiveAddress record);

    UserReceiveAddress selectByPrimaryKey(Long id);

    List<UserReceiveAddress> selectAll();

    int updateByPrimaryKey(UserReceiveAddress record);
}