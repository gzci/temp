package org.yansou.crawl.wd.jianyu;


import org.yansou.crawl.wd.core.*;

@DBTable("tab_bidd_jianyu")
public class BiddJianyu {
    @DBAutoIncrement
    @NoMatch
    @DBField(name = "id", type = "int unsigned not null auto_increment primary key")
    public int id;

    @DBField(name = "title", type = "varchar(800) COMMENT 'title名称'")
    public String title;

    @DBRowKey
    @DBNotNull
    @DBField(name = "url", type = "varchar(255) COMMENT 'URL'")
    public String url;

    @DBField(name = "context", type = "longtext COMMENT '正文'")
    public String context;

    @DBField(name = "seedUrl", type = "varchar(255) COMMENT 'URL'")
    public String seedUrl;

    @DBField(name = "time", type = "varchar(40) COMMENT '发布时间'")
    public String releaseTime;

    @DBField(name = "remark", type = "varchar(40) COMMENT'备注'")
    public String remark;

    @NoMatch
    @DBField(name = "insertTime", type = "timestamp default NOW()")
    public String insertTime;

    @DBField(name = "anchorText", type = "varchar(800) COMMENT '锚文本'")
    public String anchorText;

    public final String getAnchorText() {
        return anchorText;
    }

    public final void setAnchorText(String anchorText) {
        this.anchorText = anchorText;
    }

    @DBField(name = "refInfo", type = "longtext COMMENT '参考信息'")
    public String refInfo;

    public final int getId() {
        return id;
    }

    public final void setId(int id) {
        this.id = id;
    }

    public final String getTitle() {
        return title;
    }

    public final void setTitle(String title) {
        this.title = title;
    }

    public final String getUrl() {
        return url;
    }

    public final void setUrl(String url) {
        this.url = url;
    }

    public final String getContext() {
        return context;
    }

    public final void setContext(String context) {
        this.context = context;
    }

    public final String getSeedUrl() {
        return seedUrl;
    }

    public final void setSeedUrl(String seedUrl) {
        this.seedUrl = seedUrl;
    }

    public final String getReleaseTime() {
        return releaseTime;
    }

    public final void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }

    public final String getRemark() {
        return remark;
    }

    public final void setRemark(String remark) {
        this.remark = remark;
    }

    public final String getInsertTime() {
        return insertTime;
    }

    public final void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }

    public final String getRefInfo() {
        return refInfo;
    }

    public final void setRefInfo(String refInfo) {
        this.refInfo = refInfo;
    }

    @Override
    public String toString() {
        return super.toString() + " title=" + title + ",url=" + url;
    }

}
