package com.bestgo.admanager.servlet;

import com.bestgo.admanager.DateUtil;
import com.bestgo.admanager.Utils;
import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;
import com.facebook.ads.sdk.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.TagName;
import org.apache.log4j.Logger;

import javax.rmi.CORBA.Util;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.System;
import java.util.*;

/**
 * 首页的汇总
 */
@WebServlet(name = "query", urlPatterns = {"/query"}, asyncSupported = true)
public class Query extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Utils.isAdmin(request, response)) return;

        JsonObject json = new JsonObject();
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        String sorterId = request.getParameter("sorterId");
        String adwordsCheck = request.getParameter("adwordsCheck");
        String facebookCheck = request.getParameter("facebookCheck");
        int sorter = 0;
        if (sorterId != null) {
            sorter = Utils.parseInt(sorterId, 0);
        }
        try {
            JsonArray arr = new JsonArray();
            if ("false".equals(adwordsCheck) && "false".equals(facebookCheck)) {
                if(sorter > 0){
                    ArrayList<CampaignsSummary> campaignsSummaryList = new ArrayList<>();
                    String sqlTag = "select t.id,t.tag_name,google_package_id from web_tag t LEFT JOIN web_facebook_app_ids_rel air ON t.tag_name = air.tag_name";
                    List<JSObject> tagList = DB.findListBySql(sqlTag);
                    for (JSObject tagJSObject : tagList) {
                        CampaignsSummary campaignsSummary = new CampaignsSummary();    //CampaignsSummary类是一个没有构造器的类，当它被实例化以后系统会默认给其无参构造函数，初始化其中的实例为默认域
                        long id = tagJSObject.get("id");
                        campaignsSummary.name = tagJSObject.get("tag_name"); //可以用 .号来初始化特定的实例
                        JsonObject admob = fetchOneAppDataSummary(id, startTime, endTime,true); //用这些条件从数据库取值，返回一个JSObject
                        JsonObject facebook =  fetchOneAppDataSummary(id, startTime, endTime,false);

                        //以下两句：主要用于取endTime当天的 total_spend --------------------------------------------------------------------------------
                        JsonObject admob1 = fetchOneAppDataSummary(id, endTime, endTime,true);
                        JsonObject facebook1 =  fetchOneAppDataSummary(id, endTime, endTime,false);
                        campaignsSummary.endTime_total_spend = Utils.trimDouble(admob1.get("total_spend").getAsDouble()+ facebook1.get("total_spend").getAsDouble(),0);
                        // -----------------------------------------------------------------------------------------------------------------------------

                        campaignsSummary.total_impressions = admob.get("total_impressions").getAsDouble() + facebook.get("total_impressions").getAsDouble();
                        if (campaignsSummary.total_impressions == 0) {
                            continue;
                        }
                        campaignsSummary.total_spend = admob.get("total_spend").getAsDouble() + facebook.get("total_spend").getAsDouble();
                        campaignsSummary.total_installed = admob.get("total_installed").getAsDouble() + facebook.get("total_installed").getAsDouble();
                        campaignsSummary.total_click = admob.get("total_click").getAsDouble() + facebook.get("total_click").getAsDouble();
                        campaignsSummary.total_ctr = campaignsSummary.total_impressions > 0 ? campaignsSummary.total_click / campaignsSummary.total_impressions : 0;
                        campaignsSummary.total_cpa = campaignsSummary.total_installed > 0 ? campaignsSummary.total_spend / campaignsSummary.total_installed : 0;
                        campaignsSummary.total_cvr = campaignsSummary.total_click > 0 ? campaignsSummary.total_installed / campaignsSummary.total_click : 0;
                        String google_package_id = tagJSObject.get("google_package_id");
                        if(google_package_id != null){
                            String sqlR = "select sum(revenue) as revenues " +
                                    "from web_ad_country_analysis_report_history where app_id = '"
                                    + google_package_id + "' and date BETWEEN '" + startTime + "' AND '" + endTime + "'";
                            JSObject oneR = DB.findOneBySql(sqlR);
                            if(oneR.hasObjectData()){
                                campaignsSummary.total_revenue = Utils.convertDouble(oneR.get("revenues"),0);
                            }
                            //下面用于赋值campaignsSummary.endTime_total_revenue ----------------------------------------------------------
                            sqlR = "select sum(revenue) as revenues " +
                                    "from web_ad_country_analysis_report_history where app_id = '"
                                    + google_package_id + "' and date = '" + endTime + "'";
                            oneR = DB.findOneBySql(sqlR);
                            if(oneR.hasObjectData()){
                                campaignsSummary.endTime_total_revenue = Utils.trimDouble(Utils.convertDouble(oneR.get("revenues"),0),0);
                            }
                            // -------------------------------------------------------------------------------------------------------------
                        }
                        campaignsSummaryList.add(campaignsSummary);
                    }
                    if(campaignsSummaryList != null && campaignsSummaryList.size() > 0){
                        switch (sorter){
                            case 70:
                                Collections.sort(campaignsSummaryList, new Comparator<CampaignsSummary>() {
                                    @Override
                                    public int compare(CampaignsSummary a, CampaignsSummary b) {
                                        if(a.total_spend > b.total_spend){
                                            return 1;
                                        }else if(a.total_spend < b.total_spend){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1070:
                                Collections.sort(campaignsSummaryList, new Comparator<CampaignsSummary>() {
                                    @Override
                                    public int compare(CampaignsSummary a, CampaignsSummary b) {
                                        if(a.total_spend < b.total_spend){
                                            return 1;
                                        }else if(a.total_spend > b.total_spend){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 71:
                                Collections.sort(campaignsSummaryList, new Comparator<CampaignsSummary>() {
                                    @Override
                                    public int compare(CampaignsSummary a, CampaignsSummary b) {
                                        if(a.seven_days_total_spend> b.seven_days_total_spend){
                                            return 1;
                                        }else if(a.seven_days_total_spend < b.seven_days_total_spend){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1071:
                                Collections.sort(campaignsSummaryList, new Comparator<CampaignsSummary>() {
                                    @Override
                                    public int compare(CampaignsSummary a, CampaignsSummary b) {
                                        if(a.seven_days_total_spend < b.seven_days_total_spend){
                                            return 1;
                                        }else if(a.seven_days_total_spend > b.seven_days_total_spend){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 72:
                                Collections.sort(campaignsSummaryList, new Comparator<CampaignsSummary>() {
                                    @Override
                                    public int compare(CampaignsSummary a, CampaignsSummary b) {
                                        if(a.total_revenue> b.total_revenue){
                                            return 1;
                                        }else if(a.total_revenue < b.total_revenue){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1072:
                                Collections.sort(campaignsSummaryList, new Comparator<CampaignsSummary>() {
                                    @Override
                                    public int compare(CampaignsSummary a, CampaignsSummary b) {
                                        if(a.total_revenue < b.total_revenue){
                                            return 1;
                                        }else if(a.total_revenue > b.total_revenue){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 73:
                                Collections.sort(campaignsSummaryList, new Comparator<CampaignsSummary>() {
                                    @Override
                                    public int compare(CampaignsSummary a, CampaignsSummary b) {
                                        if(a.seven_days_total_revenue> b.seven_days_total_revenue){
                                            return 1;
                                        }else if(a.seven_days_total_revenue < b.seven_days_total_revenue){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1073:
                                Collections.sort(campaignsSummaryList, new Comparator<CampaignsSummary>() {
                                    @Override
                                    public int compare(CampaignsSummary a, CampaignsSummary b) {
                                        if(a.seven_days_total_revenue < b.seven_days_total_revenue){
                                            return 1;
                                        }else if(a.seven_days_total_revenue > b.seven_days_total_revenue){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 74:
                                Collections.sort(campaignsSummaryList, new Comparator<CampaignsSummary>() {
                                    @Override
                                    public int compare(CampaignsSummary a, CampaignsSummary b) {
                                        if(a.total_installed> b.total_installed){
                                            return 1;
                                        }else if(a.total_installed < b.total_installed){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1074:
                                Collections.sort(campaignsSummaryList, new Comparator<CampaignsSummary>() {
                                    @Override
                                    public int compare(CampaignsSummary a, CampaignsSummary b) {
                                        if(a.total_installed < b.total_installed){
                                            return 1;
                                        }else if(a.total_installed > b.total_installed){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 75:
                                Collections.sort(campaignsSummaryList, new Comparator<CampaignsSummary>() {
                                    @Override
                                    public int compare(CampaignsSummary a, CampaignsSummary b) {
                                        if(a.total_impressions> b.total_impressions){
                                            return 1;
                                        }else if(a.total_impressions < b.total_impressions){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1075:
                                Collections.sort(campaignsSummaryList, new Comparator<CampaignsSummary>() {
                                    @Override
                                    public int compare(CampaignsSummary a, CampaignsSummary b) {
                                        if(a.total_impressions < b.total_impressions){
                                            return 1;
                                        }else if(a.total_impressions > b.total_impressions){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 76:
                                Collections.sort(campaignsSummaryList, new Comparator<CampaignsSummary>() {
                                    @Override
                                    public int compare(CampaignsSummary a, CampaignsSummary b) {
                                        if(a.total_click> b.total_click){
                                            return 1;
                                        }else if(a.total_click < b.total_click){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1076:
                                Collections.sort(campaignsSummaryList, new Comparator<CampaignsSummary>() {
                                    @Override
                                    public int compare(CampaignsSummary a, CampaignsSummary b) {
                                        if(a.total_click < b.total_click){
                                            return 1;
                                        }else if(a.total_click > b.total_click){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 77:
                                Collections.sort(campaignsSummaryList, new Comparator<CampaignsSummary>() {
                                    @Override
                                    public int compare(CampaignsSummary a, CampaignsSummary b) {
                                        if(a.total_ctr> b.total_ctr){
                                            return 1;
                                        }else if(a.total_ctr < b.total_ctr){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1077:
                                Collections.sort(campaignsSummaryList, new Comparator<CampaignsSummary>() {
                                    @Override
                                    public int compare(CampaignsSummary a, CampaignsSummary b) {
                                        if(a.total_ctr < b.total_ctr){
                                            return 1;
                                        }else if(a.total_ctr > b.total_ctr){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 78:
                                Collections.sort(campaignsSummaryList, new Comparator<CampaignsSummary>() {
                                    @Override
                                    public int compare(CampaignsSummary a, CampaignsSummary b) {
                                        if(a.total_cpa> b.total_cpa){
                                            return 1;
                                        }else if(a.total_cpa < b.total_cpa){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1078:
                                Collections.sort(campaignsSummaryList, new Comparator<CampaignsSummary>() {
                                    @Override
                                    public int compare(CampaignsSummary a, CampaignsSummary b) {
                                        if(a.total_cpa < b.total_cpa){
                                            return 1;
                                        }else if(a.total_cpa > b.total_cpa){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 79:
                                Collections.sort(campaignsSummaryList, new Comparator<CampaignsSummary>() {
                                    @Override
                                    public int compare(CampaignsSummary a, CampaignsSummary b) {
                                        if(a.total_cvr> b.total_cvr){
                                            return 1;
                                        }else if(a.total_cvr < b.total_cvr){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1079:
                                Collections.sort(campaignsSummaryList, new Comparator<CampaignsSummary>() {
                                    @Override
                                    public int compare(CampaignsSummary a, CampaignsSummary b) {
                                        if(a.total_cvr < b.total_cvr){
                                            return 1;
                                        }else if(a.total_cvr > b.total_cvr){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                        }
                        for(CampaignsSummary cs : campaignsSummaryList){
                            JsonObject j = new JsonObject();
                            j.addProperty("name",cs.name);
                            String sql = "select warning_level from  web_app_logs where app_name = '" + cs.name + "' and log_date = '" + endTime + "'";
                            JSObject one = DB.findOneBySql(sql);
                            if(one.hasObjectData()){
                                int warningLevel = one.get("warning_level");             //指标warning_level 仅仅与endTime有关
                                j.addProperty("warning_level",warningLevel);
                            }
                            j.addProperty("total_spend",Utils.trimDouble(cs.endTime_total_spend,0));
                            j.addProperty("endTime_total_spend",Utils.trimDouble(cs.total_spend,0));
                            j.addProperty("endTime_total_revenue",Utils.trimDouble(cs.endTime_total_revenue,0));
                            j.addProperty("total_installed",cs.total_installed);
                            j.addProperty("total_impressions",cs.total_impressions);
                            j.addProperty("total_click",cs.total_click);
                            j.addProperty("total_ctr",Utils.trimDouble(cs.total_ctr,3));
                            j.addProperty("total_cpa",Utils.trimDouble(cs.total_cpa,3));
                            j.addProperty("total_cvr",Utils.trimDouble(cs.total_cvr,3));
                            j.addProperty("total_revenue",Utils.trimDouble(cs.total_revenue,0));
                            arr.add(j);
                        }
                    }
                }else{   //这里是不符合 sorter>0 的条件时
                    String sqlTag = "select t.id,t.tag_name,google_package_id from web_tag t LEFT JOIN web_facebook_app_ids_rel air ON t.tag_name = air.tag_name ORDER BY t.tag_name";
                    List<JSObject> tagList = DB.findListBySql(sqlTag);
                    for (JSObject tagJSObject : tagList) {
                        long id = tagJSObject.get("id");
                        String tagName = tagJSObject.get("tag_name");
                        JsonObject admob = fetchOneAppDataSummary(id, startTime, endTime,true);
                        JsonObject facebook =  fetchOneAppDataSummary(id, startTime, endTime,false);

                        JsonObject admob1 = fetchOneAppDataSummary(id, endTime, endTime,true);
                        JsonObject facebook1 =  fetchOneAppDataSummary(id, endTime, endTime,false);
                        double endTime_total_spend = admob1.get("total_spend").getAsDouble() + facebook1.get("total_spend").getAsDouble();
                        admob.addProperty("endTime_total_spend", Utils.trimDouble( endTime_total_spend,0));

                        String google_package_id = tagJSObject.get("google_package_id");
                        if(google_package_id != null) {
                            String sqlR = "select sum(revenue) as revenues " +
                                    "from web_ad_country_analysis_report_history where app_id = '"
                                    + google_package_id + "' and date = '" + endTime + "'";
                            JSObject oneR = DB.findOneBySql(sqlR);
                            if (oneR.hasObjectData()) {
                                double endTime_total_revenue = Utils.convertDouble(oneR.get("revenues"), 0);
                                admob.addProperty("endTime_total_revenue", Utils.trimDouble( endTime_total_revenue,0));
                            }
                        }


                        double total_impressions = admob.get("total_impressions").getAsDouble() + facebook.get("total_impressions").getAsDouble();
                        if (total_impressions == 0) {
                            continue;
                        }

                        double total_spend = admob.get("total_spend").getAsDouble() + facebook.get("total_spend").getAsDouble();
                        double total_installed = admob.get("total_installed").getAsDouble() + facebook.get("total_installed").getAsDouble();
                        double total_click = admob.get("total_click").getAsDouble() + facebook.get("total_click").getAsDouble();
                        double total_ctr = total_impressions > 0 ? total_click / total_impressions : 0;
                        double total_cpa = total_installed > 0 ? total_spend / total_installed : 0;
                        double total_cvr = total_click > 0 ? total_installed / total_click : 0;

                        //这行之前的JsonObject admob 是个从表里取出的JSON对象，经下面一系列addProperty的操作后，变为存储处理好的值的JSON对象
                        admob.addProperty("total_spend", Utils.trimDouble(total_spend,0));
                        admob.addProperty("total_installed", total_installed);
                        admob.addProperty("total_impressions", total_impressions);
                        admob.addProperty("total_click", total_click);
                        admob.addProperty("total_ctr", Utils.trimDouble(total_ctr,3));
                        admob.addProperty("total_cpa", Utils.trimDouble(total_cpa,3));
                        admob.addProperty("total_cvr", Utils.trimDouble(total_cvr,3));
                        admob.addProperty("name", tagName);
                        String sql = "select warning_level from  web_app_logs where app_name = '" + tagName + "' and log_date = '" + endTime + "'";
                        JSObject one = DB.findOneBySql(sql);
                        if(one.hasObjectData()){
                            int warningLevel = one.get("warning_level");
                            admob.addProperty("warning_level",warningLevel);
                        }

                        //这里计算 total_revenue
                        double total_revenue = 0;
                        google_package_id = tagJSObject.get("google_package_id");
                        if(google_package_id != null){
                            String sqlR = "select sum(revenue) as revenues " +
                                    "from web_ad_country_analysis_report_history where app_id = '"
                                    + google_package_id + "' and date BETWEEN '" + startTime + "' AND '" + endTime + "'";
                            JSObject oneR = DB.findOneBySql(sqlR);
                            if(oneR.hasObjectData()){
                                total_revenue = Utils.convertDouble(oneR.get("revenues"),0);
                            }
                        }
                        admob.addProperty("total_revenue",Utils.trimDouble(total_revenue,0));
                        arr.add(admob);
                    }
                }

            } else {     //这里是不符合 adwordsCheck  facebookCheck 都为false的条件时
                String sqlTag = "select id,tag_name from web_tag ORDER BY tag_name";
                List<JSObject> tagList = DB.findListBySql(sqlTag);
                for (int i = 0; i < tagList.size(); i++) {
                    long id = tagList.get(i).get("id");
                    String tagName = tagList.get(i).get("tag_name");
                    JsonObject jsonObject =  fetchOneAppDataSummary(id, startTime, endTime,"true".equals(adwordsCheck));
                    double total_impression = jsonObject.get("total_impressions").getAsDouble();
                    if (total_impression == 0) {
                        continue;
                    }

                    jsonObject.addProperty("name", tagName);
                    arr.add(jsonObject);
                }
            }
            json.add("data", arr);

            json.addProperty("ret", 1);
            json.addProperty("message", "执行成功");
        } catch (Exception ex) {
            json.addProperty("ret", 0);
            json.addProperty("message", ex.getMessage());
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }

        response.getWriter().write(json.toString());
    }


    //以下是在 startTime和endTime之间取一堆值初始化
    private JsonObject fetchOneAppDataSummary(long tagId, String startTime, String endTime, boolean admobCheck) throws Exception {
        String webAdCampaignTagRelTable = "web_ad_campaign_tag_rel";
        String webAdCampaignsTable = "web_ad_campaigns";
        String webAdCampaignsHistoryTable = "web_ad_campaigns_history";
        if (admobCheck) {
            webAdCampaignTagRelTable = "web_ad_campaign_tag_admob_rel";
            webAdCampaignsTable = "web_ad_campaigns_admob";
            webAdCampaignsHistoryTable = "web_ad_campaigns_history_admob";
        }
        String sql = "select sum(ch.total_spend) as spend, " +
                "sum(ch.total_installed) as installed, sum(ch.total_impressions) as impressions " +
                ",sum(ch.total_click) as click from " + webAdCampaignsTable + " c, " + webAdCampaignsHistoryTable + " ch, " +
                "(select distinct campaign_id from " + webAdCampaignTagRelTable + " where tag_id = " + tagId + ") rt " +
                "where rt.campaign_id = ch.campaign_id and c.campaign_id = ch.campaign_id " +
                "and date between '" + startTime + "' and '" + endTime + "' " +
                "and c.status != 'removed' ";
        JSObject one = DB.findOneBySql(sql);

        JsonObject jsonObject = new JsonObject();
        double total_spend = Utils.convertDouble(one.get("spend"), 0);
        double total_installed = Utils.convertDouble(one.get("installed"), 0);
        double total_impressions = Utils.convertDouble(one.get("impressions"), 0);
        double total_click = Utils.convertDouble(one.get("click"), 0);

        double total_ctr = total_impressions > 0 ? total_click / total_impressions : 0;
        double total_cpa = total_installed > 0 ? total_spend / total_installed : 0;
        double total_cvr = total_click > 0 ? total_installed / total_click : 0;

        jsonObject.addProperty("total_spend", Utils.trimDouble(total_spend,0));
        jsonObject.addProperty("total_installed", total_installed);
        jsonObject.addProperty("total_impressions", total_impressions);
        jsonObject.addProperty("total_click", total_click);
        jsonObject.addProperty("total_ctr", Utils.trimDouble(total_ctr,3));
        jsonObject.addProperty("total_cpa", Utils.trimDouble(total_cpa,3));
        jsonObject.addProperty("total_cvr", Utils.trimDouble(total_cvr,3));
        return jsonObject;
    }

    class CampaignsSummary {
        public String name;
        public double total_spend;
        public double seven_days_total_spend;
        public double endTime_total_spend;
        public double endTime_total_revenue;
        public double total_installed;
        public double total_impressions;
        public double total_click;
        public double total_ctr;
        public double total_cpa;
        public double total_cvr;
        public double total_revenue;
        public double seven_days_total_revenue;
        public String network;
    }
}
