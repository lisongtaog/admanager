package com.bestgo.admanager;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
    public static String getNow() {
        Calendar calendar = Calendar.getInstance();
        String now  = String.format("%d-%02d-%02d %02d:%02d:%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
        return now;
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
            //cd.add(Calendar.MONTH, n);//增加一个月
            return sdf.format(cd.getTime());
        } catch (Exception e) {
            return null;
        }
    }

}