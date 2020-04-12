package mapper;

import java.util.List;
import pojo.PmsCommentReplay;

public interface PmsCommentReplayMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PmsCommentReplay record);

    PmsCommentReplay selectByPrimaryKey(Long id);

    List<PmsCommentReplay> selectAll();

    int updateByPrimaryKey(PmsCommentReplay record);
}