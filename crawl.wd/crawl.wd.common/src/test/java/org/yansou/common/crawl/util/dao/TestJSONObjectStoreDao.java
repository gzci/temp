package org.yansou.common.crawl.util.dao;

import org.junit.Test;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class TestJSONObjectStoreDao {
	@Test
	public void testWrite() throws Exception {
		MysqlDataSource ds = new MysqlDataSource();
		ds.setURL("jdbc:mysql://biaoshuking.mysql.rds.aliyuncs.com:3306/hngp?useUnicode=true&characterEncoding=utf-8");
		ds.setUser("hngp");
		ds.setPassword("hngp123");
		JSONObjectStoreMySQLDao dao = new JSONObjectStoreMySQLDao(ds, "tab_rcc_project", "rowkey");
		dao.close();
	}
}
