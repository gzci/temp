package org.yansou.common.util;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class JSONUtils {
	final static public void copy(JSONObject src, JSONObject dest, String... resname) {
		if ((resname.length & 2) != 0) {
			throw new IllegalArgumentException("重命名参数表必须是偶数");
		}
		Map<String, String> rmMap = Maps.newHashMap();
		for (int i = 0; i < resname.length - 1; i += 2) {
			rmMap.put(resname[i], resname[i + 1]);
		}
		src.forEach((key, val) -> {
			if (rmMap.containsKey(key)) {
				key = rmMap.get(key);
			}
			dest.put(key, val);
		});
	}

	final public static void put(JSONObject json, String key, CharSequence val) {
		Objects.requireNonNull(key, "key is null.");
		Objects.requireNonNull(json, "json is null");
		if (StringUtils.isNotBlank(val)) {
			json.put(key, val);
		}
	}

	final public static void put(JSONObject json, String key, Object val) {
		Objects.requireNonNull(key, "key is null.");
		Objects.requireNonNull(json, "json is null.");
		if (null != val) {
			if (val instanceof JSONObject) {
				if (!((JSONObject) val).isEmpty()) {
					json.put(key, val);
				}
			} else if (val instanceof JSONArray) {
				if (!((JSONArray) val).isEmpty()) {
					json.put(key, val);
				}
			} else if (val instanceof CharSequence) {
				if (StringUtils.isNotBlank((CharSequence) val)) {
					json.put(key, val);
				}
			} else {
				json.put(key, val);
			}
		}
	}

	/**
	 * 将数组里的JSONObject转化成流
	 * 
	 * @param array
	 * @return
	 */
	final static public Stream<JSONObject> streamJSONObject(JSONArray array) {
		Builder<JSONObject> builder = Stream.builder();
		for (int i = 0; i < array.size(); i++) {
			builder.accept(array.getJSONObject(i));
		}
		return builder.build();
	}

	final public static void add(JSONArray arr, JSONObject obj) {
		Objects.requireNonNull(arr, "JSONArray is null.");
		if (null != obj && !obj.isEmpty()) {
			arr.add(obj);
		}
	}

	/**
	 * 将JSON对象中所有key值的驼峰命名方式改为下划线风格。
	 * 
	 * @param json
	 */
	final public static void keyRename(JSON json) {
		if (null == json) {
			return;
		}
		if (json instanceof JSONArray) {
			JSONArray arr = (JSONArray) json;
			for (int i = 0; i < arr.size(); i++) {
				String val = arr.getString(i);
				if (StringUtils.isBlank(val)) {
					continue;
				}
				val = StringUtils.trim(val);
				if (val.startsWith("{") && val.endsWith("}")) {
					keyRename(arr.getJSONObject(i));
				}
			}
		} else if (json instanceof JSONObject) {
			JSONObject obj = (JSONObject) json;

			List<String> keyList = Lists.newArrayList();
			for (Entry<String, Object> ent : obj.entrySet()) {
				keyList.add(ent.getKey());
			}
			for (String key : keyList) {
				String val = obj.getString(key);
				if (StringUtils.isBlank(val)) {
					continue;
				}
				String nKey = to_(key);
				if (!StringUtils.equals(key, nKey)) {
					Object _val = obj.get(key);
					obj.remove(key);
					obj.put(nKey, _val);
				}
				if (val.startsWith("{") && val.endsWith("}")) {
					keyRename(obj.getJSONObject(key));
				}
				if (val.startsWith("[") && val.endsWith("]")) {
					keyRename(obj.getJSONArray(key));
				}
			}
		}
	}

	final static String to_(String name) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (Character.isUpperCase(c)) {
				sb.append('_');
				c = Character.toLowerCase(c);
			}
			sb.append(c);
		}
		return sb.toString();
	}

	public static boolean isEmpty(JSONObject obj) {
		if (null == obj) {
			return true;
		}
		return obj.isEmpty();
	}
}
