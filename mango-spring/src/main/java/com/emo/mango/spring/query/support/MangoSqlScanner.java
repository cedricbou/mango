package com.emo.mango.spring.query.support;

import javax.inject.Inject;

import com.emo.mango.spring.cqs.support.MangoCQS;
import com.emo.mango.spring.query.annotations.Sql;

public class MangoSqlScanner extends MangoQueryScanner<Sql> {

	@Inject
	private MangoCQS cqs;

	@Override
	protected MangoCQS cqs() {
		return cqs;
	}
	
	public MangoSqlScanner() {
		super(Sql.class);
	}

	@Override
	protected Class<?> getClazzFromAnnot(Sql query) {
		return query.clazz();
	}
	
	@Override
	protected String getNameFromAnnot(Sql query) {
		return query.name();
	}
	
	@Override
	protected String getQueryFromAnnot(Sql query) {
		return query.sql();
	}
}
