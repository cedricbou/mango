package com.emo.mango.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.emo.mango.spring.cqs.support.MangoCQS;
import com.emo.mango.test.FooBar;

public class App {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = 
				new ClassPathXmlApplicationContext("spring-test.xml");
				
		for(final String name : ctx.getBeanDefinitionNames()) {
			System.out.println(" -> " + name);
		}
		
		MangoCQS cqs = (MangoCQS)ctx.getBean(MangoCQS.class);
		cqs.bus().send(new FooBar("hey", "ho"));
	}

}
