package com.emo.mango.spring.cqs.support;

import com.emo.mango.cqs.CQSSystem;
import com.emo.mango.cqs.CommandBus;
import com.emo.mango.cqs.dsl.CQS;
import com.emo.mango.cqs.dsl.InCqsDsl;

public class MangoCQS {

	private final CQSSystem system = new CQSSystem();
	
	public MangoCQS() {
		
	}
	
	public InCqsDsl dsl() {
		return CQS.in(system);
	}
	
	public CommandBus bus() {
		return system.bus();
	}
		
	public CQSSystem system() {
		return system;
	}
}
