package org.yansou.common.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import com.google.common.base.Preconditions;

/**
 * OSS词库适配器。
 * 
 * @author zhang
 *
 */
public class OSSIIOAdapter implements IIOAdapter {
	public OSSIIOAdapter() {
		this(DEFAULT_BASE_URL);
	}

	public OSSIIOAdapter(String baseUrl) {
		System.err.println("USE " + this.getClass());
		this.baseURL = baseUrl;
		this.cachePath = CACHE_ROOT_PATH + baseUrl.replaceAll("https?://[^/]+/", "");
	}

	// **默认 基本URL
	final static private String DEFAULT_BASE_URL = "http://yansoudic.oss-cn-qingdao.aliyuncs.com/hanlp/data-for-1.3.2/";
	// 缓存根路径，加上URL的路径，组成缓存路径。
	final static private String CACHE_ROOT_PATH = SystemUtils.getUserDir() + "/IOOCache/";
	/**
	 * 目标路径
	 */
	final private String baseURL;
	/**
	 * 缓存路径
	 */
	final private String cachePath;

	/**
	 * 线上词库到本地。
	 * 
	 * @param path
	 */
	private void onlineToLocal(String path) {
		try {
			System.err.println("online to local...");
			CloseableHttpResponse response = HttpClients.createDefault().execute(new HttpGet(baseURL + path));
			Preconditions.checkArgument(response.getStatusLine().getStatusCode() == 200,
					"StatusCode:" + response.getStatusLine().getStatusCode());
			long remoteLength = response.getEntity().getContentLength();
			// Arrays.asList(response.getAllHeaders()).forEach(System.out::println);
			byte[] rMd5 = Optional.ofNullable(response.getFirstHeader("Content-MD5")).map(base -> base.getValue())
					.map(Base64.getDecoder()::decode).orElse(null);
			BigInteger rCrc = Optional.ofNullable(response.getFirstHeader("x-oss-hash-crc64ecma"))
					.map(h -> h.getValue()).map(str -> new BigInteger(str)).orElse(null);
			long total = response.getEntity().getContentLength();
			boolean isPrintProgress = false;
			if (total / 4096 > 150) {
				isPrintProgress = true;
			}
			long sum = 0;
			int len;
			OutputStream output = openOutputStream(path);
			InputStream input = response.getEntity().getContent();

			byte[] buf = new byte[4096];
			int lostProgress = 0;
			while (-1 != (len = input.read(buf))) {
				sum += len;
				output.write(buf, 0, len);
				int progress = (int) (sum * 1.0f / total * 100);
				if (isPrintProgress & progress != lostProgress) {
					System.out.println(progress + "%");
					lostProgress = progress;
				}
			}
			long localLength = absolutePath(path).length();
			// 验证文件长度
			if (remoteLength > 0) {
				Preconditions.checkArgument(remoteLength == localLength,
						"File length inconsistency.[rl=" + remoteLength + ",ll=" + localLength + "]");
			}
			// 验证文件MD5
			if (Objects.nonNull(rMd5)) {
				MessageDigest md = MessageDigest.getInstance("MD5");
				byte[] lMd5 = md.digest(FileUtils.readFileToByteArray(absolutePath(path)));
				Preconditions.checkArgument(Arrays.equals(rMd5, lMd5), "context-md5 fail.");
			}
			if (Objects.nonNull(rCrc)) {
				System.out.println(rCrc);
			}
			System.err.println("read complete.");
		} catch (IOException | NoSuchAlgorithmException e) {
			absolutePath(path).deleteOnExit();
			throw new IllegalStateException(e);
		}
	}

	private OutputStream openOutputStream(String path) {
		File file = absolutePath(path);
		file.getParentFile().mkdirs();
		try {
			return new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * 获得绝对路径
	 * 
	 * @param path
	 * @return
	 */
	private File absolutePath(String path) {
		return new File(cachePath + path);
	}

	private InputStream openInputStream(String path) {
		File file = new File(cachePath + path);
		if (file.isFile()) {
			try {
				return new FileInputStream(file);
			} catch (FileNotFoundException e) {
				throw new IllegalStateException(e);
			}
		}
		return null;
	}

	@Override
	public InputStream open(String path) throws IOException {
		path = path.replaceAll("^/", "");
		InputStream res = null;
		res = openInputStream(path);
		if (null == res) {
			onlineToLocal(path);
			res = openInputStream(path);
		}
		return res;
	}

	@Override
	public OutputStream create(String path) throws IOException {
		path = path.replaceAll("^/", "");
		return openOutputStream(path);
	}

	public void clearCache() {
		try {
			FileUtils.deleteDirectory(new File(cachePath));
			System.err.println("Delete completed.");
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

}
