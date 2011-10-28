package org.techhub.techq.ruby;

import org.jruby.embed.ScriptingContainer;
import org.techhub.techq.EvaluationContainer;

public class RubyEvaluationContainer implements EvaluationContainer {

	@Override
	public String runScript(String script) {
		ScriptingContainer ruby = new ScriptingContainer();
		Long x = (Long)ruby.runScriptlet("x=144");
		Double result  = (Double)ruby.runScriptlet("Math.sqrt " + x);
		return "Square root of " + x + " is " + result;
	}

}
