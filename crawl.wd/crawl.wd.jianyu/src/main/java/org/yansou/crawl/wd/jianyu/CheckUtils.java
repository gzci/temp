package org.yansou.crawl.wd.jianyu;

import java.util.function.Supplier;

public class CheckUtils {
	public static void check(boolean check, Supplier<Throwable> suppler) {
		if (check) {
			Throwable thr = suppler.get();
			if (thr instanceof RuntimeException) {
				throw (RuntimeException) thr;
			}
			if (thr instanceof Error) {
				throw (Error) thr;
			}
			throw new IllegalStateException(thr);
		}
	}
}
