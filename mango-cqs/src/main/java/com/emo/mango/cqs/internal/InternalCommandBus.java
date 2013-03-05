package com.emo.mango.cqs.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emo.mango.cqs.Command;
import com.emo.mango.cqs.CommandBus;
import com.emo.mango.cqs.Handler;

public class InternalCommandBus implements CommandBus {
	
	private final HandlerRepository repo;
	
	public InternalCommandBus(final HandlerRepository repository) {
		this.repo = repository;
	}
	
	public <O> void send(final O o) {
		@SuppressWarnings("unchecked")
		final Command<O> command = new Command<O>((Class<O>)o.getClass());
		
		send(o, command);
	}
	
	private final Logger LOG = LoggerFactory.getLogger(InternalCommandBus.class);
	
	public <O> void send(final O o, final Command<O> command) {
		LOG.debug("will handle command {}", o);
		
		final Handler<O> handler = repo.handlerFor(command);
		handler.handle(o);
	}
}
