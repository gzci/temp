package org.yansou.crawl.wd.jianyu;

import java.util.LinkedHashMap;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.yansou.common.crawl.util.JSOUPUtils;
import org.yansou.common.crawl.util.WdUtils;
import org.yansou.common.util.RegexUtils;
import org.yansou.crawl.wd.core.ChainHandler;
import org.yansou.crawl.wd.core.Site;

import com.alibaba.fastjson.JSONObject;

public class JianyuItem implements ChainHandler {
	@Override
	public Boolean apply(JSONObject task, Site site) {
		if (isItemTask(task)) {
			site.wd().get(task.getString("url"));
			WdUtils.waitPageLoad(site.wd(), 1000);
			String pageSource = site.wd().getPageSource();
			JSONObject result = new JSONObject(new LinkedHashMap<>());
			processResult(result, task, pageSource, site);
			task.put("result", result);
		}
		return true;
	}

	private void processResult(JSONObject result, JSONObject task, String pageSource, Site site) {
		Document dom = Jsoup.parse(pageSource, site.wd().getCurrentUrl());
		result.put("url", site.wd().getCurrentUrl());
		result.put("anchorText", task.getString("anchorText"));
		result.put("context", pageSource);
		JSOUPUtils.find(dom, "title").ifPresent(e -> result.put("title", e.text().replaceAll("- 剑鱼招标订阅$", "").trim()));
		JSOUPUtils.find(dom, "#spsj").ifPresent(e -> result.put("releaseTime", e.text()));
		JSOUPUtils.find(dom, ".com-time").filter((o) -> StringUtils.isBlank(result.getString("releaseTime")))
				.ifPresent(e -> result.put("releaseTime", e.text()));
		JSOUPUtils.find(dom, ".com-area a").ifPresent(e -> result.put("area", e.text()));
		JSOUPUtils.find(dom, ".com-type a").ifPresent(e -> result.put("type", e.text()));
		if (StringUtils.isBlank(result.getString("releaseTime"))) {
			result.put("releaseTime", RegexUtils.regex("[1-2][0-9][0-9][0-9]-[0-1][0-9]-[0-1][0-9]", pageSource, 0));
		}
		CheckUtils.check(StringUtils.contains(pageSource, "403 Forbidden"), () -> new Error("箭鱼招标403错误。"));
		CheckUtils.check(StringUtils.contains(pageSource, "网页错误"), () -> new Error("网页错误,可能被封了."));
		CheckUtils.check(StringUtils.contains(pageSource, "Time-out"), () -> new Error("访问超时，Time-out"));

	}
}
