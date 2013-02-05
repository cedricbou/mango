package com.emo.mango.spring.query.support;

import javax.inject.Inject;

import com.emo.mango.spring.cqs.support.MangoCQS;
import com.emo.mango.spring.query.annotations.Jpql;

public class MangoJpqlScanner extends MangoQueryScanner<Jpql> {
	@Inject
	private MangoCQS cqs;
		
	public MangoJpqlScanner() {
		super(Jpql.class);
	}
	
	@Override
	protected MangoCQS cqs() {
		return cqs;
	}
	
	@Override
	protected Class<?> getClazzFromAnnot(Jpql query) {
		return query.clazz();
	}
	
	@Override
	protected String getNameFromAnnot(Jpql query) {
		return query.name();
	}
	
	@Override
	protected String getQueryFromAnnot(Jpql query) {
		return query.jpql();
	}
}
