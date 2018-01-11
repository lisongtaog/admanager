package com.bestgo.admanager.servlet;

import com.bestgo.admanager.DateUtil;
import com.bestgo.admanager.OperationResult;
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
import java.io.File;
import java.io.IOException;
import java.lang.System;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by jikai on 12/10/17.
 */
@WebServlet(name = "AutoCreateCampaign", urlPatterns = "/auto_create_campaign/*")
public class AutoCreateCampaign extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Utils.isAdmin(request, response)) return;

        String path = request.getPathInfo();
        JsonObject json = new JsonObject();

        OperationResult result = null;
        if (path.startsWith("/facebook")) {
            String method = path.replace("/facebook", "");
            switch (method) {
                case "/create":
                    result = facebookCampaignCreate(request);
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
                    if (word != null) {
                        JsonArray array = new JsonArray();
                        List<JSObject> data = facebookFetchData(word);
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
                                    one.addProperty(key, Utils.trimDouble((Double) value));
                                } else {
                                    one.addProperty(key, value.toString());
                                }
                            }
                            array.add(one);
                        }
                        json.add("data", array);
                        result = new OperationResult();
                        result.result = true;
                        result.message = "执行成功";
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
                                jsonObject.addProperty(key, Utils.trimDouble((Double) value));
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
                case "/create":
                    result = adwordsCampaignCreate(request);
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
                    if (word != null) {
                        JsonArray array = new JsonArray();
                        List<JSObject> data = adwordsFetchData(word);
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
                                    one.addProperty(key, Utils.trimDouble((Double) value));
                                } else {
                                    one.addProperty(key, value.toString());
                                }
                            }
                            array.add(one);
                        }
                        json.add("data", array);
                        result = new OperationResult();
                        result.result = true;
                        result.message = "执行成功";
                    }
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
                                jsonObject.addProperty(key, Utils.trimDouble((Double) value));
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

        response.getWriter().write(json.toString());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }


    static String[] FB_CAMPAIGN_FIELDS = {"id", "app_name", "create_count", "account_id", "country_region", "explode_country",
    "excluded_region", "language", "age", "explode_age", "gender", "explode_gender", "detail_target", "user_os", "user_devices",
    "campaign_name", "bugdet", "bidding", "explode_bidding", "max_cpa", "title", "message", "image_path", "create_time", "update_time", "enabled","video_path"};

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

    public static List<JSObject> facebookFetchData(String word) {
        List<JSObject> list = new ArrayList<>();
        try {
            return DB.scan("ad_campaigns_auto_create").select(FB_CAMPAIGN_FIELDS)
                    .where(DB.filter().whereLikeTo("campaign_name", "%" + word + "%")).orderByAsc("id").execute();
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

    private OperationResult facebookCampaignCreate(HttpServletRequest request) {
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
            String title = request.getParameter("title");
            String message = request.getParameter("message");
            String imagePath = request.getParameter("imagePath");
            String region = request.getParameter("region");
            String age = request.getParameter("age");
            String gender = request.getParameter("gender");
            String bidding = request.getParameter("bidding");
            String explodeCountry = request.getParameter("explodeCountry");
            String explodeAge = request.getParameter("explodeAge");
            String explodeGender = request.getParameter("explodeGender");
            String explodeBidding = request.getParameter("explodeBidding");
            String videoPath = request.getParameter("videoPath");
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


            if (campaignName.length() > 100) {
                campaignName = campaignName.substring(0, 100);
            }


            if (result.result) {
                long recordId = DB.insert("ad_campaigns_auto_create")
                        .put("app_name", appName)
                        .put("create_count", Utils.parseInt(createCount, 0))
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
                        .put("title", title)
                        .put("message", message)
                        .put("image_path", imagePath)
                        .put("video_path", videoPath)
                        .put("create_time", DateUtil.getNow())
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
            String title = request.getParameter("title");
            String message = request.getParameter("message");
            String imagePath = request.getParameter("imagePath");
            String videoPath = request.getParameter("videoPath");
            String region = request.getParameter("region");
            String age = request.getParameter("age");
            String gender = request.getParameter("gender");
            String bidding = request.getParameter("bidding");
            String explodeCountry = request.getParameter("explodeCountry");
            String explodeAge = request.getParameter("explodeAge");
            String explodeGender = request.getParameter("explodeGender");
            String explodeBidding = request.getParameter("explodeBidding");


            result.result = true;
            JSObject record = DB.simpleScan("web_system_config").select("config_value").where(DB.filter().whereEqualTo("config_key", "fb_image_path")).execute();
            String imageRoot = null;
            if (record.hasObjectData()) {
                imageRoot = record.get("config_value");
            }
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

            File imagesPath = new File(imageRoot + File.separatorChar + imagePath);
            if (!imagesPath.exists()) {
                result.result = false;
                result.message = "图片路径不存在";
            }
            if (campaignName.length() > 100) {
                campaignName = campaignName.substring(0, 100);
            }
            if (result.result) {
                DB.update("ad_campaigns_auto_create")
                        .put("app_name", appName)
                        .put("create_count", Utils.parseInt(createCount, 0))
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
                        .put("title", title)
                        .put("message", message)
                        .put("image_path", imagePath)
                        .put("video_path", videoPath)
                        .put("update_time", DateUtil.getNow())
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
            String id = request.getParameter("id");
            DB.delete("ad_campaigns_auto_create")
                    .where(DB.filter().whereEqualTo("id", id))
                    .execute();
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
            String enable = request.getParameter("enable");
            DB.update("ad_campaigns_auto_create")
                    .put("enabled", "true".equals(enable) ? 1 : 0)
                    .where(DB.filter().whereEqualTo("id", id))
                    .execute();
            result.result = true;
            result.message = "操作成功";
        } catch (Exception ex) {
            result.result = false;
            result.message = ex.getMessage();
        }
        return result;
    }

    static String[] ADWORDS_CAMPAIGN_FIELDS = {"id", "app_name", "create_count", "account_id", "country_region", "explode_country",
            "excluded_region", "language", "message1", "message2", "message3", "message4",
            "campaign_name", "bugdet", "bidding", "explode_bidding", "max_cpa", "image_path", "create_time", "update_time", "enabled","conversion_id"};

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

    public static List<JSObject> adwordsFetchData(String word) {
        List<JSObject> list = new ArrayList<>();
        try {
            return DB.scan("ad_campaigns_admob_auto_create").select(ADWORDS_CAMPAIGN_FIELDS)
                    .where(DB.filter().whereLikeTo("campaign_name", "%" + word + "%")).orderByAsc("id").execute();
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

    private OperationResult adwordsCampaignCreate(HttpServletRequest request) {
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
            String message1 = request.getParameter("message1");
            String message2 = request.getParameter("message2");
            String message3 = request.getParameter("message3");
            String message4 = request.getParameter("message4");
            String imagePath = request.getParameter("imagePath");
            String region = request.getParameter("region");
            String bidding = request.getParameter("bidding");
            String explodeCountry = request.getParameter("explodeCountry");
            String explodeBidding = request.getParameter("explodeBidding");

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
            double dBidding = Utils.parseDouble(bidding, 0);
            if (dBidding >= 0.5) {
                result.result = false;
                result.message = "bidding超过了0.5,   " + bidding;
            }
            String s = String.valueOf(System.currentTimeMillis());
            campaignName = campaignName+"_"+s.substring(s.length() - 6, s.length());
            if (campaignName.length() > 100) {
                campaignName = campaignName.substring(0, 100);
            }
            File imagesPath = new File(imageRoot + File.separatorChar + imagePath);
            if (!imagesPath.exists()) {
                result.result = false;
                result.message = "图片路径不存在";
            }

            if (result.result) {

                long recordId = DB.insert("ad_campaigns_admob_auto_create")
                        .put("app_name", appName)
                        .put("create_count", Utils.parseInt(createCount, 0))
                        .put("account_id", accountId)
                        .put("country_region", region)
                        .put("explode_country", Boolean.parseBoolean(explodeCountry) ? 1 : 0)
                        .put("excluded_region", excludedRegion)
                        .put("language", language)
                        .put("conversion_id", conversionId)
                        .put("campaign_name", campaignName)
                        .put("bugdet", bugdet)
                        .put("bidding", bidding)
                        .put("message1", message1)
                        .put("message2", message2)
                        .put("message3", message3)
                        .put("message4", message4)
                        .put("explode_bidding", Boolean.parseBoolean(explodeBidding) ? 1 : 0)
                        .put("max_cpa", maxCPA)
                        .put("image_path", imagePath)
                        .put("create_time", DateUtil.getNow())
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
            String message1 = request.getParameter("message1");
            String message2 = request.getParameter("message2");
            String message3 = request.getParameter("message3");
            String message4 = request.getParameter("message4");
            String imagePath = request.getParameter("imagePath");
            String region = request.getParameter("region");
            String bidding = request.getParameter("bidding");
            String explodeCountry = request.getParameter("explodeCountry");
            String explodeBidding = request.getParameter("explodeBidding");

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
            double dBidding = Utils.parseDouble(bidding, 0);
            if (dBidding >= 0.5) {
                result.result = false;
                result.message = "bidding超过了0.5,   " + bidding;
            }

            File imagesPath = new File(imageRoot + File.separatorChar + imagePath);
            if (!imagesPath.exists()) {
                result.result = false;
                result.message = "图片路径不存在";
            }
            if (campaignName.length() > 100) {
                campaignName = campaignName.substring(0, 100);
            }
            if (result.result) {
                DB.update("ad_campaigns_admob_auto_create")
                        .put("app_name", appName)
                        .put("create_count", Utils.parseInt(createCount, 0))
                        .put("account_id", accountId)
                        .put("country_region", region)
                        .put("explode_country", Boolean.parseBoolean(explodeCountry) ? 1 : 0)
                        .put("excluded_region", excludedRegion)
                        .put("language", language)
                        .put("campaign_name", campaignName)
                        .put("bugdet", bugdet)
                        .put("bidding", bidding)
                        .put("message1", message1)
                        .put("message2", message2)
                        .put("message3", message3)
                        .put("message4", message4)
                        .put("explode_bidding", Boolean.parseBoolean(explodeBidding) ? 1 : 0)
                        .put("max_cpa", maxCPA)
                        .put("image_path", imagePath)
                        .put("update_time", DateUtil.getNow())
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
            String id = request.getParameter("id");
            DB.delete("ad_campaigns_admob_auto_create")
                    .where(DB.filter().whereEqualTo("id", id))
                    .execute();
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
            String enable = request.getParameter("enable");
            DB.update("ad_campaigns_admob_auto_create")
                    .put("enabled", "true".equals(enable) ? 1 : 0)
                    .where(DB.filter().whereEqualTo("id", id))
                    .execute();
            result.result = true;
            result.message = "操作成功";
        } catch (Exception ex) {
            result.result = false;
            result.message = ex.getMessage();
        }
        return result;
    }
}
