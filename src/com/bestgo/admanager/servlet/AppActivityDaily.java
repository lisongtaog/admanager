package com.bestgo.admanager.servlet;

import com.bestgo.admanager.utils.DateUtil;
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
    /*

     */
    protected void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{
        String content = request.getParameter("content");
        String tagName = request.getParameter("tagName");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String theDate = request.getParameter("this_date");  //用于手写日志存储
        String path = request.getPathInfo();
        JsonObject json = new JsonObject();
        //JsonObject jsonInside = new JsonObject();
        //JsonArray arrayInside = new JsonArray();
        JsonArray array = new JsonArray();
        String message = "";
        if(path.startsWith("/create")){
            String sql = "SELECT COUNT(id) AS if_exist FROM web_ad_write_app_daily_record WHERE tag_name = '"+tagName+"' AND date='"+theDate+"'";
            long ifDataExist = 0;
            try{
                ifDataExist = DB.findOneBySql(sql).get("if_exist");
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
                boolean creation_success = DB.updateBySql(sql);
                if(creation_success){
                    message = "日志创建成功！";
                }else{
                    message = "创建过程访问数据库失败！";
                }
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
                    //按日期列表遍历（从 endDate 到 startDate-1）
                    for(int i=0;i<=days_between;i++ ){
                        JsonObject jsonMiddle = new JsonObject(); //确保每次添进arrayMiddle里的数据都是新的，而不会随着引用的改变而改变
                        String thisDate = DateUtil.addDay(endDate,-i,"yyyy-MM-dd");
                        //创建用于标示当天某国家是否有【新建】或【开启】操作的变量
                        int exist_open_create_flag = 0;
                        if(i<days_between){
                            sql = "SELECT campaign_id,content,exist_open_create FROM web_ad_system_app_daily_record_result WHERE tag_name='"
                                    +tagName+"' AND date='"+thisDate+"' AND country_code='"+this_country_code+"'";
                            List<JSObject> campaignRecord = DB.findListBySql(sql);
                            if(campaignRecord.size()>0){
                                String campaign_data = "";
                                //循环用于拼接 系列ID 和 系统操作 内容的字符串
                                for(JSObject campaign_record_for:campaignRecord){
                                    String campaign_id = "[" + campaign_record_for.get("campaign_id") + "]";
                                    String content_string = campaign_record_for.get("content");
                                    int exist_open_create = campaign_record_for.get("exist_open_create");
                                    if(exist_open_create ==1){
                                        exist_open_create_flag = exist_open_create;
                                    }
                                    campaign_data += (campaign_id + content_string + "<br><br>");
                                }
                                jsonMiddle.addProperty("campaign_data"+i,campaign_data);
                                jsonMiddle.addProperty("exist_open_create"+i,exist_open_create_flag);
                                //jsonMiddle.addProperty("date",thisDate);
                            }else{
                                jsonMiddle.addProperty("campaign_data"+i,"");
                            }
                            //接下来查询 国家+日期+标签 得到是否全关停的状态，使用键值对 status i: 1 或 2
                            sql = "SELECT is_all_closed from web_ad_tag_country_status WHERE tag_name='"+tagName
                                    +"'AND country_code='"+this_country_code+"'AND date='"+thisDate+"'";
                            JSObject statusData = DB.findOneBySql(sql);
                            long status = 1;
                            if(statusData.hasObjectData()){
                                status = statusData.get("is_all_closed");
                            }
                            jsonMiddle.addProperty("status"+i,status);
                            arrayMiddle.add(jsonMiddle);
                        }else{
                            //在startDate的前一天再添加一个关于 全关状态 的键值对
                            sql = "SELECT is_all_closed from web_ad_tag_country_status WHERE tag_name='"+tagName
                                    +"'AND country_code='"+this_country_code+"'AND date='"+thisDate+"'";
                            JSObject statusData = DB.findOneBySql(sql);
                            long status = 1;
                            if(statusData.hasObjectData()){
                                status = statusData.get("is_all_closed");
                            }
                            jsonMiddle.addProperty("status"+i,status);
                            arrayMiddle.add(jsonMiddle);
                        }
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
