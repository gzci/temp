package org.yansou.crawl.wd.core;

import com.alibaba.fastjson.JSONObject;
import org.openqa.selenium.WebDriver;

/**
 * Site对象持有所有公共资源
 */
public interface Site {
    /**
     * 获得当前站点的web驱动
     *
     * @return
     */
    WebDriver wd();

    /**
     * 获得下一个任务
     *
     * @return
     */
    JSONObject poolTask();

    /**
     * 提供一个任务
     *
     * @param task
     */
    void offerTask(JSONObject task);


    Saver getSaver();


    boolean isUpdate();



    
}
