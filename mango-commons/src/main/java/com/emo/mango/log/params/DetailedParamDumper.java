package com.emo.mango.log.params;


public class DetailedParamDumper extends ArgBasedParamDumper {

	protected String argToString(Arg arg) {
		return arg.detailed();
	};
}
