package com.emo.mango.at;

import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.emo.mango.cqs.CQSSystem;
import com.emo.mango.cqs.Command;
import com.emo.mango.cqs.CommandBus;
import com.emo.mango.cqs.dsl.CQS;

public class UseCommandTest {

	private final CQSSystem system = new CQSSystem();
	private final CommandBus bus = system.bus();

	private DoItHandler handler; 
	
	@Before
	public void setUp() {
		handler = mock(DoItHandler.class);
		CQS.in(system).handle(new Command<DoIt>(DoIt.class)).with(handler);
	}
	
	@Test
	public void dispatchCommandTest() {
		final DoIt command = new DoIt("aaaabbbb", 34);
		bus.send(command);
		
		Mockito.verify(handler).handle(command);
	}
	
}
