package com.emo.mango.cqs.queries.executors;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.emo.mango.cqs.QueryExecutor;
import com.emo.mango.cqs.queries.annotations.MangoJpql;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

public class JpqlQueryExecutor implements QueryExecutor {

	private final EntityManager em;
	
	private final String selectQuery;
	
	private final String countQuery;
	
	private final Binding[] bindings;
	
	private final String name;
	
	public JpqlQueryExecutor(final Method method, final EntityManager em) {
		this.em = em;
		
		Class<?> viewClass = method.getReturnType();
		
		if(Collection.class.isAssignableFrom(viewClass)) {
			ParameterizedType type = (ParameterizedType)method.getGenericReturnType();
			viewClass = (Class<?>)type.getActualTypeArguments()[0];
		}

		final ViewClassParser viewParser = new ViewClassParser(viewClass);
		final BindingMethodParser methodParser = new BindingMethodParser(method);
		
		this.bindings = methodParser.bindings;
		
		this.selectQuery = createQuerySelectPart(viewParser.view, viewParser.mappedFields) + " " + method.getAnnotation(MangoJpql.class).value();
		this.countQuery = "select count(*) " + method.getAnnotation(MangoJpql.class).value();
	
		this.name = method.getName();
	}
	
	private String createQuerySelectPart(Class<?> view,
			String[] mappedFields) {

		return "select new " + view.getName() + "("
				+ Joiner.on(", ").join(mappedFields) + ") ";
	}
	
	private void completeQueryParams(final Query query, Object... values) {
		Preconditions.checkArgument(values.length == bindings.length, "query vars count should be the same than the number of declared bindings");
		
		int i = 0;
		
		for (final Binding binding : bindings) {
			query.setParameter(binding.value, values[i++]);
		}
	}
	
	@Override
	public <View> List<View> query(Class<View> viewClass, Object... vars) {
		final TypedQuery<View> query = em.createQuery(selectQuery, viewClass);

		completeQueryParams(query, vars);

		return query.getResultList();
	}
	
	@Override
	public List<?> query(Object... vars) {
		final Query query = em.createQuery(selectQuery);

		completeQueryParams(query, vars);

		return query.getResultList();
	}

	@Override
	public long countItems(Object... vars) {
		final TypedQuery<Long> query = em.createQuery(countQuery, Long.class);
		completeQueryParams(query, vars);
		return query.getSingleResult();
	}

	@Override
	public int countPages(int elementsParPage, Object... vars) {
		return PageValidator.pageNumber(countItems(vars), elementsParPage);
	}
	
	@Override
	public <View> List<View> pagedQuery(Class<View> viewClass, int page,
			int elementsParPage, Object... vars) {

		PageValidator.assertValidPaging(page, elementsParPage);
		
		final TypedQuery<View> query = em.createQuery(selectQuery, viewClass);

		completeQueryParams(query, vars);

		query.setFirstResult(PageValidator.offset(page, elementsParPage));
		query.setMaxResults(PageValidator.limit(page, elementsParPage));

		return query.getResultList();
	}
	
	@Override
	public List<?> pagedQuery(int page, int elementsParPage, Object... vars) {
		PageValidator.assertValidPaging(page, elementsParPage);
		
		final Query query = em.createQuery(selectQuery);

		completeQueryParams(query, vars);

		query.setFirstResult(PageValidator.offset(page, elementsParPage));
		query.setMaxResults(PageValidator.limit(page, elementsParPage));

		return query.getResultList();
	}
	
	@Override
	public Object[] orderNamedParams(NamedParam... vars) {
		final Object[] objects = new Object[bindings.length];
		
		int i = 0;
		for (final Binding binding : bindings) {
			for(final NamedParam var : vars) {
				if(binding.value.equals(var.name)) {
					objects[i] = var.var;
				}
			}
			i++;
		}
		
		return objects;
	}
	
	@Override
	public String[] getParamNames() {
		final String[] names = new String[bindings.length];
		
		int i = 0;
		for (final Binding binding : bindings) {
			names[i++] = binding.value;
		}
		
		return names;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
}
