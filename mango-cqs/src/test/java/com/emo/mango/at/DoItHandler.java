package com.emo.mango.at;

import com.emo.mango.cqs.Handler;

public abstract class DoItHandler implements Handler<DoIt> {

	@Override
	public abstract void handle(DoIt command);
}
