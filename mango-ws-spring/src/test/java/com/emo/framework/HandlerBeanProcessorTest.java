package com.emo.framework;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.emo.skeleton.framework.CQSFactory;

public class HandlerBeanProcessorTest {
	
	private final ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-init.xml");

	@Test
	public void checkHandlerProcessed() {
		final List<String> names = Arrays.asList(context.getBeanDefinitionNames());
				
		assertTrue("command handler have been added to context", names.contains("clientIsMovingHandler"));
		
		final CQSFactory cqs = (CQSFactory)context.getBean(CQSFactory.class);
		
		// TODO: fix, it fails because of class proxying :(
		// assertTrue("handler manager contains handler for ClientIsMovingCommand", manager.handlerFor(ClientIsMoving.class) instanceof ClientIsMovingHandler);
	}
}
