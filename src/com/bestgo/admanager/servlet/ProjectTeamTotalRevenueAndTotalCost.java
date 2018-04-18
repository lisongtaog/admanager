package com.bestgo.admanager.servlet;

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
import java.util.List;

/**
 * Author: mengjun
 * Date: 2018/3/6 13:52
 * Desc: 项目组的总收入与总支出
 */
@WebServlet(name = "ProjectTeamTotalRevenueAndTotalCost", urlPatterns = "/project_team_total_revenue_and_total_cost/*")
public class ProjectTeamTotalRevenueAndTotalCost extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Utils.isAdmin(request, response)) return;

        String path = request.getPathInfo();
        JsonObject jsonObject = new JsonObject();
        String endDate = request.getParameter("endDate");
        String startDate = request.getParameter("startDate");
        if (path.matches("/query_project_team_total_revenue_and_total_cost")) {
            JsonArray jsonArray = new JsonArray();
            try {
                String sqlG = "select team_name from web_ad_category_team";
                List<JSObject> teamList = DB.findListBySql(sqlG);
                for(JSObject team : teamList){
                    double totalSpends = 0;
                    double totalRevenues = 0;
                    String teamName = team.get("team_name");
                    JsonObject d = new JsonObject();
                    d.addProperty("team_name", teamName);
                    sqlG = "select t.id,google_package_id " +
                            "from web_ad_category_team ct, web_ad_tag_category tc, web_tag t, web_facebook_app_ids_rel air " +
                            "where tc.id = t.tag_category_id and ct.id = tc.team_id and t.tag_name = air.tag_name and team_name = '" + teamName + "'";
                    List<JSObject> listTag = DB.findListBySql(sqlG);
                    if (listTag != null && listTag.size() > 0) {
                        for (JSObject t : listTag) {
                            if (t.hasObjectData()) {
                                String google_package_id = t.get("google_package_id");
                                long tagId = t.get("id");
                                sqlG = "select sum(ch.total_spend) as spend " +
                                        " from web_ad_campaigns c, web_ad_campaigns_history ch, " +
                                        "(select distinct campaign_id from web_ad_campaign_tag_rel where tag_id = " + tagId + ") rt " +
                                        "where rt.campaign_id = ch.campaign_id and c.campaign_id = ch.campaign_id " +
                                        "and date between '" + startDate + "' and '" + endDate + "'";
                                JSObject x = DB.findOneBySql(sqlG);
                                if (x.hasObjectData()) {
                                    totalSpends += Utils.convertDouble(x.get("spend"), 0);
                                }
                                sqlG = "select sum(ch.total_spend) as spend " +
                                        " from web_ad_campaigns_admob c, web_ad_campaigns_history_admob ch, " +
                                        "(select distinct campaign_id from web_ad_campaign_tag_admob_rel where tag_id = " + tagId + ") rt " +
                                        "where rt.campaign_id = ch.campaign_id and c.campaign_id = ch.campaign_id " +
                                        "and date between '" + startDate + "' and '" + endDate + "'";
                                x = DB.findOneBySql(sqlG);
                                if (x.hasObjectData()) {
                                    totalSpends += Utils.convertDouble(x.get("spend"), 0);
                                }


                                if (google_package_id != "") {
                                    sqlG = "select sum(revenue) as revenues " +
                                            "from web_ad_country_analysis_report_history where app_id = '"
                                            + google_package_id + "' and date between '" + startDate + "' and '" + endDate + "' ";
                                    x = DB.findOneBySql(sqlG);
                                    if (x.hasObjectData()) {
                                        totalRevenues += Utils.convertDouble(x.get("revenues"), 0);
                                    }
                                }
                            }
                        }
                    }
                    double totalIncomings = totalRevenues - totalSpends;
                    d.addProperty("total_revenues", Utils.trimDouble(totalRevenues, 0));
                    d.addProperty("total_spends", Utils.trimDouble(totalSpends, 0));
                    d.addProperty("total_incomings", Utils.trimDouble(totalIncomings, 0));
                    jsonArray.add(d);
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
}
