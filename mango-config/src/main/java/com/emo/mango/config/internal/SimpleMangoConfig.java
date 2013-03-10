package com.emo.mango.config.internal;

import javax.sql.DataSource;

import com.emo.mango.config.MangoConfig;
import com.emo.mango.log.Log;
import com.emo.mango.log.Loggers;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class SimpleMangoConfig implements MangoConfig {

	private Config config;
	
	private final String name;
	
	private final DataSourceExtension ds = new DataSourceExtension();
	
	private final LoggerExtension logs = new LoggerExtension();
	
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
		logs.onConfigurationChanged(this.config);
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
	
	@Override
	public Loggers loggers() {
		return logs.loggers();
	}
	
	@Override
	public Log logger(String name) {
		return logs.logger(name);
	}
}
