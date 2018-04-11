package org.yansou.crawl;

import java.util.Set;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.yansou.common.crawl.util.WdUtils;

import com.google.common.base.Function;

public class TestTianyancha {
	@Test
	public void testGet() throws Exception {
		WebDriver wd = new FirefoxDriver();
		// 请求URL
		wd.get("http://www.tianyancha.com/company/1102759");
		Thread.sleep(500);
		waitPageLoad(wd);
		String page = wd.getPageSource();
		System.out.println(page.contains("xlbrauto@xlbrauto.cn"));
		System.out.println(page.contains("北京市朝阳区朝阳体育中心西路2号院3幢"));
	}

	/**
	 * 等待页面加载完毕。 <br>
	 * 这个方法可以留着，特别常用。
	 * 
	 * @param wd
	 */
	final static public void waitPageLoad(WebDriver wd) {
		Function<WebDriver, Boolean> un = (wwd) -> ((JavascriptExecutor) wwd)
				.executeScript("return document.readyState").equals("complete");
		for (;;) {
			WebDriverWait wait = new WebDriverWait(wd, 300);
			wait.until(un);
			if (un.apply(wd)) {
				break;
			}
		}
	}
}
