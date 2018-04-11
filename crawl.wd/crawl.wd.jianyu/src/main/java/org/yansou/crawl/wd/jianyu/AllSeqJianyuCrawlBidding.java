package org.yansou.crawl.wd.jianyu;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

public class AllSeqJianyuCrawlBidding {

	public static void main(String[] args) {
		System.setProperty("webdriver.gecko.driver", "./geckodriver.exe");
		RemoteWebDriver wd = null;
		if (args.length == 1 && args[0].equals("control")) {
			wd = new PhantomJSDriver();
		}
		if (null == wd) {
			wd = new FirefoxDriver();
		}
		for (int i = 1; i <= 3; i++) {
			int num = i;
			new JianyuBiddWdCrawl(num).run(wd);
			System.out.println("《《任务" + num + "结束》》");
		}
		System.out.println("程序结束...");
		wd.close();
	}
}
