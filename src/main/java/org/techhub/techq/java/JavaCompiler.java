package org.techhub.techq.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

/**
 * 
 * @author tango
 * 
 */
public class JavaCompiler<T> {

	public static final String JAVA_EXTENSION = ".java";

	private final javax.tools.JavaCompiler compiler;
	
	private final List<String> options;
	
	private JavaClassLoader classLoader;

	private FileManagerImpl javaFileManager;

	private DiagnosticCollector<JavaFileObject> diagnostics;


	public JavaCompiler(Iterable<String> options) {
		compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null) {
			throw new IllegalStateException("Cannot find the Java compiler.");
		}
		diagnostics = new DiagnosticCollector<JavaFileObject>();
		
		
		this.options = new ArrayList<String>();
		if (options != null) {
			for (String option : options) {
				this.options.add(option);
			}
		}
	}

	public synchronized Class<T> compile(final String qualifiedClassName,
			final CharSequence javaSource,
			final DiagnosticCollector<JavaFileObject> diagnosticsList,
			final Class<?>... types) throws JavaCompilerException,
			ClassCastException {
		
		if (diagnosticsList != null) {
			diagnostics = diagnosticsList;
		} else {
			diagnostics = new DiagnosticCollector<JavaFileObject>();
		}
		JavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
		classLoader = new JavaClassLoader();
		javaFileManager = new FileManagerImpl(fileManager, classLoader);
		
		return compile(qualifiedClassName, javaSource, diagnosticsList);
	}

	public synchronized Class<T> compile(
			final String qualifiedClassName, final CharSequence javaSource,
			final DiagnosticCollector<JavaFileObject> diagnosticsList)
			throws JavaCompilerException {

		if (javaSource != null) {
			String packageName = "";
			String className = qualifiedClassName;
			int idx = qualifiedClassName.lastIndexOf('.');
			if(idx >= 0){
				packageName = qualifiedClassName.substring(0, idx);
				className = qualifiedClassName.substring(idx + 1);
			}
			final JavaFile javaFile = new JavaFile(className, javaSource);
			javaFileManager.putFileForInput(StandardLocation.SOURCE_PATH,
					packageName, className + JAVA_EXTENSION, javaFile);
		}
		final JavaFile source = new JavaFile(qualifiedClassName, javaSource);
		final CompilationTask task = compiler.getTask(null, javaFileManager,
				diagnostics, options, null, Arrays.asList(new JavaFileObject[]{ source }));
		final Boolean result = task.call();
		if (result == null || !result.booleanValue()) {
			throw new JavaCompilerException("Compilation failed.", qualifiedClassName, diagnostics);
		}
		try {
			return loadClass(qualifiedClassName);
		} catch (ClassNotFoundException e) {
			 throw new JavaCompilerException(qualifiedClassName, e, diagnostics);
		} catch (IllegalArgumentException e) {
			throw new JavaCompilerException(qualifiedClassName, e, diagnostics);
		} catch (SecurityException e) {
			throw new JavaCompilerException(qualifiedClassName, e, diagnostics);
		}
	}

	@SuppressWarnings("unchecked")
	public Class<T> loadClass(final String qualifiedClassName)
			throws ClassNotFoundException {
		return (Class<T>) classLoader.loadClass(qualifiedClassName);
	}

	public ClassLoader getClassLoader() {
		return javaFileManager.getClassLoader();
	}
}
