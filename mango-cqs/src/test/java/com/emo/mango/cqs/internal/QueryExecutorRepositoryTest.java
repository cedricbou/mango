package com.emo.mango.cqs.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.emo.mango.cqs.QueryExecutor;

public class QueryExecutorRepositoryTest {

	private QueryExecutorRepository repo;
	
	private QueryExecutor aQueryExecutor;
	
	@Before
	public void setupSUT() {
		this.repo = new QueryExecutorRepository();
		this.aQueryExecutor = Mockito.mock(QueryExecutor.class);
		Mockito.when(aQueryExecutor.getName()).thenReturn("aSampleName");
	}
	
	@Test
	public void storeAndGetAQueryExecutorTest() {
		this.repo.store(aQueryExecutor);
		assertEquals(this.aQueryExecutor, this.repo.get(aQueryExecutor.getName()));
	}
	
	@Test
	public void forbidTwoExecutorsWithSameNameTest() {
		this.repo.store(aQueryExecutor);
		try {
			this.repo.store(aQueryExecutor);
			fail("duplicate query exector names are forbidden");
		}
		catch(IllegalArgumentException e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void forbidToGetNotStoredExecutorsTest() {
		try {
			this.repo.get("ghost");
			fail("getting a query executor not stored in repository is forbidden");
		}
		catch(NullPointerException e) {
			assertTrue(true);
		}
	}
	
}
