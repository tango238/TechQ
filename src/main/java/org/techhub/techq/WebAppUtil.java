package org.techhub.techq;

public class WebAppUtil {

	/** webapp */
	public static final String WEBAPP_DIR = "src/main/webapp/";
	
	private WebAppUtil(){
	}
	
	public static String getWebAppRoot(){
		String classPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		return classPath.replaceAll("target/[a-z¥-]*classes/", WEBAPP_DIR);
	}
}
