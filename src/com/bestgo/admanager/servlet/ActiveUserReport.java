package com.bestgo.admanager.servlet;

import com.bestgo.admanager.DateUtil;
import com.bestgo.admanager.Utils;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: mengjun
 * Date: 2018/2/2 21:15
 * Desc: 分析每个应用每个国家的活跃用户度的报告
 */
@WebServlet(name = "ActiveUserReport", urlPatterns = {"/active_user_report/*"})
public class ActiveUserReport extends HttpServlet {
    private static Map<String,String> countryCodeMap;
    static{
        if(countryCodeMap == null){
            countryCodeMap = new HashMap<>();
        }
        String sql = "select country_code,country_name from app_country_code_dict";
        List<JSObject> list = null;
        try {
            list = DB.findListBySql(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(list != null){
            for(JSObject j : list){
                if(j != null){
                    String countryCode = j.get("country_code");
                    String countryName = j.get("country_name");
                    if(countryCode != null && countryName != null){
                        countryCodeMap.put(countryCode,countryName);
                    }
                }
            }
        }
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Utils.isAdmin(request, response)) return;

        String path = request.getPathInfo();
        JsonObject jsonObject = new JsonObject();
        String tagName = request.getParameter("tagName");
        String sorterId = request.getParameter("sorterId");


        if (path.matches("/query_active_user_report")) {
            List<JSObject> list = null;
            JsonArray jsonArray = new JsonArray();
            try {
                String today = DateUtil.getNowDate();
                String yesterday = DateUtil.addDay(today,-1,"yyyy-MM-dd");//美国今天
                String sevenDayAgo = DateUtil.addDay(today,-7,"yyyy-MM-dd");//美国七天前
                String sql = "select total_installeds,country_code,avg_7_day_active,avg_14_day_active,avg_30_day_active,avg_60_day_active " +
                        " from ad_report_active_user_admob_rel_result where tag_name = '" + tagName + "' ";
                int sorter = 0;
                if (sorterId != null) {
                    sorter = Utils.parseInt(sorterId, 0);
                    switch(sorter) {
                        case 2091:
                            sql += " order by avg_7_day_active desc";
                            break;
                        case 91:
                            sql += " order by avg_7_day_active";
                            break;
                        case 2092:
                            sql += " order by avg_14_day_active desc";
                            break;
                        case 92:
                            sql += " order by avg_14_day_active";
                            break;
                        case 2093:
                            sql += " order by avg_30_day_active desc";
                            break;
                        case 93:
                            sql += " order by avg_30_day_active";
                            break;
                        case 2094:
                            sql += " order by avg_60_day_active desc";
                            break;
                        case 94:
                            sql += " order by avg_60_day_active";
                            break;
                    }
                }
                list = DB.findListBySql(sql);
                for (JSObject j : list) {
                    if(j.hasObjectData()){
                        JsonObject jo = new JsonObject();
                        String countryCode = j.get("country_code");
                        String countryName = countryCodeMap.get(countryCode);
                        JSObject one = null;
                        if(countryName == null){
                            sql = "select country_name from app_country_code_dict where country_code = '" + countryCode + "'";
                            one = DB.findOneBySql(sql);
                            if(one.hasObjectData()){
                                countryName = one.get("country_name");
                            }else{
                                countryName = "--";
                            }
                            countryCodeMap.put(countryCode,countryName);
                        }
                        double sevenDaysAvgARPU = 0;
                        sql = "SELECT avg(arpu) AS seven_days_avg_arpu FROM web_ad_country_analysis_report_history h,web_facebook_app_ids_rel r " +
                                "WHERE h.app_id = r.google_package_id AND tag_name = '" + tagName + "' AND country_code = '" +
                                countryCode + "' AND date BETWEEN '" + sevenDayAgo + "' and '" + yesterday + "'";
                        one = DB.findOneBySql(sql);
                        if(one.hasObjectData()){
                            sevenDaysAvgARPU = Utils.trimDouble(Utils.convertDouble(one.get("seven_days_avg_arpu"),0),3);
                        }
                        double totalInstalleds = Utils.trimDouble(Utils.convertDouble(j.get("total_installeds"), 0),3);
                        double avgSevenDayActive = Utils.trimDouble(Utils.convertDouble(j.get("avg_7_day_active"), 0),3);
                        double avgFourteenDayActive = Utils.trimDouble(Utils.convertDouble(j.get("avg_14_day_active"), 0),3);
                        double avgThirtyDayActive = Utils.trimDouble(Utils.convertDouble(j.get("avg_30_day_active"), 0),3);
                        double avgSixtyDayActive = Utils.trimDouble(Utils.convertDouble(j.get("avg_60_day_active"), 0),3);
                        jo.addProperty("country_name", countryName);
                        jo.addProperty("total_installeds", totalInstalleds);
                        jo.addProperty("avg_7_day_active", avgSevenDayActive);
                        jo.addProperty("avg_14_day_active", avgFourteenDayActive);
                        jo.addProperty("avg_30_day_active", avgThirtyDayActive);
                        jo.addProperty("avg_60_day_active", avgSixtyDayActive);
                        jo.addProperty("seven_days_avg_arpu", sevenDaysAvgARPU);
                        jo.addProperty("avg_7_day_active_mul_arpu", avgSevenDayActive * sevenDaysAvgARPU);
                        jo.addProperty("avg_14_day_active_mul_arpu", avgFourteenDayActive * sevenDaysAvgARPU);
                        jo.addProperty("avg_30_day_active_mul_arpu", avgThirtyDayActive * sevenDaysAvgARPU);
                        jo.addProperty("avg_60_day_active_mul_arpu", avgSixtyDayActive * sevenDaysAvgARPU);
                        jsonArray.add(jo);
                    }
                }

                jsonObject.add("array", jsonArray);
                jsonObject.addProperty("ret", 1);
                jsonObject.addProperty("message", "执行成功");
            } catch (Exception e) {
                jsonObject.addProperty("ret", 0);
                jsonObject.addProperty("message", e.getMessage());
            }
        }

        response.getWriter().write(jsonObject.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
