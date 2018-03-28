package com.bestgo.admanager.servlet;

import com.bestgo.admanager.DateUtil;
import com.bestgo.admanager.Utils;
import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 多条件查询
 */
@WebServlet(name = "QueryByMulConditions", urlPatterns = {"/query_by_mul_conditions"}, asyncSupported = true)
public class QueryByMulConditions extends HttpServlet {

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
        if (sorterId != null && sorterId != "") {
            sorter = Utils.parseInt(sorterId, 0);
        }
        String tag = request.getParameter("tag");
        String countryCheck = request.getParameter("countryCheck");
        String containsNoDataCampaignCheck = request.getParameter("containsNoDataCampaignCheck");
        String campaignCreateTime = request.getParameter("campaignCreateTime");
        String countryCode = request.getParameter("countryCode");
        String countryName = request.getParameter("countryName");

        String totalInstallComparisonValue = request.getParameter("totalInstallComparisonValue");
        String totalInstallOperator = request.getParameter("totalInstallOperator");

        String cpaComparisonValue = request.getParameter("cpaComparisonValue");
        String cpaOperator = request.getParameter("cpaOperator");

        String biddingComparisonValue = request.getParameter("biddingComparisonValue");

        String likeCampaignName = request.getParameter("likeCampaignName");
        HashMap<String ,String> countryMap = Utils.getCountryMap();

