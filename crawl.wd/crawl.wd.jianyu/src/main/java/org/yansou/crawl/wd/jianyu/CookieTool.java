package org.yansou.crawl.wd.jianyu;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.yansou.common.crawl.util.URLUtils;
import org.yansou.crawl.wd.core.BasicSite;

public class CookieTool {

	static File file = new File("wdCookies.txt");

	public static void storeCookie(RemoteWebDriver wd) {

		file.delete();
		try {
			file.createNewFile();
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			for (Cookie ck : wd.manage().getCookies()) {
				bw.write(ck.getName() + ";" + ck.getValue() + ";" + ck.getDomain() + ";" + ck.getPath() + ";"
						+ ck.getExpiry() + ";" + ck.isSecure());
				bw.newLine();
			}
			bw.flush();
			bw.close();
			fw.close();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}

	}

	public static void loadThisHostCookie(RemoteWebDriver wd) {
		String host = URLUtils.getHost(wd.getCurrentUrl());
		if (!file.isFile()) {
			System.out.println("cookie文件不存在。");
			return;
		}
		try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr);) {
			String line;
			while ((line = br.readLine()) != null) {
				StringTokenizer str = new StringTokenizer(line, ";");
				while (str.hasMoreTokens()) {
					String name = str.nextToken();
					String value = str.nextToken();
					String domain = str.nextToken();
					String path = str.nextToken();
					Date expiry = null;
					String dt;
					if (!(dt = str.nextToken()).equals(null)) {
//						expiry = new Date(dt);
						System.out.println();
					}
					boolean isSecure = new Boolean(str.nextToken()).booleanValue();
					Cookie ck = new Cookie(name, value, domain, path, expiry, isSecure);
					if (StringUtils.contains(host, domain)) {
						System.out.println("cookies	 << " + ck);
						wd.manage().addCookie(ck);
					}
				}
			}

		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public static void main(String[] args) throws Exception {
		FirefoxDriver wd = new FirefoxDriver();
		BasicSite site = new BasicSite(wd);
		File coookieFile = new File("jianyu-cookie.txt");
		String url = "https://www.zhaobiao.info/swordfish/searchinfolist.html?keywords=%E5%A4%87%E6%A1%88+%E5%85%89%E4%BC%8F&searchvalue=%E5%A4%87%E6%A1%88%2B%E5%85%89%E4%BC%8F&selectType=title";
		wd.get(url);

	}
}
