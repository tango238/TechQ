package org.techhub.techq.util;

import java.io.File;
import java.io.FileReader;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * 
 * @author tango
 * 
 */
public class PropertiesLoader {

	private ResourceBundle prop;

	public PropertiesLoader(String path) {
		try {
			File file = new File(path);
			FileReader reader = new FileReader(file);
			prop = new PropertyResourceBundle(reader);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String get(String key) {
		return prop.getString(key);
	}

}
