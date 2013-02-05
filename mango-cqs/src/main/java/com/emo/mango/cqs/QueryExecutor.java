package com.emo.mango.cqs;

import java.util.List;

public interface QueryExecutor<Q> {

	public SearchCriteria getSearchCriteria();
	
	public List<Q> query(SearchValue values);

	public List<Q> simpleQuery(Object... values);

	public List<Q> objectQuery(Object value);

	public List<Q> pagedQuery(int page, int elementsParPage, SearchValue values);
	
	public List<Q> simplePagedQuery(int page, int elementsParPage, Object... values);

	public List<Q> objectPagedQuery(int page, int elementsParPage, Object value);

	public int countPages(int elementsParPage, SearchValue values);

	public int simpleCountPages(int elementsParPage, Object... values);
	
	public int objectCountPages(int elementsParPage, Object value);

	public long countItems(SearchValue value);

	public long simpleCountItems(Object... value);

	public long objectCountItems(Object value);
}
