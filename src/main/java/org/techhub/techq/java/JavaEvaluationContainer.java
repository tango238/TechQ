package org.techhub.techq.java;

import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

import org.techhub.techq.Evaluatable;
import org.techhub.techq.EvaluationContainer;

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

class PrintWriterStream extends OutputStream {
	
	private final Writer writer;
	
	public PrintWriterStream(Writer writer) {  
        this.writer = writer;  
    }  
   
    public void write(int b) throws IOException {  
        write(new byte[] {(byte) b}, 0, 1);  
    }  
   
    public void write(byte b[], int off, int len) throws IOException {  
        writer.write(new String(b, off, len));  
    }  
   
    public void flush() throws IOException {  
        writer.flush();  
    }  
   
    public void close() throws IOException {  
        writer.close();  
    }  
}
