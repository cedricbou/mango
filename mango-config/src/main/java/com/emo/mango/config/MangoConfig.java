package com.emo.mango.config;

import javax.sql.DataSource;

import com.typesafe.config.Config;


public interface MangoConfig {
	
	public Config config();
		
	public void override(final Config config);
	
	public DataSource datasource();

	public DataSource datasource(String name);
}
