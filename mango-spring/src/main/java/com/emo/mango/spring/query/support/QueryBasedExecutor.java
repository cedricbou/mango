package com.emo.mango.spring.query.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;

import com.emo.mango.cqs.AbstractQueryExecutor;
import com.emo.mango.cqs.SearchCriteria;
import com.emo.mango.cqs.SearchValue;

abstract class QueryBasedExecutor<Q> extends AbstractQueryExecutor<Q> {

	private String selectQuery;
	
	private String countQuery;
	
	private Class<?> clazz;
	
	private SearchCriteria searchCriteria;
	
	protected final void setData(final String query, final SearchCriteria searchCriteria, final Class<?> clazz) {
		
		if (!query.toLowerCase().startsWith("from")) {
			throw new IllegalArgumentException(
					"Query request must start with from");
		}
		
		this.clazz = clazz;

		this.searchCriteria = searchCriteria;
	
		final Constructor<?>[] constrs = clazz.getConstructors();

		if (constrs.length > 1) {
			throw new IllegalArgumentException(
					"the queried object should have only one constructor : " + clazz);
		}

		final Constructor<?> constr = constrs[0];

		final Annotation[][] annotOnParams = constr.getParameterAnnotations();

		final List<String> selectFields = new LinkedList<String>();

		for (final Annotation[] annots : annotOnParams) {
			for (final Annotation annot : annots) {
				if (com.emo.mango.spring.query.annotations.Query.class.isInstance(annot)) {
					selectFields
							.add(((com.emo.mango.spring.query.annotations.Query) annot)
									.value());
					break;
				}
			}
		}
		
		if(selectFields.size() != constr.getParameterTypes().length) {
			throw new IllegalArgumentException("all constructor parameter must be annoted with the @Query annotation : " + clazz);
		}

		// build request with select
		this.selectQuery = buildSelectQuery(query, clazz, selectFields.toArray(new String[] {}));
		this.countQuery = buildCountQuery(query);
	}

	protected abstract String buildSelectQuery(final String query, final Class<?> clazz, final String... fields);

	protected abstract String buildCountQuery(final String query);

	protected Class<?> getClazz() {
		return this.clazz;
	}
		
	@Override
	public SearchCriteria getSearchCriteria() {
		return searchCriteria;
	}
	
	@Override
	public final List<Q> query(final SearchValue values) {
		return pagedQuery(1, 200, values);
	}
	
	@Override
	public final List<Q> pagedQuery(int page, int elementsParPage, SearchValue values) {
		values.assertMatch(searchCriteria);

		if (page < 1) {
			throw new IllegalArgumentException(
					"page numbering begins at 1, received page " + page);
		}

		return runPagedQuery(selectQuery, page, elementsParPage, values);
	}
	
	protected abstract List<Q> runPagedQuery(String selectQuery, int page, int elementsParPage, SearchValue values);

	protected abstract long runCountQuery(String countQuery, SearchValue values);

	@Override
	public final int countPages(int elementsParPage, SearchValue values) {
		final long itemCount = countItems(values);
		return (int) (itemCount / elementsParPage)
				+ (((itemCount % elementsParPage) > 0) ? 1 : 0);
	}

	@Override
	public final long countItems(SearchValue values) {
		values.assertMatch(searchCriteria);

		return runCountQuery(countQuery, values);
	}

}
