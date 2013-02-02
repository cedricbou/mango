package com.emo.mango.spring.query.support;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.StringUtils;


public abstract class SqlExecutor<Q> extends QueryBasedExecutor<Q> {

	private final JdbcTemplate template;
	
	private final NamedParameterJdbcTemplate namedTemplate;
	
	private ConstructorMapper mapper;

	public SqlExecutor(final DataSource ds) {
		template = new JdbcTemplate(ds);
		namedTemplate = new NamedParameterJdbcTemplate(template);
	}

	@Override
	protected String buildSelectQuery(final String query, final Class<?> clazz, final String... fields) {
		final String asfields[] = new String[fields.length];
		final String asLabels[] = new String[fields.length];
		
		for(int i = 0; i < asfields.length; ++i) {
			asLabels[i] = "f" + new Integer(i).toString().trim();
			asfields[i] = fields[i] + " as " + asLabels[i];
		}

		mapper = new ConstructorMapper(asLabels);

		return "select "
				+ StringUtils.arrayToDelimitedString(asfields,
						", ") + " " + query + ((query.contains("?"))?" limit ?, ?":" limit :offset, :limit");
	}
	
	@Override
	protected String buildCountQuery(String query) {
		return "select count(*) " + query;
	}


	private class ConstructorMapper implements RowMapper<Q> {
		
		private final String[] labels;
		
		public ConstructorMapper(final String[] labels) {
			this.labels = labels;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public Q mapRow(ResultSet rs, int arg1) throws SQLException {
			
			final Constructor<?> constr = SqlExecutor.this.getClazz().getConstructors()[0];
		
			final Class<?>[] expectedTypes = constr.getParameterTypes();
			final Object[] args = new Object[labels.length];
			
			for(int i = 0; i < labels.length; ++i) {
				args[i] = rs.getObject(labels[i]);
				System.out.println(args[i].getClass() + " -> " + expectedTypes[i]);
				
				try {
					args[i] = expectedTypes[i].cast(args[i]);
				} catch(ClassCastException cce) {
					if(expectedTypes[i].getName().equals(String.class.getName())) {
						args[i] = args[i].toString();
					}
					else {
						throw cce;
					}
				}
			}
			
			try {
				return (Q)constr.newInstance(args);
			} catch (Exception e) {
				throw new RuntimeException("unable instantiate query object from this resultset", e);
			}
		}
	}
	
	@Override
	protected List<Q> runPagedQuery(String selectQuery, int page, int elementsParPage, Object... values) {
		if(selectQuery.contains("?")) {
			final Object[] valuesWithLimit = new Object[values.length + 2];
			for(int i = 0; i < values.length; ++i) {
				valuesWithLimit[i] = values[i];
			}
			
			valuesWithLimit[valuesWithLimit.length - 2] = (page - 1) * elementsParPage;
			valuesWithLimit[valuesWithLimit.length - 1] = elementsParPage;
			
			return template.query(selectQuery, valuesWithLimit, mapper);
		}
		else {
			final Map<String, Object> namedParams = new HashMap<String, Object>();
			int i = 0;
			for(final String param : getParams()) {
				namedParams.put(param, values[i++]);
			}
			namedParams.put("offset", (page - 1) * elementsParPage);
			namedParams.put("limit", elementsParPage);
			return namedTemplate.query(selectQuery, namedParams, mapper);
		}
	}

	@Override
	protected long runCountQuery(String countQuery, Object... values) {
		if(countQuery.contains("?")) {
			return template.queryForLong(countQuery, values);
		}
		else {
			final Map<String, Object> namedParams = new HashMap<String, Object>();
			int i = 0;
			for(final String param : getParams()) {
				namedParams.put(param, values[i++]);
			}
			return namedTemplate.queryForLong(countQuery, namedParams);
		}
	}
	
}
