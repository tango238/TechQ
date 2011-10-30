package org.techhub.techq.ruby;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.jruby.embed.ScriptingContainer;
import org.techhub.techq.EvaluationContainer;

public class RubyEvaluationContainer implements EvaluationContainer {

	private ScriptingContainer ruby = new ScriptingContainer();
	
	@Override
	public String runScript(String script) {
		byte[] bytes = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream pstream = new PrintStream(baos);
		ruby.setOutput(pstream);
		ruby.runScriptlet(script);
		bytes = baos.toByteArray();
		pstream.close();
		try {
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String(bytes);
	}

}
