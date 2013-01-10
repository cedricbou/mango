package com.emo.mango.spring.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.emo.mango.config.DataSourceExtension;

public class DataSourceBeanDefinitionParser extends AbstractBeanDefinitionParser {
		
	@Override
	public AbstractBeanDefinition parseInternal(Element element, ParserContext context) {

		// Register bean for datasource extension
		final BeanDefinitionBuilder datasourceExtensionBeanBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(DataSourceExtension.class);
		datasourceExtensionBeanBuilder.getRawBeanDefinition().setFactoryBeanName(ConfigBeanDefinitionParser.EXTENSION_BEAN_NAME);
		datasourceExtensionBeanBuilder.getRawBeanDefinition().setFactoryMethodName("datasource");
		
		final BeanDefinitionHolder datasourceExtensionHolder = new BeanDefinitionHolder(datasourceExtensionBeanBuilder.getBeanDefinition(), DataSourceExtension.class.getName());
		
		registerBeanDefinition(datasourceExtensionHolder, context.getRegistry());
		
		// Register datasource bean
		final BeanDefinitionBuilder datasourceBeanBuilder = BeanDefinitionBuilder.genericBeanDefinition(DataSource.class);
		datasourceBeanBuilder.getRawBeanDefinition().setFactoryBeanName(datasourceExtensionHolder.getBeanName());
		datasourceBeanBuilder.getRawBeanDefinition().setFactoryMethodName("datasource");
	
		final String datasourceName = (element.hasAttribute("datasource")?element.getAttribute("datasource"):null);
		
		if(null != datasourceName) {
			final ConstructorArgumentValues values = new ConstructorArgumentValues();
			values.addGenericArgumentValue(datasourceName);
			datasourceBeanBuilder.getRawBeanDefinition().setConstructorArgumentValues(values);
		}
		
		return datasourceBeanBuilder.getBeanDefinition();
	}

}
