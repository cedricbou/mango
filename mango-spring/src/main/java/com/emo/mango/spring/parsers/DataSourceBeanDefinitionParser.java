package com.emo.mango.spring.parsers;

import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.emo.mango.config.MangoConfig;
import com.emo.mango.config.datasource.SqlScriptInjector;
import com.emo.mango.spring.support.MangoBeanDefinitionParser;

public class DataSourceBeanDefinitionParser extends MangoBeanDefinitionParser {

	@Override
	public AbstractBeanDefinition parseMango(String userDefinedId,
			Element element, ParserContext context) {

		// Register datasource bean
		final BeanDefinitionBuilder datasourceBeanBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(DataSource.class);

		final String mangoConfigId = getAttributeWithFallback(element,
				"mango-config", getNameBasedOnClass(MangoConfig.class));

		datasourceBeanBuilder.getRawBeanDefinition().setFactoryBeanName(
				mangoConfigId);
		datasourceBeanBuilder.getRawBeanDefinition().setFactoryMethodName(
				"datasource");

		final String datasourceName = getAttributeWithFallback(element,
				"datasource", null);

		if (null != datasourceName) {
			final ConstructorArgumentValues values = new ConstructorArgumentValues();
			values.addGenericArgumentValue(datasourceName);
			datasourceBeanBuilder.getRawBeanDefinition()
					.setConstructorArgumentValues(values);
		}

		final NodeList nodes = element.getElementsByTagNameNS(
				element.getNamespaceURI(), "script");

		// FIXME : there is no guarantee of order for script executions. We
		// should implement this with a list somewhere in a bean.
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
