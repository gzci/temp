package org.yansou.crawl.wd.jianyu.zjupdate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang3.RandomUtils;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.yansou.common.crawl.util.JSOUPUtils;
import org.yansou.common.crawl.util.URLUtils;
import org.yansou.common.crawl.util.dao.JSONObjectStoreMySQLDao;
import org.yansou.common.util.SleepUtils;
import org.yansou.crawl.wd.core.BasicSite;
import org.yansou.crawl.wd.core.ChainHandler;
import org.yansou.crawl.wd.core.Saver;
import org.yansou.crawl.wd.core.Site;
import org.yansou.crawl.wd.jianyu.dao.ResultStore;

import javax.management.Query;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ZhejiangCrawlUpdate implements Runnable {
    private ChainHandler[] chainHandlers;
    private Site site;

    public ZhejiangCrawlUpdate(JSONObject task) {
        chainHandlers = new ChainHandler[]{new ChainHandler() {
            @Override
            public Boolean apply(JSONObject task, Site site) {
                if (isListTask(task)) {
                    String url = task.getString("url");
                    site.wd().get(url);
                    System.out.println("Seed url:" + url);
                    String pageSource = site.wd().getPageSource();
                    JSOUPUtils.finds(pageSource, ".artcon_new li").forEach(li -> {
                        String type = li.select("a[href*=ggcgml]").text();
                        String itemUrl = li.select("a[title]").attr("href");
                        itemUrl = URLUtils.resolveString(itemUrl, url);
                        JSONObject itemTask = new JSONObject();
                        itemTask.put("type", type);
                        itemTask.put("url", itemUrl);
                        setItemTask(itemTask);
                        site.offerTask(itemTask);
                    });
                }
                return null;
            }
        }, new ChainHandler() {
            @Override
            public Boolean apply(JSONObject task, Site site) {
                if (isItemTask(task)) {
                    String url = task.getString("url");
                    String type = task.getString("type");
                    JSONObject result = new JSONObject();
                    result.put("url", url);
                    result.put("type", type);
                    task.put("result", result);
                }
                return null;
            }
        }, new ResultStore()};
        PhantomJSDriver wd = new PhantomJSDriver();
        BasicSite basicSite = new BasicSite(wd);
        //
        ComboPooledDataSource pooledDataSource = new ComboPooledDataSource();
        pooledDataSource.setJdbcUrl("jdbc:mysql://biaoshuking.mysql.rds.aliyuncs.com:3306/hngp?useUnicode=true&characterEncoding=utf-8");
        pooledDataSource.setUser("hngp");
        pooledDataSource.setPassword("hngp123");
        QueryRunner queryRunner = new QueryRunner(pooledDataSource);
        basicSite.setSaver(entity -> queryRunner.update("UPDATE tab_bidd SET refInfo=? WHERE url=?", entity.getString("refInfo"), entity.get("url")));
        this.site = basicSite;
        this.site.offerTask(task);
    }

    @Override
    public void run() {
        for (; ; ) {
            JSONObject task = site.poolTask();
            if (null == task) {
                break;
            }
            for (int i = 0; i < chainHandlers.length; i++) {
                ChainHandler chainHandler = chainHandlers[i];
                if (Boolean.FALSE == chainHandler.apply(task, site)) {
                    break;
                }
                SleepUtils.sleep(RandomUtils.nextLong(0, 1000));
            }
        }
    }

    public static void main(String[] args) {
        String[] list = new String[]{
                "浙江/省本级@http://www.zjzfcg.gov.cn/new/articleSearch/search.do?count=30&bidType=&region=339900&chnlIds=&bidMenu=&searchKey=&bidWay=&applyYear=2017&flag=1&releaseStartDate=&noticeEndDate=&releaseEndDate=&noticeEndDate1=&zjzfcg=0",
                "浙江/杭州市@http://www.zjzfcg.gov.cn/new/articleSearch/search.do?count=30&bidType=&region=330100&chnlIds=&bidMenu=&searchKey=&bidWay=&applyYear=2017&flag=1&releaseStartDate=&noticeEndDate=&releaseEndDate=&noticeEndDate1=&zjzfcg=0",
                "浙江/宁波市@http://www.zjzfcg.gov.cn/new/articleSearch/search.do?count=30&bidType=&region=330200&chnlIds=&bidMenu=&searchKey=&bidWay=&applyYear=2017&flag=1&releaseStartDate=&noticeEndDate=&releaseEndDate=&noticeEndDate1=&zjzfcg=0",
                "浙江/温州市@http://www.zjzfcg.gov.cn/new/articleSearch/search.do?count=30&bidType=&region=330300&chnlIds=&bidMenu=&searchKey=&bidWay=&applyYear=2017&flag=1&releaseStartDate=&noticeEndDate=&releaseEndDate=&noticeEndDate1=&zjzfcg=0",
                "浙江/嘉兴市@http://www.zjzfcg.gov.cn/new/articleSearch/search.do?count=30&bidType=&region=330400&chnlIds=&bidMenu=&searchKey=&bidWay=&applyYear=2017&flag=1&releaseStartDate=&noticeEndDate=&releaseEndDate=&noticeEndDate1=&zjzfcg=0",
                "浙江/湖州市@http://www.zjzfcg.gov.cn/new/articleSearch/search.do?count=30&bidType=&region=330500&chnlIds=&bidMenu=&searchKey=&bidWay=&applyYear=2017&flag=1&releaseStartDate=&noticeEndDate=&releaseEndDate=&noticeEndDate1=&zjzfcg=0",
                "浙江/绍兴市@http://www.zjzfcg.gov.cn/new/articleSearch/search.do?count=30&bidType=&region=330600&chnlIds=&bidMenu=&searchKey=&bidWay=&applyYear=2017&flag=1&releaseStartDate=&noticeEndDate=&releaseEndDate=&noticeEndDate1=&zjzfcg=0",
                "浙江/金华市@http://www.zjzfcg.gov.cn/new/articleSearch/search.do?count=30&bidType=&region=330700&chnlIds=&bidMenu=&searchKey=&bidWay=&applyYear=2017&flag=1&releaseStartDate=&noticeEndDate=&releaseEndDate=&noticeEndDate1=&zjzfcg=0",
                "浙江/衢州市@http://www.zjzfcg.gov.cn/new/articleSearch/search.do?count=30&bidType=&region=330800&chnlIds=&bidMenu=&searchKey=&bidWay=&applyYear=2017&flag=1&releaseStartDate=&noticeEndDate=&releaseEndDate=&noticeEndDate1=&zjzfcg=0",
                "浙江/舟山市@http://www.zjzfcg.gov.cn/new/articleSearch/search.do?count=30&bidType=&region=330900&chnlIds=&bidMenu=&searchKey=&bidWay=&applyYear=2017&flag=1&releaseStartDate=&noticeEndDate=&releaseEndDate=&noticeEndDate1=&zjzfcg=0",
                "浙江/台州市@http://www.zjzfcg.gov.cn/new/articleSearch/search.do?count=30&bidType=&region=331000&chnlIds=&bidMenu=&searchKey=&bidWay=&applyYear=2017&flag=1&releaseStartDate=&noticeEndDate=&releaseEndDate=&noticeEndDate1=&zjzfcg=0",
                "浙江/丽水@http://www.zjzfcg.gov.cn/new/articleSearch/search.do?count=30&bidType=&region=331100&chnlIds=&bidMenu=&searchKey=&bidWay=&applyYear=2017&flag=1&releaseStartDate=&noticeEndDate=&releaseEndDate=&noticeEndDate1=&zjzfcg=0"};
        ExecutorService pool = Executors.newFixedThreadPool(1);
        for (String s : list) {
            String url = s.split("@")[1];
            String remark = s.split("@")[0];
            JSONObject seed = new JSONObject();
            seed.put("_STATUS_", JSON.parseObject("{'SEED':true,'LIST':true}"));
            seed.put("remark", remark);
            seed.put("url", url);
            pool.execute(new ZhejiangCrawlUpdate(seed));
            break;
        }
        pool.shutdown();
    }
}
