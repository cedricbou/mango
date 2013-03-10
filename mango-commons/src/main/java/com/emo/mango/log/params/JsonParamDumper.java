package com.emo.mango.log.params;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;

public class JsonParamDumper implements ParamDumper {

	final ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public String dump(Object... args) {
		final String[] dumps = new String[args.length];
		
		int i = 0;
		for(final Object arg : args) {
			if(arg != null) {
				try {
					dumps[i] = mapper.writeValueAsString(arg);
				} catch (JsonProcessingException e) {
					dumps[i] = "failed: " + e.getMessage();
				}
				++i;
			}
			else {
				dumps[i] = "null";
			}
		}
		
		return Joiner.on(" ").join(dumps);
	}
}
