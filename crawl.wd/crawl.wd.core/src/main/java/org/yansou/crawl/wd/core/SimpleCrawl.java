package org.yansou.crawl.wd.core;

import com.alibaba.fastjson.JSONObject;

public abstract class SimpleCrawl {
    private ChainHandler[] chainHandlers;

    protected abstract Site site();

    public SimpleCrawl(ChainHandler... chainHandlers) {
        this.chainHandlers = chainHandlers;
    }

    public void run() {
        JSONObject task = site().poolTask();
        for (; ; ) {
            if (null == task) {
                break;
            }
            runTask(task);
        }
    }

    public void runTask(JSONObject task) {
        Site site = site();
        int length = chainHandlers.length;
        for (int i = 0; i < length; i++) {
            chainHandlers[i].apply(task, site);
        }
    }

}
