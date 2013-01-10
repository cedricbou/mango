package com.emo.mango.spring.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class ConfigNamespaceHandler extends NamespaceHandlerSupport {

	public void init() {
		registerBeanDefinitionParser("config",
				new ConfigBeanDefinitionParser());

		registerBeanDefinitionParser("datasource",
				new DataSourceBeanDefinitionParser());
	}

}
