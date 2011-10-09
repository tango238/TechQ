package org.techhub.techq;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.tools.SimpleJavaFileObject;

public class JavaFile extends SimpleJavaFileObject {
	
	private ByteArrayOutputStream byteCode;
	
	private final CharSequence source;

	JavaFile(final String baseName, final CharSequence source) {
		super(ClassUtil.toURI(baseName + TechqCompiler.JAVA_EXTENSION), Kind.SOURCE);
		this.source = source;
	}

	JavaFile(final String name, final Kind kind) {
		super(ClassUtil.toURI(name), kind);
		source = null;
	}

	@Override
	public CharSequence getCharContent(final boolean ignoreEncodingErrors)
			throws UnsupportedOperationException {
		
		if (source == null) {
			throw new UnsupportedOperationException("getCharContent()");
		}
		return source;
	}

	@Override
	public InputStream openInputStream() {
		return new ByteArrayInputStream(getByteCode());
	}

	@Override
	public OutputStream openOutputStream() {
		byteCode = new ByteArrayOutputStream();
		return byteCode;
	}
	
	byte[] getByteCode() {
		return byteCode.toByteArray();
	}
}
