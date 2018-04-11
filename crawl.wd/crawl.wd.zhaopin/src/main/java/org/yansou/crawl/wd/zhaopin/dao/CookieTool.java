package org.yansou.crawl.wd.zhaopin.dao;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.yansou.common.crawl.util.JSOUPUtils;
import org.yansou.common.crawl.util.WdUtils;
import org.yansou.crawl.wd.core.BasicSite;

import java.io.*;
import java.util.Date;
import java.util.StringTokenizer;

public class CookieTool {

    /**
     * 保存所有Cookie到文件
     *
     * @param file
     * @param wd
     */
    public static void storeAllCookie(File file, RemoteWebDriver wd) {
        try {
            file.delete();
            file.createNewFile();
            FileWriter fw = new FileWriter( file );
            BufferedWriter bw = new BufferedWriter( fw );
            for (Cookie ck : wd.manage().getCookies()) {
                bw.write( ck.getName() + ";" + ck.getValue() + ";"
                        + ck.getDomain() + ";" + ck.getPath() + ";"
                        + ck.getExpiry() + ";" + ck.isSecure() );
                bw.newLine();
            }
            bw.flush();
            bw.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println( "cookie write to file" );
        }
    }

    /**
     * 读取所有Cookie设置到WebDriver
     *
     * @param file
     * @param wd
     */
    public static boolean readAndSetAllCookie(File file, RemoteWebDriver wd) {
        if (!file.isFile()) {
            System.out.println( "cookie文件不存在。" );
            return false;
        }
        try {
            FileReader fr = new FileReader( file );
            BufferedReader br = new BufferedReader( fr );
            String line;
            while ((line = br.readLine()) != null) {
                StringTokenizer str = new StringTokenizer( line, ";" );
                while (str.hasMoreTokens()) {
                    String name = str.nextToken();
                    String value = str.nextToken();
                    String domain = str.nextToken();
                    String path = str.nextToken();
                    Date expiry = null;
                    String dt;
                    if (!(dt = str.nextToken()).equals( null )) {
                        //expiry=new Date(dt);
                        System.out.println();
                    }
                    boolean isSecure = new Boolean( str.nextToken() ).booleanValue();
                    Cookie ck = new Cookie( name, value, domain, path, expiry, isSecure );
                    wd.manage().addCookie( ck );
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    public static void main(String[] args) throws Exception {
        FirefoxDriver wd = new FirefoxDriver();
        BasicSite site = new BasicSite( wd );
        File coookieFile = new File( "jianyu-cookie.txt" );
        String url = "https://www.zhaobiao.info/swordfish/searchinfolist.html?keywords=%E5%A4%87%E6%A1%88+%E5%85%89%E4%BC%8F&searchvalue=%E5%A4%87%E6%A1%88%2B%E5%85%89%E4%BC%8F&selectType=title";
        wd.get( url );
        if (readAndSetAllCookie( coookieFile, wd )) {
            System.out.println( "读取Cookie重新访问:" + url );
            wd.get( url );
        }
        String pageSource = wd.getPageSource();
        if (JSOUPUtils.find( pageSource, ".loginBtn" ).isPresent()) {
            WebElement element = wd.findElementByClassName( "loginBtn" );
            element.click();
        }
        for (; ; ) {
            System.out.println( "等待扫码登陆..." );
            if (WdUtils.waitFindByCss( wd, ".imgShow", 2000 ).findAny().isPresent()) {
                WdUtils.waitPageLoad( wd, 300 );
                System.out.println( "登陆完成..." );
                break;
            }
        }

        storeAllCookie( coookieFile, wd );
    }
}
