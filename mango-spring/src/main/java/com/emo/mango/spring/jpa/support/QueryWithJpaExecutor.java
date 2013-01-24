package com.emo.mango.spring.jpa.support;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

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

	@SuppressWarnings("unchecked")
	public List<Q> query(Object... values) {
		if(params.length != values.length) {
			throw new IllegalArgumentException("number of values does not match number of declared params for this query");
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
		
		return query.getResultList();
	}
}
