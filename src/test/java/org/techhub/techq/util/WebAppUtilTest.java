package org.techhub.techq.util;


import org.junit.Assert;
import org.junit.Test;
import org.techhub.techq.util.WebAppUtil;

public class WebAppUtilTest {

	@Test
	public void testGetWebAppRoot() throws Exception {
		String root = WebAppUtil.getWebAppRoot();
		Assert.assertTrue(root.endsWith("techq/src/main/webapp/"));
	}
}
