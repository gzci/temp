package org.yansou.common.crawl.cdb;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

public abstract class CrawlJob<R> {
	private Class<R> requestClazz;
	private String rowkey;

	public void addRequest(R request) {
		Object obj = JSONObject.toJSON(request);
		System.out.println(obj);
		System.out.println(obj.getClass());
	}

	public List<R> getRequest() {
		return null;
	}

	public void success(R request) {
		// 请求成功
	}

	public void fail(R request) {
		// 请求失败
	}
}
