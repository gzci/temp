package org.yansou.crawl.wd.jianyu;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.yansou.common.crawl.util.JSOUPUtils;
import org.yansou.common.crawl.util.WdUtils;
import org.yansou.crawl.wd.core.ChainHandler;
import org.yansou.crawl.wd.core.Site;

import com.alibaba.fastjson.JSONObject;

public class JianyuList implements ChainHandler {

	@Override
	public Boolean apply(JSONObject task, Site site) {
		if (isSeedTask(task) || isListTask(task)) {
			String url = task.getString("url");
			site.wd().get(url);
			WdUtils.waitPageLoad(site.wd(), 300);

			if (isSeedTask(task)) {
				String pageSource = site.wd().getPageSource();
				autoLogin((RemoteWebDriver) site.wd(), pageSource);
			}
			if (isListTask(task)) {
				String pageSource = site.wd().getPageSource();
				do {
					pageSource = site.wd().getPageSource();
					// 获得当前页所有的ItemTask
					List<JSONObject> itemTaskList = JSOUPUtils.finds(pageSource, ".lucene li").map(e -> {
						String itemUrl = "https://www.zhaobiao.info/article/content/"
								+ e.select("a[dataid]").attr("dataid") + ".html";
						JSONObject res = new JSONObject();
						res.put("url", itemUrl);
						res.put("anchorText", e.select("a[dataid]").text());
						return setItemTask(res);
					}).collect(Collectors.toList());
					for (JSONObject itemTask : itemTaskList) {
						// 判断当前url是否被保存
						if (site.getSaver().exist(itemTask.getString("url"))) {
							// 如果已被保存，则判断是否更新。
							if (site.isUpdate()) {
								// 添加更新任务。
								site.offerTask(setUpdate(itemTask, true));
							}
						} else {
							// 未保存，直接设置新任务。
							site.offerTask(itemTask);
						}
					}

				} while (clickNext(site.wd(), pageSource));
			}
			return true;
		}
		return true;
	}

	/**
	 * 点击下一页
	 *
	 * @param wd
	 * @return
	 */
	private boolean clickNext(WebDriver wd, String pageSource) {
		if (!pageSource.contains("小时前") && !pageSource.contains("天前")) {
			return false;
		}
		WebElement element = wd.findElement(By.cssSelector("[class*=nbnext]"));
		String _class = element.getAttribute("class");
		if (_class.contains("disabled")) {
			return false;
		} else {
			element.click();
			// 等待页面加载完毕,且最少两秒。
			WdUtils.waitPageLoad(wd, 2000);
			return true;
		}
	}

	/**
	 * 登陆逻辑
	 *
	 * @param wd
	 * @param pageSource
	 */
	private void autoLogin(RemoteWebDriver wd, String pageSource) {
		CookieTool.loadThisHostCookie(wd);
		wd.get(wd.getCurrentUrl());
		System.out.println("load cookies.");
		WdUtils.waitPageLoad(wd, 200);
		pageSource = wd.getPageSource();
		if (JSOUPUtils.find(pageSource, ".loginBtn").isPresent()) {
			if (!JSOUPUtils.find(pageSource, ".imgShow").isPresent()) {
				wd.findElementByClassName("loginBtn").click();
			} else {
				System.out.println("已登錄...");
				return;
			}
		}
		for (;;) {
			System.out.println("等待扫码登陆...");
			if (WdUtils.waitFindByCss(wd, ".imgShow", 2000).findAny().isPresent()) {
				WdUtils.waitPageLoad(wd, 300);
				System.out.println("登陆完成...");
				CookieTool.storeCookie(wd);
				System.out.println("store cookies.");
				break;
			}
		}
	}

}
