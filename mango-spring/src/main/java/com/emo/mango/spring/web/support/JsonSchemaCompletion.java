package com.emo.mango.spring.web.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonSchemaCompletion {
	private <T extends Annotation> T findFieldOrGetterAnnotation(final Class<?> type, final String fieldName, final Class<T> annotationClass) {
		if(fieldName == null) {
			return type.getAnnotation(annotationClass);
		}
		
		final T get = findMethodAnnotation(type, "get" + StringUtils.capitalize(fieldName), annotationClass);
		if(get == null) {
			final T is = findMethodAnnotation(type, "is" + StringUtils.capitalize(fieldName), annotationClass);
			if(is == null) {
				final T field = findFieldAnnotation(type, fieldName, annotationClass);
				if(field == null) {
					final Class<?> childType = findFieldOrGetterType(type, fieldName);
					if(childType != null) {
						return childType.getAnnotation(annotationClass);
					}
					else {
						return null;
					}
				}
				else {
					return field;
				}
			}
			else {
				return is;
			}
		}
		else {
			return get;
		}
	}

	private <T extends Annotation> T findFieldAnnotation(final Class<?> type, final String fieldName, final Class<T> annotationClass) {
		try {
			final Field field = type.getDeclaredField(fieldName);
			return field.getAnnotation(annotationClass);
		}
		catch(NoSuchFieldException e) {
			return null;
		}
	}

	private <T extends Annotation> T findMethodAnnotation(final Class<?> type, final String methodName, final Class<T> annotationClass) {
		try {
			final Method method = type.getMethod(methodName);
			return method.getAnnotation(annotationClass);
		}
		catch(NoSuchMethodException e1) {
			return null;
		}
	}

	
	private Class<?> findFieldOrGetterType(final Class<?> type, final String fieldName) {
		if(fieldName == null) {
			return null;
		}
		
		final Class<?> get = findMethod(type, "get" + StringUtils.capitalize(fieldName));
		if(get == null) {
			final Class<?> is = findMethod(type, "is" + StringUtils.capitalize(fieldName));
			if(is == null) {
				final Class<?> field = findField(type, fieldName);
				return field;
			}
			else {
				return is;
			}
		}
		else {
			return get;
		}
	}

	private Class<?> findField(final Class<?> type, final String fieldName) {
		try {
			final Field field = type.getDeclaredField(fieldName);
			return field.getType();
		}
		catch(NoSuchFieldException e) {
			return null;
		}
	}

	private Class<?> findMethod(final Class<?> type, final String methodName) {
		try {
			final Method method = type.getMethod(methodName);
			return method.getReturnType();
		}
		catch(NoSuchMethodException e1) {
			return null;
		}
	}

	
	public void schemaAddTitle(final String name, final JsonNode node,
			final Class<?> type) {
		
		final Doc doc = findFieldOrGetterAnnotation(type, name, Doc.class);
		final Class<?> foundtype = findFieldOrGetterType(type, name);
		final Class<?> subtype = (foundtype == null)?type:foundtype;
				
		if (node.isObject() && node.has("type")) {
			final ObjectNode obj = (ObjectNode) node;
						
			if (null != doc) {
				obj.put("description", doc.value());
			}

			if( null != name) {
				obj.put("title", name);
			}
		}

		Iterator<String> nodes = node.fieldNames();

		while (nodes.hasNext()) {
			final String childName = nodes.next();
			schemaAddTitle(childName, node.get(childName), subtype);
		}

	}

}
