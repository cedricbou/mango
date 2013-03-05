package com.emo.mango.cqs.queries.executors;

import com.emo.mango.cqs.queries.annotations.Var;

class Binding {
	public final String value;
	public final Var var;

	public Binding(final String value) {
		this(value, null);
	}

	
	public Binding(final String value, final Var var) {
		this.value = value;
		this.var = var;
	}
		
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Binding
			&& value.equals(((Binding)obj).value);
	}
}
