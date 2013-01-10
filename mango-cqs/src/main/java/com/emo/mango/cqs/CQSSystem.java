package com.emo.mango.cqs;

import com.emo.mango.cqs.internal.HandlerRepository;
import com.emo.mango.cqs.internal.InternalCommandBus;

public class CQSSystem {
	
	private final HandlerRepository repository = new HandlerRepository();
	
	private final CommandBus bus = new InternalCommandBus(repository);
	
	public <O> void declareHandler(final Command<O> cmd, final Handler<O> handler) {
		repository.addHandler(cmd, handler);
	}
	
	public CommandBus bus() {
		return bus;
	}
	
	public Command<?>[] commands() {
		return repository.handledCommands();
	}
	
	public Command<?> command(final String name) throws DuplicateCommandException {
		return repository.handledCommandByName(name);
	}
}
