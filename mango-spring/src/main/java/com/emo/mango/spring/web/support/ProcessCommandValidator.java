package com.emo.mango.spring.web.support;

import com.emo.mango.validation.MangoValidator;
import com.emo.mango.validation.MangoValidatorFactory;
import com.emo.mango.validation.ValidationException;

public class ProcessCommandValidator {

	private MangoValidator validator = MangoValidatorFactory.NONE;
	
	public void validate(final Object obj) throws ValidationException {
		validator.validate(obj);
	}

	public MangoValidator getValidator() {
		return validator;
	}

	public void setValidator(MangoValidator validator) {
		this.validator = validator;
	}

}
