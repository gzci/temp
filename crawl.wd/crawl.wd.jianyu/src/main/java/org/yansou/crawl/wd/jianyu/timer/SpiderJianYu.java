package org.yansou.crawl.wd.jianyu.timer;
import java.text.SimpleDateFormat;
import java.util.Date;
//添加quartz依赖
import org.openqa.selenium.firefox.FirefoxDriver;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.yansou.crawl.wd.jianyu.JianyuBiddWdCrawl;

//通过quartz实现定时任务需要实现Job接口
public class SpiderJianYu implements Job{
    //重写Job中的execute方法
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        //设置时间输出格式
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
        String time = simpleDateFormat.format(new Date());
        //打印爬虫执行时间
        System.out.println(time);
        //模拟爬虫程序
        System.out.println("爬取剑鱼数据。。。。。。。。");


        FirefoxDriver wd = new FirefoxDriver();
        new JianyuBiddWdCrawl(1).run(wd);

    }
}
