package org.yansou.common.util;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 正则帮助类
 * 
 * @author zhang
 *
 */
public class RegexUtils {
	public final static String NUM_CHAR = "[1234567890一二三四五六七八九十]";

	private static final Logger LOG = LoggerFactory.getLogger(RegexUtils.class);

	private final static String[] getGroupNames(String regex) {
		List<String> names = regexList("(?is)\\(\\?<([a-z0-9-_]+)>.+?\\)", regex, 1);
		return names.toArray(new String[names.size()]);
	}

	/**
	 * 使用正则获取字符串中第一个匹配的内容。
	 * 
	 * @param regex
	 *            正则表达式
	 * @param text
	 *            目标字符串
	 * @param group
	 *            捕获组序号
	 * @return
	 */
	final static public String regex(String regex, String text, int group) {
		try {
			Matcher matcher = Pattern.compile(regex).matcher(text);
			if (matcher.find()) {
				return matcher.group(group);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 
	 * @param regex
	 *            正则表达式
	 * @param text
	 *            目标字符串
	 * @param group
	 *            捕获组序号
	 * @return
	 */
	final static public List<String> regexList(String regex, String text, int group) {
		List<String> list = Lists.newArrayList();
		try {
			Matcher matcher = Pattern.compile(regex).matcher(text);
			while (matcher.find()) {
				list.add(matcher.group(group));
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return list;
	}

	/**
	 * 使用正则获取字符串中第一个匹配的内容。
	 * 
	 * @param regex
	 *            正则表达式
	 * @param text
	 *            目标字符串
	 * @param name
	 *            捕获组名
	 * @return
	 */
	final static public String regex(String regex, String text, String name) {
		try {
			Matcher matcher = Pattern.compile(regex).matcher(text);
			if (matcher.find()) {
				return matcher.group(name);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 
	 * @param regex
	 *            正则表达式
	 * @param text
	 *            目标字符串
	 * @param name
	 *            捕获组名
	 * @return
	 */
	final static public List<String> regexList(String regex, String text, String name) {
		List<String> list = Lists.newArrayList();
		try {
			Matcher matcher = Pattern.compile(regex).matcher(text);
			while (matcher.find()) {
				list.add(matcher.group(name));
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return list;
	}

	/**
	 * 
	 * @param regex
	 *            正则表达式
	 * @param text
	 *            目标字符串
	 * @param names
	 *            捕获组名
	 * @return
	 */
	final static public List<Map<String, String>> regexToListMap(String regex, String text, String... names) {
		List<Map<String, String>> resList = Lists.newArrayList();
		try {
			Matcher matcher = Pattern.compile(regex).matcher(text);
			while (matcher.find()) {
				Map<String, String> map = Maps.newLinkedHashMap();
				for (String name : names) {
					map.put(name, matcher.group(name));
				}
				resList.add(map);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return resList;
	}

	final static public List<Map<String, String>> regexToListMap(String regex, String text) {
		return regexToListMap(regex, text, getGroupNames(regex));
	}

	/**
	 * 
	 * @param regex
	 *            正则表达式
	 * @param text
	 *            目标文本
	 * @param names
	 *            捕获组名
	 * @return
	 */
	final static public Map<String, String> regexToMap(String regex, String text, String... names) {
		Map<String, String> map = Maps.newLinkedHashMap();
		try {
			Matcher matcher = Pattern.compile(regex).matcher(text);
			if (matcher.find()) {
				for (String name : names) {
					map.put(name, matcher.group(name));
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return map;
	}

	final static public Map<String, String> regexToMap(String regex, String text) {
		return regexToMap(regex, text, getGroupNames(regex));
	}

	final static public JSONObject regexToJSONObject(String regex, String text, String... names) {
		JSONObject map = new JSONObject(Maps.newLinkedHashMap());
		try {
			Matcher matcher = Pattern.compile(regex).matcher(text);
			if (matcher.find()) {
				for (String name : names) {
					map.put(name, matcher.group(name));
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return map;
	}

	final static public JSONObject regexToJSONObject(String regex, String text) {
		return regexToJSONObject(regex, text, getGroupNames(regex));
	}

	final static public JSONArray regexToJSONArray(String regex, String text, String... names) {
		JSONArray resList = new JSONArray();
		try {
			Matcher matcher = Pattern.compile(regex).matcher(text);
			while (matcher.find()) {
				JSONObject map = new JSONObject(Maps.newLinkedHashMap());
				for (String name : names) {
					map.put(name, matcher.group(name));
				}
				resList.add(map);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return resList;
	}

	final static public JSONArray regexToJSONArray(String regex, String text) {
		return regexToJSONArray(regex, text, getGroupNames(regex));
	}
}
