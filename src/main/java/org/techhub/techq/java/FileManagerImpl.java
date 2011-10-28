package org.techhub.techq.java;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardLocation;

import org.techhub.techq.ClassUtil;

public class FileManagerImpl extends ForwardingJavaFileManager<JavaFileManager> {
	
	private final JavaClassLoader classLoader;

	private final Map<URI, JavaFileObject> fileObjects = new HashMap<URI, JavaFileObject>();

	public FileManagerImpl(JavaFileManager fileManager, JavaClassLoader classLoader) {
		super(fileManager);
		this.classLoader = classLoader;
	}
	
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	@Override
	public FileObject getFileForInput(Location location, String packageName,
			String relativeName) throws IOException {
		
		FileObject o = fileObjects.get(uri(location, packageName, relativeName));
		if (o != null) {
			return o;
		}
		return super.getFileForInput(location, packageName, relativeName);
	}

	public void putFileForInput(StandardLocation location, String packageName,
			String relativeName, JavaFileObject file) {
		fileObjects.put(uri(location, packageName, relativeName), file);
	}

	private URI uri(Location location, String packageName, String relativeName) {
		return ClassUtil.toURI(location.getName() + '/' + packageName + '/' + relativeName);
	}

	@Override
	public JavaFileObject getJavaFileForOutput(Location location,
			String qualifiedName, Kind kind, FileObject outputFile) throws IOException {
		JavaFileObject file = new JavaFile(qualifiedName, kind);
		// Classloaderに追加する
		classLoader.add(qualifiedName, file);
		return file;
	}

	@Override
	public ClassLoader getClassLoader(JavaFileManager.Location location) {
		return classLoader;
	}

	@Override
	public String inferBinaryName(Location loc, JavaFileObject file) {
		return (file instanceof JavaFile) ? file.getName() : super.inferBinaryName(loc, file);
	}

	@Override
	public Iterable<JavaFileObject> list(Location location, String packageName,
			Set<Kind> kinds, boolean recurse) throws IOException {
		
		Iterable<JavaFileObject> result = super.list(location, packageName, kinds, recurse);
		
		List<JavaFileObject> files = new ArrayList<JavaFileObject>();
		if (location == StandardLocation.CLASS_PATH && kinds.contains(JavaFileObject.Kind.CLASS)) {
			// 指定されたLocationがClassPathの場合
			for (JavaFileObject file : fileObjects.values()) {
				if (file.getKind() == Kind.CLASS && file.getName().startsWith(packageName)){
					files.add(file);
				}
			}
			files.addAll(classLoader.files());
		} else if (location == StandardLocation.SOURCE_PATH && kinds.contains(JavaFileObject.Kind.SOURCE)) {
			// 指定されたLocationがSourcePathの場合
			for (JavaFileObject file : fileObjects.values()) {
				if (file.getKind() == Kind.SOURCE && file.getName().startsWith(packageName))
					files.add(file);
			}
		}
		for (JavaFileObject file : result) {
			files.add(file);
		}
		return files;
	}
}
