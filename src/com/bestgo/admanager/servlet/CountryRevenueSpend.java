package com.bestgo.admanager.servlet;

import com.bestgo.admanager.OperationResult;
import com.bestgo.admanager.utils.NumberUtil;
import com.bestgo.admanager.bean.CountryRecord;
import com.bestgo.admanager.utils.Utils;
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
 * 国家收支
 */
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
        String tagName = request.getParameter("tag_name");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        OperationResult result = new OperationResult();
        boolean flag = false;
        try {
            result.result = true;

            if (tagName.isEmpty()) {
                result.result = false;
                json.addProperty("message", "标签不能为空");
            }
            JsonArray jsonArray = new JsonArray();
            if(result.result){
                String sqlWT = "select id from web_tag where tag_name = '" + tagName + "'";
                JSObject idJS = DB.findOneBySql(sqlWT);
                long tag_id = idJS.get("id");

                String sqlCTRF = "select campaign_id from web_ad_campaign_tag_rel where tag_id = " + tag_id;
                List<JSObject> campaignIdFJSObjectList = DB.findListBySql(sqlCTRF);

                if(campaignIdFJSObjectList != null && campaignIdFJSObjectList.size()>0){
                    String campaignIdFListStr = Utils.getStrForListDistinctByAttrWithCommmas(campaignIdFJSObjectList,"campaign_id");
                    String sqlA = "select country_code, sum(total_spend) spend," +
                            "sum(total_installed) installed " +
                            "from web_ad_campaigns_country_history where date between '" + startTime + "' and '"
                            + endTime + "' and " +
                            " campaign_id in  (" + campaignIdFListStr + ") group by country_code";
                    List<JSObject> countryFacebookList = DB.findListBySql(sqlA);
                    if(countryFacebookList != null && countryFacebookList.size() >0){
                        for (JSObject cf : countryFacebookList) {
                            CountryRecord record = new CountryRecord();
                            String countryCode1 = cf.get("country_code");
                            record.country_code = countryCode1;
                            record.installed = NumberUtil.convertDouble(cf.get("installed"),0.0);
                            record.spend = NumberUtil.convertDouble(cf.get("spend"),0.0);
                            if(record.installed >0){
                                record.cpa = record.spend / record.installed;
                            }else{
                                record.cpa = 0.0;
                            }
                            map.put(countryCode1, record);
                        }
                        flag = true;
                    }
                }
                String sqlCTRA = "select campaign_id from web_ad_campaign_tag_admob_rel where tag_id = " + tag_id;
                List<JSObject> campaignIdAJSObjectList = DB.findListBySql(sqlCTRA);
                if(campaignIdAJSObjectList != null && campaignIdAJSObjectList.size()>0){
                    String campaignIdAListStr = Utils.getStrForListDistinctByAttrWithCommmas(campaignIdAJSObjectList,"campaign_id");

                    String sqlB = "select country_code, sum(total_spend) spend," +
                                "sum(total_installed) installed " +
                                "from web_ad_campaigns_country_history_admob where date between '" + startTime + "' and '"
                                + endTime + "' and " +
                                " campaign_id in  (" + campaignIdAListStr + ") group by country_code";
                    List<JSObject> countryAdwordsList = DB.findListBySql(sqlB);
                    if(campaignIdAJSObjectList != null && campaignIdAJSObjectList.size()>0){
                        for (JSObject ca : countryAdwordsList) {
                            String country_code2 = ca.get("country_code");
                            double spend2 = NumberUtil.convertDouble(ca.get("spend"), 0.0);
                            double installed2 = NumberUtil.convertDouble(ca.get("installed"), 0.0);
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
                        flag = true;
                    }

                }

                ArrayList<CountryRecord>  countryRecordList = new ArrayList<>();
                if(flag){
                    String sqlCountry = "select country_code,country_name from app_country_code_dict";
                    List<JSObject> countryList = DB.findListBySql(sqlCountry);
                    Map<String,String> countryMap = new HashMap<>();
                    for(JSObject j : countryList){
                        countryMap.put(j.get("country_code"),j.get("country_name"));
                    }

                    String sqlFAIR = "select google_package_id from web_facebook_app_ids_rel where tag_name = '" + tagName + "'";
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
                            record3.revenue = NumberUtil.convertDouble(r.get("total_revenue"),0.0);
                            //收益减去花费
                            record3.incoming = record3.revenue - record3.spend;
                            countryRecordList.add(record3);
                        }
                    }
                    if(countryRecordList != null && countryRecordList.size() > 0){
                        Collections.sort(countryRecordList, new Comparator<CountryRecord>() {
                            @Override
                            public int compare(CountryRecord a, CountryRecord b) {
                                if(a.incoming >= b.incoming){
                                    return 1;
                                }else{
                                    return -1;
                                }
                            }
                        });
                        for(CountryRecord ncr : countryRecordList){
                            JsonObject j = new JsonObject();
                            j.addProperty("country_code",ncr.country_code);
                            j.addProperty("revenue",ncr.revenue);
                            j.addProperty("spend",ncr.spend);
                            j.addProperty("incoming",ncr.incoming);

                            j.addProperty("installed",ncr.installed);
                            j.addProperty("cpa",ncr.cpa);
                            jsonArray.add(j);
                        }
                        json.addProperty("ret", 1);
                        json.addProperty("message", "执行成功");
                        json.add("arr", jsonArray);
                    }else{
                        flag = false;
                    }
                }
            }
            if(!flag){
                json.addProperty("ret", 0);
                json.addProperty("message", "没有数据!");
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