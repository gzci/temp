package org.yansou.crawl.wd.jianyu;

import com.alibaba.fastjson.JSONObject;
import org.yansou.crawl.wd.core.ChainHandler;
import org.yansou.crawl.wd.core.Site;

import java.sql.SQLException;

/**
 * 剑雨信息保存入库
 */
public class JianyuStore implements ChainHandler {
    @Override
    public Boolean apply(JSONObject task, Site site) {
        if (isItemTask(task) && null != task.get("result")) {
            JSONObject result = task.getJSONObject("result");
            try {
                site.getSaver().save(result);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        }
        return true;
    }
}
