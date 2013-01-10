package com.emo.mango.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = 
				new ClassPathXmlApplicationContext("spring-test.xml");
		
		ctx.getBean("toto");
		
		for(final String name : ctx.getBeanDefinitionNames()) {
			System.out.println(" -> " + name);
		}

	}

}
