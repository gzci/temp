package org.yansou.crawl.wd.core;

import org.openqa.selenium.WebDriver;

@FunctionalInterface
public interface WebDriverExtractField {
    void apply(String fieldName, String data, WebDriver wd, Site site);
}
