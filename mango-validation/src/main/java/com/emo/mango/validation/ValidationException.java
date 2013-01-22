package com.emo.mango.validation;


public class ValidationException extends RuntimeException {

	private static final long serialVersionUID = 6305838577703178784L;

	public ValidationException() {
	}

	public ValidationException(String[] violations) {
		super(join(violations));
	}
	
	private static String join(String[] messages) {
		String message = "";
		
		if(messages.length > 0) {
			message += messages[0];
		}
		
		for(final String item : messages) {
			message += "; " + item; 
		}

		return message;
	}
}
