package com.emo.mango.log.params;

class SimpleArg extends Arg {
	final String name;
	final String value;

	public SimpleArg(final String name, final String value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public String detailed() {
		return name + "(" + value + ")";
	}

	@Override
	public String simple() {
		return value;
	}
}