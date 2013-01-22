package com.emo.mango.spring.cqs.support;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.emo.mango.cqs.Command;
import com.emo.mango.cqs.Handler;
import com.emo.mango.spring.cqs.annotations.CommandHandler;

public class MangoCommandHandlerScanner implements ApplicationContextAware,
		InitializingBean {
	private ApplicationContext applicationContext;

	@Inject
	private MangoCQS cqs;

	@Override
	public void afterPropertiesSet() throws Exception {
		scanForCommandHandlers();
	}

	private <O> void declareCommand(final Class<O> command, final Handler<O> handler) {
		cqs.dsl().handle(new Command<O>(command)).with(handler);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void scanForCommandHandlers() {
		final Map<String, Object> myHandlers = applicationContext
				.getBeansWithAnnotation(CommandHandler.class);

		for (final String beanName : myHandlers.keySet()) {
			final CommandHandler annotation = applicationContext
					.findAnnotationOnBean(beanName, CommandHandler.class);

			final Object myHandler = myHandlers.get(beanName);

			if (myHandler instanceof Handler<?>) {
				declareCommand(annotation.value(), (Handler)myHandler);
			}
			else {
				// TODO: add warning or error if CustomView not implementing ViewExecutor.
			}
		}
	}

	@Override
	public void setApplicationContext(
			final ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
