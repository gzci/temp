package org.yansou.common.crawl.cdb;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Maps;

public class TestCrawlJob {

	@Test
	public void testAddRequest() throws Exception {
		CrawlJob<Object> job = new CrawlJob<Object>() {
		};

		Map<String, String> map = Maps.newLinkedHashMap();
		map.put("url", "http://www.baidu.com/");
		job.addRequest(map);
	}
}
