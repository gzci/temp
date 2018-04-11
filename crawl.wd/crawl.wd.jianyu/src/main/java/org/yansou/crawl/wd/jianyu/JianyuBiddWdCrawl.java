package org.yansou.crawl.wd.jianyu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.apache.commons.lang3.RandomUtils;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.yansou.common.crawl.util.dao.JSONObjectStoreMySQLDao;
import org.yansou.common.util.SleepUtils;
import org.yansou.crawl.wd.core.BasicSite;
import org.yansou.crawl.wd.core.ChainHandler;
import org.yansou.crawl.wd.core.Saver;
import org.yansou.crawl.wd.jianyu.dao.ResultStore;

import java.util.Arrays;
import java.util.List;

public class JianyuBiddWdCrawl {

	public JianyuBiddWdCrawl(int num) {
		this.num = num;
	}

	private int num = 0;

	public static void main(String[] args) {
		System.setProperty("webdriver.gecko.driver", "./geckodriver.exe");
		FirefoxDriver wd = new FirefoxDriver();
		new JianyuBiddWdCrawl(1).run(wd);
//		new JianyuBiddWdCrawl(2).run(wd);
//		new JianyuBiddWdCrawl(3).run(wd);
	}

	public void run(RemoteWebDriver wd) {
		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setUrl(
				"jdbc:mysql://localhost:3306/guotest?useUnicode=true&characterEncoding=utf-8");
		dataSource.setUser("root");
		dataSource.setPassword("root");
		try (JSONObjectStoreMySQLDao dao = new JSONObjectStoreMySQLDao(dataSource, "tab_raw_bidd", "url")) {
			BasicSite site = new BasicSite(wd);
			site.setUpdate(false);
			site.setSaver(new Saver() {
				@Override
				public boolean exist(Object id) {
					return null != dao.getIdInRowkey(String.valueOf(id));
				}

				@Override
				public void save(JSONObject entity) {
					dao.syncStore(entity);
				}
			});

			if (num == 1) {
				// 招标公告
				site.offerTask(JSON.parseObject(
						"{'_STATUS_':{'SEED':true,'LIST':true},'url':'https://www.zhaobiao.info/jylab/supsearch/index.html?keywords=%E5%85%89%E4%BC%8F&searchvalue=%E5%85%89%E4%BC%8F&publishtime=lately-7&timeslot=&area=&subtype=%E6%8B%9B%E6%A0%87%2C%E9%82%80%E6%A0%87%2C%E8%AF%A2%E4%BB%B7%2C%E7%AB%9E%E8%B0%88%2C%E5%8D%95%E4%B8%80%2C%E7%AB%9E%E4%BB%B7%2C%E5%8F%98%E6%9B%B4%2C%E5%85%B6%E4%BB%96&minprice=&maxprice=&industry=&selectType=all'}"));
			}
			if (num == 2) {
				// 招标结果
				site.offerTask(JSON.parseObject(
						"{'_STATUS_':{'SEED':true,'LIST':true},'url':'https://www.zhaobiao.info/jylab/supsearch/index.html?keywords=%E5%85%89%E4%BC%8F&searchvalue=%E5%85%89%E4%BC%8F&publishtime=lately-7&timeslot=&area=&subtype=%E4%B8%AD%E6%A0%87%2C%E6%88%90%E4%BA%A4%2C%E5%BA%9F%E6%A0%87%2C%E6%B5%81%E6%A0%87&minprice=&maxprice=&industry=&selectType=all'}"));
			}
			if (num == 3) {
				// 招标信用信息
				site.offerTask(JSON.parseObject(
						"{'_STATUS_':{'SEED':true,'LIST':true},'url':'https://www.zhaobiao.info/jylab/supsearch/index.html?keywords=%E5%85%89%E4%BC%8F&searchvalue=%E5%85%89%E4%BC%8F&publishtime=lately-7&timeslot=&area=&subtype=%E5%90%88%E5%90%8C%2C%E9%AA%8C%E6%94%B6%2C%E8%BF%9D%E8%A7%84&minprice=&maxprice=&industry=&selectType=all'}"));
				// 招标预告
				site.offerTask(JSON.parseObject(
						"{'_STATUS_':{'SEED':true,'LIST':true},'url':'https://www.zhaobiao.info/jylab/supsearch/index.html?keywords=%E5%85%89%E4%BC%8F&searchvalue=%E5%85%89%E4%BC%8F&publishtime=lately-7&timeslot=&area=&subtype=%E9%A2%84%E5%91%8A&minprice=&maxprice=&industry=&selectType=all'}"));
			}

			List<ChainHandler> chainHandlerList = Arrays.asList(new JianyuList(), new JianyuItem(), new ResultStore(),
					new JianyuHostStore());
			for (;;) {
				JSONObject task = site.poolTask();
				if (null == task) {
					// 没有更多任务
					break;
				}
				for (int i = 0; i < chainHandlerList.size(); i++) {
					ChainHandler chainHandler = chainHandlerList.get(i);
					if (Boolean.FALSE == chainHandler.apply(task, site)) {
						break;
					}
					int sleeptime = RandomUtils.nextInt(5, 10);
					SleepUtils.sleep(sleeptime);
				}
				int sleeptime = RandomUtils.nextInt(10, 30);
				System.out.println("sleep time:" + sleeptime);
				SleepUtils.sleep(sleeptime);
			}
		}
	}

}
