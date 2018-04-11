package org.yansou.crawl.wd.jianyu;


import org.yansou.crawl.wd.core.DBField;
import org.yansou.crawl.wd.core.DBRowKey;
import org.yansou.crawl.wd.core.DBTable;

@DBTable("tab_put_on_record")
public class PutOnRecord {
    @DBField(name = "id", type = "int unsigned not null auto_increment primary key")
    public long id;
    @DBField(name = "title", type = "varchar(255) DEFAULT NULL")
    public String title;
    @DBField(name = "context", type = "longtext")
    public String context;
    @DBField(name = "fmd5", type = "varchar(255) DEFAULT NULL")
    public String fmd5;
    @DBRowKey
    @DBField(name = "url", type = "varchar(255) DEFAULT NULL")
    public String url;
    @DBField(name = "anchor_text", type = "varchar(255) DEFAULT NULL")
    public String anchorText;
    @DBField(name = "area", type = "varchar(255) DEFAULT NULL")
    public String area;
    @DBField(name = "time", type = "varchar(40) COMMENT '发布时间'")
    public String releaseTime;
    @DBField(name = "remark", type = "varchar(255) DEFAULT NULL")
    public String remark;

    public String getAnchorText() {
        return anchorText;
    }

    public void setAnchorText(String anchorText) {
        this.anchorText = anchorText;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    public long getId() {
        return id;
    }

    public PutOnRecord setId(long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public PutOnRecord setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContext() {
        return context;
    }

    public PutOnRecord setContext(String context) {
        this.context = context;
        return this;
    }

    public String getFmd5() {
        return fmd5;
    }

    public PutOnRecord setFmd5(String fmd5) {
        this.fmd5 = fmd5;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public PutOnRecord setUrl(String url) {
        this.url = url;
        return this;
    }
}
