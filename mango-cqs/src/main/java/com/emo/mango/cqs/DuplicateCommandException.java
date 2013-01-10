package com.emo.mango.cqs;

public class DuplicateCommandException extends Exception {

	private static final long serialVersionUID = 1L;

	public DuplicateCommandException() {
		super();
	}

	public DuplicateCommandException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public DuplicateCommandException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public DuplicateCommandException(String arg0) {
		super(arg0);
	}

	public DuplicateCommandException(Throwable arg0) {
		super(arg0);
	}

	
}
