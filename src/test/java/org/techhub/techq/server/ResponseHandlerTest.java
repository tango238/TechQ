package org.techhub.techq.server;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import java.util.Map;

import org.junit.Test;


public class ResponseHandlerTest {

	@Test
	public void testParseParameter1() throws Exception {
		ResponseHandler handler = new ResponseHandler();
		Map<String, String> params = handler.parseParameter("aaa=111&bbb=222");
		assertThat(params.get("aaa"), is("111"));
		assertThat(params.get("bbb"), is("222"));
		assertThat(params.size(), is(2));
	}
	
	@Test
	public void testParseParameter2() throws Exception {
		ResponseHandler handler = new ResponseHandler();
		Map<String, String> params = handler.parseParameter("script=System.out.println%28%22Hello+World.%22%29;");
		assertThat(params.get("script"), is("System.out.println(\"Hello World.\");"));
	}
	
}
