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

@WebServlet(name = "CountryRevenueSpend", urlPatterns = {"/country_revenue_spend"}, asyncSupported = true)
public class CountryRevenueSpend extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Utils.isAdmin(request, response)) return;

        JsonObject json = new JsonObject();

        String tag_name = request.getParameter("tag_name");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        try {
            String sqlWT = "select id from web_tag where tag_name = '" + tag_name + "' limit 1";
            JSObject idJS = DB.findOneBySql(sqlWT);
            int tag_id = idJS.get("id");
            String sqlCTR = "select campaign_id from tag_id = '" + tag_id + "'";
            List<JSObject> campaignIdJSObjectList = DB.findListBySql(sqlCTR);
            String camaignIdListStr = "";
            for(JSObject j : campaignIdJSObjectList){
                camaignIdListStr += j.get("campaign_id") + ",";
            }
            camaignIdListStr = camaignIdListStr.substring(0,camaignIdListStr.length() - 1);

            Map<String, CountryRecord> map = new HashMap<>();
            String sqlA = "select country_code, sum(total_spend) spend," +
                    "sum(total_installed) installed, " +
                    "(case when installed > 0 then spend/installed else 0 end) as cpa " +
                    "from web_ad_campaign_country_history_admob" +
                    " where campaign_id in  (" + camaignIdListStr + ") and date between '" + startTime +
                    "' and '" + endTime + "' group by country_code";
            List<JSObject> countryFacebookList = DB.findListBySql(sqlA);

            for (JSObject cf : countryFacebookList) {
                CountryRecord record = new CountryRecord();
                String country_code1 = cf.get("country_code");
                record.country_code = country_code1;
                record.cpa = cf.get("cpa");
                record.installed = cf.get("installed");
                record.spend = cf.get("spend");
                map.put(country_code1, record);
            }

            String sqlF = "select country_code, sum(total_spend) spend," +
                    "sum(total_installed) installed, " +
                    "(case when installed > 0 then spend/installed else 0 end) as cpa " +
                    "from web_ad_campaign_country_history" +
                    " where campaign_id in  (" + camaignIdListStr + ") and date between '" + startTime +
                    "' and '" + endTime + "' group by country_code";
            List<JSObject> countryAdwordsList = DB.findListBySql(sqlF);
            for (JSObject ca : countryAdwordsList) {
                CountryRecord record2 = new CountryRecord();
                String country_code2 = ca.get("country_code");
                double spend = ca.get("spend");
                double installed = ca.get("installed");
                if (map.containsKey(country_code2)) {
                    record2.spend = map.get(country_code2).spend + spend;
                    record2.installed = map.get(country_code2).installed + installed;
                    record2.cpa = record2.spend / record2.installed;
                } else {
                    record2.country_code = country_code2;
                    record2.spend = spend;
                    record2.installed = installed;
                    record2.cpa = ca.get("cpa");
                }
                map.put(country_code2, record2);
            }

            String sqlFAIR = "select google_package_id from web_facebook_app_ids_rel where tag_name = '" + tag_name + "' limit 1";
            JSObject goolePackageIdJSObject = DB.findOneBySql(sqlFAIR);
            String google_package_id = goolePackageIdJSObject.get("google_package_id");

            String sqlR = "select country_code, sum(revenue) total_revenue" +
                    " from web_ad_daily_revenue_history" +
                    " where  app_id = '" + google_package_id + "' and create_time between '" + startTime +
                    "' and '" + endTime + "' group by country_code";
            List<JSObject> listR = DB.findListBySql(sqlR);
            for (JSObject r : listR) {
                String country_code3 = r.get("country_code");
                if (map.containsKey(country_code3)) {
                    CountryRecord record3 = map.get(country_code3);
                    record3.revenue = r.get("total_revenue");
                    map.put(country_code3, record3);
                }
            }
            Collection<CountryRecord> countryRecords = map.values();

            JsonArray jsonArray = new JsonArray();
            if(countryRecords != null){
                for(CountryRecord cr : countryRecords){
                    JsonObject j = new JsonObject();
                    j.addProperty("country_code",cr.country_code);
                    j.addProperty("spend",cr.spend);
                    j.addProperty("installed",cr.installed);
                    j.addProperty("revenue",cr.revenue);
                    j.addProperty("cpa",cr.cpa);
                    jsonArray.add(j);
                }
            }
            json.add("data", jsonArray);

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

    class CountryRecord {
        public String country_code;
        public double installed;
        public double spend;
        public double cpa;
        public double revenue;
    }

}