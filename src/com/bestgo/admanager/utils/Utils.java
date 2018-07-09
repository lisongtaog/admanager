package com.bestgo.admanager.utils;

import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;
import com.google.gson.JsonObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.*;
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
        LogUtils.logRequest(request);
        return true;
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
     * @param attr JSObject的某个属性,根据它来获取值
     * @return 字符串
     */
    public static String getStrForListDistinctByAttrWithCommmas(List<JSObject> list,String attr){
        if(list == null || list.size() == 0) return "";

        Set<String> set = new HashSet<>();
        String returnStr = "";
        for(JSObject j : list){
            if(j.hasObjectData()){
                String str = j.get(attr);
                if(str == null || str == "" || str.equals("''")) {
                    continue;
                }else {
                    set.add(str);
                }
            }
        }

        if(set.size() == 1){
            for(String s : set){
                return "'" + s + "'";
            }
        }else{
            for(String s : set){
                returnStr += "'" +s + "',";
            }
            if(returnStr.length() > 0){
                return  returnStr.substring(0,returnStr.length() - 1);
            }
        }
        return "";
    }


    /**
     * 获取两个List不同的部分
     * @param mainList
     * @param compareList 被比较的List参照物
     * @param commonFiled 对象之间比较的属性
     * @return
     */
    public static List<JSObject> getDiffJSObjectList(List<JSObject> mainList, List<JSObject> compareList,String commonFiled) {
        if(compareList == null){
            return mainList;
        }
        if(mainList == null){
            return null;
        }
        List<JSObject> diff = new ArrayList<>();
        Map<String,String> map = new HashMap<>(mainList.size());
        for (JSObject x : compareList) {
            String key = x.get(commonFiled);
            if(key != null && key != ""){
                map.put(key,"compare");
            }
        }
        for (JSObject i : mainList) {
            String key = i.get(commonFiled);
            if(key != null && key != ""){
                if(!"compare".equals(map.get(key))) {
                    diff.add(i);
                }
            }
        }
        return diff;
    }


    /**
     * 遍历某个目录，获取这个目录下的所有文件夹路径和文件路径
     * @param file
     * @param resultAll
     * @param returnDirectory 如果为true，则返回所有文件夹路径
     * @param returnFile  如果为true，则返回所有文件路径
     * @return
     */
    public static List<String> ergodicDirectory(File file, List<String> resultAll, boolean returnDirectory,boolean returnFile){
        File[] files = file.listFiles();
        if(files == null) return resultAll;// 判断目录下是不是空的
        for (File f : files) {
            if(f.isDirectory()){// 判断是否是文件夹
                if(returnDirectory){
                    resultAll.add(f.getPath());
                }
                ergodicDirectory(f,resultAll,returnDirectory,returnFile);// 调用自身,查找子目录
            }else{
                if(returnFile)
                    resultAll.add(f.getPath());
            }
        }
        return resultAll;
    }

}
