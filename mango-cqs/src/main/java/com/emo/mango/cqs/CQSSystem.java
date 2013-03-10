package com.emo.mango.cqs;

import java.util.List;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import com.emo.mango.config.MangoConfig;
import com.emo.mango.config.MangoConfigs;
import com.emo.mango.cqs.internal.HandlerRepository;
import com.emo.mango.cqs.internal.InternalCommandBus;
import com.emo.mango.cqs.internal.QueryExecutorFactory;
import com.emo.mango.cqs.internal.QueryExecutorRepository;

public class CQSSystem {
	
	private final HandlerRepository handlerRepository = new HandlerRepository();
	
	private final QueryExecutorFactory queryExecutorFactory = new QueryExecutorFactory();
	
	private final QueryExecutorRepository queryExecutorRepository = new QueryExecutorRepository();
	
	private final CommandBus bus;

	public CQSSystem() {
		this(MangoConfigs.singleton().get());
	}
	
	public CQSSystem(final MangoConfig config) {
		this.bus = new InternalCommandBus(handlerRepository, config);
	}
		
	public void declareQueries(final Class<? extends Queries> queriesClass, final DataSource ds, final EntityManager em) {
		final List<QueryExecutor> qes = queryExecutorFactory.executorsFor(queriesClass, ds, em);
		
		for(final QueryExecutor qe : qes) {
			queryExecutorRepository.store(qe);
		}
	}

	public void declareQueries(final Class<? extends Queries> queriesClass, final EntityManager em) {
		declareQueries(queriesClass, null, em);
	}

	public void declareQueries(final Class<? extends Queries> queriesClass) {
		declareQueries(queriesClass, null, null);
	}

	public <I extends Queries> I open(Class<I> queriesClass,
			DataSource dataSource,
			EntityManager entityManager) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public <O> void declareHandler(final Command<O> cmd, final Handler<O> handler) {
		handlerRepository.addHandler(cmd, handler);
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

	public QueryExecutor getQueryExecutor(final String queryName) {
		return queryExecutorRepository.get(queryName);
	}

}
