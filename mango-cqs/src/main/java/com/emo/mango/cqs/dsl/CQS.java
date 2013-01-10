package com.emo.mango.cqs.dsl;

import com.emo.mango.cqs.CQSSystem;

public class CQS {
	public static InCqsDsl in(final CQSSystem system) {
		return new InCqsDsl(system);
	}
}
