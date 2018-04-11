package org.yansou.common.crawl.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;
import org.yansou.common.util.ReflectUtils;

public class TestReflectUtils {
	@Test
	public void testGetMethod() throws Exception {
		ArrayList<?> list = new ArrayList<>();
		Number num = ReflectUtils.get(list, "modCount");
		assertEquals(0, num.intValue());
	}
}
