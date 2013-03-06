package com.emo.mango.json.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterators;

public class JsonUtils {

	// TODO: check, thread safe ?
	final static ObjectMapper mapper = new ObjectMapper();

	public static String[] getRootFieldNames(final Object object) {
		final JsonNode node = mapper.valueToTree(object);
		return Iterators.toArray(node.fieldNames(), String.class);
	}

	public static String[] getRootValuesAsString(final Object object) {
		final JsonNode node = mapper.valueToTree(object);

		final String[] fields = Iterators.toArray(node.fieldNames(),
				String.class);
		final String[] values = new String[fields.length];

		int i = 0;
		for (final String field : fields) {
			final JsonNode valueAsNode = node.get(field);
			values[i++] = (valueAsNode != null) ? (node.get(field).asText()
					.replaceFirst("^\"(.*)\"$", "$1")) : "n/a";
		}

		return values;
	}
}
