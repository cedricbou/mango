package com.emo.mango.cqs.internal;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.emo.mango.cqs.Command;
import com.emo.mango.cqs.DuplicateException;
import com.emo.mango.cqs.Handler;

public class HandlerRepository {
	
	private final Map<Command<?>, Handler<?>> handlers = new HashMap<Command<?>, Handler<?>>();
	private final List<Command<?>> commands = new LinkedList<Command<?>>();
	private final Map<String, List<Command<?>>> commandByName = new HashMap<String, List<Command<?>>>();
	
	public <O> void addHandler(final Command<O> command, final Handler<O> handler) {
		if(null == command) {
			throw new IllegalArgumentException("unexpected null command");
		}

		if(null == handler) {
			throw new IllegalArgumentException("unexpected null handler");
		}
		
		if(handlers.containsKey(command)) {
			throw new IllegalArgumentException("a command with this name already exists. " + command);
		}
		
		handlers.put(command, handler);
		storeCommand(command);
	}
	
	private <O> void storeCommand(final Command<O> command) {
		commands.add(command);
		
		final String name = command.name.toLowerCase();
		
		if(!commandByName.containsKey(name)) {
			commandByName.put(name, new LinkedList<Command<?>>());
		}
		
		commandByName.get(name).add(command);
	}
	
	public Command<?>[] handledCommands() {
		return commands.toArray(new Command<?>[] {});
	}
	
	public Command<?> handledCommandByName(final String name) throws DuplicateException {
		if(!commandByName.containsKey(name.toLowerCase()) || commandByName.get(name.toLowerCase()).size() == 0) {
			throw new IllegalArgumentException("no command with this name " + name);
		}
		
		if(commandByName.get(name.toLowerCase()).size() > 1) {
			throw new DuplicateException("several commands with the same name exists, expected one. (" + name + ")");
		}
		
		return commandByName.get(name.toLowerCase()).get(0);
	}
	
	public <O> Handler<O> handlerFor(final Command<O> command) {
		if(null == command) {
			throw new IllegalArgumentException("unexpected null command");
		}
		
		final Handler<?> handler = handlers.get(command);
		
		if(null == handler) {
			throw new IllegalArgumentException("no handler found for command " + command);
		}
		
		return castHandlerWith(command, handler);
	}
	
	private <O> Handler<O> castHandlerWith(final Command<O> command, final Handler<?> handler) {
		try {
			@SuppressWarnings("unchecked")
			final Handler<O> mappedToHandler = (Handler<O>)handler;
			return mappedToHandler;
		}
		catch(Exception e) {
			throw new IllegalStateException("asked handler does not match type for command " + command);
		}
	}

}
