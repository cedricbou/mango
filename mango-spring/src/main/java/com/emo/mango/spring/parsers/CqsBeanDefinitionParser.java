package com.emo.mango.spring.parsers;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.emo.mango.spring.cqs.support.MangoCommandHandlerScanner;
import com.emo.mango.spring.cqs.support.MangoCQS;
import com.emo.mango.spring.jpa.support.MangoQueryWithJpaScanner;
import com.emo.mango.spring.support.MangoBeanDefinitionParser;

public class CqsBeanDefinitionParser extends MangoBeanDefinitionParser {

	@Override
	public AbstractBeanDefinition parseMango(String id, Element element,
			ParserContext context) {

		final BeanDefinitionBuilder cqsFactoryBeanBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(MangoCQS.class);

		final BeanDefinitionBuilder handlerScannerBeanBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(MangoCommandHandlerScanner.class);

		// TODO : make this optional with something like <mango:cqs with-jpa="false" />
		final BeanDefinitionBuilder queryWithJpaScannerBeanBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(MangoQueryWithJpaScanner.class);

		registerBeanDefinitionWithNameBasedOnClass(handlerScannerBeanBuilder, context);

		registerBeanDefinitionWithNameBasedOnClass(queryWithJpaScannerBeanBuilder, context);

		registerBeanDefinitionWithNameFallbackToNameBasedOnClass(cqsFactoryBeanBuilder, id, context);
		
		return null;
	}

}
