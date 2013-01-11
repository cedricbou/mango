package com.emo.mango.spring.config;

import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.emo.mango.config.MangoConfig;
import com.emo.mango.config.datasource.SqlScriptInjector;

public class DataSourceBeanDefinitionParser extends
		AbstractBeanDefinitionParser {

	@Override
	public AbstractBeanDefinition parseInternal(Element element,
			ParserContext context) {

		// Register datasource bean
		final BeanDefinitionBuilder datasourceBeanBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(DataSource.class);
		datasourceBeanBuilder.getRawBeanDefinition().setFactoryBeanName(
				MangoConfig.class.getName());
		datasourceBeanBuilder.getRawBeanDefinition().setFactoryMethodName(
				"datasource");

		final String datasourceName = (element.hasAttribute("datasource") ? element
				.getAttribute("datasource") : null);

		if (null != datasourceName) {
			final ConstructorArgumentValues values = new ConstructorArgumentValues();
			values.addGenericArgumentValue(datasourceName);
			datasourceBeanBuilder.getRawBeanDefinition()
					.setConstructorArgumentValues(values);
		}

		final NodeList nodes = element.getElementsByTagNameNS(element.getNamespaceURI(), "script");

		if (null != nodes) {
			for (int i = 0; i < nodes.getLength(); ++i) {
				final Node node = nodes.item(i);
				// Todo : null check
				final String location = node.getAttributes()
						.getNamedItem("location").getTextContent();

				final BeanDefinitionBuilder scriptBeanBuilder = BeanDefinitionBuilder
						.genericBeanDefinition(SqlScriptInjector.class);

				final ConstructorArgumentValues values = new ConstructorArgumentValues();
				values.addIndexedArgumentValue(0,
						datasourceBeanBuilder.getBeanDefinition());
				values.addIndexedArgumentValue(1, location);

				scriptBeanBuilder.getRawBeanDefinition()
						.setConstructorArgumentValues(values);

				final BeanDefinitionHolder holder = new BeanDefinitionHolder(
						scriptBeanBuilder.getBeanDefinition(),
						SqlScriptInjector.class.getName() + "-"
								+ UUID.randomUUID().toString());
				registerBeanDefinition(holder, context.getRegistry());
			}
		}
		
		return datasourceBeanBuilder.getBeanDefinition();
	}

}
