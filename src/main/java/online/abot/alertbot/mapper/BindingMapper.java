package online.abot.alertbot.mapper;

import online.abot.alertbot.domian.Binding;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by Administrator on 2017/10/22.
 */
@Mapper
public interface BindingMapper {
    /*@Select("select * from im_binding where im_id = #{im_id}")
    Binding findByQqId(@Param("im_id") String imId);*/

    @Select("select * from binding")
    List<Binding> findAll();

    @Insert("insert into binding(im_id,account_id,is_enabled) values(#{imId},#{accountId},#{isEnabled})")
    void insertQQBinding(Binding binding);

    @Delete("delete from binding where im_id =#{im_id} and account_id= #{account_id}")
    void deleteQQBinding(@Param("im_id") String imId, @Param("account_id") String accountId);

    @Update("update binding set is_enabled=#{is_enabled} where im_id =#{im_id} and account_id= #{account_id}")
    void updateQQBindingStatus(Binding binding);

}