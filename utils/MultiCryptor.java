/*
 * Copyright 2017 @ursful.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yidao.court.prelitigation.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

@Slf4j
public class MultiCryptor {

	private static final String base64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

	private static final String CHARSET_UTF8 = "UTF-8";
	private static final String ALGORITHM_MD5 = "MD5";
	private static final String ALGORITHM_SHA = "SHA";
	private static final String ALGORITHM_SHA256 = "SHA-256";
	private static final String ALGORITHM_SHA384 = "SHA-384";
	private static final String ALGORITHM_SHA512 = "SHA-512";
	private static final String ALGORITHM_DES_CBC_PKCS5PADDING = "DES/ECB/PKCS5Padding";//DES/ECB/PKCS5Padding
	private static final String ALGORITHM_RSA_ECB_PKCS1PADDING = "RSA/ECB/PKCS1Padding";
	private static final String ALGORITHM_DES = "DES";
	private static final String ALGORITHM_RSA = "RSA";

	private static final int BUFFER_LENGTH = 1024 * 10;
 


	/**
	 * RSA加密
	 * @param content 加密内容
	 * @param modulus 模
	 * @param exponent 指数
	 * @return String
	 */
	public static String rsaEncrypt(String content, String modulus, String exponent){
		RSAPublicKey publicKey = createPublicKey(modulus, exponent);
		if(publicKey != null){
			return rsaEncrypt(content, publicKey);
		}
		return null;
	}

	/**
	 * RSA解密
	 * @param data 解密内容
	 * @param modulus 模
	 * @param exponent 指数
	 * @return String
	 */
	public static String rsaDecrypt(String data, String modulus, String exponent){
		RSAPrivateKey privateKey = createPrivateKey(modulus, exponent);
		
		if(privateKey != null){
			return rsaDecrypt(data, privateKey);
		}
		return null;
	}

	/**
	 * RSA解密
	 * @param data 解密内容
	 * @param privateKey 私钥
	 * @return String
	 */
	public static String rsaDecrypt(String data, RSAPrivateKey privateKey){
		data = base64ToHex(data);
		String result = null;
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM_RSA_ECB_PKCS1PADDING);
			
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			//模长
			int key_len = privateKey.getModulus().bitLength() / 8;
			byte[] bytes = data.getBytes(CHARSET_UTF8);
			byte[] bcd = ASCII_To_BCD(bytes, bytes.length);
			//如果密文长度大于模长则要分组解密
			byte[][] arrays = splitArray(bcd, key_len);
			StringBuffer sb = new StringBuffer();
			for(byte[] arr : arrays){
				sb.append(new String(cipher.doFinal(arr), CHARSET_UTF8));
			}
			result = sb.toString();
		} catch (Exception e) {
            log.error(e.getMessage(), e);
		}
		return result;
	}

	/**
	 * 分割素组
	 * @param data
	 * @param len
	 * @return byte[][]
	 */
	private static byte[][] splitArray(byte[] data,int len){
		int count = data.length / len + (data.length % len == 0?0:1);
		int left = (data.length % len == 0?len:data.length % len);
		byte[][] arrays = new byte[count][];
		 
		for(int i=0; i< count; i++){
			int lenth = Math.min(len, left);
			byte[] arr = new byte[lenth];
			System.arraycopy(data, i*len, arr, 0, lenth);
			arrays[i] = arr;
		}
		return arrays;
	}

	/**
	 * ASCII转bcd
	 * @param ascii
	 * @param asc_len
	 * @return byte[]
	 */
	private static byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {
		byte[] bcd = new byte[asc_len / 2];
		int j = 0;
		for (int i = 0; i < (asc_len + 1) / 2; i++) {
			bcd[i] = asc_to_bcd(ascii[j++]);
			bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));
		}
		return bcd;
	}

	/**
	 * asc转bcd
	 * @param asc
	 * @return byte
	 */
	private static byte asc_to_bcd(byte asc) {
		byte bcd;

		if ((asc >= '0') && (asc <= '9')) {
			bcd = (byte) (asc - '0');
		} else if ((asc >= 'A') && (asc <= 'F')) {
			bcd = (byte) (asc - 'A' + 10);
		} else if ((asc >= 'a') && (asc <= 'f')) {
			bcd = (byte) (asc - 'a' + 10);
		} else {
			bcd = (byte) (asc - 48);
		}
		return bcd;
	}

	/**
	 * RSA 加密
	 * @param content 加密内容
	 * @param publicKey 公钥
	 * @return String
	 */
	public static String rsaEncrypt(String content, RSAPublicKey publicKey){
		String result = null;
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM_RSA_ECB_PKCS1PADDING);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			// 模长
			int key_len = publicKey.getModulus().bitLength() / 8;
			// 加密数据长度 <= 模长-11
			byte[][] datas = splitString(content.getBytes(CHARSET_UTF8), key_len - 11);
			
			//128-11 = 116
			//如果明文长度大于模长-11则要分组加密
			StringBuffer sb = new StringBuffer();
			for (byte[] s : datas) { 
				byte [] d = cipher.doFinal(s);
				sb.append(bcd2Str(d));
			}

			result = hexToBase64(sb.toString());
		} catch (Exception e) {
            log.error(e.getMessage(), e);
		}
		return result;
	}

	/**
	 * BCD转字符串
	 * @param bytes
	 * @return String
	 */
	private static String bcd2Str(byte[] bytes) {
		char temp[] = new char[bytes.length * 2], val;
		for (int i = 0; i < bytes.length; i++) {
			val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);
			temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');

			val = (char) (bytes[i] & 0x0f);
			temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
		}
		return new String(temp);
	}
	 
	/**
	 * 拆分字符串
	 * @param string
	 * @param len
	 * @return String[]
	 */
	private static byte[][] splitString(byte[] string, int len) {
		int lines = string.length / len + (string.length % len==0?0:1);
		
		byte[][] strings = new byte[lines][];
		for (int i = 0; i < lines; i++) {
			int left = Math.min(len, string.length%len);
			if(left == 0){
				left = len;
			}
			byte [] lefts = new byte[left];
			System.arraycopy(string, i*left, lefts, 0, left);
			strings[i] = lefts;
		}
		return strings;
	}
	
	/**
	 * 创建RSA私钥
	 * @param modulus 模指数
	 * @param exponent 指数
	 * @return RSAPrivateKey
	 */
	public static RSAPrivateKey createPrivateKey(String modulus, String exponent){
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
			BigInteger mod = new BigInteger(modulus, 16);
			BigInteger exp = new BigInteger(exponent, 16);
			RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(mod, exp);
			return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
		} catch (Exception e) {
            log.error(e.getMessage(), e);
		}
        return null;
	}
	
	/**
	 * 创建RSA公钥
	 * @param modulus 模指数
	 * @param exponent 指数
	 * @return RSAPublicKey
	 */
	public static RSAPublicKey createPublicKey(String modulus, String exponent){
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
			BigInteger mod = new BigInteger(modulus, 16);
			BigInteger exp = new BigInteger(exponent, 16);
			RSAPublicKeySpec keySpec = new RSAPublicKeySpec(mod, exp);
			return (RSAPublicKey) keyFactory.generatePublic(keySpec);
		} catch (Exception e) {
            log.error(e.getMessage(), e);
		}
        return null;
	}
	
	/**
	 * 返回16进制的私钥素数
	 * @param privateKey RSA私钥
	 * @return String
	 */
	public static String getPrivatePrime(RSAPrivateKey privateKey){
		String result = null;
		if(privateKey != null){
			result = privateKey.getPrivateExponent().toString(16);
		}
		return result;
	}
	
	/**
	 * 返回16进制的私钥素数
	 * @param keyPair 密钥对
	 * @return String
	 */
	public static String getPrivatePrime(KeyPair keyPair){
		String result = null;
		if(keyPair != null){
			result = getPrivatePrime(getRSAPrivateKey(keyPair));
		}
		return result;
	}
	
	
	/**
	 * 返回16进制的公钥素数
	 * @param publicKey RSA公钥
	 * @return String
	 */
	public static String getPublicPrime(RSAPublicKey publicKey){
		String result = null;
		if(publicKey != null){
			result = publicKey.getPublicExponent().toString(16);
		}
		return result;
	}
	
	/**
	 * 返回16进制的公钥素数
	 * @param keyPair 密钥对
	 * @return
	 */
	public static String getPublicPrime(KeyPair keyPair){
		String result = null;
		if(keyPair != null){
			result = getPublicPrime(getRSAPublicKey(keyPair));
		}
		return result;
	}
	
	/**
	 * 返回16进制的modulus
	 * @param obj KeyPair 或者 RSAPublicKey 或者 RSAPrivateKey
	 * @return String
	 */
	public static String getModulus(Object obj){
		BigInteger modulus = null;
		if(obj instanceof KeyPair){
			modulus = getRSAPublicKey((KeyPair)obj).getModulus();
		}else if(obj instanceof RSAPublicKey){
			modulus = ((RSAPublicKey)obj).getModulus();
		}else if(obj instanceof RSAPrivateKey){
			modulus = ((RSAPrivateKey)obj).getModulus();
		}
		if(modulus != null){
			return modulus.toString(16);
		}
		return null;
	}
	
	/**
	 * 获取RSA公钥
	 * @param keyPair
	 * @return RSAPublicKey
	 */
	public static RSAPublicKey getRSAPublicKey(KeyPair keyPair){
		return (RSAPublicKey)keyPair.getPublic();
	}
	
	/**
	 * 获取RSA私钥
	 * @param keyPair
	 * @return RSAPrivateKey
	 */
	public static RSAPrivateKey getRSAPrivateKey(KeyPair keyPair){
		return (RSAPrivateKey)getPrivateKey(keyPair);
	}
	
	/**
	 * 生成RSA密钥对，默认初始化长度1024
	 * @return KeyPair
	 */
	public static KeyPair generateRSAKeyPair(){
		return generateKeyPair(ALGORITHM_RSA, 1024);
	}
	
	/**
	 * 生成RSA密钥对
	 * @size 默认1024
	 * @return KeyPair
	 */
	public static KeyPair generateRSAKeyPair(int size){
		if(size < 1){
			size = 1024;
		}
		return generateKeyPair(ALGORITHM_RSA, size);
	}
	
	
	/**
	 * 获取公钥
	 * @param keyPair
	 * @return PublicKey
	 */
	public static PublicKey getPublicKey(KeyPair keyPair){
		return keyPair.getPublic();
	}
	
	/**
	 * 获取私钥
	 * @param keyPair
	 * @return PrivateKey
	 */
	public static PrivateKey getPrivateKey(KeyPair keyPair){
		return keyPair.getPrivate();
	}
	
	/**
	 * 生成密钥对
	 * @param algorithm 算法
	 * @return KeyPair
	 */
	public static KeyPair generateKeyPair(String algorithm, int size){
		KeyPair keyPair = null;
		try {
			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(algorithm);
			keyPairGen.initialize(size);
			keyPair = keyPairGen.generateKeyPair();
		} catch (Exception e) {
            log.error(e.getMessage(), e);
		}
       
        return keyPair;
	}


	/**
	 * 字符串加密，返回Base64
	 * @param content 字符串
	 * @param charset 字符串编码
	 * @param password 密码
	 * @return String
	 */
	public static String desEncrypt(String content, String password, String charset){
		if(content == null){
			return null;
		}
		try {
			if(charset == null){
				charset = CHARSET_UTF8;
			}
			byte [] data = content.getBytes(charset);
			byte [] result = des(password, data, true);
			return Base64.encodeBytes(result);
		} catch (Exception e) {
            log.error(e.getMessage(), e);
		}
        return null;
	}
	
	/**
	 * 字符串加密，默认UTF-8编码，返回Base64
	 * @param content 字符串
	 * @param password 密码
	 * @return String
	 */
	public static String desEncrypt(String content, String password){
		 return desEncrypt(content, password, CHARSET_UTF8);
	}


	/**
	 * Base64的字符串，解密后的字符串
	 * @param content Base64字节数组
	 * @param password 密码
	 * @return String
	 */
	public static byte[] desDecrypt(byte[] content, String password){
		if(content == null){
			return null;
		}
		try {
			byte[] data = Base64.decode(content);
			byte[] res = des(password, data, false);
			return res;
		} catch (Exception e) {
            log.error(e.getMessage(), e);
		}
        return null;
	}

	
	/**
	 * Base64的字符串，解密后的字符串
	 * @param content Base64字符串
	 * @param charset 字符串编码
	 * @param password 密码
	 * @return String
	 */
	public static String desDecrypt(String content, String password, String charset){
		String result = null;
		if(content == null){
			return result;
		}
		try {
			byte[] data = Base64.decode(content);
			byte[] res = des(password, data, false);
			if(charset == null){
				charset = CHARSET_UTF8;
			}
			result = new String(res, charset);
		} catch (Exception e) {
            log.error(e.getMessage(), e);
		}
		return result;
	}
	
	
	public static String decodeInputStream(InputStream inputStream){
		return decodeInputStream(inputStream, CHARSET_UTF8);
	}
	
	/**
	 * 将流解析成字符串
	 * @param inputStream 输入流
	 * @param charset 编码
	 * @return
	 */
	public static String decodeInputStream(InputStream inputStream, String charset){
		try {
			byte[] buf = new byte[1024];
			int size = 0;
			byte [] b = null;
			while((size = inputStream.read(buf)) != -1){
				if(b == null){
					System.out.println("size:" + size);
					b = new byte[size];
					System.arraycopy(buf, 0, b, 0, size);
				}else{
					byte [] tmp = b;
					b = new byte[tmp.length + size];
					System.arraycopy(tmp, 0, b, 0, tmp.length);
					System.arraycopy(buf, 0, b, tmp.length, size);
				}
			}
			if(b != null){
				return new String(b, charset);
			}
		} catch (IOException e) {
            log.error(e.getMessage(), e);
		} finally {
			try {
				inputStream.close();
			} catch (Exception e2) {
			}
		}
		return null;
	}
	
	/**
	 * Base64的字符串，解密后的字符串，默认UTF-8编码
	 * @param content Base64字符串
	 * @param password 密码
	 * @return String
	 */
	public static String desDecrypt(String content, String password){
		return desDecrypt(content, password, CHARSET_UTF8);
	}
	
	
	/**
	 * 字符串的des加解密
	 * @param password 密码
	 * @param text 数据
	 * @param isEncrypt true：加密， false：解密
	 * @return byte[]
	 */
	
	private static byte[] des(String password, byte[] text, boolean isEncrypt){
		try {
			byte[] keyByte = password.getBytes(CHARSET_UTF8);
			byte[] byteTemp = new byte[8];// 创建一个空的八位数组,默认情况下为0
			int length = Math.min(byteTemp.length, keyByte.length);
			for (int i = 0; i < length; i++) {
				byteTemp[i] = keyByte[i];
			}
			SecretKeySpec key = new SecretKeySpec(byteTemp, ALGORITHM_DES);
			Cipher cp = Cipher.getInstance(ALGORITHM_DES_CBC_PKCS5PADDING);
			//IvParameterSpec zero = new IvParameterSpec(IvParameterBytes);
			cp.init(isEncrypt?Cipher.ENCRYPT_MODE:Cipher.DECRYPT_MODE, key);
			return cp.doFinal(text);
		} catch (Exception e) {
            log.error(e.getMessage(), e);
		}
        return null;
	}
	
	/**
	 * 文件的des加密，返回加密后的文件路径
	 * @param password 密码
	 * @param source 原始文件
	 * @param dest 加密文件
	 * @return String
	 */
	public static String fileDesEncrypt(String source, String dest, String password){
		return desFile(password, source, dest, true);
	}
	
	/**
	 * 文件的des解密，返回解密后的文件路径
	 * @param password 密码
	 * @param source 加密文件
	 * @param dest 解密文件
	 * @return String
	 */
	public static String fileDesDecrypt(String source, String dest, String password){
		return desFile(password, source, dest, false);
	}
	
	/**
	 * 文件的des加解密，返回加解密后的文件路径
	 * @param password 密码
	 * @param source 原始文件
	 * @param dest 目标文件
	 * @param isEncrypt true，加密；false，解密
	 * @return String
	 */
	private static String desFile(String password, String source, String dest, boolean isEncrypt){
		byte[] keyByte = password.getBytes();
		byte[] byteTemp = new byte[8];// 创建一个空的八位数组,默认情况下为0
		int length = Math.min(byteTemp.length, keyByte.length);
		for (int i = 0; i < length; i++) {
			byteTemp[i] = keyByte[i];
		}
		SecretKeySpec key = new SecretKeySpec(byteTemp, ALGORITHM_DES);
		
		try {
			FileInputStream in = new FileInputStream(source);
			 
			Cipher cp = Cipher.getInstance(ALGORITHM_DES_CBC_PKCS5PADDING);
			 
			//IvParameterSpec zero = new IvParameterSpec(IvParameterBytes);
			cp.init(isEncrypt?Cipher.ENCRYPT_MODE:Cipher.DECRYPT_MODE, key);
			File distFile = new File(dest);
			FileOutputStream out=new FileOutputStream(distFile);
			CipherOutputStream cout = new CipherOutputStream(out, cp);
			byte [] buf = new byte[1024*10];
			int b = 0;
			while ((b = in.read(buf)) != -1) {
				cout.write(buf, 0, b);
			}
			cout.close();
			out.close();
			in.close();
			if(distFile.exists()){
				return distFile.getCanonicalPath();
			}
		} catch (Exception e) {
            log.error(e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * 获取字符串的SHA， 默认UTF-8
	 * @param content 字符串内容
	 * @return String
	 */
	public static String sha(String content){
		return sha(content, CHARSET_UTF8);
	}
	
	/**
	 * 获取字符串的SHA
	 * @param content 字符串内容
	 * @param charset 字符编码
	 * @return String
	 */
	public static String sha(String content, String charset){
		return stringDigest(content, charset, ALGORITHM_SHA);
	}
	
	/**
	 * 获取文件的SHA
	 * @param file 文件的完整路径
	 * @return String
	 */
	public static String fileSha(String file){
		return fileDigest(file, ALGORITHM_SHA);
	}

	/**
	 * 获取字符串的SHA256， 默认UTF-8
	 * @param content 字符串内容
	 * @return String
	 */
	public static String sha256(String content){
		return sha256(content, CHARSET_UTF8);
	}
	
	/**
	 * 获取字符串的SHA256
	 * @param content 字符串内容
	 * @param charset 字符编码
	 * @return String
	 */
	public static String sha256(String content, String charset){
		return stringDigest(content, charset, ALGORITHM_SHA256);
	}
	
	/**
	 * 获取文件的SHA256
	 * @param file 文件的完整路径
	 * @return String
	 */
	public static String fileSha256(String file){
		return fileDigest(file, ALGORITHM_SHA256);
	}
	
	/**
	 * 获取字符串的SHA384， 默认UTF-8
	 * @param content 字符串内容
	 * @return String
	 */
	public static String sha384(String content){
		return sha384(content, CHARSET_UTF8);
	}
	
	/**
	 * 获取字符串的SHA384
	 * @param content 字符串内容
	 * @param charset 字符编码
	 * @return String
	 */
	public static String sha384(String content, String charset){
		return stringDigest(content, charset, ALGORITHM_SHA384);
	}
	
	/**
	 * 获取文件的SHA384
	 * @param file 文件的完整路径
	 * @return String
	 */
	public static String fileSha384(String file){
		return fileDigest(file, ALGORITHM_SHA384);
	}
	
	
	/**
	 * 获取字符串的SHA512， 默认UTF-8
	 * @param content 字符串内容
	 * @return String
	 */
	public static String sha512(String content){
		return sha512(content, CHARSET_UTF8);
	}
	
	/**
	 * 获取字符串的SHA512
	 * @param content 字符串内容
	 * @param charset 字符编码
	 * @return String
	 */
	public static String sha512(String content, String charset){
		return stringDigest(content, charset, ALGORITHM_SHA512);
	}
	
	/**
	 * 获取文件的SHA512
	 * @param file 文件的完整路径
	 * @return String
	 */
	public static String fileSha512(String file){
		return fileDigest(file, ALGORITHM_SHA512);
	}
	 
	/**
	 * 获取字符串的MD5， 默认编码UTF-8
	 * @param content
	 * @return String
	 */
	public static String md5(String content){
		return stringDigest(content, CHARSET_UTF8, ALGORITHM_MD5);
	}
	
	/**
	 * 获取字符串内容的MD5
	 * @param content 字符串内容
	 * @param charset 字符编码
	 * @return String
	 */
	public static String md5(String content, String charset){
		return stringDigest(content, charset, ALGORITHM_MD5);
	}
	
	/**
	 * 获取文件MD5
	 * @param file 文件的完整路径
	 * @return String
	 */
	public static String fileMd5(String file){
		return fileDigest(file, ALGORITHM_MD5);
	}

	
	/**
	 * 获取字符串的hash散列摘要,默认编码utf-8
	 * @param content 字符串内容
	 * @param algorithm 算法
	 * @return String
	 */
	private static String stringDigest(String content, String charset, String algorithm){
		try {
			if(charset == null){
				charset = CHARSET_UTF8;
			}
			MessageDigest md5 = MessageDigest.getInstance(algorithm);
			byte [] data = content.getBytes(charset);
	        md5.update(data);
	        return byteToHex(md5.digest());
		} catch (Exception e) {
            log.error(e.getMessage(), e);
		}
        return null;
	}
	
	/**
	 * 获取文件的hash散列摘要
	 * @param file 文件的完整路径
	 * @param algorithm 算法(md5, sha,...)
	 * @return String
	 */
	private static String fileDigest(String file, String algorithm){
		try {
	        InputStream ins = new FileInputStream(file);
	    	MessageDigest md5 = MessageDigest.getInstance(algorithm);
	        byte[] buffer = new byte[BUFFER_LENGTH];
	        int len = 0;
	        while((len = ins.read(buffer)) != -1){
	            md5.update(buffer, 0, len);
	        }
	        ins.close();
	        return byteToHex(md5.digest());
		} catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
	}
	 

	/**
	 * byte数组转为Hex
	 * @param digest
	 * @return String
	 */
	private static String byteToHex(byte[] digest) {
		if(digest == null){
			return null;
		}
		StringBuilder sb = new StringBuilder(digest.length*2);
		for(int i = 0; i < digest.length; i++){
			sb.append(Integer.toHexString((0x000000ff&digest[i]) | 0xFFFFFF00).substring(6));
		}
		return sb.toString();
	}


	/**
	 * 16进制转base64
	 * @param hex
	 * @return String
	 */
	public static String hexToBase64(String hex){
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i  < hex.length(); i++){
			String h = hex.charAt(i)+"";
			String sh = Integer.toBinaryString(256 + Integer.parseInt(h,16));
			sb.append(sh.substring(5));
		}
		String source = sb.toString();
		int left = source.length() % 6;
		if(left > 0){
			source = source + "000000".substring(left);
		}
		StringBuffer result = new StringBuffer();
		for(int i = 0; i + 6 <= source.length(); i=i+6){
			int index = Integer.parseInt("00" + source.substring(i, i+6), 2);
			result.append(base64.charAt(index));
		}
		String temp = result.toString();
		
		left = temp.length()%4;
		if(left > 0){
			temp += "====".substring(left);
		}
		
		return temp;
	}


	/**
	 * base64转16进制
	 * @param data
	 * @return String
	 */
	public static String base64ToHex(String data){
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < data.length(); i++){
			if(data.charAt(i) == '='){
				break;
			}else{
				Integer index = base64.indexOf(data.charAt(i));
				String bi = Integer.toBinaryString(index+256).substring(3);
				sb.append(bi);
			}
		}
		String source = sb.toString();
	 
		StringBuffer res = new StringBuffer();
		for(int i = 0; i + 4 <= source.length(); i = i+4){
			Integer a = Integer.parseInt(source.substring(i, i+4), 2);
			String b = Integer.toHexString(a%16);
			res.append(b);
		}
		return res.toString();
	}

	/**
	 * 16进制转byte
	 * @param hexString
	 * @return byte[]
	 */
	public static byte[] hexToByte(String hexString) {
		char[] hex = hexString.toCharArray();
		// 转rawData长度减半
		int length = hex.length / 2;
		byte[] rawData = new byte[length];
		for (int i = 0; i < length; i++) {
			// 先将hex转10进位数值
			int high = Character.digit(hex[i * 2], 16);
			int low = Character.digit(hex[i * 2 + 1], 16);
			// 將第一個值的二進位值左平移4位,ex: 00001000 => 10000000 (8=>128)
			// 然后与第二个值的二进位值作联集ex: 10000000 | 00001100 => 10001100 (137)
			int value = (high << 4) | low;
			// 与FFFFFFFF作补集
			if (value > 127) {
				value -= 256;
			}
			// 最后转回byte就OK
			rawData[i] = (byte) value;
		}
		return rawData;
	}


	 
}
