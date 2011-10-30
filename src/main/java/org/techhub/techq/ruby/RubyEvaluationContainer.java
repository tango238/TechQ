package org.techhub.techq.ruby;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jruby.embed.ScriptingContainer;
import org.techhub.techq.EvaluationContainer;

public class RubyEvaluationContainer implements EvaluationContainer {

	private Object lock = new Object();
	
	private boolean initialized = false;
	
	private ByteArrayOutputStream baos;
	private PrintStream pstream;
	private ScriptingContainer ruby;
	
	private synchronized void init(){
		baos = new ByteArrayOutputStream();
		pstream = new PrintStream(baos);
		ruby = new ScriptingContainer();
		ruby.setOutput(pstream);
		initialized = true;
	}
	
	private synchronized void close(){
		pstream.close();
		try {
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String runScript(String script) {
		synchronized (lock) {
			if(!initialized){
				init();
			}
		}
		byte[] bytes = null;
		ruby.runScriptlet(script);
		ruby.clear();
		bytes = baos.toByteArray();
		try {
			baos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		pstream.flush();
		baos.reset();
		return new String(bytes);
	}

}
