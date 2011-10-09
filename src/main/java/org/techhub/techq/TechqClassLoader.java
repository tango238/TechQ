package org.techhub.techq;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.tools.JavaFileObject;

public class TechqClassLoader extends ClassLoader {
	
	private final Map<String, JavaFileObject> classes = new HashMap<String, JavaFileObject>();

	
	Collection<JavaFileObject> files() {
		return Collections.unmodifiableCollection(classes.values());
	}

	@Override
	protected Class<?> findClass(final String qualifiedClassName)
			throws ClassNotFoundException {
		JavaFileObject file = classes.get(qualifiedClassName);
		if (file != null) {
			byte[] bytes = ((JavaFile) file).getByteCode();
			return defineClass(qualifiedClassName, bytes, 0, bytes.length);
		}
		try {
			Class<?> c = Class.forName(qualifiedClassName);
			return c;
		} catch (ClassNotFoundException nf) {
			// Ignore
		}
		return super.findClass(qualifiedClassName);
	}

	
	void add(final String qualifiedClassName, final JavaFileObject javaFile) {
		classes.put(qualifiedClassName, javaFile);
	}

	@Override
	protected synchronized Class<?> loadClass(final String name,
			final boolean resolve) throws ClassNotFoundException {
		return super.loadClass(name, resolve);
	}

	@Override
	public InputStream getResourceAsStream(final String name) {
		if (name.endsWith(".class")) {
			String qualifiedClassName = name.substring(0,
					name.length() - ".class".length()).replace('/', '.');
			JavaFile file = (JavaFile) classes.get(qualifiedClassName);
			if (file != null) {
				return new ByteArrayInputStream(file.getByteCode());
			}
		}
		return super.getResourceAsStream(name);
	}
}
