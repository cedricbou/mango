package com.emo.mango.log;

public class AppLog extends Log {

	public AppLog(LogConfiguration config) {
		super(config);
	}

	@Override
	public void log(String subject, String action, Object... args) {
		final BusinessTransaction bt = BusinessTransactionUtils.bound();
		
		config.logger.info("{{}} {}", bt.code, config.dumper.dump(args));
	}
}
