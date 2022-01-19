package com.reyco.encryption.core.handlers;

import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;

/** 
 * @author  reyco
 * @date    2021.11.25
 * @version v1.0.1 
 */
public class CachingDelegateEncryptablePropertySource<T> extends PropertySource<T> implements EncryptablePropertySource<T>  {
	private final PropertySource<T> delegate;
	/**
	 * @param name
	 */
	public CachingDelegateEncryptablePropertySource(PropertySource<T> delegate) {
		 super(delegate.getName(), delegate.getSource());
		 this.delegate = delegate;
	}

	@Override
	public PropertySource<T> getDelegate() {
		return delegate;
	}

	@Override
	public Object getProperty(String name) {
		Object value = new PropertySource<Object>(name) {
			@Override
			public Object getProperty(String name) {
				return delegate.getProperty(name);
			}
		}.getProperty(name);
		return unwrapEncryptedValue(value);
	}
	/** 
	 * 
	 * @param value
	 * @return
	 */
	public Object unwrapEncryptedValue(Object v) {
		String r,sv = "";
		if(isEncrypted(v)) {
			sv = (String)v;
			int si,ei=0;
			if ((si=sv.lastIndexOf(PREFIX)) != -1) {
				if ((ei=findPlaceholderEndIndex(sv, si)) != -1) {
					String svp = sv.substring(0,si);
					String p = sv.substring(si + PREFIX.length(), ei);
					p = StringEncryptor.decrypt(p.toString());
					Object ptv = null;
					if((ptv=getProperty(p))==null) {
						throw new RuntimeException(p+"这个配置未定义！");
					}
					String svs = sv.substring(ei+1);
					r = svp + ptv + svs;
					return unwrapEncryptedValue(r);
				}
			}
		}
		return v;
	}
	/**
	 * 获取索引
	 * @param b
	 * @param si
	 * @return
	 */
	public int findPlaceholderEndIndex(CharSequence b, int si) {
		int i = si + PREFIX.length();
		int withinNestedPlaceholder = 0;
		while (i < b.length()) {
			if (StringUtils.substringMatch(b, i, SUFFIX)) {
				if (withinNestedPlaceholder > 0) {
					withinNestedPlaceholder--;
					i = i + SUFFIX.length();
				} else {
					return i;
				}
			} else if (StringUtils.substringMatch(b, i, PREFIX)) {
				withinNestedPlaceholder++;
				i = i + PREFIX.length();
			} else {
				i++;
			}
		}
		return -1;
	}
}
