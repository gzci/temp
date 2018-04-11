package org.yansou.common.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.ResultSetHandler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class JSONArrayHandler implements ResultSetHandler<JSONArray> {
	final static BasicRowProcessor proc = new BasicRowProcessor();

	@Override
	public JSONArray handle(ResultSet rs) throws SQLException {
		JSONArray arr = new JSONArray();
		while (rs.next()) {
			arr.add(this.handleRow(rs));
		}
		return arr;
	}

	private JSONObject handleRow(ResultSet rs) throws SQLException {
		return new JSONObject(proc.toMap(rs));
	}

	final static public JSONArrayHandler create() {
		return new JSONArrayHandler();
	}

}
