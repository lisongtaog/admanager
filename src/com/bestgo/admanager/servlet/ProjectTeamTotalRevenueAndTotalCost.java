package com.bestgo.admanager.servlet;

import com.bestgo.admanager.bean.AppBean;
import com.bestgo.admanager.utils.NumberUtil;
import com.bestgo.admanager.utils.Utils;
import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: mengjun
 * Date: 2018/3/6 13:52
 * Desc: 项目组的总收入与总支出
 */
@WebServlet(name = "ProjectTeamTotalRevenueAndTotalCost", urlPatterns = "/project_team_total_revenue_and_total_cost/*")
public class ProjectTeamTotalRevenueAndTotalCost extends BaseHttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doPost(request, response);
        if (!Utils.isAdmin(request, response)) return;

        String path = request.getPathInfo();
        JsonObject jsonObject = new JsonObject();
        String endDate = request.getParameter("endDate");
        String startDate = request.getParameter("startDate");
        if (path.matches("/query_project_team_total_revenue_and_total_cost")) {
            JsonArray jsonArray = new JsonArray();
            try {
                Map<String, List<AppBean>> appBeanMap = fetchTeamTagAppIdMap();
                Map<Long, Double> adwordsTagIdCostMap = fetchAdwordsTagIdCostMap(startDate, endDate);
                Map<Long, Double> facebookTagIdCostMap = fetchFacebookTagIdCostMap(startDate, endDate);
                Map<String, Double> appIdRevenueMap = fetchAppIdRevenueMap(startDate, endDate);
                String teamName = null;
                double totalCost = 0.0;
                double totalRevenue = 0.0;
                JsonObject d = null;
                for (Map.Entry<String,List<AppBean>> entry : appBeanMap.entrySet()) {
                    teamName = entry.getKey();
                    List<AppBean> appBeanList = entry.getValue();
                    for (int i = 0,len = appBeanList.size();i < len;i++) {
                        AppBean appBean = appBeanList.get(i);
                        Double adwordsCost = adwordsTagIdCostMap.get(appBean.tagId);
                        if (adwordsCost == null) {
                            adwordsCost = 0.0;
                        }
                        totalCost += adwordsCost;
                        Double fbCost = facebookTagIdCostMap.get(appBean.tagId);
                        if (fbCost == null) {
                            fbCost = 0.0;
                        }
                        totalCost += fbCost;
                        Double currRevenue = appIdRevenueMap.get(appBean.appId);
                        if (currRevenue == null) {
                            currRevenue = 0.0;
                        }
                        totalRevenue += currRevenue;
                    }
                    d = new JsonObject();
                    d.addProperty("team_name", teamName);
                    double totalIncoming = totalRevenue - totalCost;
                    d.addProperty("total_revenues", NumberUtil.trimDouble(totalRevenue, 0));
                    d.addProperty("total_spends", NumberUtil.trimDouble(totalCost, 0));
                    d.addProperty("total_incomings", NumberUtil.trimDouble(totalIncoming, 0));
                    jsonArray.add(d);
                    totalCost = 0.0;
                    totalRevenue = 0.0;
                }

                jsonObject.add("array", jsonArray);
                jsonObject.addProperty("ret", 1);
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


    /**
     * Team与标签ID和应用ID的Map
     * @return
     */
    private Map<String,List<AppBean>> fetchTeamTagAppIdMap() {
        Map<String,List<AppBean>> map = new HashMap<>();
        AppBean appBean = null;
        String teamName = null;
        List<AppBean> appList = null;
        List<JSObject> list = new ArrayList<>();
        try {
            list = DB.findListBySql("SELECT team_name,t.id,google_package_id\n" +
                    "FROM web_ad_category_team ct, web_ad_tag_category tc, web_tag t, web_facebook_app_ids_rel air\n" +
                    "WHERE tc.id = t.tag_category_id AND ct.id = tc.team_id AND t.tag_name = air.tag_name order by ct.id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0,len = list.size(); i < len; i++) {
            JSObject one = list.get(i);
            if (one.hasObjectData()) {
                teamName = one.get("team_name");
                appList = map.get(teamName);
                if (appList == null) {
                    appList = new ArrayList<>();
                }
                appBean = new AppBean();
                appBean.tagId = one.get("id");
                appBean.appId = one.get("google_package_id");
                appList.add(appBean);
                map.put(teamName,appList);
            }
        }
        return map;
    }

    private Map<Long,Double> fetchFacebookTagIdCostMap(String startDate,String endDate) {
        Map<Long,Double> map = new HashMap<>();
        Long tagId = 0L;
        double totalSpend = 0.0;
        List<JSObject> list = new ArrayList<>();
        try {
            list = DB.findListBySql("select c.tag_id,sum(ch.total_spend) as spend\n" +
                    "from web_ad_campaigns c, web_ad_campaigns_history ch\n" +
                    "where c.campaign_id = ch.campaign_id\n" +
                    "and ch.date between '" + startDate + "' and '" + endDate + "' \n" +
                    "GROUP BY c.tag_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0,len = list.size(); i < len; i++) {
            JSObject one = list.get(i);
            if (one.hasObjectData()) {
                tagId = one.get("tag_id");
                totalSpend = NumberUtil.convertDouble(one.get("spend"),0);
                map.put(tagId,totalSpend);
            }
        }
        return map;
    }

    private Map<Long,Double> fetchAdwordsTagIdCostMap(String startDate,String endDate) {
        Map<Long,Double> map = new HashMap<>();
        Long tagId = 0L;
        double totalSpend = 0.0;
        List<JSObject> list = new ArrayList<>();
        try {
            list = DB.findListBySql("select c.tag_id,sum(ch.total_spend) as spend\n" +
                    "from web_ad_campaigns_admob c, web_ad_campaigns_history_admob ch\n" +
                    "where c.campaign_id = ch.campaign_id\n" +
                    "and ch.date between '" + startDate + "' and '" + endDate + "' \n" +
                    "GROUP BY c.tag_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0,len = list.size(); i < len; i++) {
            JSObject one = list.get(i);
            if (one.hasObjectData()) {
                tagId = one.get("tag_id");
                totalSpend = NumberUtil.convertDouble(one.get("spend"),0);
                map.put(tagId,totalSpend);
            }
        }
        return map;
    }

    private Map<String,Double> fetchAppIdRevenueMap(String startDate,String endDate) {
        Map<String,Double> map = new HashMap<>();
        String appId = null;
        double totalRevenue = 0.0;
        List<JSObject> list = new ArrayList<>();
        try {
            list = DB.findListBySql("SELECT app_id,sum(revenue) AS revenues\n" +
                    "FROM web_ad_country_analysis_report_history \n" +
                    "WHERE date BETWEEN '" + startDate + "' AND '" + endDate + "'\n" +
                    "GROUP BY app_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0,len = list.size(); i < len; i++) {
            JSObject one = list.get(i);
            if (one.hasObjectData()) {
                appId = one.get("app_id");
                totalRevenue = NumberUtil.convertDouble(one.get("revenues"),0);
                map.put(appId,totalRevenue);
            }
        }
        return map;
    }
}
