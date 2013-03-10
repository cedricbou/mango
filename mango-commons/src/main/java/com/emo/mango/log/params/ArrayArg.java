package com.emo.mango.log.params;

import com.google.common.base.Joiner;

class ArrayArg extends Arg {
	final Arg[] args;
	final String name;

	public ArrayArg(final String name, final String type, final Arg... args) {
		this.args = args;
		this.name = name + ":" + type;
	}

	public ArrayArg(final String name, final Arg... args) {
		this.args = args;
		this.name = name;
	}

	@Override
	public String detailed() {
		final String[] items = new String[args.length];

		int i = 0;
		for (final Arg arg : args) {
			if (arg != null) {
				items[i] = arg.detailed();
			}
			else {
				items[i] = "null";
			}
			++i;
		}

		if (args.length == 0) {
			return name;
		}

		return name + "[" + Joiner.on(", ").join(items) + "]";
	}

	@Override
	public String simple() {
		final String[] items = new String[args.length];

		int i = 0;
		for (final Arg arg : args) {
			if (arg != null) {
				items[i] = arg.simple();
			}
			else {
				items[i] = "null";
			}
			
			++i;
		}

		if (args.length == 0) {
			return name;
		}

		return name + "[" + Joiner.on(", ").join(items) + "]";
	}
}