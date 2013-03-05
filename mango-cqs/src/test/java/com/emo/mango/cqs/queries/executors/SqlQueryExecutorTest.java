package com.emo.mango.cqs.queries.executors;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.List;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;

import com.emo.mango.cqs.queries.annotations.MangoSql;
import com.emo.mango.test.support.FindWithNames;
import com.emo.mango.test.support.FooBar;
import com.emo.mango.test.support.SetUpData;

public class SqlQueryExecutorTest {

	private SqlQueryExecutor executor;

	@Before
	public void setUp() {
		final DataSource ds = JdbcConnectionPool.create("jdbc:h2:mem:test",
				"username", "password");
		
		SetUpData.setUpPeople(new DBI(ds));

		Method queryMethod = null;

		for (final Method method : FindWithNames.class.getMethods()) {
			if (method.isAnnotationPresent(MangoSql.class) && method.getName().equals("findNameStartingWith")) {
				queryMethod = method;
			}
		}

		executor = new SqlQueryExecutor(queryMethod, ds);
	}
	
	@Test
	public void testSimpleQuery() {
		final List<FooBar> foobars = executor.query(FooBar.class, "greg%");
		assertEquals(4, foobars.size());
	}

	@Test
	public void testPagedQuery() {
		final List<FooBar> foobars = executor.pagedQuery(FooBar.class, 2, 2, "greg%");
		assertEquals(2, foobars.size());
	}

	@Test
	public void testCountQuery() {
		long foobars = executor.countItems("greg%");
		assertEquals(4, foobars);
	}

}
