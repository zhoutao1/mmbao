package com.mmbao.session;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(value = BeanDefinition.SCOPE_SINGLETON)
public class SpringBeanUtils implements ApplicationContextAware {

	private static ApplicationContext applicationContext = null;

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringBeanUtils.applicationContext = applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static <T> T getBean(Class<T> requiredType) throws BeansException {
		return applicationContext.getBean(requiredType);
	}

	public static Object getBean(String beanId) throws BeansException {
		return applicationContext.getBean(beanId);
	}
}
