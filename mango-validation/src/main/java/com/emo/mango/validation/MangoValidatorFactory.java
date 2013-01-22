package com.emo.mango.validation;

public class MangoValidatorFactory {
	public final static MangoValidator NONE = new NoValidation();
	public final static MangoValidator OVAL = new OvalValidator();

	private static enum ValidatorType {
		NONE(MangoValidatorFactory.NONE), OVAL(MangoValidatorFactory.OVAL);
		
		private MangoValidator validator;
		
		private ValidatorType(final MangoValidator val) {
			this.validator = val;
		}
		
		public MangoValidator validator() {
			return validator;
		}
	}
	
	private final ValidatorType type;
	
	public MangoValidatorFactory() {
		this.type = ValidatorType.NONE;
	}
	
	public MangoValidatorFactory(final String validator) {
		this.type = ValidatorType.valueOf(validator);
	}
	
	public MangoValidator validator() {
		return type.validator();
	}
}
