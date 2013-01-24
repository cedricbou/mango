package com.emo.mango.cqs;

import java.util.List;

public interface QueryExecutor<Q> {

	public String[] getParams();
	
	public List<Q> query(Object... params);
}
