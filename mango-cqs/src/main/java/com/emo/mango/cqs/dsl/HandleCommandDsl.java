package com.emo.mango.cqs.dsl;

import com.emo.mango.cqs.CQSSystem;
import com.emo.mango.cqs.Command;
import com.emo.mango.cqs.Handler;

public class HandleCommandDsl<O> {

	private final CQSSystem system;
	private final Command<O> cmd;
	
	protected HandleCommandDsl(final CQSSystem system, final Command<O> cmd) {
		this.system = system;
		this.cmd = cmd;
	}
	
	public void with(final Handler<O> handler) {
		system.declareHandler(cmd, handler);
	}
}
