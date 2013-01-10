package com.emo.mango.at;

public class DoIt {

	public final String code;
	public final int number;
	
	public DoIt(final String code, final int number) {
		this.code = code;
		this.number = number;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof DoIt) {
			final DoIt d = (DoIt)o;
			return this.number == d.number && this.code.equals(d.code);
		}
		return false;
	}
}
