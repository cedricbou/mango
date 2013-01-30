package com.emo.mango.config.internal;

import javax.sql.DataSource;

import com.emo.mango.config.MangoConfig;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class SimpleMangoConfig implements MangoConfig {

	private Config config;
	
	private final String name;
	
	private final DataSourceExtension ds = new DataSourceExtension();
	
	public SimpleMangoConfig(final String name) {
		this.name = name;
		reload();
	}
	
	public void reload() {
		final Config orig = ConfigFactory.load();
		
		if(!name.equalsIgnoreCase("default")) {
			this.config = orig
                .getConfig("name")
                .withFallback(orig);
		}
		else {
			this.config = orig;
		}
		
		ds.onConfigurationChanged(this.config);
	}

	@Override
	public Config config() {
		return config;
	}

	@Override
	public void override(Config config) {
		this.config = config.withFallback(this.config);	
	}

	@Override
	public DataSource datasource() {
		return ds.datasource();
	}

	@Override
	public DataSource datasource(String name) {
		return ds.datasource(name);
	}
}
