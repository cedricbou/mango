package com.emo.mango.log;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;

import org.junit.Test;

import com.emo.mango.annotations.Final;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SingleThreadedClaimStrategy;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;

public class BusinessTransactionTest {

	private static class Event {
		public @Final String foo;
		public @Final int bar;
		
		protected Event() {	}
		
		public Event(String foo, int bar) {
			copy(foo, bar);
		}
		
		public void copy(String foo, int bar) {
			this.foo = foo;
			this.bar = bar;
		}
	}
	
	private static class MyEventFactory implements EventFactory<Event> {
		@Override
		public Event newInstance() {
			return new Event();
		}
	}
	
	private static class MyEventHandler implements EventHandler<Event> {
		private final Set<String> result;
		private final String id;
		private final BusinessTransaction bt;
				
		public MyEventHandler(String id, Set<String> result, BusinessTransaction bt) {
			this.id = id;
			this.result = result;
			this.bt = bt;
		}
		
		@Override
		public void onEvent(Event event, long seq, boolean arg2)
				throws Exception {

			BusinessTransactionUtils.bindLocally(bt);
			
			final String testLine = id + "-" + event.foo + "-" + BusinessTransactionUtils.bound().toString();
			System.out.println(testLine);
			result.add(testLine);	
		}
	}
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void test() throws InterruptedException {
		Disruptor<Event> disruptor =
				  new Disruptor<Event>(new MyEventFactory(), Executors.newCachedThreadPool(),
				                            new SingleThreadedClaimStrategy(32),
				                            new SleepingWaitStrategy());
		
		final Set<String> result = new HashSet<String>(); 
		final BusinessTransaction bt = BusinessTransactionUtils.bound();
		bt.top();
		
		disruptor.handleEventsWith(new MyEventHandler("eh1", result, bt), new MyEventHandler("eh2", result, bt), new MyEventHandler("eh3", result, bt));
		
		RingBuffer<Event> ringBuffer = disruptor.start();
		
		long next = ringBuffer.next();
		Event event = ringBuffer.get(next);
		event.copy("one", 1);
		ringBuffer.publish(next);
		
		next = ringBuffer.next();
		event = ringBuffer.get(next);
		event.copy("two", 2);
		ringBuffer.publish(next);

		next = ringBuffer.next();
		event = ringBuffer.get(next);
		event.copy("three", 3);
		ringBuffer.publish(next);

		Thread.sleep(1000);
		
		assertTrue(bt.timeSinceBeginning() > 950);
		assertTrue(bt.popTopTimeEllapsed() > 950);
		bt.top();
		
		Thread.sleep(500);
		
		final long ellapsed = bt.popTopTimeEllapsed();
		
		assertTrue(ellapsed > 450);
		assertTrue(ellapsed < 650);
		
		disruptor.shutdown();
		
		final String foos[] = new String[] { "one", "two", "three" };
		final String ehs[] = new String[] {"eh1", "eh2", "eh3" };
		
		assertEquals(9, result.size());
		
		for(final String eh : ehs) {
			for(final String foo : foos) {
				assertTrue(result.contains(eh + "-" + foo + "-" + bt.toString()));
			}
		}
	}

}
