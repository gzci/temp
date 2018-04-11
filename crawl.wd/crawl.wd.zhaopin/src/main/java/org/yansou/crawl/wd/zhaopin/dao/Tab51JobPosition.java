package org.yansou.crawl.wd.zhaopin.dao;


import org.yansou.crawl.wd.core.DBField;
import org.yansou.crawl.wd.core.DBRowKey;
import org.yansou.crawl.wd.core.DBTable;

@DBTable("tab_51job_position")
public class Tab51JobPosition {
    //ID
    @DBField(name = "id", type = "int unsigned not null auto_increment primary key")
    public long id;
    //全部正文
    @DBField(name = "context", type = "longtext")
    public String context;
    //URL
    @DBRowKey
    @DBField(name = "url", type = "VARCHAR(255) DEFAULT NULL")
    public String url;
    //发布时间
    @DBField(name = "pub_time", type = "VARCHAR(40) COMMENT '发布时间'")
    public String pub_time;
    //公司名称
    @DBField(name = "company_name", type = "VARCHAR(255) DEFAULT NULL")
    public String companyName;

    //职务名称
    @DBField(name = "position_name", type = "VARCHAR(255) DEFAULT NULL")
    public String positionName;

    //职位描述
    @DBField(name = "position_description", type = "longtext")
    public String positionDescription;

    //职位关键字
    @DBField(name = "position_keyword", type = "VARCHAR(255) DEFAULT NULL")
    public String positionKeyword;

    //职位薪水
    @DBField(name = "position_salary", type = "VARCHAR(255) DEFAULT NULL")
    public String positionSalary;
    //职位类别
    @DBField(name = "position_category", type = "VARCHAR(255) DEFAULT NULL")
    public String positionCategory;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPub_time() {
        return pub_time;
    }

    public void setPub_time(String pub_time) {
        this.pub_time = pub_time;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getPositionDescription() {
        return positionDescription;
    }

    public void setPositionDescription(String positionDescription) {
        this.positionDescription = positionDescription;
    }

    public String getPositionKeyword() {
        return positionKeyword;
    }

    public void setPositionKeyword(String positionKeyword) {
        this.positionKeyword = positionKeyword;
    }

    public String getPositionSalary() {
        return positionSalary;
    }

    public void setPositionSalary(String positionSalary) {
        this.positionSalary = positionSalary;
    }

    public String getPositionCategory() {
        return positionCategory;
    }

    public void setPositionCategory(String positionCategory) {
        this.positionCategory = positionCategory;
    }
}
