package com.emo.mango.cqs.queries.executors;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.skife.jdbi.v2.util.LongMapper;

import com.emo.mango.cqs.QueryExecutor;
import com.emo.mango.cqs.queries.annotations.MangoSql;
import com.google.common.base.Joiner;

public class SqlQueryExecutor implements QueryExecutor {

	private final String selectQuery;

	private final String countQuery;

	private final DataSource ds;

	private final Binding[] bindings;

	private final String[] mappedFields;

	private final Class<?>[] viewTypes;

	private final Constructor<?> viewConstructor;

	private final String name;

	public SqlQueryExecutor(final Method method, final DataSource ds) {
		this.ds = ds;

		Class<?> viewClass = method.getReturnType();

		if (Collection.class.isAssignableFrom(viewClass)) {
			ParameterizedType type = (ParameterizedType) method
					.getGenericReturnType();
			viewClass = (Class<?>) type.getActualTypeArguments()[0];
		}

		final ViewClassParser viewParser = new ViewClassParser(viewClass);

		this.mappedFields = viewParser.mappedFields;
		this.viewConstructor = viewParser.constructor;
		this.viewTypes = viewParser.types;

		final BindingMethodParser methodParser = new BindingMethodParser(method);

		this.bindings = methodParser.bindings;

		this.selectQuery = createQuerySelectPart(viewParser.view,
				viewParser.mappedFields)
				+ " "
				+ method.getAnnotation(MangoSql.class).value();
		this.countQuery = "select count(*) "
				+ method.getAnnotation(MangoSql.class).value();

		this.name = method.getName();
	}

	private String createQuerySelectPart(Class<?> view, String[] mappedFields) {

		final String[] asAliases = new String[mappedFields.length];
		for (int i = 0; i < asAliases.length; ++i) {
			asAliases[i] = mappedFields[i] + " as f"
					+ new Integer(i).toString().trim();
		}

		return "select " + Joiner.on(", ").join(asAliases);
	}

	private String getQueryOffsetLimit() {
		return "limit :limit offset :offset";
	}

	private <View> Query<View> completeQueryParams(Query<View> query,
			Object... values) {
		int i = 0;
		for (final Binding binding : bindings) {
			query = query.bind(binding.value, values[i++]);
		}

		return query;
	}

	@Override
	public long countItems(Object... values) {
		final Handle h = DBI.open(ds);
		try {
			Query<Map<String, Object>> query = h.createQuery(countQuery);
			Query<Long> longQuery = query.map(LongMapper.FIRST);
			longQuery = completeQueryParams(longQuery, values);

			final long r = longQuery.first();
			return r;
		} finally {
			h.close();
		}

	}

	@Override
	public int countPages(int elementsParPage, Object... values) {
		return PageValidator.pageNumber(countItems(values), elementsParPage);
	}

	@Override
	public <View> List<View> query(Class<View> viewClass, Object... values) {
		final Handle h = DBI.open(ds);

		try {
			Query<Map<String, Object>> query = h.createQuery(selectQuery);

			Query<View> mappedQuery = query.map(new ConstructorMapper<View>(
					viewConstructor, mappedFields, viewTypes));
			mappedQuery = completeQueryParams(mappedQuery, values);

			final List<View> r = mappedQuery.list();
			return r;

		} finally {
			h.close();
		}
	}

	@Override
	public List<?> query(Object... vars) {
		return query(Object.class, vars);
	}

	@Override
	public <View> List<View> pagedQuery(Class<View> viewClass, int page,
			int elementsParPage, Object... values) {

		PageValidator.assertValidPaging(page, elementsParPage);

		final Handle h = DBI.open(ds);
		try {
			Query<Map<String, Object>> query = h.createQuery(selectQuery + " "
					+ getQueryOffsetLimit());

			Query<View> mappedQuery = query.map(new ConstructorMapper<View>(
					viewConstructor, mappedFields, viewTypes));
			mappedQuery = completeQueryParams(mappedQuery, values);

			mappedQuery = mappedQuery.bind("offset",
					PageValidator.offset(page, elementsParPage));
			mappedQuery = mappedQuery.bind("limit",
					PageValidator.limit(page, elementsParPage));

			final List<View> r = mappedQuery.list();

			return r;
		} finally {
			h.close();
		}
	}

	@Override
	public List<?> pagedQuery(int page, int elementsParPage, Object... vars) {
		return pagedQuery(Object.class, page, elementsParPage, vars);
	}

	private static class ConstructorMapper<View> implements
			ResultSetMapper<View> {

		private final String[] mappedFields;

		private final Class<?>[] types;

		private final Constructor<?> constructor;

		@SuppressWarnings("unchecked")
		@Override
		public View map(int pos, ResultSet rs, StatementContext sc)
				throws SQLException {

			final Object[] args = new Object[mappedFields.length];

			for (int i = 0; i < mappedFields.length; ++i) {
				args[i] = rs.getObject("f" + new Integer(i).toString().trim());
				// System.out.println(args[i].getClass() + " -> " +
				// expectedTypes[i]);
				// Attempt type conversion
				try {
					args[i] = types[i].cast(args[i]);
				} catch (ClassCastException cce) {
					if (types[i].getName().equals(String.class.getName())) {
						args[i] = args[i].toString();
					} else {
						throw cce;
					}
				}
			}

			try {
				return (View) constructor.newInstance(args);
			} catch (Exception e) {
				throw new RuntimeException(
						"unable instantiate query object from this resultset",
						e);
			}
		}

		public ConstructorMapper(final Constructor<?> constructor,
				final String[] mappedFields, final Class<?>[] types) {
			this.mappedFields = mappedFields;
			this.types = types;
			this.constructor = constructor;
		}

	}

	@Override
	public Object[] orderNamedParams(NamedParam... vars) {
		final Object[] objects = new Object[bindings.length];

		int i = 0;
		for (final Binding binding : bindings) {
			for (final NamedParam var : vars) {
				if (binding.value.equals(var.name)) {
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
