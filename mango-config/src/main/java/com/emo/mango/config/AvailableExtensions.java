package com.emo.mango.config;

import com.emo.mango.config.internal.ExtensionFactories;

public enum AvailableExtensions {
	DataSource(ExtensionFactories.DataSource);
	
	private ExtensionFactories factory;
	
	private AvailableExtensions(final ExtensionFactories factory) {
		this.factory = factory;
	}
	
	public ExtensionFactories factory() {
		return factory;
	}
}
