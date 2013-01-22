package com.emo.mango.test;

import net.sf.oval.constraint.Length;

public class FooBar {

	@Length(max=2)
	public final String foo;

	public final String bar;
	
	public FooBar(final String foo, final String bar) {
		this.foo = foo;
		this.bar = bar;
	}
	
	@Override
	public String toString() {
		return "foo: " + foo + "; bar: " + bar;
	}
}
