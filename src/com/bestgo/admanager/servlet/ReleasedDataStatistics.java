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
        if (path.startsWith("/query_released_data_statistics")) {
            JsonArray jsonArray = new JsonArray();
            try {
                String sqlG = "select id,team_name from web_ad_category_team";
                if(!likeTeamName.isEmpty()){
                    sqlG = "select id,team_name from web_ad_category_team where team_name like '%" + likeTeamName + "%'";
                }
                List<JSObject> listJS = DB.findListBySql(sqlG);
                if(listJS != null && listJS.size()>0){
                    for(JSObject oneG : listJS){
                        if(oneG.hasObjectData()){
                            long teamId = oneG.get("id");
                            String teamName = oneG.get("team_name");
                            sqlG = "select id,category_name from web_ad_tag_category where team_id = " + teamId;
                            if(!likeCategoryName.isEmpty()){
                                sqlG = "select id,category_name from web_ad_tag_category where team_id = " + teamId + " and category_name like '%" + likeCategoryName + "%'";
                            }
                            List<JSObject> listCategory = DB.findListBySql(sqlG);
                            if(listCategory != null && listCategory.size()>0){
                                for(JSObject j : listCategory){
                                    if(j.hasObjectData()){
                                        long categoryId = j.get("id");
                                        String categoryName = j.get("category_name");
                                        sqlG = "select t.id,t.tag_name,google_package_id,anticipated_incoming,anticipated_revenue from web_tag t LEFT JOIN web_facebook_app_ids_rel r ON t.tag_name = r.tag_name  where tag_category_id = " + categoryId;
                                        List<JSObject> listTag = DB.findListBySql(sqlG);

                                        if(listTag != null && listTag.size()>0){
                                            for(JSObject t : listTag){
                                                if(t.hasObjectData()){
                                                    long tagId = t.get("id");
                                                    String tagName = t.get("tag_name");
                                                    double anticipatedIncoming = t.get("anticipated_incoming");
                                                    double anticipatedRevenue = t.get("anticipated_revenue");
                                                    JsonObject d = new JsonObject();
                                                    d.addProperty("team_name",teamName);
                                                    d.addProperty("category_name",categoryName);
                                                    d.addProperty("tag_name",tagName);
                                                    d.addProperty("anticipated_incoming",anticipatedIncoming);
                                                    d.addProperty("anticipated_revenue",anticipatedRevenue);
                                                    for(int i=0;i>-7;i--){
                                                        String date = DateUtil.addDay(endTime,i,"yyyy-MM-dd");
                                                        sqlG = "select sum(ch.total_spend) as spend " +
                                                                " from web_ad_campaigns c, web_ad_campaigns_history ch, " +
                                                                "(select distinct campaign_id from web_ad_campaign_tag_rel where tag_id = " + tagId + ") rt " +
                                                                "where rt.campaign_id = ch.campaign_id and c.campaign_id = ch.campaign_id " +
                                                                "and date = '" + date + "' " +
                                                                "and c.status != 'removed'";
                                                        JSObject x = DB.findOneBySql(sqlG);
                                                        double totalSpend = 0;
                                                        if(x.hasObjectData()){
                                                            totalSpend = Utils.convertDouble(x.get("spend"),0);
                                                        }
                                                        sqlG = "select sum(ch.total_spend) as spend " +
                                                                " from web_ad_campaigns_admob c, web_ad_campaigns_history_admob ch, " +
                                                                "(select distinct campaign_id from web_ad_campaign_tag_admob_rel where tag_id = " + tagId + ") rt " +
                                                                "where rt.campaign_id = ch.campaign_id and c.campaign_id = ch.campaign_id " +
                                                                "and date = '" + date + "' " +
                                                                "and c.status != 'removed'";
                                                        x = DB.findOneBySql(sqlG);
                                                        if(x.hasObjectData()){
                                                            totalSpend += Utils.convertDouble(x.get("spend"),0);
                                                        }
                                                        double totalRevenue = 0;
                                                        String google_package_id = t.get("google_package_id");
                                                        if(google_package_id != null){
                                                            sqlG =  "select sum(revenue) as revenues " +
                                                                    "from web_ad_country_analysis_report_history where app_id = '"
                                                                    + google_package_id + "' and date = '" + date + "'";
                                                            JSObject oneR = DB.findOneBySql(sqlG);
                                                            if(oneR != null){
                                                                totalRevenue = Utils.convertDouble(oneR.get("revenues"),0);
                                                            }
                                                        }
                                                        double totalIncoming = totalRevenue - totalSpend;
                                                        d.addProperty("total_revenue" + i,Utils.trimDouble(totalRevenue,0));
                                                        d.addProperty("total_spend" + i,Utils.trimDouble(totalSpend,0));
                                                        d.addProperty("total_incoming" + i,Utils.trimDouble(totalIncoming,0));
                                                    }
                                                    jsonArray.add(d);
                                                }
                                                }
                                        }

                                    }
                                }

                            }

                        }

                    }

                    jsonObject.add("array", jsonArray);
                    jsonObject.addProperty("ret", 1);
                }

            } catch (Exception e) {
                jsonObject.addProperty("ret", 0);
                jsonObject.addProperty("message", e.getMessage());
            }
        }
        response.getWriter().write(jsonObject.toString());
    }

}