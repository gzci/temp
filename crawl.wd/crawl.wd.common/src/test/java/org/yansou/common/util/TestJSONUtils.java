package org.yansou.common.util;

import java.nio.charset.Charset;

import org.junit.Assert;
import org.junit.Test;

import com.alibaba.fastjson.JSON;

public class TestJSONUtils {
	@Test
	public void testKeyRename() throws Exception {
		String str = org.apache.commons.io.IOUtils.toString(
				ClassLoader.getSystemResourceAsStream("org/yansou/crawl/rcc/util/test.json"), Charset.forName("GBK"));
		JSON json = (JSON) JSON.parse(str);
		System.out.println(json);
		JSONUtils.keyRename(json);
		System.out.println("********************");
		System.out.println(json);
	}
	
	@Test
	public void testTo_() throws Exception {
		Assert.assertEquals("project_number", JSONUtils.to_("projectNumber"));
		Assert.assertEquals("project_number_test", JSONUtils.to_("projectNumberTest"));
		Assert.assertEquals("t_number_num", JSONUtils.to_("tNumberNum"));
	}
}
