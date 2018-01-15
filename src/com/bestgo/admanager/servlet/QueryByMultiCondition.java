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

@WebServlet(name = "QueryByMultiCondition", urlPatterns = {"/query_by_multi_condition"}, asyncSupported = true)
public class QueryByMultiCondition extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Utils.isAdmin(request, response)) return;

        JsonObject json = new JsonObject();
        String tag = request.getParameter("tag");

        try {
            JSObject tagObject = DB.simpleScan("web_tag").select("id", "tag_name").where(DB.filter().whereEqualTo("tag_name", tag)).execute();
            if (tagObject.hasObjectData()) {
                String startTime = request.getParameter("startTime");
                String endTime = request.getParameter("endTime");
                String sorterId = request.getParameter("sorterId");
                int sorter = 0;
                if (sorterId != null) {
                    sorter = Utils.parseInt(sorterId, 0);
                }
                String adwordsCheck = request.getParameter("adwordsCheck");
                String facebookCheck = request.getParameter("facebookCheck");
                String countryCheck = request.getParameter("countryCheck");
                String campaignCreateTime = request.getParameter("campaignCreateTime");
                String countryCode = request.getParameter("countryCode");

                //总安装量对比值
                String totalInstallComparisonValue = request.getParameter("totalInstallComparisonValue");
                //与总安装量对比符号
                String totalInstallOperator = request.getParameter("totalInstallOperator");
                boolean isPositiveInteger = (totalInstallComparisonValue == "" || totalInstallComparisonValue == null) ? false : totalInstallComparisonValue.matches("^\\+?[1-9][0-9]*$");
                if(isPositiveInteger){
                    if("1".equals(totalInstallOperator)){
                        totalInstallComparisonValue = " >= " + totalInstallComparisonValue;
                    } else if("2".equals(totalInstallOperator)){
                        totalInstallComparisonValue = " <= " + totalInstallComparisonValue;
                    } else {
                        totalInstallComparisonValue = " = " + totalInstallComparisonValue;
                    }
                }else if("0".equals(totalInstallComparisonValue) && "3".equals(totalInstallOperator)){
                    totalInstallComparisonValue = " = 0 ";
                } else{
                    totalInstallComparisonValue = "";
                }

                String likeCampaignName = request.getParameter("likeCampaignName");
                HashMap<String ,String> countryMap = Utils.getCountryMap();
                Long id = tagObject.get("id");
                JsonObject jsonObject = new JsonObject();
                if (countryCode != null && countryCode != "") {
                    countryCheck = "false";
                }
                if (adwordsCheck != null && adwordsCheck.equals("false") && facebookCheck != null && facebookCheck.equals("false")) {
                    JsonObject admob = fetchOneAppDataOfAdmob(id, tag,startTime, endTime, "true".equals(countryCheck), countryCode,likeCampaignName,campaignCreateTime,true,countryMap,totalInstallComparisonValue);
                    JsonObject facebook = fetchOneAppDataOfFacebook(id, tag,startTime, endTime,"true".equals(countryCheck), countryCode,likeCampaignName,campaignCreateTime,true,countryMap,totalInstallComparisonValue);
                    double total_spend = admob.get("total_spend").getAsDouble() + facebook.get("total_spend").getAsDouble();
                    double total_installed = admob.get("total_installed").getAsDouble() + facebook.get("total_installed").getAsDouble();
                    double total_impressions = admob.get("total_impressions").getAsDouble() + facebook.get("total_impressions").getAsDouble();
                    double total_click = admob.get("total_click").getAsDouble() + facebook.get("total_click").getAsDouble();
                    double total_ctr = total_impressions > 0 ? total_click / total_impressions : 0;
                    double total_cpa = total_installed > 0 ? total_spend / total_installed : 0;
                    double total_cvr = total_click > 0 ? total_installed / total_click : 0;

                    JsonArray array = admob.getAsJsonArray("array");
                    JsonArray array1 = facebook.getAsJsonArray("array");
                    array.addAll(array1);
                    Gson gson = new Gson();
                    if(sorter > 0 && "false".equals(countryCheck)){
                        List<Campaigns> campaignsList = gson.fromJson(array, new TypeToken<List<Campaigns>>() {}.getType());
                        switch (sorter){
                            case 1:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if(a.create_time.compareTo(b.create_time) > 0){
                                            return 1;
                                        }else if(a.create_time.compareTo(b.create_time) < 0){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1001:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if(a.create_time.compareTo(b.create_time) > 0){
                                            return -1;
                                        }else if(a.create_time.compareTo(b.create_time) < 0){
                                            return 1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 2:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if(a.status.compareTo(b.status) > 0){
                                            return 1;
                                        }else if(a.status.compareTo(b.status) < 0){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1002:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if(a.status.compareTo(b.status) > 0){
                                            return -1;
                                        }else if(a.status.compareTo(b.status) < 0){
                                            return 1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 3:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if(a.budget > b.budget){
                                            return 1;
                                        }else if(a.budget < b.budget){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1003:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if(a.budget > b.budget){
                                            return -1;
                                        }else if(a.budget < b.budget){
                                            return 1;
                                        }else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 4:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if(a.bidding > b.bidding){
                                            return 1;
                                        }else  if(a.bidding < b.bidding){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1004:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if(a.bidding > b.bidding){
                                            return -1;
                                        }else  if(a.bidding < b.bidding){
                                            return 1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 5:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if(a.spend > b.spend){
                                            return 1;
                                        }else if(a.spend < b.spend){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1005:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if(a.spend > b.spend){
                                            return -1;
                                        }else if(a.spend < b.spend){
                                            return 1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 6:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if(a.installed > b.installed){
                                            return 1;
                                        }else if(a.installed < b.installed){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1006:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if(a.installed > b.installed){
                                            return -1;
                                        }else  if(a.installed < b.installed){
                                            return 1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 7:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if(a.click > b.click){
                                            return 1;
                                        }else if(a.click < b.click){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1007:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if(a.click > b.click){
                                            return -1;
                                        }else if(a.click < b.click){
                                            return 1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 8:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if(a.cpa > b.cpa){
                                            return 1;
                                        }else if(a.cpa < b.cpa){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1008:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if(a.cpa > b.cpa){
                                            return -1;
                                        }else if(a.cpa < b.cpa){
                                            return 1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 9:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if(a.ctr > b.ctr){
                                            return 1;
                                        }else if(a.ctr < b.ctr){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1009:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if(a.ctr > b.ctr){
                                            return -1;
                                        }else if(a.ctr < b.ctr){
                                            return 1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 10:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if(a.cvr > b.cvr){
                                            return 1;
                                        }else if(a.cvr < b.cvr){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1010:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if(a.cvr > b.cvr){
                                            return -1;
                                        }else if(a.cvr < b.cvr){
                                            return 1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 11:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if(a.roi > b.roi){
                                            return 1;
                                        }else  if(a.roi < b.roi){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1011:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if(a.roi > b.roi){
                                            return -1;
                                        }else if(a.roi < b.roi){
                                            return 1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                        }
                        JsonArray jsonArray = new JsonArray();

                        for(Campaigns c : campaignsList){
                            JsonObject j = new JsonObject();
                            j.addProperty("campaign_id",c.campaign_id);
                            j.addProperty("short_name",c.short_name);
                            j.addProperty("account_id",c.account_id);
                            j.addProperty("campaign_name",c.campaign_name);
                            j.addProperty("status",c.status);
                            j.addProperty("create_time",c.create_time);
                            j.addProperty("country_code",c.country_code);
                            j.addProperty("budget",c.budget);
                            j.addProperty("bidding",c.bidding);
                            j.addProperty("impressions",c.impressions);
                            j.addProperty("installed",c.installed);
                            j.addProperty("click",c.click);
                            j.addProperty("spend",c.spend);
                            j.addProperty("ctr",c.ctr);
                            j.addProperty("cpa",c.cpa);
                            j.addProperty("cvr",c.cvr);
                            j.addProperty("roi",c.roi);
                            j.addProperty("campaign_spends",c.campaign_spends);
                            j.addProperty("network",c.network);
                            jsonArray.add(j);
                        }
                        jsonObject.addProperty("total_spend", Utils.trimDouble(total_spend));
                        jsonObject.addProperty("total_installed", total_installed);
                        jsonObject.addProperty("total_impressions", total_impressions);
                        jsonObject.addProperty("total_click", total_click);
                        jsonObject.addProperty("total_ctr", Utils.trimDouble(total_ctr));
                        jsonObject.addProperty("total_cpa", Utils.trimDouble(total_cpa));
                        jsonObject.addProperty("total_cvr", Utils.trimDouble(total_cvr));
                        jsonObject.add("array",jsonArray);
                    }else{
                        admob.addProperty("total_spend", Utils.trimDouble(total_spend));
                        admob.addProperty("total_installed", total_installed);
                        admob.addProperty("total_impressions", total_impressions);
                        admob.addProperty("total_click", total_click);
                        admob.addProperty("total_ctr", Utils.trimDouble(total_ctr));
                        admob.addProperty("total_cpa", Utils.trimDouble(total_cpa));
                        admob.addProperty("total_cvr", Utils.trimDouble(total_cvr));
                        jsonObject = admob;
                    }


                } else {
                    if("true".equals(adwordsCheck)){
                        jsonObject = fetchOneAppDataOfAdmob(id, tag,startTime, endTime, "true".equals(countryCheck), countryCode,likeCampaignName,campaignCreateTime,true,countryMap,totalInstallComparisonValue);
                    }else{
                        jsonObject = fetchOneAppDataOfFacebook(id, tag,startTime, endTime, "true".equals(countryCheck), countryCode,likeCampaignName,campaignCreateTime,true,countryMap,totalInstallComparisonValue);
                    }
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
                    }
                    JsonArray newArr = new JsonArray();
                    if(sorter > 0){
                        List<CountryRecord> countryRecordList = new ArrayList<>();
                        for (String key : dataSets.keySet()) {
                            String sql = "select price from web_ad_tag_country_price_dict cpd, app_country_code_dict ccd " +
                                    "where cpd.country_code = ccd.country_code and ccd.country_name = '" + key + "' and tag_name = '" + tag + "'";
                            JSObject oneR = DB.findOneBySql(sql);
                            double price = Utils.convertDouble(oneR.get("price"),0);
                            CountryRecord record = dataSets.get(key);
                            record.country_name = key;
                            record.ctr = record.impressions > 0 ? record.click / record.impressions : 0;
                            record.cpa = record.installed > 0 ? record.spend / record.installed : 0;
                            record.cvr = record.click > 0 ? record.installed / record.click : 0;
                            record.roi = (price - record.cpa) * record.installed;
                            countryRecordList.add(record);
                        }
                        switch (sorter){
                            case 21:
                                Collections.sort(countryRecordList, new Comparator<CountryRecord>() {
                                    @Override
                                    public int compare(CountryRecord a, CountryRecord b) {
                                        if(a.impressions > b.impressions){
                                            return 1;
                                        }else if(a.impressions < b.impressions){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1021:
                                Collections.sort(countryRecordList, new Comparator<CountryRecord>() {
                                    @Override
                                    public int compare(CountryRecord a, CountryRecord b) {
                                        if(a.impressions > b.impressions){
                                            return -1;
                                        }else if(a.impressions < b.impressions){
                                            return 1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 22:
                                Collections.sort(countryRecordList, new Comparator<CountryRecord>() {
                                    @Override
                                    public int compare(CountryRecord a, CountryRecord b) {
                                        if(a.spend > b.spend){
                                            return 1;
                                        }else if(a.spend < b.spend){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1022:
                                Collections.sort(countryRecordList, new Comparator<CountryRecord>() {
                                    @Override
                                    public int compare(CountryRecord a, CountryRecord b) {
                                        if(a.spend > b.spend){
                                            return -1;
                                        }else if(a.spend < b.spend){
                                            return 1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 23:
                                Collections.sort(countryRecordList, new Comparator<CountryRecord>() {
                                    @Override
                                    public int compare(CountryRecord a, CountryRecord b) {
                                        if(a.installed > b.installed){
                                            return 1;
                                        }else  if(a.installed < b.installed){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1023:
                                Collections.sort(countryRecordList, new Comparator<CountryRecord>() {
                                    @Override
                                    public int compare(CountryRecord a, CountryRecord b) {
                                        if(a.installed > b.installed){
                                            return -1;
                                        }else if(a.installed < b.installed){
                                            return 1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 24:
                                Collections.sort(countryRecordList, new Comparator<CountryRecord>() {
                                    @Override
                                    public int compare(CountryRecord a, CountryRecord b) {
                                        if(a.click > b.click){
                                            return 1;
                                        }else if(a.click < b.click){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1024:
                                Collections.sort(countryRecordList, new Comparator<CountryRecord>() {
                                    @Override
                                    public int compare(CountryRecord a, CountryRecord b) {
                                        if(a.click > b.click){
                                            return -1;
                                        }else if(a.click < b.click){
                                            return 1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 25:
                                Collections.sort(countryRecordList, new Comparator<CountryRecord>() {
                                    @Override
                                    public int compare(CountryRecord a, CountryRecord b) {
                                        if(a.cpa > b.cpa){
                                            return 1;
                                        }else if(a.cpa < b.cpa){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1025:
                                Collections.sort(countryRecordList, new Comparator<CountryRecord>() {
                                    @Override
                                    public int compare(CountryRecord a, CountryRecord b) {
                                        if(a.cpa > b.cpa){
                                            return -1;
                                        }else  if(a.cpa < b.cpa){
                                            return 1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 26:
                                Collections.sort(countryRecordList, new Comparator<CountryRecord>() {
                                    @Override
                                    public int compare(CountryRecord a, CountryRecord b) {
                                        if(a.ctr > b.ctr){
                                            return 1;
                                        }else if(a.ctr < b.ctr){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1026:
                                Collections.sort(countryRecordList, new Comparator<CountryRecord>() {
                                    @Override
                                    public int compare(CountryRecord a, CountryRecord b) {
                                        if(a.ctr > b.ctr){
                                            return -1;
                                        }else if(a.ctr < b.ctr){
                                            return 1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 27:
                                Collections.sort(countryRecordList, new Comparator<CountryRecord>() {
                                    @Override
                                    public int compare(CountryRecord a, CountryRecord b) {
                                        if(a.cvr > b.cvr){
                                            return 1;
                                        }else  if(a.cvr < b.cvr){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1027:
                                Collections.sort(countryRecordList, new Comparator<CountryRecord>() {
                                    @Override
                                    public int compare(CountryRecord a, CountryRecord b) {
                                        if(a.cvr > b.cvr){
                                            return -1;
                                        }else if(a.cvr < b.cvr){
                                            return 1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 28:
                                Collections.sort(countryRecordList, new Comparator<CountryRecord>() {
                                    @Override
                                    public int compare(CountryRecord a, CountryRecord b) {
                                        if(a.roi > b.roi){
                                            return 1;
                                        }else if(a.roi < b.roi){
                                            return -1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1028:
                                Collections.sort(countryRecordList, new Comparator<CountryRecord>() {
                                    @Override
                                    public int compare(CountryRecord a, CountryRecord b) {
                                        if(a.roi > b.roi){
                                            return -1;
                                        }else if(a.roi < b.roi){
                                            return 1;
                                        }else{
                                            return 0;
                                        }
                                    }
                                });
                                break;
                        }
                        for(CountryRecord record : countryRecordList){
                            JsonObject one = new JsonObject();
                            one.addProperty("country_name", record.country_name);
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
                    }else{
                        for (String key : dataSets.keySet()) {
                            String sql = "select price from web_ad_tag_country_price_dict cpd, app_country_code_dict ccd " +
                                    "where cpd.country_code = ccd.country_code and ccd.country_name = '" + key + "' and tag_name = '" + tag + "'";
                            JSObject oneR = DB.findOneBySql(sql);
                            double price = Utils.convertDouble(oneR.get("price"),0);
                            JsonObject one = new JsonObject();
                            CountryRecord record = dataSets.get(key);
                            record.ctr = record.impressions > 0 ? record.click / record.impressions : 0;
                            record.cpa = record.installed > 0 ? record.spend / record.installed : 0;
                            record.cvr = record.click > 0 ? record.installed / record.click : 0;
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

        response.getWriter().write(json.toString());
    }

    class CountryRecord {
        public String country_name;
        public double impressions;
        public double installed;
        public double click;
        public double spend;
        public double ctr;
        public double cpa;
        public double cvr;
        public double roi;
    }
    class Campaigns {
        public String campaign_id;
        public String account_id;
        public String short_name;
        public String campaign_name;
        public String status;
        public String create_time;
        public String country_code;
        public double budget;
        public double bidding;
        public double impressions;
        public double installed;
        public double click;
        public double spend;
        public double ctr;
        public double cpa;
        public double cvr;
        public double roi;
        public double campaign_spends;
        public String network;
    }


    private JsonObject fetchOneAppDataOfFacebook(long tagId, String tagName, String startTime, String endTime, boolean countryCheck, String countryCode,String likeCampaignName,String campaignCreateTime,boolean hasROI,HashMap<String ,String> countryMap,String totalInstallComparisonValue) throws Exception {
        List<JSObject> listNotExistData = null;
        List<JSObject> list = DB.scan("web_ad_campaign_tag_rel").select("campaign_id")
                .where(DB.filter().whereEqualTo("tag_id", tagId)).execute();

        Set<String> campaignIdSet = new HashSet<>();
        for(JSObject j : list){
            campaignIdSet.add(j.get("campaign_id"));
        }

        String campaignIds = "";
        if(campaignCreateTime != null && campaignCreateTime != ""){
            List<JSObject> campaignIdJSObjectList = new ArrayList<>();
            String sqlQuery = "select campaign_id from ad_campaigns where app_name = '"+ tagName +"' and create_time like '" + campaignCreateTime + "%'";
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
        }else{
            for(String s : campaignIdSet){
                campaignIds += "'" + s + "',";
            }
        }

        if(campaignIds != null && campaignIds.length()>0){
            campaignIds = campaignIds.substring(0,campaignIds.length()-1);
        }

        List<JSObject> listCampaignSpend4CountryCode = new ArrayList<>();
        Map<String,JSObject> countryCampaignspendMap = new HashMap<>();
        if (campaignIds != null && campaignIds != "") {
            String sql = "";
            if(countryCode != null && countryCode != ""){
                sql = "select ch.campaign_id, sum(ch.total_spend) as campaign_spends " +
                        " from web_ad_campaigns c, web_ad_campaigns_country_history ch " +
                        " where c.campaign_id=ch.campaign_id " +
                        ((likeCampaignName == null || likeCampaignName == "") ? " " : " and campaign_name like '%" + likeCampaignName +"%' " )  +
                        " and date between '" + startTime + "' and '" + endTime + "' " +
                        " and c.status != 'removed' and c.campaign_id in (" + campaignIds + ")" +
                        " group by ch.campaign_id";
                listCampaignSpend4CountryCode = DB.findListBySql(sql);

                for(JSObject j : listCampaignSpend4CountryCode){
                    countryCampaignspendMap.put(j.get("campaign_id"),j);
                }
                sql = "select campaign_id, a.account_id,short_name, campaign_name, a.status, create_time, budget, bidding, spend, installed, impressions, click" +
                        " from (" +
                        "select ch.campaign_id, account_id, campaign_name,c.status, create_time, c.budget, c.bidding, sum(ch.total_spend) as spend, " +
                        "sum(ch.total_installed) as installed, sum(ch.total_impressions) as impressions " +
                        ",sum(ch.total_click) as click from web_ad_campaigns c,web_ad_campaigns_country_history ch  "+ "where c.campaign_id=ch.campaign_id and country_code= '" + countryCode + "' " +
                        ((likeCampaignName == null || likeCampaignName == "") ? " " : " and campaign_name like '%" + likeCampaignName +"%' " )  +
                        " and date between '" + startTime + "' and '" + endTime + "' " +
                        " and c.status != 'removed' and c.campaign_id in (" + campaignIds + ")" +
                        " group by ch.campaign_id " +
                        ((totalInstallComparisonValue == null || totalInstallComparisonValue == "") ? " " : " having installed " + totalInstallComparisonValue)  +
                        ") a left join web_account_id b on a.account_id = b.account_id";
                list = DB.findListBySql(sql);
                sql = "select c.campaign_id, c.account_id,short_name, c.campaign_name, c.status, create_time, budget, bidding,c.total_spend,c.total_installed \n" +
                        " from web_ad_campaigns_country_history ch,web_ad_campaigns c,web_account_id b where ch.campaign_id = c.campaign_id \n" +
                        "and c.account_id = b.account_id and ch.country_code = '" + countryCode + "'" +
                        " and ch.total_impressions = 0 and ch.total_click = 0 and c.`status` = 'ACTIVE' and c.campaign_id in (" + campaignIds + ")";
                listNotExistData = DB.findListBySql(sql);
            }else if (countryCheck) {
                sql = "select campaign_id, country_code, a.account_id,short_name, campaign_name, a.status, create_time, budget, bidding, spend, installed, impressions, click" +
                        " from (" +
                        "select ch.campaign_id, country_code, account_id, campaign_name,c.status, create_time, c.budget, c.bidding, sum(ch.total_spend) as spend, " +
                        "sum(ch.total_installed) as installed, sum(ch.total_impressions) as impressions " +
                        ",sum(ch.total_click) as click from web_ad_campaigns c, web_ad_campaigns_country_history ch " +
                        "where c.campaign_id=ch.campaign_id " +
                        ((likeCampaignName == null || likeCampaignName == "") ? " " : " and campaign_name like '%" + likeCampaignName +"%' " )  +
                        " and date between '" + startTime + "' and '" + endTime + "' " +
                        " and c.status != 'removed' and c.campaign_id in (" + campaignIds + ")" +
                        " group by ch.campaign_id, country_code " +
                        ((totalInstallComparisonValue == null || totalInstallComparisonValue == "") ? " " : " having installed " + totalInstallComparisonValue)  +
                        ") a left join web_account_id b on a.account_id = b.account_id";
                list = DB.findListBySql(sql);
//                sql = "select c.campaign_id, c.account_id,short_name, c.campaign_name, c.status, create_time, budget, bidding,c.total_spend,c.total_installed " +
//                        " from web_ad_campaigns_country_history ch,web_ad_campaigns c,web_account_id b where ch.campaign_id = c.campaign_id and c.account_id = b.account_id and " +
//                        " ch.total_impressions = 0 and ch.total_click = 0 and c.`status` = 'ACTIVE'";
//                listNotExistData = DB.findListBySql(sql);
            }else{
                sql = "select campaign_id, a.account_id,short_name, campaign_name, a.status, create_time, budget, bidding, spend, installed, impressions, click" +
                        " from (" +
                        "select ch.campaign_id, account_id, campaign_name,c.status, create_time, c.budget, c.bidding, sum(ch.total_spend) as spend, " +
                        "sum(ch.total_installed) as installed, sum(ch.total_impressions) as impressions " +
                        ",sum(ch.total_click) as click from web_ad_campaigns c, web_ad_campaigns_history ch " +
                        "where c.campaign_id=ch.campaign_id " +
                        ((likeCampaignName == null || likeCampaignName == "") ? " " : " and campaign_name like '%" + likeCampaignName +"%' " )  +
                        " and date between '" + startTime + "' and '" + endTime + "' " +
                        " and c.status != 'removed' and c.campaign_id in (" + campaignIds + ")" +
                        " group by ch.campaign_id " +
                        ((totalInstallComparisonValue == null || totalInstallComparisonValue == "") ? " " : " having installed " + totalInstallComparisonValue)  +
                        ") a left join web_account_id b on a.account_id = b.account_id";
                list = DB.findListBySql(sql);
                sql = "select campaign_id, a.account_id, short_name, campaign_name, create_time, a.status, budget, bidding, total_spend, total_installed" +
                        " from web_ad_campaigns a,web_account_id b where total_click = 0 and ctr =0 and a.`status` = 'ACTIVE' and a.account_id = b.account_id " +
                        " and cpa = 0 and campaign_id in (" + campaignIds + ")";
                listNotExistData = DB.findListBySql(sql);
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
                        String sqlT = "select sum(total_installed) installed, sum(cpa) cpa from web_ad_campaigns_country_history where campaign_id = '" + campaign_id + "' " +
                                "and country_code = '"+countryCode+"' and date between '"+startTime+"' and '"+endTime+"'";

                        JSObject twoI = DB.findOneBySql(sqlT);
                        cpaI = Utils.convertDouble(twoI.get("cpa"),0);
                        installedI = Utils.convertDouble(twoI.get("installed"),0);
                        roi = ( priceI - cpaI ) * installedI;
                    }
                }else{
                    String sql = "select cch.country_code, sum(cpa) cpa, sum(total_installed) installed, price " +
                            "from web_ad_campaigns_country_history cch,web_ad_tag_country_price_dict cpd " +
                            "where cch.country_code = cpd.country_code and campaign_id = '"+campaign_id+"' and tag_name = '"
                            + tagName + "' and date between '"+startTime+"' and '"+endTime+"' group by cch.country_code";


                    List<JSObject> listM = DB.findListBySql(sql);
                    for(JSObject j : listM){
                        cpaI = Utils.convertDouble(j.get("cpa"),0);
                        installedI = Utils.convertDouble(j.get("installed"),0);
                        priceI = Utils.convertDouble(j.get("price"),0);
                        roi += (priceI - cpaI)*installedI;
                    }
                }
            }

            String short_name = one.get("short_name");
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

            JSObject js = countryCampaignspendMap.get(campaign_id);
            double campaign_spends = 0;
            if(js != null && js.hasObjectData()){
                campaign_spends = Utils.convertDouble(js.get("campaign_spends"), 0);
            }

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
            d.addProperty("short_name", short_name);
            d.addProperty("account_id", account_id);
            d.addProperty("campaign_name", campaign_name);
            d.addProperty("status", status);
            d.addProperty("create_time", create_time);
            d.addProperty("country_code", country_code);
            d.addProperty("country_name", countryMap.get(country_code));
            d.addProperty("budget", budget);
            d.addProperty("bidding", bidding);
            d.addProperty("impressions", impressions);
            d.addProperty("spend", Utils.trimDouble(spend));
            d.addProperty("campaign_spends", campaign_spends);
            d.addProperty("installed", installed);
            d.addProperty("click", click);
            d.addProperty("ctr", Utils.trimDouble(ctr));
            d.addProperty("cpa", Utils.trimDouble(cpa));
            d.addProperty("cvr", Utils.trimDouble(cvr));
            d.addProperty("roi", Utils.trimDouble(roi));
            d.addProperty("network", "facebook");
            array.add(d);
        }
        if(listNotExistData != null){
            for(JSObject j : listNotExistData){
                if(j != null && j.hasObjectData()){
                    JsonObject d = new JsonObject();
                    String campaign_id = j.get("campaign_id");
                    String short_name = j.get("short_name");
                    String account_id = j.get("account_id");
                    String campaign_name = j.get("campaign_name");
                    String create_time = j.get("create_time").toString();
                    create_time = create_time.substring(0,create_time.length()-5);
                    String status = j.get("status");
                    double budget = j.get("budget");
                    double bidding = j.get("bidding");

                    double spend = Utils.convertDouble(j.get("total_spend"), 0);
                    double installed = Utils.convertDouble(j.get("total_installed"), 0);
                    d.addProperty("campaign_id", campaign_id);
                    d.addProperty("short_name", short_name);
                    d.addProperty("account_id", account_id);
                    d.addProperty("campaign_name", campaign_name);
                    d.addProperty("status", status);
                    d.addProperty("create_time", create_time);
                    d.addProperty("budget", budget);
                    d.addProperty("bidding", bidding);
                    d.addProperty("spend", Utils.trimDouble(spend));
                    d.addProperty("installed", installed);

                    //假定数字，页面显示时是"--",这里为了排序
                    d.addProperty("click", 0);
                    d.addProperty("impressions", 0);
                    d.addProperty("ctr", 0);
                    d.addProperty("cpa", 0);
                    d.addProperty("cvr", 0);
                    d.addProperty("roi", -100000);

                    d.addProperty("network", "facebook");
                    array.add(d);
                }
            }
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

    private JsonObject fetchOneAppDataOfAdmob(long tagId, String tagName, String startTime, String endTime, boolean countryCheck, String countryCode,String likeCampaignName,String campaignCreateTime,boolean hasROI,HashMap<String ,String> countryMap,String totalInstallComparisonValue) throws Exception {
        List<JSObject> listNotExistData = null;
        List<JSObject> list = DB.scan("web_ad_campaign_tag_admob_rel").select("campaign_id")
                .where(DB.filter().whereEqualTo("tag_id", tagId)).execute();

        Set<String> campaignIdSet = new HashSet<>();
        for(JSObject j : list){
            campaignIdSet.add(j.get("campaign_id"));
        }

        String campaignIds = "";
        if(campaignCreateTime != null && campaignCreateTime != ""){
            List<JSObject> campaignIdJSObjectList = new ArrayList<>();
            String sqlQuery = "select campaign_id from ad_campaigns_admob where app_name = '"+ tagName +"' and create_time like '" + campaignCreateTime + "%'";
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
        }else{
            for(String s : campaignIdSet){
                campaignIds += "'" + s + "',";
            }
        }

        if(campaignIds != null && campaignIds.length()>0){
            campaignIds = campaignIds.substring(0,campaignIds.length()-1);
        }

        List<JSObject> listCampaignSpend4CountryCode = new ArrayList<>();
        Map<String,JSObject> countryCampaignspendMap = new HashMap<>();
        if (campaignIds != null && campaignIds != "") {
            String sql = "";
            List<JSObject> listAll = new ArrayList<>();
            List<JSObject> listHasData = new ArrayList<>();
            if(countryCode != null && countryCode != ""){
                sql = "select ch.campaign_id, sum(ch.total_spend) as campaign_spends " +
                        " from web_ad_campaigns_admob c, web_ad_campaigns_country_history_admob ch " +
                        " where c.campaign_id=ch.campaign_id " +
                        ((likeCampaignName == null || likeCampaignName == "") ? " " : " and campaign_name like '%" + likeCampaignName +"%' " )  +
                        " and date between '" + startTime + "' and '" + endTime + "' " +
                        " and c.status != 'removed' and c.campaign_id in (" + campaignIds + ")" +
                        " group by ch.campaign_id";
                listCampaignSpend4CountryCode = DB.findListBySql(sql);

                for(JSObject j : listCampaignSpend4CountryCode){
                    countryCampaignspendMap.put(j.get("campaign_id"),j);
                }
                sql = "select campaign_id, a.account_id,short_name, campaign_name, a.status, create_time, budget, bidding, spend, installed, impressions, click" +
                        " from (" +
                        "select ch.campaign_id, account_id, campaign_name,c.status, create_time, c.budget, c.bidding, sum(ch.total_spend) as spend, " +
                        "sum(ch.total_installed) as installed, sum(ch.total_impressions) as impressions " +
                        ",sum(ch.total_click) as click from web_ad_campaigns_admob c,web_ad_campaigns_country_history_admob ch  "+ "where c.campaign_id=ch.campaign_id and country_code= '" + countryCode + "' " +
                        ((likeCampaignName == null || likeCampaignName == "") ? " " : " and campaign_name like '%" + likeCampaignName +"%' " )  +
                        " and date between '" + startTime + "' and '" + endTime + "' " +
                        " and c.status != 'removed' and c.campaign_id in (" + campaignIds + ")" +
                        " group by ch.campaign_id " +
                        ((totalInstallComparisonValue == null || totalInstallComparisonValue == "") ? " " : " having installed " + totalInstallComparisonValue)  +
                        ") a left join web_account_id_admob b on a.account_id = b.account_id";
                list = DB.findListBySql(sql);

                sql = "select campaign_id, a.account_id, short_name, campaign_name, create_time, status, budget, bidding, total_spend, total_installed, total_click, total_impressions, cpa,ctr, " +
                        "(case when total_click > 0 then total_installed/total_click else 0 end) as cvr " +
                        " from web_ad_campaigns_admob a , web_account_id_admob b where a.status = 'enabled' and " +
                        "campaign_id in (" + campaignIds + ") and a.account_id = b.account_id";
                listAll = DB.findListBySql(sql);
                sql = "select campaign_id, impressions from ( " +
                        "select ch.campaign_id, " +
                        " sum(ch.total_impressions) as impressions " +
                        " from web_ad_campaigns_admob c, web_ad_campaigns_country_history_admob ch " +
                        "where c.campaign_id = ch.campaign_id " +
                        "and date between '" + startTime + "' and '" + endTime + "' " +
                        "and c.status != 'removed' and c.campaign_id in (" + campaignIds + ") " +
                        "group by ch.campaign_id having impressions > 0 ) a ";
                listHasData = DB.findListBySql(sql);
                listNotExistData = Utils.getDiffJSObjectList(listAll, listHasData, "campaign_id");
            }else if (countryCheck) {
                sql = "select campaign_id, country_code, a.account_id,short_name, campaign_name, a.status, create_time, budget, bidding, spend, installed, impressions, click" +
                        " from (" +
                        "select ch.campaign_id, country_code, account_id, campaign_name,c.status, create_time, c.budget, c.bidding, sum(ch.total_spend) as spend, " +
                        "sum(ch.total_installed) as installed, sum(ch.total_impressions) as impressions " +
                        ",sum(ch.total_click) as click from web_ad_campaigns_admob c, web_ad_campaigns_country_history_admob ch " +
                        "where c.campaign_id=ch.campaign_id " +
                        ((likeCampaignName == null || likeCampaignName == "") ? " " : " and campaign_name like '%" + likeCampaignName +"%' " )  +
                        " and date between '" + startTime + "' and '" + endTime + "' " +
                        " and c.status != 'removed' and c.campaign_id in (" + campaignIds + ")" +
                        " group by ch.campaign_id, country_code " +
                        ((totalInstallComparisonValue == null || totalInstallComparisonValue == "") ? " " : " having installed " + totalInstallComparisonValue)  +
                        ") a left join web_account_id_admob b on a.account_id = b.account_id";
                list = DB.findListBySql(sql);
            }else{
                sql = "select campaign_id, a.account_id,short_name, campaign_name, a.status, create_time, budget, bidding, spend, installed, impressions, click" +
                        " from (" +
                        "select ch.campaign_id, account_id, campaign_name,c.status, create_time, c.budget, c.bidding, sum(ch.total_spend) as spend, " +
                        "sum(ch.total_installed) as installed, sum(ch.total_impressions) as impressions " +
                        ",sum(ch.total_click) as click from web_ad_campaigns_admob c, web_ad_campaigns_history_admob ch " +
                        "where c.campaign_id=ch.campaign_id " +
                        ((likeCampaignName == null || likeCampaignName == "") ? " " : " and campaign_name like '%" + likeCampaignName +"%' " )  +
                        " and date between '" + startTime + "' and '" + endTime + "' " +
                        " and c.status != 'removed' and c.campaign_id in (" + campaignIds + ")" +
                        " group by ch.campaign_id " +
                        ((totalInstallComparisonValue == null || totalInstallComparisonValue == "") ? " " : " having installed " + totalInstallComparisonValue)  +
                        ") a left join web_account_id_admob b on a.account_id = b.account_id";
                list = DB.findListBySql(sql);
                sql = "select campaign_id, a.account_id, short_name, campaign_name, create_time, status, budget, bidding, total_spend, total_installed, total_click, total_impressions, cpa,ctr, " +
                        "(case when total_click > 0 then total_installed/total_click else 0 end) as cvr " +
                        " from web_ad_campaigns_admob a , web_account_id_admob b where a.status = 'enabled' and " +
                        "campaign_id in (" + campaignIds + ") and a.account_id = b.account_id";
                listAll = DB.findListBySql(sql);
                sql = "select campaign_id, impressions from ( " +
                        "select ch.campaign_id, " +
                        " sum(ch.total_impressions) as impressions " +
                        " from web_ad_campaigns_admob c, web_ad_campaigns_country_history_admob ch " +
                        "where c.campaign_id = ch.campaign_id " +
                        "and date between '" + startTime + "' and '" + endTime + "' " +
                        "and c.status != 'removed' and c.campaign_id in (" + campaignIds + ") " +
                        "group by ch.campaign_id having impressions > 0 ) a ";
                listHasData = DB.findListBySql(sql);
                listNotExistData = Utils.getDiffJSObjectList(listAll, listHasData, "campaign_id");
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
                        String sqlT = "select sum(total_installed) installed, sum(cpa) cpa from web_ad_campaigns_country_history_admob where campaign_id = '" + campaign_id + "' " +
                                "and country_code = '"+countryCode+"' and date between '"+startTime+"' and '"+endTime+"'";

                        JSObject twoI = DB.findOneBySql(sqlT);
                        cpaI = Utils.convertDouble(twoI.get("cpa"),0);
                        installedI = Utils.convertDouble(twoI.get("installed"),0);
                        roi = ( priceI - cpaI ) * installedI;
                    }
                }else{
                    String sql = "select cch.country_code, sum(cpa) cpa, sum(total_installed) installed, price " +
                            "from web_ad_campaigns_country_history_admob cch,web_ad_tag_country_price_dict cpd " +
                            "where cch.country_code = cpd.country_code and campaign_id = '"+campaign_id+"' and tag_name = '"
                            + tagName + "' and date between '"+startTime+"' and '"+endTime+"' group by cch.country_code";


                    List<JSObject> listM = DB.findListBySql(sql);
                    for(JSObject j : listM){
                        cpaI = Utils.convertDouble(j.get("cpa"),0);
                        installedI = Utils.convertDouble(j.get("installed"),0);
                        priceI = Utils.convertDouble(j.get("price"),0);
                        roi += (priceI - cpaI)*installedI;
                    }
                }
            }

            String short_name = one.get("short_name");
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

            JSObject js = countryCampaignspendMap.get(campaign_id);
            double campaign_spends = 0;
            if(js != null && js.hasObjectData()){
                campaign_spends = Utils.convertDouble(js.get("campaign_spends"), 0);
            }

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
            d.addProperty("short_name", short_name);
            d.addProperty("account_id", account_id);
            d.addProperty("campaign_name", campaign_name);
            d.addProperty("status", status);
            d.addProperty("create_time", create_time);
            d.addProperty("country_code", country_code);
            d.addProperty("country_name", countryMap.get(country_code));
            d.addProperty("budget", budget);
            d.addProperty("bidding", bidding);
            d.addProperty("impressions", impressions);
            d.addProperty("spend", Utils.trimDouble(spend));
            d.addProperty("campaign_spends", campaign_spends);
            d.addProperty("installed", installed);
            d.addProperty("click", click);
            d.addProperty("ctr", Utils.trimDouble(ctr));
            d.addProperty("cpa", Utils.trimDouble(cpa));
            d.addProperty("cvr", Utils.trimDouble(cvr));
            d.addProperty("roi", Utils.trimDouble(roi));
            d.addProperty("network", "facebook");
            array.add(d);
        }
        if(listNotExistData != null){
            for(JSObject j : listNotExistData){
                if(j != null && j.hasObjectData()){
                    JsonObject d = new JsonObject();
                    String campaign_id = j.get("campaign_id");
                    String short_name = j.get("short_name");
                    String account_id = j.get("account_id");
                    String campaign_name = j.get("campaign_name");
                    String create_time = j.get("create_time").toString();
                    create_time = create_time.substring(0,create_time.length()-5);
                    String status = j.get("status");
                    double budget = j.get("budget");
                    double bidding = j.get("bidding");

                    double spend = Utils.convertDouble(j.get("total_spend"), 0);
                    double installed = Utils.convertDouble(j.get("total_installed"), 0);
                    d.addProperty("campaign_id", campaign_id);
                    d.addProperty("short_name", short_name);
                    d.addProperty("account_id", account_id);
                    d.addProperty("campaign_name", campaign_name);
                    d.addProperty("status", status);
                    d.addProperty("create_time", create_time);
                    d.addProperty("budget", budget);
                    d.addProperty("bidding", bidding);
                    d.addProperty("spend", Utils.trimDouble(spend));
                    d.addProperty("installed", installed);

                    //假定数字，页面显示时是"--",这里为了排序
                    d.addProperty("click", 0);
                    d.addProperty("impressions", 0);
                    d.addProperty("ctr", 0);
                    d.addProperty("cpa", 0);
                    d.addProperty("cvr", 0);
                    d.addProperty("roi", -100000);

                    d.addProperty("network", "admob");
                    array.add(d);
                }
            }
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
