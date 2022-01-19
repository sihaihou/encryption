package com.reyco.encryption.core.handlers;

/**
 * @author reyco
 * @date 2021.11.25
 * @version v1.0.1
 */
public class StringEncryptor {
	
	public static String encrypt(String message) {
		return message;
	}

	public static String decrypt(String encryptedMessage) {
		System.out.println("解密："+encryptedMessage);
		return encryptedMessage;
	}
}
