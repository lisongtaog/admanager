package com.bestgo.admanager.servlet;

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
import java.util.List;

/**
 * Author: Xixi
 * Date: 7/12/2018
 * Desc: 按标签和安装日期查询广告收益
 */
@WebServlet(name = "CampaignImpressionsRevenueAnalysis", urlPatterns = {"/campaign_impressions_revenue_report/*"})
public class CampaignImpressionsRevenueAnalysis extends HttpServlet {

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

        try{
            JsonArray array = new JsonArray();
            String sql = "SELECT id FROM web_tag WHERE tag_name = '"+ tagName + "'";
            long tagId = DB.findOneBySql(sql).get("id");
            sql = "SELECT m.installed_date,m.campaign_id,m.campaign_name,m.tag_id,m.country_code,m.country_name,\n" +
                    "m.1_day,m.2_day,m.3_day,m.4_day,m.5_day,m.6_day,m.7_day,m.8_day,m.9_day,m.10_day,m.11_day,m.12_day,m.13_day,m.14_day,m.15_day,m.16_day,m.17_day,m.18_day,m.19_day,m.20_day\n" +
                    "FROM\n" +
                    "(\n" +
                    "\tSELECT v.installed_date,v.campaign_id,v.campaign_name,v.tag_id,v.country_code,v.country_name,\n" +
                    "\tCASE WHEN DATEDIFF(event_date,installed_date)=0 THEN revenue ELSE 0 END  AS 1_day,\n" +
                    "\tCASE WHEN DATEDIFF(event_date,installed_date)=1 THEN revenue ELSE 0 END  AS 2_day,\n" +
                    "\tCASE WHEN DATEDIFF(event_date,installed_date)=2 THEN revenue ELSE 0 END  AS 3_day,\n" +
                    "\tCASE WHEN DATEDIFF(event_date,installed_date)=3 THEN revenue ELSE 0 END  AS 4_day,\n" +
                    "\tCASE WHEN DATEDIFF(event_date,installed_date)=4 THEN revenue ELSE 0 END  AS 5_day,\n" +
                    "\tCASE WHEN DATEDIFF(event_date,installed_date)=5 THEN revenue ELSE 0 END  AS 6_day,\n" +
                    "\tCASE WHEN DATEDIFF(event_date,installed_date)=6 THEN revenue ELSE 0 END  AS 7_day,\n" +
                    "\tCASE WHEN DATEDIFF(event_date,installed_date)=7 THEN revenue ELSE 0 END  AS 8_day,\n" +
                    "\tCASE WHEN DATEDIFF(event_date,installed_date)=8 THEN revenue ELSE 0 END  AS 9_day,\n" +
                    "\tCASE WHEN DATEDIFF(event_date,installed_date)=9 THEN revenue ELSE 0 END  AS 10_day,\n" +
                    "\tCASE WHEN DATEDIFF(event_date,installed_date)=10 THEN revenue ELSE 0 END  AS 11_day,\n" +
                    "\tCASE WHEN DATEDIFF(event_date,installed_date)=11 THEN revenue ELSE 0 END  AS 12_day,\n" +
                    "\tCASE WHEN DATEDIFF(event_date,installed_date)=12 THEN revenue ELSE 0 END  AS 13_day,\n" +
                    "\tCASE WHEN DATEDIFF(event_date,installed_date)=13 THEN revenue ELSE 0 END  AS 14_day,\n" +
                    "\tCASE WHEN DATEDIFF(event_date,installed_date)=14 THEN revenue ELSE 0 END  AS 15_day,\n" +
                    "\tCASE WHEN DATEDIFF(event_date,installed_date)=15 THEN revenue ELSE 0 END  AS 16_day,\n" +
                    "\tCASE WHEN DATEDIFF(event_date,installed_date)=16 THEN revenue ELSE 0 END  AS 17_day,\n" +
                    "\tCASE WHEN DATEDIFF(event_date,installed_date)=17 THEN revenue ELSE 0 END  AS 18_day,\n" +
                    "\tCASE WHEN DATEDIFF(event_date,installed_date)=18 THEN revenue ELSE 0 END  AS 19_day,\n" +
                    "\tCASE WHEN DATEDIFF(event_date,installed_date)=19 THEN revenue ELSE 0 END  AS 20_day\n" +
                    "\tfrom\n" +
                    "\t(\n" +
                    "\t\t\tSELECT d.installed_date,d.event_date,campaign_id,d.campaign_name,d.tag_id,d.country_code,c.country_name,\n" +
                    "\t\t\tSUM(impressions) AS impressions,SUM(revenue) AS revenue\n" +
                    "\t\t\tFROM web_ad_tag_campaign_impressions_revenue_history_admob d,app_country_code_dict c WHERE d.country_code = c.country_code\n" +
                    "\t\t\tGROUP BY installed_date,event_date,tag_id,campaign_id,campaign_name,country_code\n" +
                    "\t) v\n" +
                    ") m";
            sql += " WHERE tag_id = "+tagId+" AND installed_date = '"+ installedDate + "'" ;

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
