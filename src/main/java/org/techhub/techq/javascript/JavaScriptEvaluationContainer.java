package org.techhub.techq.javascript;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.techhub.techq.EvaluationContainer;

public class JavaScriptEvaluationContainer implements EvaluationContainer {

	private ScriptEngine engine;
	
	private Object lock = new Object();
	
	private boolean initialized = false;
	
	private StringWriter writer = new StringWriter();
	
	private void init(){
		engine = new ScriptEngineManager().getEngineByName("JavaScript");
		ScriptContext context = engine.getContext();
		context.setWriter(new PrintWriter(writer));
		context.setErrorWriter(new PrintWriter(writer));
		initialized = true;
	}
	
	@Override
	public String runScript(String script) {
		synchronized (lock) {
			if(!initialized){
				init();
			}
		}
		
		String output = "";
		try {
			engine.eval(script);
			writer.flush();
			output = writer.toString();
		} catch (ScriptException e) {
			output = e.getMessage();
		}
		return output;
	}

}
