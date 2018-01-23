package com.bestgo.admanager.servlet;

import com.bestgo.admanager.Config;
import com.bestgo.admanager.DateUtil;
import com.bestgo.admanager.OperationResult;
import com.bestgo.admanager.Utils;
import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.services.Store;
import com.bestgo.common.database.utils.JSObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.org.apache.xpath.internal.SourceTree;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.*;
import java.lang.System;
import java.math.BigDecimal;
import java.util.*;

@WebServlet(name = "Campaign", urlPatterns = "/campaign/*")
public class Campaign extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Utils.isAdmin(request, response)) return;

        String path = request.getPathInfo();
        JsonObject json = new JsonObject();

        if (path.startsWith("/create")) {
            String appName = request.getParameter("appName");
            String appId = request.getParameter("appId");
            String accountId = request.getParameter("accountId");
            String accountName = request.getParameter("accountName");

            String createCount = request.getParameter("createCount");
            String pageId = request.getParameter("pageId");
            String region = request.getParameter("region");
            String excludedRegion = request.getParameter("excludedRegion");
            String language = request.getParameter("language");
            String age = request.getParameter("age");
            String gender = request.getParameter("gender");
            String interest = request.getParameter("interest");
            String userOs = request.getParameter("userOs");
            String userDevice = request.getParameter("userDevice");
            String campaignName = request.getParameter("campaignName");
            String bugdet = request.getParameter("bugdet");
            String bidding = request.getParameter("bidding");
            String maxCPA = request.getParameter("maxCPA");
            String title = request.getParameter("title");
            String message = request.getParameter("message");
            String imagePath = request.getParameter("imagePath");
            String videoPath = request.getParameter("videoPath");

            OperationResult result = new OperationResult();
            try {
                result.result = true;

                if (createCount.isEmpty()) {
                    result.result = false;
                    result.message = "创建数量不能为空";
                }

                if (title.isEmpty()) {
                    result.result = false;
                    result.message = "标题不能为空";
                }
                if (message.isEmpty()) {
                    result.result = false;
                    result.message = "广告语不能为空";
                }
                if (campaignName.isEmpty()) {
                    result.result = false;
                    result.message = "广告系列名称不能为空";
                }
                if (bugdet.isEmpty()) {
                    result.result = false;
                    result.message = "预算不能为空";
                }
                if (bidding.isEmpty()) {
                    result.result = false;
                    result.message = "出价不能为空";
                }
                double dBidding = Utils.parseDouble(bidding, 0);
                if (dBidding >= 0.5) {
                    result.result = false;
                    result.message = "bidding超过了0.5,   " + bidding;
                }

                File imagesPath = null;
                File videosPath = null;
                if(!imagePath.isEmpty()){
                    JSObject record = DB.simpleScan("web_system_config").select("config_value").where(DB.filter().whereEqualTo("config_key", "fb_image_path")).execute();
                    String imageRoot = null;
                    if (record.hasObjectData()) {
                        imageRoot = record.get("config_value");
                    }
                    imagesPath = new File(imageRoot + File.separatorChar + imagePath);
                    if (!imagesPath.exists()) {
                        result.result = false;
                        result.message = "图片路径不存在";
                    }
                }else if(!videoPath.isEmpty()){
                    JSObject record = DB.simpleScan("web_system_config").select("config_value").where(DB.filter().whereEqualTo("config_key", "fb_video_path")).execute();
                    String videoRoot = null;
                    if (record.hasObjectData()) {
                        videoRoot = record.get("config_value");
                    }
                    videosPath = new File(videoRoot + File.separatorChar + videoPath);
                    if (!videosPath.exists()) {
                        result.result = false;
                        result.message = "视频路径不存在";
                    }
                }else {
                    result.result = false;
                    result.message = "图片或视频路径二选一，不能为空！";
                }


                if (result.result) {

                    Calendar calendar = Calendar.getInstance();
                    String campaignNameOld = campaignName + "_";
                    String[] accountNameArr = accountName.split(",");
                    String accountNameArrStr = accountName.replace(",", "");
                    String[] accountIdArr = accountId.split(",");
                    int createCountInt = Integer.parseInt(createCount);
                    Collection<File> uploadImages = null;
                    Collection<File> uploadVideos = null;
                    if(imagesPath != null){
                        uploadImages = FileUtils.listFiles(imagesPath, null, false);
                    }else if(videosPath != null){
                        uploadVideos = FileUtils.listFiles(videosPath, null, false);
                    }

                    for(int j=0,len = accountNameArr.length;j<len;j++){
                        for(int i=0;i<createCountInt;i++){
                            String now  = String.format("%d-%02d-%02d %02d:%02d:%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH),
                                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
                            String s = String.valueOf(System.currentTimeMillis());
                            s = s.substring(9,s.length());
                            campaignName = campaignNameOld.replace(accountNameArrStr,accountNameArr[j]) + s + "_" + i;
                            if (campaignName.length() > 100) {
                                campaignName = campaignName.substring(0, 100);
                            }


                            long genId = DB.insert("ad_campaigns")
                                    .put("facebook_app_id", appId)
                                    .put("account_id", accountIdArr[j])
                                    .put("country_region", region)
                                    .put("excluded_region", excludedRegion)
                                    .put("create_time", now)
                                    .put("language", language)
                                    .put("campaign_name", campaignName)
                                    .put("page_id", pageId)
                                    .put("bugdet", bugdet)
                                    .put("bidding", bidding)
                                    .put("title", title)
                                    .put("message", message)
                                    .put("app_name", appName)
                                    .put("tag_name", appName)
                                    .put("age", age)
                                    .put("gender", gender)
                                    .put("detail_target", interest)
                                    .put("max_cpa", maxCPA)
                                    .put("user_devices", userDevice)
                                    .put("user_os", userOs)
                                    .executeReturnId();
                            if(genId >0){
                                boolean flag = false;
                                if(uploadImages != null){
                                    for (File file : uploadImages) {
                                        String fileName = file.getAbsolutePath().toLowerCase();
                                        if (fileName.endsWith("gif") || fileName.endsWith("jpg") || fileName.endsWith("jpeg") || fileName.endsWith("png")) {
                                            String sql = "insert into ad_ads set parent_id=" + genId + ", image_file_path='" + file.getAbsolutePath() + "'";
                                            flag = DB.updateBySql(sql);
                                        }
                                    }
                                }else if(uploadVideos != null){
                                    String video_file_path = null;
                                    String thumbnail_image_file_path = null;
                                    for (File file : uploadVideos) {
                                        String fileAbsolutePath = file.getAbsolutePath();
                                        String fileName = fileAbsolutePath.toLowerCase();
//                                    String xxx = file.getAbsolutePath().replaceAll("\\\\","/");
                                        if (fileName.endsWith("mp4") || fileName.endsWith("mov")) {
                                            video_file_path = fileAbsolutePath;
                                        }else if (fileName.endsWith("jpg") || fileName.endsWith("jpeg") || fileName.endsWith("png") || fileName.endsWith("gif")) {
                                            thumbnail_image_file_path = fileAbsolutePath;
                                        }
                                    }
                                    if(video_file_path != null && thumbnail_image_file_path != null){
                                        String sql = "insert into ad_ads set parent_id = '"+genId+"', video_file_path = '"+video_file_path+"', thumbnail_image_file_path = '"+thumbnail_image_file_path+"'";
                                        flag = DB.updateBySql(sql);
                                    }
                                }
                                if(!flag){
                                    DB.delete("ad_campaigns").where(DB.filter().whereEqualTo("campaign_name", campaignName)).execute();
                                    System.out.println("由于Media导致第"+(i+1)+"个系列创建失败,名为["+campaignName + "]的系列已经被删除！");
                                }
                            }

//                            if(genId >0){
//                                try {
//                                    String[] split = region.split(",");
//                                    String countryNamesStr = "";
//                                    for(int k=0,ee=split.length;k<ee;k++){
//                                        countryNamesStr += "'" + split[k] + "',";
//                                    }
//                                    if(countryNamesStr != null && countryNamesStr.length()>0){
//                                        countryNamesStr = countryNamesStr.substring(0,countryNamesStr.length()-1);
//                                    }
//                                    String sql = "select country_code from app_country_code_dict where country_name in ("+countryNamesStr+")";
//                                    List<JSObject> countryCodeJSObjectList = DB.findListBySql(sql);
//                                    for(JSObject m: countryCodeJSObjectList){
//                                        String countryCodeStr = m.get("country_code");
//                                        DB.insert("web_ad_campaign_operation_log")
//                                                .put("operation_date", now)
//                                                .put("app_name",appName)
//                                                .put("country_code", countryCodeStr)
//                                                .put("campaign_name",campaignName)
//                                                .put("enabled",1)
//                                                .put("bidding",bidding)
//                                                .execute();
//                                    }
//
//
//
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }

                        }
                    }
                    result.result = true;
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
            String campaignName = request.getParameter("campaignName");
            String status = request.getParameter("status");
            String budget = request.getParameter("budget");
            String bidding = request.getParameter("bidding");
            String tags = request.getParameter("tags");

            if (id == null) {
                try {
                    JSObject campaign = DB.simpleScan("web_ad_campaigns").select("id", "campaign_id", "adset_id", "campaign_name", "status", "budget", "bidding").where(DB.filter().whereEqualTo("campaign_id", campaignId)).execute();
                    if (campaign.hasObjectData()) {
                        id = campaign.get("id").toString();
                    }
                } catch (Exception e) {
                }
            }
            OperationResult result = updateCampaign(id, campaignName, status, budget, bidding, tags);
            json.addProperty("ret", result.result ? 1 : 0);
            json.addProperty("message", result.message);
        } else if (path.startsWith("/query_batch_change_status")) {
            try {
                String[] fields = {"id", "network", "campaign_id", "campaign_name", "failed_count", "last_error_message"};
                JsonArray array = new JsonArray();

                List<JSObject> list = DB.scan("web_ad_batch_change_campaigns").select(fields)
                        .where(DB.filter().whereEqualTo("success", 0)).execute();
                for (int i = 0; i < list.size(); i++) {
                    JsonObject one = new JsonObject();
                    for (int j = 0; j < fields.length; j++) {
                        String value = list.get(i).get(fields[j]).toString();
                        if (fields[j].equals("campaign_name")) {
                            if (value.isEmpty()) {
                                String campaignId = list.get(i).get("campaign_id");
                                String tableName = "web_ad_campaigns";
                                if (list.get(i).get("network").equals("admob")) {
                                    tableName = "web_ad_campaigns_admob";
                                }
                                JSObject record = DB.simpleScan(tableName)
                                        .select("campaign_name")
                                        .where(DB.filter().whereEqualTo("campaign_id", campaignId))
                                        .execute();
                                if (record.hasObjectData()) {
                                    value = record.get("campaign_name");
                                }
                            }
                        }
                        one.addProperty(fields[j], value);
                    }
                    array.add(one);
                }
                json.add("data", array);
                json.addProperty("ret", 1);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (path.startsWith("/query_status")) {
            try {
                Calendar calendar = Calendar.getInstance();
                String startDate = String.format("%d-%02d-%02d 00:00:00",
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
                String endDate = String.format("%d-%02d-%02d 23:59:59",
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));

                calendar.add(Calendar.DAY_OF_MONTH, -1);
                String yesterdayStart = String.format("%d-%02d-%02d 00:00:00",
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
                String yesterdayEnd = String.format("%d-%02d-%02d 23:59:59",
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));

                long count = 0;
                String sql = "select count(id) as cnt from ad_campaigns where create_time between '" + startDate + "' and '" + endDate + "'";
                JSObject record = DB.findOneBySql(sql);
                if (record.hasObjectData()) {
                    count = record.get("cnt");
                }
                sql = "select count(id) as cnt from ad_campaigns_admob where create_time between '" + startDate + "' and '" + endDate + "'";
                record = DB.findOneBySql(sql);
                if (record.hasObjectData()) {
                    count += (long)record.get("cnt");
                }

                Map<String, ReduceCostItem> reduceCostItemHashMap = new HashMap<>();

                sql = "select campaign_id, network, enabled, excluded_country from web_ad_batch_change_campaigns where create_time between '" + startDate + "' and '" + endDate + "' and success=1";
                List<JSObject> changeList = DB.findListBySql(sql);
                for (int i = 0; i < changeList.size(); i++) {
                    JSObject one = changeList.get(i);
                    int enabled = one.get("enabled");
                    String excludeCountry = one.get("excluded_country");
                    String network = one.get("network");
                    String campaignId = one.get("campaign_id");
                    if (enabled != 0 && excludeCountry.isEmpty()) {
                        continue;
                    }
                    ReduceCostItem item = reduceCostItemHashMap.get(campaignId);
                    if (item == null) {
                        item = new ReduceCostItem();
                        item.campaignId = campaignId;
                        if (network.equals("admob")) {
                            sql = "select tag_name from web_ad_campaign_tag_admob_rel, web_tag where campaign_id=? and tag_id=web_tag.id";
                            JSObject tagNameRecord = DB.findOneBySql(sql, campaignId);
                            item.appName = tagNameRecord.get("tag_name");
                        } else {
                            sql = "select tag_name from web_ad_campaign_tag_rel, web_tag where campaign_id=? and tag_id=web_tag.id";
                            JSObject tagNameRecord = DB.findOneBySql(sql, campaignId);
                            item.appName = tagNameRecord.get("tag_name");
                        }
                        reduceCostItemHashMap.put(campaignId, item);
                    }
                    if (enabled == 0) {
                        item.enabled = 0;
                    } else if (item.enabled != 0) {
                        item.enabled = enabled;
                    }
                    if (!excludeCountry.isEmpty()) {
                        item.countryExcluded.add(excludeCountry);
                    }
                }

                sql = "select campaign_id, country_code, total_spend from web_ad_campaigns_country_history where date between ? and ?";
                List<JSObject> facebookHistory = DB.findListBySql(sql, yesterdayStart, yesterdayEnd);
                for (int i = 0; i < facebookHistory.size(); i++) {
                    JSObject one = facebookHistory.get(i);
                    String campaignId = one.get("campaign_id");
                    String countryCode = one.get("country_code");
                    double cost = Utils.convertDouble(one.get("total_spend"), 0);
                    ReduceCostItem item = reduceCostItemHashMap.get(campaignId);
                    if (item == null) {
                        continue;
                    } else {
                        if (item.enabled == 0) {
                            if (!item.countryRemoved.contains(countryCode)) {
                                item.reduceCost += cost;
                                item.countryRemoved.add(countryCode);
                            }
                        } else if (item.countryExcluded.contains(countryCode)) {
                            if (!item.countryRemoved.contains(countryCode)) {
                                item.reduceCost += cost;
                                item.countryRemoved.add(countryCode);
                            }
                        }
                    }
                }

                sql = "select campaign_id, country_code, total_spend from web_ad_campaigns_country_history_admob where date between ? and ?";
                List<JSObject> adwordsHistory = DB.findListBySql(sql, yesterdayStart, yesterdayEnd);
                for (int i = 0; i < adwordsHistory.size(); i++) {
                    JSObject one = adwordsHistory.get(i);
                    String campaignId = one.get("campaign_id");
                    String countryCode = one.get("country_code");
                    double cost = Utils.convertDouble(one.get("total_spend"), 0);
                    ReduceCostItem item = reduceCostItemHashMap.get(campaignId);
                    if (item == null) {
                        continue;
                    } else {
                        if (item.enabled == 0) {
                            if (!item.countryRemoved.contains(countryCode)) {
                                item.reduceCost += cost;
                                item.countryRemoved.add(countryCode);
                            }
                        } else if (item.countryExcluded.contains(countryCode)) {
                            if (!item.countryRemoved.contains(countryCode)) {
                                item.reduceCost += cost;
                                item.countryRemoved.add(countryCode);
                            }
                        }
                    }
                }

                Map<String, Double> reduceCost = new HashMap<>();
                for (ReduceCostItem item : reduceCostItemHashMap.values()) {
                    Double cost = reduceCost.get(item.appName);
                    if (cost == null) {
                        cost = 0.0;
                    }
                    cost += item.reduceCost;
                    reduceCost.put(item.appName, cost);
                }

                JsonArray reduceArr = new JsonArray();
                for (String key : reduceCost.keySet()) {
                    JsonObject one = new JsonObject();
                    one.addProperty("appName", key);
                    one.addProperty("cost", Utils.trimDouble(reduceCost.get(key)));
                    reduceArr.add(one);
                }

                JsonObject yesterdayData = new JsonObject();
                long yesterdayCount = 0;
                double totalSpend = 0;
                double totalInstalled = 0;
                sql = "select count(id) as cnt from ad_campaigns where create_time between '" + yesterdayStart + "' and '" + yesterdayEnd + "' and success=1";
                record = DB.findOneBySql(sql);
                if (record.hasObjectData()) {
                    yesterdayCount += (long)record.get("cnt");
                }
                sql = "select count(id) as cnt from ad_campaigns_admob where create_time between '" + yesterdayStart + "' and '" + yesterdayEnd + "' and success=1";
                record = DB.findOneBySql(sql);
                if (record.hasObjectData()) {
                    yesterdayCount += (long)record.get("cnt");
                }

                sql = "select campaign_id from ad_campaigns where create_time between '" + yesterdayStart + "' and '" + yesterdayEnd + "' and success=1";
                List<JSObject> campaignIds = DB.findListBySql(sql);
                String str = "";
                for (int i = 0; i < campaignIds.size(); i++) {
                    if (i == campaignIds.size() - 1) {
                        str += campaignIds.get(i).get("campaign_id").toString();
                    } else {
                        str += campaignIds.get(i).get("campaign_id").toString() + ",";
                    }
                }

                sql = "select sum(total_spend) as total_spend, sum(total_installed) as total_intalled from web_ad_campaigns_history " +
                        "where date between '" + yesterdayStart + "' and '" + yesterdayEnd + "' and campaign_id in (" + str + ")";
                record = DB.findOneBySql(sql);
                totalSpend += Utils.convertDouble(record.get("total_spend"), 0);
                totalInstalled += Utils.convertDouble(record.get("total_intalled"), 0);

                sql = "select count(id) as cnt from ad_campaigns_admob where create_time between '" + yesterdayEnd + "' and '" + yesterdayEnd + "' and success=1";
                record = DB.findOneBySql(sql);
                if (record.hasObjectData()) {
                    yesterdayCount += (long)record.get("cnt");
                }
                sql = "select campaign_id from ad_campaigns_admob where create_time between '" + yesterdayStart + "' and '" + yesterdayEnd + "' and success=1";
                campaignIds = DB.findListBySql(sql);
                str = "";
                for (int i = 0; i < campaignIds.size(); i++) {
                    if (i == campaignIds.size() - 1) {
                        str += campaignIds.get(i).get("campaign_id").toString();
                    } else {
                        str += campaignIds.get(i).get("campaign_id").toString() + ",";
                    }
                }
                sql = "select sum(total_spend) as total_spend, sum(total_installed) as total_intalled from web_ad_campaigns_history_admob " +
                        "where date between '" + yesterdayStart + "' and '" + yesterdayEnd + "' and campaign_id in (" + str + ")";
                record = DB.findOneBySql(sql);
                totalSpend += Utils.convertDouble(record.get("total_spend"), 0);
                totalInstalled += Utils.convertDouble(record.get("total_intalled"), 0);

                yesterdayData.addProperty("count", yesterdayCount);
                yesterdayData.addProperty("total_spend", totalSpend);
                yesterdayData.addProperty("total_installed", totalInstalled);

                String[] fields = {"id", "campaign_name", "failed_count", "last_error_message"};
                JsonArray array = new JsonArray();

                List<JSObject> list = DB.scan("ad_campaigns").select(fields)
                        .where(DB.filter().whereEqualTo("success", 0)).execute();
                for (int i = 0; i < list.size(); i++) {
                    JsonObject one = new JsonObject();
                    for (int j = 0; j < fields.length; j++) {
                        one.addProperty(fields[j], list.get(i).get(fields[j]).toString());
                    }
                    one.addProperty("network", "Facebook");
                    array.add(one);
                }

                list = DB.scan("ad_campaigns_admob").select(fields)
                        .where(DB.filter().whereEqualTo("success", 0)).execute();
                for (int i = 0; i < list.size(); i++) {
                    JsonObject one = new JsonObject();
                    for (int j = 0; j < fields.length; j++) {
                        one.addProperty(fields[j], list.get(i).get(fields[j]).toString());
                    }
                    one.addProperty("network", "AdWords");
                    array.add(one);
                }
                json.addProperty("today_create_count", count);
                json.add("data", array);
                json.add("yesterdayData", yesterdayData);
                json.add("reduceArr", reduceArr);
                json.addProperty("ret", 1);
            } catch (Exception ex) {
                ex.printStackTrace();
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
                            one.addProperty(key, (String)value);
                        } else if (value instanceof Integer) {
                            one.addProperty(key, (Integer)value);
                        } else if (value instanceof Long) {
                            one.addProperty(key, (Long)value);
                        } else if (value instanceof Double) {
                            one.addProperty(key, Utils.trimDouble((Double)value));
                        } else {
                            one.addProperty(key, value.toString());
                        }
                    }
                    List<String> tags = Campaign.bindTags(data.get(i).get("campaign_id"));
                    String tagStr = "";
                    for (int ii = 0; ii < tags.size(); ii++) {
                        tagStr += (tags.get(ii) + ",");
                    }
                    if (tagStr.length() > 0) {
                        tagStr = tagStr.substring(0, tagStr.length() - 1);
                    }
                    double installed = Utils.convertDouble(one.get("total_installed").getAsDouble(), 0);
                    double click = Utils.convertDouble(one.get("total_click").getAsDouble(), 0);
                    double cvr = click > 0 ? installed / click : 0;
                    one.addProperty("cvr", Utils.trimDouble(cvr));
                    one.addProperty("tagStr", tagStr);
                    array.add(one);

                }
                json.add("data", array);
            }
        } else if (path.startsWith("/selectCampaingnWhereNotExistTag")) {
            JsonArray array = new JsonArray();
            String sqlCampaignIds = "select campaign_id from web_ad_campaign_tag_rel";
            
            String sqlAll = "select campaign_id from web_ad_campaigns";
            List<JSObject> data = new ArrayList<>();
            try {
                List<JSObject> campaignIdsList = DB.findListBySql(sqlCampaignIds);
                Set<String>  campaignIdsSet = new HashSet<>();
                for(JSObject j : campaignIdsList){
                    campaignIdsSet.add(j.get("campaign_id"));
                }
                List<JSObject> allList = DB.findListBySql(sqlAll);
                Set<String>  allSet = new HashSet<>();
                for(JSObject k : allList){
                    allSet.add(k.get("campaign_id"));
                }
                List<String> diffList = Utils.getDiffrentStrList(allSet, campaignIdsSet);
                String allStr = "";
                for(String j : diffList){
                    allStr += j + ",";
                }
                allStr = allStr.substring(0,allStr.length()-1);
                String sqlFilterAll = "select id,campaign_id,adset_id,account_id,campaign_name,create_time,status,budget,bidding," +
                        "total_spend,total_click,total_installed,cpa,ctr,effective_status from web_ad_campaigns where campaign_id in (" + allStr + ")";
                data = DB.findListBySql(sqlFilterAll);
                if(data != null){
                    for (int i = 0,len = data.size(); i < len; i++) {
                        JsonObject one = new JsonObject();
                        Set<String> keySet = data.get(i).getKeys();
                        for (String key : keySet) {
                            Object value = data.get(i).get(key);
                            if (value instanceof String) {
                                one.addProperty(key, (String)value);
                            } else if (value instanceof Integer) {
                                one.addProperty(key, (Integer)value);
                            } else if (value instanceof Long) {
                                one.addProperty(key, (Long)value);
                            } else if (value instanceof Double) {
                                one.addProperty(key, Utils.trimDouble((Double)value));
                            } else {
                                one.addProperty(key, value.toString());
                            }
                        }
                        double installed = Utils.convertDouble(one.get("total_installed").getAsDouble(), 0);
                        double click = Utils.convertDouble(one.get("total_click").getAsDouble(), 0);
                        double cvr = click > 0 ? installed / click : 0;
                        one.addProperty("cvr", Utils.trimDouble(cvr));
                        //one.addProperty("tagStr", "_");
                        array.add(one);

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            json.addProperty("ret", 1);
            json.add("data", array);
        }else if (path.startsWith("/find_create_data")) {
            String id = request.getParameter("campaignId");
            try {
                JSObject record = DB.simpleScan("ad_campaigns").select("id", "app_name", "campaign_id", "facebook_app_id", "account_id", "country_region",
                        "language", "campaign_name", "page_id", "bugdet", "bidding", "title", "message", "tag_name", "age", "gender", "detail_target", "max_cpa")
                        .where(DB.filter().whereEqualTo("campaign_id", id)).execute();
                if (record.hasObjectData()) {
                    json.addProperty("ret", 1);
                    JsonObject one = new JsonObject();
                    Set<String> keySet = record.getKeys();
                    for (String key : keySet) {
                        Object value = record.get(key);
                        if (value instanceof String) {
                            one.addProperty(key, (String)value);
                        } else if (value instanceof Integer) {
                            one.addProperty(key, (Integer)value);
                        } else if (value instanceof Long) {
                            one.addProperty(key, (Long)value);
                        } else if (value instanceof Double) {
                            one.addProperty(key, Utils.trimDouble((Double)value));
                        } else {
                            one.addProperty(key, value.toString());
                        }
                    }
                    json.add("data", one);
                } else {
                    json.addProperty("ret", 0);
                    json.addProperty("message", "没有找到创建数据");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (path.startsWith("/batch_change")) {
            try {
                String data = request.getParameter("data");
                String  appName = request.getParameter("appName");

                JsonParser parser = new JsonParser();
                Gson gson = new Gson();
                JsonArray array = parser.parse(data).getAsJsonArray();
                String now = DateUtil.getNow();

                for (int i = 0; i < array.size(); i++) {
                    BatchChangeItem item = gson.fromJson(array.get(i), BatchChangeItem.class);
                    String sql = "";


                    JSObject record = DB.simpleScan("web_ad_batch_change_campaigns")
                            .select("id").where(DB.filter().whereEqualTo("campaign_id", item.campaignId))
                            .and(DB.filter().whereEqualTo("success", 0)).execute();

                    String sqlNetwork = "";
                    String sqlCountry = "";
                    if("admob".equals(item.network)){
                        sqlNetwork = "select campaign_name,budget,bidding from web_ad_campaigns_admob where campaign_id = '"+item.campaignId+"'";
                        sqlCountry = "select DISTINCT country_code from web_ad_campaigns_country_history_admob where campaign_id = '"+item.campaignId+"'";
                    }else if("facebook".equals(item.network)){
                        sqlNetwork = "select campaign_name,budget,bidding from web_ad_campaigns where campaign_id = '"+item.campaignId+"'";
                        sqlCountry = "select DISTINCT country_code from web_ad_campaigns_country_history where campaign_id = '"+item.campaignId+"'";
                    }
                    JSObject one = DB.findOneBySql(sqlNetwork);
                    List<JSObject> countryJSObjectList = DB.findListBySql(sqlCountry);
                    int enabled = -1;
                    if (item.enabled != null) {
                        enabled = item.enabled ? 1 : 0;
                    }
                    if (enabled == 1) item.excludedCountry = null;
                    if (record.hasObjectData() && (item.excludedCountry == null || item.excludedCountry.isEmpty())) {
                        long id = record.get("id");
                        DB.update("web_ad_batch_change_campaigns")
                                .put("enabled", enabled)
                                .put("bugdet", item.budget)
                                .put("bidding", item.bidding)
                                .put("network", item.network)
                                .put("account_id", item.accountId)
                                .put("campaign_name", item.campaignName != null ? item.campaignName : "")
                                .put("excluded_country", item.excludedCountry != null ? item.excludedCountry : "")
                                .put("create_time", now)
                                .put("success", 0)
                                .where(DB.filter().whereEqualTo("id", id))
                                .execute();
                    } else {
                        DB.insert("web_ad_batch_change_campaigns")
                                .put("enabled", enabled)
                                .put("bugdet", item.budget)
                                .put("bidding", item.bidding)
                                .put("network", item.network)
                                .put("account_id", item.accountId)
                                .put("campaign_id", item.campaignId)
                                .put("campaign_name", item.campaignName != null ? item.campaignName : "")
                                .put("excluded_country", item.excludedCountry != null ? item.excludedCountry : "")
                                .put("create_time", now)
                                .put("success", 0)
                                .execute();
                    }

                    String budgetStr = "";
                    String biddingStr = "";
                    if(record.hasObjectData()){
                        double bugdet = one.get("budget");
                        bugdet = bugdet / 100;
                        double bidding = one.get("bidding");
                        bidding = bidding / 100;
                        if(bugdet  < item.budget){
                            budgetStr = "预算上升；";
                        }
                        if(bugdet > item.budget){
                            budgetStr = "预算下降；";
                        }
                        if(bidding < item.bidding){
                            biddingStr = "竞价上升；";
                        }
                        if(bidding > item.bidding){
                            biddingStr = "竞价下降；";
                        }
                        for(JSObject js : countryJSObjectList){
                            DB.insert("web_ad_campaign_operation_log")
                                    .put("operation_date", now)
                                    .put("app_name",appName)
                                    .put("country_code",js.get("country_code"))
                                    .put("campaign_id",item.campaignId)
                                    .put("campaign_name", one.get("campaign_name"))
                                    .put("enabled",enabled)
                                    .put("details_text",budgetStr + biddingStr)
                                    .execute();
                        }

                    }
                }
                json.addProperty("ret", 1);
            } catch (Exception ex) {
                ex.printStackTrace();
                json.addProperty("ret", 0);
                json.addProperty("message", ex.getMessage());
            }
        }else if (path.startsWith("/selectFacebookMessage")) {
            String appName = request.getParameter("appName");
            String language = request.getParameter("language");
            try {
                String sql = "select title,message from web_ad_descript_dict where app_name = '" + appName + "' and language = '" + language +"' limit 1";
                JSObject titleMessage = new JSObject();
                try {
                    titleMessage = DB.findOneBySql(sql);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                json.addProperty("title",(String)(titleMessage.get("title")));
                json.addProperty("message",(String)(titleMessage.get("message")));
                json.addProperty("language", language);
                json.addProperty("ret", 1);

                } catch (Exception e) {
                    e.printStackTrace();
                }
        }else if (path.startsWith("/selectTitleMessageByRegion")) {
            String region = request.getParameter("region");
            String appName = request.getParameter("appName");
            String language = "";
            if (region != null) {
                Map<String, String> regionLanguageRelMap = Config.getRegionLanguageRelMap();
                String[] regionArray = region.split(",");
                Set<String> languageSet = new HashSet<>();

                for (int i=0,len = regionArray.length;i<len;i++){
                    languageSet.add(regionLanguageRelMap.get(regionArray[i]));
                }

                if(languageSet.size() == 1){
                    language = regionLanguageRelMap.get(regionArray[0]);
                }else{
                    language = "English";
                }
                String sql = "select title,message from web_ad_descript_dict where app_name = '" + appName + "' and language = '" + language +"' limit 1";

                JSObject titleMessage = new JSObject();
                try {
                    titleMessage = DB.findOneBySql(sql);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                json.addProperty("title",(String)(titleMessage.get("title")));
                json.addProperty("message",(String)(titleMessage.get("message")));
                json.addProperty("ret", 1);
            }
        }

        response.getWriter().write(json.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    class BatchChangeItem {
        public Boolean enabled;
        public String network;
        public String campaignId;
        public String accountId;
        public String campaignName;
        public double budget;
        public double bidding;
        public String excludedCountry;
        public int success;
    }

    class ReduceCostItem {
        public String campaignId;
        public String appName;
        public int enabled = -1;
        public double reduceCost;

        public Set<String> countryRemoved = new HashSet<>();
        public Set<String> countryExcluded = new HashSet<>();
    }

    private OperationResult updateCampaign(String id, String campaignName, String status, String budget, String bidding, String tags) {
        OperationResult ret = new OperationResult();

        try {
            JSObject campaign = DB.simpleScan("web_ad_campaigns").select("id", "campaign_id", "adset_id", "campaign_name", "status", "budget", "bidding").where(DB.filter().whereEqualTo("id", Utils.parseInt(id, 0))).execute();
            if (campaign.hasObjectData()) {
                String campaign_id = campaign.get("campaign_id");
                String adset_id = campaign.get("adset_id");
                String oldCampaignName = campaign.get("campaign_name");
                String oldStatus = campaign.get("status");
                double oldBudget = (double)campaign.get("budget") / 100;
                double oldBidding = (double)campaign.get("bidding")/ 100;

                if (!oldCampaignName.equals(campaignName) || !oldStatus.equals(status) || oldBudget != Utils.parseDouble(budget, 0) || oldBidding != Utils.parseDouble(bidding, 0)) {
                    JSObject record = DB.simpleScan("web_system_config").select("config_value").where(DB.filter().whereEqualTo("config_key", "adtools_path")).execute();
                    if (record.hasObjectData()) {
                        String jarPath = record.get("config_value");
                        String cmd = "java -jar " + jarPath + " -update ";
                        if (!oldCampaignName.equals(campaignName) || !oldStatus.equals(status)) {
                            cmd += "-campaign_id " + campaign_id + " ";
                            if (!oldCampaignName.equals(campaignName))
                                cmd += "-campaign_name \"" + campaignName + "\" ";
                            if (!oldStatus.equals(status)) {
                                cmd += "-status \"" + status + "\" ";
                            }
                        }

                        if (oldBudget != Utils.parseDouble(budget, 0) || oldBidding != Utils.parseDouble(bidding, 0)) {
                            cmd += "-adset_id " + adset_id + " ";
                            if (oldBudget != Utils.parseDouble(budget, 0))
                                cmd += "-budget \"" + budget + "\" ";
                            if (oldBidding != Utils.parseDouble(bidding, 0))
                                cmd += "-bidding \"" + bidding + "\" ";
                        }

                        Logger logger = Logger.getRootLogger();
                        logger.debug("update campaign: " + cmd);

                        Process process = Runtime.getRuntime().exec(cmd);
                        InputStream is = process.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                        String lines = "";
                        String line = reader.readLine();
                        while (line != null) {
                            lines += line + "\n";
                            line = reader.readLine();
                        }
                        process.waitFor();
                        int code = process.exitValue();
                        if (code == 0) {
                            DB.update("web_ad_campaigns")
                                    .put("campaign_name", campaignName)
                                    .put("status", status)
                                    .put("budget", Utils.parseDouble(budget, 0) * 100)
                                    .put("bidding", Utils.parseDouble(bidding, 0) * 100)
                                    .where(DB.filter().whereEqualTo("id", Utils.parseInt(id, 0)))
                                    .execute();
                        } else  {
                            ret.result = false;
                            ret.message = cmd + "\n" + lines;
                            return ret;
                        }
                    }
                }

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
                        DB.delete("web_ad_campaign_tag_rel")
                                .where(DB.filter().whereEqualTo("tag_id", tagId))
                                .and(DB.filter().whereEqualTo("campaign_id", campaignId)).execute();
                    }
                    for (String tagName : newList) {
                        long tagId = tagIds.get(tagName);
                        DB.insert("web_ad_campaign_tag_rel").put("tag_id", tagId)
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
            return DB.scan("web_ad_campaigns").select("id", "campaign_id", "adset_id", "account_id", "campaign_name", "create_time",
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
            return DB.scan("web_ad_campaigns").select("id", "campaign_id", "adset_id", "account_id", "campaign_name", "create_time",
                    "status", "budget", "bidding", "total_spend", "total_installed", "total_click", "cpa", "ctr")
                    .limit(size).start(index * size).orderByAsc("id").execute();
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return list;
    }

    public static long count() {
        try {
            JSObject object = DB.simpleScan("web_ad_campaigns").select(DB.func(DB.COUNT, "id")).execute();
            return object.get("count(id)");
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return 0;
    }

    private static List<JSObject> findBindTags(String campaignId) {
        ArrayList<JSObject> retList = new ArrayList<>();
        String sql = "select tag_id, tag_name from web_ad_campaign_tag_rel,web_tag where web_tag.id=web_ad_campaign_tag_rel.tag_id and campaign_id=?";
        try {
            return DB.findListBySql(sql, campaignId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retList;
    }

    public static List<String> bindTags(String campaignId) {
        ArrayList<String> retList = new ArrayList<>();
        String sql = "select tag_name from web_ad_campaign_tag_rel,web_tag where web_tag.id=web_ad_campaign_tag_rel.tag_id and campaign_id=?";
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
}

