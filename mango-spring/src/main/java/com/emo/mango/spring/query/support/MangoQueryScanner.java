package com.emo.mango.spring.query.support;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.emo.mango.cqs.ObjectBasedSearchCriteria;
import com.emo.mango.cqs.PropertyBasedSearchCriteria;
import com.emo.mango.cqs.QueryExecutor;
import com.emo.mango.cqs.QueryItem;
import com.emo.mango.spring.cqs.support.MangoCQS;
import com.emo.mango.spring.query.annotations.QueryClass;
import com.emo.mango.spring.query.annotations.QueryParams;

abstract class MangoQueryScanner<A extends Annotation> implements ApplicationContextAware,
		InitializingBean {
	private ApplicationContext applicationContext;

	protected abstract MangoCQS cqs();
	
	private final Class<A> annotClass;
	
	protected MangoQueryScanner(final Class<A> annotClass) {
		this.annotClass = annotClass;
	}
	
	@Override
	public final void afterPropertiesSet() throws Exception {
		scanQueries();
	}

	private <O> void declareQuery(final QueryItem query, final QueryExecutor<O> executor) {
		cqs().dsl().execute(query).with(executor);
	}
		
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void scanQueries() {
		final Map<String, Object> myBeans = applicationContext
				.getBeansWithAnnotation(annotClass);

		for (final String beanName : myBeans.keySet()) {
			final A query = applicationContext
					.findAnnotationOnBean(beanName, annotClass);

			final Object myBean = myBeans.get(beanName);

			final QueryItem queryItem;

			if(getNameFromAnnot(query) != null && getNameFromAnnot(query).length() >= 1) {
				queryItem = new QueryItem(getClazzFromAnnot(query), getNameFromAnnot(query));
			}
			else {
				queryItem = new QueryItem(getClazzFromAnnot(query));
			}

			if (myBean instanceof QueryBasedExecutor<?>) {
				final QueryBasedExecutor executor = (QueryBasedExecutor)myBean;

				final QueryParams queryParam = executor.getClass().getAnnotation(QueryParams.class);
				final QueryClass queryClass = executor.getClass().getAnnotation(QueryClass.class);
				
				if(queryClass != null && queryParam != null) {
					throw new IllegalStateException("only one of @QueryParam or @QueryClass is allowed, you cannot use both annotations.");
				}
				
				if(queryParam != null) {
					executor.setData(getQueryFromAnnot(query), new PropertyBasedSearchCriteria(queryParam.value()), queryItem.clazz);
				}
				else if(queryClass != null) {
					executor.setData(getQueryFromAnnot(query), new ObjectBasedSearchCriteria(queryClass.value()), queryItem.clazz);
				}
				else {
					executor.setData(getQueryFromAnnot(query), new PropertyBasedSearchCriteria(), queryItem.clazz);
				}

				declareQuery(queryItem, executor);
			}
			else {
				// TODO: add warning or error if CustomView not implementing ViewExecutor.
			}
		}
	}

	protected abstract String getQueryFromAnnot(A query);
	
	protected abstract Class<?> getClazzFromAnnot(A query);

	protected abstract String getNameFromAnnot(A query);

	@Override
	public final void setApplicationContext(
			final ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
