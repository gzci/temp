package org.yansou.common.crawl.util;

import org.junit.Test;

import io.reactivex.Flowable;

public class TestRx {
	@Test
	public void testHelloWorld() throws Exception {
		Flowable.just("Hello world")
		.map(x->x.toUpperCase())
		.subscribe(System.out::println);
	}
}
