package org.techhub.techq.compile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;


public class TechqCompiler<T> {

	public static final String JAVA_EXTENSION = ".java";

	private final JavaCompiler compiler;
	
	private final List<String> options;
	
	private TechqClassLoader classLoader;

	private FileManagerImpl javaFileManager;

	private DiagnosticCollector<JavaFileObject> diagnostics;


	public TechqCompiler(Iterable<String> options) {
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
			final Class<?>... types) throws TechqCompilerException,
			ClassCastException {
		
		if (diagnosticsList != null) {
			diagnostics = diagnosticsList;
		} else {
			diagnostics = new DiagnosticCollector<JavaFileObject>();
		}
		JavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
		classLoader = new TechqClassLoader();
		javaFileManager = new FileManagerImpl(fileManager, classLoader);
		
		return compile(qualifiedClassName, javaSource, diagnosticsList);
	}

	public synchronized Class<T> compile(
			final String qualifiedClassName, final CharSequence javaSource,
			final DiagnosticCollector<JavaFileObject> diagnosticsList)
			throws TechqCompilerException {

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
			throw new TechqCompilerException("Compilation failed.", qualifiedClassName, diagnostics);
		}
		try {
			return loadClass(qualifiedClassName);
		} catch (ClassNotFoundException e) {
			 throw new TechqCompilerException(qualifiedClassName, e, diagnostics);
		} catch (IllegalArgumentException e) {
			throw new TechqCompilerException(qualifiedClassName, e, diagnostics);
		} catch (SecurityException e) {
			throw new TechqCompilerException(qualifiedClassName, e, diagnostics);
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
