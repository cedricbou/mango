package com.emo.mango.config.internal;

import java.lang.reflect.Method;

import com.emo.mango.config.Extension;

public enum ExtensionFactories {
	DataSource("datasource");

	private final Method factory;

	private ExtensionFactories(final String factory) {
		try {
			this.factory = DefaultExtensionsImpl.class.getMethod(factory);
		} catch (Exception e) {
			throw new RuntimeException(
					"error when configuring ProvidedExtensions enum for factory "
							+ factory, e);
		}
	}

	protected Extension extension(final DefaultExtensionsImpl extensions) {
		try {
			return (Extension) factory.invoke(extensions);
		} catch (Exception e) {
			throw new RuntimeException(
					"error when calling ProvidedExtensions factory method "
							+ factory, e);
		}
	}
}