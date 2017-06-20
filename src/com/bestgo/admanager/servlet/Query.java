package com.bestgo.admanager.servlet;

import com.bestgo.admanager.Utils;
import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "query", urlPatterns = {"/query"}, asyncSupported = true)
public class Query extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Utils.isAdmin(request, response)) return;

        JsonObject json = new JsonObject();

        String tag = request.getParameter("tag");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");

        try {
            JSObject tagObject = DB.simpleScan("web_tag")
                    .select("id", "tag_name")
                    .where(DB.filter().whereEqualTo("tag_name", tag)).execute();
            if (tagObject.hasObjectData()) {
                Long id = tagObject.get("id");
                List<JSObject> list = DB.scan("web_ad_campaign_tag_rel").select("campaign_id")
                        .where(DB.filter().whereEqualTo("tag_id", id)).execute();
                String campaignIds = "";
                for (int i = 0; i < list.size(); i++) {
                    campaignIds += (list.get(i).get("campaign_id") + ",");
                }
                if (campaignIds.length() > 0) {
                    campaignIds = campaignIds.substring(0, campaignIds.length() - 1);
                }

                String sql = "select ch.campaign_id, account_id, campaign_name,c.status, create_time, c.budget, c.bidding, sum(ch.total_spend) as spend, " +
                        "sum(ch.total_installed) as installed, sum(ch.total_impressions) as impressions " +
                        ",sum(ch.total_click) as click from web_ad_campaigns c, web_ad_campaigns_history ch " +
                        "where c.campaign_id=ch.campaign_id\n" +
                        "and date between '" + startTime +"' and '" + endTime +"' " +
                        "and c.campaign_id in (" + campaignIds + ")" +
                        "group by ch.campaign_id;";
                Logger logger = Logger.getRootLogger();
                logger.debug(sql);
                list = DB.findListBySql(sql);
                JsonObject jsonObject = new JsonObject();
                JsonArray array = new JsonArray();
                double total_spend = 0;
                double total_installed = 0;
                double total_impressions = 0;
                double total_click = 0;
                double total_ctr = 0;
                double total_cpa = 0;
                double total_cvr = 0;
                for (int i = 0; i < list.size(); i++) {
                    JSObject one = list.get(i);
                    String campaign_id = one.get("campaign_id");
                    String account_id = one.get("account_id");
                    String campaign_name = one.get("campaign_name");
                    String status = one.get("status");
                    String create_time = one.get("create_time").toString();
                    double budget = one.get("budget");
                    double bidding = one.get("bidding");
                    double spend = Utils.convertDouble(one.get("spend"), 0);
                    double installed = Utils.convertDouble(one.get("installed"), 0);
                    double impressions = Utils.convertDouble(one.get("impressions"), 0);
                    double click = Utils.convertDouble(one.get("click"), 0);
                    double ctr = impressions > 0 ? click / impressions : 0;
                    double cpa = installed > 0 ? spend / installed : 0;
                    double cvr = click > 0 ? installed / click : 0;
                    total_spend += spend;
                    total_installed += installed;
                    total_impressions += impressions;
                    total_click += click;
                    total_ctr = total_impressions > 0 ? total_click / total_impressions : 0;
                    total_cpa = total_installed > 0 ? total_spend / total_installed : 0;
                    total_cvr = total_click > 0 ? total_installed / total_click : 0;

                    JsonObject d = new JsonObject();
                    d.addProperty("campaign_id", campaign_id);
                    d.addProperty("account_id", account_id);
                    d.addProperty("campaign_name", campaign_name);
                    d.addProperty("status", status);
                    d.addProperty("create_time", create_time);
                    d.addProperty("budget", budget);
                    d.addProperty("bidding", bidding);
                    d.addProperty("spend", spend);
                    d.addProperty("installed", installed);
                    d.addProperty("click", click);
                    d.addProperty("ctr", Utils.trimDouble(ctr));
                    d.addProperty("cpa", Utils.trimDouble(cpa));
                    d.addProperty("cvr", Utils.trimDouble(cvr));
                    array.add(d);
                }
                jsonObject.add("array", array);
                jsonObject.addProperty("total_spend", total_spend);
                jsonObject.addProperty("total_installed", total_installed);
                jsonObject.addProperty("total_impressions", total_impressions);
                jsonObject.addProperty("total_click", total_click);
                jsonObject.addProperty("total_ctr", Utils.trimDouble(total_ctr));
                jsonObject.addProperty("total_cpa", Utils.trimDouble(total_cpa));
                jsonObject.addProperty("total_cvr", Utils.trimDouble(total_cvr));
                json.add("data", jsonObject);

                json.addProperty("ret", 1);
                json.addProperty("message", "执行成功");
            } else {
                json.addProperty("ret", 0);
                json.addProperty("message", "标签不存在");
            }
        } catch (Exception ex) {
            json.addProperty("ret", 0);
            json.addProperty("message", ex.getMessage());
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }

        response.getWriter().write(json.toString());
    }
}
