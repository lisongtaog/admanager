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

@WebServlet(name = "CountryRevenueSpend", urlPatterns = {"/country_revenue_spend"})
public class CountryRevenueSpend extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Utils.isAdmin(request, response)) return;

        JsonObject json = new JsonObject();
        Map<String, CountryRecord> map = new HashMap<>();
        String tag_name = request.getParameter("tag_name");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        try {
            String sqlWT = "select id from web_tag where tag_name = '" + tag_name + "' limit 1";
            JSObject idJS = DB.findOneBySql(sqlWT);
            long tag_id = idJS.get("id");

            String sqlCTRF = "select campaign_id from web_ad_campaign_tag_rel where tag_id = " + tag_id;
            List<JSObject> campaignIdFJSObjectList = DB.findListBySql(sqlCTRF);
            String campaignIdFListStr = "";
            for(JSObject j : campaignIdFJSObjectList){
                campaignIdFListStr += "'" +j.get("campaign_id") + "',";
            }
            campaignIdFListStr = campaignIdFListStr.substring(0,campaignIdFListStr.length() - 1);


            String sqlA = "select country_code, sum(total_spend) spend," +
                    "sum(total_installed) installed " +
                    "from web_ad_campaigns_country_history where date between '" + startTime + "' and '"
                     + endTime + "' and " +
                    " campaign_id in  (" + campaignIdFListStr + ") group by country_code";
            List<JSObject> countryFacebookList = DB.findListBySql(sqlA);

            for (JSObject cf : countryFacebookList) {
                CountryRecord record = new CountryRecord();
                String country_code1 = cf.get("country_code");
                record.country_code = country_code1;
                record.installed = Utils.convertDouble(cf.get("installed"),0.0);
                record.spend = Utils.convertDouble(cf.get("spend"),0.0);
                if(record.installed >0){
                    record.cpa = record.spend / record.installed;
                }else{
                    record.cpa = 0.0;
                }
                map.put(country_code1, record);
            }

            String sqlCTRA = "select campaign_id from web_ad_campaign_tag_admob_rel where tag_id = " + tag_id;
            List<JSObject> campaignIdAJSObjectList = DB.findListBySql(sqlCTRA);
            String campaignIdAListStr = "";
            for(JSObject j : campaignIdAJSObjectList){
                campaignIdAListStr += "'" + j.get("campaign_id") + "',";
            }
            campaignIdAListStr = campaignIdAListStr.substring(0,campaignIdAListStr.length() - 1);

            String sqlB = "select country_code, sum(total_spend) spend," +
                    "sum(total_installed) installed " +
                    "from web_ad_campaigns_country_history_admob where date between '" + startTime + "' and '"
                    + endTime + "' and " +
                    " campaign_id in  (" + campaignIdAListStr + ") group by country_code";
            List<JSObject> countryAdwordsList = DB.findListBySql(sqlB);
            for (JSObject ca : countryAdwordsList) {

                String country_code2 = ca.get("country_code");
                double spend2 = Utils.convertDouble(ca.get("spend"), 0.0);
                double installed2 = Utils.convertDouble(ca.get("installed"), 0.0);
                CountryRecord record2 = null;
                if (map.containsKey(country_code2)) {
                    record2 = map.get(country_code2);
                    record2.spend = record2.spend + spend2;
                    record2.installed = record2.installed + installed2;
                    if(record2.installed > 0){
                        record2.cpa = record2.spend / record2.installed;
                    }else{
                        record2.cpa = 0.0;
                    }

                } else {
                    record2 = new CountryRecord();
                    record2.country_code = country_code2;
                    record2.spend = spend2;
                    record2.installed = installed2;
                    if(record2.installed > 0){
                        record2.cpa = record2.spend / record2.installed;
                    }else{
                        record2.cpa = 0.0;
                    }
                }
                map.put(country_code2, record2);
            }

            String sqlCountry = "select country_code,country_name from app_country_code_dict";
            List<JSObject> countryList = DB.findListBySql(sqlCountry);
            Map<String,String> countryMap = new HashMap<>();
            for(JSObject j : countryList){
                countryMap.put(j.get("country_code"),j.get("country_name"));
            }

            String sqlFAIR = "select google_package_id from web_facebook_app_ids_rel where tag_name = '" + tag_name + "' limit 1";
            JSObject goolePackageIdJSObject = DB.findOneBySql(sqlFAIR);
            String google_package_id = goolePackageIdJSObject.get("google_package_id");

            String sqlR = "select country_code, sum(revenue) total_revenue" +
                    " from web_ad_daily_revenue_history" +
                    " where  app_id = '" + google_package_id + "' and create_time between '" + startTime +
                    "' and '" + endTime + "' group by country_code";
            List<JSObject> listR = DB.findListBySql(sqlR);
            Map<String, CountryRecord> newMap = new HashMap<>();
            for (JSObject r : listR) {
                String country_code3 = r.get("country_code");
                if (map.containsKey(country_code3)) {
                    CountryRecord record3 = map.get(country_code3);
                    record3.revenue = Utils.convertDouble(r.get("total_revenue"),0.0);
                    record3.country_name = countryMap.get(country_code3);
                    //如果赔钱，存入新的集合中
                    if(record3.spend > record3.revenue){
                        newMap.put(country_code3, record3);
                    }

                }
            }
            Collection<CountryRecord> newCountryRecords = newMap.values();

            JsonArray jsonArray = new JsonArray();
            if(newCountryRecords != null){
                for(CountryRecord ncr : newCountryRecords){
                    JsonObject j = new JsonObject();
                    j.addProperty("country_code",ncr.country_code);
                    j.addProperty("country_name",ncr.country_name);
                    j.addProperty("revenue",ncr.revenue);
                    j.addProperty("spend",ncr.spend);
                    j.addProperty("installed",ncr.installed);
                    j.addProperty("cpa",ncr.cpa);
                    jsonArray.add(j);
                }
            }
            json.add("arr", jsonArray);

            json.addProperty("ret", 1);
            json.addProperty("message", "执行成功");

        } catch (Exception ex) {
            json.addProperty("ret", 0);
            json.addProperty("message", ex.getMessage());
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        java.lang.System.out.println(json.toString());
        response.getWriter().write(json.toString());
    }

    class CountryRecord {
        public String country_code;
        public String country_name;
        public double installed;
        public double spend;
        public double cpa;
        public double revenue;
    }

}