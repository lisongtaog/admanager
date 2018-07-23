package com.bestgo.admanager.servlet;

import com.bestgo.admanager.Config;
import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@WebServlet(name = "CampaignCreateAdsShowUp", urlPatterns = "/campaign_create_ads_show_up/*")
public class CampaignCreateAdsShowUp extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String app_name = request.getParameter("appName");
        String region = request.getParameter("region");
        String path = request.getPathInfo();
        JsonObject json = new JsonObject();
        JsonArray array = new JsonArray();
        try {
            if (path.startsWith("/facebook")) {
                array = fetchAds(app_name, region, false);
            } else if (path.startsWith("/adwords")) {
                array = fetchAds(app_name, region, true);
            }
            if (array.size() > 0) {
                json.add("ads", array);
                json.addProperty("ret", 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            String Exception = e.getMessage();
            json.addProperty("ret", 0);
            json.addProperty("message", Exception);
        }
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(json.toString());
    }

    private JsonArray fetchAds(String appName, String region, boolean admobCheck) throws Exception {
        JsonArray adsArray = new JsonArray();
        if (admobCheck == false) {
            //未针对空广告做处理
            Map<String, String> regionLanguageRelMap = Config.getRegionLanguageRelMap();
            String[] regionArray = region.split(",");
            Set<String> languageSet = new HashSet<>();
            String language = "";
            for (int i = 0, len = regionArray.length; i < len; i++) {
                String s = regionLanguageRelMap.get(regionArray[i]);
                if (null == s) {
                    continue;
                }
                languageSet.add(s);
            }

            if (languageSet.size() == 1) {
                language = regionLanguageRelMap.get(regionArray[0]);
            } else {
                language = "English";
            }
            String sql = "select group_id,language,title,message from web_ad_descript_dict where app_name ='" + appName + "'and language = '" + language + "'";
            List<JSObject> ads = DB.findListBySql(sql);
            for (JSObject j : ads) {
                JsonObject temp = new JsonObject();
                Integer group_id = j.get("group_id");
                temp.addProperty("group_id", group_id);
                //选择worldWide，默认显示英语
                if ("null".equals(j.get("language").toString())) {
                    temp.addProperty("language", "English");
                } else {
                    temp.addProperty("language", j.get("language").toString());
                }
                temp.addProperty("title", j.get("title").toString());
                temp.addProperty("message", j.get("message").toString());
                adsArray.add(temp);
            }
        } else {
            //未针对空广告做处理
            Map<String, String> regionLanguageAdmobRelMap = Config.getRegionLanguageRelMap();
            String languageAdmob = "";
            String[] regionAdmobArray = region.split(",");
            Set<String> languageAdmobSet = new HashSet<>();
            for (int i = 0, len = regionAdmobArray.length; i < len; i++) {
                String s = regionLanguageAdmobRelMap.get(regionAdmobArray[i]);
                if (null == s) {
                    continue;
                }
                languageAdmobSet.add(s);
            }

            if (languageAdmobSet.size() == 1) {
                languageAdmob = regionLanguageAdmobRelMap.get(regionAdmobArray[0]);
            } else {
                languageAdmob = "English";
            }
            String sql = "select group_id,language,message1,message2,message3,message4 from web_ad_descript_dict_admob where app_name " +
                    "='" + appName + "'and language = '" + languageAdmob + "'";
            List<JSObject> ads = DB.findListBySql(sql);
            for (JSObject j : ads) {
                JsonObject temp = new JsonObject();
                Integer group_id = j.get("group_id");
                temp.addProperty("group_id", group_id);  //当 group_id 是 Integer类的时候需不需要
                //选择All，默认显示英语
                if ("null".equals(j.get("language").toString())) {
                    temp.addProperty("language", "English");
                } else {
                    temp.addProperty("language", j.get("language").toString());
                }
//                temp.addProperty("language", j.get("language").toString());
                temp.addProperty("message1", j.get("message1").toString());
                temp.addProperty("message2", j.get("message2").toString());
                temp.addProperty("message3", j.get("message3").toString());
                temp.addProperty("message4", j.get("message4").toString());
                adsArray.add(temp);
            }
        }
        return adsArray;
    }
}
