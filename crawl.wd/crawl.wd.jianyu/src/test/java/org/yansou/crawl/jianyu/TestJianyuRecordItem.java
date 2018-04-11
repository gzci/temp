package org.yansou.crawl.jianyu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.yansou.crawl.wd.core.BasicSite;
import org.yansou.crawl.wd.jianyu.JianyuList;
import org.yansou.crawl.wd.jianyu.JianyuItem;

public class TestJianyuRecordItem {


    @Test
    public void testApply0() {
        JianyuList listChnia = new JianyuList();
        JianyuItem itemChnia = new JianyuItem();
        FirefoxDriver wd = new FirefoxDriver();
        BasicSite site = new BasicSite( wd );
        JSONObject task = new JSONObject();
        task.put( "url", "https://www.zhaobiao.info/article/content/ABCY2ZrfjIeOy86All1ZGI8DCc4QTJjR2hxP1gzKS4NaGhzZlpUCl4=.html" );
        itemChnia.setItemTask( task );
        itemChnia.setSeedTask( task );
        //执行登陆
        listChnia.apply( task, site );
        itemChnia.apply( task, site );

        System.out.println( JSON.toJSONString( task, 4 ) );
    }

}
