package com.emo.mango.cqs;

import java.util.List;

public interface QueryExecutor {
	
	public <View> List<View> query(Class<View> viewClass, Object... vars);

	public List<?> query(Object... vars);
	
	public <View> List<View> pagedQuery(Class<View> viewClass, int page, int elementsParPage, Object... vars);

	public List<?> pagedQuery(int page, int elementsParPage, Object... vars);
	
	public int countPages(int elementsParPage, Object... vars);

	public long countItems(Object... vars);
	
	public String getName();
	
	public Object[] orderNamedParams(final NamedParam... vars);
	
	public String[] getParamNames();

	public static class NamedParam {
		public final String name;
		public final Object var;
		
		public NamedParam(final String name, final Object var) {
			this.name = name;
			this.var = var;
		}
	}
}
