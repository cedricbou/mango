package com.emo.mango.cqs.queries.executors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;

import com.emo.mango.cqs.queries.annotations.QueryMap;

class ViewClassParser {

	public final Class<?> view;

	public final Constructor<?> constructor;

	public final Class<?>[] types;

	public final String[] mappedFields;

	public ViewClassParser(Class<?> view) {
		this.view = view;
		
		final Constructor<?>[] constrs = view.getConstructors();

		if (constrs.length > 1) {
			throw new IllegalArgumentException(
					"the query view object should have only one constructor : "
							+ view);
		}

		final Constructor<?> constr = constrs[0];

		final Annotation[][] annotOnParams = constr.getParameterAnnotations();

		final List<String> selectFields = new LinkedList<String>();

		for (final Annotation[] annots : annotOnParams) {
			for (final Annotation annot : annots) {
				if (QueryMap.class.isInstance(annot)) {
					selectFields.add(((QueryMap) annot).value());
					break;
				}
			}
		}

		if (selectFields.size() != constr.getParameterTypes().length) {
			throw new IllegalArgumentException(
					"all constructor parameter must be annoted with the @QueryMap annotation : "
							+ view);
		}

		this.mappedFields = selectFields.toArray(new String[] {});
		this.constructor = constr;
		this.types = constr.getParameterTypes();
	}

}
