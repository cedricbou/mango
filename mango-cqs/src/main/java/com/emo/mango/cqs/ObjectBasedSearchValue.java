package com.emo.mango.cqs;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.sf.oval.internal.util.StringUtils;

import com.emo.mango.utils.ObjectReflectGetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectBasedSearchValue implements SearchValue {

	private final JsonNode node;

	private final String[] criteria;
	
	private final Object[] values;
	
	private final ObjectReflectGetter getter;

	public ObjectBasedSearchValue(final Object o) {
		getter = new ObjectReflectGetter(o);
			
		final ObjectMapper mapper = new ObjectMapper();
		this.node = mapper.valueToTree(o);

		final List<String> fields = new LinkedList<String>();
		final Iterator<String> it = this.node.fieldNames();

		while (it.hasNext()) {
			fields.add(it.next());
		}

		criteria = fields.toArray(new String[] {});
		
		this.values = new Object[criteria.length];
		
		int i = 0;
		
		for(final String criterion : criteria) {
			this.values[i++] = getter.get(criterion);
		}
	}

	@Override
	public String[] getCriteria() {
		return criteria;
	}

	@Override
	public Object[] getValues() {
		return values;
	}

	@Override
	public Object get(String criteria) {
		return getter.get(criteria);
	}

	@Override
	public void assertMatch(SearchCriteria criteria) {
		final String[] columns = criteria.getCriteria();
		
		if(columns.length != this.criteria.length) {
			throw new IllegalArgumentException("number of values criteria does not match declared criteria");
		}
		
		for(int i = 0; i < columns.length; ++i) {
			if(!columns[i].equals(this.criteria[i])) {
				throw new IllegalArgumentException("Expected criteria '" + columns[i] + "' but got '" + this.criteria[i] + "'.");
			}
		}
	}
	
	@Override
	public String toString() {
		return StringUtils.implode(criteria, ";") + " // " + StringUtils.implode(values, ";");
	}
}
