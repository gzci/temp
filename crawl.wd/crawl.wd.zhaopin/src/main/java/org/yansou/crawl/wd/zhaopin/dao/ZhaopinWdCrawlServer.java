package org.yansou.crawl.wd.zhaopin.dao;

import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.*;
import java.util.Arrays;

public class ZhaopinWdCrawlServer {

    public static void main(String[] args) throws Exception {
        FirefoxDriver wd = new FirefoxDriver();
        //Cookie文件。
        File cookieFile = new File( SystemUtils.getUserDir() + "JianyuWd-Cookies.cookie" );
        try {
            if (cookieFile.exists() && Arrays.binarySearch( args, "readCookie" ) > 0) {
                try (ObjectInputStream ois = new ObjectInputStream( new FileInputStream( cookieFile ) )) {
                    Cookie cookie = null;
                    while (null != (cookie = (Cookie) ois.readObject())) {
                        wd.manage().addCookie( cookie );
                    }
                    System.out.println( "读取Cookie." );
                }
            }
            new Zhaopin2WdCrawl().run( wd );
            System.out.println( "等待一天后，继续执行下一次" );
        } finally {
            try (ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream( cookieFile ) )) {
                wd.manage().getCookies().forEach( cookie -> {
                    try {
                        oos.writeObject( cookie );
                    } catch (IOException e) {
                        throw new IllegalStateException( e );
                    }
                } );
                System.out.println( "保存Cookie." );
            }
            wd.close();

        }

    }
}
