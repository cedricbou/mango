package com.emo.mango.spring.query.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.util.ClassUtils;


class ComponentClassScanner extends ClassPathScanningCandidateComponentProvider {

	public ComponentClassScanner() {
		super(false);
	}

	@SuppressWarnings("unchecked")
	public final <T> Collection<Class<? extends T>> getComponentClasses(String basePackage) {
		basePackage = basePackage == null ? "" : basePackage;
		List<Class<? extends T>> classes = new ArrayList<Class<? extends T>>();
		for (BeanDefinition candidate : findCandidateComponents(basePackage)) {
			try {
				Class<?> cls = ClassUtils.resolveClassName(candidate.getBeanClassName(),
						ClassUtils.getDefaultClassLoader());
				classes.add((Class<? extends T>) cls);
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}
		return classes;
	}

	@Override
	protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
		return (/* beanDefinition.getMetadata().isConcrete() && */ beanDefinition.getMetadata().isIndependent());
	}


}
