package com.bestgo.admanager.servlet;

import com.bestgo.admanager.OperationResult;
import com.bestgo.admanager.constant.JedisConstant;
import com.bestgo.admanager.utils.*;
import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
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

/**
 * Created by jikai on 12/10/17.
 * 自动创建系列
 */
@WebServlet(name = "AutoCreateCampaign", urlPatterns = "/auto_create_campaign/*")
public class AutoCreateCampaign extends BaseHttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doPost(request, response);
        if (!Utils.isAdmin(request, response)) return;
        Jedis jedis = JedisPoolUtil.getJedis();
        String path = request.getPathInfo();
        JsonObject json = new JsonObject();

        OperationResult result = null;
        if (path.startsWith("/facebook")) {
            String method = path.replace("/facebook", "");
            switch (method) {
                case "/update_bidding":
                    result = facebookCampaignBiddingBatchUpdate(request);
                    break;
                case "/create":
                    result = facebookCampaignCreate(request,jedis);
                    break;
                case "/create2":
                    result = facebookCampaignCreate2(request,jedis);
                    break;
                case "/delete":
                    result = facebookCampaignDelete(request);
                    break;
                case "/enable":
                    result = facebookCampaignEnable(request);
                    break;
                case "/modify":
                    result = facebookCampaignUpdate(request);
                    break;
                case "/query":
                    String word = request.getParameter("word");
                    String tagName = request.getParameter("tagName"); //<input type="text">框内没有值时，默认有null值
                    String countryName = request.getParameter("country");  //而jQuery.ui 的 autocomplete()方法，当框内没有值时，默认""
                    JsonArray array = new JsonArray();
                    List<JSObject> data = facebookFetchData(word, tagName, countryName);
                    if (data.size() > 0) {
                        json.addProperty("ret", 1);
                        for (int i = 0; i < data.size(); i++) {
                            JsonObject one = new JsonObject();
                            Set<String> keySet = data.get(i).getKeys();
                            String value0 = "";
                            String[] account_ids;
                            for (String key : keySet) {
                                Object value = data.get(i).get(key);
                                if (value instanceof String) {
                                    one.addProperty(key, (String) value);
                                } else if (value instanceof Integer) {
                                    one.addProperty(key, (Integer) value);
                                } else if (value instanceof Long) {
                                    one.addProperty(key, (Long) value);
                                } else if (value instanceof Double) {
                                    one.addProperty(key, NumberUtil.trimDouble((Double) value, 4));
                                } else {
                                    one.addProperty(key, value.toString());
                                }
                                if ("account_id".equalsIgnoreCase(key)) {
                                    value0 = (String) value;
                                    account_ids = value0.split(",");

                                    String[] account_names = new String[account_ids.length];

                                    for (int j = 0; j < account_ids.length; j++) {
                                        account_names[j] = AdAccount.accountIdNameRelationMap.get(account_ids[j]);
                                    }
//                                     String.join(",", account_names);
                                    one.addProperty(key, String.join(",", account_names));
                                }
                                if ("bid_strategy".equalsIgnoreCase(key)) {
                                    one.addProperty(key, (int) value == 1 ? "TARGET_COST" : "LOWEST_COST_WITH_BID_CAP");
                                }
                                if ("mode_type".equalsIgnoreCase(key)) {
                                    one.addProperty(key, (int) value == 0 ? "系统创建" : "次日留用");
                                }
                            }
                            array.add(one);
                        }
                        json.add("data", array);
                        result = new OperationResult();
                        result.result = true;
                        result.message = "执行成功";
                    }else {
                        result = new OperationResult();
                        result.result = false;
                        result.message = "无符合条件的数据！！";
                    }
                    break;
                case "/query_by_id":
                    String id = request.getParameter("id");
                    JSObject one = facebookFetchById(id);
                    if (one != null && one.hasObjectData()) {
                        JsonObject jsonObject = new JsonObject();
                        Set<String> keySet = one.getKeys();
                        for (String key : keySet) {
                            Object value = one.get(key);
                            if (value instanceof String) {
                                jsonObject.addProperty(key, (String) value);
                            } else if (value instanceof Integer) {
                                jsonObject.addProperty(key, (Integer) value);
                            } else if (value instanceof Long) {
                                jsonObject.addProperty(key, (Long) value);
                            } else if (value instanceof Double) {
                                jsonObject.addProperty(key, NumberUtil.trimDouble((Double) value, 4));
                            } else {
                                jsonObject.addProperty(key, value.toString());
                            }
                        }
                        json.add("data", jsonObject);
                        result = new OperationResult();
                        result.result = true;
                        result.message = "执行成功";
                    }
                    break;
            }
        } else if (path.startsWith("/adwords")) {
            String method = path.replace("/adwords", "");
            switch (method) {
                case "/update_bidding":
                    result = adwordsCampaignBiddingBatchUpdate(request);
                    break;
                case "/create":
                    result = adwordsCampaignCreate(request,jedis);
                    break;
                case "/create2":
                    result = adwordsCampaignCreate2(request,jedis);
                    break;
                case "/delete":
                    result = adwordsCampaignDelete(request);
                    break;
                case "/enable":
                    result = adwordsCampaignEnable(request);
                    break;
                case "/modify":
                    result = adwordsCampaignUpdate(request);
                    break;
                case "/query":
                    String word = request.getParameter("word");
                    String tagName = request.getParameter("tagName");
                    String countryName = request.getParameter("country");
                    JsonArray array = new JsonArray();
                    List<JSObject> data = adwordsFetchData(word, tagName, countryName);
                    json.addProperty("ret", 1);
                    for (int i = 0; i < data.size(); i++) {
                        JsonObject one = new JsonObject();
                        Set<String> keySet = data.get(i).getKeys();
                        String value0 = "";
                        String[] account_ids;
                        for (String key : keySet) {
                            Object value = data.get(i).get(key);
                            if (value instanceof String) {
                                one.addProperty(key, (String) value);
                            } else if (value instanceof Integer) {
                                one.addProperty(key, (Integer) value);
                            } else if (value instanceof Long) {
                                one.addProperty(key, (Long) value);
                            } else if (value instanceof Double) {
                                one.addProperty(key, NumberUtil.trimDouble((Double) value, 4));
                            } else {
                                one.addProperty(key, value.toString());
                            }

                            if ("account_id".equalsIgnoreCase(key)) {
                                value0 = (String) value;
                                account_ids = value0.split(",");

                                String[] account_names = new String[account_ids.length];

                                for (int j = 0; j < account_ids.length; j++) {
                                    account_names[j] = AdAccountAdmob.accountIdNameAdmobRelationMap.get(account_ids[j]);
                                }
//                                     String.join(",", account_names);
                                one.addProperty(key, String.join(",", account_names));
                            }

                            if ("mode_type".equalsIgnoreCase(key)) {
                                one.addProperty(key, (int) value == 0 ? "系统创建" : "次日留用");
                            }
                        }

                        array.add(one);
                    }
                    json.add("data", array);
                    result = new OperationResult();
                    result.result = true;
                    result.message = "执行成功";
                    break;
                case "/query_by_id":
                    String id = request.getParameter("id");
                    JSObject one = adwordsFetchById(id);
                    if (one != null && one.hasObjectData()) {
                        JsonObject jsonObject = new JsonObject();
                        Set<String> keySet = one.getKeys();
                        for (String key : keySet) {
                            Object value = one.get(key);
                            if (value instanceof String) {
                                jsonObject.addProperty(key, (String) value);
                            } else if (value instanceof Integer) {
                                jsonObject.addProperty(key, (Integer) value);
                            } else if (value instanceof Long) {
                                jsonObject.addProperty(key, (Long) value);
                            } else if (value instanceof Double) {
                                jsonObject.addProperty(key, NumberUtil.trimDouble((Double) value, 4));
                            } else {
                                jsonObject.addProperty(key, value.toString());
                            }
                        }
                        json.add("data", jsonObject);
                        result = new OperationResult();
                        result.result = true;
                        result.message = "执行成功";
                    }
                    break;
            }
        }
        if (result != null) {
            json.addProperty("ret", result.result ? 1 : 0);
            json.addProperty("message", result.message);
        } else {
            json.addProperty("ret", 0);
            json.addProperty("message", "执行失败");
        }
        //将jedis连接释放，回收到连接池
        if (jedis != null) {
            jedis.close();
        }
        response.getWriter().write(json.toString());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }


    static String[] FB_CAMPAIGN_FIELDS = {"id", "app_name", "create_count", "account_id", "country_region", "explode_country",
            "excluded_region", "language", "age", "explode_age", "gender", "explode_gender",
            "detail_target", "user_os", "user_devices", "campaign_name", "bugdet", "bidding",
            "explode_bidding", "max_cpa", "title", "message", "image_path", "create_time",
            "update_time", "enabled", "video_path", "group_id", "publisher_platforms", "bid_strategy", "page_id", "mode_type"};

    public static JSObject facebookFetchById(String id) {
        try {
            return DB.simpleScan("ad_campaigns_auto_create").select(FB_CAMPAIGN_FIELDS)
                    .where(DB.filter().whereEqualTo("id", id)).execute();
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }

    public static List<JSObject> facebookFetchData(String word, String tagName, String countryName) {
        List<JSObject> list = new ArrayList<>();
        try {
            if (StringUtil.isNotEmpty(tagName) && countryName.isEmpty() && (StringUtil.isEmpty(word))) {
                return DB.scan("ad_campaigns_auto_create").select(FB_CAMPAIGN_FIELDS)
                        .where(DB.filter().whereEqualTo("app_name", tagName)).orderByAsc("id").execute();
            } else if (StringUtil.isNotEmpty(tagName) && StringUtil.isNotEmpty(countryName) && (StringUtil.isEmpty(word))) {
                return DB.scan("ad_campaigns_auto_create").select(FB_CAMPAIGN_FIELDS)
                        .where(DB.filter().whereEqualTo("app_name", tagName))
                        .and(DB.filter().whereEqualTo("country_region", countryName))
                        .orderByAsc("id").execute();
            } else if (StringUtil.isNotEmpty(tagName) && StringUtil.isNotEmpty(word) && countryName.isEmpty()) {
                return DB.scan("ad_campaigns_auto_create").select(FB_CAMPAIGN_FIELDS)
                        .where(DB.filter().whereEqualTo("app_name", tagName))
                        .and(DB.filter().whereLikeTo("campaign_name", "%" + word + "%"))
                        .orderByAsc("id").execute();
            } else if (StringUtil.isNotEmpty(tagName) && StringUtil.isNotEmpty(word) && StringUtil.isNotEmpty(countryName)) {
                return DB.scan("ad_campaigns_auto_create").select(FB_CAMPAIGN_FIELDS)
                        .where(DB.filter().whereEqualTo("app_name", tagName))
                        .and(DB.filter().whereLikeTo("campaign_name", "%" + word + "%"))
                        .and(DB.filter().whereEqualTo("country_region", countryName))
                        .orderByAsc("id").execute();
            }

        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return list;
    }

    public static List<JSObject> facebookFetchData(int index, int size) {
        List<JSObject> list = new ArrayList<>();
        try {
            return DB.scan("ad_campaigns_auto_create").select(FB_CAMPAIGN_FIELDS)
                    .limit(size).start(index * size).orderByAsc("id").execute();
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return list;
    }

    public static long facebookCount() {
        try {
            JSObject object = DB.simpleScan("ad_campaigns_auto_create").select(DB.func(DB.COUNT, "id")).execute();
            return object.get("count(id)");
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return 0;
    }

    private OperationResult facebookCampaignBiddingBatchUpdate(HttpServletRequest request) {
        OperationResult result = new OperationResult();
        JsonParser parser = new JsonParser();
        String bidding_array = request.getParameter("bidding_array");
        JsonArray bidding_JsonArray = parser.parse(bidding_array).getAsJsonArray();
        try {
            for (int i = 0; i < bidding_JsonArray.size(); i++) {
                JsonObject j = bidding_JsonArray.get(i).getAsJsonObject();
                String id = j.get("id").getAsString();
                String bidding = j.get("bidding").getAsString();
                DB.update("ad_campaigns_auto_create")
                        .put("bidding", bidding)
                        .where(DB.filter().whereEqualTo("id", id))  //这里 id 可是一个String 类型啊！
                        .execute();
            }
            result.result = true;
            result.message = "批量更新成功";
        } catch (Exception e) {
            result.result = false;
            result.message = e.getMessage();
        }
        return result;
    }

    private OperationResult facebookCampaignCreate(HttpServletRequest request,Jedis jedis) {
        OperationResult result = new OperationResult();
        try {
            String appName = request.getParameter("appName");
            String accountName = request.getParameter("accountName");
            String accountId = request.getParameter("accountId");
            String createCount = request.getParameter("createCount");
            String excludedRegion = request.getParameter("excludedRegion");
            String language = request.getParameter("language");
            String interest = request.getParameter("interest");
            String userOs = request.getParameter("userOs");
            String userDevice = request.getParameter("userDevice");
            String campaignName = request.getParameter("campaignName");
            String bugdet = request.getParameter("bugdet");
            String maxCPA = request.getParameter("maxCPA");
            String groupId = request.getParameter("groupId");
            String title = request.getParameter("title");
            String message = request.getParameter("message");
            String region = request.getParameter("region");
            String age = request.getParameter("age");
            String gender = request.getParameter("gender");
            String bidding = request.getParameter("bidding");
            String explodeCountry = request.getParameter("explodeCountry");
            String explodeAge = request.getParameter("explodeAge");
            String explodeGender = request.getParameter("explodeGender");
            String explodeBidding = request.getParameter("explodeBidding");
            String identification = request.getParameter("identification");
            String materialPath = request.getParameter("materialPath");
            String publisherPlatforms = request.getParameter("publisherPlatforms");
            String bidStrategy = request.getParameter("bidStrategy");
            String pageId = request.getParameter("FBpage[pageId]");
            String imagePath = new String();
            String videoPath = new String();
            if (identification.equals("image")) {
                imagePath = materialPath;
                videoPath = "";
            } else if (identification.equals("video")) {
                videoPath = materialPath;
                imagePath = "";
            }

            result.result = false;
            File imagesPath = null;
            File videosPath = null;
            Collection<File> uploadImages = null;
            Collection<File> uploadVideos = null;
            if (createCount.isEmpty()) {
                result.message = "创建数量不能为空";
            } else if (title.isEmpty()) {
                result.message = "标题不能为空";
            } else if (message.isEmpty()) {
                result.message = "广告语不能为空";
            } else if (campaignName.isEmpty()) {
                result.message = "广告系列名称不能为空";
            } else if (bugdet.isEmpty()) {
                result.message = "预算不能为空";
            } else if (bidding.isEmpty()) {
                result.message = "出价不能为空";
            } else if (gender == null) {
                result.message = "性别不能为空";
            } else if (region.isEmpty()) {
                result.message = "国家不能为空";
            } else if (publisherPlatforms.isEmpty()) {
                result.message = "版位不能为空";
            } else {
                double dBidding = NumberUtil.parseDouble(bidding, 0);
                double maxBidding = 0.1;
                String maxBiddingStr = jedis.hget(JedisConstant.TAG_NAME_BIDDING_MAP, appName);
                if (maxBiddingStr == null) {
                    JSObject one = DB.findOneBySql("select max_bidding from web_tag where tag_name = '" + appName + "'");
                    if (one.hasObjectData()) {
                        maxBidding = NumberUtil.convertDouble(one.get("max_bidding"), 0);
                        jedis.hset(JedisConstant.TAG_NAME_BIDDING_MAP, appName, maxBidding + "");
                    }
                } else {
                    maxBidding = NumberUtil.parseDouble(maxBiddingStr, 0);
                }
                if (dBidding > maxBidding) {
                    result.message = "bidding超过了本应用的最大出价,   " + bidding + " > " + maxBidding;
                } else {
                    //路径检查
                    if (!imagePath.isEmpty()) {
                        JSObject record = DB.simpleScan("web_system_config").select("config_value").where(DB.filter().whereEqualTo("config_key", "fb_image_path")).execute();
                        String imageRoot = null;
                        if (record.hasObjectData()) {
                            imageRoot = record.get("config_value");
                        }
                        imagesPath = new File(imageRoot + File.separatorChar + imagePath);
                        if (imagesPath.exists()) {
                            uploadImages = FileUtils.listFiles(imagesPath, null, false);
                            if (uploadImages != null && uploadImages.size() == 1) {
                                result.result = true;
                            } else {
                                result.message = "创建失败，每个系列必须而且只能上传一张图片";
                            }
                        } else {
                            result.message = "图片路径不存在";
                        }
                    } else if (!videoPath.isEmpty()) {
                        JSObject record = DB.simpleScan("web_system_config").select("config_value").where(DB.filter().whereEqualTo("config_key", "fb_video_path")).execute();
                        String videoRoot = null;
                        if (record.hasObjectData()) {
                            videoRoot = record.get("config_value");
                        }
                        videosPath = new File(videoRoot + File.separatorChar + videoPath);
                        if (videosPath.exists()) {
                            uploadVideos = FileUtils.listFiles(videosPath, null, false);
                            if (uploadVideos != null && uploadVideos.size() == 2) {
                                result.result = true;
                            } else {
                                result.message = "创建失败，每个系列必须而且只能上传一个视频和一个缩略图";
                            }
                        } else {
                            result.message = "视频路径不存在";
                        }
                    } else {
                        result.message = "图片或视频路径二选一，不能为空！";
                    }
                }
            }

            if (result.result) {
                campaignName = campaignName + "_Strategy" + bidStrategy;
                if (campaignName.length() > 150) {
                    campaignName = campaignName.substring(0, 150);
                }
                interest = (interest == null) ? "" : interest;
                userOs = (userOs == null) ? "" : userOs;
                userDevice = (userDevice == null) ? "" : userDevice;
                boolean record = DB.insert("ad_campaigns_auto_create")
                        .put("app_name", appName)
                        .put("create_count", NumberUtil.parseInt(createCount, 0))
                        .put("account_id", accountId)
                        .put("country_region", region)
                        .put("explode_country", Boolean.parseBoolean(explodeCountry) ? 1 : 0)
                        .put("excluded_region", excludedRegion)
                        .put("language", language)
                        .put("age", age)
                        .put("explode_age", Boolean.parseBoolean(explodeAge) ? 1 : 0)
                        .put("gender", gender)
                        .put("explode_gender", Boolean.parseBoolean(explodeGender) ? 1 : 0)
                        .put("detail_target", interest)
                        .put("user_os", userOs)
                        .put("user_devices", userDevice)
                        .put("campaign_name", campaignName)
                        .put("bugdet", bugdet)
                        .put("bidding", bidding)
                        .put("explode_bidding", Boolean.parseBoolean(explodeBidding) ? 1 : 0)
                        .put("max_cpa", maxCPA)
                        .put("group_id", groupId)
                        .put("title", title)
                        .put("message", message)
                        .put("image_path", imagePath)
                        .put("video_path", videoPath)
                        .put("create_time", DateUtil.getNowTime())
                        .put("publisher_platforms", publisherPlatforms)
                        .put("bid_strategy", bidStrategy)
                        .put("page_id", pageId)
                        .execute();
                if (record) {
                    result.result = true;
                    result.message = "创建成功";
                } else {
                    result.result = false;
                    result.message = "创建失败";
                }
            }
        } catch (Exception ex) {
            result.result = false;
            result.message = ex.getMessage();
        }
        return result;
    }

    private OperationResult facebookCampaignCreate2(HttpServletRequest request,Jedis jedis) {
        OperationResult result = new OperationResult();
        try {
            String flag = request.getParameter("flag");
            String appName = request.getParameter("appName");
            String accountName = request.getParameter("accountName");
            String accountId = request.getParameter("accountId");
            String createCount = request.getParameter("createCount");
            String excludedRegion = request.getParameter("excludedRegion");
            String language = request.getParameter("language");
            String interest = request.getParameter("interest");
            String userOs = request.getParameter("userOs");
            String userDevice = request.getParameter("userDevice");
            String campaignName = request.getParameter("campaignName");
            String bugdet = request.getParameter("bugdet");
            String maxCPA = request.getParameter("maxCPA");
            String groupId = request.getParameter("groupId");
            String title = request.getParameter("title");
            String message = request.getParameter("message");
            String region = request.getParameter("region");
            String age = request.getParameter("age");
            String gender = request.getParameter("gender");
            String bidding = request.getParameter("bidding");
            String explodeCountry = request.getParameter("explodeCountry");
            String explodeAge = request.getParameter("explodeAge");
            String explodeGender = request.getParameter("explodeGender");
            String explodeBidding = request.getParameter("explodeBidding");
            String identification = request.getParameter("identification");
            String materialPath = request.getParameter("materialPath");
            String publisherPlatforms = request.getParameter("publisherPlatforms");
            String bidStrategy = request.getParameter("bidStrategy");
            String pageId = request.getParameter("FBpage[pageId]");
            String imagePath = new String();
            String videoPath = new String();
            if (identification.equals("image")) {
                imagePath = materialPath;
                videoPath = "";
            } else if (identification.equals("video")) {
                videoPath = materialPath;
                imagePath = "";
            }

            result.result = false;
            File imagesPath = null;
            File videosPath = null;
            Collection<File> uploadImages = null;
            Collection<File> uploadVideos = null;
            if (createCount.isEmpty()) {
                result.message = "创建数量不能为空";
            } else if (title.isEmpty()) {
                result.message = "标题不能为空";
            } else if (message.isEmpty()) {
                result.message = "广告语不能为空";
            } else if (campaignName.isEmpty()) {
                result.message = "广告系列名称不能为空";
            } else if (bugdet.isEmpty()) {
                result.message = "预算不能为空";
            } else if (bidding.isEmpty()) {
                result.message = "出价不能为空";
            } else if (gender == null) {
                result.message = "性别不能为空";
            } else if (region.isEmpty()) {
                result.message = "国家不能为空";
            } else if (publisherPlatforms.isEmpty()) {
                result.message = "版位不能为空";
            } else {
                double dBidding = NumberUtil.parseDouble(bidding, 0);
                double maxBidding = 0.1;
                String maxBiddingStr = jedis.hget(JedisConstant.TAG_NAME_BIDDING_MAP, appName);
                if (maxBiddingStr == null) {
                    JSObject one = DB.findOneBySql("select max_bidding from web_tag where tag_name = '" + appName + "'");
                    if (one.hasObjectData()) {
                        maxBidding = NumberUtil.convertDouble(one.get("max_bidding"), 0);
                        jedis.hset(JedisConstant.TAG_NAME_BIDDING_MAP, appName, maxBidding + "");
                    }
                } else {
                    maxBidding = NumberUtil.parseDouble(maxBiddingStr, 0);
                }
                if (dBidding > maxBidding) {
                    result.message = "bidding超过了本应用的最大出价,   " + bidding + " > " + maxBidding;
                } else {
                    //路径检查
                    if (!imagePath.isEmpty()) {
                        JSObject record = DB.simpleScan("web_system_config").select("config_value").where(DB.filter().whereEqualTo("config_key", "fb_image_path")).execute();
                        String imageRoot = null;
                        if (record.hasObjectData()) {
                            imageRoot = record.get("config_value");
                        }
                        imagesPath = new File(imageRoot + File.separatorChar + imagePath);
                        if (imagesPath.exists()) {
                            uploadImages = FileUtils.listFiles(imagesPath, null, false);
                            if (uploadImages != null && uploadImages.size() == 1) {
                                result.result = true;
                            } else {
                                result.message = "创建失败，每个系列必须而且只能上传一张图片";
                            }
                        } else {
                            result.message = "图片路径不存在";
                        }
                    } else if (!videoPath.isEmpty()) {
                        JSObject record = DB.simpleScan("web_system_config").select("config_value").where(DB.filter().whereEqualTo("config_key", "fb_video_path")).execute();
                        String videoRoot = null;
                        if (record.hasObjectData()) {
                            videoRoot = record.get("config_value");
                        }
                        videosPath = new File(videoRoot + File.separatorChar + videoPath);
                        if (videosPath.exists()) {
                            uploadVideos = FileUtils.listFiles(videosPath, null, false);
                            if (uploadVideos != null && uploadVideos.size() == 2) {
                                result.result = true;
                            } else {
                                result.message = "创建失败，每个系列必须而且只能上传一个视频和一个缩略图";
                            }
                        } else {
                            result.message = "视频路径不存在";
                        }
                    } else {
                        result.message = "图片或视频路径二选一，不能为空！";
                    }
                }
            }

            if (result.result) {
                campaignName = campaignName + "_Strategy" + bidStrategy;
                if (campaignName.length() > 150) {
                    campaignName = campaignName.substring(0, 150);
                }
                interest = (interest == null) ? "" : interest;
                userOs = (userOs == null) ? "" : userOs;
                userDevice = (userDevice == null) ? "" : userDevice;
                boolean record = DB.insert("ad_campaigns_auto_create")
                        .put("app_name", appName)
                        .put("create_count", NumberUtil.parseInt(createCount, 0))
                        .put("account_id", accountId)
                        .put("country_region", region)
                        .put("explode_country", Boolean.parseBoolean(explodeCountry) ? 1 : 0)
                        .put("excluded_region", excludedRegion)
                        .put("language", language)
                        .put("age", age)
                        .put("explode_age", Boolean.parseBoolean(explodeAge) ? 1 : 0)
                        .put("gender", gender)
                        .put("explode_gender", Boolean.parseBoolean(explodeGender) ? 1 : 0)
                        .put("detail_target", interest)
                        .put("user_os", userOs)
                        .put("user_devices", userDevice)
                        .put("campaign_name", campaignName)
                        .put("bugdet", bugdet)
                        .put("bidding", bidding)
                        .put("explode_bidding", Boolean.parseBoolean(explodeBidding) ? 1 : 0)
                        .put("max_cpa", maxCPA)
                        .put("group_id", groupId)
                        .put("title", title)
                        .put("message", message)
                        .put("image_path", imagePath)
                        .put("video_path", videoPath)
                        .put("create_time", DateUtil.getNowTime())
                        .put("publisher_platforms", publisherPlatforms)
                        .put("bid_strategy", bidStrategy)
                        .put("page_id", pageId)
                        .put("mode_type", Integer.parseInt(flag))
                        .execute();
                if (record) {
                    result.result = true;
                    result.message = "创建成功";
                } else {
                    result.result = false;
                    result.message = "创建失败";
                }
            }
        } catch (Exception ex) {
            result.result = false;
            result.message = ex.getMessage();
        }
        return result;
    }

    private OperationResult facebookCampaignUpdate(HttpServletRequest request) {
        OperationResult result = new OperationResult();
        try {
            String id = request.getParameter("id");
            String appName = request.getParameter("appName");
            String accountName = request.getParameter("accountName");
            String accountId = request.getParameter("accountId");
            String createCount = request.getParameter("createCount");
            String excludedRegion = request.getParameter("excludedRegion");
            String language = request.getParameter("language");
            String interest = request.getParameter("interest");
            String userOs = request.getParameter("userOs");
            String userDevice = request.getParameter("userDevice");
            String campaignName = request.getParameter("campaignName");
            String bugdet = request.getParameter("bugdet");
            String maxCPA = request.getParameter("maxCPA");
            String groupId = request.getParameter("groupId");
            String title = request.getParameter("title");
            String message = request.getParameter("message");
            String materialPath = request.getParameter("materialPath");
            String identification = request.getParameter("identification");
            String region = request.getParameter("region");
            String age = request.getParameter("age");
            String gender = request.getParameter("gender");
            String bidding = request.getParameter("bidding");
            String pageId = request.getParameter("FBpage[pageId]");
            String publisherPlatforms = request.getParameter("publisherPlatforms");
            String bidStrategy = request.getParameter("bidStrategy");

            String imagePath = new String();
            String videoPath = new String();
            if (identification.equals("image")) {
                imagePath = materialPath;
                videoPath = "";
            } else {
                videoPath = materialPath;
                imagePath = "";
            }
            result.result = true;
            JSObject record = DB.simpleScan("web_system_config").select("config_value").where(DB.filter().whereEqualTo("config_key", "fb_image_path")).execute();
            String imageRoot = record.get("config_value");
            record = DB.simpleScan("web_system_config").select("config_value").where(DB.filter().whereEqualTo("config_key", "fb_video_path")).execute();
            String videoRoot = record.get("config_value");
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
            if (gender == null) {
                result.result = false;
                result.message = "性别不能为空";
            }
            if (region.isEmpty()) {
                result.result = false;
                result.message = "国家不能为空";
            }
            double dBidding = NumberUtil.parseDouble(bidding, 0);
            if (dBidding >= 0.5) {
                result.result = false;
                result.message = "bidding超过了0.5,   " + bidding;
            }

            if (identification.equals("image")) {
                File imagesPath = new File(imageRoot + File.separatorChar + materialPath);
                if (!imagesPath.exists()) {
                    result.result = false;
                    result.message = "图片路径不存在";
                }
            } else if (identification.equals("video")) {
                File videosPath = new File(videoRoot + File.separatorChar + materialPath);
                if (!videosPath.exists()) {
                    result.result = false;
                    result.message = "视频路径不存在";
                }
            }
            if (campaignName.length() > 110) {
                campaignName = campaignName.substring(0, 110);
            }
            if (result.result) {
                DB.update("ad_campaigns_auto_create")
                        .put("app_name", appName)
                        .put("create_count", NumberUtil.parseInt(createCount, 0))
                        .put("account_id", accountId)
                        .put("country_region", region)
                        .put("excluded_region", excludedRegion)
                        .put("language", language)
                        .put("age", age)
                        .put("gender", gender)
                        .put("detail_target", interest)
                        .put("user_os", userOs)
                        .put("user_devices", userDevice)
                        .put("campaign_name", campaignName)
                        .put("bugdet", bugdet)
                        .put("bidding", bidding)
                        .put("max_cpa", maxCPA)
                        .put("group_id", groupId)
                        .put("title", title)
                        .put("message", message)
                        .put("image_path", imagePath)
                        .put("video_path", videoPath)
                        .put("update_time", DateUtil.getNowTime())
                        .put("page_id", pageId)
                        .put("publisher_platforms", publisherPlatforms)
                        .put("bid_strategy", bidStrategy)

                        .where(DB.filter().whereEqualTo("id", id))
                        .execute();
                result.result = true;
                result.message = "更新成功";
            }
        } catch (Exception ex) {
            result.result = false;
            result.message = ex.getMessage();
        }
        return result;
    }

    private OperationResult facebookCampaignDelete(HttpServletRequest request) {
        OperationResult result = new OperationResult();
        try {
            String id_batch = request.getParameter("id_batch");
            String[] id_array = id_batch.split(",");
            for (String id : id_array) {   //这里会有删不掉的隐患吗？
                DB.delete("ad_campaigns_auto_create")
                        .where(DB.filter().whereEqualTo("id", id))
                        .execute();
            }
            result.result = true;
            result.message = "删除成功";
        } catch (Exception ex) {
            result.result = false;
            result.message = ex.getMessage();
        }
        return result;
    }

    private OperationResult facebookCampaignEnable(HttpServletRequest request) {
        OperationResult result = new OperationResult();
        try {
            String id = request.getParameter("id");
            String id_batch = request.getParameter("id_batch");
            String enable = request.getParameter("enable");
            if (id != null) {
                DB.update("ad_campaigns_auto_create")
                        .put("enabled", "true".equals(enable) ? 1 : 0)
                        .where(DB.filter().whereEqualTo("id", id))
                        .execute();
                result.result = true;
                result.message = "操作成功";
            } else if (id_batch != null) {
                String sql = "update ad_campaigns_auto_create set enabled = " + ("true".equals(enable) ? 1 : 0) +
                        " where id in(" + id_batch + ")";
                DB.updateBySql(sql); //待测试是否真的存入
                result.result = true;
                result.message = "操作成功";
            }

        } catch (Exception ex) {
            result.result = false;
            result.message = ex.getMessage();
        }
        return result;
    }

    static String[] ADWORDS_CAMPAIGN_FIELDS = {"id", "app_name", "create_count", "account_id", "country_region", "explode_country",
            "excluded_region", "language", "message1", "message2", "message3", "message4",
            "campaign_name", "bugdet", "bidding", "explode_bidding", "max_cpa", "image_path", "create_time", "update_time", "enabled", "conversion_id", "group_id", "mode_type"};

    public static JSObject adwordsFetchById(String id) {
        try {
            return DB.simpleScan("ad_campaigns_admob_auto_create").select(ADWORDS_CAMPAIGN_FIELDS)
                    .where(DB.filter().whereEqualTo("id", id)).execute();
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }

    public static List<JSObject> adwordsFetchData(String word, String tagName, String countryName) {
        List<JSObject> list = new ArrayList<>();
        try {
            if (StringUtil.isNotEmpty(tagName) && countryName.isEmpty() && (StringUtil.isEmpty(word))) {
                return DB.scan("ad_campaigns_admob_auto_create").select(ADWORDS_CAMPAIGN_FIELDS)
                        .where(DB.filter().whereEqualTo("app_name", tagName)).orderByAsc("id").execute();
            } else if (StringUtil.isNotEmpty(tagName) && StringUtil.isNotEmpty(countryName) && (StringUtil.isEmpty(word))) {
                return DB.scan("ad_campaigns_admob_auto_create").select(ADWORDS_CAMPAIGN_FIELDS)
                        .where(DB.filter().whereEqualTo("app_name", tagName))
                        .and(DB.filter().whereEqualTo("country_region", countryName))
                        .orderByAsc("id").execute();
            } else if (StringUtil.isNotEmpty(tagName) && StringUtil.isNotEmpty(word) && countryName.isEmpty()) {
                return DB.scan("ad_campaigns_admob_auto_create").select(ADWORDS_CAMPAIGN_FIELDS)
                        .where(DB.filter().whereEqualTo("app_name", tagName))
                        .and(DB.filter().whereLikeTo("campaign_name", "%" + word + "%"))
                        .orderByAsc("id").execute();
            } else if (StringUtil.isNotEmpty(tagName) && StringUtil.isNotEmpty(word) && StringUtil.isNotEmpty(countryName)) {
                return DB.scan("ad_campaigns_admob_auto_create").select(ADWORDS_CAMPAIGN_FIELDS)
                        .where(DB.filter().whereEqualTo("app_name", tagName))
                        .and(DB.filter().whereLikeTo("campaign_name", "%" + word + "%"))
                        .and(DB.filter().whereEqualTo("country_region", countryName))
                        .orderByAsc("id").execute();
            }
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return list;
    }

    public static List<JSObject> adwordsFetchData(int index, int size) {
        List<JSObject> list = new ArrayList<>();
        try {
            return DB.scan("ad_campaigns_admob_auto_create").select(ADWORDS_CAMPAIGN_FIELDS)
                    .limit(size).start(index * size).orderByAsc("id").execute();
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return list;
    }

    public static long adwordsCount() {
        try {
            JSObject object = DB.simpleScan("ad_campaigns_admob_auto_create").select(DB.func(DB.COUNT, "id")).execute();
            return object.get("count(id)");
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return 0;
    }

    private OperationResult adwordsCampaignBiddingBatchUpdate(HttpServletRequest request) {
        OperationResult result = new OperationResult();
        JsonParser parser = new JsonParser();
        String bidding_array = request.getParameter("bidding_array");
        JsonArray bidding_JsonArray = parser.parse(bidding_array).getAsJsonArray();
        try {
            for (int i = 0; i < bidding_JsonArray.size(); i++) {
                JsonObject j = bidding_JsonArray.get(i).getAsJsonObject();
                String id = j.get("id").getAsString();
                String bidding = j.get("bidding").getAsString();
                DB.update("ad_campaigns_admob_auto_create")
                        .put("bidding", bidding)
                        .where(DB.filter().whereEqualTo("id", id))  //这里 id 可是一个String 类型啊！
                        .execute();
            }
            result.result = true;
            result.message = "批量更新成功";
        } catch (Exception e) {
            result.result = false;
            result.message = e.getMessage();
        }
        return result;
    }

    private OperationResult adwordsCampaignCreate(HttpServletRequest request,Jedis jedis) {
        OperationResult result = new OperationResult();
        try {
            String appName = request.getParameter("appName");
            String accountName = request.getParameter("accountName");
            String accountId = request.getParameter("accountId");
            String createCount = request.getParameter("createCount");
            String excludedRegion = request.getParameter("excludedRegion");
            String language = request.getParameter("language");
            String conversionId = request.getParameter("conversion_id");
            String campaignName = request.getParameter("campaignName");
            String bugdet = request.getParameter("bugdet");
            String maxCPA = request.getParameter("maxCPA");
            String groupId = request.getParameter("groupId");
            String message1 = request.getParameter("message1");
            String message2 = request.getParameter("message2");
            String message3 = request.getParameter("message3");
            String message4 = request.getParameter("message4");
            String imagePath = request.getParameter("imagePath");
            String region = request.getParameter("region");
            String bidding = request.getParameter("bidding");
            String explodeCountry = request.getParameter("explodeCountry");
            String explodeBidding = request.getParameter("explodeBidding");

            result.result = false;
            if (createCount.isEmpty()) {
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
            } else {
                double dBidding = NumberUtil.parseDouble(bidding, 0);
                double maxBidding = 0.1;
                String maxBiddingStr = jedis.hget(JedisConstant.TAG_NAME_BIDDING_MAP, appName);
                if (maxBiddingStr == null) {
                    JSObject one = DB.findOneBySql("select max_bidding from web_tag where tag_name = '" + appName + "'");
                    if (one.hasObjectData()) {
                        maxBidding = NumberUtil.convertDouble(one.get("max_bidding"), 0);
                        jedis.hset(JedisConstant.TAG_NAME_BIDDING_MAP, appName, maxBidding + "");
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
                        result.message = "图片路径不存在";
                    }
                }
            }
            if (result.result) {
                String s = String.valueOf(System.currentTimeMillis());
                campaignName = campaignName + "_" + s.substring(s.length() - 6, s.length());
                if (campaignName.length() > 110) {
                    campaignName = campaignName.substring(0, 110);
                }
                if (maxCPA == null) maxCPA = "";
                long recordId = DB.insert("ad_campaigns_admob_auto_create")
                        .put("app_name", appName)
                        .put("create_count", NumberUtil.parseInt(createCount, 0))
                        .put("account_id", accountId)
                        .put("country_region", region)
                        .put("explode_country", Boolean.parseBoolean(explodeCountry) ? 1 : 0)
                        .put("excluded_region", excludedRegion)
                        .put("language", language)
                        .put("conversion_id", conversionId)
                        .put("campaign_name", campaignName)
                        .put("bugdet", bugdet)
                        .put("bidding", bidding)
                        .put("group_id", groupId)
                        .put("message1", message1)
                        .put("message2", message2)
                        .put("message3", message3)
                        .put("message4", message4)
                        .put("explode_bidding", Boolean.parseBoolean(explodeBidding) ? 1 : 0)
                        .put("max_cpa", maxCPA)
                        .put("image_path", imagePath)
                        .put("create_time", DateUtil.getNowTime())
                        .executeReturnId();
                if (recordId > 0) {
                    result.result = true;
                    result.message = "创建成功";
                } else {
                    result.result = false;
                    result.message = "创建失败";
                }
            }
        } catch (Exception ex) {
            result.result = false;
            result.message = ex.getMessage();
        }
        return result;
    }

    private OperationResult adwordsCampaignCreate2(HttpServletRequest request,Jedis jedis) {
        OperationResult result = new OperationResult();
        try {
            String flag = request.getParameter("flag");
            String appName = request.getParameter("appName");
            String accountName = request.getParameter("accountName");
            String accountId = request.getParameter("accountId");
            String createCount = request.getParameter("createCount");
            String excludedRegion = request.getParameter("excludedRegion");
            String language = request.getParameter("language");
            String conversionId = request.getParameter("conversion_id");
            String campaignName = request.getParameter("campaignName");
            String bugdet = request.getParameter("bugdet");
            String maxCPA = request.getParameter("maxCPA");
            String groupId = request.getParameter("groupId");
            String message1 = request.getParameter("message1");
            String message2 = request.getParameter("message2");
            String message3 = request.getParameter("message3");
            String message4 = request.getParameter("message4");
            String imagePath = request.getParameter("imagePath");
            String region = request.getParameter("region");
            String bidding = request.getParameter("bidding");
            String explodeCountry = request.getParameter("explodeCountry");
            String explodeBidding = request.getParameter("explodeBidding");

            result.result = false;
            if (createCount.isEmpty()) {
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
            } else {
                double dBidding = NumberUtil.parseDouble(bidding, 0);
                double maxBidding = 0.1;
                String maxBiddingStr = jedis.hget(JedisConstant.TAG_NAME_BIDDING_MAP, appName);
                if (maxBiddingStr == null) {
                    JSObject one = DB.findOneBySql("select max_bidding from web_tag where tag_name = '" + appName + "'");
                    if (one.hasObjectData()) {
                        maxBidding = NumberUtil.convertDouble(one.get("max_bidding"), 0);
                        jedis.hset(JedisConstant.TAG_NAME_BIDDING_MAP, appName, maxBidding + "");
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
                        result.message = "图片路径不存在";
                    }
                }
            }
            if (result.result) {
                String s = String.valueOf(System.currentTimeMillis());
                campaignName = campaignName + "_" + s.substring(s.length() - 6, s.length());
                if (campaignName.length() > 110) {
                    campaignName = campaignName.substring(0, 110);
                }
                if (maxCPA == null) maxCPA = "";
                long recordId = DB.insert("ad_campaigns_admob_auto_create")
                        .put("app_name", appName)
                        .put("create_count", NumberUtil.parseInt(createCount, 0))
                        .put("account_id", accountId)
                        .put("country_region", region)
                        .put("explode_country", Boolean.parseBoolean(explodeCountry) ? 1 : 0)
                        .put("excluded_region", excludedRegion)
                        .put("language", language)
                        .put("conversion_id", conversionId)
                        .put("campaign_name", campaignName)
                        .put("bugdet", bugdet)
                        .put("bidding", bidding)
                        .put("group_id", groupId)
                        .put("message1", message1)
                        .put("message2", message2)
                        .put("message3", message3)
                        .put("message4", message4)
                        .put("explode_bidding", Boolean.parseBoolean(explodeBidding) ? 1 : 0)
                        .put("max_cpa", maxCPA)
                        .put("image_path", imagePath)
                        .put("create_time", DateUtil.getNowTime())
                        .put("mode_type", Integer.parseInt(flag))
                        .executeReturnId();
                if (recordId > 0) {
                    result.result = true;
                    result.message = "创建成功";
                } else {
                    result.result = false;
                    result.message = "创建失败";
                }
            }
        } catch (Exception ex) {
            result.result = false;
            result.message = ex.getMessage();
        }
        return result;
    }

    private OperationResult adwordsCampaignUpdate(HttpServletRequest request) {
        OperationResult result = new OperationResult();
        try {
            String id = request.getParameter("id");
            String appName = request.getParameter("appName");
            String accountName = request.getParameter("accountName");
            String accountId = request.getParameter("accountId");
            String createCount = request.getParameter("createCount");
            String excludedRegion = request.getParameter("excludedRegion");
            String language = request.getParameter("language");
            String campaignName = request.getParameter("campaignName");
            String bugdet = request.getParameter("bugdet");
            String maxCPA = request.getParameter("maxCPA");
            String groupId = request.getParameter("groupId");
            String message1 = request.getParameter("message1");
            String message2 = request.getParameter("message2");
            String message3 = request.getParameter("message3");
            String message4 = request.getParameter("message4");
            String imagePath = request.getParameter("imagePath");
            String region = request.getParameter("region");
            String bidding = request.getParameter("bidding");
            String conversionId = request.getParameter("conversion_id");

            result.result = true;
            JSObject record = DB.simpleScan("web_system_config").select("config_value").where(DB.filter().whereEqualTo("config_key", "admob_image_path")).execute();
            String imageRoot = null;
            if (record.hasObjectData()) {
                imageRoot = record.get("config_value");
            }

            result.result = true;

            if (createCount.isEmpty()) {
                result.result = false;
                result.message = "创建数量不能为空";
            }
            if (message1.isEmpty()) {
                result.result = false;
                result.message = "广告语1不能为空";
            }
            if (message2.isEmpty()) {
                result.result = false;
                result.message = "广告语2不能为空";
            }
            if (message3.isEmpty()) {
                result.result = false;
                result.message = "广告语3不能为空";
            }
            if (message4.isEmpty()) {
                result.result = false;
                result.message = "广告语4不能为空";
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
            double dBidding = NumberUtil.parseDouble(bidding, 0);
            if (dBidding >= 0.5) {
                result.result = false;
                result.message = "bidding超过了0.5,   " + bidding;
            }

            File imagesPath = new File(imageRoot + File.separatorChar + imagePath);
            if (!imagesPath.exists()) {
                result.result = false;
                result.message = "图片路径不存在";
            }
            if (campaignName.length() > 110) {
                campaignName = campaignName.substring(0, 110);
            }
            if (result.result) {
                DB.update("ad_campaigns_admob_auto_create")
                        .put("app_name", appName)
                        .put("create_count", NumberUtil.parseInt(createCount, 0))
                        .put("account_id", accountId)
                        .put("country_region", region)
                        .put("excluded_region", excludedRegion)
                        .put("language", language)
                        .put("campaign_name", campaignName)
                        .put("bugdet", bugdet)
                        .put("bidding", bidding)
                        .put("message1", message1)
                        .put("message2", message2)
                        .put("message3", message3)
                        .put("message4", message4)
                        .put("max_cpa", maxCPA)
                        .put("group_id", groupId)
                        .put("image_path", imagePath)
                        .put("update_time", DateUtil.getNowTime())
                        .put("conversion_id", conversionId)
                        .where(DB.filter().whereEqualTo("id", id))
                        .execute();
                result.result = true;
                result.message = "更新成功";
            }
        } catch (Exception ex) {
            result.result = false;
            result.message = ex.getMessage();
        }
        return result;
    }

    private OperationResult adwordsCampaignDelete(HttpServletRequest request) {
        OperationResult result = new OperationResult();
        try {
            String id_batch = request.getParameter("id_batch");
            String[] id_array = id_batch.split(",");
            for (String id : id_array) {
                DB.delete("ad_campaigns_admob_auto_create")
                        .where(DB.filter().whereEqualTo("id", id))
                        .execute();
            }
            result.result = true;
            result.message = "删除成功";
        } catch (Exception ex) {
            result.result = false;
            result.message = ex.getMessage();
        }
        return result;
    }

    private OperationResult adwordsCampaignEnable(HttpServletRequest request) {
        OperationResult result = new OperationResult();
        try {
            String id = request.getParameter("id");
            String id_batch = request.getParameter("id_batch");
            String enable = request.getParameter("enable");
            if (id != null) {
                DB.update("ad_campaigns_admob_auto_create")
                        .put("enabled", "true".equals(enable) ? 1 : 0)
                        .where(DB.filter().whereEqualTo("id", id))
                        .execute();
                result.result = true;
                result.message = "操作成功";
            } else if (id_batch != null) {
                String sql = "update ad_campaigns_admob_auto_create set enabled = " + ("true".equals(enable) ? 1 : 0) +
                        " where id in(" + id_batch + ")";
                DB.updateBySql(sql);
                result.result = true;
                result.message = "操作成功";
            }

        } catch (Exception ex) {
            result.result = false;
            result.message = ex.getMessage();
        }
        return result;
    }
}
