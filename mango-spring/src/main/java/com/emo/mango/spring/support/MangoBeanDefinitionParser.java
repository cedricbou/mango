package com.emo.mango.spring.support;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public abstract class MangoBeanDefinitionParser extends AbstractBeanDefinitionParser {

	@Override
	public final AbstractBeanDefinition parseInternal(Element element,
			ParserContext context) {
		
		final String userDefinedId;
		
		if(element.hasAttribute("id")) {
			userDefinedId = element.getAttribute("id");
		}
		else {
			userDefinedId = "";
		}
		
		return this.parseMango(userDefinedId, element, context);
	}
	

	public abstract AbstractBeanDefinition parseMango(final String userDefinedId, final Element element, final ParserContext context);

	protected void registerBeanDefinitionWithName(final BeanDefinitionBuilder builder, final String name, final ParserContext context) {
		final BeanDefinitionHolder holder = new BeanDefinitionHolder(
				builder.getBeanDefinition(), name);

		registerBeanDefinition(holder, context.getRegistry());
	}

	protected String getNameBasedOnClass(final BeanDefinitionBuilder builder) {
		return getNameBasedOnClass(builder.getRawBeanDefinition().getBeanClass());
	}
	
	protected String getNameBasedOnClass(final Class<?> clazz) {
		return StringUtils.uncapitalize(clazz.getSimpleName());
	}
	
	protected String getClassName(final BeanDefinitionBuilder builder) {
		return getClassName(builder.getRawBeanDefinition().getBeanClass());
	}
	
	protected String getClassName(final Class<?> clazz) {
		return clazz.getName();
	}
	
	protected void registerBeanDefinitionWithClass(final BeanDefinitionBuilder builder, final ParserContext context) {
		final String name = getClassName(builder);
		registerBeanDefinitionWithName(builder, name, context);
	}

	protected void registerBeanDefinitionWithNameBasedOnClass(final BeanDefinitionBuilder builder, final ParserContext context) {
		final String name = getNameBasedOnClass(builder);
		registerBeanDefinitionWithName(builder, name, context);
	}

	
	protected void registerBeanDefinitionWithNameFallbackToClass(final BeanDefinitionBuilder builder, final String name, final ParserContext context) {
		if(name != null && name.length() > 0) {
			registerBeanDefinitionWithName(builder, name, context);
		}
		else {
			registerBeanDefinitionWithClass(builder, context);
		}
	}
	
	protected void registerBeanDefinitionWithNameFallbackToNameBasedOnClass(final BeanDefinitionBuilder builder, final String name, final ParserContext context) {
		if(name != null && name.length() > 0) {
			registerBeanDefinitionWithName(builder, name, context);
		}
		else {
			registerBeanDefinitionWithNameBasedOnClass(builder, context);
		}
		
	}
	
	
	protected String getAttributeWithFallback(final Element element, final String attributeName, final String defaultValue) {
		final String attribute;
		
		if(element.hasAttribute(attributeName)) {
			attribute = element.getAttribute(attributeName);
		}
		else {
			attribute = defaultValue;
		}

		return attribute;
	}
	
}