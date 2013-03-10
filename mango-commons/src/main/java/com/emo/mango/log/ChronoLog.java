package com.emo.mango.log;

public class ChronoLog extends Log {

	public ChronoLog(LogConfiguration config) {
		super(config);
	}

	@Override
	public void log(String subject, String action, Object... args) {
		final BusinessTransaction bt = BusinessTransactionUtils.bound();
		
		config.logger.info("{{}} ({} ms) {}", bt.code,
				bt.timeSinceBeginning(), config.dumper.dump(args));
	}
}
