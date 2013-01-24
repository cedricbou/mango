package com.emo.mango.spring.jpa.support;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.emo.mango.cqs.QueryExecutor;

public abstract class QueryWithJpaExecutor<Q> implements QueryExecutor<Q> {

	private String jpql;
	
	private String[] params = new String[] {};
	
	protected QueryWithJpaExecutor() {
		
	}
	
	protected abstract EntityManager entityManager();
	
	public String getJpql() {
		return jpql;
	}

	public void setJpql(String jpql) {
		this.jpql = jpql;
	}
	
	public String[] getParams() {
		return params;
	}
	
	public void setParams(String[] params) {
		this.params = params;
	}

	@Override
	public List<Q> query(Object... values) {
		return pagedQuery(1, 200, values);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Q> pagedQuery(int page, int elementsParPage, Object... values) {
		if(params.length != values.length) {
			throw new IllegalArgumentException("number of values does not match number of declared params for this query");
		}
		
		if(page < 1) {
			throw new IllegalArgumentException("page numbering begins at 1, received page " + page);
		}
		
		final Query query = entityManager().createQuery(jpql);
		int i = 0;
		for(final String name : params) {
			if(jpql.contains("?")) {
				query.setParameter(i, values[i]);
			}
			else {
				query.setParameter(name, values[i]);
			}
			++i;
		}
		
		query.setFirstResult((page - 1) * elementsParPage);
		query.setMaxResults(elementsParPage);
		
		return query.getResultList();
	}

	@Override
	public int countPages(int elementsParPage, Object... values) {
		final long itemCount = countItems(values);
		return (int)(itemCount / elementsParPage) + (((itemCount % elementsParPage) > 0)?1:0);
	}

	@Override
	public long countItems(Object... values) {
		if(params.length != values.length) {
			throw new IllegalArgumentException("number of values does not match number of declared params for this query");
		}

		final int indexOfFrom = jpql.indexOf("from");
		
		if(indexOfFrom < 0) {
			throw new IllegalStateException("jpql query does not contain a from keywords, unable to transform query to count item : " + jpql);
		}
		
		final String countJpql = jpql.substring(indexOfFrom);
		
		final TypedQuery<Long> query = entityManager().createQuery("select new java.lang.Long(count(*)) " + countJpql, Long.class);
	
		return query.getSingleResult();
	}
}
