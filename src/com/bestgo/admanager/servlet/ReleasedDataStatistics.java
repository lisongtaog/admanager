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
import java.util.List;

/**
 * Author: mengjun
 * Date: 2018/2/20 18:06
 * Desc: 对每个项目组每个品类每个应用的投放数据的统计
 */
@WebServlet(name = "ReleasedDataStatistics", urlPatterns = {"/released_data_statistics/*"}, asyncSupported = true)
public class ReleasedDataStatistics extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Utils.isAdmin(request, response)) return;

        String path = request.getPathInfo();
        JsonObject jsonObject = new JsonObject();
        String likeCategoryName = request.getParameter("likeCategoryName");
        likeCategoryName = likeCategoryName.trim();
        String likeTeamName = request.getParameter("likeTeamName");
        likeTeamName = likeTeamName.trim();
        String endTime = request.getParameter("endTime");
        if (path.matches("/query_released_data_statistics")) {
            JsonArray jsonArray = new JsonArray();
            try {
                String sqlG = "select team_name,category_name,t.id,t.tag_name,anticipated_incoming,anticipated_revenue,google_package_id " +
                        "from web_ad_category_team ct, web_ad_tag_category tc, web_tag t, web_facebook_app_ids_rel air " +
                        "where tc.id = t.tag_category_id and ct.id = tc.team_id and t.tag_name = air.tag_name " +
                        ((likeTeamName == "") ? " " : " and team_name like '%" + likeTeamName + "%' ") +
                        ((likeCategoryName == "") ? " " : " and category_name like '%" + likeCategoryName + "%' ") +
                        " ORDER BY ct.id,tc.id,t.id ";
                List<JSObject> listTag = DB.findListBySql(sqlG);
                if (listTag != null && listTag.size() > 0) {
                    for (JSObject t : listTag) {
                        if (t.hasObjectData()) {
                            String teamName = t.get("team_name");
                            String categoryName = t.get("category_name");
                            String google_package_id = t.get("google_package_id");
                            long tagId = t.get("id");
                            JsonObject d = new JsonObject();
                            d.addProperty("team_name", teamName);
                            d.addProperty("category_name", categoryName);
                            String tagName = t.get("tag_name");
                            double anticipatedIncoming = t.get("anticipated_incoming");
                            double anticipatedRevenue = t.get("anticipated_revenue");
                            d.addProperty("tag_name", tagName);
                            d.addProperty("anticipated_incoming", anticipatedIncoming);
                            d.addProperty("anticipated_revenue", anticipatedRevenue);
                            for (int i = 0; i > -7; i--) {
                                String date = DateUtil.addDay(endTime, i, "yyyy-MM-dd");
                                sqlG = "select sum(ch.total_spend) as spend " +
                                        " from web_ad_campaigns c, web_ad_campaigns_history ch, " +
                                        "(select distinct campaign_id from web_ad_campaign_tag_rel where tag_id = " + tagId + ") rt " +
                                        "where rt.campaign_id = ch.campaign_id and c.campaign_id = ch.campaign_id " +
                                        "and date = '" + date + "' " +
                                        "and c.status != 'removed'";
                                JSObject x = DB.findOneBySql(sqlG);
                                double totalSpend = 0;
                                if (x.hasObjectData()) {
                                    totalSpend = Utils.convertDouble(x.get("spend"), 0);
                                }
                                sqlG = "select sum(ch.total_spend) as spend " +
                                        " from web_ad_campaigns_admob c, web_ad_campaigns_history_admob ch, " +
                                        "(select distinct campaign_id from web_ad_campaign_tag_admob_rel where tag_id = " + tagId + ") rt " +
                                        "where rt.campaign_id = ch.campaign_id and c.campaign_id = ch.campaign_id " +
                                        "and date = '" + date + "' " +
                                        "and c.status != 'removed'";
                                x = DB.findOneBySql(sqlG);
                                if (x.hasObjectData()) {
                                    totalSpend += Utils.convertDouble(x.get("spend"), 0);
                                }
                                double totalRevenue = 0;

                                if (google_package_id != "") {
                                    sqlG = "select sum(revenue) as revenues " +
                                            "from web_ad_country_analysis_report_history where app_id = '"
                                            + google_package_id + "' and date = '" + date + "'";
                                    x = DB.findOneBySql(sqlG);
                                    if (x.hasObjectData()) {
                                        totalRevenue = Utils.convertDouble(x.get("revenues"), 0);
                                    }
                                }
                                double totalIncoming = totalRevenue - totalSpend;
                                d.addProperty("total_revenue" + i, Utils.trimDouble(totalRevenue, 0));
                                d.addProperty("total_spend" + i, Utils.trimDouble(totalSpend, 0));
                                d.addProperty("total_incoming" + i, Utils.trimDouble(totalIncoming, 0));
                            }
                            jsonArray.add(d);

                        }
                    }
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

}