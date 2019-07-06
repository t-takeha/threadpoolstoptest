package jp.dip.cloudlet.threadpoolstoptest.db.repository;

import jp.dip.cloudlet.threadpoolstoptest.db.entity.Trantest;
import jp.dip.cloudlet.threadpoolstoptest.db.mapper.TrantestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Repository
public class TrantestRepository {

    @Autowired
    TrantestMapper mapper;

    @Transactional
    public Trantest register(String name) {
        Trantest param = new Trantest();
        param.setName(name);
        param.setLasttime(Timestamp.valueOf(LocalDateTime.now()));
        mapper.insert(param);

        return param;
    }

    @Transactional
    public Trantest save(Trantest param) {
        param.setLasttime(Timestamp.valueOf(LocalDateTime.now()));
        mapper.update(param);

        return param;
    }

    @Transactional
    public Trantest find(int id) {
        Trantest trantest = mapper.select(id);
        return trantest;
    }
}
