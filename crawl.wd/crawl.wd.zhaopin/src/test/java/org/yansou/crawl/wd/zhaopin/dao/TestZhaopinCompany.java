package org.yansou.crawl.wd.zhaopin.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.yansou.crawl.wd.core.BasicSite;

public class TestZhaopinCompany {

    @Test
    public void testApply() {
        DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
        capabilities.setJavascriptEnabled( false );
        PhantomJSDriver wd = new PhantomJSDriver( capabilities );
        ZhaopinCompany chain = new ZhaopinCompany();
        BasicSite site = new BasicSite( wd );
        JSONObject task = JSON.parseObject( "{'url':'http://jobs.51job.com/all/co2735473.html'}" );
        chain.apply( task, site );
    }
}
