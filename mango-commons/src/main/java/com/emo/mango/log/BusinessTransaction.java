package com.emo.mango.log;

import java.util.Stack;
import java.util.UUID;

public class BusinessTransaction {
	public final String code;
	private final long started;
	
	private final Stack<Long> topped;
	
	private final BusinessTransaction parent;
	
	
	protected BusinessTransaction() {
		this(UUID.randomUUID().toString(), System.currentTimeMillis(), null);
	}
	
	protected BusinessTransaction(String code, long started, final BusinessTransaction parent) {
		this.code = code;
		this.started = started;
		this.parent = parent;
		this.topped = new Stack<Long>();
	}

	public final BusinessTransaction copy() {
		return new BusinessTransaction(code, started, this);
	}

	public final void top() {
		topped.add(System.currentTimeMillis());
	}
	
	public final long popTopTimeEllapsed() {
		return System.currentTimeMillis() - topped.pop();
	}
	
	public final long timeSinceBeginning() {
		return System.currentTimeMillis() - started;
	}
	
	@Override
	public final String toString() {
		return "{" + code + "} started at " + started;
	}
}