package com.emo.mango.config.internal;

import org.slf4j.LoggerFactory;

import com.emo.mango.log.Log;
import com.emo.mango.log.LogConfiguration;
import com.emo.mango.log.Loggers;
import com.emo.mango.log.params.ParamDumpFactory;
import com.emo.mango.log.params.ParamDumpType;
import com.typesafe.config.Config;

public class LoggerExtension {

	private Loggers loggers;

	public void onConfigurationChanged(Config loggersConfig) {
		final LogConfiguration access;
		final LogConfiguration chrono;
		
		if(loggersConfig.hasPath("loggers")) {
			final Config config = loggersConfig.getConfig("loggers");
			
			access = readConfig("access", config, "mango.log.access");
			chrono = readConfig("chrono", config, "mango.log.chrono");
			
			this.loggers = new Loggers(access, chrono);
		}
		else {
			this.loggers = new Loggers(defaultConfig("mango.log.access"), defaultConfig("mango.log.chrono"));
		}
	}

	private final LogConfiguration readConfig(final String path, final Config config, final String defaultLoggerName) {
		if (config.hasPath("access")) {
			return logConfig(config.getConfig("access"), "mango.log.access");
		} else {
			return defaultConfig(defaultLoggerName);
		}
	}

	private final LogConfiguration logConfig(final Config config,
			final String defaultLoggerName) {
		final String logger = config.hasPath("logger") ? config
				.getString("logger") : defaultLoggerName;
		final String dumper = config.hasPath("dumper") ? config
				.getString("dumper") : "Simple";

		final ParamDumpType type = ParamDumpType.valueOf(dumper);

		return new LogConfiguration(ParamDumpFactory.dumper(type),
				LoggerFactory.getLogger(logger));
	}

	private final LogConfiguration defaultConfig(final String defaultLoggerName) {
		return new LogConfiguration(
				ParamDumpFactory.dumper(ParamDumpType.Simple),
				LoggerFactory.getLogger(defaultLoggerName));
	}

	public Loggers loggers() {
		return loggers;
	}
	
	public Log logger(final String name) {
		return loggers.logger(name);
	}
}
