package jp.dip.cloudlet.threadpoolstoptest.db.mapper;

import jp.dip.cloudlet.threadpoolstoptest.db.entity.Trantest;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

public interface TrantestMapper {
//    @Select("SELECT id, #{name, jdbcType=VARCHAR}, #{lasttime, jdbcType=TIMESTAMP} FROM trantest where id = #{id}")
    @Select("SELECT id, name, lasttime FROM trantest where id = #{id}")
    Trantest select(int id);

    @Insert("INSERT INTO trantest (id, name, lasttime) VALUES (#{id}, #{name}, #{lasttime})")
    @SelectKey(statement="select trantest_seq.nextval from dual", keyProperty="id", before=true, resultType=int.class)
    void insert(Trantest param);

    @Update("UPDATE trantest SET name = #{name}, lasttime = #{lasttime} WHERE id = #{id}")
    int update(Trantest param);
}
