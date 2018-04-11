package org.yansou.crawl.wd.jianyu.timer;
//添加quartz依赖
import org.openqa.selenium.firefox.FirefoxDriver;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.yansou.crawl.wd.jianyu.JianyuBiddWdCrawl;

public class SpiderTimer {
    public static void main(String[] args) {

        try {
            //1获取一个默认调度器
            Scheduler defaultScheduler = StdSchedulerFactory.getDefaultScheduler();
            //2开启调度器
            defaultScheduler.start();

            //封装要调度的任务
            String simpleName = SpiderJianYu.class.getSimpleName();
            JobDetail jobDetail = new JobDetail(simpleName,Scheduler.DEFAULT_GROUP, SpiderJianYu.class);
            //表示设置定时操作(每隔5秒执行一次)
            CronTrigger trigger = new CronTrigger(simpleName,Scheduler.DEFAULT_GROUP, "0 0/30 * * * ?");
            //3执行调度任务
            defaultScheduler.scheduleJob(jobDetail, trigger);

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("首次开始：");
        System.setProperty("webdriver.gecko.driver", "./geckodriver.exe");
        FirefoxDriver wd = new FirefoxDriver();
        new JianyuBiddWdCrawl(1).run(wd);
    }
}