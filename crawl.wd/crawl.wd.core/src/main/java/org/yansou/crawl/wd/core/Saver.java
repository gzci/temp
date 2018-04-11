package org.yansou.crawl.wd.core;

import com.alibaba.fastjson.JSONObject;

import java.sql.SQLException;

public interface Saver {
    /**
     * 通过主键判断对象是否已经保存
     *
     * @param id
     * @return 已经保存返回 true 未保存返回false
     */
    boolean exist(Object id);

    /**
     * 保存对象
     *
     * @param entity
     */
    void save(JSONObject entity) throws SQLException;
}
