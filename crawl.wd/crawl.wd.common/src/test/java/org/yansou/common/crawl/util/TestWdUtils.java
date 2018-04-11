package org.yansou.common.crawl.util;

import java.net.URL;
import java.util.logging.Level;

import org.junit.Test;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.http.W3CHttpCommandCodec;
import org.openqa.selenium.remote.internal.ApacheHttpClient;
import org.openqa.selenium.remote.service.DriverCommandExecutor;

public class TestWdUtils {
	@Test
	public void testOpen() throws Exception {
		FirefoxDriver wd = new FirefoxDriver();
		wd.setLogLevel(Level.OFF);
		DriverCommandExecutor ce = (DriverCommandExecutor) wd.getCommandExecutor();
		wd.get("http://denglu.dlzb.com/");
		WdUtils.waitFindByCss(wd, "#username", 1000 * 10000).forEach(w -> {
			System.out.println(w);
		});
		System.out.println(ce.getClass());
	}

	@Test
	public void testLocalLogs() throws Exception {
		FirefoxDriver wd = new FirefoxDriver();
		wd.get("http://www.dlzb.com/nfdw/");
	}
}
