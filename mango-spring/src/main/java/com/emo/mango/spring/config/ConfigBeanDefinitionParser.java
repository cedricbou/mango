package com.emo.mango.spring.config;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.emo.mango.config.DefaultExtensions;
import com.emo.mango.config.MangoConfig;
import com.emo.mango.config.MangoConfigFactory;

public class ConfigBeanDefinitionParser extends AbstractBeanDefinitionParser {

	public static String EXTENSION_BEAN_NAME = DefaultExtensions.class.getName();

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

		BeanDefinitionBuilder extensionsBeanBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(DefaultExtensions.class);
		extensionsBeanBuilder.getRawBeanDefinition().setFactoryBeanName(configHolder.getBeanName());
		extensionsBeanBuilder.setFactoryMethod("ext");

		final BeanDefinitionHolder extensionHolder = new BeanDefinitionHolder(extensionsBeanBuilder.getBeanDefinition(), EXTENSION_BEAN_NAME);
				
		registerBeanDefinition(extensionHolder, context.getRegistry());
		
		return null;
	}
}
