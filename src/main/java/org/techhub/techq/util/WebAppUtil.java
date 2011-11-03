package org.techhub.techq.util;

import java.io.File;


/**
 * 
 * @author tango
 * 
 */
public class WebAppUtil {

	public static final String CONFIG;
	
	static {
		CONFIG = new File("").getAbsolutePath() + "/webapp.properties";
	}
	
	private static PropertiesLoader loader = new PropertiesLoader(CONFIG);
	
	private WebAppUtil(){
	}
	
	public static boolean validate(){
		if(getWebAppRoot() == null || getDefaultDocument() == null){
			return false;
		}
		return true;
	}
	
	public static String getWebAppRoot(){
		return loader.get("document.root");
	}
	
	public static String getDefaultDocument(){
		return loader.get("default.document");
	}
}
