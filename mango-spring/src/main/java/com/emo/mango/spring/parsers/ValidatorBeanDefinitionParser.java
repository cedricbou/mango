package com.emo.mango.spring.parsers;

import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.emo.mango.spring.support.MangoBeanDefinitionParser;
import com.emo.mango.validation.MangoValidator;
import com.emo.mango.validation.MangoValidatorFactory;

public class ValidatorBeanDefinitionParser extends MangoBeanDefinitionParser {

	@Override
	public AbstractBeanDefinition parseMango(String id, Element element,
			ParserContext context) {

		final BeanDefinitionBuilder validatorFactoryBeanBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(MangoValidatorFactory.class);
		
		final String type = getAttributeWithFallback(element, "type", null);
		
		if(null != type) {
			final ConstructorArgumentValues values = new ConstructorArgumentValues();
			values.addGenericArgumentValue(type);
			validatorFactoryBeanBuilder.getRawBeanDefinition()
					.setConstructorArgumentValues(values);
		}
		
		registerBeanDefinitionWithNameBasedOnClass(validatorFactoryBeanBuilder, context);

		final BeanDefinitionBuilder mangoValidatorBeanBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(MangoValidator.class);
		
		mangoValidatorBeanBuilder.getRawBeanDefinition().setFactoryBeanName(getNameBasedOnClass(validatorFactoryBeanBuilder));
		mangoValidatorBeanBuilder.getRawBeanDefinition().setFactoryMethodName("validator");

		registerBeanDefinitionWithNameFallbackToNameBasedOnClass(mangoValidatorBeanBuilder, id, context);
		
		return null;
	}
}
