package org.techhub.techq.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileUtil {

	public static String read(String path) {
		// read file
		FileReader fileReader = null;
		BufferedReader reader = null;
		StringBuilder content = new StringBuilder();
		try {
			fileReader = new FileReader(path);
			reader = new BufferedReader(fileReader);

			String read;
			while ((read = reader.readLine()) != null) {
				content.append(read).append("\n");
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return content.toString();
	}
}
