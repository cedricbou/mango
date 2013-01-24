package com.emo.mango.cqs.dsl;

import com.emo.mango.cqs.CQSSystem;
import com.emo.mango.cqs.QueryExecutor;
import com.emo.mango.cqs.QueryItem;

public class ExecuteQueryDsl<O> {

	private final CQSSystem system;
	private final QueryItem<O> query;
	
	protected ExecuteQueryDsl(final CQSSystem system, final QueryItem<O> query) {
		this.system = system;
		this.query = query;
	}
	
	public void with(final QueryExecutor<O> executor) {
		system.declareQueryExecutor(query, executor);
	}
}
