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
import java.util.*;

/**
 * 查询无数据的系列
 */
@WebServlet(name = "QueryNotExistData", urlPatterns = {"/query_not_exist_data"}, asyncSupported = true)
public class QueryNotExistData extends HttpServlet {
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
        String adwordsCheck = request.getParameter("adwordsCheck");
        String facebookCheck = request.getParameter("facebookCheck");
        String countryCode = request.getParameter("countryCode");
        String countryName = request.getParameter("countryName");
        String likeCampaignName = request.getParameter("likeCampaignName");
        String campaignCreateTime = request.getParameter("campaignCreateTime");
        try {
            JSObject tagObject = DB.simpleScan("web_tag")
                    .select("id", "tag_name")
                    .where(DB.filter().whereEqualTo("tag_name", tag)).execute();
            if (tagObject.hasObjectData()) {
                Long tagId = tagObject.get("id");
                JsonObject jsonObject = null;
                if (adwordsCheck != null && adwordsCheck.equals("false") && facebookCheck != null && facebookCheck.equals("false")) {
                    JsonObject admob = fetchOneAppData(tag,tagId, startTime, endTime, true,countryCode,likeCampaignName,campaignCreateTime);
                    JsonObject facebook = fetchOneAppData(tag,tagId, startTime, endTime, false,countryName,likeCampaignName,campaignCreateTime);
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
                    admob.addProperty("total_ctr", Utils.trimDouble(total_ctr,3));
                    admob.addProperty("total_cpa", Utils.trimDouble(total_cpa,3));
                    admob.addProperty("total_cvr", Utils.trimDouble(total_cvr,3));
                    JsonArray array = admob.getAsJsonArray("array");
                    JsonArray array1 = facebook.getAsJsonArray("array");
                    for (int i = 0; i < array1.size(); i++) {
                        array.add(array1.get(i));
                    }
                    jsonObject = admob;
                } else {
                    if("true".equals(adwordsCheck)){
                        jsonObject = fetchOneAppData(tag,tagId, startTime, endTime, true,countryCode,likeCampaignName,campaignCreateTime);
                    }else{
                        jsonObject = fetchOneAppData(tag,tagId, startTime, endTime, false,countryName,likeCampaignName,campaignCreateTime);
                    }

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

        response.getWriter().write(json.toString());
    }


    private JsonObject fetchOneAppData(String tagName,long tagId, String startTime, String endTime, boolean admobCheck,String country,String likeCampaignName,String campaignCreateTime) throws Exception {
        String webAdCampaignTagRelTable = "web_ad_campaign_tag_rel";
        String webAdCampaignsTable = "web_ad_campaigns";
        String adCampaignsTable = "ad_campaigns";
        String webAdCampaignsHistoryTable = "web_ad_campaigns_history";
        String webAdCampaignsCountryHistoryTable = "web_ad_campaigns_country_history";
        String webAccountIdTable = "web_account_id";
        String openStatus = "ACTIVE";
        List<JSObject> listAll = new ArrayList<>();
        List<JSObject> listHasData = new ArrayList<>();
        if (admobCheck) {
            adCampaignsTable = "ad_campaigns_admob";
            webAdCampaignTagRelTable = "web_ad_campaign_tag_admob_rel";
            webAdCampaignsTable = "web_ad_campaigns_admob";
            webAdCampaignsHistoryTable = "web_ad_campaigns_history_admob";
            webAccountIdTable = "web_account_id_admob";
            openStatus = "enabled";
            webAdCampaignsCountryHistoryTable = "web_ad_campaigns_country_history_admob";
        }
        List<JSObject> list = null;
        if(country == "" || country == null){
            list = DB.scan(webAdCampaignTagRelTable).select("campaign_id")
                    .where(DB.filter().whereEqualTo("tag_id", tagId)).execute();
        }else{
            String sql = "select campaign_id from " + adCampaignsTable + " where app_name = '"+ tagName+"' and country_region like '%" + country + "%'";
            list = DB.findListBySql(sql);
        }
        Set<String> campaignIdSet = new HashSet<>();
        for(JSObject j : list){
            campaignIdSet.add(j.get("campaign_id"));
        }

        String campaignIds = "";
        if(campaignCreateTime == "" || campaignCreateTime == null){
            for(String s : campaignIdSet){
                campaignIds += "'" + s + "',";
            }
        }else{
            List<JSObject> campaignIdJSObjectList = new ArrayList<>();
            String sqlQuery = "select campaign_id from "+adCampaignsTable+" where app_name = '"+ tagName +"' and create_time like '" + campaignCreateTime + "%'";
            campaignIdJSObjectList  = DB.findListBySql(sqlQuery);

            if(campaignIdJSObjectList != null && campaignIdJSObjectList.size()>0){
                Set<String> campaignIdcommonSet = new HashSet<>();
                for(JSObject j : campaignIdJSObjectList){
                    String campaign_id = j.get("campaign_id");
                    if(campaignIdSet.contains(campaign_id)){
                        campaignIdcommonSet.add(campaign_id);
                    }
                }
                for(String s : campaignIdcommonSet){
                    campaignIds += "'" + s + "',";
                }
            }
        }

        if(campaignIds != null && campaignIds.length()>0){
            campaignIds = campaignIds.substring(0,campaignIds.length()-1);
        }
        String sql = "";
        if (!campaignIds.isEmpty()) {

            if(admobCheck){
                sql = "select campaign_id, a.account_id, short_name, campaign_name, create_time, a.status, budget, bidding, total_spend, " +
                        " total_installed, total_click, cpa,ctr, " +
                        " (case when total_click > 0 then total_installed/total_click else 0 end) as cvr " +
                        "  from " + webAdCampaignsTable + " a LEFT JOIN " + webAccountIdTable + " b ON a.account_id = b.account_id " +
                        " where a.status = '" + openStatus + "'" +
                        " and campaign_id in (" + campaignIds + ") " +
                        ((likeCampaignName == "" || likeCampaignName == null) ? "" : " and campaign_name like %" + likeCampaignName + "%");
            }else{
                sql = "select campaign_id, a.account_id, short_name, campaign_name, create_time, a.status, budget, bidding, total_spend, " +
                        " total_installed, total_click, cpa,ctr, " +
                        " (case when total_click > 0 then total_installed/total_click else 0 end) as cvr " +
                        "  from " + webAdCampaignsTable + " a LEFT JOIN " + webAccountIdTable + " b ON a.account_id = b.account_id " +
                        " where a.status = '" + openStatus + "' AND b.status = 1 " +
                        " and campaign_id in (" + campaignIds + ") " +
                        ((likeCampaignName == "" || likeCampaignName == null) ? "" : " and campaign_name like %" + likeCampaignName + "%");
            }

            listAll = DB.findListBySql(sql);
            if(country == "" || country == null){
                sql = "select campaign_id, impressions from ( " +
                        "select ch.campaign_id, " +
                        " sum(ch.total_impressions) as impressions " +
                        " from " + webAdCampaignsTable + " c, " + webAdCampaignsHistoryTable + " ch " +
                        "where c.campaign_id = ch.campaign_id " +
                        "and date between '" + startTime + "' and '" + endTime + "' " +
                        "and c.status != 'removed' and c.campaign_id in (" + campaignIds + ") " +
                        "group by ch.campaign_id having impressions > 0 ) a ";
            }else{
                sql = "select campaign_id, impressions from ( " +
                        "select ch.campaign_id, " +
                        " sum(ch.total_impressions) as impressions " +
                        " from " + webAdCampaignsTable + " c, " + webAdCampaignsCountryHistoryTable + " ch " +
                        "where c.campaign_id = ch.campaign_id " +
                        "and date between '" + startTime + "' and '" + endTime + "' " +
                        "and c.status != 'removed' and c.campaign_id in (" + campaignIds + ") " +
                        "group by ch.campaign_id having impressions > 0 ) a ";
            }
            listHasData = DB.findListBySql(sql);
            list = Utils.getDiffJSObjectList(listAll, listHasData, "campaign_id");
        } else {
            list.clear();
        }
        JsonObject jsonObject = new JsonObject();
        JsonArray array = new JsonArray();
        double total_spend = 0;
        double total_installed = 0;
        double total_impressions = 0;
        double total_click = 0;

        for (int i = 0; i < list.size(); i++) {
            JSObject one = list.get(i);
            String campaign_id = one.get("campaign_id");

            String short_name = one.get("short_name");
            String account_id = one.get("account_id");
            String campaign_name = one.get("campaign_name");
            String status = one.get("status");
            String create_time = one.get("create_time").toString();
            create_time = create_time.substring(0,create_time.length()-5);
            double budget = one.get("budget");
            double bidding = one.get("bidding");

            double spend = Utils.convertDouble(one.get("total_spend"), 0);
            double installed = Utils.convertDouble(one.get("total_installed"), 0);
            double cpa =  0;
            double cvr = 0;
            double click = 0;

            total_spend += spend;
            total_installed += installed;

            JsonObject d = new JsonObject();
            d.addProperty("campaign_id", campaign_id);
            d.addProperty("short_name", short_name);
            d.addProperty("account_id", account_id);
            d.addProperty("campaign_name", campaign_name);
            d.addProperty("status", status);
            d.addProperty("create_time", create_time);
            d.addProperty("budget", budget);
            d.addProperty("bidding", bidding);

            d.addProperty("spend", spend);
            d.addProperty("installed", installed);
            d.addProperty("click", click);
            d.addProperty("cpa", Utils.trimDouble(cpa,3));
            d.addProperty("cvr", Utils.trimDouble(cvr,3));
            if (admobCheck) {
                d.addProperty("network", "admob");
            } else {
                d.addProperty("network", "facebook");
            }

            array.add(d);
        }
        double total_ctr = total_impressions > 0 ? total_click / total_impressions : 0;
        double total_cpa = total_installed > 0 ? total_spend / total_installed : 0;
        double total_cvr = total_click > 0 ? total_installed / total_click : 0;
        jsonObject.add("array", array);
        jsonObject.addProperty("total_spend", total_spend);
        jsonObject.addProperty("total_installed", total_installed);
        jsonObject.addProperty("total_impressions", 0);
        jsonObject.addProperty("total_click", 0);
        jsonObject.addProperty("total_ctr", Utils.trimDouble(total_ctr,3));
        jsonObject.addProperty("total_cpa", Utils.trimDouble(total_cpa,3));
        jsonObject.addProperty("total_cvr", Utils.trimDouble(total_cvr,3));
        return jsonObject;
    }
}
