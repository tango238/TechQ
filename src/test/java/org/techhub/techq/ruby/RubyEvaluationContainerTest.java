package org.techhub.techq.ruby;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.techhub.techq.EvaluationContainer;
import org.techhub.techq.util.FileUtil;

public class RubyEvaluationContainerTest {

	
	@Test
	public void testRunScript1() throws Exception {
		String script = FileUtil.read("target/test-classes/q1.rb");
		EvaluationContainer container = new RubyEvaluationContainer();
		String result = container.runScript(script);
		assertThat(result, is("this is method"));
	}
	
	@Test
	public void testRunScript2() throws Exception {
		String script = FileUtil.read("target/test-classes/q1.rb");
		for(int i=0; i<10; i++){
			EvaluationContainer container = new RubyEvaluationContainer();
			String result = container.runScript(script);
			assertThat(result, is("this is method"));
		}
	}
	
}
