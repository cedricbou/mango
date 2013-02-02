package com.emo.mango.spring.parsers;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.emo.mango.spring.cqs.support.MangoCQS;
import com.emo.mango.spring.cqs.support.MangoCommandHandlerScanner;
import com.emo.mango.spring.query.support.MangoJpqlScanner;
import com.emo.mango.spring.query.support.MangoSqlScanner;
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
		final BeanDefinitionBuilder jpqlScannerBeanBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(MangoJpqlScanner.class);

		final BeanDefinitionBuilder sqlScannerBeanBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(MangoSqlScanner.class);

		registerBeanDefinitionWithNameBasedOnClass(handlerScannerBeanBuilder, context);

		registerBeanDefinitionWithNameBasedOnClass(jpqlScannerBeanBuilder, context);

		registerBeanDefinitionWithNameBasedOnClass(sqlScannerBeanBuilder, context);

		registerBeanDefinitionWithNameFallbackToNameBasedOnClass(cqsFactoryBeanBuilder, id, context);
		
		return null;
	}

}
