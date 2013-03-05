package com.emo.mango.cqs.queries.executors;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.List;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;

import com.emo.mango.cqs.queries.annotations.MangoJdbi;
import com.emo.mango.test.support.FindWithNames;
import com.emo.mango.test.support.FooBar;
import com.emo.mango.test.support.SetUpData;

public class JdbiQueryExecutorTest {

	private JdbiQueryExecutor executor;

	@Before
	public void setUp() {
		final DataSource ds = JdbcConnectionPool.create("jdbc:h2:mem:test",
				"username", "password");
		
		SetUpData.setUpPeople(new DBI(ds));

		Method countMethod = null;
		Method queryMethod = null;
		Method pagedMethod = null;

		for (final Method method : FindWithNames.class.getMethods()) {
			if (method.isAnnotationPresent(MangoJdbi.class)) {
				final MangoJdbi annot = method.getAnnotation(MangoJdbi.class);
				if (annot.value().equals("FindNameEndingWith")) {
					switch (annot.type()) {
					case COUNT:
						countMethod = method;
						break;
					case PAGED:
						pagedMethod = method;
						break;
					case STANDARD:
						queryMethod = method;
						break;
					}
				}
			}
		}

		executor = new JdbiQueryExecutor("FindNameEndingWith",
				FindWithNames.class, countMethod, queryMethod, pagedMethod, ds);

	}
	
	@Test
	public void testSimpleQuery() {
		final List<FooBar> foobars = executor.query(FooBar.class, "%greg");
		assertEquals(4, foobars.size());
	}

	@Test
	public void testPagedQuery() {
		final List<FooBar> foobars = executor.pagedQuery(FooBar.class, 2, 2, "%greg");
		assertEquals(2, foobars.size());
	}

	@Test
	public void testCountQuery() {
		long foobars = executor.countItems("%greg");
		assertEquals(4, foobars);
	}

}
