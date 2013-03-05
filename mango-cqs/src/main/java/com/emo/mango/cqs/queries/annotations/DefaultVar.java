package com.emo.mango.cqs.queries.annotations;

public class DefaultVar implements Var {

	@Override
	public Object evaluate(Object passedIn) {
		return passedIn;
	}
}
