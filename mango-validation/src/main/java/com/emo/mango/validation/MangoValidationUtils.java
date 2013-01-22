package com.emo.mango.validation;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.exception.ConstraintsViolatedException;


public class MangoValidationUtils {

	
	public void assertValidated(final Object command) {
		// TODO: proper logging.
		final Validator validator = new Validator();
		final List<ConstraintViolation> violations = validator.validate(command);
		if(violations != null && violations.size() > 0) {
			final ObjectMapper mapper = new ObjectMapper();
			try {
				System.out.println("[ERROR] Constraints Violation for : " + mapper.writeValueAsString(command));
			}
			catch(Exception e) {
				System.err.println("[ERROR] Constraints Violation : unable to write object as json");
				e.printStackTrace(System.err);
			}
			for(final ConstraintViolation violation : violations) {
				System.out.println(violation.toString());
			}
			throw new ConstraintsViolatedException(violations);
		}
	}

}
