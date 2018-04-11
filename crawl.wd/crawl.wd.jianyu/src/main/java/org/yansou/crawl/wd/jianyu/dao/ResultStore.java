package org.yansou.crawl.wd.jianyu.dao;

import com.alibaba.fastjson.JSONObject;
import org.yansou.crawl.wd.core.ChainHandler;
import org.yansou.crawl.wd.core.Site;

import java.sql.SQLException;

/**
 * 剑雨信息保存入库
 */
public class ResultStore implements ChainHandler {


    @Override
    public Boolean apply(JSONObject task, Site site) {
        if (isItemTask( task ) && null != task.get( "result" )) {
            JSONObject result = task.getJSONObject( "result" );
            try {
            String anchorText=	result.getString("anchorText");
                site.getSaver().save( result );
                System.out.println("COMPLATE:"+anchorText);
            } catch (SQLException e) {
                throw new IllegalStateException( e );
            }
        }
        return true;
    }
}
