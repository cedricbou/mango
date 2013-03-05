package com.emo.mango.at.queries;

import static org.junit.Assert.assertEquals;

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

import com.emo.mango.cqs.CQSSystem;
import com.emo.mango.cqs.QueryExecutor;
import com.emo.mango.test.support.FindWithNames;
import com.emo.mango.test.support.FooBar;
import com.emo.mango.test.support.SetUpData;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class CqsQueriesTesting {

	private CQSSystem cqs;

	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private DataSource dataSource;

	@Before
	public void setUp() {
		SetUpData.setUpPeople(new DBI(dataSource));

		this.cqs = new CQSSystem();
		this.cqs.declareQueries(FindWithNames.class, dataSource, em);
	}

	@Test
	public void queryExecutorTest() {

		final QueryExecutorFixture[] fixtures = new QueryExecutorFixture[] {
			new QueryExecutorFixture("findExactName", new Object[] { "grzegusz" }, 4, "cool")
		};

		for (final QueryExecutorFixture fixture : fixtures) {
			final QueryExecutor qe = cqs.getQueryExecutor(fixture.query);

			int i = 0;
			
			for (final FooBar view : qe.pagedQuery(FooBar.class, 1, 20, fixture.params)) {
				assertEquals(fixture.expectedFlag, view.foo);
				++i;
			}

			assertEquals(fixture.expectedCount, i);
		}
	}
	
	private static class QueryExecutorFixture {
		public final String query;
		public final Object[] params;
		public final int expectedCount;
		public final String expectedFlag;
		
		public QueryExecutorFixture(final String query, final Object[] params, final int expectedCount, final String expectedFlag) {
			this.query = query;
			this.params = params;
			this.expectedCount = expectedCount;
			this.expectedFlag = expectedFlag;
		}
	}

	/*
	@Test
	public void interfaceQueryTest() {
		final FindWithNames queries = cqs.open(FindWithNames.class,
				new DataSourceProvider(ds), new EntityManagerProvider(em));

		final FooBar fooBar1 = queries.findExactName("greg");
		assertEquals("winner", fooBar1.foo);

		final FooBar fooBar2 = queries.findExactNameUnmanaged("greg stephan");
		assertEquals("winner", fooBar2.foo);

		final List<FooBar> fooBars1 = queries.findNameStartingWith("greg");
		assertEquals(4, fooBars1.size());

		final List<FooBar> fooBars2 = queries.findNameEndingWith("greg");
		assertEquals(3, fooBars2.size());

		queries.close();
	}
	*/
	
}
