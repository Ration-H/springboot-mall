package mapper;

import java.util.List;
import pojo.PaymentInfo;

public interface PaymentInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PaymentInfo record);

    PaymentInfo selectByPrimaryKey(Long id);

    List<PaymentInfo> selectAll();

    int updateByPrimaryKey(PaymentInfo record);
}