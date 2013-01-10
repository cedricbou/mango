package com.emo.mango.cqs.dsl;

import com.emo.mango.cqs.CQSSystem;
import com.emo.mango.cqs.Command;

public class InCqsDsl {

	final CQSSystem system;
	
	protected InCqsDsl(final CQSSystem system) {
		this.system = system;
	}
	
	public <O> HandleCommandDsl<O> handle(final Command<O> cmd) {
		return new HandleCommandDsl<O>(system, cmd);
	}
}
