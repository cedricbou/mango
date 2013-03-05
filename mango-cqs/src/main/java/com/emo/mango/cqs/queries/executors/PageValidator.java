package com.emo.mango.cqs.queries.executors;

import com.google.common.base.Preconditions;

public class PageValidator {

	public static void assertValidPaging(int page, int elementsPerPage) {
		Preconditions.checkArgument(page > 0, "page start at page 1");
		Preconditions.checkArgument(elementsPerPage > 0, "at least 1 element is required per page");
	}
	
	public static int limit(int page, int elementsPerPage) {
		return elementsPerPage;
	}
	
	public static int offset(int page, int elementsPerPage) {
		return ((page - 1) * elementsPerPage);
	}

	public static int pageNumber(long countItems, int elementsParPage) {
		return (int)(countItems / elementsParPage) + ((countItems % elementsParPage > 0)?1:0);
	}
}
