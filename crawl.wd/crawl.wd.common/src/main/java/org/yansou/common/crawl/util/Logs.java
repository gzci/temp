package org.yansou.common.crawl.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Logs {
	final static private Logger LOG = LoggerFactory.getLogger(Logs.class);

	final static public void info(Object msg) {
		System.out.println(msg);
	}

	final static public void debug(Object msg) {
		System.out.println(msg);
	}

	final static public void debug(String message, Throwable e) {
		System.out.println(message);
		e.printStackTrace();
	}

	final static public void debug(Throwable e) {
		debug(e.getMessage(), e);

	}

	final public static void info(String message, Throwable e) {
		System.out.println(message);
		e.printStackTrace();
	}

	final public static void info(Throwable e) {
		info(e.getMessage(), e);
	}

	public static void error(String string, Object... args) {

	}
}
