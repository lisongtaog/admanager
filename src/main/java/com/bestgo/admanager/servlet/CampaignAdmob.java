package com.bestgo.admanager.servlet;

import com.bestgo.admanager.Config;
import com.bestgo.admanager.OperationResult;
import com.bestgo.admanager.utils.DateUtil;
import com.bestgo.admanager.utils.JedisPoolUtil;
import com.bestgo.admanager.utils.NumberUtil;
import com.bestgo.admanager.utils.Utils;
import com.bestgo.admanager_tools.AdWordsFetcher;
import com.bestgo.admanager_tools.DefaultConfig;
import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.System;
import java.util.*;

import static com.bestgo.admanager_tools.AdWordsFetcher.syncStatus;

/**
 * Desc: 有关Adwords系列创建的操作
 */
@WebServlet(name = "CampaignAdMob", urlPatterns = "/campaign_admob/*")
public class CampaignAdmob extends BaseHttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doPost(request, response);
        if (!Utils.isAdmin(request, response)) return;
        Jedis jedis = JedisPoolUtil.getJedis();
        String path = request.getPathInfo();
        JsonObject json = new JsonObject();
        if (path.startsWith("/upCampaign")) {
            OperationResult result = new OperationResult();
            try {
                String accountId = request.getParameter("accountId");
                String accountName = request.getParameter("accountName");
                String containsDisabledAccountId = request.getParameter("containsDisabledAccountId");

                String campaignStatus = request.getParameter("campaignStatus");
                String region = request.getParameter("region");
                String appName = request.getParameter("appName");

                String[] accountIds = accountId.split(",");
                String[] accountNames = accountName.split(",");
                String[] campaignStatuss = campaignStatus.split(",");
                String[] regions = region.split(",");


                DB.init();

                DefaultConfig.setProxy();

                //更新系列
                if (accountIds.length > 1) {
                    for (int j = 0; j < accountIds.length; j++) {
                        syncStatus(accountIds[j]);
                    }
                } else if (accountIds.length == 1) {
                    syncStatus(accountId);
                    System.out.println("完成了!");
                } else {
                    return;
                }
                System.out.println("更新完成，请点击删除！");
                result.message = "更新完成，请点击删除！";
                result.result = true;

            } catch (Exception e) {
                result.message = e.getMessage();
                result.result = false;
            }

            json.addProperty("ret", result.result ? 1 : 0);
            json.addProperty("message", result.message);
        } else if (path.startsWith("/archivedCampaign")) {
            OperationResult result = new OperationResult();
            try {
                String accountId = request.getParameter("accountId");
                String accountName = request.getParameter("accountName");
                String containsDisabledAccountId = request.getParameter("containsDisabledAccountId");

                String campaignStatus = request.getParameter("campaignStatus");
                String region = request.getParameter("region");
                String appName = request.getParameter("appName");

                String[] accountIds = accountId.split(",");
                String[] accountNames = accountName.split(",");
                String[] campaignStatuss = campaignStatus.split(",");
                String[] regions = region.split(",");


                DB.init();

                DefaultConfig.setProxy();

                //删除系列
                if (accountIds.length > 1) {
                    for (int i = 0; i < accountIds.length; i++) {
                        AdWordsFetcher.deleteAdwordsCampaignMultipleConditions(accountIds[i], campaignStatus, appName, region);
                    }
                } else {
                    AdWordsFetcher.deleteAdwordsCampaignMultipleConditions(accountId, campaignStatus, appName, region);
                }
                System.out.println("删除完成！！");
                result.message = "删除完成！！";
                result.result = true;

            } catch (Exception e) {
                result.message = e.getMessage();
                result.result = false;
            }

            json.addProperty("ret", result.result ? 1 : 0);
            json.addProperty("message", result.message);
        } else if (path.startsWith("/countArchivedCampaign")) {
            OperationResult result = new OperationResult();
            try {
                String accountId = request.getParameter("accountId");
                String accountName = request.getParameter("accountName");
                String containsDisabledAccountId = request.getParameter("containsDisabledAccountId");

                String campaignStatus = request.getParameter("campaignStatus");
                String region = request.getParameter("region");
                String appName = request.getParameter("appName");

                String[] accountIds = accountId.split(",");
                String[] accountNames = accountName.split(",");
                String[] campaignStatuss = campaignStatus.split(",");
                String[] regions = region.split(",");


                DB.init();

                DefaultConfig.setProxy();

                long index = 0;

                //删除系列个数统计
                if (accountIds.length > 1) {
                    for (int i = 0; i < accountIds.length; i++) {
                        index = countArchivedCampaignAdmob(accountIds[i], campaignStatus, appName, region);
                    }
                } else {
                    index = countArchivedCampaignAdmob(accountId, campaignStatus, appName, region);
                }

                int index1 = (int) index / 1000;

                System.out.println("需要删除" + index + "个系列，每次删除1000个。剩余" + index1 + "次操作！");
                result.message = "需要删除" + index + "个系列，每次删除1000个。剩余" + index1 + "次操作！";
                result.result = true;

            } catch (Exception e) {
                result.message = e.getMessage();
                result.result = false;
            }

            json.addProperty("ret", result.result ? 1 : 0);
            json.addProperty("message", result.message);
        } else if (path.startsWith("/create")) {
            String appName = request.getParameter("appName");
            String gpPackageId = request.getParameter("gpPackageId");
            String accountId = request.getParameter("accountId");
            String accountName = request.getParameter("accountName");
            String createCount = request.getParameter("createCount");
            String region = request.getParameter("region");
            String excludedRegion = request.getParameter("excludedRegion");
            String language = request.getParameter("language");
            String conversionId = request.getParameter("conversion_id");
            String campaignName = request.getParameter("campaignName");
            String bugdet = request.getParameter("bugdet");
            String bidding = request.getParameter("bidding");
            bidding = bidding.trim();
            String maxCPA = request.getParameter("maxCPA");
            String message1 = request.getParameter("message1");
            String groupIdStr = request.getParameter("groupId");
            String message2 = request.getParameter("message2");
            String message3 = request.getParameter("message3");
            String message4 = request.getParameter("message4");
            String imagePath = request.getParameter("imagePath");

            if (maxCPA == null) maxCPA = "";

            OperationResult result = new OperationResult();
            try {

                result.result = false;
                int groupId = NumberUtil.parseInt(groupIdStr, 0);
                if (groupId == 0) {
                    result.message = "广告组ID不存在！请联系管理员";
                } else if (createCount.isEmpty()) {
                    result.message = "创建数量不能为空";
                } else if (message1.isEmpty()) {
                    result.message = "广告语1不能为空";
                } else if (message2.isEmpty()) {
                    result.message = "广告语2不能为空";
                } else if (message3.isEmpty()) {
                    result.message = "广告语3不能为空";
                } else if (message4.isEmpty()) {
                    result.message = "广告语4不能为空";
                } else if (campaignName.isEmpty()) {
                    result.message = "广告系列名称不能为空";
                } else if (bugdet.isEmpty()) {
                    result.message = "预算不能为空";
                } else if (bidding.isEmpty()) {
                    result.message = "出价不能为空";
                } else if (bidding.indexOf(" ") != -1) {
                    result.message = "出价不能出现空格！";
                } else if (region.isEmpty()) {
                    result.message = "国家不能为空";
                } else {
                    double dBidding = NumberUtil.parseDouble(bidding, 0);
                    double maxBidding = 0.1;
                    String maxBiddingStr = jedis.hget("tagNameBiddingMap", appName);
                    if (maxBiddingStr == null) {
                        JSObject one = DB.findOneBySql("select max_bidding from web_tag where tag_name = '" + appName + "'");
                        if (one.hasObjectData()) {
                            maxBidding = NumberUtil.convertDouble(one.get("max_bidding"), 0);
                            jedis.hset("tagNameBiddingMap", appName, maxBidding + "");
                        }
                    } else {
                        maxBidding = NumberUtil.parseDouble(maxBiddingStr, 0);
                    }
                    if (dBidding > maxBidding) {
                        result.message = "bidding超过了本应用的最大出价,   " + bidding + " > " + maxBidding;
                    } else {
                        JSObject record = DB.simpleScan("web_system_config").select("config_value").where(DB.filter().whereEqualTo("config_key", "admob_image_path")).execute();
                        String imageRoot = "";
                        if (record.hasObjectData()) {
                            imageRoot = record.get("config_value");
                        }

                        File imagesPath = new File(imageRoot + File.separatorChar + imagePath);
                        if (imagesPath.exists()) {
                            result.result = true;
                        } else {
                            result.result = false;
                            result.message = "图片路径不存在";
                        }
                        if (result.result) {
                            String imageAbsolutePath = imagesPath.getAbsolutePath();
                            Calendar calendar = Calendar.getInstance();
                            String campaignNameOld = "";

                            String[] countrys = region.split(",");
                            String countrysStr = "";
                            for (int i = 0, len = countrys.length; i < len; i++) {
                                countrysStr += "'" + countrys[i] + "',";
                            }
                            String countryListStr = "";
                            if (countrysStr.length() > 0) {
                                countrysStr = countrysStr.substring(0, countrysStr.length() - 1);
                                String sqlCountry = "select country_name from app_country_code_dict where country_code in (" + countrysStr + ")";
                                List<JSObject> countryList = DB.findListBySql(sqlCountry);

                                for (JSObject j : countryList) {
                                    countryListStr += j.get("country_name") + ",";
                                }
                            }
                            if (countryListStr.length() > 30) {
                                countryListStr = countryListStr.substring(0, 30);
                            }
                            if (countryListStr != null && countryListStr.length() > 0) {
                                campaignNameOld = campaignName.replace(region, countryListStr) + "_";
                            } else {
                                campaignNameOld = campaignName + "_";
                            }
                            campaignNameOld = campaignNameOld.replace("Group_", "Group" + groupId);
                            String[] accountNameArr = accountName.split(",");
                            String[] accountIdArr = accountId.split(",");
                            int createCountInt = Integer.parseInt(createCount);
                            Random random = new Random();
                            for (int j = 0, len = accountNameArr.length; j < len; j++) {
                                for (int i = 0; i < createCountInt; i++) {
                                    String now = String.format("%d-%02d-%02d %02d:%02d:%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH),
                                            calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
                                    int r = random.nextInt();
                                    long s = System.currentTimeMillis();
                                    String part = new StringBuffer(i + r + s + "").reverse().toString();
                                    campaignName = campaignNameOld + accountNameArr[j] + "_" + (i + j) + part;

                                    if (campaignName.length() > 150) {
                                        campaignName = campaignName.substring(0, 150);
                                    }
                                    long genId = DB.insert("ad_campaigns_admob")
                                            .put("account_id", accountIdArr[j])
                                            .put("campaign_name", campaignName)
                                            .put("app_id", gpPackageId)
                                            .put("country_region", region)
                                            .put("language", language)
                                            .put("conversion_id", conversionId)
                                            .put("excluded_region", excludedRegion)
                                            .put("create_time", now)
                                            .put("bugdet", bugdet)
                                            .put("bidding", bidding)
                                            .put("max_cpa", maxCPA)
                                            .put("group_id", groupId)
                                            .put("message1", message1)
                                            .put("message2", message2)
                                            .put("message3", message3)
                                            .put("message4", message4)
                                            .put("app_name", appName)
                                            .put("tag_name", appName)
                                            .put("image_path", imageAbsolutePath)
                                            .executeReturnId();
                                    if (genId <= 0) {
                                        Logger logger = Logger.getRootLogger();
                                        logger.debug("app_id=" + gpPackageId + ", account_id=" + accountIdArr[j] + ", country_region=" + region +
                                                ", excluded_region=" + excludedRegion + ", create_time=" + now + ", language=" + language +
                                                ", campaign_name=" + campaignName + ", conversion_id=" + conversionId + ", bugdet=" + bugdet +
                                                ", bidding=" + bidding + ", bugdet=" + bugdet + ", bidding=" + bidding + ", message1=" + message1 + ", message2=" + message2
                                                + ", message3=" + message3 + ", message4=" + message4 + ", app_name=" + appName + ", image_path=" + imageAbsolutePath);
                                    }
                                }
                            }

                            result.result = true;
                        }
                    }

                }

            } catch (Exception ex) {
                result.message = ex.getMessage();
                result.result = false;
            }
            json.addProperty("ret", result.result ? 1 : 0);
            json.addProperty("message", result.message);
        } else if (path.startsWith("/update")) {
            String id = request.getParameter("id");
            String campaignId = request.getParameter("campaignId");
            String tags = request.getParameter("tags");
            String  region = request.getParameter("selectRegionAdmob");
            String selectRegion = region == null ? "" : region;

            OperationResult result = new OperationResult();
            if (id != null) {
                try {
                    String sql = "SELECT id FROM web_tag WHERE tag_name = '" + tags + "'";
                    JSObject one = DB.findOneBySql(sql);
                    if (one.hasObjectData()) {

                        long tagId = one.get("id");
                        sql = "UPDATE web_ad_campaigns_admob SET tag_id = " + tagId + ", country_code = '" + selectRegion + "' WHERE id = " + id;
                        boolean b = DB.updateBySql(sql);
                        if (b) {
                            sql = "UPDATE web_ad_campaigns_history_admob SET tag_id = " + tagId + ", country_code = '" + selectRegion + "' WHERE campaign_id = '" + campaignId + "'";
                            b = DB.updateBySql(sql);
                            if (b) {
                                result.result = true;
                                result.message = "更新成功！";
                            }
                        }

                    } else {
                        result.result = false;
                        result.message = "没有在web_tag表找到这个应用！";
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                result.result = false;
                result.message = "ID为空！传参异常，联系管理员！";
            }
            json.addProperty("ret", result.result ? 1 : 0);
            json.addProperty("message", result.message);
        } else if (path.startsWith("/select_messages_by_app_and_region_and_group_id")) {
            String region = request.getParameter("regionAdmob");
            String appName = request.getParameter("appNameAdmob");
            String advertGroupId = request.getParameter("advertGroupId");
            if (region != null) {
                Map<String, String> regionLanguageAdmobRelMap = Config.getRegionLanguageRelMap();
                String languageAdmob = "";
                String[] regionAdmobArray = region.split(",");
                Set<String> languageAdmobSet = new HashSet<>();
                for (int i = 0, len = regionAdmobArray.length; i < len; i++) {
                    languageAdmobSet.add(regionLanguageAdmobRelMap.get(regionAdmobArray[i]));
                }

                if (languageAdmobSet.size() == 1) {
                    languageAdmob = regionLanguageAdmobRelMap.get(regionAdmobArray[0]);
                } else {
                    languageAdmob = "English";
                }
                String sql = "select message1,message2,message3,message4 from web_ad_descript_dict_admob where app_name = '" + appName + "' and language = '" + languageAdmob + "' and group_id = " + advertGroupId;

                JSObject messages = null;
                try {
                    messages = DB.findOneBySql(sql);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (messages != null && messages.hasObjectData()) {
                    json.addProperty("message1", (String) (messages.get("message1")));
                    json.addProperty("message2", (String) (messages.get("message2")));
                    json.addProperty("message3", (String) (messages.get("message3")));
                    json.addProperty("message4", (String) (messages.get("message4")));
                    json.addProperty("ret", 1);
                }
            }
        } else if (path.startsWith("/query")) {
            String word = request.getParameter("word");
            if (word != null) {
                JsonArray array = new JsonArray();
                List<JSObject> data = fetchData(word);
                json.addProperty("ret", 1);
                for (int i = 0; i < data.size(); i++) {
                    JsonObject one = new JsonObject();
                    Set<String> keySet = data.get(i).getKeys();
                    for (String key : keySet) {
                        Object value = data.get(i).get(key);
                        if (value instanceof String) {
                            one.addProperty(key, (String) value);
                        } else if (value instanceof Integer) {
                            one.addProperty(key, (Integer) value);
                        } else if (value instanceof Long) {
                            one.addProperty(key, (Long) value);
                        } else if (value instanceof Double) {
                            one.addProperty(key, NumberUtil.trimDouble((Double) value, 3));
                        } else {
                            one.addProperty(key, value.toString());
                        }
                    }
                    List<String> tags = CampaignAdmob.bindTags(data.get(i).get("campaign_id"));
                    String tagStr = "";
                    for (int ii = 0; ii < tags.size(); ii++) {
                        tagStr += (tags.get(ii) + ",");
                    }
                    if (tagStr.length() > 0) {
                        tagStr = tagStr.substring(0, tagStr.length() - 1);
                    }
                    double installed = NumberUtil.convertDouble(one.get("total_installed").getAsDouble(), 0);
                    double click = NumberUtil.convertDouble(one.get("total_click").getAsDouble(), 0);
                    double cvr = click > 0 ? installed / click : 0;
                    one.addProperty("cvr", NumberUtil.trimDouble(cvr, 3));
                    one.addProperty("tagStr", tagStr);
                    array.add(one);
                }
                json.add("data", array);
            }
        } else if (path.startsWith("/fetch_campaigns_not_exist_tag")) {
            String today = DateUtil.getNowDate();
            String tenDaysAgo = DateUtil.addDay(today, -10, "yyyy-MM-dd");//十天前
            JsonArray array = new JsonArray();
            try {
                String sqlFilterAll = "SELECT id,campaign_id,campaign_name,account_id,create_time,STATUS,budget,bidding,total_spend,total_click,total_installed,total_impressions,cpa,ctr,tag_id,country_code\n" +
                        "\tFROM web_ad_campaigns_admob \n" +
                        "\t\tWHERE create_time > '" + tenDaysAgo + "' AND  (tag_id = 0 OR country_code = '')";
                List<JSObject> data = DB.findListBySql(sqlFilterAll);
                if (data != null) {
                    for (int i = 0, len = data.size(); i < len; i++) {
                        JsonObject one = new JsonObject();
                        Set<String> keySet = data.get(i).getKeys();
                        for (String key : keySet) {
                            Object value = data.get(i).get(key);

                            if ("tag_id".equalsIgnoreCase(key) && !"0".equalsIgnoreCase(value.toString())) {
                                value = selTagName(value.toString());
                            }

                            if (value instanceof String) {
                                one.addProperty(key, (String) value);
                            } else if (value instanceof Integer) {
                                one.addProperty(key, (Integer) value);
                            } else if (value instanceof Long) {
                                one.addProperty(key, (Long) value);
                            } else if (value instanceof Double) {
                                one.addProperty(key, NumberUtil.trimDouble((Double) value, 3));
                            } else {
                                one.addProperty(key, value.toString());
                            }
                        }
                        double installed = NumberUtil.convertDouble(one.get("total_installed").getAsDouble(), 0);
                        double click = NumberUtil.convertDouble(one.get("total_click").getAsDouble(), 0);
                        double cvr = click > 0 ? installed / click : 0;
                        one.addProperty("cvr", NumberUtil.trimDouble(cvr, 3));
                        //one.addProperty("tagStr", "");
                        array.add(one);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            json.addProperty("ret", 1);
            json.add("data", array);
        } else if (path.startsWith("/selectAdmobMessage")) {
            String appNameAdmob = request.getParameter("appNameAdmob");
            String languageAdmob = request.getParameter("languageAdmob");
            try {
                String sql = "select message1,message2,message3,message4 from web_ad_descript_dict_admob where app_name = '" + appNameAdmob + "' and language = '" + languageAdmob + "' limit 1";
                JSObject messages = new JSObject();
                try {
                    messages = DB.findOneBySql(sql);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                json.addProperty("message1", (String) (messages.get("message1")));
                json.addProperty("message2", (String) (messages.get("message2")));
                json.addProperty("message3", (String) (messages.get("message3")));
                json.addProperty("message4", (String) (messages.get("message4")));
                json.addProperty("languageAdmob", languageAdmob);
                json.addProperty("ret", 1);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (path.startsWith("/selectMaxBiddingByAppName")) {
            String appName = request.getParameter("appName");
            String maxBiddingStr = jedis.hget("tagNameBiddingMap", appName);
            if (maxBiddingStr == null) {
                JSObject one = null;
                try {
                    one = DB.findOneBySql("select max_bidding from web_tag where tag_name = '" + appName + "'");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (one.hasObjectData()) {
                    maxBiddingStr = NumberUtil.convertDouble(one.get("max_bidding"), 0) + "";
                    jedis.hset("tagNameBiddingMap", appName, maxBiddingStr);
                }
            }
            if (maxBiddingStr != null) {
                json.addProperty("max_bidding", maxBiddingStr);
                json.addProperty("ret", 1);
            }
        }
        //将jedis连接释放，回收到连接池
        if (jedis != null) {
            jedis.close();
        }
        response.getWriter().write(json.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    private OperationResult updateCampaign(String id, String tags) {
        OperationResult ret = new OperationResult();

        try {
            JSObject campaign = DB.simpleScan("web_ad_campaigns_admob").select("id", "campaign_id").where(DB.filter().whereEqualTo("id", NumberUtil.parseInt(id, 0))).execute();
            if (campaign.hasObjectData()) {
                if (tags != null) {
                    String[] newTagList = tags.split(",");
                    String campaignId = campaign.get("campaign_id");
                    List<JSObject> tagList = findBindTags(campaignId);
                    List<String> newList = new ArrayList<>();
                    List<String> delList = new ArrayList<>();

                    for (int i = 0; i < tagList.size(); i++) {
                        boolean save = false;
                        for (int j = 0; j < newTagList.length; j++) {
                            if (tagList.get(i).get("tag_name").equals(newTagList[j])) {
                                save = true;
                            }
                        }
                        if (!save) {
                            delList.add(tagList.get(i).get("tag_name"));
                        }
                    }
                    for (int i = 0; i < newTagList.length; i++) {
                        if (newTagList[i].isEmpty()) continue;

                        boolean create = true;
                        for (int j = 0; j < tagList.size(); j++) {
                            if (newTagList[i].equals(tagList.get(j).get("tag_name"))) {
                                create = false;
                            }
                        }
                        if (create) {
                            newList.add(newTagList[i]);
                        }
                    }
                    List<JSObject> allTags = Tags.fetchAllTags();
                    HashMap<String, Long> tagIds = new HashMap<>();
                    for (int i = 0; i < allTags.size(); i++) {
                        tagIds.put(allTags.get(i).get("tag_name"), allTags.get(i).get("id"));
                    }
                    for (String tagName : delList) {
                        long tagId = tagIds.get(tagName);
                        DB.delete("web_ad_campaign_tag_admob_rel").where(DB.filter().whereEqualTo("tag_id", tagId))
                                .and(DB.filter().whereEqualTo("campaign_id", campaignId)).execute();
                    }
                    for (String tagName : newList) {
                        long tagId = tagIds.get(tagName);
                        DB.insert("web_ad_campaign_tag_admob_rel").put("tag_id", tagId)
                                .put("campaign_id", campaignId).execute();
                    }
                }

                ret.result = true;
                ret.message = "修改成功";
            } else {
                ret.result = false;
                ret.message = "修改的广告系列不存在";
            }
        } catch (Exception e) {
            ret.result = false;
            ret.message = e.getMessage();
            Logger logger = Logger.getRootLogger();
            logger.error(e.getMessage(), e);
        }

        return ret;
    }

    public static List<JSObject> fetchData(String word) {
        List<JSObject> list = new ArrayList<>();
        try {
            return DB.scan("web_ad_campaigns_admob").select("id", "campaign_id", "campaign_name", "account_id", "create_time",
                    "status", "budget", "bidding", "total_spend", "total_installed", "total_click", "cpa", "ctr")
                    .where(DB.filter().whereLikeTo("campaign_name", "%" + word + "%")).or(DB.filter().whereEqualTo("campaign_id", word)).orderByAsc("id").execute();
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return list;
    }

    public static List<JSObject> fetchData(int index, int size) {
        List<JSObject> list = new ArrayList<>();
        try {
            return DB.scan("web_ad_campaigns_admob").select("id", "campaign_id", "campaign_name", "account_id", "create_time",
                    "status", "budget", "bidding", "total_spend", "total_installed", "total_click", "cpa", "ctr", "country_code")
                    .limit(size).start(index * size).orderByAsc("id").execute();
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return list;
    }

    public static long count() {
        try {
            JSObject object = DB.simpleScan("web_ad_campaigns_admob").select(DB.func(DB.COUNT, "id")).execute();
            return object.get("count(id)");
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return 0;
    }

    private static List<JSObject> findBindTags(String campaignId) {
        ArrayList<JSObject> retList = new ArrayList<>();
        String sql = "select tag_id, tag_name from web_ad_campaign_tag_admob_rel,web_tag where web_tag.id=web_ad_campaign_tag_admob_rel.tag_id and campaign_id=?";
        try {
            return DB.findListBySql(sql, campaignId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retList;
    }

    public static List<String> bindTags(String campaignId) {
        ArrayList<String> retList = new ArrayList<>();
        String sql = "select tag_name from web_ad_campaign_tag_admob_rel,web_tag where web_tag.id=web_ad_campaign_tag_admob_rel.tag_id and campaign_id=?";
        try {
            List<JSObject> list = DB.findListBySql(sql, campaignId);
            for (int i = 0; i < list.size(); i++) {
                retList.add(list.get(i).get("tag_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retList;
    }

    /**
     * 多情况下删除Adwords的广告系列个数统计
     *
     * @param accountId
     * @param status
     * @param tag
     * @param country
     */
    public static long countArchivedCampaignAdmob(String accountId, String status, String tag, String country) {
        try {
            String sql = "";
            JSObject oneBySql;
            long geShu = 0;
            if (!(null == accountId) && !"".equals(accountId)) {//帐户非空
                if (!(null == status) && !"".equals(status)) {//帐号+状态
                    sql = "SELECT COUNT(*) AS geShu  FROM web_ad_campaigns_admob WHERE account_id = '" + accountId + "' AND STATUS = '" + status + "' ";
                    oneBySql = DB.findOneBySql(sql);
                    geShu = oneBySql.get("geShu");
                } else if (!(null == tag) && !"".equals(tag)) {//帐号+应用
                    Map<String, Integer> facebookTagDetails = AdWordsFetcher.getFacebookTagDetails();
                    Integer tag_id = facebookTagDetails.get(tag);
                    sql = "SELECT COUNT(*) AS geShu FROM web_ad_campaigns_admob WHERE account_id = '" + accountId + "' AND tag_id = '" + tag_id + "' AND status != 'ARCHIVED' ";
                    oneBySql = DB.findOneBySql(sql);
                    geShu = oneBySql.get("geShu");
                } else {//帐户下所有系列
                    sql = "SELECT COUNT(*) AS geShu FROM web_ad_campaigns_admob where account_id = '" + accountId + "' AND status != 'ARCHIVED' ";
                    oneBySql = DB.findOneBySql(sql);
                    geShu = oneBySql.get("geShu");
                }
            } else if (!(null == tag) && !(null == country) && !"".equals(tag) && !"".equals(country)) {//应用+国家
                Map<String, Integer> facebookTagDetails = AdWordsFetcher.getFacebookTagDetails();
                Integer tag_id = facebookTagDetails.get(tag);
                sql = "SELECT  COUNT(*) AS geShu FROM web_ad_campaigns_admob WHERE tag_id = '" + tag_id + "' AND country_code LIKE '%" + country + "%' AND status != 'ARCHIVED' ";
                oneBySql = DB.findOneBySql(sql);
                geShu = oneBySql.get("geShu");
            } else {
                System.out.println("参数有误！请重新输入！！");
            }
            return geShu;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String selTagName(String tag_id) {
        ArrayList<String> retList = new ArrayList<>();
        String tag_name = "";
        String sql = "SELECT tag_name FROM web_tag WHERE id = " + tag_id;
        try {
            JSObject oneBySql = DB.findOneBySql(sql);
            tag_name = oneBySql.get("tag_name");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tag_name;
    }

}
