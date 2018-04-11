package org.yansou.crawl.rcc.util;

import java.util.Random;

import org.junit.Test;

public class TestGL {
	@Test
	public void testGL() throws Exception {
		Random r = new Random();
		int zm = 0;
		int fm = 0;
		for (int i = 0; i < 100000000; i++) {
			if (r.nextBoolean()) {
				zm++;
			} else {
				fm++;
			}
		}
		System.out.println("正面概率" + (double) zm / (zm + fm));
		System.out.println("反面概率" + (double) fm / (zm + fm));
	}
}
