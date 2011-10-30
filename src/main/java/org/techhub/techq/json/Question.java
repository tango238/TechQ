package org.techhub.techq.json;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Question {

	public String languageName;
	
	public String inputString;
	
}
