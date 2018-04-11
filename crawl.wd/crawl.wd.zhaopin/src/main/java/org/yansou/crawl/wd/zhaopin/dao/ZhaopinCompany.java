package org.yansou.crawl.wd.zhaopin.dao;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.yansou.common.crawl.util.JSOUPUtils;
import org.yansou.common.crawl.util.WdUtils;
import org.yansou.common.util.RegexUtils;
import org.yansou.crawl.wd.core.ChainHandler;
import org.yansou.crawl.wd.core.Site;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Optional;

public class ZhaopinCompany implements ChainHandler {

	EntityDao dao = new EntityDao(Tab51JobCompany.class);
	EntityDao pDao = new EntityDao(Tab51JobPosition.class);

	@Override
	public Boolean apply(JSONObject task, Site site) {
		String url = task.getString("url");
		if (StringUtils.isNotBlank(url) && url.matches("https?://[^/]+/[a-z-]+/co[0-9]+\\.html.*")) {
			site.wd().get(url);
			WdUtils.waitPageLoad(site.wd());
			String pageSource = site.wd().getPageSource();
			Document dom = Jsoup.parse(pageSource, site.wd().getCurrentUrl());
			// 获得职位链接，生成职位任务
			JSOUPUtils.finds(dom, "#joblistdata .el").forEach(e -> {
				JSONObject newTask = new JSONObject();
				// 职位名称
				String zwname = e.select("a.zw-name").attr("title");
				// 职位URL
				String zwUrl = e.select("a.zw-name").attr("href");
				zwUrl = Optional.of(zwUrl).map(u -> u.replaceAll("\\?.*$", "")).orElse(null);
				String pub_time = e.select(".t5").text();
				try {
					Date date = new SimpleDateFormat("MM-dd").parse(pub_time);
					newTask.put("pub_time", new SimpleDateFormat("yyyy-MM-dd").format(date));
				} catch (Exception e1) {
					throw new IllegalStateException(e1);
				}
				if (pDao.isInsert(zwUrl)) {
					newTask.put("url", zwUrl);
					newTask.put("TYPE", "POSITION");
					site.offerTask(newTask);
					System.out.println("add :" + zwUrl);
				}
			});
			// 获得公司信息并入库
			Tab51JobCompany t = new Tab51JobCompany();
			t.url = url;
			t.context = pageSource;
			t.companyName = JSOUPUtils.find(dom, "h1[title]").map(e -> e.attr("title")).orElse(null);
			t.companyAddress = JSOUPUtils.find(dom, ".fp").map(e -> e.text()).orElse(null);
			t.companyNature = JSOUPUtils.find(dom, ".ltype").map(e -> e.text())
					.map(txt -> RegexUtils.regex("[^\\|]+", txt, 0)).map(txt -> txt.replaceAll("[^\u4e00-\u9fa5]", ""))
					.orElse(null);
			t.companyScale = JSOUPUtils.find(dom, ".ltype").map(e -> e.text())
					.map(txt -> RegexUtils.regex("\\|([^\\|]+)", txt, 1)).map(txt -> txt).orElse(null);
			t.companyScopeOfBusiness = JSOUPUtils.find(dom, ".ltype").map(e -> e.text())
					.map(txt -> RegexUtils.regex("\\|([^\\|]+)|([^\\|]+)", txt, 2)).orElse(null);
			t.companyIntroduction = JSOUPUtils.find(dom, ".con_msg").map(e -> e.html()).orElse(null);
			// 集中检查字段

			if (dao.isInsert(t.getUrl())) {
				dao.ins(t);
			}
		}
		return true;
	}

}
