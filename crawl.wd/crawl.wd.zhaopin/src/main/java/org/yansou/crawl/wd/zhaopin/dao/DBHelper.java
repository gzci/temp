package org.yansou.crawl.wd.zhaopin.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class DBHelper {
	static Map<String, MysqlDataSource> dataSourceMap = Maps.newConcurrentMap();
	static JSONObject configs;
	static {
		try (InputStream is = ClassLoader.getSystemResourceAsStream("db.json");) {
			String json = IOUtils.toString(is, "UTF-8");
			configs = JSON.parseObject(json);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	final static public Connection createConnection(String name) throws SQLException {
		MysqlDataSource ds = getDataSource(name);
		Preconditions.checkNotNull(ds, "没有配置这个数据库:" + name);
		return ds.getConnection();
	}

	public static MysqlDataSource getDataSource(String name) {
		MysqlDataSource dataSource = dataSourceMap.get(name);
		if (null == dataSource) {
			dataSource = initDataSource(configs.getJSONObject(name));
			if (null != dataSource) {
				dataSourceMap.put(name, dataSource);
			}
		}
		return dataSource;
	}

	private static MysqlDataSource initDataSource(JSONObject conf) {
		if (null == conf)
			return null;
		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setUrl(conf.getString("url"));
		dataSource.setUser(conf.getString("user"));
		dataSource.setPassword(conf.getString("pwd"));
		return dataSource;
	}

}
