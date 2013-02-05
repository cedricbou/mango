package com.emo.mango.cqs;

public class PropertyBasedSearchCriteria implements SearchCriteria {

	private final String[] names;
	
	public String[] getCriteria() {
		return names;
	}
	
	public PropertyBasedSearchCriteria(final String... criteriaNames) {
		this.names = criteriaNames;
	}
}
