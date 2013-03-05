package com.emo.mango.test.support;

import com.emo.mango.cqs.queries.annotations.QueryMap;

public class FooBar {
	public final String foo;
	public final String bar;
	
	public FooBar(final @QueryMap("p.foo") String foo, @QueryMap("p.bar") final String bar) {
		this.foo = foo;
		this.bar = bar;
	}

}
