package com.emo.mango.cqs;

import com.emo.mango.cqs.internal.HandlerRepository;
import com.emo.mango.cqs.internal.InternalCommandBus;
import com.emo.mango.cqs.internal.QueryExecutorRepository;

public class CQSSystem {
	
	private final HandlerRepository handlerRepository = new HandlerRepository();
	
	private final QueryExecutorRepository executorRepository = new QueryExecutorRepository();
	
	private final CommandBus bus = new InternalCommandBus(handlerRepository);
		
	public <O> void declareHandler(final Command<O> cmd, final Handler<O> handler) {
		handlerRepository.addHandler(cmd, handler);
	}
	
	public <O> void declareQueryExecutor(final QueryItem query, final QueryExecutor<O> executor) {
		executorRepository.addExecutor(query, executor);
	}
	
	public CommandBus bus() {
		return bus;
	}
		
	public Command<?>[] commands() {
		return handlerRepository.handledCommands();
	}
	
	public Command<?> command(final String name) throws DuplicateException {
		return handlerRepository.handledCommandByName(name);
	}

	public QueryItem query(final String name) throws DuplicateException {
		return executorRepository.queryByName(name);
	}

	public QueryExecutor<?> queryExecutor(final String queryName) throws DuplicateException {
		return queryExecutor(query(queryName));
	}
	
	public QueryExecutor<?> queryExecutor(final QueryItem query) throws DuplicateException {
		return executorRepository.executorFor(query);
	}
}
