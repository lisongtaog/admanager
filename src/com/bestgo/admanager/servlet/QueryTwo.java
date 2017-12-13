package com.bestgo.admanager.servlet;

import com.bestgo.admanager.Utils;
import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@WebServlet(name = "QueryTwo", urlPatterns = {"/query_two/*"}, asyncSupported = true)
public class QueryTwo extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Utils.isAdmin(request, response)) return;

        String path = request.getPathInfo();
        JsonObject jsonObject = new JsonObject();

        String tagName = request.getParameter("tagName");
        String countryName = request.getParameter("countryName");


        if (path.startsWith("/query_operation_log")) {
            try {
                String sql = "select country_code from app_country_code_dict where country_name = '" + countryName + "'";
                JSObject countryCodeJSObject = DB.findOneBySql(sql);
                String country_code = countryCodeJSObject.get("country_code");

                String sqlA = "select operation_date,campaign_id,enabled,bidding,details_text " +
                        "from web_ad_campaign_operation_log where app_name = '"+tagName+"' " +
                        "and country_code = '" + country_code + "' order by operation_date desc";
                List<JSObject> objectList = DB.findListBySql(sqlA);
                JsonArray jsonArray = new JsonArray();
                boolean flag = false;
                boolean sign = false;
                String biddingsStr = "出价=<[";
                HashSet<Double> doubles = new HashSet<>();
                for(JSObject j : objectList){
                    double enabled = Utils.convertDouble(j.get("enabled"),0);
                    Double bidding = Utils.convertDouble(j.get("bidding"),0);
                    if(enabled == 1){
                        flag = true;
                    }
                    if(enabled == 0){
                        sign = true;
                    }
                    if(bidding != 0){
                        doubles.add(bidding);
                    }
                }
                for(Double d : doubles){
                    biddingsStr +=  d + ", ";
                }

                for(JSObject j : objectList){
                    String operation_date = j.get("operation_date").toString();
                    String campaign_id = j.get("campaign_id");
                    String details_text = j.get("details_text");
                    if(flag && sign){
                        details_text += " 部分关闭";
                    }else if(!flag && sign){
                        details_text += " 全部关闭";
                    }
                    JsonObject d = new JsonObject();
                    d.addProperty("operation_date", operation_date);
                    d.addProperty("campaign_id", campaign_id);
                    d.addProperty("details_text", biddingsStr + "]>" + details_text);
                    jsonArray.add(d);
                }
                jsonObject.addProperty("ret", 1);
                jsonObject.addProperty("message", "执行成功");
                jsonObject.add("array", jsonArray);
            } catch (Exception e) {
                jsonObject.addProperty("ret", 0);
                jsonObject.addProperty("message", e.getMessage());
            }
        }
        response.getWriter().write(jsonObject.toString());
    }
}