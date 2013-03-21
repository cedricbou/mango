package com.emo.mango.log;


public abstract class Log {

	protected final LogConfiguration config;

	public Log(LogConfiguration config) {
		this.config = config;
		new LogConfigurer().configure(config);
	}
	
	public abstract void log(final String subject, final String action, final Object... args);

}
