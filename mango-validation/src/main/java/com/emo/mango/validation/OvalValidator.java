package com.emo.mango.validation;

import java.util.LinkedList;
import java.util.List;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;

public class OvalValidator implements MangoValidator {

	private static final String[] EMPTY_STRING_ARRAY = new String[] {};
	
	@Override
	public void validate(final Object obj) throws ValidationException {
		// FIXME : check if validator is thread safe and can be a class member.
		final Validator validator = new Validator();
		final List<ConstraintViolation> violations = validator.validate(obj);
				
		if(violations != null && violations.size() > 0) {
			final List<String> humanReadableViolations = new LinkedList<String>();

			for(final ConstraintViolation violation : violations) {
				humanReadableViolations.add(violation.toString());
			}
			
			throw new ValidationException(humanReadableViolations.toArray(EMPTY_STRING_ARRAY));
		}
	}

}
