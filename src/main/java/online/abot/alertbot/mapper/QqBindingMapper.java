package online.abot.alertbot.mapper;

import online.abot.alertbot.domian.QqBinding;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by Administrator on 2017/10/22.
 */
@Mapper
public interface QqBindingMapper {
    /*@Select("select * from qq_binding where qq_id = #{qq_id}")
    QqBinding findByQqId(@Param("qq_id") String qqId);*/

    @Select("select * from qq_binding")
    List<QqBinding> findAll();

    @Insert("insert into qq_binding(qq_id,account_id,is_enabled) values(#{qqId},#{accountId},#{isEnabled})")
    void insertQQBinding(QqBinding qqBinding);

    @Delete("delete from qq_binding where qq_id =#{qq_id} and account_id= #{account_id}")
    void deleteQQBinding(@Param("qq_id") String qqId, @Param("account_id") String accountId);

    @Update("update qq_binding set is_enabled=#{is_enabled} where qq_id =#{qq_id} and account_id= #{account_id}")
    void updateQQBindingStatus(QqBinding qqBinding);

}