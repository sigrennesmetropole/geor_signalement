package org.georchestra.signalement.core.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContext implements ApplicationContextAware {
	private static org.springframework.context.ApplicationContext instance;

	private static void initializeApplicationContext(
			org.springframework.context.ApplicationContext applicationContext) {
		ApplicationContext.instance = applicationContext;
	}

	public static boolean isInitialized() {
		return instance != null;
	}

	@Override
	public void setApplicationContext(org.springframework.context.ApplicationContext applicationContext)
			throws BeansException {
		initializeApplicationContext(applicationContext);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		return (T) instance.getBean(name);
	}

	public static <T> T getBean(Class<T> clazz) {
		return instance.getBean(clazz);
	}

	public static String[] getBeanNames() {
		return instance.getBeanDefinitionNames();
	}
}
