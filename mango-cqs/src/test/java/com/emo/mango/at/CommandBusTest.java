package com.emo.mango.at;

import org.junit.Test;
import org.mockito.Mockito;

import com.emo.mango.cqs.Command;
import com.emo.mango.cqs.internal.HandlerRepository;
import com.emo.mango.cqs.internal.InternalCommandBus;

public class CommandBusTest {

	@Test
	public void sendCommand() {
		final HandlerRepository repo = new HandlerRepository();
		final InternalCommandBus bus = new InternalCommandBus(repo);
				
		final DoItHandler handler = Mockito.mock(DoItHandler.class);
		
		final Command<DoIt> command = new Command<DoIt>(DoIt.class);
		
		repo.addHandler(command, handler);
		
		bus.send(new DoIt("123", 44));
		
		Mockito.verify(handler).handle(new DoIt("123", 44));
	}
}
