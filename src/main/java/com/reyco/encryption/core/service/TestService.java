package com.reyco.encryption.core.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author reyco
 * @version v1.0.1
 */
@Service
public class TestService {
	
	@Value(value = "${name}")
	private String name;
	
	public void test() {
		System.out.println("name:"+name);
	}
	
}
