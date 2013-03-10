package com.emo.mango.log.params;


public class SimpleParamDumper extends ArgBasedParamDumper {

	@Override
	protected String argToString(Arg arg) {
		return arg.simple();
	}
}
