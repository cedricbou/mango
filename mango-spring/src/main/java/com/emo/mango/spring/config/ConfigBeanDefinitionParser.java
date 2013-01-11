package com.emo.mango.spring.config;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.emo.mango.config.MangoConfig;
import com.emo.mango.config.MangoConfigFactory;

public class ConfigBeanDefinitionParser extends AbstractBeanDefinitionParser {

	@Override
	public AbstractBeanDefinition parseInternal(Element element,
			ParserContext context) {
		final BeanDefinitionBuilder configFactoryBeanBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(MangoConfigFactory.class);

		final BeanDefinitionHolder factoryHolder = new BeanDefinitionHolder(
				configFactoryBeanBuilder.getBeanDefinition(),
				MangoConfigFactory.class.getName());
		registerBeanDefinition(factoryHolder, context.getRegistry());

		BeanDefinitionBuilder configBeanBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(MangoConfig.class);

		configBeanBuilder.getRawBeanDefinition().setFactoryBeanName(
				factoryHolder.getBeanName());
		configBeanBuilder.getRawBeanDefinition().setFactoryMethodName("config");

		final BeanDefinitionHolder configHolder = new BeanDefinitionHolder(
				configBeanBuilder.getBeanDefinition(),
				MangoConfig.class.getName());
		registerBeanDefinition(configHolder, context.getRegistry());
				
		return null;
	}
}
