package org.yansou.crawl.rcc.util;

import java.util.Calendar;

import org.junit.Test;

public class TestTimeQuantum {
	@Test
	public void testTime() throws Exception {
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		System.out.println(hour);
	}
}
