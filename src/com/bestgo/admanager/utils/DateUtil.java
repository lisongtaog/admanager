package com.bestgo.admanager.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

    /**
     * 时间戳转换成日期格式字符串
     * @param seconds 格式为format的字符串
     * @param format
     * @return
     */
    public static String timeStamp2Date(Timestamp seconds, String format) {
        if(seconds == null){
            return "";
        }
        if(format == null || format.isEmpty()){
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(seconds);
    }

    /**
     * 取得当前时间戳（精确到秒）
     * @return
     */
    public static String getCurrTimestamp(){
        long time = System.currentTimeMillis();
        String t = String.valueOf(time/1000);
        return t;
    }

    /**
     * 获取当前时间
     * @return 时间字符串
     */
    public static String getNowTime() {
        Calendar calendar = Calendar.getInstance();
        String now  = String.format("%d-%02d-%02d %02d:%02d:%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
        return now;
    }

    /**
     * 获取当前日期
     * @return 日期字符串
     */
    public static String getNowDate() {
        Calendar calendar = Calendar.getInstance();
        String now  = String.format("%d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        return now;
    }

    /**
     * 获取美国当前日期
     *  夏令时 GMT-7:00
     *  其他时 GMT-8:00
     * @return 日期字符串
     */
    public static String getUSANowDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT-7:00"));
        String date = String.format("%d-%d-%d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        return date;
    }


    /**
     * 字符串转Date
     * @param dateStr
     * @param formatStr
     * @return
     */
    public static Date convertDateStrToDate(String dateStr,String formatStr){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatStr);
        Date dd = null;
        try {
            dd = simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dd;
    }

    /**
     * 日期字符串增加 n 天
     * @param s
     * @param n
     * @param format
     * @return 日期字符串
     */
    public static String addDay(String s, int n,String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Calendar cd = Calendar.getInstance();
            cd.setTime(sdf.parse(s));
            cd.add(Calendar.DATE, n);//增加一天
            return sdf.format(cd.getTime());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 日期字符串增加 n 月
     * @param s
     * @param n
     * @param format
     * @return 日期字符串
     */
    public static String addMonth(String s, int n,String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Calendar cd = Calendar.getInstance();
            cd.setTime(sdf.parse(s));
            cd.add(Calendar.MONTH, n);//增加一个月
            return sdf.format(cd.getTime());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 计算两个日期间隔天数，比如2018-01-01与2018-01-03间隔2天
     * 如果要是算从起始日期算的第几天的话，还要+1
     * @param startDateStr
     * @param endDateStr
     * @return
     */
    public static Integer getIntervalBetweenTwoDates(String startDateStr, String endDateStr,String format) {
        if (startDateStr != null && endDateStr != null) {
            SimpleDateFormat sf = new SimpleDateFormat(format);
            Date start = null;
            try {
                start = sf.parse(startDateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date end = null;
            try {
                end = sf.parse(endDateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (start != null && end != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(start);
                long time1 = cal.getTimeInMillis();
                cal.setTime(end);
                long time2 = cal.getTimeInMillis();
                long between_days = (time2 - time1) / (1000 * 3600 * 24);
                return Integer.parseInt(String.valueOf(between_days));
            }
        }
        return null;
    }

}