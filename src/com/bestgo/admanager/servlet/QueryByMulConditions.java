package com.bestgo.admanager.servlet;

import com.bestgo.admanager.bean.Campaigns;
import com.bestgo.admanager.bean.CountryRecord;
import com.bestgo.admanager.utils.DateUtil;
import com.bestgo.admanager.utils.NumberUtil;
import com.bestgo.admanager.utils.StringUtil;
import com.bestgo.admanager.utils.Utils;
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
        if (StringUtil.isNotEmpty(sorterId)) {
            sorter = NumberUtil.parseInt(sorterId, 0);
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
        String biddingOperator = request.getParameter("biddingOperator");

        String likeCampaignName = request.getParameter("likeCampaignName");
        HashMap<String, String> countryMap = Utils.getCountryCodeNameMap();

        try {
            JSObject tagObject = DB.simpleScan("web_tag")
                    .select("id", "tag_name")
                    .where(DB.filter().whereEqualTo("tag_name", tag)).execute();
            if (tagObject.hasObjectData()) {
                Long id = tagObject.get("id");
                JsonObject jsonObject = new JsonObject();
                if (StringUtil.isNotEmpty(countryCode)) {
                    countryCheck = "false";
                }

                JsonArray array = null;
                double total_spend = 0;
                double total_installed = 0;
                double total_impressions = 0;
                double total_click = 0;

                /**
                 * 各种状态计数
                 */
                int total_ARCHIVED = 0;
                int total_ACTIVE = 0;
                int total_PAUSED   = 0;

                int total_paused = 0;
                int total_removed = 0;
                int total_enabled = 0;

                //如果【Facebook】和【Adwords】都未选中，则要一起统计
                if ("false".equals(adwordsCheck) && "false".equals(facebookCheck)) {
                    JsonObject admob = fetchOneAppData(id, tag, startTime, endTime, true, "true".equals(countryCheck), countryCode,
                            likeCampaignName, campaignCreateTime, countryMap, totalInstallComparisonValue, "true".equals(containsNoDataCampaignCheck), countryCode,
                            cpaComparisonValue, biddingComparisonValue, biddingOperator, totalInstallOperator, cpaOperator);

                    JsonObject facebook = fetchOneAppData(id, tag, startTime, endTime, false, "true".equals(countryCheck), countryCode, likeCampaignName,
                            campaignCreateTime, countryMap, totalInstallComparisonValue, "true".equals(containsNoDataCampaignCheck), countryName,
                            cpaComparisonValue, biddingComparisonValue, biddingOperator, totalInstallOperator, cpaOperator);

                    /**
                     * 各种状态计数
                     */
                    total_ARCHIVED = admob.get("total_ARCHIVED").getAsInt() + facebook.get("total_ARCHIVED").getAsInt();
                    total_ACTIVE = admob.get("total_ACTIVE").getAsInt() + facebook.get("total_ACTIVE").getAsInt();
                    total_PAUSED = admob.get("total_PAUSED").getAsInt() + facebook.get("total_PAUSED").getAsInt();
                    total_paused = admob.get("total_paused").getAsInt() + facebook.get("total_paused").getAsInt();
                    total_removed = admob.get("total_removed").getAsInt() + facebook.get("total_removed").getAsInt();
                    total_enabled = admob.get("total_enabled").getAsInt() + facebook.get("total_enabled").getAsInt();


                    total_spend = admob.get("total_spend").getAsDouble() + facebook.get("total_spend").getAsDouble();
                    total_installed = admob.get("total_installed").getAsDouble() + facebook.get("total_installed").getAsDouble();
                    total_impressions = admob.get("total_impressions").getAsDouble() + facebook.get("total_impressions").getAsDouble();
                    total_click = admob.get("total_click").getAsDouble() + facebook.get("total_click").getAsDouble();

                    array = admob.getAsJsonArray("array");
                    JsonArray array1 = facebook.getAsJsonArray("array");
                    array.addAll(array1);

                } else if ("true".equals(adwordsCheck)) { //如果只选中【Adwords】
                    JsonObject admob = fetchOneAppData(id, tag, startTime, endTime, true, "true".equals(countryCheck), countryCode, likeCampaignName,
                            campaignCreateTime, countryMap, totalInstallComparisonValue, "true".equals(containsNoDataCampaignCheck), countryCode, cpaComparisonValue,
                            biddingComparisonValue, biddingOperator, totalInstallOperator, cpaOperator);

                    /**
                     * 各种状态计数
                     */
                    total_ARCHIVED = admob.get("total_ARCHIVED").getAsInt();
                    total_ACTIVE = admob.get("total_ACTIVE").getAsInt();
                    total_PAUSED = admob.get("total_PAUSED").getAsInt();
                    total_paused = admob.get("total_paused").getAsInt();
                    total_removed = admob.get("total_removed").getAsInt();
                    total_enabled = admob.get("total_enabled").getAsInt();

                    total_spend = admob.get("total_spend").getAsDouble();
                    total_installed = admob.get("total_installed").getAsDouble();
                    total_impressions = admob.get("total_impressions").getAsDouble();
                    total_click = admob.get("total_click").getAsDouble();
                    array = admob.getAsJsonArray("array");

                } else { //如果只选中【Facebook】
                    JsonObject facebook = fetchOneAppData(id, tag, startTime, endTime, false, "true".equals(countryCheck), countryCode, likeCampaignName,
                            campaignCreateTime, countryMap, totalInstallComparisonValue, "true".equals(containsNoDataCampaignCheck), countryName, cpaComparisonValue,
                            biddingComparisonValue, biddingOperator, totalInstallOperator, cpaOperator);

                    /**
                     * 各种状态计数
                     */
                    total_ARCHIVED = facebook.get("total_ARCHIVED").getAsInt();
                    total_ACTIVE = facebook.get("total_ACTIVE").getAsInt();
                    total_PAUSED = facebook.get("total_PAUSED").getAsInt();
                    total_paused = facebook.get("total_paused").getAsInt();
                    total_removed = facebook.get("total_removed").getAsInt();
                    total_enabled = facebook.get("total_enabled").getAsInt();

                    total_spend = facebook.get("total_spend").getAsDouble();
                    total_installed = facebook.get("total_installed").getAsDouble();
                    total_impressions = facebook.get("total_impressions").getAsDouble();
                    total_click = facebook.get("total_click").getAsDouble();
                    array = facebook.getAsJsonArray("array");
                }
                double total_ctr = total_impressions > 0 ? total_click / total_impressions : 0;
                double total_cpa = total_installed > 0 ? total_spend / total_installed : 0;
                double total_cvr = total_click > 0 ? total_installed / total_click : 0;
                Gson gson = new Gson();

                //如果【细分到国家】未选中
                if ("false".equals(countryCheck)) {
                    if (sorter > 0) {
                        List<Campaigns> campaignsList = gson.fromJson(array, new TypeToken<List<Campaigns>>() {
                        }.getType());
                        switch (sorter) {
                            case 1:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if (a.create_time.compareTo(b.create_time) > 0) {
                                            return 1;
                                        } else if (a.create_time.compareTo(b.create_time) < 0) {
                                            return -1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1001:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if (a.create_time.compareTo(b.create_time) > 0) {
                                            return -1;
                                        } else if (a.create_time.compareTo(b.create_time) < 0) {
                                            return 1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 2:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if (a.status.compareTo(b.status) > 0) {
                                            return 1;
                                        } else if (a.status.compareTo(b.status) < 0) {
                                            return -1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1002:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if (a.status.compareTo(b.status) > 0) {
                                            return -1;
                                        } else if (a.status.compareTo(b.status) < 0) {
                                            return 1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 3:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if (a.budget > b.budget) {
                                            return 1;
                                        } else if (a.budget < b.budget) {
                                            return -1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1003:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if (a.budget > b.budget) {
                                            return -1;
                                        } else if (a.budget < b.budget) {
                                            return 1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 4:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if (a.bidding > b.bidding) {
                                            return 1;
                                        } else if (a.bidding < b.bidding) {
                                            return -1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1004:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if (a.bidding > b.bidding) {
                                            return -1;
                                        } else if (a.bidding < b.bidding) {
                                            return 1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 5:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if (a.spend > b.spend) {
                                            return 1;
                                        } else if (a.spend < b.spend) {
                                            return -1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1005:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if (a.spend > b.spend) {
                                            return -1;
                                        } else if (a.spend < b.spend) {
                                            return 1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 6:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if (a.installed > b.installed) {
                                            return 1;
                                        } else if (a.installed < b.installed) {
                                            return -1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1006:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if (a.installed > b.installed) {
                                            return -1;
                                        } else if (a.installed < b.installed) {
                                            return 1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 7:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if (a.click > b.click) {
                                            return 1;
                                        } else if (a.click < b.click) {
                                            return -1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1007:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if (a.click > b.click) {
                                            return -1;
                                        } else if (a.click < b.click) {
                                            return 1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 8:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if (a.cpa > b.cpa) {
                                            return 1;
                                        } else if (a.cpa < b.cpa) {
                                            return -1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1008:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if (a.cpa > b.cpa) {
                                            return -1;
                                        } else if (a.cpa < b.cpa) {
                                            return 1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 9:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if (a.ctr > b.ctr) {
                                            return 1;
                                        } else if (a.ctr < b.ctr) {
                                            return -1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1009:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if (a.ctr > b.ctr) {
                                            return -1;
                                        } else if (a.ctr < b.ctr) {
                                            return 1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 10:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if (a.cvr > b.cvr) {
                                            return 1;
                                        } else if (a.cvr < b.cvr) {
                                            return -1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1010:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if (a.cvr > b.cvr) {
                                            return -1;
                                        } else if (a.cvr < b.cvr) {
                                            return 1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 11:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if (a.un_rate > b.un_rate) {
                                            return 1;
                                        } else if (a.un_rate < b.un_rate) {
                                            return -1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1011:
                                Collections.sort(campaignsList, new Comparator<Campaigns>() {
                                    @Override
                                    public int compare(Campaigns a, Campaigns b) {
                                        if (a.un_rate > b.un_rate) {
                                            return -1;
                                        } else if (a.un_rate < b.un_rate) {
                                            return 1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                        }
                        JsonArray jsonArray = new JsonArray();

                        for (Campaigns c : campaignsList) {
                            JsonObject j = new JsonObject();
                            j.addProperty("campaign_id", c.campaign_id);
                            j.addProperty("short_name", c.short_name);
                            j.addProperty("account_id", c.account_id);
                            j.addProperty("campaign_name", c.campaign_name);
                            j.addProperty("status", c.status);
                            j.addProperty("create_time", c.create_time);
                            j.addProperty("country_code", c.country_code);
                            j.addProperty("budget", c.budget);
                            j.addProperty("bidding", c.bidding);
                            j.addProperty("impressions", c.impressions);
                            j.addProperty("installed", c.installed);
                            j.addProperty("click", c.click);
                            j.addProperty("spend", c.spend);
                            j.addProperty("ctr", c.ctr);
                            j.addProperty("cpa", c.cpa);
                            j.addProperty("cvr", c.cvr);
                            j.addProperty("un_rate", c.un_rate);
                            j.addProperty("campaign_spends", c.campaign_spends);
                            j.addProperty("network", c.network);
                            jsonArray.add(j);
                        }
                        jsonObject.add("array", jsonArray);
                    } else {
                        jsonObject.add("array", array);
                    }

                } else { //如果【细分到国家】被选中
                    HashMap<String, CountryRecord> dataSets = new HashMap<>();
                    for (int i = 0, len = array.size(); i < len; i++) {
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
                    if (sorter > 0) {
                        List<CountryRecord> countryRecordList = new ArrayList<>();
                        for (String key : dataSets.keySet()) {
                            CountryRecord record = dataSets.get(key);
                            record.country_name = key;
                            record.ctr = record.impressions > 0 ? record.click / record.impressions : 0;
                            record.cpa = record.installed > 0 ? record.spend / record.installed : 0;
                            record.cvr = record.click > 0 ? record.installed / record.click : 0;
                            countryRecordList.add(record);
                        }
                        switch (sorter) {
                            case 21:
                                Collections.sort(countryRecordList, new Comparator<CountryRecord>() {
                                    @Override
                                    public int compare(CountryRecord a, CountryRecord b) {
                                        if (a.impressions > b.impressions) {
                                            return 1;
                                        } else if (a.impressions < b.impressions) {
                                            return -1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1021:
                                Collections.sort(countryRecordList, new Comparator<CountryRecord>() {
                                    @Override
                                    public int compare(CountryRecord a, CountryRecord b) {
                                        if (a.impressions > b.impressions) {
                                            return -1;
                                        } else if (a.impressions < b.impressions) {
                                            return 1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 22:
                                Collections.sort(countryRecordList, new Comparator<CountryRecord>() {
                                    @Override
                                    public int compare(CountryRecord a, CountryRecord b) {
                                        if (a.spend > b.spend) {
                                            return 1;
                                        } else if (a.spend < b.spend) {
                                            return -1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1022:
                                Collections.sort(countryRecordList, new Comparator<CountryRecord>() {
                                    @Override
                                    public int compare(CountryRecord a, CountryRecord b) {
                                        if (a.spend > b.spend) {
                                            return -1;
                                        } else if (a.spend < b.spend) {
                                            return 1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 23:
                                Collections.sort(countryRecordList, new Comparator<CountryRecord>() {
                                    @Override
                                    public int compare(CountryRecord a, CountryRecord b) {
                                        if (a.installed > b.installed) {
                                            return 1;
                                        } else if (a.installed < b.installed) {
                                            return -1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1023:
                                Collections.sort(countryRecordList, new Comparator<CountryRecord>() {
                                    @Override
                                    public int compare(CountryRecord a, CountryRecord b) {
                                        if (a.installed > b.installed) {
                                            return -1;
                                        } else if (a.installed < b.installed) {
                                            return 1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 24:
                                Collections.sort(countryRecordList, new Comparator<CountryRecord>() {
                                    @Override
                                    public int compare(CountryRecord a, CountryRecord b) {
                                        if (a.click > b.click) {
                                            return 1;
                                        } else if (a.click < b.click) {
                                            return -1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1024:
                                Collections.sort(countryRecordList, new Comparator<CountryRecord>() {
                                    @Override
                                    public int compare(CountryRecord a, CountryRecord b) {
                                        if (a.click > b.click) {
                                            return -1;
                                        } else if (a.click < b.click) {
                                            return 1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 25:
                                Collections.sort(countryRecordList, new Comparator<CountryRecord>() {
                                    @Override
                                    public int compare(CountryRecord a, CountryRecord b) {
                                        if (a.cpa > b.cpa) {
                                            return 1;
                                        } else if (a.cpa < b.cpa) {
                                            return -1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1025:
                                Collections.sort(countryRecordList, new Comparator<CountryRecord>() {
                                    @Override
                                    public int compare(CountryRecord a, CountryRecord b) {
                                        if (a.cpa > b.cpa) {
                                            return -1;
                                        } else if (a.cpa < b.cpa) {
                                            return 1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 26:
                                Collections.sort(countryRecordList, new Comparator<CountryRecord>() {
                                    @Override
                                    public int compare(CountryRecord a, CountryRecord b) {
                                        if (a.ctr > b.ctr) {
                                            return 1;
                                        } else if (a.ctr < b.ctr) {
                                            return -1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1026:
                                Collections.sort(countryRecordList, new Comparator<CountryRecord>() {
                                    @Override
                                    public int compare(CountryRecord a, CountryRecord b) {
                                        if (a.ctr > b.ctr) {
                                            return -1;
                                        } else if (a.ctr < b.ctr) {
                                            return 1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 27:
                                Collections.sort(countryRecordList, new Comparator<CountryRecord>() {
                                    @Override
                                    public int compare(CountryRecord a, CountryRecord b) {
                                        if (a.cvr > b.cvr) {
                                            return 1;
                                        } else if (a.cvr < b.cvr) {
                                            return -1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1027:
                                Collections.sort(countryRecordList, new Comparator<CountryRecord>() {
                                    @Override
                                    public int compare(CountryRecord a, CountryRecord b) {
                                        if (a.cvr > b.cvr) {
                                            return -1;
                                        } else if (a.cvr < b.cvr) {
                                            return 1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 28:
                                Collections.sort(countryRecordList, new Comparator<CountryRecord>() {
                                    @Override
                                    public int compare(CountryRecord a, CountryRecord b) {
                                        if (a.un_rate > b.un_rate) {
                                            return 1;
                                        } else if (a.un_rate < b.un_rate) {
                                            return -1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                            case 1028:
                                Collections.sort(countryRecordList, new Comparator<CountryRecord>() {
                                    @Override
                                    public int compare(CountryRecord a, CountryRecord b) {
                                        if (a.un_rate > b.un_rate) {
                                            return -1;
                                        } else if (a.un_rate < b.un_rate) {
                                            return 1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });
                                break;
                        }
                        for (CountryRecord record : countryRecordList) {
                            JsonObject one = new JsonObject();
                            one.addProperty("country_name", record.country_name);
                            one.addProperty("impressions", record.impressions);
                            one.addProperty("installed", record.installed);
                            one.addProperty("click", record.click);
                            one.addProperty("spend", NumberUtil.trimDouble(record.spend, 2));
                            one.addProperty("ctr", NumberUtil.trimDouble(record.ctr, 3));
                            one.addProperty("cpa", NumberUtil.trimDouble(record.cpa, 3));
                            one.addProperty("cvr", NumberUtil.trimDouble(record.cvr, 3));
                            one.addProperty("un_rate", NumberUtil.trimDouble(record.un_rate, 3));
                            newArr.add(one);
                        }
                    } else {
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
                            one.addProperty("spend", NumberUtil.trimDouble(record.spend, 2));
                            one.addProperty("ctr", NumberUtil.trimDouble(record.ctr, 3));
                            one.addProperty("cpa", NumberUtil.trimDouble(record.cpa, 3));
                            one.addProperty("cvr", NumberUtil.trimDouble(record.cvr, 3));
                            one.addProperty("un_rate", NumberUtil.trimDouble(record.un_rate, 3));
                            newArr.add(one);
                        }
                    }

                    jsonObject.add("array", newArr);
                }
                /**
                 *  各种状态计数
                 */
                jsonObject.addProperty("total_ARCHIVED", total_ARCHIVED);
                jsonObject.addProperty("total_ACTIVE", total_ACTIVE);
                jsonObject.addProperty("total_PAUSED", total_PAUSED);
                jsonObject.addProperty("total_paused", total_paused);
                jsonObject.addProperty("total_removed", total_removed);
                jsonObject.addProperty("total_enabled", total_enabled);

                jsonObject.addProperty("total_spend", NumberUtil.trimDouble(total_spend, 2));
                jsonObject.addProperty("total_installed", total_installed);
                jsonObject.addProperty("total_impressions", total_impressions);



                jsonObject.addProperty("total_click", total_click);
                jsonObject.addProperty("total_ctr", NumberUtil.trimDouble(total_ctr, 3));
                jsonObject.addProperty("total_cpa", NumberUtil.trimDouble(total_cpa, 3));
                jsonObject.addProperty("total_cvr", NumberUtil.trimDouble(total_cvr, 3));
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


    /**
     * 统计Adwords或Facebook其中一个的数据
     *
     * @param tagId                       标签ID
     * @param tagName                     标签名称
     * @param startTime                   开始日期
     * @param endTime                     结束日期
     * @param admobCheck                  true,表示只计算Adwords的；false，表示只计算Facebook的
     * @param countryCheck                【细分到国家】的按钮是否被选中
     * @param countryCode                 页面传参进来的国家代号
     * @param likeCampaignName            模糊查询的系列名称
     * @param campaignCreateTime          系列创建日期
     * @param countryMap                  国家名称与代号的Map
     * @param totalInstallComparisonValue 总安装比较值
     * @param containsNoDataCampaignCheck 【查询无数据的系列】按钮是否被选中
     * @param country                     页面传参进来的国家代号或国家名称；如果是Adwords，则内部传入是countryCode;如果是Facebook，则内部传入countryName
     * @param cpaComparisonValue          cpa比较值
     * @param biddingComparisonValue      竞价比较值
     * @param biddingOperator             竞价比较符号
     * @param totalInstallOperator        总安装的条件符号
     * @param cpaOperator                 cpa的条件符号
     * @return
     * @throws Exception
     */
    private JsonObject fetchOneAppData(long tagId, String tagName, String startTime, String endTime, boolean admobCheck, boolean countryCheck,
                                       String countryCode, String likeCampaignName, String campaignCreateTime, HashMap<String, String> countryMap,
                                       String totalInstallComparisonValue, boolean containsNoDataCampaignCheck, String country, String cpaComparisonValue,
                                       String biddingComparisonValue, String biddingOperator, String totalInstallOperator, String cpaOperator)
            throws Exception {
        String afterCampaignCreateTime = "";
        if (StringUtil.isNotEmpty(campaignCreateTime)) {
            afterCampaignCreateTime = DateUtil.addDay(campaignCreateTime, 1, "yyyy-MM-dd"); //系列创建日期的第二天
        }
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
            webAdCampaignsTable = "web_ad_campaigns_admob";
            webAdCampaignsHistoryTable = "web_ad_campaigns_history_admob";
            webAccountIdTable = "web_account_id_admob";
            webAdCampaignsCountryHistoryTable = "web_ad_campaigns_country_history_admob";
            openStatus = "enabled";
        }
        List<JSObject> list = null;

        List<JSObject> listCampaignSpend4CountryCode = new ArrayList<>();
        Map<String, JSObject> countryCampaignspendMap = new HashMap<>();
        String havingField = "";
        if (totalInstallComparisonValue.isEmpty() && cpaComparisonValue.isEmpty() && biddingComparisonValue.isEmpty()) {
            havingField = " having impressions > 0 ";
        } else {
            if (!totalInstallComparisonValue.isEmpty() && cpaComparisonValue.isEmpty() && biddingComparisonValue.isEmpty()) {
                havingField = " having installed " + totalInstallOperator + " " + totalInstallComparisonValue;
            } else if (totalInstallComparisonValue.isEmpty() && !cpaComparisonValue.isEmpty() && biddingComparisonValue.isEmpty()) {
                havingField = " having cpa " + cpaOperator + " " + cpaComparisonValue;
            } else if (totalInstallComparisonValue.isEmpty() && cpaComparisonValue.isEmpty() && !biddingComparisonValue.isEmpty()) {
                havingField = " having bidding " + biddingOperator + biddingComparisonValue;
            } else if (!totalInstallComparisonValue.isEmpty() && !cpaComparisonValue.isEmpty() && biddingComparisonValue.isEmpty()) {
                havingField = " having installed " + totalInstallOperator + " " + totalInstallComparisonValue +
                        " and cpa " + cpaOperator + " " + cpaComparisonValue;
            } else if (!totalInstallComparisonValue.isEmpty() && cpaComparisonValue.isEmpty() && !biddingComparisonValue.isEmpty()) {
                havingField = " having installed " + totalInstallOperator + " " + totalInstallComparisonValue +
                        " and bidding " + biddingOperator + biddingComparisonValue;
            } else if (totalInstallComparisonValue.isEmpty() && !cpaComparisonValue.isEmpty() && !biddingComparisonValue.isEmpty()) {
                havingField = " having cpa " + cpaOperator + " " + cpaComparisonValue +
                        " and bidding " + biddingOperator + biddingComparisonValue;
            } else {
                havingField = " having installed " + totalInstallOperator + " " + totalInstallComparisonValue + " and cpa " + cpaOperator + " " +
                        cpaComparisonValue + " and bidding " + " " + biddingOperator + biddingComparisonValue;
            }
        }

        String sql = "";
        if (StringUtil.isNotEmpty(countryCode)) {
            sql = "select ch.campaign_id, sum(ch.total_spend) as campaign_spends " +
                    " from " + webAdCampaignsTable + " c, " + webAdCampaignsCountryHistoryTable + " ch " +
                    " where c.campaign_id = ch.campaign_id and c.tag_id = " + tagId +
                    " and date between '" + startTime + "' and '" + endTime + "' " +
                    (StringUtil.isNotEmpty(campaignCreateTime) ? " AND create_time >= '" + campaignCreateTime + "' AND create_time < '" + afterCampaignCreateTime + "' " : "") +
                    (likeCampaignName.isEmpty() ? " " : " and campaign_name like '%" + likeCampaignName + "%' ") +
                    " group by ch.campaign_id";
            listCampaignSpend4CountryCode = DB.findListBySql(sql);

            for (JSObject j : listCampaignSpend4CountryCode) {
                countryCampaignspendMap.put(j.get("campaign_id"), j);
            }
            sql = "select campaign_id, a.account_id,short_name, campaign_name, a.status, create_time, budget, bidding, spend, installed, impressions, click,cpa" +
                    " from (" +
                    "select ch.campaign_id, account_id, campaign_name,c.status, create_time, c.budget, c.bidding, sum(ch.total_spend) as spend, " +
                    "sum(ch.total_installed) as installed, sum(ch.total_impressions) as impressions,sum(ch.total_click) as click, " +
                    "(case when sum(ch.total_installed) > 0 then sum(ch.total_spend) / sum(ch.total_installed) else 0 end) as cpa " +
                    " from " + webAdCampaignsTable + " c, " + webAdCampaignsCountryHistoryTable + " ch " +
                    "where c.campaign_id=ch.campaign_id  and c.tag_id = " + tagId + " and country_code= '" + countryCode + "' " +
                    (StringUtil.isNotEmpty(campaignCreateTime) ? " AND create_time >= '" + campaignCreateTime + "' AND create_time < '" + afterCampaignCreateTime + "' " : "") +
                    (likeCampaignName.isEmpty() ? " " : " and campaign_name like '%" + likeCampaignName + "%' ") +
                    " and date between '" + startTime + "' and '" + endTime + "' " +
                    " group by ch.campaign_id " + havingField +
                    ") a left join " + webAccountIdTable + " b on a.account_id = b.account_id";
            list = DB.findListBySql(sql);
            if (containsNoDataCampaignCheck) {
                if (admobCheck) {
                    sql = "SELECT c.campaign_id, c.account_id, short_name, c.campaign_name, c.create_time, budget, c.bidding, c.total_spend " +
                            " FROM " + adCampaignsTable + " a, " + webAdCampaignsTable + " c, " + webAccountIdTable + " b WHERE a.campaign_id = c.campaign_id " +
                            " AND c.account_id = b.account_id AND c.status = '" + openStatus + "' AND a.country_region = '" + country + "' AND app_name = '" + tagName + "' " +
                            (StringUtil.isNotEmpty(campaignCreateTime) ? " AND c.create_time >= '" + campaignCreateTime + "' AND c.create_time < '" + afterCampaignCreateTime + "' " : "") +
                            (likeCampaignName.isEmpty() ? " " : " and c.campaign_name like '%" + likeCampaignName + "%' ");
                } else {
                    sql = "SELECT c.campaign_id, c.account_id, short_name, c.campaign_name, c.create_time, budget, c.bidding, c.total_spend " +
                            " FROM " + adCampaignsTable + " a, " + webAdCampaignsTable + " c, " + webAccountIdTable + " b WHERE a.campaign_id = c.campaign_id " +
                            " AND c.account_id = b.account_id AND b.status = 1 " +
                            " AND c.status = '" + openStatus + "' AND a.country_region = '" + country + "' AND app_name = '" + tagName + "' " +
                            (StringUtil.isNotEmpty(campaignCreateTime) ? " AND c.create_time >= '" + campaignCreateTime + "' AND c.create_time < '" + afterCampaignCreateTime + "' " : "") +
                            (likeCampaignName.isEmpty() ? " " : " and c.campaign_name like '%" + likeCampaignName + "%' ");
                }

                listAll = DB.findListBySql(sql);
                sql = "select ch.campaign_id from " + webAdCampaignsHistoryTable + " ch," + webAdCampaignsTable + " c " +
                        " where c.campaign_id = ch.campaign_id and c.tag_id = " + tagId + " and date between '" + startTime + "' and '" + endTime + "'";
                List<JSObject> dataList = DB.findListBySql(sql);
                if (dataList != null && dataList.size() > 0) {
                    listNoData = Utils.getDiffJSObjectList(listAll, dataList, "campaign_id");
                } else {
                    listNoData = listAll;
                }
            }
        } else if (countryCheck) {//细分到国家
            sql = "select campaign_id, country_code, a.account_id,short_name, campaign_name, a.status, create_time, budget, bidding, spend, installed, impressions, click,cpa" +
                    " from (" +
                    "select ch.campaign_id, country_code, account_id, campaign_name,c.status, create_time, c.budget, c.bidding, sum(ch.total_spend) as spend, " +
                    "sum(ch.total_installed) as installed, sum(ch.total_impressions) as impressions, " +
                    "(case when sum(ch.total_installed) > 0 then sum(ch.total_spend) / sum(ch.total_installed) else 0 end) as cpa, " +
                    " sum(ch.total_click) as click from " + webAdCampaignsTable + " c, " + webAdCampaignsCountryHistoryTable + " ch " +
                    "where c.campaign_id = ch.campaign_id and c.tag_id = " + tagId +
                    " and date between '" + startTime + "' and '" + endTime + "' " +
                    (likeCampaignName.isEmpty() ? " " : " and campaign_name like '%" + likeCampaignName + "%' ") +
                    " group by ch.campaign_id, country_code " + havingField +
                    ") a left join " + webAccountIdTable + " b on a.account_id = b.account_id";
            list = DB.findListBySql(sql);
        } else {
            sql = "select campaign_id, a.account_id,short_name, campaign_name, a.status, create_time, budget, bidding, spend, installed, impressions, click,cpa" +
                    " from (" +
                    "select ch.campaign_id, account_id, campaign_name,c.status, create_time, c.budget, c.bidding, sum(ch.total_spend) as spend, " +
                    "sum(ch.total_installed) as installed, sum(ch.total_impressions) as impressions, " +
                    "(case when sum(ch.total_installed) > 0 then sum(ch.total_spend) / sum(ch.total_installed) else 0 end) as cpa, " +
                    " sum(ch.total_click) as click from " + webAdCampaignsTable + " c, " + webAdCampaignsHistoryTable + " ch " +
                    "where c.campaign_id = ch.campaign_id and c.tag_id = " + tagId +
                    " and date between '" + startTime + "' and '" + endTime + "' " +
                    (StringUtil.isNotEmpty(campaignCreateTime) ? " AND c.create_time >= '" + campaignCreateTime + "' AND c.create_time < '" + afterCampaignCreateTime + "' " : "") +
                    (likeCampaignName.isEmpty() ? " " : " and campaign_name like '%" + likeCampaignName + "%' ") +
                    " group by ch.campaign_id " + havingField +
                    ") a left join " + webAccountIdTable + " b on a.account_id = b.account_id";
            list = DB.findListBySql(sql);
            if (containsNoDataCampaignCheck) {
                if (admobCheck) {
                    sql = "select c.campaign_id, c.account_id, short_name, c.campaign_name, create_time, c.status, budget, bidding, c.total_spend " +
                            "  from " + webAdCampaignsTable + " c," + webAccountIdTable + " b " +
                            " where c.account_id = b.account_id and c.tag_id = " + tagId + " and c.status = '" + openStatus + "'" +
                            (likeCampaignName.isEmpty() ? " " : " and campaign_name like '%" + likeCampaignName + "%' ");
                } else {
                    sql = "select c.campaign_id, c.account_id, short_name, c.campaign_name, create_time, c.status, budget, bidding, c.total_spend " +
                            "  from " + webAdCampaignsTable + " c, " + webAccountIdTable + " b " +
                            "where c.account_id = b.account_id and c.tag_id = " + tagId + " and c.status = '" + openStatus + "' AND b.status = 1 " +
                            (likeCampaignName.isEmpty() ? " " : " and campaign_name like '%" + likeCampaignName + "%' ");
                }

                listAll = DB.findListBySql(sql);
                sql = "select ch.campaign_id from " + webAdCampaignsHistoryTable + " ch," + webAdCampaignsTable + " c " +
                        " where c.campaign_id = ch.campaign_id and c.tag_id = " + tagId + " and date between '" + startTime + "' and '" + endTime + "'";
                List<JSObject> dataList = DB.findListBySql(sql);
                if (dataList != null && dataList.size() > 0) {
                    listNoData = Utils.getDiffJSObjectList(listAll, dataList, "campaign_id");
                } else {
                    listNoData = listAll;
                }
            }
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

        /**
         * 各种状态计数
         */
        int total_ARCHIVED = 0;
        int total_ACTIVE = 0;
        int total_PAUSED   = 0;

        int total_paused = 0;
        int total_removed = 0;
        int total_enabled = 0;

        if (list != null && list.size() > 0) {
            for (JSObject one : list) {
                JsonObject d = new JsonObject();
                double bidding = one.get("bidding");
                double installed = NumberUtil.convertDouble(one.get("installed"), 0);
                String campaignId = one.get("campaign_id");
                double spend = NumberUtil.convertDouble(one.get("spend"), 0);
                double cpa = NumberUtil.convertDouble(one.get("cpa"), 0);

                //目前只有Adwords能收集到unRate和openRate
//                if(admobCheck){
//                    String sqlQuery = "SELECT un_rate,open_rate FROM web_ad_campaign_un_rate_open_rate_admob " +
//                            "WHERE campaign_id = '" + campaignId + "' AND date = '" + beforeThreeDays + "'";
//                    JSObject oneQ = DB.findOneBySql(sqlQuery);
//                    if(oneQ.hasObjectData()){
//
//                        //系列卸载率 = 系列卸载数量 / 系列安装数量
//                        double unRate = Utils.convertDouble(oneQ.get("un_rate"),0);
//                        d.addProperty("un_rate", Utils.trimDouble(unRate,3));
//
//                        //系列开启率 = 系列安装数量 / 系列总安装
//                        double openRate = Utils.convertDouble(oneQ.get("open_rate"),0);
//                        d.addProperty("open_rate", Utils.trimDouble(openRate,3));
//                    }
//                }

                String short_name = one.get("short_name");
                String account_id = one.get("account_id");
                String campaign_name = one.get("campaign_name");
                String status = one.get("status");
                String create_time = one.get("create_time").toString();
                create_time = create_time.substring(0, create_time.length() - 5);
                String country_code = one.get("country_code");
                double budget = one.get("budget");

                double impressions = NumberUtil.convertDouble(one.get("impressions"), 0);
                double click = NumberUtil.convertDouble(one.get("click"), 0);
                double ctr = impressions > 0 ? click / impressions : 0;


                double cvr = click > 0 ? installed / click : 0;

                JSObject js = countryCampaignspendMap.get(campaignId);
                double campaign_spends = 0;
                if (js != null && js.hasObjectData()) {
                    campaign_spends = NumberUtil.convertDouble(js.get("campaign_spends"), 0);
                }
                total_spend += spend;
                total_installed += installed;
                total_impressions += impressions;
                total_click += click;
                /**
                 *  各种状态计数
                 */
                if ("ARCHIVED".equals(status)) {
                    total_ARCHIVED++;
                } else if ("PAUSED".equals(status)) {
                    total_PAUSED++;
                } else if ("ACTIVE".equals(status)) {
                    total_ACTIVE++;
                } else if ("paused".equals(status)) {
                    total_paused++;
                }else if ("removed".equals(status)) {
                    total_removed++;
                }else if ("enabled".equals(status)) {
                    total_enabled++;
                }


                total_ctr = total_impressions > 0 ? total_click / total_impressions : 0;
                total_cpa = total_installed > 0 ? total_spend / total_installed : 0;
                total_cvr = total_click > 0 ? total_installed / total_click : 0;

                //针对系列的ECPM
                double ecpm = impressions == 0 ? 0 : spend * 1000 / impressions;

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
                d.addProperty("spend", NumberUtil.trimDouble(spend, 2));
                d.addProperty("campaign_spends", campaign_spends);
                d.addProperty("installed", installed);
                d.addProperty("click", click);
                d.addProperty("ctr", NumberUtil.trimDouble(ctr, 3));
                d.addProperty("cpa", NumberUtil.trimDouble(cpa, 3));
                d.addProperty("cvr", NumberUtil.trimDouble(cvr, 3));
                d.addProperty("ecpm", NumberUtil.trimDouble(ecpm, 3));
                d.addProperty("ctr_mul_cvr", NumberUtil.trimDouble(ctr * cvr, 3));


                if (admobCheck) {
                    d.addProperty("network", "admob");
                } else {
                    d.addProperty("network", "facebook");
                }
                array.add(d);
            }
        }

        if (listNoData != null && listNoData.size() > 0) {
            for (JSObject one : listNoData) {
                double bidding = one.get("bidding");
                if (StringUtil.isNotEmpty(biddingComparisonValue)) {
                    double v = Double.parseDouble(biddingComparisonValue);
                    if (bidding != v) {
                        continue;
                    }
                }
                String campaign_id = one.get("campaign_id");
                String short_name = one.get("short_name");
                String account_id = one.get("account_id");
                String campaign_name = one.get("campaign_name");
                String create_time = one.get("create_time").toString();
                create_time = create_time.substring(0, create_time.length() - 5);
                String country_code = one.get("country_code");
                double budget = one.get("budget");
                double spend = NumberUtil.convertDouble(one.get("spend"), 0);

                JSObject js = countryCampaignspendMap.get(campaign_id);
                double campaign_spends = 0;
                if (js != null && js.hasObjectData()) {
                    campaign_spends = NumberUtil.convertDouble(js.get("campaign_spends"), 0);
                }

                total_spend += spend;


                JsonObject d = new JsonObject();
                d.addProperty("campaign_id", campaign_id);
                d.addProperty("short_name", short_name);
                d.addProperty("account_id", account_id);
                d.addProperty("campaign_name", campaign_name);
                d.addProperty("status", openStatus);
                d.addProperty("create_time", create_time);
                d.addProperty("country_code", country_code);
                d.addProperty("country_name", countryMap.get(country_code));
                d.addProperty("budget", budget);
                d.addProperty("bidding", bidding);
                d.addProperty("impressions", 0);
                d.addProperty("spend", NumberUtil.trimDouble(spend, 2));
                d.addProperty("campaign_spends", campaign_spends);
                d.addProperty("installed", 0);
                d.addProperty("click", 0);
                d.addProperty("ctr", 0);
                d.addProperty("cpa", 0);
                d.addProperty("cvr", 0);
                d.addProperty("ctr_mul_cvr", 0);
                d.addProperty("un_rate", -100000);
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

        /**
         *  各种状态计数
         */
        jsonObject.addProperty("total_ARCHIVED", total_ARCHIVED);
        jsonObject.addProperty("total_ACTIVE", total_ACTIVE);
        jsonObject.addProperty("total_PAUSED", total_PAUSED);
        jsonObject.addProperty("total_paused", total_paused);
        jsonObject.addProperty("total_removed", total_removed);
        jsonObject.addProperty("total_enabled", total_enabled);



        jsonObject.addProperty("total_ctr", NumberUtil.trimDouble(total_ctr, 3));
        jsonObject.addProperty("total_cpa", NumberUtil.trimDouble(total_cpa, 3));
        jsonObject.addProperty("total_cvr", NumberUtil.trimDouble(total_cvr, 3));
        return jsonObject;
    }

}
