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
    //key=countryCode,value=countryName
    private static HashMap<String, String> countryCodeNameMap;

    //key=countryName,value=countryCode
    private static HashMap<String, String> countryNameCodeMap;
    private static Object lock = new Object();
    private static Object lock2 = new Object();

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

    /**
     * 获取key=countryCode,value=countryName的Map
     *
     * @return
     */
    public static HashMap getCountryCodeNameMap() {
        if (countryCodeNameMap == null) {
            try {
                synchronized (lock) {
                    countryCodeNameMap = new HashMap<>();
                    List<JSObject> list = DB.scan("app_country_code_dict").select("country_code", "country_name").execute();
                    for (JSObject one : list) {
                        String countryCode = one.get("country_code");
                        String countryName = one.get("country_name");
                        countryCodeNameMap.put(countryCode, countryName);
                    }
                }
            } catch (Exception ex) {
            }
        }
        return countryCodeNameMap;
    }


    /**
     * 获取key=countryName,value=countryCode的Map
     *
     * @return
     */
    public static HashMap getCountryNameCodeMap() {
        if (countryNameCodeMap == null) {
            try {
                synchronized (lock2) {
                    countryNameCodeMap = new HashMap<>();
                    List<JSObject> list = DB.scan("app_country_code_dict").select("country_code", "country_name").execute();
                    for (JSObject one : list) {
                        String countryCode = one.get("country_code");
                        String countryName = one.get("country_name");
                        countryNameCodeMap.put(countryName, countryCode);
                    }
                }
            } catch (Exception ex) {
            }
        }
        return countryNameCodeMap;
    }


    /**
     * 从较大的Set集合中，找到与较小的Set集合不同的字符串对象，并放到list中返回
     *
     * @param maxSet
     * @param minSet
     * @return 字符串集合
     */
    public static List<String> getDiffrentStrList(Set<String> maxSet, Set<String> minSet) {
        List<String> diff = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>(maxSet.size());
        for (String str : maxSet) {
            map.put(str, 1);
        }
        for (String str : minSet) {
            if (map.get(str) != null) {
                map.put(str, 2);
                continue;
            }
            diff.add(str);
        }
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() == 1) {
                diff.add(entry.getKey());
            }
        }
        return diff;
    }

    /**
     * 将一个list根据某个属性去重，并遍历以逗号分隔拼接成字符串，用于in()里面的查询条件
     *
     * @param list List<JSObject>
     * @param attr JSObject的某个属性,根据它来获取值
     * @return 字符串
     */
    public static String getStrForListDistinctByAttrWithCommmas(List<JSObject> list, String attr) {
        if (list == null || list.size() == 0) return "";

        Set<String> set = new HashSet<>();
        String returnStr = "";
        for (JSObject j : list) {
            if (j.hasObjectData()) {
                String str = j.get(attr);
                if (str == null || str == "" || str.equals("''")) {
                    continue;
                } else {
                    set.add(str);
                }
            }
        }

        if (set.size() == 1) {
            for (String s : set) {
                return "'" + s + "'";
            }
        } else {
            for (String s : set) {
                returnStr += "'" + s + "',";
            }
            if (returnStr.length() > 0) {
                return returnStr.substring(0, returnStr.length() - 1);
            }
        }
        return "";
    }


    /**
     * 获取两个List不同的部分
     *
     * @param mainList
     * @param compareList 被比较的List参照物
     * @param commonFiled 对象之间比较的属性
     * @return
     */
    public static List<JSObject> getDiffJSObjectList(List<JSObject> mainList, List<JSObject> compareList, String commonFiled) {
        if (compareList == null) {
            return mainList;
        }
        if (mainList == null) {
            return null;
        }
        List<JSObject> diff = new ArrayList<>();
        Map<String, String> map = new HashMap<>(mainList.size());
        for (JSObject x : compareList) {
            String key = x.get(commonFiled);
            if (key != null && key != "") {
                map.put(key, "compare");
            }
        }
        for (JSObject i : mainList) {
            String key = i.get(commonFiled);
            if (key != null && key != "") {
                if (!"compare".equals(map.get(key))) {
                    diff.add(i);
                }
            }
        }
        return diff;
    }

    /**
     * 图片目录遍历:获取这个目录下的所有文件夹路径和文件路径,并且只返回有效文件的路径
     *
     * @param file
     * @param resultAll       经校验合法的文件路径
     * @param returnDirectory 如果为true，则返回所有文件夹路径,否则返回文件路径
     * @return
     */
    public static List<String> ergodicImageDirectory(File file, List<String> resultAll, boolean returnDirectory) {
        File[] files = file.listFiles();
        if (files == null) return resultAll;// 判断目录下是不是空的
        for (File f : files) {
            if (f.isDirectory()) {
                if (returnDirectory) {
                    resultAll.add(f.getPath());
                }
                ergodicImageDirectory(f, resultAll, returnDirectory);
            } else {
                //需要在判断为文件以后判断文件后缀名是否合法
                String[] allowType = new String[]{".jpeg", ".gif", ".jpg", ".png"};
                String fileName = f.toString();
                if (typeValid(fileName, allowType)) {
                    resultAll.add(f.getPath());
                }
            }
        }
        return resultAll;
    }

    /**
     * 视频目录遍历:获取这个目录下的所有文件夹路径和文件路径,并且只返回有效文件的路径
     *
     * @param file
     * @param resultAll       经校验合法的文件路径
     * @param returnDirectory 如果为true，则返回所有文件夹路径,否则返回文件路径
     * @return
     */
    public static List<String> ergodicVideoDirectory(File file, List<String> resultAll, boolean returnDirectory) {
        File[] files = file.listFiles();
        if (files == null) return resultAll;// 判断目录下是不是空的
        for (File f : files) {
            if (f.isDirectory()) {
                if (returnDirectory) {
                    resultAll.add(f.getPath());
                }
                ergodicVideoDirectory(f, resultAll, returnDirectory);
            } else {
                //需要在判断为文件以后判断文件后缀名是否合法
                String[] allowType = new String[]{".mov", ".mp4"};
                String fileName = f.toString();
                if (typeValid(fileName, allowType)) {
                    resultAll.add(f.getPath());
                }
            }
        }
        return resultAll;
    }

    /**
     * 文件后缀检查方法
     */
    public static boolean typeValid(String contentType, String[] allowTypes) {
        if (null == contentType || "".equals(contentType)) {
            return false;
        }
        for (String type : allowTypes) {
            if (contentType.indexOf(type) > -1 || contentType.indexOf(type.toUpperCase()) > -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 执行Shell命令
     *
     * @param cmd
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    public static boolean executeShellCommand(String cmd) throws InterruptedException, IOException {
        String[] cmdparts = {"/bin/sh", "-c", cmd};
        Process process = Runtime.getRuntime().exec(cmdparts);
        process.waitFor();
        return true;
    }

    /**
     * 获取标签名称与包ID（appID）的Map
     *
     * @return
     */
    public static Map<String, String> getTagNamePackageIdMap() {
        Map<String, String> map = new HashMap<>();
        List<JSObject> list = null;
        try {
            list = DB.findListBySql("SELECT tag_name,google_package_id FROM web_facebook_app_ids_rel");
            for (int i = 0, len = list.size(); i < len; i++) {
                JSObject js = list.get(i);
                String tagName = js.get("tag_name");
                String googlePackageId = js.get("google_package_id");
                map.put(googlePackageId, tagName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 获取标签名称与包ID（appID）的Map
     *
     * @return
     */
    public static Map<String, String> getPackageIdByTagNameMap() {
        Map<String, String> map = new HashMap<>();
        List<JSObject> list = null;
        try {
            list = DB.findListBySql("SELECT tag_name,google_package_id FROM web_facebook_app_ids_rel");
            for (int i = 0, len = list.size(); i < len; i++) {
                JSObject js = list.get(i);
                String tagName = js.get("tag_name");
                String googlePackageId = js.get("google_package_id");
                map.put(tagName, googlePackageId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public  static List<JSObject> fetchFBAccountList(boolean isAcitve){
        List<JSObject> list = new ArrayList<>();
        try {
            if (isAcitve) {
                list = DB.findListBySql("SELECT account_id,short_name FROM web_account_id WHERE `status` = 1");
            } else {
                list = DB.findListBySql("SELECT account_id,short_name FROM web_account_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public  static List<JSObject> fetchAdwordsAccountList(boolean isAcitve){
        List<JSObject> list = new ArrayList<>();
        try {
            if (isAcitve) {
                list = DB.findListBySql("SELECT account_id,short_name FROM web_account_id_admob WHERE `status` = 1");
            } else {
                list = DB.findListBySql("SELECT account_id,short_name FROM web_account_id_admob");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
