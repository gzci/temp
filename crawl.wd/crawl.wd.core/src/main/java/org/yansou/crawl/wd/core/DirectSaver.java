package org.yansou.crawl.wd.core;

import com.alibaba.fastjson.JSONObject;

import java.sql.SQLException;

@FunctionalInterface
public interface DirectSaver extends Saver {

    default boolean exist(Object id) {
        return false;
    }

    @Override
    void save(JSONObject entity) throws SQLException;
}
