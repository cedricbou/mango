package com.emo.mango.log;

public class AccessLog extends Log {

	public AccessLog(LogConfiguration config) {
		super(config);
	}

	@Override
	public void log(String subject, String action, Object... args) {
		config.logger.info("{{}} {}.{} {}", BusinessTransactionUtils.bound().code, subject, action,
				config.dumper.dump(args));
	}
}
