package org.techhub.techq.util;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * 
 * @author tango
 * 
 */
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
