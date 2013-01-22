package com.emo.mango.spring.parsers;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.annotation.ComponentScanBeanDefinitionParser;
import org.springframework.web.servlet.config.MvcAnnotationDrivenBeanDefinitionParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.emo.mango.spring.support.MangoBeanDefinitionParser;
import com.emo.mango.spring.web.support.ProcessCommandValidator;
import com.emo.mango.validation.MangoValidator;

public class WebBeanDefinitionParser extends MangoBeanDefinitionParser {

	private static final String FORGED_CONTEXT_COMPONENT_SCAN_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><component-scan xmlns=\"http://www.springframework.org/schema/context\" base-package=\"" 
			+ "com.emo.mango.spring.web.support" + "\" />"; 
	
	private static final String FORGED_MVC_ANNOTATION_DRIVEN_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><component-scan xmlns=\"http://www.springframework.org/schema/mvc\" />"; 
	
	@Override
	public AbstractBeanDefinition parseMango(String id, Element element,
			ParserContext context) {

		final BeanDefinitionBuilder processCommandvalidatorFactoryBeanBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(ProcessCommandValidator.class);

		final String userDefinedValidatorId = getAttributeWithFallback(element, "with-validator", null);
		
		MutablePropertyValues values = new MutablePropertyValues();
		
		if(null == userDefinedValidatorId && context.getRegistry().containsBeanDefinition(getNameBasedOnClass(MangoValidator.class))) {
			values.addPropertyValue("validator", new RuntimeBeanReference(getNameBasedOnClass(MangoValidator.class)));
		}
		else if(null != userDefinedValidatorId ) {
			values.addPropertyValue("validator", new RuntimeBeanReference(getNameBasedOnClass(MangoValidator.class)));
		}

		processCommandvalidatorFactoryBeanBuilder.getRawBeanDefinition().setPropertyValues(values);

		registerBeanDefinitionWithClass(processCommandvalidatorFactoryBeanBuilder, context);
		
		final ComponentScanBeanDefinitionParser contextComponentScan = new ComponentScanBeanDefinitionParser();
		final MvcAnnotationDrivenBeanDefinitionParser mvcAnnotationDriven = new MvcAnnotationDrivenBeanDefinitionParser();
				
	    try {
	    	final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		    final DocumentBuilder dBuilder;
			dBuilder = dbFactory.newDocumentBuilder();
		    final Document docComponent = dBuilder.parse(new ByteArrayInputStream(FORGED_CONTEXT_COMPONENT_SCAN_XML.getBytes("UTF-8")));
		    contextComponentScan.parse(docComponent.getDocumentElement(), context);
		    final Document docMvc = dBuilder.parse(new ByteArrayInputStream(FORGED_MVC_ANNOTATION_DRIVEN_XML.getBytes("UTF-8")));
		    mvcAnnotationDriven.parse(docMvc.getDocumentElement(), context);
	    } catch (Exception e) {
			throw new RuntimeException("failed to init context component scan in mango:web", e);
		}
	    
		return null;
	}

}
