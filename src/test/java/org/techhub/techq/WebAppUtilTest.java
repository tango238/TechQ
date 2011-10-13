package org.techhub.techq;


import org.junit.Assert;
import org.junit.Test;

public class WebAppUtilTest {

	@Test
	public void testGetWebAppRoot() throws Exception {
		String root = WebAppUtil.getWebAppRoot();
		Assert.assertTrue(root.endsWith("techq/src/main/webapp/"));
	}
}
