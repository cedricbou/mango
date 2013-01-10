package com.emo.handlers;

import org.springframework.stereotype.Component;

import com.emo.commands.Placebo;
import com.emo.mango.cqs.Handler;
import com.emo.skeleton.annotations.CommandHandler;

@CommandHandler(Placebo.class)
@Component
public class PlaceboHandler implements Handler<Placebo> {
 
	public void handle(Placebo command) {};
}
