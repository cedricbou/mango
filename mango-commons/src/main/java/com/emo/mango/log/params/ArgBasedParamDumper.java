package com.emo.mango.log.params;

import com.google.common.base.Joiner;

abstract class ArgBasedParamDumper implements ParamDumper {

	protected abstract String argToString(final Arg arg);
	
	@Override
	public String dump(final Object... args) {
		final String[] dumps = new String[args.length];
		
		int i = 0;
		for(final Object arg : args) {
			dumps[i++] = argToString(Arg.buildArg("", arg));
		}
		
		return Joiner.on(" ").join(dumps);
	};
}
