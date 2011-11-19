package org.techhub.techq.java;

import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

import org.techhub.techq.Evaluatable;
import org.techhub.techq.EvaluationContainer;
import org.techhub.techq.PrintWriterStream;

import com.sun.tools.javac.util.JCDiagnostic;

/**
 * 
 * @author tango
 * 
 */
public class JavaEvaluationContainer implements EvaluationContainer {

	private final JavaCompiler<Evaluatable> compiler = new JavaCompiler<Evaluatable>(
			Arrays.asList(new String[] { "-target", "1.6" }));
	
	@Override
	public String runScript(String script) {
		String result = "";
		
		String source = "import org.techhub.techq.Evaluatable; public class Main { public static void main() {" + script + " } }";
		
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
			try {
				StringWriter sw = new StringWriter();
				System.setOut(new PrintStream(new PrintWriterStream(sw)));
				Method method = compiledClass.getMethod("main");
				method.invoke(null, new Object[]{});
				sw.flush();
				result = sw.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				FileOutputStream fdOut = new FileOutputStream(FileDescriptor.out);
				System.setOut(new PrintStream(new BufferedOutputStream(fdOut, 128), true));
			}
		}
		return result;
	}

}

