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

	public static String setKey(String jointMark, String ... values){
		StringBuffer buffer = new StringBuffer();
		if (values != null) {
			for (int i = 0,length = values.length;i< length;i++) {
				buffer.append(values[i]);
				if (i < length - 1) {
					buffer.append(jointMark);
				}
			}
		}
		return buffer.toString();
	}
}

