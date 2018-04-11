package org.yansou.crawl.wd.jianyu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.RandomUtils;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.yansou.common.util.SleepUtils;
import org.yansou.crawl.wd.core.BasicSite;
import org.yansou.crawl.wd.core.ChainHandler;
import org.yansou.crawl.wd.core.Saver;
import org.yansou.crawl.wd.jianyu.dao.EntityInsertPipeline;
import org.yansou.crawl.wd.jianyu.dao.ResultStore;

import java.util.Arrays;
import java.util.List;

public class JianyuRecodeWdCrawl {

	public static void main(String[] args) {
		FirefoxDriver wd = new FirefoxDriver();
		new JianyuRecodeWdCrawl().run(wd);
	}

	public void run(RemoteWebDriver wd) {
		EntityInsertPipeline<PutOnRecord> pipeline = new EntityInsertPipeline<PutOnRecord>(PutOnRecord.class);
		BasicSite site = new BasicSite(wd);
		site.setUpdate(false);
		site.setSaver(new Saver() {
			@Override
			public boolean exist(Object id) {
				return !pipeline.getDao().isInsert(String.valueOf(id));
			}

			@Override
			public void save(JSONObject entity) {
				pipeline.process(entity, site);
			}
		});
		site.offerTask(JSON.parseObject(
				"{'_STATUS_':{'SEED':true,'LIST':true},'url':'https://www.zhaobiao.info/jylab/supsearch/proposedProject.html?keywords=%E5%85%89%E4%BC%8F&searchvalue=%E5%85%89%E4%BC%8F&publishtime=lately-7&timeslot=&area=&subtype=%E6%8B%9F%E5%BB%BA'}"));
		// site.offerTask(JSON.parseObject(
		// "{'_STATUS_':{'SEED':true,'LIST':true},'url':'https://www.zhaobiao.info/swordfish/searchinfolist.html?keywords=%E5%A4%87%E6%A1%88+%E5%85%89%E4%BC%8F&searchvalue=%E5%A4%87%E6%A1%88%2B%E5%85%89%E4%BC%8F&selectType=title'}"));
		List<ChainHandler> chainHandlerList = Arrays.asList(new JianyuList(), new JianyuItem(), new ResultStore());
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
				int sleeptime = RandomUtils.nextInt(500, 1000);
				SleepUtils.sleep(sleeptime);
			} // 停顿最长一分半。
			int sleeptime = RandomUtils.nextInt(10 * 1000, 30 * 1000);
			System.out.println("sleep time:" + sleeptime);
			SleepUtils.sleep(sleeptime);
		}
	}
}
