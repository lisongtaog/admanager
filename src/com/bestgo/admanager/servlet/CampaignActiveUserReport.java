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
public class CampaignActiveUserReport extends BaseHttpServlet {

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
        String tagName = request.getParameter("tagName");
        String installedDate = request.getParameter("installedDate");
        int index = Integer.parseInt(request.getParameter("page_index"));
        int size = Integer.parseInt(request.getParameter("page_size"));
        int order = Integer.parseInt(request.getParameter("order"));
        boolean desc = order < 1000;
        if (order >= 1000) order = order - 1000;

        String[] orders = {" order by campaign_id "," order by campaign_name", " order by country_name",
                " order by 1_day "," order by 2_day "," order by 3_day "," order by 4_day "," order by 5_day "," order by 6_day ",
                " order by 7_day "," order by 8_day "," order by 9_day "," order by 10_day "," order by 11_day "," order by 12_day ",
                " order by 13_day "," order by 14_day "," order by 15_day "," order by 16_day "," order by 17_day "," order by 18_day ",
                " order by 19_day "," order by 20_day "
        };

        //从视图里查询
        try{
            JsonArray array = new JsonArray();
            String sql = "SELECT id FROM web_tag WHERE tag_name = '"+ tagName + "'";
            long tagId = DB.findOneBySql(sql).get("id");
            sql = "SELECT campaign_id,campaign_name,1_day,2_day,3_day,4_day,5_day,6_day,7_day,8_day,9_day,10_day,11_day,12_day,13_day,14_day," +
                    "15_day,16_day,17_day,18_day,19_day,20_day,country_name FROM view_active_user WHERE tag_id = "+tagId+
                    " AND installed_date = '"+ installedDate + "'";
            int count = DB.findListBySql(sql).size();
            if (order < orders.length) {//单列排序
                sql += orders[order] + (desc ? " desc" : "");
            }
            sql += " limit " + index * size + "," + size;

            List<JSObject> list = DB.findListBySql(sql);

            if(list.size()>0){
                for(JSObject j: list){
                    JsonObject ji = new JsonObject();
                    ji.addProperty("campaign_id",j.get("campaign_id").toString());
                    ji.addProperty("campaign_name",j.get("campaign_name").toString());
                    ji.addProperty("country_name",j.get("country_name").toString());
                    for (int k = 1;k<21;k++){
                        String num = String.valueOf(k);
                        ji.addProperty(num+"_day",j.get(num+"_day").toString());
                    }
                    array.add(ji);
                }
                json.addProperty("ret",1);
                json.addProperty("total",count);
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
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(json.toString());
    }

}
