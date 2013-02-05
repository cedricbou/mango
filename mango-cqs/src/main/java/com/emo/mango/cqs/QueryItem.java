package com.emo.mango.cqs;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class QueryItem {

	public final String name;
	public final String namespace;
	public final Class<?> clazz;

	public QueryItem(Class<?> clazz) {
		this(clazz, clazz.getPackage().getName(), clazz.getSimpleName());
	}

	public QueryItem(Class<?> clazz, String name) {
		this(clazz, clazz.getPackage().getName(), name);
	}

	public QueryItem(Class<?> clazz, String namespace, String name) {
		this.name = name;
		this.namespace = namespace;
		this.clazz = clazz;
	}

	private final ObjectMapper mapper = new ObjectMapper();

	private String[] columns;

	public String[] columns() {
		if (columns == null) {
			JsonNode n;
			try {
				n = mapper.generateJsonSchema(clazz).getSchemaNode()
						.get("properties");
			} catch (JsonMappingException e) {
				throw new RuntimeException(
						"failed to jsonify the query item class to produce columns. "
								+ this);
			}

			final List<String> fields = new LinkedList<String>();
			final Iterator<String> it = n.fieldNames();

			while (it.hasNext()) {
				fields.add(it.next());
			}

			columns = fields.toArray(new String[] {});
		}

		return columns;
	}

	public String[] valuesAsStringFor(Object item) {
		final JsonNode node = mapper.valueToTree(item);
		final String[] values = new String[columns().length];

		int i = 0;
		for (final String field : columns()) {
			values[i++] = ((node.get(field) != null) ? (node.get(field).asText().replaceFirst("^\"(.*)\"$","$1"))
					: "null");
		}

		return values;
	}

	@Override
	public int hashCode() {
		return (this.name.toLowerCase()).hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof QueryItem) {
			final QueryItem c = (QueryItem) o;
			return this.name.equalsIgnoreCase(c.name);
		}
		return false;
	}

	@Override
	public String toString() {
		return "{" + namespace + "}" + name;
	}
}
