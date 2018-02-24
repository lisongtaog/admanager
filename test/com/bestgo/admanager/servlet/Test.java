package com.bestgo.admanager.servlet;

import com.bestgo.admanager.DateUtil;
import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;
import com.sun.org.apache.xpath.internal.SourceTree;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.System;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static sun.plugin.cache.FileVersion.regEx;

public class Test {

    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT-8:00"));
        String date = String.format("%d-%d-%d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        System.out.println("date=" + date);
        /*String a = "20171111";
        String b = "20180206";
        Integer intervalBetweenTwoDates = getIntervalBetweenTwoDates(a, b,"yyyyMMdd");
        System.out.println(intervalBetweenTwoDates);*/
    }
           /* try {
                //中文
                System.out.println("我我我我我我我我我我我我我我".getBytes("iso8859-1").length);
                //英文
                System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaa".getBytes("iso8859-1").length);
                //俄文
                System.out.println("иииииииииииииииииииииииии".getBytes("iso8859-1").length);
                //德文
                System.out.println("üüüüüüüüüüüüüüüüüüüüüüüüü".getBytes("iso8859-1").length);
                System.out.println("ما هي الصيغة لحساب الطول؟".getBytes("iso8859-1").length);
                System.out.println("の計算の長さの公式は何私".length());
                System.out.println("그것의 길이를 계산하는 공".length());


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }*/


//            String sql = "select tag_name,max_bidding from web_tag";
//            List<JSObject> list = null;
//            try {
//                list = DB.findListBySql(sql);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            if(list != null && list.size() > 0){
//                for(JSObject j : list){
//                    System.out.println((String)j.get("tag_name")+" " +(double)j.get("max_bidding"));
//                    System.out.println();
//                }
//            }
//            Map<String,Double> sevenDaysTotalSpendMap = new HashMap<>();
//            Double xx = sevenDaysTotalSpendMap.get("xx");
//            String str = "/home/fan/ad_auto翻译/facebook_ads_images/WeatherV6/1011/6/weather8j.pg";
//
//            String reg = "\\p{InCJK Unified Ideographs}&&\\P{Cn}";
//            Pattern pattern = Pattern.compile(reg);
//            boolean result = pattern.matcher(str.trim()).find();
//            System.out.println(result);
            /*Calendar calendar = Calendar.getInstance();
            String now  = String.format("%d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
            System.out.println(now);*/

//            File file = new File("https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=tup&step_word=&hs=2&pn=0&spn=0&di=44781793010&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=0&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=undefined&cs=2168427908%2C4072089613&os=1749316700%2C501427981&simid=0%2C0&adpicid=0&lpn=0&ln=1997&fr=&fmq=1515231160249_R&fm=&ic=undefined&s=undefined&se=&sme=&tab=0&width=undefined&height=undefined&face=undefined&ist=&jit=&cg=&bdtype=13&oriquery=&objurl=http%3A%2F%2Fimgsrc.baidu.com%2Fimgad%2Fpic%2Fitem%2F32fa828ba61ea8d3d8d6c33f9c0a304e251f5810.jpg&fromurl=ippr_z2C%24qAzdH3FAzdH3Frwtxtg_z%26e3Bv54AzdH3Fri5p5v5ry6t2ipAzdH3F8c8mb899d&gsm=0&rpstart=0&rpnum=0");
//            boolean exists = file.exists();
//            String absolutePath = file.getAbsolutePath();
//            System.out.println(exists + "---"+absolutePath);
            /*Calendar calendar = Calendar.getInstance();
            String now  = String.format("%d%02d%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
            System.out.println(now);*/
            /*String[] accountNameArr = {"17", "17"};
            int createCountInt = 2;
            Calendar calendar = Calendar.getInstance();
            String campaignNameOld = "VideoV7_DE_All_0.09_20171129_";
            String campaignName = "";
            Random random = new Random(1);
            for (int j = 0, len = accountNameArr.length; j < len; j++) {
                for (int i = 0; i < createCountInt; i++) {
                    String now = String.format("%d-%02d-%02d %02d:%02d:%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH),
                            calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
                    String r = String.valueOf(random.nextInt()).substring(1, 5);
                    campaignName = campaignNameOld + accountNameArr[j] + "_" + r + String.valueOf(System.currentTimeMillis()).substring(1, 4) + i;
                    System.out.println("campaignName=" + campaignName);
                }
            }*/



    private static Integer getIntervalBetweenTwoDates(String startDateStr, String endDateStr,String format) {
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