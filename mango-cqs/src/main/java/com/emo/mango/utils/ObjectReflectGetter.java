package com.emo.mango.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.util.StringUtils;

public class ObjectReflectGetter {

	private final Object o;
	
	public ObjectReflectGetter(final Object o) {
		this.o = o;
	}
	
	public Object get(final String propertyName) {
		return findFieldOrGetterType(propertyName);
	}
	
	private Object findFieldOrGetterType(final String fieldName) {
		if(fieldName == null) {
			return null;
		}
		
		final Object get = findMethod("get" + StringUtils.capitalize(fieldName));
		if(get == null) {
			final Object is = findMethod("is" + StringUtils.capitalize(fieldName));
			if(is == null) {
				final Object field = findField(fieldName);
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

	private Object findField(final String fieldName) {
		try {
			final Field field = o.getClass().getDeclaredField(fieldName);
			if(field == null) {
				return null;
			}
			
			return field.get(o);
		}
		catch(NoSuchFieldException e) {
			return null;
		} catch (IllegalArgumentException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		}
	}

	private Object findMethod(final String methodName) {
		try {
			final Method method = o.getClass().getMethod(methodName);
			if(method == null) {
				return null;
			}
			
			return method.invoke(o);
		}
		catch(NoSuchMethodException e1) {
			return null;
		} catch (IllegalArgumentException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		} catch (InvocationTargetException e) {
			return null;
		}
	}
}
