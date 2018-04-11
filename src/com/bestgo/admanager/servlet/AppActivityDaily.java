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

                //这里用于回显手写日志：最外层对应键content_array
                JsonArray handwrite_content = new JsonArray();
                for(int j = 0;j<days_between;j++){
                    JsonObject content_key_value = new JsonObject();
                    String thisDate = DateUtil.addDay(endDate,-j,"yyyy-MM-dd");
                    sql = "SELECT content FROM web_ad_write_app_daily_record WHERE tag_name = '"+tagName+"'AND date='"+thisDate+"'";
                    JSObject content_from_table = DB.findOneBySql(sql);
                    if(content_from_table.hasObjectData()){
                        String content_value = content_from_table.get("content");
                        content_key_value.addProperty("content",content_value);
                    }else{
                        String content_value = "(今日没有记录)";
                        content_key_value.addProperty("content",content_value);
                    }
                    handwrite_content.add(content_key_value);
                }

                // 这里用于显示系统操作日志，最外层对应键 country_array
                // 先建立国家为条件的循环(已确保List里的国家一定是表web_ad_system_app_daily_record_result里出现过的)
                for(JSObject country_for:countryCode){
                    String this_country_code = country_for.get("country_code");
                    JsonArray arrayMiddle = new JsonArray(); //每个国家循环创建一次，仅用于存放单国家不同日期的数组
                    //按日期列表遍历（从后往前）
                    for(int i=0;i<days_between;i++ ){
                        JsonObject jsonMiddle = new JsonObject(); //确保每次添进arrayMiddle里的数据都是新的，而不会随着引用的改变而改变
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
                                campaign_data += (campaign_id + content_string + "<br><br>");

                                //jsonInside.addProperty("campaign_id",campaign_id);
                                //jsonInside.addProperty("content",content_string);
                                //arrayInside.add(jsonInside);
                            }
                            jsonMiddle.addProperty("campaign_data"+i,campaign_data);
                            //jsonMiddle.addProperty("date",thisDate);
                        }else{
                            jsonMiddle.addProperty("campaign_data"+i,"");
                        }
                        arrayMiddle.add(jsonMiddle);
                    }
                    String this_country_name = country_for.get("country_name");
                    JsonObject jsonHere = new JsonObject();
                    jsonHere.addProperty("country_name",this_country_name);
                    jsonHere.add("date_data",arrayMiddle);
                    array.add(jsonHere); 
                }
                json.add("content_array",handwrite_content);
                json.add("country_array",array);
                response.setCharacterEncoding("utf-8");
                response.getWriter().write(json.toString());

            }catch(Exception e){
                e.printStackTrace();
                message = e.getMessage();
                response.getWriter().write(message);
            }
        }
    }
}
