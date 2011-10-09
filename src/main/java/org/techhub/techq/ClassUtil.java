package org.techhub.techq;

import java.net.URI;
import java.net.URISyntaxException;

public class ClassUtil {

	private ClassUtil(){
	}
	
	public static URI toURI(String name) {
		try {
			return new URI(name);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}
