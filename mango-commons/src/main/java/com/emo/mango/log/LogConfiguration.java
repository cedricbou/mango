package com.emo.mango.log;

import org.slf4j.Logger;

import com.emo.mango.log.params.ParamDumper;

public class LogConfiguration {
	public final ParamDumper dumper;
	public final Logger logger;
	public final String file;
	public final String fileNamePattern;
	
	public LogConfiguration(final ParamDumper dumper, final Logger logger, final String file, final String fileNamePattern) {
		this.dumper = dumper;
		this.logger = logger;
		this.file = file;
		this.fileNamePattern = fileNamePattern;
	}
}
