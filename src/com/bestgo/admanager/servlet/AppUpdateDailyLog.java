package com.bestgo.admanager.servlet;

import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;
import com.google.gson.*;
import sun.text.normalizer.UTF16;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by Yunxi on 3/30/18.
 */
@WebServlet(name = "AppUpdateDailyLog", urlPatterns = {"/app_update_daily_log/*"})
public class AppUpdateDailyLog extends HttpServlet{
    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException,IOException{
        doPost(request,response);
    }
    protected void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{
        String content = request.getParameter("content");
        String tagName = request.getParameter("tag_name");
        String date = request.getParameter("date");
        String path = request.getPathInfo();
        JsonObject json = new JsonObject();
        JsonArray array = new JsonArray();
        String message = "";
        if(path.startsWith("/create")){
            String sql = "REPLACE INTO web_ad_write_app_daily_record (tag_name,date,content)" +
                         "VALUES ('"+ tagName + "','" + date +"','" + content +"')";
            try{
                DB.updateBySql(sql);
                json.addProperty("flag",1);
                message = "Succeeded！";
                json.addProperty("message",message);
            }catch(Exception e){
                message = e.getMessage();
                json.addProperty("flag",0);
                json.addProperty("message",message);
            }
            response.setCharacterEncoding("utf-8");
            response.getWriter().write(json.toString());
        }else if(path.startsWith("/search")){
            JsonObject jsonObject = new JsonObject();
            String sql ="SELECT a.country_name,b.content FROM app_country_code_dict a,web_ad_system_app_daily_record b WHERE a.country_code = b.country_code"+
                        " AND b.date = '" + date + "'AND b.tag_name = '" + tagName + "' ORDER BY country_name";
            try{
                List<JSObject> countryRecords = DB.findListBySql(sql);
                sql = "SELECT content FROM web_ad_write_app_daily_record WHERE date = '"+ date + "' AND tag_name = '"+ tagName +"'";
                JSObject contentRecorded = DB.findOneBySql(sql);
                String content_r = contentRecorded.get("content");
                jsonObject.addProperty("content",content_r);
                if(countryRecords.size()>0){
                    for(int i=0,len=countryRecords.size();i<len;i++){
                        JSObject j = countryRecords.get(i);
                        String Country = j.get("country_name");
                        String AppContent = j.get("content");
                        JsonObject js = new JsonObject();
                        js.addProperty("country",Country);
                        js.addProperty("content",AppContent);
                        array.add(js);
                    }
                    jsonObject.add("resultArray",array);
                }else{
                    jsonObject.add("array",null);
                }
                response.getWriter().write(jsonObject.toString());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
