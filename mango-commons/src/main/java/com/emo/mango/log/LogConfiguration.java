package com.emo.mango.log;

import org.slf4j.Logger;

import com.emo.mango.log.params.ParamDumper;

public class LogConfiguration {
	public final ParamDumper dumper;
	public final Logger logger;

	public LogConfiguration(final ParamDumper dumper, final Logger logger) {
		this.dumper = dumper;
		this.logger = logger;
	}

}
