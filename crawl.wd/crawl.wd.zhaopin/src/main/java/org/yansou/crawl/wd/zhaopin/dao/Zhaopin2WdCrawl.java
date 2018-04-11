package org.yansou.crawl.wd.zhaopin.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.apache.commons.lang3.RandomUtils;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.yansou.common.crawl.util.dao.JSONObjectStoreMySQLDao;
import org.yansou.common.util.SleepUtils;
import org.yansou.crawl.wd.core.BasicSite;
import org.yansou.crawl.wd.core.ChainHandler;
import org.yansou.crawl.wd.core.Saver;
import sun.awt.image.ImageWatched;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class Zhaopin2WdCrawl {
	public static void main(String[] args) {
		DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
		// 关闭JS。
		capabilities.setJavascriptEnabled(false);
		PhantomJSDriver wd = new PhantomJSDriver(capabilities);
		new Zhaopin2WdCrawl().run(wd);
	}

	public void run(RemoteWebDriver wd) {
		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setUrl(
				"jdbc:mysql://biaoshuking.mysql.rds.aliyuncs.com:3306/hngp?useUnicode=true&characterEncoding=utf-8");
		dataSource.setUser("hngp");
		dataSource.setPassword("hngp123");
		BasicSite site = new MyBasicSite(wd);

		site.setUpdate(true);
		site.offerTask(JSON.parseObject(
				"{'_STATUS_':{'SEED':true,'LIST':true},'url':'http://search.51job.com/list/080200,000000,0000,00,9,99,%2B,2,1.html?lang=c&stype=1&postchannel=0000&workyear=99&cotype=99&degreefrom=99&jobterm=99&companysize=99&lonlat=0%2C0&radius=-1&ord_field=0&confirmdate=9&fromType=&dibiaoid=0&address=&line=&specialarea=00&from=&welfare='}"));
		List<ChainHandler> chainHandlerList = Arrays.asList(new ZhaopinList(), new ZhaopinCompany(),
				new ZhaopinPosition());
		for (;;) {
			JSONObject task = site.poolTask();
			if (null == task) {
				// 没有更多任务
				break;
			}
			for (int i = 0; i < chainHandlerList.size(); i++) {
				ChainHandler chainHandler = chainHandlerList.get(i);
				if (Boolean.FALSE.equals(  chainHandler.apply(task, site))) {
					break;
				}
			}
			int sleeptime = RandomUtils.nextInt(0, 2 * 1000);
			System.out.println("sleep time:" + sleeptime);
			SleepUtils.sleep(sleeptime);
		}
	}

	private static class MyBasicSite extends BasicSite {
		public MyBasicSite(RemoteWebDriver wd, Saver saver) {
			super(wd, saver);
		}

		public MyBasicSite(RemoteWebDriver wd) {
			super(wd);
		}

		private Map<String, LinkedBlockingQueue<JSONObject>> queueMap = new HashMap<>();

		@Override
		public void offerTask(JSONObject task) {
			String type = task.getString("TYPE");
			LinkedBlockingQueue<JSONObject> queue = queueMap.getOrDefault(type, new LinkedBlockingQueue<JSONObject>());
			queueMap.put(type, queue);
			queue.offer(task);
		}

		@Override
		public JSONObject poolTask() {
			LinkedBlockingQueue<JSONObject> maxQueue = null;
			for (LinkedBlockingQueue<JSONObject> queue : queueMap.values()) {
				if (null == maxQueue || queue.size() > maxQueue.size()) {
					maxQueue = queue;
				}
			}
			if (null != maxQueue) {
				return maxQueue.poll();
			}
			return null;
		}
	}
}
