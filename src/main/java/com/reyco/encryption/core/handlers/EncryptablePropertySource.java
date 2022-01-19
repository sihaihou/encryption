package com.reyco.encryption.core.handlers;

import org.springframework.core.env.PropertySource;

/**
 * @author reyco
 * @date 2021.11.25
 * @version v1.0.1
 */
public interface EncryptablePropertySource<T> {

	public static final String PREFIX = "(";
	public static final String SUFFIX = ")";

	PropertySource<T> getDelegate();

	Object getProperty(String name);
	/**
	 * 验证是否包含PREFIX、SUFFIX
	 * @param property
	 * @return
	 */
	default boolean isEncrypted(Object property) {
        if (property == null || !(property instanceof String)) {
            return false;
        }
        final String tv = ((String)property).trim();
        int pi,ps;
        if((pi=tv.lastIndexOf(PREFIX))!=-1 && (ps=tv.indexOf(SUFFIX))!=-1 && pi<ps) {
        	return true;
        }
        return false;
    }
}
