package com.emo.mango.cqs;

public interface CommandBus {
	
	public <O> void send(final O o);
}
