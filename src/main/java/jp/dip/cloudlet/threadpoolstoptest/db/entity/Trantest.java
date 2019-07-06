package jp.dip.cloudlet.threadpoolstoptest.db.entity;

import java.sql.Timestamp;

/**
 * テーブル「trantest」のEntityクラス
 */
public class Trantest {
    private int id;
    private String name;
    private Timestamp lasttime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getLasttime() {
        return lasttime;
    }

    public void setLasttime(Timestamp lasttime) {
        this.lasttime = lasttime;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Trantest{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", lasttime=").append(lasttime);
        sb.append('}');
        return sb.toString();
    }
}
