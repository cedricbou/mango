package com.emo.mango.cqs.queries.executors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import com.emo.mango.cqs.queries.annotations.QueryBind;
import com.google.common.base.Preconditions;

public class BindingMethodParser {

	public final Binding[] bindings;
	
	public BindingMethodParser(final Method m) {
		final List<Binding> bindings = new LinkedList<Binding>();
		
		for(final Annotation[] annots :  m.getParameterAnnotations()) {
			for(final Annotation annot : annots) {
				if(QueryBind.class.isInstance(annot)) {
					bindings.add(new Binding(((QueryBind)annot).value()));
				}
			}
		}
		
		this.bindings = bindings.toArray(new Binding[] {});
		
		Preconditions.checkState(m.getParameterTypes().length == bindings.size(), "all method parameters should be annotated with @QueryBind : " + m);
	}
}
