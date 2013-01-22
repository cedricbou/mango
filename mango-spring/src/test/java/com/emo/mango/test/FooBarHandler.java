package com.emo.mango.test;

import org.springframework.stereotype.Component;

import com.emo.mango.cqs.Handler;
import com.emo.mango.spring.cqs.annotations.CommandHandler;

@Component
@CommandHandler(FooBar.class)
public class FooBarHandler implements Handler<FooBar> {

	@Override
	public void handle(FooBar cmd) {
		System.out.println(cmd);
	}
}
