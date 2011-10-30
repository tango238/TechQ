package org.techhub.techq.ruby;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.jruby.embed.ScriptingContainer;
import org.junit.Test;
import org.techhub.techq.EvaluationContainer;
import org.techhub.techq.util.FileUtil;

public class RubyEvaluationContainerTest {

	@Test
	public void testName() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream pstream = new PrintStream(baos);
		ScriptingContainer ruby = new ScriptingContainer();
		ruby.setOutput(pstream);
		
		for(int i=1; i<10; i++){
			byte[] bytes = null;
			
			ruby.runScriptlet("print " + i);
			ruby.clear();
			bytes = baos.toByteArray();
			baos.flush();
			baos.reset();
			pstream.flush();
			
			assertThat(new String(bytes), is(String.valueOf(i)));
		}
		pstream.close();
		try {
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
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
		EvaluationContainer container = new RubyEvaluationContainer();
		String result = container.runScript(script);
		assertThat(result, is("this is method"));
		
		result = container.runScript(script);
		assertThat(result, is("this is method"));
	}
	
	
}
