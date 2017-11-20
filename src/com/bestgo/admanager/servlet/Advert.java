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

        if (path.startsWith("/saveAdvertFacebook")) {
            String appName = request.getParameter("appName");
            String language = request.getParameter("language");
            String title = request.getParameter("title");
            String message = request.getParameter("message");
            String existData = request.getParameter("existData");
            List<JSObject> list = new ArrayList<>();
            String sql = "select title,message from web_ad_descript_dict where app_name='" + appName + "' and language = '" + language + "'";
            list = fetchData(sql);
            OperationResult result = new OperationResult();
            try {
                result.result = true;

                if (title.isEmpty()) {
                    result.result = false;
                    result.message = "标题不能为空";
                }
                if (message.isEmpty()) {
                    result.result = false;
                    result.message = "广告语不能为空";
                }

                if (result.result) {
                    if(list != null && list.size() > 0){
                        DB.update("web_ad_descript_dict")
                                .put("title", title)
                                .put("message", message)
                                .where(DB.filter().whereEqualTo("app_name", appName))
                                .and(DB.filter().whereEqualTo("language", language))
                                .execute();
                        json.addProperty("existData","true");
                    }else{
                        DB.insert("web_ad_descript_dict")
                                .put("language", language)
                                .put("title", title)
                                .put("message", message)
                                .put("app_name", appName)
                                .execute();
                        json.addProperty("existData","false");
                    }
                    result.result = true;
                }
            } catch (Exception ex) {
                result.message = ex.getMessage();
                result.result = false;
            }
            json.addProperty("ret", result.result ? 1 : 0);
            json.addProperty("message", result.message);
        } else if (path.startsWith("/queryBeforeInsertion")) {
            String appName = request.getParameter("appName");
            String language = request.getParameter("language");
            List<JSObject> list =null;
            try {
                if(appName != null && language != null){
                    String sql = "select title,message from web_ad_descript_dict where app_name = '" + appName + "' and language = '" + language + "' limit 1";
                    JSObject one  = DB.findOneBySql(sql);
                    String title = one.get("title");
                    String message = one.get("message");
                    json.addProperty("ret", 1);
                    json.addProperty("title", title);
                    json.addProperty("message", message);
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
