package com.emo.mango.config.internal.ds;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ConnectionInvocationHandler implements InvocationHandler {

	public static class Operation {
		
	}
	
	private static class WrappedOperationResult {
		private Object result;
		
		public WrappedOperationResult(final Object result) {
			this.result = result;
		}
		
		public Object underlying() {
			return result;
		}
	}
	
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		if(proxy instanceof WrappedOperationResult) {
			return method.invoke(((WrappedOperationResult)proxy).underlying(), args);
		}

		
		if(method.getName().startsWith("prepare") || method.getName().startsWith("create")) {
		}
		
		return null;
	}
	
}
