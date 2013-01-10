package com.emo.mango.config;

import com.typesafe.config.Config;


public interface MangoConfig {
	
	public Config config();
	
	public void newExtension(final Extension ext);
	
	public DefaultExtensions ext();
	
	public void override(final Config config);
}
