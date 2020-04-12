package mapper;

import java.util.List;
import pojo.PmsComment;

public interface PmsCommentMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PmsComment record);

    PmsComment selectByPrimaryKey(Long id);

    List<PmsComment> selectAll();

    int updateByPrimaryKey(PmsComment record);
}