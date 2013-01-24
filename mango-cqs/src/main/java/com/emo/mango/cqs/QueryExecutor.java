package com.emo.mango.cqs;

import java.util.List;

public interface QueryExecutor<Q> {

	public String[] getParams();
	
	public List<Q> query(Object... params);
	
	public List<Q> pagedQuery(int page, int elementsParPage, Object... params);
	
	public int countPages(int elementsParPage, Object... params);
	
	public long countItems(Object... params);
}
