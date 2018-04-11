package org.yansou.common.crawl.util;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * URL帮助类
 * 
 * @author yangluan
 * @date 2014-11-18
 */
public final class URLUtils {
	private URLUtils() {

	}

	private static final String str = "!*'();:@&=+$,/?#-_.~01234567890QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm%";
	private static final Set<Character> set = new HashSet<Character>();
	private static final String SEPARATOR = "://";
	static {
		for (int i = 0; i < str.length(); i++) {
			set.add(str.charAt(i));
		}
	}

	/**
	 * @param url
	 *            传入的url
	 * @return utf-8编码格式的url
	 * @throws UnsupportedEncodingException
	 *             出现错误抛出的异常
	 */
	public final static String autoEncode(String url) {
		StringBuffer sb = new StringBuffer();
		int length = url.length();
		for (int i = 0; i < length; i++) {
			char c = url.charAt(i);
			if (set.contains(c)) {
				sb.append(c);
			} else {
				if (' ' == c) {
					sb.append("%20");
				} else {
					try {
						sb.append(URLEncoder.encode(c + "", "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return sb.toString();
	}

	public final static String standardizationString(String url) {
		if (StringUtils.isBlank(url)) {
			return url;
		}
		// http://www.hnstartx.com/././././shussan.php
		String top = "http";
		String host = "";
		String path = "/";
		String query = null;
		int length = url.length();
		StringBuilder sb = new StringBuilder();
		int status = 0;
		n1: for (int i = 0; i < length; i++) {
			char c = url.charAt(i);
			switch (status) {
			case 0:
				if (c == ':' && url.charAt(i + 1) == '/' && url.charAt(i + 2) == '/') {
					i++;
					i++;
					top = sb.toString();
					sb.setLength(0);
					status = 1;
					continue n1;
				}
				break;
			case 1:
				if (c == '/') {
					host = sb.toString();
					sb.setLength(0);
					i--;
					status = 2;
					continue n1;
				}
				break;
			case 2:
				if (c == '?') {
					path = sb.toString();
					sb.setLength(0);
					status = 3;
					continue n1;
				}
				break;
			case 3:
				if (i + 1 == length) {
					sb.append(c);
					query = sb.toString();
					sb.setLength(0);
					break n1;
				}
				break;
			default:
				break;
			}

			sb.append(c);

			if (i + 1 == length) {
				if (1 == status) {
					host = sb.toString();
					path = "/";
				} else if (2 == status) {
					path = sb.toString();
				}
				sb.setLength(0);
				break n1;
			}
		}
		for (;;) {
			String newPath = pathUnit(path);
			if (StringUtils.equals(newPath, path)) {
				break;
			}
			path = newPath;
		}
		if (StringUtils.isBlank(path)) {
			path = "/";
		}
		StringBuffer sb2 = new StringBuffer();
		sb2.append(top);
		sb2.append(SEPARATOR);
		sb2.append(host);
		sb2.append(path);
		if (StringUtils.isNotBlank(query)) {
			sb2.append('?');
			sb2.append(query);
		}
		return sb2.toString().replace("/./", "/");
	}

	/**
	 * URL规范化
	 * 
	 * @param url
	 * @return
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 */
	public final static URL standardization(URL url) throws URISyntaxException, MalformedURLException {
		String path = url.getPath();
		String authority = url.getAuthority();
		String query = url.getQuery();
		for (;;) {
			String newPath = pathUnit(path);
			if (StringUtils.equals(newPath, path)) {
				break;
			}
			path = newPath;
		}
		if (StringUtils.isBlank(path)) {
			path = "/";
		}
		StringBuffer sb = new StringBuffer();
		sb.append(url.getProtocol());
		sb.append(SEPARATOR);
		sb.append(authority);
		sb.append(path);
		if (StringUtils.isNotBlank(query)) {
			sb.append('?');
			sb.append(query);
		}
		return new URL(sb.toString());
	}

	public static String getRightUrl(String url) throws UnsupportedEncodingException {
		String partOne = url.substring(0, url.lastIndexOf('/') + 1);
		String partTwo = url.replace(partOne, "");
		Pattern pat = Pattern.compile("%[A-Z_0-9][A-Z_0-9]");
		Matcher matPartTwo = pat.matcher(partTwo);

		if (matPartTwo.find()) {
			for (int i = 0; i < 3; i++) {
				partTwo = URLDecoder.decode(partTwo, "UTF-8");
			}
		} else {
			if (url.matches("[\\w*\\p{Punct}]*")) {
				return url;
			}
		}

		partTwo = URLEncoder.encode(partTwo, "UTF-8");

		return partOne + partTwo;

	}

	private static final Pattern pattern = Pattern.compile("^\\w+://");

	public static String resolveString(String destUrl, String baseUrl) {
		if (destUrl == null) {
			return null;
		}
		String uri = getPath(destUrl, baseUrl);
		uri = decode20(uri);
		int index = uri.indexOf('#');
		if (index > 0) {
			uri = uri.substring(0, index);
		}
		String nURL = standardizationString(uri);
		nURL = encode20(nURL);
		return nURL;
	}

	public static URL resolve(final String baseUri, final URI reference)
			throws UnsupportedEncodingException, MalformedURLException, URISyntaxException {
		String uri = baseUri;
		uri = autoEncode(uri);
		uri = StringUtils.replace(uri, "&amp;", "&");
		if (!pattern.matcher(uri).find()) {
			if (StringUtils.startsWith(uri, "/")) {
				uri = reference.getScheme() + SEPARATOR + reference.getAuthority() + uri;
			} else if (StringUtils.startsWith(uri, "?")) {
				uri = reference.getScheme() + SEPARATOR + reference.getAuthority() + reference.getPath() + uri;
			} else {
				String path = reference.getPath();
				if (StringUtils.isBlank(path)) {
					path = "/";
				}
				int index = path.lastIndexOf('/');
				String dir = path.substring(0, index) + "/";
				uri = reference.getScheme() + SEPARATOR + reference.getAuthority() + dir + uri;
			}
		}
		uri = decode20(uri);
		String nURL = standardization(new URL(uri)).toString();
		nURL = encode20(nURL);
		return new URL(nURL);
	}

	static final String pathUnit(String path) {
		StringBuffer sb = new StringBuffer();
		String[] sp = StringUtils.split(path, '/');
		for (int i = 0; i < sp.length; i++) {
			if (i + 1 < sp.length && StringUtils.equals("..", StringUtils.trim(sp[i + 1]))) {
				i++;
				continue;
			}
			sb.append('/');
			sb.append(sp[i]);
		}
		return sb.toString();
	}

	static final String encode20(String urlStr) {
		StringBuffer sb = new StringBuffer();
		int length = urlStr.length();
		for (int i = 0; i < length; i++) {
			char c = urlStr.charAt(i);
			if (' ' == c) {
				sb.append("%20");
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	static final String decode20(String urlStr) {
		if (null == urlStr) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		int length = urlStr.length();
		for (int i = 0; i < length; i++) {
			char c = urlStr.charAt(i);
			if (c == '%' && i + 2 < length && '2' == urlStr.charAt(i + 1) && '0' == urlStr.charAt(i + 2)) {
				sb.append(' ');
				i++;
				i++;
			} else if (c == '+') {
				sb.append(' ');
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static final String getPath(String url) {
		if (StringUtils.isBlank(url)) {
			throw new IllegalArgumentException("url is blank.");
		}
		int top = StringUtils.indexOf(url, "://");
		if (StringUtils.INDEX_NOT_FOUND == top) {
			throw new IllegalArgumentException("url err. url=" + url);
		}
		top += 3;

		int end = StringUtils.indexOf(url, '/', top);
		if (StringUtils.INDEX_NOT_FOUND == end) {
			end = url.length();
		}

		return StringUtils.substring(url, end, url.length());
	}

	/**
	 * 获得Host
	 * 
	 * @param url
	 * @return
	 */
	public static final String getHost(String url) {
		if (StringUtils.isBlank(url)) {
			throw new IllegalArgumentException("url is blank.");
		}
		int top = StringUtils.indexOf(url, "://");
		if (StringUtils.INDEX_NOT_FOUND == top) {
			throw new IllegalArgumentException("url err. url=" + url);
		}
		top += 3;

		int end = StringUtils.indexOf(url, '/', top);
		if (StringUtils.INDEX_NOT_FOUND == end) {
			end = url.length();
		}

		return StringUtils.substring(url, top, end);
	}

	/**
	 * 取得url内的参数
	 * 
	 * @param source
	 * @param key
	 * @return
	 */
	public static final String getParameter(String source, String key) {
		Pattern p = Pattern.compile("[?&]" + key + "=([^&?]*)");
		Matcher m = p.matcher(source);
		if (m.find()) {
			return m.group(1);
		}
		return "";

	}

	public static final String encodeUrlGBK(String url) {
		if (StringUtils.isBlank(url)) {
			return url;
		}
		int index = url.indexOf('?');
		String top = url.substring(0, index + 1);
		String end = url.substring(index + 1);
		try {
			StringBuffer sb = new StringBuffer();
			sb.append(top);
			String ends[] = end.split("&");
			for (String e : ends) {
				int i2 = e.indexOf('=');
				String key = e.substring(0, i2 + 1);
				String value = e.substring(i2 + 1);
				value = URLEncoder.encode(value, "GBK");
				sb.append(key).append(value).append('&');
			}
			if (sb.length() > 0)
				sb.delete(sb.length() - 1, sb.length());
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return url;
		}
	}

	public static final String encodeUrlUTF_8(String url) {
		if (StringUtils.isBlank(url)) {
			return url;
		}
		int index = url.indexOf('?');
		String top = url.substring(0, index + 1);
		String end = url.substring(index + 1);
		try {
			StringBuffer sb = new StringBuffer();
			sb.append(top);
			String ends[] = end.split("&");
			for (String e : ends) {
				int i2 = e.indexOf('=');
				String key = e.substring(0, i2 + 1);
				String value = e.substring(i2 + 1);
				value = URLEncoder.encode(value, "UTF-8");
				sb.append(key).append(value).append('&');
			}
			if (sb.length() > 0)
				sb.delete(sb.length() - 1, sb.length());
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return url;
		}
	}

	/**
	 * 将相对URL路径转换为绝对URL路径
	 * 
	 * @param destUrl
	 *            目标路径
	 * @param ref
	 *            当前路径
	 * @return
	 */
	private static String getPath(String destUrl, String ref) {
		if (StringUtils.isBlank(destUrl) || StringUtils.isBlank(ref)) {
			return destUrl;
		}
		ref = StringUtils.trim(ref);
		destUrl = StringUtils.trim(destUrl);
		String rpath = "";
		// 判断传入参数是否是绝对路径.是则直接返回
		if (destUrl.startsWith("http://") || destUrl.startsWith("https://")) {
			rpath = destUrl;
		} else {
			String upath = null;
			// 判断相对路径是否指定到根路径
			if (destUrl.startsWith("/")) {
				// 截取当前路径的跟路径
				int index = ref.indexOf('/', 8);
				if (index > 8)
					upath = ref.substring(0, index);
			} else
			// 判斷当前路径,是否只是参数
			if (destUrl.startsWith("?")) {
				// 截取当前页面的路径
				int index = ref.indexOf('?', 8);
				if (index > 8)
					upath = ref.substring(0, index);
				if (null == upath) {
					upath = ref;
				}
			} else {
				// 截取到当前目录路径
				upath = matchNo1(ref, "https?://.*/", 0);
				int index = ref.lastIndexOf('/', 8) + 1;
				if (index > 8)
					upath = ref.substring(0, index);
			}
			rpath = upath + destUrl;
		}
		// &符号的转义,将被处理 //多余的相对路径,将被删除
		return StringUtils.replace(rpath, "&amp;", "&");
	}

	/**
	 * 正则匹配
	 * 
	 * @param str
	 * @param regx
	 * @param group
	 * @return
	 */
	private static String matchNo1(String str, String regx, int group) {
		if (null == regx) {
			return null;
		}
		return natchNo1(str, Pattern.compile(regx), group);
	}

	private static String natchNo1(String str, Pattern pattern, int group) {
		if (null == str) {
			return null;
		}
		Matcher m = pattern.matcher(str);
		if (m.find()) {
			return m.group(group);
		}
		return null;
	}

	static Pattern _x = Pattern.compile("\\\\x([1-9a-f]{2})");

	/**
	 * decodeAscii 顾名思义吧。
	 * 
	 * @param str
	 * @return
	 */
	public static String decodeAscii(String str) {
		Matcher matcher = _x.matcher(str);
		int end = 0;
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			int thisStart = matcher.start(0);
			sb.append(str.substring(end, thisStart));
			end = matcher.end(0);
			String s = matcher.group(1);
			char c = (char) Integer.parseInt(s, 16);
			sb.append(c);
		}
		sb.append(str.substring(end));
		return sb.toString();
	}

}
