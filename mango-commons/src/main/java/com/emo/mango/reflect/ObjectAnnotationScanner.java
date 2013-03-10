package com.emo.mango.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import com.emo.mango.lang.Empty;
import com.google.common.base.CaseFormat;

public class ObjectAnnotationScanner {

	private final Class<? extends Annotation> a;

	public ObjectAnnotationScanner(final Class<? extends Annotation> a) {
		this.a = a;
	}

	/**
	 * return a name usable with ObjectReflectGetter for all methods or field
	 * annotated.
	 * 
	 */
	public String[] scan(final Object o) {
		final List<String> props = new LinkedList<String>();
		
		for(final Field field : o.getClass().getFields()) {
			if(field.isAnnotationPresent(a)) {
				props.add(field.getName());
			}
		}
		
		for(final Method method : o.getClass().getMethods()) {
			if(method.isAnnotationPresent(a)) {
				if(method.getName().startsWith("get")) {
					props.add(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, method.getName().replaceFirst("get", "")));
				}
				else if(method.getName().startsWith("is")) {
					props.add(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, method.getName().replaceFirst("is", "")));
				}
				else {
					props.add(method.getName());
				}
			}
		}
		
		return props.toArray(Empty.STRING);
	}
}
