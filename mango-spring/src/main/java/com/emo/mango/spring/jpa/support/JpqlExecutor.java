package com.emo.mango.spring.jpa.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.util.StringUtils;

import com.emo.mango.cqs.QueryExecutor;

public abstract class JpqlExecutor<Q> implements QueryExecutor<Q> {

	private String jpql;

	private String[] params = new String[] {};

	protected JpqlExecutor() {

	}

	protected abstract EntityManager entityManager();

	protected void setData(final String jpql, final String[] params,
			final Class<?> clazz) {
		if (!jpql.startsWith("from")) {
			throw new IllegalArgumentException(
					"jpql request must start with from");
		}

		if (null == params) {
			this.params = new String[] {};
		} else {
			this.params = params;
		}

		final Constructor<?>[] constrs = clazz.getConstructors();

		if (constrs.length > 1) {
			throw new IllegalArgumentException(
					"jpql : the queried object should have only one constructor");
		}

		final Constructor<?> constr = constrs[0];

		final Annotation[][] annotOnParams = constr.getParameterAnnotations();

		final List<String> selectFields = new LinkedList<String>();

		for (final Annotation[] annots : annotOnParams) {
			for (final Annotation annot : annots) {
				if (com.emo.mango.spring.jpa.annotations.Query.class.isInstance(annot)) {
					selectFields
							.add(((com.emo.mango.spring.jpa.annotations.Query) annot)
									.value());
					break;
				}
			}
		}
		
		if(selectFields.size() != constr.getParameterTypes().length) {
			throw new IllegalArgumentException("Jpql : all constructor parameter must be annoted with the @Query annotation");
		}

		// build request with select
		this.jpql = "select new "
				+ clazz.getName()
				+ "("
				+ StringUtils.arrayToDelimitedString(selectFields.toArray(),
						", ") + ") " + jpql;
	}

	public String getJpql() {
		return jpql;
	}

	public String[] getParams() {
		return params;
	}

	@Override
	public List<Q> query(Object... values) {
		return pagedQuery(1, 200, values);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Q> pagedQuery(int page, int elementsParPage, Object... values) {
		if (params.length != values.length) {
			throw new IllegalArgumentException(
					"number of values does not match number of declared params for this query");
		}

		if (page < 1) {
			throw new IllegalArgumentException(
					"page numbering begins at 1, received page " + page);
		}

		final Query query = entityManager().createQuery(jpql);
		int i = 0;
		for (final String name : params) {
			if (jpql.contains("?")) {
				query.setParameter(i + 1, values[i]);
			} else {
				query.setParameter(name, values[i]);
			}
			++i;
		}

		query.setFirstResult((page - 1) * elementsParPage);
		query.setMaxResults(elementsParPage);

		return query.getResultList();
	}

	@Override
	public int countPages(int elementsParPage, Object... values) {
		final long itemCount = countItems(values);
		return (int) (itemCount / elementsParPage)
				+ (((itemCount % elementsParPage) > 0) ? 1 : 0);
	}

	@Override
	public long countItems(Object... values) {
		if (params.length != values.length) {
			throw new IllegalArgumentException(
					"number of values does not match number of declared params for this query");
		}

		final int indexOfFrom = jpql.indexOf("from");

		if (indexOfFrom < 0) {
			throw new IllegalStateException(
					"jpql query does not contain a from keywords, unable to transform query to count item : "
							+ jpql);
		}

		final String countJpql = jpql.substring(indexOfFrom);

		final TypedQuery<Long> query = entityManager().createQuery(
				"select new java.lang.Long(count(*)) " + countJpql, Long.class);

		int i = 0;
		for (final String name : params) {
			if (jpql.contains("?")) {
				query.setParameter(i + 1, values[i]);
			} else {
				query.setParameter(name, values[i]);
			}
			++i;
		}
		
		return query.getSingleResult();
	}
}
