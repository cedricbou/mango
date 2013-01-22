package com.emo.mango.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import com.emo.mango.spring.parsers.ConfigBeanDefinitionParser;
import com.emo.mango.spring.parsers.CqsBeanDefinitionParser;
import com.emo.mango.spring.parsers.DataSourceBeanDefinitionParser;
import com.emo.mango.spring.parsers.ValidatorBeanDefinitionParser;
import com.emo.mango.spring.parsers.WebBeanDefinitionParser;

public class NamespaceHandler extends NamespaceHandlerSupport {

	public void init() {
		registerBeanDefinitionParser("config", new ConfigBeanDefinitionParser());

		registerBeanDefinitionParser("datasource",
				new DataSourceBeanDefinitionParser());

		registerBeanDefinitionParser("cqs", new CqsBeanDefinitionParser());

		registerBeanDefinitionParser("web", new WebBeanDefinitionParser());

		registerBeanDefinitionParser("validator", new ValidatorBeanDefinitionParser());
	}

}
