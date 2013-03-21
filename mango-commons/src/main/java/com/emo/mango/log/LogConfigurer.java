package com.emo.mango.log;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;

public class LogConfigurer {

	public void configure(final LogConfiguration config) {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory
				.getILoggerFactory();

		Logger logbackLogger = loggerContext.getLogger(config.logger.getName());
		logbackLogger.detachAndStopAllAppenders();

		RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender<ILoggingEvent>();
		fileAppender.setContext(loggerContext);
		fileAppender.setName("mango-rolling-file-appender");
		fileAppender.setFile(null);
		
		PatternLayoutEncoder encoder = new PatternLayoutEncoder();
		encoder.setContext(loggerContext);
		encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{12} - %msg%n");
		encoder.start();
		
		fileAppender.setEncoder(encoder);
		
		final TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<ILoggingEvent>();
		rollingPolicy.setContext(loggerContext);
		rollingPolicy.setParent(fileAppender);
		rollingPolicy.setFileNamePattern(config.fileNamePattern);
		rollingPolicy.start();

		fileAppender.setRollingPolicy(rollingPolicy);
		fileAppender.setEncoder(encoder);
		fileAppender.start();

		logbackLogger.setAdditive(false);

		// attach the rolling file appender to the logger of your choice
		logbackLogger.addAppender(fileAppender);
	}
}
