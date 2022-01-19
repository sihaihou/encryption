package com.reyco.encryption.core.handlers;

import java.util.stream.Stream;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author reyco
 * @date 2021.11.25
 * @version v1.0.1
 * @param <T>
 */
@Component
public class EnableEncryptablePropertySourcesPostProcessor<T> implements BeanFactoryPostProcessor, EnvironmentAware {

	private ConfigurableEnvironment environment;

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = (ConfigurableEnvironment) environment;

	}

	@Override
	@SuppressWarnings("unchecked")
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		MutablePropertySources propertySources = environment.getPropertySources();
		propertySources.stream()
				.filter((propertySource) -> !(propertySource instanceof EncryptableMapPropertySourceWrapper))
				.forEach((propertySource) -> {
					PropertySource<T> encryptablePropertySourceWrapper = null;
					if (needsProxyAnyway(propertySource.getClass().getName())) {
						encryptablePropertySourceWrapper = (PropertySource<T>) proxyPropertySource(propertySource);
					} else if (propertySource instanceof MapPropertySource) {
						encryptablePropertySourceWrapper = (PropertySource<T>) new EncryptableMapPropertySourceWrapper<Object>((MapPropertySource) propertySource);
					} else if (propertySource instanceof EnumerablePropertySource) {
						encryptablePropertySourceWrapper = new EncryptableEnumerablePropertySourceWrapper<>((EnumerablePropertySource<T>) propertySource);
					} else {
						encryptablePropertySourceWrapper = new EncryptablePropertySourceWrapper(propertySource);
					}
					propertySources.replace(propertySource.getName(), encryptablePropertySourceWrapper);
				});
	}

	/**
	 * @param propertySource
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> PropertySource<T> proxyPropertySource(PropertySource<T> propertySource) {
		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setTargetClass(propertySource.getClass());
		proxyFactory.setProxyTargetClass(true);
		proxyFactory.addInterface(EncryptablePropertySource.class);
		proxyFactory.setTarget(propertySource);
		proxyFactory.addAdvice(new EncryptablePropertySourceMethodInterceptor<T>(propertySource));
		return (PropertySource<T>) proxyFactory.getProxy();
	}

	private static boolean needsProxyAnyway(String className) {
		return Stream.of("org.springframework.boot.context.config.ConfigFileApplicationListener$ConfigurationPropertySources",
				"org.springframework.boot.context.properties.source.ConfigurationPropertySourcesPropertySource")
				.anyMatch(className::equals);
	}
}
