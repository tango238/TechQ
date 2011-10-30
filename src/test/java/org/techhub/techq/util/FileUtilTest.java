package org.techhub.techq.util;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;


public class FileUtilTest {

	@Test
	public void testRead() throws Exception {
		String content = FileUtil.read("target/test-classes/aaa.txt");
		assertThat(content, is("hoge"));
	}
}
