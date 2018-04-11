package org.yansou.crawl.wd.core;

import com.alibaba.fastjson.JSONObject;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.concurrent.LinkedBlockingQueue;

public class BasicSite implements Site {
    private final LinkedBlockingQueue<JSONObject> queue = new LinkedBlockingQueue();
    private final RemoteWebDriver wd;
    private boolean isUpdate;
    private long sleepTime = 1;


    public BasicSite setSaver(Saver saver) {
        this.saver = saver;
        return this;
    }

    public BasicSite setSaver(DirectSaver saver) {
        this.saver = saver;
        return this;
    }

    private Saver saver = new Saver() {
        @Override
        public boolean exist(Object id) {
            return false;
        }

        @Override
        public void save(JSONObject entity) {

        }
    };

    public BasicSite(RemoteWebDriver wd, Saver saver) {
        this.wd = wd;
        this.saver = saver;
    }

    public BasicSite(RemoteWebDriver wd) {
        this(wd, null);
    }


    @Override
    public RemoteWebDriver wd() {
        return wd;
    }

    @Override
    public JSONObject poolTask() {
        JSONObject task = queue.poll();
        System.out.println("始执行任务:" + task);
        return task;
    }

    @Override
    public void offerTask(JSONObject task) {
        System.out.println("发现新任务:" + task);
        queue.offer(task);
    }

    @Override
    public Saver getSaver() {
        return saver;
    }

    @Override
    public boolean isUpdate() {
        return isUpdate;
    }

    public long sleepTime() {
        return sleepTime;
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    public BasicSite setUpdate(boolean update) {
        isUpdate = update;
        return this;
    }
}
