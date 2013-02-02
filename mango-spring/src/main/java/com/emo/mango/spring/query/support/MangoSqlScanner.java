package com.emo.mango.spring.query.support;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.emo.mango.cqs.QueryExecutor;
import com.emo.mango.cqs.QueryItem;
import com.emo.mango.spring.cqs.support.MangoCQS;
import com.emo.mango.spring.query.annotations.QueryParams;
import com.emo.mango.spring.query.annotations.Sql;

public class MangoSqlScanner implements ApplicationContextAware,
		InitializingBean {
	private ApplicationContext applicationContext;

	@Inject
	private MangoCQS cqs;

	@Override
	public void afterPropertiesSet() throws Exception {
		scanForQueriesWithJpa();
	}

	private <O> void declareQuery(final Class<O> query, final QueryExecutor<O> executor) {
		cqs.dsl().execute(new QueryItem<O>(query)).with(executor);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void scanForQueriesWithJpa() {
		final Map<String, Object> myQueries = applicationContext
				.getBeansWithAnnotation(Sql.class);

		for (final String beanName : myQueries.keySet()) {
			final Sql annotation = applicationContext
					.findAnnotationOnBean(beanName, Sql.class);

			final Object myQuery = myQueries.get(beanName);

			if (myQuery instanceof QueryExecutor<?>) {
				final SqlExecutor executor = (SqlExecutor)myQuery;
				final QueryParams queryParam = executor.getClass().getAnnotation(QueryParams.class);
				executor.setData(annotation.sql(), ((queryParam != null)?queryParam.value():null), annotation.clazz());
				// TODO : a query should be declarable from the name as well, not only the class!
				declareQuery(annotation.clazz(), executor);
			}
			else {
				// TODO: add warning or error if CustomView not implementing ViewExecutor.
			}
		}
	}

	@Override
	public void setApplicationContext(
			final ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
