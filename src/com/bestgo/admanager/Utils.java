package com.bestgo.admanager;

import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;
import com.google.gson.JsonObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class Utils {
    public static HashMap<String, String> countryCodeMap;
    private static Object lock = new Object();

    public static boolean isAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        HttpSession session = request.getSession();
        Object isAdmin = session.getAttribute("isAdmin");
        if (isAdmin == null) {
            JsonObject json = new JsonObject();
            json.addProperty("ret", 0);
            json.addProperty("message", "请先登录");
            response.getWriter().write(json.toString());
            return false;
        }
        return true;
    }

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

    public static double trimDouble(double value) {
        return Double.parseDouble(String.format("%.4f", value));
    }

    public static String getAccessToken() {
        try {
            JSObject jsObject = DB.simpleScan("ad_app_config").select("access_token").execute();
            return jsObject.get("access_token");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getFirstAdAccountId() {
        try {
            JSObject jsObject = DB.simpleScan("web_account_id").select("account_id").orderByAsc("id").execute();
            if (jsObject.hasObjectData()) {
                return jsObject.get("account_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static HashMap getCountryMap() {
        if (countryCodeMap == null) {
            try {
                synchronized (lock) {
                    countryCodeMap = new HashMap<>();
                    List<JSObject> list = DB.scan("app_country_code_dict").select("country_code", "country_name").execute();
                    for (JSObject one : list) {
                        String countryCode = one.get("country_code");
                        String countryName = one.get("country_name");
                        countryCodeMap.put(countryCode, countryName);
                    }
                }
            } catch (Exception ex) {
            }
        }
        return countryCodeMap;
    }


    /**
     * 从较大的Set集合中，找到与较小的Set集合不同的字符串对象，并放到list中返回
     * @param maxSet
     * @param minSet
     * @return 字符串集合
     */
    public static List<String> getDiffrentStrList(Set<String> maxSet, Set<String> minSet) {
        List<String> diff = new ArrayList<>();
        Map<String,Integer> map = new HashMap<>(maxSet.size());
        for (String str : maxSet) {
            map.put(str, 1);
        }
        for (String str : minSet) {
            if(map.get(str)!=null)
            {
                map.put(str, 2);
                continue;
            }
            diff.add(str);
        }
        for(Map.Entry<String, Integer> entry:map.entrySet())
        {
            if(entry.getValue()==1)
            {
                diff.add(entry.getKey());
            }
        }
        return diff;
    }

    /**
     * 将一个list根据某个属性去重，并遍历以逗号分隔拼接成字符串，用于in()里面的查询条件
     * @param list List<JSObject>
     * @param attr JSObject的某个属性
     * @param attrValueIsString  根据属性获得的每个值是否是字符串
     * @return 字符串
     */
    public static String getStrForListDistinctByAttrWithCommmas(List<JSObject> list,String attr,boolean attrValueIsString){
        Set<String> set = new HashSet<>();
        String returnStr = "";
        for(JSObject j : list){
            set.add(j.get(attr));
        }
        if(attrValueIsString){
            for(String s : set){
                returnStr += "'" +s + "',";
            }
        }else{
            for(String s : set){
                returnStr += s + ",";
            }
        }

        if(returnStr.length() >0){
            return  returnStr.substring(0,returnStr.length() - 1);
        }
        return null;
    }
}
