package com.bestgo.admanager.servlet;

import com.bestgo.admanager.OperationResult;
import com.bestgo.admanager.Utils;
import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

@WebServlet(name = "Advert", urlPatterns = "/advert/*")
public class Advert extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Utils.isAdmin(request, response)) return;

        String path = request.getPathInfo();
        JsonObject json = new JsonObject();

        if (path.startsWith("/save_advert_facebook")) {
            String appName = request.getParameter("appName");
            String language = request.getParameter("language");
            String[] titleArr = new String[4];
            String[] messageArr = new String[4];
            titleArr[0] = request.getParameter("title11");
            messageArr[0] = request.getParameter("message11");
            titleArr[1] = request.getParameter("title22");
            messageArr[1] = request.getParameter("message22");
            titleArr[2] = request.getParameter("title33");
            messageArr[2] = request.getParameter("message33");
            titleArr[3] = request.getParameter("title44");
            messageArr[3] = request.getParameter("message44");
            String sql = "select group_id from web_ad_descript_dict where app_name='" + appName + "' and language = '" + language + "'";
            List<JSObject> list = fetchData(sql);
            OperationResult result = new OperationResult();
            try {
                if(list != null && list.size() > 0){
                    HashSet<Integer> set = new HashSet<>();
                    for(JSObject j : list){
                        Integer i = j.get("group_id");
                        set.add(i);
                    }
                    for(int i=1;i<=4;i++){
                        if(set.contains(i)){
                            DB.update("web_ad_descript_dict")
                                    .put("title", titleArr[i-1])
                                    .put("message", messageArr[i-1])
                                    .where(DB.filter().whereEqualTo("app_name", appName))
                                    .and(DB.filter().whereEqualTo("language", language))
                                    .and(DB.filter().whereEqualTo("group_id", i))
                                    .execute();
                        }else{
                            DB.insert("web_ad_descript_dict")
                                    .put("language", language)
                                    .put("title", titleArr[i-1])
                                    .put("message", messageArr[i-1])
                                    .put("group_id", i)
                                    .put("app_name", appName)
                                    .execute();
                        }
                    }
                    json.addProperty("existData","true");
                }else{
                    for(int i=1;i<=4;i++){
                        DB.insert("web_ad_descript_dict")
                                .put("language", language)
                                .put("title", titleArr[i-1])
                                .put("message", messageArr[i-1])
                                .put("group_id", i)
                                .put("app_name", appName)
                                .execute();
                    }
                    json.addProperty("existData","false");
                }
                result.result = true;
            } catch (Exception ex) {
                result.message = ex.getMessage();
                result.result = false;
            }
            json.addProperty("ret", result.result ? 1 : 0);
            json.addProperty("message", result.message);
        } else if (path.startsWith("/query_before_insertion")) {
            String appName = request.getParameter("appName");
            String language = request.getParameter("language");
            List<JSObject> list =null;
            try {
                if(appName != "" && language != ""){
                    String sql = "select group_id,title,message from web_ad_descript_dict where app_name = '" + appName + "' and language = '" + language + "'";
                    list = DB.findListBySql(sql);

                }
                if(list != null && list.size()>0){
                    JsonArray array = new JsonArray();
                    for(JSObject one: list){
                        JsonObject j = new JsonObject();
                        String title = one.get("title");
                        String message = one.get("message");
                        int groupId = one.get("group_id");
                        j.addProperty("title", title);
                        j.addProperty("group_id", groupId);
                        j.addProperty("message", message);
                        array.add(j);
                    }
                    json.add("array",array);
                    json.addProperty("ret", 1);
                }

            } catch (Exception e) {
                json.addProperty("ret", 0);
                e.printStackTrace();
            }
        }

        response.getWriter().write(json.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public static List<JSObject> fetchData(String sql) {
        List<JSObject> list = new ArrayList<>();
        try {
            return DB.findListBySql(sql);
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return list;
    }
}
