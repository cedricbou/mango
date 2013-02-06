package com.emo.mango.cqs;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsonschema.JsonSchema;

public class ObjectBasedSearchCriteria implements SearchCriteria {

	private final String[] criteria;
	
	public final Class<?> clazz;

	public ObjectBasedSearchCriteria(final Class<?> clazz) {
		this.clazz = clazz;
		
		final ObjectMapper mapper = new ObjectMapper();

		try {
			final JsonSchema schema = mapper.generateJsonSchema(clazz);
			final JsonNode properties = schema.getSchemaNode().get("properties");
			
			final List<String> fields = new LinkedList<String>();
			final Iterator<String> it = properties.fieldNames();
			while(it.hasNext()) {
				fields.add(it.next());
			}
			criteria = fields.toArray(new String[] {});
		} catch (JsonMappingException e) {
			throw new IllegalArgumentException(
					"failed to transform search criteria object to a list of criteria.",
					e);
		}
	}

	@Override
	public String[] getCriteria() {
		return criteria;
	}

}
