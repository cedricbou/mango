package com.emo.mango.cqs.internal;

import java.util.HashMap;
import java.util.Map;

import com.emo.mango.cqs.QueryExecutor;
import com.google.common.base.Preconditions;

public class QueryExecutorRepository {

	private final Map<String, QueryExecutor> contents = new HashMap<String, QueryExecutor>();
	
	public void store(final QueryExecutor qe) {
		Preconditions.checkArgument(!contents.containsKey(qe.getName()));
		contents.put(qe.getName(), qe);
	}
	
	public QueryExecutor get(final String name) {
		final QueryExecutor found = contents.get(name);
		Preconditions.checkNotNull(found, "no query executor found in repository for name : " + name);
		return found;
	}
}
