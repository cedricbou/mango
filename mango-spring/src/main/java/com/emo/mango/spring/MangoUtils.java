package com.emo.mango.spring;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// FIXME : create a utils mango module ?
public class MangoUtils {

	public static String toJson(final Object object) {
		final ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			return object.getClass().getName() + "-" + System.identityHashCode(object) + "-[FAILED OBJECT TO RENDER AS JSON]";
		}
	}
}
