package com.bestgo.admanager.servlet;

import com.bestgo.admanager.DateUtil;
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
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@WebServlet(name = "QueryOne", urlPatterns = {"/query_one/*"}, asyncSupported = true)
public class QueryOne extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Utils.isAdmin(request, response)) return;

        String path = request.getPathInfo();
        JsonObject json = new JsonObject();

        String tag = request.getParameter("tag");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        String adwordsCheck = request.getParameter("adwordsCheck");
        String facebookCheck = request.getParameter("facebookCheck");
        HashMap<String ,String> countryMap = Utils.getCountryMap();

        if (path.startsWith("/query_not_has_data")) {
            try {
                JSObject tagObject = DB.simpleScan("web_tag")
                        .select("id", "tag_name")
                        .where(DB.filter().whereEqualTo("tag_name", tag)).execute();
                if (tagObject.hasObjectData()) {
                    Long tagId = tagObject.get("id");
                    JsonObject jsonObject = null;
                    if (adwordsCheck != null && adwordsCheck.equals("false") && facebookCheck != null && facebookCheck.equals("false")) {
                        JsonObject admob = fetchOneAppData(tagId, startTime, endTime, true,countryMap);
                        JsonObject facebook = fetchOneAppData(tagId, startTime, endTime, false,countryMap);
                        double total_spend = admob.get("total_spend").getAsDouble() + facebook.get("total_spend").getAsDouble();
                        double total_installed = admob.get("total_installed").getAsDouble() + facebook.get("total_installed").getAsDouble();
                        double total_impressions = admob.get("total_impressions").getAsDouble() + facebook.get("total_impressions").getAsDouble();
                        double total_click = admob.get("total_click").getAsDouble() + facebook.get("total_click").getAsDouble();
                        double total_ctr = total_impressions > 0 ? total_click / total_impressions : 0;
                        double total_cpa = total_installed > 0 ? total_spend / total_installed : 0;
                        double total_cvr = total_click > 0 ? total_installed / total_click : 0;
                        admob.addProperty("total_spend", total_spend);
                        admob.addProperty("total_installed", total_installed);
                        admob.addProperty("total_impressions", total_impressions);
                        admob.addProperty("total_click", total_click);
                        admob.addProperty("total_ctr", Utils.trimDouble(total_ctr));
                        admob.addProperty("total_cpa", Utils.trimDouble(total_cpa));
                        admob.addProperty("total_cvr", Utils.trimDouble(total_cvr));
                        JsonArray array = admob.getAsJsonArray("array");
                        JsonArray array1 = facebook.getAsJsonArray("array");
                        for (int i = 0; i < array1.size(); i++) {
                            array.add(array1.get(i));
                        }
                        jsonObject = admob;
                    } else {
                        jsonObject = fetchOneAppData(tagId, startTime, endTime, "true".equals(adwordsCheck),countryMap);
                    }

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
        }

        response.getWriter().write(json.toString());
    }


    private JsonObject fetchOneAppData(long tagId, String startTime, String endTime, boolean admobCheck,HashMap<String ,String> countryMap) throws Exception {
        String relationTable = "web_ad_campaign_tag_rel";
        String webAdCampaignTable = "web_ad_campaigns";
        String webAdCampaignHistoryTable = "web_ad_campaigns_history";
        String webAccountIdTable = "web_account_id";
        if (admobCheck) {
            relationTable = "web_ad_campaign_tag_admob_rel";
            webAdCampaignTable = "web_ad_campaigns_admob";
            webAdCampaignHistoryTable = "web_ad_campaigns_history_admob";
            webAccountIdTable = "web_account_id_admob";
        }

        List<JSObject> list = DB.scan(relationTable).select("campaign_id")
                .where(DB.filter().whereEqualTo("tag_id", tagId)).execute();


        Set<String> campaignIdSet = new HashSet<>();
        for(JSObject j : list){
            campaignIdSet.add(j.get("campaign_id"));
        }

        String campaignIds = "";
        for(String s : campaignIdSet){
            campaignIds += "'" + s + "',";
        }
        if(campaignIds != null && campaignIds.length()>0){
            campaignIds = campaignIds.substring(0,campaignIds.length()-1);
        }

        if (!campaignIds.isEmpty()) {
            String sql = "";
            if(admobCheck){
                List<JSObject> listAll = new ArrayList<>();
                List<JSObject> listHasData = new ArrayList<>();
                sql = "select campaign_id, a.account_id, short_name, campaign_name, create_time, status, budget, bidding, total_spend, total_installed, total_click, total_impressions, cpa,ctr, " +
                        "(case when total_click > 0 then total_installed/total_click else 0 end) as cvr " +
                        " from " + webAdCampaignTable + " a , "+webAccountIdTable+" b where a.status != 'paused' and a.status != 'removed' and " +
                        "campaign_id in (" + campaignIds + ") and a.account_id = b.account_id";
                listAll = DB.findListBySql(sql);
                sql = "select campaign_id, impressions from ( " +
                        "select ch.campaign_id, " +
                        " sum(ch.total_impressions) as impressions " +
                        " from " + webAdCampaignTable + " c, " + webAdCampaignHistoryTable + " ch " +
                        "where c.campaign_id = ch.campaign_id " +
                        "and date between '" + startTime + "' and '" + endTime + "' " +
                        "and c.status != 'removed' and c.campaign_id in (" + campaignIds + ") " +
                        "group by ch.campaign_id having impressions > 0 ) a ";
                listHasData = DB.findListBySql(sql);
                list = Utils.getDiffJSObjectList(listAll, listHasData, "campaign_id");
            }else{//Facebook
                sql = "select campaign_id, a.account_id, short_name, campaign_name, status, create_time, budget, bidding, spend, installed, impressions, click" +
                        ", (case when impressions > 0 then click/impressions else 0 end) as ctr" +
                        ", (case when installed > 0 then spend/installed else 0 end) as cpa" +
                        ", (case when click > 0 then installed/click else 0 end) as cvr" +
                        " from (" +
                        "select ch.campaign_id, account_id, campaign_name,c.status, create_time, c.budget, c.bidding, sum(ch.total_spend) as spend, " +
                        "sum(ch.total_installed) as installed, sum(ch.total_impressions) as impressions " +
                        ",sum(ch.total_click) as click from " + webAdCampaignTable + " c, " + webAdCampaignHistoryTable + " ch " +
                        "where c.campaign_id=ch.campaign_id and status != 'paused' " +
                        "and date between '" + startTime + "' and '" + endTime + "' " +
                        "and c.status != 'removed' and c.campaign_id in (" + campaignIds + ") " +
                        "group by ch.campaign_id having impressions = 0 ) a  left join " + webAccountIdTable + " b on a.account_id = b.account_id";
                list = DB.findListBySql(sql);
            }
        } else {
            list.clear();
        }
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

            String short_name = one.get("short_name");
            String account_id = one.get("account_id");
            String campaign_name = one.get("campaign_name");
            String status = one.get("status");
            String create_time = one.get("create_time").toString();
            create_time = create_time.substring(0,create_time.length()-5);
            String country_code = one.get("country_code");
            double budget = one.get("budget");
            double bidding = one.get("bidding");
            double impressions = Utils.convertDouble(one.get("impressions"), 0);

            double spend = 0;
            double installed = 0;
            double click = 0;
            if(!admobCheck){
                spend = Utils.convertDouble(one.get("spend"), 0);
                installed = Utils.convertDouble(one.get("installed"), 0);
                click = Utils.convertDouble(one.get("click"), 0);
            }else {
                spend = Utils.convertDouble(one.get("total_spend"), 0);
                installed = Utils.convertDouble(one.get("total_installed"), 0);
                click = Utils.convertDouble(one.get("total_click"), 0);
            }
            if (impressions != 0 || click >0) {
                continue;
            }

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
            d.addProperty("short_name", short_name);
            d.addProperty("account_id", account_id);
            d.addProperty("campaign_name", campaign_name);
            d.addProperty("status", status);
            d.addProperty("create_time", create_time);
            d.addProperty("country_code", country_code);
            d.addProperty("country_name", countryMap.get(country_code));
            d.addProperty("budget", budget);
            d.addProperty("bidding", bidding);

            if (admobCheck) {
                d.addProperty("network", "admob");
            } else {
                d.addProperty("network", "facebook");
            }

            d.addProperty("impressions", impressions);
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
        return jsonObject;
    }
}
