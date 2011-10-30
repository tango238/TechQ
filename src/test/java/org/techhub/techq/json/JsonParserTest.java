package org.techhub.techq.json;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;
import org.techhub.techq.util.FileUtil;


public class JsonParserTest {

	@Test
	public void testParse() throws Exception {
		String json = FileUtil.read("target/test-classes/sample.json");
		JsonParser parser = new JsonParser();
		Question q = parser.parse(json);
		
		assertThat(q.languageName, is("Ruby"));
		assertThat(q.inputString, is("print 'hoge'"));
	}
}
