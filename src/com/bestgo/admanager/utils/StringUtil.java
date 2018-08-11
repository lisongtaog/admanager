package com.bestgo.admanager.utils;
/**
 * @author mengjun
 * @date 2018/7/9 17:13
 * @desc 处理有关字符串的操作
 */
public class StringUtil {

	public static boolean isEmpty(String s) {
		return s == null || "".equals(s.trim());
	}

	public static boolean isNotEmpty(String s) {
		return !isEmpty(s);
	}
}

