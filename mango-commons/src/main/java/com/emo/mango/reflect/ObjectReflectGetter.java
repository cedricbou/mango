package com.emo.mango.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.google.common.base.CaseFormat;
import com.google.common.base.Optional;

public class ObjectReflectGetter {

	private final Object o;

	public ObjectReflectGetter(final Object o) {
		this.o = o;
	}

	public Optional<Object> get(final String propertyName) {
		return findFieldOrGetterType(propertyName);
	}

	private Optional<Object> findFieldOrGetterType(final String fieldName) {
		if (fieldName == null) {
			return Optional.absent();
		}

		final String capitalized = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, fieldName);
		
		final Optional<Object> get = findMethod("get" + capitalized);
		if (!get.isPresent()) {
			final Optional<Object> is = findMethod("is" + capitalized);
			if (!is.isPresent()) {
				final Optional<Object> field = findField(fieldName);
				return field;
			} else {
				return is;
			}
		} else {
			return get;
		}
	}

	private Optional<Object> findField(final String fieldName) {
		try {
			final Field field = o.getClass().getDeclaredField(fieldName);
			
			if (field == null) {
				return Optional.absent();
			}

			return Optional.of(field.get(o));
		} catch (NoSuchFieldException e) {
			return Optional.absent();
		} catch (IllegalArgumentException e) {
			return Optional.absent();
		} catch (IllegalAccessException e) {
			return Optional.absent();
		}
	}

	private Optional<Object> findMethod(final String methodName) {
		try {
			final Method method = o.getClass().getMethod(methodName);
			
			if (method == null) {
				return Optional.absent();
			}

			return Optional.of(method.invoke(o));
		} catch (NoSuchMethodException e1) {
			return Optional.absent();
		} catch (IllegalArgumentException e) {
			return Optional.absent();
		} catch (IllegalAccessException e) {
			return Optional.absent();
		} catch (InvocationTargetException e) {
			return Optional.absent();
		}
	}
}
