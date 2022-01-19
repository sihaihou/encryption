package com.reyco.encryption.core.handlers;

import org.springframework.core.env.PropertySource;

/**
 * @author reyco
 * @date 2021.11.25
 * @version v1.0.1
 * @param <T>
 */
public class EncryptablePropertySourceWrapper<T> extends PropertySource<T> implements EncryptablePropertySource<T> {
	private final EncryptablePropertySource<T> encryptableDelegate;
	/**
	 * @param name
	 */
	public EncryptablePropertySourceWrapper(PropertySource<T> delegate) {
		super(delegate.getName(), delegate.getSource());
		encryptableDelegate = new CachingDelegateEncryptablePropertySource<>(delegate);
	}

	@Override
	public Object getProperty(String name) {
		return encryptableDelegate.getProperty(name);
	}

	@Override
	public PropertySource<T> getDelegate() {
		return encryptableDelegate.getDelegate();
	}
}
