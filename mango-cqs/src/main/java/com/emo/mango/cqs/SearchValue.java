package com.emo.mango.cqs;

public interface SearchValue {

	public Object[] getValues();
	
	public String[] getCriteria();
	
	public Object get(String criteria);
	
	public void assertMatch(final SearchCriteria criteria);
}
