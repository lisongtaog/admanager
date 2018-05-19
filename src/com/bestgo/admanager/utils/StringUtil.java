package com.bestgo.admanager.utils;

public class StringUtil {

	public static boolean isEmpty(String s) {
		return s == null || "".equals(s.trim());
	}

	public static boolean isNotEmpty(String s) {
		return !isEmpty(s);
	}
}

