package org.techhub.techq.ruby;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;
import org.techhub.techq.EvaluationContainer;
import org.techhub.techq.util.FileUtil;

public class RubyEvaluationContainerTest {

	@Test
	public void testRunScript() throws Exception {
		String script = FileUtil.read("target/test-classes/q1.rb");
		EvaluationContainer container = new RubyEvaluationContainer();
		String result = container.runScript(script);
		assertThat(result, is("this is method"));
	}
}
