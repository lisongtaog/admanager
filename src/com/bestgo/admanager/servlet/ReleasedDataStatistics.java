package com.bestgo.admanager.servlet;

import com.bestgo.admanager.utils.DateUtil;
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
                String sql = "select team_name,category_name,t.tag_name,anticipated_incoming,anticipated_revenue " +
                        "from web_ad_category_team ct, web_ad_tag_category tc, web_tag t " +
                        "where tc.id = t.tag_category_id and ct.id = tc.team_id " +
                        ((likeTeamName == "") ? " " : " and team_name like '%" + likeTeamName + "%' ") +
                        ((likeCategoryName == "") ? " " : " and category_name like '%" + likeCategoryName + "%' ") +
                        " ORDER BY ct.id,tc.id,t.id ";
                List<JSObject> listTag = DB.findListBySql(sql);
                if (listTag != null && listTag.size() > 0) {
                    for (JSObject t : listTag) {
                        if (t.hasObjectData()) {
                            String teamName = t.get("team_name");
                            String categoryName = t.get("category_name");
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
                                double totalRevenue = 0;
                                double totalSpend = 0;
                                double totalIncoming = 0;
                                String date = DateUtil.addDay(endTime, i, "yyyy-MM-dd");
                                sql = "SELECT incoming,spend,revenue FROM web_ad_tag_released_data_statistics " +
                                      "WHERE team_name = '" + teamName + "' AND date = '" + date +"' " +
                                      "AND category_name = '" + categoryName + "' AND tag_name = '" + tagName + "'";
                                JSObject one = DB.findOneBySql(sql);
                                if(one.hasObjectData()){
                                    totalIncoming = one.get("incoming");
                                    totalSpend = one.get("spend");
                                    totalRevenue = one.get("revenue");
                                }
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