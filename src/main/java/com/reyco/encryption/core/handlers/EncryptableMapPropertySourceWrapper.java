package com.reyco.encryption.core.handlers;

import java.util.Map;

import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

/**
 * @author reyco
 * @date 2021.11.25
 * @version v1.0.1
 * @param <T>
 */
public class EncryptableMapPropertySourceWrapper<T> extends MapPropertySource implements EncryptablePropertySource<Map<String, Object>> {
	
	private final EncryptablePropertySource<Map<String, Object>> encryptableDelegate;
	
	public EncryptableMapPropertySourceWrapper(MapPropertySource delegate) {
		super(delegate.getName(), delegate.getSource());
		encryptableDelegate = new CachingDelegateEncryptablePropertySource<>(delegate);
	}

	@Override
	public Object getProperty(String name) {
		return encryptableDelegate.getProperty(name);
	}

	@Override
	public PropertySource<Map<String, Object>> getDelegate() {
		return (PropertySource<Map<String, Object>>) encryptableDelegate.getDelegate();
	}
}
