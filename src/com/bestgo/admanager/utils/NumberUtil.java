package com.bestgo.admanager.utils;

import java.math.BigDecimal;

/**
 * @author mengjun
 * @date 2018/7/9 17:13
 * @desc 处理有关数字的操作
 */
public class NumberUtil {
    public static int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public static double parseDouble(String value, double defaultValue) {
        try {
            return Double.parseDouble(value);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public static long convertLong(Object value, long defaultValue) {
        try {
            if (value instanceof BigDecimal) {
                return ((BigDecimal)value).longValue();
            } else if (value instanceof Double) {
                return (long)value;
            } else if (value instanceof Long) {
                return (Long)value;
            } else if (value instanceof Integer) {
                return (Integer)value;
            }
        } catch (Exception ex) {
        }
        return defaultValue;
    }

    public static double convertDouble(Object value, double defaultValue) {
        try {
            if (value instanceof BigDecimal) {
                return ((BigDecimal)value).doubleValue();
            } else if (value instanceof Double) {
                return (double)value;
            } else if (value instanceof Long) {
                return (Long)value;
            } else if (value instanceof Integer) {
                return (Integer)value;
            }
        } catch (Exception ex) {
        }
        return defaultValue;
    }


    /**
     * double类型的数据保留n位小数
     * @param value
     * @return
     */
    public static double trimDouble(double value,int n) {
        return Double.parseDouble(String.format("%." + n + "f", value));
    }
}