        String beforeThreeDays = DateUtil.addDay(endTime,-3,"yyyy-MM-dd");//不包括endTime
        try {
            JSObject tagObject = DB.simpleScan("web_tag")
                    .select("id", "tag_name")
                    .where(DB.filter().whereEqualTo("tag_name", tag)).execute();
            if (tagObject.hasObjectData()) {
                Long id = tagObject.get("id");
                JsonObject jsonObject = new JsonObject();
                if (countryCode != null && countryCode != "") {
                    countryCheck = "false";
                }
                if ("false".equals(adwordsCheck) && "false".equals(facebookCheck)) {
                    JsonObject admob = fetchOneAppData(id, tag,startTime, endTime, true, "true".equals(countryCheck), countryCode,likeCampaignName,campaignCreateTime,countryMap,totalInstallComparisonValue, "true".equals(containsNoDataCampaignCheck),countryCode,cpaComparisonValue,biddingComparisonValue,totalInstallOperator,cpaOperator,beforeThreeDays);
                    JsonObject facebook = fetchOneAppData(id, tag,startTime, endTime,false, "true".equals(countryCheck), countryCode,likeCampaignName,campaignCreateTime,countryMap,totalInstallComparisonValue, "true".equals(containsNoDataCampaignCheck),countryName,cpaComparisonValue,biddingComparisonValue,totalInstallOperator,cpaOperator,beforeThreeDays);
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
                                        if(a.un_rate > b.un_rate){
                                            return 1;
                                        }else  if(a.un_rate < b.un_rate){
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
                                        if(a.un_rate > b.un_rate){
                                            return -1;
                                        }else if(a.un_rate < b.un_rate){
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
                            j.addProperty("open_cpa",c.open_cpa);
                            j.addProperty("cvr",c.cvr);
                            j.addProperty("un_rate",c.un_rate);
                            j.addProperty("open_rate",c.open_rate);
                            j.addProperty("campaign_spends",c.campaign_spends);
                            j.addProperty("network",c.network);
                            jsonArray.add(j);
                        }
                        jsonObject.addProperty("total_spend", Utils.trimDouble(total_spend,0));
                        jsonObject.addProperty("total_installed", total_installed);
                        jsonObject.addProperty("total_impressions", total_impressions);
                        jsonObject.addProperty("total_click", total_click);
                        jsonObject.addProperty("total_ctr", Utils.trimDouble(total_ctr,3));
                        jsonObject.addProperty("total_cpa", Utils.trimDouble(total_cpa,3));
                        jsonObject.addProperty("total_cvr", Utils.trimDouble(total_cvr,3));
                        jsonObject.add("array",jsonArray);
                    }else{
                        admob.addProperty("total_spend", Utils.trimDouble(total_spend,0));
                        admob.addProperty("total_installed", total_installed);
                        admob.addProperty("total_impressions", total_impressions);
                        admob.addProperty("total_click", total_click);
                        admob.addProperty("total_ctr", Utils.trimDouble(total_ctr,3));
                        admob.addProperty("total_cpa", Utils.trimDouble(total_cpa,3));
                        admob.addProperty("total_cvr", Utils.trimDouble(total_cvr,3));
                        jsonObject = admob;
                    }


                } else {
                    if("true".equals(adwordsCheck)){
                        jsonObject = fetchOneAppData(id, tag,startTime, endTime, true, "true".equals(countryCheck), countryCode,likeCampaignName,campaignCreateTime,countryMap,totalInstallComparisonValue, "true".equals(containsNoDataCampaignCheck),countryCode,cpaComparisonValue,biddingComparisonValue,totalInstallOperator,cpaOperator,beforeThreeDays);

                    }else{
                        jsonObject = fetchOneAppData(id, tag,startTime, endTime, false, "true".equals(countryCheck), countryCode,likeCampaignName,campaignCreateTime,countryMap,totalInstallComparisonValue, "true".equals(containsNoDataCampaignCheck),countryName,cpaComparisonValue,biddingComparisonValue,totalInstallOperator,cpaOperator,beforeThreeDays);

                    }
                }
                if ("true".equals(countryCheck)) {
                    JsonArray array = jsonObject.getAsJsonArray("array");
                    HashMap<String, CountryRecord> dataSets = new HashMap<>();
                    for (int i = 0; i < array.size(); i++) {
                        JsonObject one = array.get(i).getAsJsonObject();
                        String currCountryName = "";
                        if (one.get("country_name").isJsonNull()) {
                            currCountryName = one.get("country_code").getAsString();
                        } else {
                            currCountryName = one.get("country_name").getAsString();
                        }
                        CountryRecord record = dataSets.get(currCountryName);
                        if (record == null) {
                            record = new CountryRecord();
                            dataSets.put(currCountryName, record);
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
                            CountryRecord record = dataSets.get(key);
                            record.country_name = key;
                            record.ctr = record.impressions > 0 ? record.click / record.impressions : 0;
                            record.cpa = record.installed > 0 ? record.spend / record.installed : 0;
                            record.cvr = record.click > 0 ? record.installed / record.click : 0;
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
                                        if(a.un_rate > b.un_rate){
                                            return 1;
                                        }else if(a.un_rate < b.un_rate){
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
                                        if(a.un_rate > b.un_rate){
                                            return -1;
                                        }else if(a.un_rate < b.un_rate){
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
                            one.addProperty("spend", Utils.trimDouble(record.spend,0));
                            one.addProperty("ctr", Utils.trimDouble(record.ctr,3));
                            one.addProperty("cpa", Utils.trimDouble(record.cpa,3));
                            one.addProperty("cvr", Utils.trimDouble(record.cvr,3));
                            one.addProperty("un_rate", Utils.trimDouble(record.un_rate,3));
                            newArr.add(one);
                        }
                    }else{
                        for (String key : dataSets.keySet()) {
                            JsonObject one = new JsonObject();
                            CountryRecord record = dataSets.get(key);
                            record.ctr = record.impressions > 0 ? record.click / record.impressions : 0;
                            record.cpa = record.installed > 0 ? record.spend / record.installed : 0;
                            record.cvr = record.click > 0 ? record.installed / record.click : 0;
                            one.addProperty("country_name", key);
                            one.addProperty("impressions", record.impressions);
                            one.addProperty("installed", record.installed);
                            one.addProperty("click", record.click);
                            one.addProperty("spend", Utils.trimDouble(record.spend,0));
                            one.addProperty("ctr", Utils.trimDouble(record.ctr,3));
                            one.addProperty("cpa", Utils.trimDouble(record.cpa,3));
                            one.addProperty("cvr", Utils.trimDouble(record.cvr,3));
                            one.addProperty("un_rate", Utils.trimDouble(record.un_rate,3));
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



    private JsonObject fetchOneAppData(long tagId, String tagName, String startTime, String endTime, boolean admobCheck, boolean countryCheck, String countryCode,String likeCampaignName,String campaignCreateTime,HashMap<String ,String> countryMap,String totalInstallComparisonValue, boolean containsNoDataCampaignCheck,String country,String cpaComparisonValue,String biddingComparisonValue,String totalInstallOperator,String cpaOperator,String beforeThreeDays) throws Exception {
        String webAdCampaignTagRelTable = "web_ad_campaign_tag_rel";
        String webAdCampaignsTable = "web_ad_campaigns";
        String adCampaignsTable = "ad_campaigns";
        String webAdCampaignsHistoryTable = "web_ad_campaigns_history";
        String webAccountIdTable = "web_account_id";
        String webAdCampaignsCountryHistoryTable = "web_ad_campaigns_country_history";
        String openStatus = "ACTIVE";
        List<JSObject> listAll = new ArrayList<>();
        List<JSObject> listNoData = null;
        if (admobCheck) {
            adCampaignsTable = "ad_campaigns_admob";
            webAdCampaignTagRelTable = "web_ad_campaign_tag_admob_rel";
            webAdCampaignsTable = "web_ad_campaigns_admob";
            webAdCampaignsHistoryTable = "web_ad_campaigns_history_admob";
            webAccountIdTable = "web_account_id_admob";
            webAdCampaignsCountryHistoryTable = "web_ad_campaigns_country_history_admob";
            openStatus = "enabled";
        }

        List<JSObject> list = DB.scan(webAdCampaignTagRelTable).select("campaign_id")
                .where(DB.filter().whereEqualTo("tag_id", tagId)).execute();


        Set<String> campaignIdSet = new HashSet<>();
            for(JSObject j : list){
            campaignIdSet.add(j.get("campaign_id"));
        }

        String campaignIds = "";
        if(campaignCreateTime != null && campaignCreateTime != ""){
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
            String havingField = "";
            if(totalInstallComparisonValue == "" && cpaComparisonValue == ""){
                havingField = " having impressions > 0 ";
            }else{
                if(totalInstallComparisonValue != "" && cpaComparisonValue == ""){
                    havingField = " having installed " + totalInstallOperator + " " + totalInstallComparisonValue;
                }else if(totalInstallComparisonValue == "" && cpaComparisonValue != ""){
                    havingField = " having cpa " + cpaOperator + " " + cpaComparisonValue;
                }else{
                    havingField = " having installed " + totalInstallOperator + " " + totalInstallComparisonValue + " and cpa " + cpaOperator + " " + cpaComparisonValue;
                }

            }

            String sql = "";
            if(countryCode != null && countryCode != ""){
                sql = "select ch.campaign_id, sum(ch.total_spend) as campaign_spends " +
                        " from " + webAdCampaignsTable + " c, " + webAdCampaignsCountryHistoryTable + " ch " +
                        " where c.campaign_id = ch.campaign_id " +
                        ((likeCampaignName == "" || likeCampaignName == null) ? " " : " and campaign_name like '%" + likeCampaignName +"%' " )  +
                        " and date between '" + startTime + "' and '" + endTime + "' " +
                        " and c.status != 'removed' and ch.campaign_id in (" + campaignIds + ")" +
                        " group by ch.campaign_id";
                listCampaignSpend4CountryCode = DB.findListBySql(sql);

                for(JSObject j : listCampaignSpend4CountryCode){
                    countryCampaignspendMap.put(j.get("campaign_id"),j);
                }
                sql = "select campaign_id, a.account_id,short_name, campaign_name, a.status, create_time, budget, bidding, spend, installed, impressions, click" +
                        " from (" +
                        "select ch.campaign_id, account_id, campaign_name,c.status, create_time, c.budget, c.bidding, sum(ch.total_spend) as spend, " +
                        "sum(ch.total_installed) as installed, sum(ch.total_impressions) as impressions,sum(ch.total_click) as click, " +
                        "(case when sum(ch.total_installed) > 0 then sum(ch.total_spend) / sum(ch.total_installed) else 0 end) as cpa " +
                        " from " + webAdCampaignsTable + " c, " + webAdCampaignsCountryHistoryTable + " ch " +
                        "where c.campaign_id=ch.campaign_id and country_code= '" + countryCode + "' " +
                        ((likeCampaignName == "" || likeCampaignName == null) ? " " : " and campaign_name like '%" + likeCampaignName +"%' " )  +
                        " and date between '" + startTime + "' and '" + endTime + "' " +
                        " and c.status != 'removed' and c.campaign_id in (" + campaignIds + ")" +
                        " group by ch.campaign_id " + havingField +
                        ") a left join " + webAccountIdTable + " b on a.account_id = b.account_id";

                list = DB.findListBySql(sql);
                if(containsNoDataCampaignCheck){
                    if(admobCheck){
                        sql = "SELECT c.campaign_id, c.account_id, short_name, c.campaign_name, c.create_time, c.status, budget, c.bidding, c.total_spend " +
                                " FROM " + adCampaignsTable + " a, " + webAdCampaignsTable + " c, " + webAccountIdTable + " b WHERE a.campaign_id = c.campaign_id " +
                                " AND c.account_id = b.account_id AND c.status = '" + openStatus + "' AND a.country_region = '" + country + "' AND app_name = '" + tagName + "' " +
                                ((likeCampaignName == "" || likeCampaignName == null) ? " " : " and c.campaign_name like '%" + likeCampaignName +"%' " );
                    }else{
                        sql = "SELECT c.campaign_id, c.account_id, short_name, c.campaign_name, c.create_time, c.status, budget, c.bidding, c.total_spend " +
                                " FROM " + adCampaignsTable + " a, " + webAdCampaignsTable + " c, " + webAccountIdTable + " b WHERE a.campaign_id = c.campaign_id " +
                                " AND c.account_id = b.account_id AND b.status = 1 " +
                                " AND c.status = '" + openStatus + "' AND a.country_region = '" + country + "' AND app_name = '" + tagName + "' " +
                                ((likeCampaignName == "" || likeCampaignName == null) ? " " : " and c.campaign_name like '%" + likeCampaignName +"%' " );
                    }

                    listAll = DB.findListBySql(sql);
                    if(list != null && list.size() > 0){
                        listNoData = Utils.getDiffJSObjectList(listAll, list, "campaign_id");
                    }else{
                        listNoData = listAll;
                    }
                }
            }else if (countryCheck) {
                sql = "select campaign_id, country_code, a.account_id,short_name, campaign_name, a.status, create_time, budget, bidding, spend, installed, impressions, click" +
                        " from (" +
                        "select ch.campaign_id, country_code, account_id, campaign_name,c.status, create_time, c.budget, c.bidding, sum(ch.total_spend) as spend, " +
                        "sum(ch.total_installed) as installed, sum(ch.total_impressions) as impressions, " +
                        "(case when sum(ch.total_installed) > 0 then sum(ch.total_spend) / sum(ch.total_installed) else 0 end) as cpa, " +
                        " sum(ch.total_click) as click from " + webAdCampaignsTable + " c, " + webAdCampaignsCountryHistoryTable + " ch " +
                        "where c.campaign_id=ch.campaign_id " +
                        ((likeCampaignName == null || likeCampaignName == "") ? " " : " and campaign_name like '%" + likeCampaignName +"%' " )  +
                        " and date between '" + startTime + "' and '" + endTime + "' " +
                        " and c.status != 'removed' and c.campaign_id in (" + campaignIds + ")" +
                        " group by ch.campaign_id, country_code " + havingField +
                        ") a left join " + webAccountIdTable + " b on a.account_id = b.account_id";
                list = DB.findListBySql(sql);
            }else{
                sql = "select campaign_id, a.account_id,short_name, campaign_name, a.status, create_time, budget, bidding, spend, installed, impressions, click" +
                        " from (" +
                        "select ch.campaign_id, account_id, campaign_name,c.status, create_time, c.budget, c.bidding, sum(ch.total_spend) as spend, " +
                        "sum(ch.total_installed) as installed, sum(ch.total_impressions) as impressions, " +
                        "(case when sum(ch.total_installed) > 0 then sum(ch.total_spend) / sum(ch.total_installed) else 0 end) as cpa, " +
                        " sum(ch.total_click) as click from " + webAdCampaignsTable + " c, " + webAdCampaignsHistoryTable + " ch " +
                        "where c.campaign_id=ch.campaign_id " +
                        ((likeCampaignName == "" || likeCampaignName == null) ? " " : " and campaign_name like '%" + likeCampaignName +"%' " )  +
                        " and date between '" + startTime + "' and '" + endTime + "' " +
                        " and c.status != 'removed' and c.campaign_id in (" + campaignIds + ")" +
                        " group by ch.campaign_id "  + havingField +
                        ") a left join " + webAccountIdTable + " b on a.account_id = b.account_id";
                list = DB.findListBySql(sql);
                if(containsNoDataCampaignCheck){
                    if(admobCheck){
                        sql = "select campaign_id, c.account_id, short_name, campaign_name, create_time, c.status, budget, bidding, c.total_spend " +
                                "  from " + webAdCampaignsTable + " c LEFT JOIN " + webAccountIdTable + " b ON c.account_id = b.account_id where c.status = '" + openStatus + "'" +
                                ((likeCampaignName == "" || likeCampaignName == null) ? " " : " and campaign_name like '%" + likeCampaignName +"%' " )  +
                                " and campaign_id in (" + campaignIds + ") ";
                    }else{
                        sql = "select campaign_id, c.account_id, short_name, campaign_name, create_time, c.status, budget, bidding, c.total_spend " +
                                "  from " + webAdCampaignsTable + " c LEFT JOIN " + webAccountIdTable + " b ON c.account_id = b.account_id " +
                                "where c.status = '" + openStatus + "' AND b.status = 1 " +
                                ((likeCampaignName == "" || likeCampaignName == null) ? " " : " and campaign_name like '%" + likeCampaignName +"%' " )  +
                                " AND campaign_id in (" + campaignIds + ") ";
                    }

                    listAll = DB.findListBySql(sql);
                    if(list != null && list.size() >0){
                        listNoData = Utils.getDiffJSObjectList(listAll, list, "campaign_id");
                    }else{
                        listNoData = listAll;
                    }
                }
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

        if(list != null && list.size() > 0){
            for (JSObject one : list) {
                double bidding = one.get("bidding");
                if(biddingComparisonValue != ""){
                    double v = Double.parseDouble(biddingComparisonValue);
                    if(bidding != v){
                        continue;
                    }
                }
                double installed = Utils.convertDouble(one.get("installed"), 0);
                String campaignId = one.get("campaign_id");

                //系列卸载率 = 系列卸载数量 / 系列安装数量(随着数据量的增加，将来这里必须提前算出来！！！！！)
                double unRate = 0;

                //系列开启率 = 系列安装数量 / 系列总安装(随着数据量的增加，将来这里必须提前算出来！！！！！)
                double openRate = 0;

                if(admobCheck){
//                    String sqlQuery = "select COUNT(id) as uninstall_count from ad_campaign_user_date_admob_rel where campaign_id = '" + campaignId + "' and uninstall_date is NOT NULL and ";
                    String sqlQuery = "SELECT COUNT(id) AS uninstall_count FROM ad_campaign_user_date_admob_rel " +
                                      " WHERE campaign_id = '" + campaignId + "' AND uninstall_date IS NOT NULL " +
                                      " AND query_date BETWEEN '2018-02-11' AND '" + beforeThreeDays + "' ";
                    JSObject oneQ = DB.findOneBySql(sqlQuery);
                    long uninstallCount = 0;
                    long installCount = 0;
                    if(oneQ.hasObjectData()){
                        uninstallCount = oneQ.get("uninstall_count");
                    }
                    sqlQuery = "SELECT COUNT(id) as install_count FROM ad_campaign_user_date_admob_rel " +
                            " WHERE campaign_id = '" + campaignId + "'" +
                            " AND query_date BETWEEN '2018-02-11' AND '" + beforeThreeDays + "' ";
                    oneQ = DB.findOneBySql(sqlQuery);
                    if(oneQ.hasObjectData()){
                        installCount = oneQ.get("install_count");
                        unRate = installCount == 0 ? 0 : ((double)uninstallCount / installCount);
                    }
                    sqlQuery = "SELECT sum(ch.total_installed) as total_installeds " +
                               " FROM web_ad_campaigns_admob c, web_ad_campaigns_history_admob ch " +
                               " WHERE c.campaign_id=ch.campaign_id  AND c.campaign_id = '" + campaignId + "'" +
                               " AND c.status != 'removed' " +
                               " AND date between '2018-02-11' AND '" + beforeThreeDays + "'";
                    oneQ = DB.findOneBySql(sqlQuery);
                    if(oneQ.hasObjectData()){
                        double totalInstalleds = Utils.convertDouble(oneQ.get("total_installeds"),0);
                        openRate = totalInstalleds == 0 ? 0 : ((double)installCount / totalInstalleds);
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
                double spend = Utils.convertDouble(one.get("spend"), 0);
                double impressions = Utils.convertDouble(one.get("impressions"), 0);
                double click = Utils.convertDouble(one.get("click"), 0);
                double ctr = impressions > 0 ? click / impressions : 0;
                double cpa = installed > 0 ? spend / installed : 0;
                double openCpa = openRate == 0 ? 0 : cpa / openRate;
                double cvr = click > 0 ? installed / click : 0;

                JSObject js = countryCampaignspendMap.get(campaignId);
                double campaign_spends = 0;
                if(js != null && js.hasObjectData()){
                    campaign_spends = Utils.convertDouble(js.get("campaign_spends"), 0);
                }
                total_spend += spend;
                total_installed += installed;
                total_impressions += impressions;
                total_click += click;
                total_ctr = total_impressions > 0 ? total_click / total_impressions : 0;
                total_cpa = total_installed > 0 ? total_spend / total_installed : 0;
                total_cvr = total_click > 0 ? total_installed / total_click : 0;


                JsonObject d = new JsonObject();
                d.addProperty("campaign_id", campaignId);
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
                d.addProperty("spend", Utils.trimDouble(spend,0));
                d.addProperty("campaign_spends", campaign_spends);
                d.addProperty("installed", installed);
                d.addProperty("click", click);
                d.addProperty("ctr", Utils.trimDouble(ctr,3));
                d.addProperty("cpa", Utils.trimDouble(cpa,3));
                d.addProperty("open_cpa", Utils.trimDouble(openCpa,3));
                d.addProperty("cvr", Utils.trimDouble(cvr,3));
                d.addProperty("un_rate", Utils.trimDouble(unRate,3));
                d.addProperty("open_rate", Utils.trimDouble(openRate,3));
                if (admobCheck) {
                    d.addProperty("network", "admob");
                } else {
                    d.addProperty("network", "facebook");
                }
                array.add(d);
            }
        }

        if(listNoData != null && listNoData.size() > 0){
            for (JSObject one : listNoData) {
                double bidding = one.get("bidding");
                if(biddingComparisonValue != ""){
                    double v = Double.parseDouble(biddingComparisonValue);
                    if(bidding != v){
                        continue;
                    }
                }
                String campaign_id = one.get("campaign_id");
                String short_name = one.get("short_name");
                String account_id = one.get("account_id");
                String campaign_name = one.get("campaign_name");
                String status = one.get("status");
                String create_time = one.get("create_time").toString();
                create_time = create_time.substring(0,create_time.length()-5);
                String country_code = one.get("country_code");
                double budget = one.get("budget");
                double spend = Utils.convertDouble(one.get("spend"), 0);

                JSObject js = countryCampaignspendMap.get(campaign_id);
                double campaign_spends = 0;
                if(js != null && js.hasObjectData()){
                    campaign_spends = Utils.convertDouble(js.get("campaign_spends"), 0);
                }

                total_spend += spend;

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
                d.addProperty("impressions", 0);
                d.addProperty("spend", Utils.trimDouble(spend,0));
                d.addProperty("campaign_spends", campaign_spends);
                d.addProperty("installed", 0);
                d.addProperty("click", 0);
                d.addProperty("ctr", 0);
                d.addProperty("cpa", 0);
                d.addProperty("open_cpa", 0);
                d.addProperty("cvr", 0);
                d.addProperty("un_rate", -100000);
                d.addProperty("open_rate", -100000);
                if (admobCheck) {
                    d.addProperty("network", "admob");
                } else {
                    d.addProperty("network", "facebook");
                }
                array.add(d);
            }
        }

        jsonObject.add("array", array);
        jsonObject.addProperty("total_spend", total_spend);
        jsonObject.addProperty("total_installed", total_installed);
        jsonObject.addProperty("total_impressions", total_impressions);
        jsonObject.addProperty("total_click", total_click);
        jsonObject.addProperty("total_ctr", Utils.trimDouble(total_ctr,3));
        jsonObject.addProperty("total_cpa", Utils.trimDouble(total_cpa,3));
        jsonObject.addProperty("total_cvr", Utils.trimDouble(total_cvr,3));
        return jsonObject;
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
        public double un_rate;
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
        public double open_cpa;
        public double un_rate;
        public double open_rate;
        public double campaign_spends;
        public String network;
    }

}
