package com.emo.mango.spring.parsers;

import java.util.UUID;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.emo.mango.spring.cqs.support.MangoCQS;
import com.emo.mango.spring.cqs.support.MangoCommandHandlerScanner;
import com.emo.mango.spring.query.support.MangoQueryScanner;
import com.emo.mango.spring.support.MangoBeanDefinitionParser;

public class CqsBeanDefinitionParser extends MangoBeanDefinitionParser {

	@Override
	public AbstractBeanDefinition parseMango(String id, Element element,
			ParserContext context) {

		final BeanDefinitionBuilder cqsFactoryBeanBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(MangoCQS.class);

		final BeanDefinitionBuilder handlerScannerBeanBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(MangoCommandHandlerScanner.class);

		final NodeList nodes = element.getElementsByTagNameNS(
				element.getNamespaceURI(), "query-scan");

		if (null != nodes) {
			for (int i = 0; i < nodes.getLength(); ++i) {
				final Node node = nodes.item(i);

				if (node != null) {
					final String packageToScan = node.getAttributes()
							.getNamedItem("package").getTextContent();

					final String beanDataSource = (node.getAttributes()
							.getNamedItem("with-datasource") != null) ? node
							.getAttributes().getNamedItem("with-datasource")
							.getTextContent() : null;

					final String beanEntityManagerProvider = (node
							.getAttributes().getNamedItem(
									"with-entitymanager-provider") != null) ? node
							.getAttributes()
							.getNamedItem("with-entitymanager-provider")
							.getTextContent()
							: null;

					final BeanDefinitionBuilder queryScannerBeanBuilder = BeanDefinitionBuilder
							.genericBeanDefinition(MangoQueryScanner.class);

					final ConstructorArgumentValues values = new ConstructorArgumentValues();

					int argIndex = 0;
					
					values.addIndexedArgumentValue(argIndex++, packageToScan);

					if (beanDataSource != null) {
						values.addIndexedArgumentValue(argIndex++, new RuntimeBeanReference(beanDataSource));
					}

					if (beanEntityManagerProvider != null) {
						values.addIndexedArgumentValue(
								argIndex++,
								new RuntimeBeanReference(beanEntityManagerProvider));
					}

					queryScannerBeanBuilder.getRawBeanDefinition()
							.setConstructorArgumentValues(values);

					final BeanDefinitionHolder holder = new BeanDefinitionHolder(
							queryScannerBeanBuilder.getBeanDefinition(),
							MangoQueryScanner.class.getName() + "-"
									+ UUID.randomUUID().toString());

					registerBeanDefinition(holder, context.getRegistry());
				}
			}
		}

		registerBeanDefinitionWithNameBasedOnClass(handlerScannerBeanBuilder,
				context);

		registerBeanDefinitionWithNameFallbackToNameBasedOnClass(
				cqsFactoryBeanBuilder, id, context);

		return null;
	}

}
