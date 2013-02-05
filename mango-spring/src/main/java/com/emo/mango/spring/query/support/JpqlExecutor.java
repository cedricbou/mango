package com.emo.mango.spring.query.support;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.util.StringUtils;

import com.emo.mango.cqs.SearchValue;


public abstract class JpqlExecutor<Q> extends QueryBasedExecutor<Q> {

	protected abstract EntityManager entityManager();

	@Override
	protected String buildSelectQuery(final String query, final Class<?> clazz, final String... fields) {
		return "select new "
				+ clazz.getName()
				+ "("
				+ StringUtils.arrayToDelimitedString(fields,
						", ") + ") " + query;
	}
	
	@Override
	protected String buildCountQuery(String query) {
		return "select new java.lang.Long(count(*)) " + query;
	}

	private void completeQueryParams(final boolean withQuestionMark, final Query query, SearchValue values) {
		int i = 0;
		for (final String name : values.getCriteria()) {
			if (withQuestionMark) {
				query.setParameter(i + 1, values.getValues()[i]);
			} else {
				query.setParameter(name, values.get(name));
			}
			++i;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected List<Q> runPagedQuery(String selectQuery, int page, int elementsParPage, SearchValue values) {
		final Query query = entityManager().createQuery(selectQuery);
		
		completeQueryParams(selectQuery.contains("?"), query, values);
		
		query.setFirstResult((page - 1) * elementsParPage);
		query.setMaxResults(elementsParPage);

		return query.getResultList();
	}

	@Override
	protected long runCountQuery(String countQuery, SearchValue values) {
		final TypedQuery<Long> query = entityManager().createQuery(
				countQuery, Long.class);

		completeQueryParams(countQuery.contains("?"), query, values);
		
		return query.getSingleResult();
	}
	
}
