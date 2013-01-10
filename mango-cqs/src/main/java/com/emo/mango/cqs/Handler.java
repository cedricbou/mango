package com.emo.mango.cqs;

public interface Handler<C> {

	public void handle(final C cmd);
}
