package com.bestgo.admanager.servlet;

import com.bestgo.admanager.DateUtil;
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

        JsonArray jsonArray = new JsonArray();

        if (path.startsWith("/query_operation_log")) {
            String tagName = request.getParameter("tagName");
            String countryName = request.getParameter("countryName");
            try {
                String sql = "select country_code from app_country_code_dict where country_name = '" + countryName + "'";
                JSObject countryCodeJSObject = DB.findOneBySql(sql);
                String country_code = countryCodeJSObject.get("country_code");

                String sqlA = "select operation_date,campaign_id,enabled,bidding,details_text " +
                        "from web_ad_campaign_operation_log where app_name = '"+tagName+"' " +
                        "and country_code = '" + country_code + "' order by operation_date desc";
                List<JSObject> objectList = DB.findListBySql(sqlA);

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
        }else  if (path.startsWith("/query_create_campaign_statistics")) {
            String campaignCreateTime = request.getParameter("campaignCreateTime");
            String addDay = DateUtil.addDay(campaignCreateTime, 1, "yyyy-MM-dd");
            try {
                List<JSObject> tags = DB.scan("web_tag")
                        .select("tag_name").execute();
                String sqlAllFacebook = "select app_name,count(id) as facebook_all  from ad_campaigns where create_time >= '"+campaignCreateTime+"' and create_time < '"+addDay+"' group by app_name";
                java.lang.System.out.println(sqlAllFacebook);
                List<JSObject> listAllF = DB.findListBySql(sqlAllFacebook);

                String sqlFacebookHander = "select app_name,count(id) as facebook_hander  from ad_campaigns where auto_create_id = 0 and create_time >= '"+campaignCreateTime+"' and create_time < '"+addDay+"' group by app_name";
                List<JSObject> listFH = DB.findListBySql(sqlFacebookHander);

                String sqlAllAdmob = "select app_name,count(id) as adwords_all from ad_campaigns_admob where create_time >= '"+campaignCreateTime+"' and create_time < '"+addDay+"' group by app_name";
                List<JSObject> listAllA = DB.findListBySql(sqlAllAdmob);

                String sqlAdwordsHander = "select app_name,count(id) as adwords_hander  from ad_campaigns_admob where auto_create_id = 0 and create_time >= '"+campaignCreateTime+"' and create_time < '"+addDay+"' group by app_name";
                List<JSObject> listAH = DB.findListBySql(sqlAdwordsHander);
                long total_all_f = 0;
                long total_all_a = 0;
                for(JSObject tag : tags){
                    Count count = new Count();
                    count.appName = tag.get("tag_name");

                    for(JSObject f : listAllF){
                        String app_name = f.get("app_name");
                        if(count.appName.equalsIgnoreCase(app_name)){
                            count.allF = f.get("facebook_all");
                            break;
                        }
                    }
                    for(JSObject fh : listFH){
                        String app_name = fh.get("app_name");
                        if(count.appName.equalsIgnoreCase(app_name)){
                            count.facebook_hander = fh.get("facebook_hander");
                            break;
                        }
                    }
                    count.facebook_auto = count.allF - count.facebook_hander;
                    for(JSObject a : listAllA){
                        String app_name = a.get("app_name");
                        if(count.appName.equalsIgnoreCase(app_name)){
                            count.allA = a.get("adwords_all");
                            break;
                        }
                    }

                    for(JSObject ah : listAH){
                        String app_name = ah.get("app_name");
                        if(count.appName.equalsIgnoreCase(app_name)){
                            count.adwords_hander = ah.get("adwords_hander");
                            break;
                        }
                    }
                    count.adwords_auto = count.allA - count.adwords_hander;
                    count.all = count.allA + count.allF;
                    if(count.all > 0){
                        total_all_a += count.allA;
                        total_all_f += count.allF;
                        JsonObject d = new JsonObject();
                        d.addProperty("app_name", count.appName);
                        d.addProperty("facebook_hander", count.facebook_hander);
                        d.addProperty("facebook_auto", count.facebook_auto);
                        d.addProperty("adwords_hander", count.adwords_hander);
                        d.addProperty("adwords_auto", count.adwords_auto);
                        d.addProperty("facebook_all", count.allF);
                        d.addProperty("adwords_all", count.allA);
                        d.addProperty("all", count.all);
                        jsonArray.add(d);
                    }

                }
                long total_all = total_all_a + total_all_f;
                jsonObject.addProperty("ret", 1);
                jsonObject.addProperty("total_all", total_all);
                jsonObject.addProperty("total_all_a", total_all_a);
                jsonObject.addProperty("total_all_f", total_all_f);
                jsonObject.addProperty("message", "执行成功");
                jsonObject.add("array", jsonArray);
            } catch (Exception e) {
                jsonObject.addProperty("ret", 0);
                jsonObject.addProperty("message", e.getMessage());
            }
        }
        response.getWriter().write(jsonObject.toString());
    }

    class Count{
        public String appName;
        public long allF;
        public long allA;
        public long all;
        public long facebook_hander;
        public long facebook_auto;
        public long adwords_auto;
        public long adwords_hander;
    }
}