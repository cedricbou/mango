package com.emo.mango.cqs.internal;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.emo.mango.cqs.QueryExecutor;
import com.emo.mango.cqs.queries.executors.JdbiQueryExecutor;
import com.emo.mango.cqs.queries.executors.JpqlQueryExecutor;
import com.emo.mango.cqs.queries.executors.SqlQueryExecutor;
import com.emo.mango.test.support.FindWithNames;

public class QueryExecutorFactoryTest {

	private List<QueryExecutor> executors;
	
	private DataSource ds = Mockito.mock(DataSource.class);
	
	private EntityManager em = Mockito.mock(EntityManager.class);
	
	@Before
	public void setUpSUT() {
		executors = new QueryExecutorFactory().executorsFor(FindWithNames.class, ds, em);
	}
	
	@Test
	public void shouldBuildAsManyQEAsMethods() {
		assertEquals(4, executors.size());
	}
	
	@Test
	public void shouldHaveAsManyJpqlExecutorAsMangoJpqlAnnotatedMethods() {
		int count = 0;
		
		for(final QueryExecutor qe : executors) {
			if(qe instanceof JpqlQueryExecutor) {
				count++;
			}
		}
		
		assertEquals(2, count);
	}
	
	@Test
	public void shouldHaveAsManySqlExecutorsAsMangoSqlAnnotatedMethods() {
		int count = 0;
		
		for(final QueryExecutor qe : executors) {
			if(qe instanceof SqlQueryExecutor) {
				count++;
			}
		}
		
		assertEquals(1, count);
	}
	
	@Test
	public void shouldHaveAsManyJdbiExecutorsAsJdbiAnnotatedMethods() {
		int count = 0;
		
		for(final QueryExecutor qe : executors) {
			if(qe instanceof JdbiQueryExecutor) {
				count++;
			}
		}
		
		assertEquals(1, count);
	}
}
