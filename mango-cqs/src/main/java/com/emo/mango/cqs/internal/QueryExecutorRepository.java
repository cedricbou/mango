package com.emo.mango.cqs.internal;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.emo.mango.cqs.DuplicateException;
import com.emo.mango.cqs.QueryExecutor;
import com.emo.mango.cqs.QueryItem;

public class QueryExecutorRepository {
	
	private final Map<QueryItem<?>, QueryExecutor<?>> executors = new HashMap<QueryItem<?>, QueryExecutor<?>>();
	private final List<QueryItem<?>> queries = new LinkedList<QueryItem<?>>();
	private final Map<String, List<QueryItem<?>>> queriesByName = new HashMap<String, List<QueryItem<?>>>();
	
	public <O> void addExecutor(final QueryItem<O> query, final QueryExecutor<O> executor) {
		if(null == query) {
			throw new IllegalArgumentException("unexpected null query item");
		}

		if(null == executor) {
			throw new IllegalArgumentException("unexpected null query executor");
		}
		
		executors.put(query, executor);
		storeQuery(query);
	}
	
	private <O> void storeQuery(final QueryItem<O> queryItem) {
		queries.add(queryItem);
		
		final String name = queryItem.name.toLowerCase();
		
		if(!queriesByName.containsKey(name)) {
			queriesByName.put(name, new LinkedList<QueryItem<?>>());
		}
		
		queriesByName.get(name).add(queryItem);
	}
	
	/*
	public Command<?>[] handledCommands() {
		return commands.toArray(new Command<?>[] {});
	}
	
	public Command<?> handledCommandByName(final String name) throws DuplicateCommandException {
		if(!commandByName.containsKey(name.toLowerCase()) || commandByName.get(name.toLowerCase()).size() == 0) {
			throw new IllegalArgumentException("no command with this name " + name);
		}
		
		if(commandByName.get(name.toLowerCase()).size() > 1) {
			throw new DuplicateCommandException("several commands with the same name exists, expected one. (" + name + ")");
		}
		
		return commandByName.get(name.toLowerCase()).get(0);
	}
	*/
	
	public <O> QueryExecutor<O> executorFor(final QueryItem<O> query) {
		if(null == query) {
			throw new IllegalArgumentException("unexpected null query item");
		}
		
		final QueryExecutor<?> executor = executors.get(query);
		
		if(null == executor) {
			throw new IllegalArgumentException("no executor found for query item " + query);
		}
		
		return castExecutorWith(query, executor);
	}
	
	private <O> QueryExecutor<O> castExecutorWith(final QueryItem<O> query, final QueryExecutor<?> executor) {
		try {
			@SuppressWarnings("unchecked")
			final QueryExecutor<O> mappedToExecutor = (QueryExecutor<O>)executor;
			return mappedToExecutor;
		}
		catch(Exception e) {
			throw new IllegalStateException("asked executor does not match type for query item " + query);
		}
	}

	public QueryItem<?> queryByName(String name) throws DuplicateException {
		if(!queriesByName.containsKey(name.toLowerCase()) || queriesByName.get(name.toLowerCase()).size() == 0) {
			throw new IllegalArgumentException("no command with this name " + name);
		}
		
		if(queriesByName.get(name.toLowerCase()).size() > 1) {
			throw new DuplicateException("several commands with the same name exists, expected one. (" + name + ")");
		}
		
		return queriesByName.get(name.toLowerCase()).get(0);
	}

}
