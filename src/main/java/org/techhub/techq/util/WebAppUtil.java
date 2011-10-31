package org.techhub.techq.util;

/**
 * 
 * @author tango
 * 
 */
public class WebAppUtil {

	/** webapp */
	public static final String WEBAPP_DIR = "src/main/webapp/";
	
	/** Welcome Page */
	public static final String WELCOME_PAGE = "/TechQClient.html";
	
	private WebAppUtil(){
	}
	
	public static String getWebAppRoot(){
		String classPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		return classPath.replaceAll("target/[a-z¥-]*classes/", WEBAPP_DIR);
	}
}
