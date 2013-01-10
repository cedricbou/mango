package com.emo.skeleton.framework;

import org.springframework.stereotype.Service;

import com.emo.mango.cqs.CQSSystem;
import com.emo.mango.cqs.CommandBus;
import com.emo.mango.cqs.dsl.CQS;
import com.emo.mango.cqs.dsl.InCqsDsl;

@Service
public class CQSFactory {

	private final CQSSystem system = new CQSSystem();
	
	public CQSFactory() {
		
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
