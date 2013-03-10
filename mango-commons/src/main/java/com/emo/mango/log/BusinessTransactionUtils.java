package com.emo.mango.log;

public class BusinessTransactionUtils {

	private static final ThreadLocal<BusinessTransaction> bt = new ThreadLocal<BusinessTransaction>();

	public static void bindNew() {
		bt.set(new BusinessTransaction());
	}
	
	public static void bindLocally(final BusinessTransaction parentBt) {
		bt.set(parentBt.copy());
	}

	public static boolean isBound() {
		return bt.get() != null;
	}

	public static BusinessTransaction bound() {
		if (!isBound()) {
			bindNew();
		}

		return bt.get();
	}
}
