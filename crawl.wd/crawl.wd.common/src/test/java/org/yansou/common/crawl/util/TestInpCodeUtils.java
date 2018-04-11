package org.yansou.common.crawl.util;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class TestInpCodeUtils {
	@Test
	public void testGetCode() throws Exception {
		String str = InpCodeUtils.getCode(FileUtils.readFileToByteArray(new File("H:/GetValidateCode.jpg")), "3040");
		System.out.println(str);
	}
}
