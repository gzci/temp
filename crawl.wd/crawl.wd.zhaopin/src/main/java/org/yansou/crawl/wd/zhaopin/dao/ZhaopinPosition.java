package org.yansou.crawl.wd.zhaopin.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.yansou.common.crawl.util.JSOUPUtils;
import org.yansou.common.crawl.util.WdUtils;
import org.yansou.common.util.JSONUtils;
import org.yansou.crawl.wd.core.ChainHandler;
import org.yansou.crawl.wd.core.Site;

import java.sql.SQLException;

/**
 * 招聘网站的职位
 */
public class ZhaopinPosition implements ChainHandler {
    EntityDao dao = new EntityDao( Tab51JobPosition.class );

    @Override
    public Boolean apply(JSONObject task, Site site) {
        String url = task.getString( "url" );
        if (StringUtils.isNotBlank( url ) && url.matches( "https?://[^/]+/[a-z-]+/[0-9]+\\.html.*" )) {
            site.wd().get( url );
            WdUtils.waitPageLoad( site.wd() );
            Tab51JobPosition t = new Tab51JobPosition();
            String pageSource = site.wd().getPageSource();
            Document dom = Jsoup.parse( pageSource, site.wd().getCurrentUrl() );
            t.url = url;
            t.context = pageSource;
            t.companyName = JSOUPUtils.find( dom, ".cname a" ).map( e -> e.attr( "title" ) ).orElse( null );
            t.positionName = JSOUPUtils.find( dom, "h1[title]" ).map( e -> e.attr( "title" ) ).orElse( null );
            t.positionDescription = JSOUPUtils.find( dom, "[class=bmsg job_msg inbox]" ).map( e -> e.html() ).orElse( null );
            t.positionSalary = JSOUPUtils.find( dom, ".cn > strong" ).map( e -> e.text() ).orElse( null );
            t.positionCategory = JSOUPUtils.find( dom, ".el" ).map( e -> e.text() ).orElse( null );
            t.pub_time = task.getString( "pub_time" );
            //集中检查字段是否完整
            Preconditions.checkNotNull( t.url );
            Preconditions.checkNotNull( t.companyName );
            Preconditions.checkNotNull( t.positionName );
            Preconditions.checkNotNull( t.positionDescription );
            if (dao.isInsert( t.getUrl() )) {
                System.out.printf( "INS Ponsition:[%s,%s]", t.getPositionName(), t.getUrl() );
                dao.ins( t );
            }
        }
        return true;
    }
}
