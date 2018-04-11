package org.yansou.common.crawl.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import com.alibaba.fastjson.JSONPath;
import com.google.common.collect.Maps;

public class InpCodeUtils {
	final static private String API_URL = "http://ali-checkcode2.showapi.com/checkcode";
	// final static private String API_URL =
	// "http://ali-checkcode.showapi.com/checkcode";

	final static private String Authorization = "APPCODE 1ba0bab1011f477c98ab60a148d335ac";

	final static public String getCode(String url, String codeType) throws IOException {
		return getCode(IOUtils.toByteArray(new URL(url)), codeType);
	}

	final static public String getCode(byte[] data, String codeType) throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost request = new HttpPost(API_URL);
		request.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		request.setHeader("Authorization", Authorization);
		Map<String, String> body = Maps.newHashMap();
		body.put("typeId", codeType);
		body.put("img_base64", Base64.getEncoder().encodeToString(data));
		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(body.entrySet().stream()
					.map(ent -> new BasicNameValuePair(ent.getKey(), ent.getValue())).collect(Collectors.toList()));
			request.setEntity(entity);
			CloseableHttpResponse response = client.execute(request);
			if (200 != response.getStatusLine().getStatusCode()) {
				throw new IOException("STATUS_CODE:" + response.getStatusLine());
			}
			String jsonStr = IOUtils.toString(response.getEntity().getContent(), Charset.forName("UTF-8"));
			System.out.println(jsonStr);
			return JSONPath.read(jsonStr, "$.showapi_res_body.Result").toString();
		} catch (UnsupportedEncodingException e) {
			throw new IOException(e);
		}
	}
}
