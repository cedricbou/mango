package com.emo.mango.test;

import com.emo.mango.cqs.Handler;
import com.emo.mango.spring.cqs.annotations.CommandHandler;

@CommandHandler(command = FooBar.class)
public class FooBarHandler implements Handler<FooBar> {

	@Override
	public void handle(FooBar cmd) {
		System.out.println(cmd);
	}
}
