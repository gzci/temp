package org.yansou.crawl.wd.zhaopin.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.yansou.crawl.wd.core.BasicSite;

public class TestZhaopinList {
    @Test
    public void testApply() {
        DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
        capabilities.setJavascriptEnabled( false );
        PhantomJSDriver wd = new PhantomJSDriver( capabilities );
        BasicSite site = new BasicSite( wd );
        JSONObject task = new JSONObject();
        task.put( "url", "http://search.51job.com/list/080200,000000,0000,00,9,99,%2B,2,1.html?lang=c&stype=1&postchannel=0000&workyear=99&cotype=99&degreefrom=99&jobterm=99&companysize=99&lonlat=0%2C0&radius=-1&ord_field=0&confirmdate=9&fromType=&dibiaoid=0&address=&line=&specialarea=00&from=&welfare=" );
        ZhaopinList chain = new ZhaopinList();
        chain.apply( task, site );
        for (; ; ) {
            JSONObject itask = site.poolTask();
            if (null == itask) {
                break;
            }
            System.out.println( itask );
        }
    }
}
