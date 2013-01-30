package com.emo.mango.config;

import java.util.HashMap;
import java.util.Map;

import com.emo.mango.config.internal.SimpleMangoConfig;

public class MangoConfigs {

	private static MangoConfigs singleton;

	public final static MangoConfigs singleton() {
		if(singleton == null) {
			singleton = new MangoConfigs();
		}
		
		return singleton;
	}
	
	private final Map<String, SimpleMangoConfig> configs = new HashMap<String, SimpleMangoConfig>();
	
	private final SimpleMangoConfig initAndGet(final String name) {
		if(!configs.containsKey(name)) {
			configs.put(name, new SimpleMangoConfig(name));
		}
		return configs.get(name);
	}
	
	public final MangoConfig get() {
		return initAndGet("default");
	}
	
	public final MangoConfig get(final String name) {
		return initAndGet(name);
	}
	
	public final void reload(final String name) {
		initAndGet(name).reload();
	}
}
