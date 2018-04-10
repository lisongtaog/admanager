package com.bestgo.admanager.servlet;

import com.bestgo.admanager.DateUtil;
import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Yunxi on 3/30/18.
 * Desc:用于【应用日更记录】页面的操作
 */
@WebServlet(name = "AppActivityDaily", urlPatterns = {"/app_activity_daily/*"})
public class AppActivityDaily extends HttpServlet{
    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException,IOException{
        doPost(request,response);
    }
    protected void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{
        String content = request.getParameter("content");
        String tagName = request.getParameter("tagName");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String theDate = request.getParameter("this_date");
        String path = request.getPathInfo();
        JsonObject json = new JsonObject();
        //JsonObject jsonInside = new JsonObject();
        //JsonArray arrayInside = new JsonArray();
        JsonArray array = new JsonArray();
        String message = "";
        if(path.startsWith("/create")){
            String sql = "SELECT COUNT(id) AS if WHERE tag_name = '"+tagName+"' AND date='"+theDate+"'";
            long ifDataExist = 0;
            try{
                ifDataExist = DB.findOneBySql(sql).get("if");
            }catch(Exception e){
                e.printStackTrace();
            }
            if(ifDataExist>0){
                sql = "UPDATE web_ad_write_app_daily_record SET content='"+content+"'WHERE tag_name = '"+tagName+"' AND date='"+theDate+"'";
            }else{
                sql = "INSERT INTO web_ad_write_app_daily_record (tag_name,date,content)" +
                        "VALUES ('"+ tagName + "','" + theDate +"','" + content +"')";
            }
            try{
                DB.updateBySql(sql);
                message = "日志创建成功！";
                json.addProperty("message",message);
            }catch(Exception e){
                message = e.getMessage();
                json.addProperty("message",message);
            }
            response.setCharacterEncoding("utf-8");
            response.getWriter().write(json.toString());
        }else if(path.startsWith("/search")){
            String sqlC = "SELECT DISTINCT a.country_code,a.country_name FROM app_country_code_dict a,web_ad_system_app_daily_record_result b"+
                          " WHERE a.country_code=b.country_code AND b.tag_name='"+tagName+"'";
            try{
                List<JSObject> countryCode = DB.findListBySql(sqlC);
                String sql="";

                //得到包括两日期在内的天数
                SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
                Date start = date_format.parse(startDate);
                Date end = date_format.parse(endDate);
                long days_between = (end.getTime()-start.getTime())/1000/3600/24 + 1;

                //先建立国家为条件的循环(已确保List里的国家一定是表web_ad_system_app_daily_record_result里出现过的)
                for(JSObject country_for:countryCode){
                    String this_country_code = country_for.get("country_code");
                    JsonArray arrayMiddle = new JsonArray(); //每个国家循环创建一次，确保每次都会释放
                    JsonObject jsonMiddle = new JsonObject();
                    //按日期列表遍历（从后往前）
                    for(int i=0;i<days_between;i++ ){
                        String thisDate = DateUtil.addDay(endDate,-i,"yyyy-MM-dd");
                        sql = "SELECT campaign_id,content FROM web_ad_system_app_daily_record_result WHERE tag_name='"
                                +tagName+"' AND date='"+thisDate+"' AND country_code='"+this_country_code+"'";
                        List<JSObject> campaignRecord = DB.findListBySql(sql);
                        if(campaignRecord.size()>0){
                            String campaign_data = "";
                            //循环用于拼接 系列ID 和 系统操作 内容的字符串
                            for(JSObject campaign_record_for:campaignRecord){
                                String campaign_id = "[" + campaign_record_for.get("campaign_id") + "]";
                                String content_string = campaign_record_for.get("content");
                                campaign_data += (campaign_id + content_string + "<br><br>"); //梦君说的substring用索引去掉最后一个逗号，加上换行

                                //jsonInside.addProperty("campaign_id",campaign_id);
                                //jsonInside.addProperty("content",content_string);
                                //arrayInside.add(jsonInside);
                            }
                            jsonMiddle.addProperty("campaign_data",campaign_data);
                            //jsonMiddle.addProperty("date",thisDate);
                        }else{
                            jsonMiddle.addProperty("campaign_data",""); //确保没有数据也会占一个数组位
                            continue;
                        }
                        arrayMiddle.add(jsonMiddle);
                    }
                    String this_country_name = country_for.get("country_name");
                    JsonObject jsonHere = new JsonObject();
                    jsonHere.addProperty("country_name",this_country_name);
                    jsonHere.add("date_data",arrayMiddle);
                    array.add(jsonHere); 
                }
                response.setCharacterEncoding("utf-8");
                response.getWriter().write(array.toString());

            }catch(Exception e){
                e.printStackTrace();
                message = e.getMessage();
                response.getWriter().write(message);
            }
        }
    }
}
