package com.emo.mango.spring.query.support;

import org.springframework.core.type.filter.AssignableTypeFilter;

import com.emo.mango.cqs.Queries;

class QueriesInterfaceScanner extends ComponentClassScanner {

	public QueriesInterfaceScanner() {
		super();
		addIncludeFilter( new AssignableTypeFilter(Queries.class));
	}
}
