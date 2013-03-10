package com.emo.mango.log;

import java.util.HashMap;
import java.util.Map;


public class Loggers {

	private final Map<String, Log> loggers = new HashMap<String, Log>();
	
	public Loggers(final LogConfiguration access, final LogConfiguration chronos) {
		newLogger("access", new AccessLog(access));
		newLogger("chrono", new ChronoLog(chronos));
	}
	
	public boolean hasLogger(final String name) {
		return loggers.containsKey(name);
	}
	
	public void newLogger(final String name, final LogConfiguration config) {
		newLogger(name, new AppLog(config));
	}
	
	public void newLogger(final String name, final Log log) {
		loggers.put(name, log);
	}
	
	public Log logger(final String name) {
		return loggers.get(name);
	}
	
	public Log access() {
		return logger("access");
	}
	
	public Log chrono() {
		return logger("chrono");
	}
	
}
