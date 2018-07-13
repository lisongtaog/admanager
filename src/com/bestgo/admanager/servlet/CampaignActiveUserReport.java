package com.bestgo.admanager.servlet;

import com.bestgo.admanager.utils.DateUtil;
import com.bestgo.admanager.utils.NumberUtil;
import com.bestgo.admanager.utils.Utils;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Xixi
 * Date: 7/12/2018
 * Desc: 按标签和安装日期查询活跃用户数量
 */
@WebServlet(name = "CampaignActiveUserReport", urlPatterns = {"/campaign_active_user_report/*"})
public class CampaignActiveUserReport extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            doRequest(request,response);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void doRequest(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException{
        JsonObject json = new JsonObject();
//        String path = request.getPathInfo();
        String tagName = request.getParameter("tagName");
        String installedDate = request.getParameter("installedDate");

        //得到上限共20天内符合条件的最大日期
        String limitDate = DateUtil.addDay(installedDate,19,"yyyy-MM-dd");
        try{
            String sql = "SELECT id FROM web_tag WHERE tag_name = '"+ tagName + "'";
            long tagId = DB.findOneBySql(sql).get("id");
            sql = "SELECT a.event_date,a.campaign_name,a.campaign_id,a.active_num,b.country_name FROM " +
                    "web_ad_tag_campaign_active_user_history_admob a,app_country_code_dict b "+
                    "WHERE tag_id = "+tagId+" AND event_date BETWEEN '"+installedDate+"' AND '"+ limitDate + "' AND a.country_code = b.country_code";
            List<JSObject> list = DB.findListBySql(sql);
            JsonArray array = new JsonArray();
            if(list.size()>0){
                for(JSObject j: list){
                    JsonObject ji = new JsonObject();
                    ji.addProperty("campaign_id",j.get("campaign_id").toString());
                    ji.addProperty("campaign_name",j.get("campaign_name").toString());
                    ji.addProperty("country_name",j.get("country_name").toString());
                    ji.addProperty("event_date",j.get("event_date").toString());
                    ji.addProperty("active_num",j.get("active_num").toString());
                    array.add(ji);
                }
                json.addProperty("ret",1);
                json.add("data",array);
            }else{
                json.addProperty("ret",0);
                json.addProperty("message","此条件下无数据");
            }
        }catch(Exception e){
            e.printStackTrace();
            json.addProperty("ret",0);
            json.addProperty("message",e.getMessage());
        }
        response.getWriter().write(json.toString());
    }

}
