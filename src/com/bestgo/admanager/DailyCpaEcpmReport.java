package com.bestgo.admanager;

import com.bestgo.admanager.utils.Utils;
import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jikai on 11/30/17.
 */
public class DailyCpaEcpmReport {
    public static class Item {
        public String appId;
        public String appName;
        public String countryCode;
        public String countryName;
        public Date date;
        public double spend;
        public long installed;
        public double revenue;
        public long impression;
        public double cpa;
        public double cpaTrend;
        public double ecpm;
        public double ecpmTrend;
    }

    public static List<Item> report(String date) {
        ArrayList<Item> list = new ArrayList<>();
        HashMap<String, Item> countryReportMap = new HashMap<>();
        HashMap<String ,String> countryMap = Utils.getCountryMap();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date formatDate = sdf.parse(date);

            String sql = "select tag_name, google_package_id from web_facebook_app_ids_rel";
            List<JSObject> appIds = DB.findListBySql(sql);
            HashMap<String, String> appMaps = new HashMap<>();
            for (JSObject one : appIds) {
                String tagName = one.get("tag_name");
                String appId = one.get("google_package_id");
                appMaps.put(tagName, appId);
            }
            sql = "select id, tag_name from web_tag";
            List<JSObject> tagIds = DB.findListBySql(sql);
            HashMap<String, Long> tagMaps = new HashMap<>();
            for (JSObject one : tagIds) {
                String tagName = one.get("tag_name");
                long tagId = one.get("id");
                tagMaps.put(tagName, tagId);
                String appId = appMaps.get(tagName);
                if (appId == null) {
                    continue;
                }

                sql = "select country_code, sum(total_spend) as total_spend, sum(total_installed) as total_installed from web_ad_campaigns_country_history " +
                        "where date=? and campaign_id in (" +
                        "select campaign_id from web_ad_campaign_tag_rel where tag_id=?) " +
                        "group by country_code;";
                List<JSObject> fbCountryList = DB.findListBySql(sql, date, tagId);
                for (JSObject two : fbCountryList) {
                    String countryCode = two.get("country_code");
                    double spend = Utils.convertDouble(two.get("total_spend"), 0);
                    long installed = Utils.convertLong(two.get("total_installed"), 0);
                    String key = appId + "_" + countryCode;
                    Item item = countryReportMap.get(key);
                    if (item == null) {
                        item = new Item();
                        item.appId = appId;
                        item.appName = tagName;
                        item.countryCode = countryCode;
                        item.countryName = countryMap.get(countryCode);
                        item.date = formatDate;
                        countryReportMap.put(key, item);
                    }
                    item.spend = spend;
                    item.installed = installed;
                    if (item.installed > 0) {
                        item.cpa = item.spend / item.installed;
                    }
                }
                sql = "select country_code, sum(total_spend) as total_spend, sum(total_installed) as total_installed from web_ad_campaigns_country_history_admob " +
                        "where date=? and campaign_id in (" +
                        "select campaign_id from web_ad_campaign_tag_admob_rel where tag_id=?) " +
                        "group by country_code;";
                List<JSObject> adwordsCountryList = DB.findListBySql(sql, date, tagId);
                for (JSObject two : adwordsCountryList) {
                    String countryCode = two.get("country_code");
                    double spend = Utils.convertDouble(two.get("total_spend"), 0);
                    long installed = Utils.convertLong(two.get("total_installed"), 0);
                    String key = appId + "_" + countryCode;
                    Item item = countryReportMap.get(key);
                    if (item == null) {
                        item = new Item();
                        item.appId = appId;
                        item.appName = tagName;
                        item.countryCode = countryCode;
                        item.countryName = countryMap.get(countryCode);
                        item.date = formatDate;
                        countryReportMap.put(key, item);
                    }
                    item.spend += spend;
                    item.installed += installed;
                    if (item.installed > 0) {
                        item.cpa = item.spend / item.installed;
                    }
                }

                sql = "select country_code, sum(revenue) as revenue, sum(impression) as impression from web_ad_daily_revenue_history " +
                        "where create_time=? and app_id=? " +
                        "group by country_code;";
                List<JSObject> revenueList = DB.findListBySql(sql, date, appId);
                for (JSObject two : revenueList) {
                    String countryCode = two.get("country_code");
                    double revenue = Utils.convertDouble(two.get("revenue"), 0);
                    long impression = Utils.convertLong(two.get("impression"), 0);
                    String key = appId + "_" + countryCode;
                    Item item = countryReportMap.get(key);
                    if (item == null) {
                        item = new Item();
                        item.appId = appId;
                        item.appName = tagName;
                        item.countryCode = countryCode;
                        item.countryName = countryMap.get(countryCode);
                        item.date = formatDate;
                        countryReportMap.put(key, item);
                    }
                    item.revenue = revenue;
                    item.impression = impression;
                    if (item.impression > 0) {
                        item.ecpm = item.revenue / item.impression * 1000;
                    }
                }
            }
            list = new ArrayList<>(countryReportMap.values());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
}
