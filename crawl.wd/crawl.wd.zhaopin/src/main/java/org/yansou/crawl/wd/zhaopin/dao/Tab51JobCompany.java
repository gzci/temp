package org.yansou.crawl.wd.zhaopin.dao;


import org.yansou.crawl.wd.core.DBField;
import org.yansou.crawl.wd.core.DBRowKey;
import org.yansou.crawl.wd.core.DBTable;

@DBTable("tab_51job_company")
public class Tab51JobCompany {
    //ID
    @DBField(name = "id", type = "int unsigned not null auto_increment primary key")
    public long id;
    //正文
    @DBField(name = "context", type = "longtext")
    public String context;
    //URL
    @DBRowKey
    @DBField(name = "url", type = "VARCHAR(255) DEFAULT NULL")
    public String url;
    //公司名称
    @DBField(name = "company_name", type = "VARCHAR(255) DEFAULT NULL")
    public String companyName;

    //公司性质
    @DBField(name = "company_nature", type = "VARCHAR(255) DEFAULT NULL")
    public String companyNature;

    //公司规模
    @DBField(name = "company_scale", type = "VARCHAR(255) DEFAULT NULL")
    public String companyScale;

    //经营范围
    @DBField(name = "company_scope_of_business", type = "VARCHAR(255) DEFAULT NULL")
    public String companyScopeOfBusiness;

    //公司介绍
    @DBField(name = "company_introduction", type = "longtext")
    public String companyIntroduction;

    //公司地址
    @DBField(name = "company_address", type = "VARCHAR(255) DEFAULT NULL")
    public String companyAddress;

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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyNature() {
        return companyNature;
    }

    public void setCompanyNature(String companyNature) {
        this.companyNature = companyNature;
    }

    public String getCompanyScale() {
        return companyScale;
    }

    public void setCompanyScale(String companyScale) {
        this.companyScale = companyScale;
    }

    public String getCompanyScopeOfBusiness() {
        return companyScopeOfBusiness;
    }

    public void setCompanyScopeOfBusiness(String companyScopeOfBusiness) {
        this.companyScopeOfBusiness = companyScopeOfBusiness;
    }

    public String getCompanyIntroduction() {
        return companyIntroduction;
    }

    public void setCompanyIntroduction(String companyIntroduction) {
        this.companyIntroduction = companyIntroduction;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }
}
