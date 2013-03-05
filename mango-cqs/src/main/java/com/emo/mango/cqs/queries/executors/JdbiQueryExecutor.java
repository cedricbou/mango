package com.emo.mango.cqs.queries.executors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.sqlobject.Bind;

import com.emo.mango.cqs.Queries;
import com.emo.mango.cqs.QueryExecutor;
import com.google.common.base.Preconditions;

public class JdbiQueryExecutor implements QueryExecutor {

	private final DBI dbi;
	
	private final Queries queries;
	
	private final Method countMethod;
	private final Method queryMethod;
	private final Method pagedMethod;
	
	private final Class<? extends Queries> clazz;
	
	private final String name;
	
	private final String[] boundNames;
	
	public JdbiQueryExecutor(String name, Class<? extends Queries> queriesClass,
			Method countMethod, Method queryMethod, Method pagedMethod, DataSource ds) {
		this.dbi = new DBI(ds);
		// this.dbi.setSQLLog(new PrintStreamLog(System.out));
		this.queries = this.dbi.onDemand(queriesClass);
		this.queryMethod = queryMethod;
		this.pagedMethod = pagedMethod;
		this.countMethod = countMethod;
		
		this.boundNames = boundNames(queryMethod);
		
		this.clazz = queriesClass;
		this.name = name;
	}
	
	private final String[] boundNames(final Method method) {
		final List<String> bounds = new LinkedList<String>();
		
		for(Annotation[] annots : method.getParameterAnnotations()) {
			for(Annotation annot : annots) {
				if(Bind.class.isAssignableFrom(annot.annotationType())) {
					bounds.add(((Bind)annot).value());
				}
			}
		}
		
		return bounds.toArray(new String[] {});
	}
	
	@Override
	public <View> List<View> pagedQuery(Class<View> viewClass, int page,
			int elementsParPage, Object... vars) {
		Preconditions.checkNotNull(pagedMethod, "unable to execute paged query, no jdbi paged method defined for interface " + clazz);
		
		PageValidator.assertValidPaging(page, elementsParPage);
		
		final Object[] withLimitOffset = new Object[vars.length + 2];
		System.arraycopy(vars, 0, withLimitOffset, 0, vars.length);
		
		withLimitOffset[vars.length] = PageValidator.offset(page, elementsParPage);
		withLimitOffset[vars.length + 1] = PageValidator.limit(page, elementsParPage);
		
		return queryWithMethod(viewClass, pagedMethod, withLimitOffset);
	}
	
	@Override
	public List<?> pagedQuery(int page,
			int elementsParPage, Object... vars) {
		return pagedQuery(Object.class, page, elementsParPage, vars);
	}
	
	@Override
	public <View> List<View> query(Class<View> viewClass, Object... vars) {
		Preconditions.checkNotNull(queryMethod, "unable to execute unpaged query, no jdbi no pagination method defined for interface " + clazz);

		return queryWithMethod(viewClass, queryMethod, vars);
	}
	
	@Override
	public List<?> query(Object... vars) {
		return query(Object.class, vars);
	}
	
	@Override
	public long countItems(Object... vars) {
		Preconditions.checkNotNull(countMethod, "unable to execute count query, no jdbi count method defined for interface " + clazz);
	
		try {
			return (Long)countMethod.invoke(this.queries, vars);
		} catch (Exception e) {
			throw new IllegalStateException("error while calling reflect method on jdbi instance", e);
		}
	}
	
	@Override
	public int countPages(int elementsParPage, Object... vars) {
		return PageValidator.pageNumber(countItems(vars), elementsParPage);
	}
	
	@SuppressWarnings("unchecked")
	private <View> List<View> queryWithMethod(Class<View> viewClass, final Method method, Object... vars) {
		try {
			return (List<View>)method.invoke(this.queries, vars);
		} catch (Exception e) {
			throw new IllegalStateException("error while calling reflect method on jdbi instance", e);
		}
	}

	@Override
	public Object[] orderNamedParams(NamedParam... vars) {
		final Object[] objects = new Object[boundNames.length];
		
		int i = 0;
		for(final String boundName : boundNames) {
			for(final NamedParam var : vars) {
				if(boundName.equals(var.name)) {
					objects[i] = var.var;
				}
			}
			i++;
		}
		
		return objects;
	}
	
	@Override
	public String[] getParamNames() {
		return boundNames;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	
}
