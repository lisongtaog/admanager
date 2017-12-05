package com.bestgo.admanager.servlet;

import com.bestgo.admanager.Utils;
import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.TagName;
import org.apache.log4j.Logger;

import javax.rmi.CORBA.Util;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

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
        String campaignCreateTime = request.getParameter("campaignCreateTime");
        String isSummary = request.getParameter("summary");
        String sorterId = request.getParameter("sorterId");
        String adwordsCheck = request.getParameter("adwordsCheck");
        String countryCheck = request.getParameter("countryCheck");
        String facebookCheck = request.getParameter("facebookCheck");
        String countryCode = request.getParameter("countryCode");
        String likeCampaignName = request.getParameter("likeCampaignName");

        if (isSummary != null) {
            try {
                List<JSObject> tags = DB.scan("web_tag")
                        .select("id", "tag_name").orderByAsc("tag_name").execute();
                JsonArray arr = new JsonArray();
                if (adwordsCheck != null && adwordsCheck.equals("false") && facebookCheck != null && facebookCheck.equals("false")) {
                    for (int i = 0; i < tags.size(); i++) {
                        long id = tags.get(i).get("id");
                        String tagName = tags.get(i).get("tag_name");
                        JsonObject admob = fetchOneAppData(id, tagName,startTime, endTime, 0, true, false, countryCode,likeCampaignName,campaignCreateTime,false);
                        JsonObject facebook = fetchOneAppData(id, tagName,startTime, endTime, 0, false, false, countryCode,likeCampaignName,campaignCreateTime,false);
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
                        admob.addProperty("name", tagName);
                        if (total_impressions == 0) {
                            continue;
                        }
                        arr.add(admob);
                    }
                } else {
                    for (int i = 0; i < tags.size(); i++) {
                        long id = tags.get(i).get("id");
                        String tagName = tags.get(i).get("tag_name");
                        JsonObject jsonObject = fetchOneAppData(id, tagName,startTime, endTime, 0, "true".equals(adwordsCheck), false, countryCode,likeCampaignName,campaignCreateTime,false);
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
        } else {
            try {
                int sorter = 0;
                if (sorterId != null) {
                    sorter = Utils.parseInt(sorterId, 0);
                }
                JSObject tagObject = DB.simpleScan("web_tag")
                        .select("id", "tag_name")
                        .where(DB.filter().whereEqualTo("tag_name", tag)).execute();
                if (tagObject.hasObjectData()) {
                    Long id = tagObject.get("id");
                    JsonObject jsonObject = null;
                    if (countryCode != null && !countryCode.isEmpty()) {
                        countryCheck = "false";
                    }
                    if (adwordsCheck != null && adwordsCheck.equals("false") && facebookCheck != null && facebookCheck.equals("false")) {
                        JsonObject admob = fetchOneAppData(id, tag,startTime, endTime, sorter, true, "true".equals(countryCheck), countryCode,likeCampaignName,campaignCreateTime,true);
                        JsonObject facebook = fetchOneAppData(id, tag,startTime, endTime, sorter, false, "true".equals(countryCheck), countryCode,likeCampaignName,campaignCreateTime,true);
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
                        jsonObject = fetchOneAppData(id, tag,startTime, endTime, sorter, "true".equals(adwordsCheck), "true".equals(countryCheck), countryCode,likeCampaignName,campaignCreateTime,true);
                    }
                    if ("true".equals(countryCheck)) {
                        JsonArray array = jsonObject.getAsJsonArray("array");
                        HashMap<String, CountryRecord> dataSets = new HashMap<>();
                        for (int i = 0; i < array.size(); i++) {
                            JsonObject one = array.get(i).getAsJsonObject();
                            String countryName = "";
                            if (one.get("country_name").isJsonNull()) {
                                countryName = one.get("country_code").getAsString();
                            } else {
                                countryName = one.get("country_name").getAsString();
                            }
                            CountryRecord record = dataSets.get(countryName);
                            if (record == null) {
                                record = new CountryRecord();
                                dataSets.put(countryName, record);
                            }
                            record.impressions += one.get("impressions").getAsDouble();
                            record.installed += one.get("installed").getAsDouble();
                            record.click += one.get("click").getAsDouble();
                            record.spend += one.get("spend").getAsDouble();
                            record.ctr = record.impressions > 0 ? record.click / record.impressions : 0;
                            record.cpa = record.installed > 0 ? record.spend / record.installed : 0;
                            record.cvr = record.click > 0 ? record.installed / record.click : 0;
                        }
                        JsonArray newArr = new JsonArray();
                        for (String key : dataSets.keySet()) {
                            String sql = "select price from web_ad_tag_country_price_dict cpd, app_country_code_dict ccd\n" +
                                    "where cpd.country_code = ccd.country_code and ccd.country_name = '" + key + "' and tag_name = '" + tag + "'";
                            JSObject oneR = DB.findOneBySql(sql);
                            double price = Utils.convertDouble(oneR.get("price"),0);
                            JsonObject one = new JsonObject();
                            CountryRecord record = dataSets.get(key);
                            record.roi = (price - record.cpa) * record.installed;
                            one.addProperty("country_name", key);
                            one.addProperty("impressions", record.impressions);
                            one.addProperty("installed", record.installed);
                            one.addProperty("click", record.click);
                            one.addProperty("spend", Utils.trimDouble(record.spend));
                            one.addProperty("ctr", Utils.trimDouble(record.ctr));
                            one.addProperty("cpa", Utils.trimDouble(record.cpa));
                            one.addProperty("cvr", Utils.trimDouble(record.cvr));
                            one.addProperty("roi", Utils.trimDouble(record.roi));
                            newArr.add(one);
                        }
                        jsonObject.add("array", newArr);
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

    class CountryRecord {
        public double impressions;
        public double installed;
        public double click;
        public double spend;
        public double ctr;
        public double cpa;
        public double cvr;
        public double roi;
    }

    private JsonObject fetchOneAppData(long tagId, String tagName, String startTime, String endTime,
                                       int sorterId, boolean admobCheck, boolean countryCheck, String countryCode,String likeCampaignName,String campaignCreateTime,boolean hasROI) throws Exception {
        String relationTable = "web_ad_campaign_tag_rel";
        String webAdCampaignTable = "web_ad_campaigns";
        String webAdCampaignHistoryTable = "web_ad_campaigns_history";
        if (countryCheck || (countryCode != null && !countryCode.isEmpty())) {
            webAdCampaignHistoryTable = "web_ad_campaigns_country_history";
        }
        if (admobCheck) {
            relationTable = "web_ad_campaign_tag_admob_rel";
            webAdCampaignTable = "web_ad_campaigns_admob";
            webAdCampaignHistoryTable = "web_ad_campaigns_history_admob";
            if (countryCheck || (countryCode != null && !countryCode.isEmpty())) {
                webAdCampaignHistoryTable = "web_ad_campaigns_country_history_admob";
            }


        }

        HashMap<String ,String> countryMap = Utils.getCountryMap();

        List<JSObject> list = DB.scan(relationTable).select("campaign_id")
                .where(DB.filter().whereEqualTo("tag_id", tagId)).execute();


        Set<String> campaignIdSet = new HashSet<>();
        for(JSObject j : list){
            campaignIdSet.add(j.get("campaign_id"));
        }

        String campaignIds = "";
        if(campaignCreateTime != null && campaignCreateTime.length() >0){
            List<JSObject> campaignIdJSObjectList = new ArrayList<>();
            if(admobCheck){
                String sqlAdmobCampaignId = "select campaign_id from ad_campaigns_admob where app_name = '"+ tagName +"' and create_time like '" + campaignCreateTime + "%'";
                campaignIdJSObjectList  = DB.findListBySql(sqlAdmobCampaignId);
            }else{
                String sqlFacebookCampaignId = "select campaign_id from ad_campaigns where app_name = '"+ tagName +"' and create_time like '" + campaignCreateTime + "%'";
                campaignIdJSObjectList  = DB.findListBySql(sqlFacebookCampaignId);
            }

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
        }else{
            for(String s : campaignIdSet){
                campaignIds += "'" + s + "',";
            }
        }

        if(campaignIds != null && campaignIds.length()>0){
            campaignIds = campaignIds.substring(0,campaignIds.length()-1);
        }


        String orderStr = "";
        if (sorterId >= 0) {
            switch (sorterId) {
                case 0:
                case 1:
                case 1001:
                    orderStr = "order by create_time ";
                    break;
                case 2:
                case 1002:
                    orderStr = "order by status ";
                    break;
                case 3:
                case 1003:
                    orderStr = "order by budget ";
                    break;
                case 4:
                case 1004:
                    orderStr = "order by bidding ";
                    break;
                case 5:
                case 1005:
                    orderStr = "order by spend ";
                    break;
                case 6:
                case 1006:
                    orderStr = "order by installed ";
                    break;
                case 7:
                case 1007:
                    orderStr = "order by click ";
                    break;
                case 8:
                case 1008:
                    orderStr = "order by cpa ";
                    break;
                case 9:
                case 1009:
                    orderStr = "order by ctr ";
                    break;
                case 10:
                case 1010:
                    orderStr = "order by cvr ";
                    break;
                default:
                    orderStr = "order by create_time ";
            }
            if (sorterId > 1000) {
                orderStr += " desc";
            }
        }

        if (!campaignIds.isEmpty()) {
            String sql = "select campaign_id, account_id, campaign_name, status, create_time, budget, bidding, spend, installed, impressions, click" +
                    ", (case when impressions > 0 then click/impressions else 0 end) as ctr" +
                    ", (case when installed > 0 then spend/installed else 0 end) as cpa" +
                    ", (case when click > 0 then installed/click else 0 end) as cvr" +
                    " from (" +
                    "select ch.campaign_id, account_id, campaign_name,c.status, create_time, c.budget, c.bidding, sum(ch.total_spend) as spend, " +
                    "sum(ch.total_installed) as installed, sum(ch.total_impressions) as impressions " +
                    ",sum(ch.total_click) as click from " + webAdCampaignTable + " c, " + webAdCampaignHistoryTable + " ch " +
                    "where c.campaign_id=ch.campaign_id\n" +
                    ((likeCampaignName != null) ? " and campaign_name like '%" + likeCampaignName +"%' " : "")  +
                    "and date between '" + startTime + "' and '" + endTime + "' " +
                    "and c.campaign_id in (" + campaignIds + ")" +
                    ((countryCode != null && !countryCode.isEmpty()) ? " and country_code='" + countryCode + "'" : "") +
                    "group by ch.campaign_id) a " + orderStr;
            if (countryCheck) {
                sql = "select campaign_id, country_code, account_id, campaign_name, status, create_time, budget, bidding, spend, installed, impressions, click" +
                        ", (case when impressions > 0 then click/impressions else 0 end) as ctr" +
                        ", (case when installed > 0 then spend/installed else 0 end) as cpa" +
                        ", (case when click > 0 then installed/click else 0 end) as cvr" +
                        " from (" +
                        "select ch.campaign_id, country_code, account_id, campaign_name,c.status, create_time, c.budget, c.bidding, sum(ch.total_spend) as spend, " +
                        "sum(ch.total_installed) as installed, sum(ch.total_impressions) as impressions " +
                        ",sum(ch.total_click) as click from " + webAdCampaignTable + " c, " + webAdCampaignHistoryTable + " ch " +
                        "where c.campaign_id=ch.campaign_id\n" +
                        ((likeCampaignName != null) ? " and campaign_name like '%" + likeCampaignName +"%' " : "")  +
                        "and date between '" + startTime + "' and '" + endTime + "' " +
                        "and c.campaign_id in (" + campaignIds + ")" +
                        "group by ch.campaign_id, country_code) a " + orderStr;
            }
            list = DB.findListBySql(sql);
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
            double roi = 0;
            if(hasROI){
                double priceI = 0;
                double cpaI = 0;
                double installedI = 0;
                if(countryCode != null && countryCode.length()>0){
                    String sql = "select price from web_ad_tag_country_price_dict where tag_name = '" + tagName + "' and country_code = '" + countryCode + "'";
                    JSObject oneI = DB.findOneBySql(sql);
                    if(one.hasObjectData()){
                        priceI = Utils.convertDouble(oneI.get("price"),0);
                        String sqlT = "";
                        if(admobCheck){
                            sqlT = "select sum(total_installed) installed, sum(cpa) cpa from web_ad_campaigns_country_history_admob where campaign_id = '" + campaign_id + "' \n" +
                                    "and country_code = '"+countryCode+"' and date between '"+startTime+"' and '"+endTime+"'";
                        }else{
                            sqlT = "select sum(total_installed) installed, sum(cpa) cpa from web_ad_campaigns_country_history where campaign_id = '" + campaign_id + "' \n" +
                                    "and country_code = '"+countryCode+"' and date between '"+startTime+"' and '"+endTime+"'";
                        }
                        JSObject twoI = DB.findOneBySql(sqlT);
                        cpaI = Utils.convertDouble(twoI.get("cpa"),0);
                        installedI = Utils.convertDouble(twoI.get("installed"),0);
                        roi = ( priceI - cpaI ) * installedI;
                    }
                }else{
                    String sql = "";
                    if(admobCheck){
                        sql = "select cch.country_code, sum(cpa) cpa, sum(total_installed) installed, price \n" +
                                "from web_ad_campaigns_country_history_admob cch,web_ad_tag_country_price_dict cpd \n" +
                                "where cch.country_code = cpd.country_code and campaign_id = '"+campaign_id+"' and tag_name = '"+tagName+"'\n" +
                                "and date between '"+startTime+"' and '"+endTime+"' group by cch.country_code";
                    }else{
                        sql = "select cch.country_code, sum(cpa) cpa, sum(total_installed) installed, price \n" +
                                "from web_ad_campaigns_country_history cch,web_ad_tag_country_price_dict cpd \n" +
                                "where cch.country_code = cpd.country_code and campaign_id = '"+campaign_id+"' and tag_name = '"+tagName+"'\n" +
                                "and date between '"+startTime+"' and '"+endTime+"' group by cch.country_code";
                    }

                    List<JSObject> listM = DB.findListBySql(sql);
                    for(JSObject j : listM){
                        cpaI = Utils.convertDouble(j.get("cpa"),0);
                        installedI = Utils.convertDouble(j.get("installed"),0);
                        priceI = Utils.convertDouble(j.get("price"),0);
                        roi += (priceI - cpaI)*installedI;
                    }
                }
            }

            String account_id = one.get("account_id");
            String campaign_name = one.get("campaign_name");
            String status = one.get("status");
            String create_time = one.get("create_time").toString();
            create_time = create_time.substring(0,create_time.length()-5);
            String country_code = one.get("country_code");
            double budget = one.get("budget");
            double bidding = one.get("bidding");
            double spend = Utils.convertDouble(one.get("spend"), 0);
            double installed = Utils.convertDouble(one.get("installed"), 0);
            double impressions = Utils.convertDouble(one.get("impressions"), 0);
            double click = Utils.convertDouble(one.get("click"), 0);
            double ctr = impressions > 0 ? click / impressions : 0;
            double cpa = installed > 0 ? spend / installed : 0;
            double cvr = click > 0 ? installed / click : 0;
            if (impressions == 0) {
                continue;
            }
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
            d.addProperty("country_code", country_code);
            d.addProperty("country_name", countryMap.get(country_code));
            d.addProperty("budget", budget);
            d.addProperty("bidding", bidding);
            d.addProperty("impressions", impressions);
            d.addProperty("spend", spend);
            d.addProperty("installed", installed);
            d.addProperty("click", click);
            d.addProperty("ctr", Utils.trimDouble(ctr));
            d.addProperty("cpa", Utils.trimDouble(cpa));
            d.addProperty("cvr", Utils.trimDouble(cvr));
            d.addProperty("roi", Utils.trimDouble(roi));
            if (admobCheck) {
                d.addProperty("network", "admob");
            } else {
                d.addProperty("network", "facebook");
            }
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
