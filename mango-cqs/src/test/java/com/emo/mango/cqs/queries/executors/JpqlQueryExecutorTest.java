package com.emo.mango.cqs.queries.executors;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skife.jdbi.v2.DBI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.emo.mango.cqs.queries.annotations.MangoJpql;
import com.emo.mango.test.support.FindWithNames;
import com.emo.mango.test.support.FooBar;
import com.emo.mango.test.support.SetUpData;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class JpqlQueryExecutorTest {

	private JpqlQueryExecutor executor;
	
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private DataSource dataSource;

	@Before
	public void setUp() {
		SetUpData.setUpPeople(new DBI(dataSource));

		Method queryMethod = null;

		for (final Method method : FindWithNames.class.getMethods()) {
			if (method.isAnnotationPresent(MangoJpql.class) && method.getName().equals("findExactName")) {
				queryMethod = method;
			}
		}

		executor = new JpqlQueryExecutor(queryMethod, em);

	}
	
	@Test
	public void testSimpleQuery() {
		final List<FooBar> foobars = executor.query(FooBar.class, "grzegusz");
		assertEquals(4, foobars.size());
	}

	@Test
	public void testPagedQuery() {
		final List<FooBar> foobars = executor.pagedQuery(FooBar.class, 2, 2, "grzegusz");
		assertEquals(2, foobars.size());
	}

	@Test
	public void testCountQuery() {
		long foobars = executor.countItems("grzegusz");
		assertEquals(4, foobars);
	}

}
