package mapper;

import java.util.List;
import pojo.UserLevel;

public interface UserLevelMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserLevel record);

    UserLevel selectByPrimaryKey(Long id);

    List<UserLevel> selectAll();

    int updateByPrimaryKey(UserLevel record);
}