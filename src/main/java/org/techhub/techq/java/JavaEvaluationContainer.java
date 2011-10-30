package org.techhub.techq.java;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Locale;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

import org.techhub.techq.Evaluatable;
import org.techhub.techq.EvaluationContainer;

import com.sun.tools.javac.util.JCDiagnostic;

public class JavaEvaluationContainer implements EvaluationContainer {

	private final JavaCompiler<Evaluatable> compiler = new JavaCompiler<Evaluatable>(
			Arrays.asList(new String[] { "-target", "1.6" }));
	
	@Override
	public String runScript(String script) {
		String result = null;
		
		String source = "import org.techhub.techq.Evaluatable; public class Main implements Evaluatable { @Override public String eval() { return \""
				+ script + "\"; } }";
		
		final DiagnosticCollector<JavaFileObject> errors = new DiagnosticCollector<JavaFileObject>();
		Class<Evaluatable> compiledClass = null;
		try {
			compiledClass = compiler.compile("Main", source, errors,
					new Class<?>[] { Evaluatable.class });
		} catch (JavaCompilerException ex) {
			DiagnosticCollector<JavaFileObject> diagnostics = ex.getDiagnostics();
			for(Object diagnostic : diagnostics.getDiagnostics()){
				JCDiagnostic jcDiagnostic = (JCDiagnostic) diagnostic;
				result = jcDiagnostic.getMessage(Locale.JAPANESE);
			}
		}

		if (compiledClass != null) {
			Evaluatable evaluator;
			try {
				evaluator = compiledClass.newInstance();
				result = evaluator.eval();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

}
