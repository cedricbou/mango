package com.emo.mango.spring.parsers;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.emo.mango.config.MangoConfig;
import com.emo.mango.config.MangoConfigs;
import com.emo.mango.spring.support.MangoBeanDefinitionParser;

public class ConfigBeanDefinitionParser extends MangoBeanDefinitionParser {

	@Override
	public AbstractBeanDefinition parseMango(String userDefinedId,
			Element element, ParserContext context) {

		final BeanDefinitionBuilder configFactoryBeanBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(MangoConfigs.class);
		configFactoryBeanBuilder.setFactoryMethod("singleton");

		registerBeanDefinitionWithNameBasedOnClass(configFactoryBeanBuilder, context);
		
		BeanDefinitionBuilder configBeanBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(MangoConfig.class);

		configBeanBuilder.getRawBeanDefinition().setFactoryBeanName(
				getNameBasedOnClass(configFactoryBeanBuilder));
		configBeanBuilder.getRawBeanDefinition().setFactoryMethodName("get");

		registerBeanDefinitionWithNameFallbackToNameBasedOnClass(configBeanBuilder, userDefinedId, context);
		
		return null;
	}
	
}
