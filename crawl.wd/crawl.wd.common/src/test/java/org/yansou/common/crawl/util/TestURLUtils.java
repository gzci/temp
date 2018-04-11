package org.yansou.common.crawl.util;

import org.junit.Assert;
import org.junit.Test;

public class TestURLUtils {
	@Test
	public void testGetPath() throws Exception {
		String url = "http://www.baidu.com/index.html";
		System.out.println(URLUtils.getPath(url));
		Assert.assertEquals("/index.html", URLUtils.getPath(url));
	}
}
