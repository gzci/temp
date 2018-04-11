package org.yansou.crawl.wd.core;

import com.alibaba.fastjson.JSONObject;

import java.util.function.BiFunction;

public interface ChainHandler extends BiFunction<JSONObject, Site, Boolean> {
    //用于记录任务状态的键名
    String TASK_STATUS_KEY = "_STATUS_";
    String ITEM = "ITEM";
    String LIST = "LIST";
    String SEED = "SEED";
    String UPDATE = "UPDATE";

    default JSONObject status(JSONObject task) {
        JSONObject taskStatus = task.getJSONObject( TASK_STATUS_KEY );
        if (null == taskStatus) {
            taskStatus = new JSONObject();
            task.put( TASK_STATUS_KEY, taskStatus );
        }
        return taskStatus;
    }

    default JSONObject setItemTask(JSONObject task) {
        status( task ).put( ITEM, true );
        return task;
    }

    default JSONObject setListTask(JSONObject task) {
        status( task ).put( LIST, true );
        return task;
    }

    default JSONObject setSeedTask(JSONObject task) {
        status( task ).put( SEED, true );
        return task;
    }

    default JSONObject setUpdate(JSONObject task, boolean isUpdate) {
        status( task ).put( UPDATE, isUpdate );
        return task;
    }

    default boolean isSeedTask(JSONObject task) {
        return status( task ).getBooleanValue( SEED );
    }

    default boolean isListTask(JSONObject task) {
        return status( task ).getBooleanValue( LIST );
    }

    default boolean isItemTask(JSONObject task) {
        return status( task ).getBooleanValue( ITEM );
    }

    default boolean isUpdate(JSONObject task) {
        return status( task ).getBooleanValue( UPDATE );
    }

    /**
     * @param task 当前任务对象，包含URL，状态，以及抽取到的信息。
     * @param site 持有所有公共资源
     * @return 返回为true继续执行下一个LinkNode，为false则中断。
     */
    @Override
    Boolean apply(JSONObject task, Site site);
}
