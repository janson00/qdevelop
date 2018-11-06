package cn.qdevelop.common.security;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class QBase64Utils {
	
	public static String encode(byte[] input){
		Object retObj= null;
		try {
			@SuppressWarnings("rawtypes")
			Class clazz=Class.forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");
			@SuppressWarnings("unchecked")
			Method mainMethod= clazz.getMethod("encode", byte[].class);
			mainMethod.setAccessible(true);
			retObj = mainMethod.invoke(null, new Object[]{input});
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return (String)retObj;
	}
	
	/***
	 * decode by Base64
	 */
	public static String decode(String input) {
		Object retObj= null;
		try {
			@SuppressWarnings("rawtypes")
			Class clazz=Class.forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");
			@SuppressWarnings("unchecked")
			Method mainMethod= clazz.getMethod("decode", String.class);
			mainMethod.setAccessible(true);
			retObj = mainMethod.invoke(null, input);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return new String((byte[])retObj);
	}
	
	public static void main(String[] args) {
		String s = (encode("janson".getBytes()));
		System.out.println(s);
		System.out.println(decode(s));
	}
}
