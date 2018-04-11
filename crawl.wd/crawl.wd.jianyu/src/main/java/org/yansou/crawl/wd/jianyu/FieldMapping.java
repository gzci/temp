package org.yansou.crawl.wd.jianyu;

import java.util.HashMap;

public class FieldMapping {
    //字段名
    private String name;
    //字段别名
    private String alias;
    //字段值搜索语句
    private String cssSelector;
    private String xpath;
    private String regex;
    //是否必须字段
    private Boolean required;
}
