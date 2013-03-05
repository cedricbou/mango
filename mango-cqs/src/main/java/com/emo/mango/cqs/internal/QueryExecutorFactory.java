package com.emo.mango.cqs.internal;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import com.emo.mango.cqs.Queries;
import com.emo.mango.cqs.QueryExecutor;
import com.emo.mango.cqs.queries.annotations.MangoJdbi;
import com.emo.mango.cqs.queries.annotations.MangoJpql;
import com.emo.mango.cqs.queries.annotations.MangoSql;
import com.emo.mango.cqs.queries.annotations.QueryType;
import com.emo.mango.cqs.queries.executors.JdbiQueryExecutor;
import com.emo.mango.cqs.queries.executors.JpqlQueryExecutor;
import com.emo.mango.cqs.queries.executors.SqlQueryExecutor;
import com.google.common.base.CaseFormat;
import com.google.common.base.Preconditions;

public class QueryExecutorFactory {

	public List<QueryExecutor> executorsFor(
			final Class<? extends Queries> queriesClass, DataSource ds) {
		return executorsFor(queriesClass, ds, null);
	}

	public List<QueryExecutor> executorsFor(
			final Class<? extends Queries> queriesClass, EntityManager em) {
		return executorsFor(queriesClass, null, em);
	}

	public List<QueryExecutor> executorsFor(
			final Class<? extends Queries> queriesClass, DataSource ds,
			EntityManager em) {

		final List<QueryExecutor> executors = new LinkedList<QueryExecutor>();
		final List<QueryMethods> allQueryMethods = queryMethodsIn(queriesClass);

		for (final QueryMethods queryMethods : allQueryMethods) {
			executors.add(buildQueryExecutorFromMethod(queriesClass,
					queryMethods, ds, em));
		}

		return executors;
	}

	private QueryExecutor buildQueryExecutorFromMethod(
			final Class<? extends Queries> queriesClass,
			final QueryMethods queryMethods, DataSource ds, EntityManager em) {
		if (queryMethods.isMangoSql()) {
			return buildMangoSqlExecutorFromMethod(queryMethods, ds);
		} else if (queryMethods.isMangoJpql()) {
			return buildMangoJpqlExecutorFromMethod(queryMethods, em);
		} else if (queryMethods.isMangoJdbi()) {
			return buildJdbiQueryExecutorFromMethod(queriesClass, queryMethods,
					ds);
		} else {
			throw new IllegalArgumentException(
					"no query executor builder for the query method "
							+ queryMethods.name);
		}
	}

	private QueryExecutor buildJdbiQueryExecutorFromMethod(
			final Class<? extends Queries> queriesClass,
			QueryMethods queryMethods, final DataSource ds) {
		Preconditions
				.checkNotNull(ds,
						"should have a non null datasource to build jdbi query executor");

		return new JdbiQueryExecutor(queryMethods.name, queriesClass,
				queryMethods.methods.get(QueryType.COUNT),
				queryMethods.methods.get(QueryType.STANDARD),
				queryMethods.methods.get(QueryType.PAGED), ds);
	}

	private QueryExecutor buildMangoJpqlExecutorFromMethod(
			QueryMethods queryMethods, final EntityManager em) {
		Preconditions.checkNotNull(em, "should have a non null entity manager to build jpql query executor");
		
		return new JpqlQueryExecutor(queryMethods.methods.get(QueryType.STANDARD), em);
	}

	private QueryExecutor buildMangoSqlExecutorFromMethod(
			QueryMethods queryMethods, final DataSource ds) {
		Preconditions.checkNotNull(ds, "should have a non null datasource to build sql query executor");
		
		return new SqlQueryExecutor(queryMethods.methods.get(QueryType.STANDARD), ds);
	}

	private List<QueryMethods> queryMethodsIn(
			final Class<? extends Queries> queriesClass) {
		final Method[] candidates = queriesClass.getMethods();

		final List<QueryMethods> selected = new LinkedList<QueryMethods>();

		final Map<String, Map<QueryType, Method>> methods = new HashMap<String, Map<QueryType, Method>>();

		for (final Method method : candidates) {
			if (isQueryMethod(method)) {
				final QueryDesc desc = getQueryDesc(method);

				if (!methods.containsKey(desc.name)) {
					methods.put(desc.name, new HashMap<QueryType, Method>());
					selected.add(new QueryMethods(desc.name, methods
							.get(desc.name)));
				}

				methods.get(desc.name).put(desc.type, desc.method);
			}
		}

		return selected;
	}

	private boolean isQueryMethod(final Method method) {
		return method.isAnnotationPresent(MangoJpql.class)
				|| method.isAnnotationPresent(MangoSql.class)
				|| method.isAnnotationPresent(MangoJdbi.class);
	}

	private QueryDesc getQueryDesc(final Method method) {
		if (method.isAnnotationPresent(MangoJdbi.class)) {
			return new QueryDesc(method.getAnnotation(MangoJdbi.class).value(),
					method, method.getAnnotation(MangoJdbi.class).type());
		}

		return new QueryDesc(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL,
				method.getName()), method, QueryType.STANDARD);
	}

	private static class QueryMethods {
		public final String name;
		public final Map<QueryType, Method> methods;

		public QueryMethods(String name, Map<QueryType, Method> methods) {
			this.name = name;
			this.methods = methods;
		}

		public boolean isMangoJdbi() {
			if (methods.containsKey(QueryType.STANDARD)) {
				return methods.get(QueryType.STANDARD).isAnnotationPresent(
						MangoJdbi.class);
			} else if (methods.containsKey(QueryType.PAGED)) {
				return methods.get(QueryType.PAGED).isAnnotationPresent(
						MangoJdbi.class);
			}
			return false;
		}

		public boolean isMangoSql() {
			if (methods.containsKey(QueryType.STANDARD)) {
				return methods.get(QueryType.STANDARD).isAnnotationPresent(
						MangoSql.class);
			}
			return false;
		}

		public boolean isMangoJpql() {
			if (methods.containsKey(QueryType.STANDARD)) {
				return methods.get(QueryType.STANDARD).isAnnotationPresent(
						MangoJpql.class);
			}
			return false;
		}
	}

	private static class QueryDesc {
		public final String name;
		public final QueryType type;
		public final Method method;

		public QueryDesc(String name, Method method, QueryType type) {
			this.name = name;
			this.type = type;
			this.method = method;
		}
	}

}
