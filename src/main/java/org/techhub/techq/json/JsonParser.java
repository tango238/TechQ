package org.techhub.techq.json;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * 
 * @author tango
 * 
 */
public class JsonParser {
	
	public JsonParser() {
	}

	public Question parse(String json){
		ObjectMapper mapper = new ObjectMapper();
		Question q = null;
		try {
			q = mapper.readValue(json, Question.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return q;
	}
}
