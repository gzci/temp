package org.yansou.crawl.wd.jianyu;

import com.alibaba.fastjson.JSONObject;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.yansou.common.crawl.util.JSOUPUtils;
import org.yansou.common.crawl.util.URLUtils;
import org.yansou.common.crawl.util.dao.JSONObjectStoreMySQLDao;
import org.yansou.crawl.wd.core.ChainHandler;
import org.yansou.crawl.wd.core.Site;

public class JianyuHostStore implements ChainHandler {
    private MysqlDataSource dataSource;
    private JSONObjectStoreMySQLDao tabHostDao;

    public JianyuHostStore() {
        dataSource = new MysqlDataSource();
        dataSource.setUrl( "jdbc:mysql://localhost:3306/guotest?useUnicode=true&characterEncoding=utf-8" );
        dataSource.setUser( "root" );
        dataSource.setPassword( "root" );
        tabHostDao = new JSONObjectStoreMySQLDao( dataSource, "tab_host", "host" );
    }


    @Override
    public Boolean apply(JSONObject task, Site site) {
        try {
            String context = task.getString( "context" );
            JSOUPUtils.find( context, ".com-original" ).map( e -> e.attr( "href" ) ).map( URLUtils::getHost ).filter( host -> tabHostDao.getIdInRowkey( host ) == null ).map( host -> {
                JSONObject object = new JSONObject();
                object.put( "host", host );
                return object;
            } ).ifPresent( json -> {
                tabHostDao.syncStore( json );
                System.out.println( "store host:" + json.getString( "host" ) );
            } );
        } catch (Exception e) {
            //出任何异常都catch住，保证主业务逻辑顺畅。
            e.printStackTrace();
        }
        return true;
    }
}
