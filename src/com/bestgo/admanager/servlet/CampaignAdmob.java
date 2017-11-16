package com.bestgo.admanager.servlet;

import com.bestgo.admanager.Config;
import com.bestgo.admanager.OperationResult;
import com.bestgo.admanager.Utils;
import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

@WebServlet(name = "CampaignAdMob", urlPatterns = "/campaign_admob/*")
public class CampaignAdmob extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Utils.isAdmin(request, response)) return;

        String path = request.getPathInfo();
        JsonObject json = new JsonObject();

        if (path.startsWith("/create")) {
            String appName = request.getParameter("appName");
            String gpPackageId = request.getParameter("gpPackageId");
            String accountId = request.getParameter("accountId");
            String region = request.getParameter("region");
            String excludedRegion = request.getParameter("excludedRegion");
            String language = request.getParameter("language");
            String campaignName = request.getParameter("campaignName");
            String bugdet = request.getParameter("bugdet");
            String bidding = request.getParameter("bidding");
            String message1 = request.getParameter("message1");
            String message2 = request.getParameter("message2");
            String message3 = request.getParameter("message3");
            String message4 = request.getParameter("message4");
            String imagePath = request.getParameter("imagePath");

            OperationResult result = new OperationResult();
            try {
                JSObject record = DB.simpleScan("web_system_config").select("config_value").where(DB.filter().whereEqualTo("config_key", "admob_image_path")).execute();
                String imageRoot = null;
                if (record.hasObjectData()) {
                    imageRoot = record.get("config_value");
                }

                result.result = true;

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

                File imagesPath = new File(imageRoot + File.separatorChar + imagePath);
                if (!imagesPath.exists()) {
                    result.result = false;
                    result.message = "图片路径不存在";
                }

                if (result.result) {
                    Calendar calendar = Calendar.getInstance();
                    String now  = String.format("%d-%02d-%02d %02d:%02d:%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH),
                            calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
                    long genId = DB.insert("ad_campaigns_admob")
                            .put("account_id", accountId)
                            .put("campaign_name", campaignName)
                            .put("app_id", gpPackageId)
                            .put("country_region", region)
                            .put("language", language)
                            .put("excluded_region", excludedRegion)
                            .put("create_time", now)
                            .put("bugdet", bugdet)
                            .put("bidding", bidding)
                            .put("message1", message1)
                            .put("message2", message2)
                            .put("message3", message3)
                            .put("message4", message4)
                            .put("app_name", appName)
                            .put("tag_name", appName)
                            .put("image_path", imagesPath.getAbsolutePath())
                            .executeReturnId();

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
            String tags = request.getParameter("tags");

            if (id == null) {
                try {
                    JSObject campaign = DB.simpleScan("web_ad_campaigns_admob")
                            .select("id")
                            .where(DB.filter().whereEqualTo("campaign_id", campaignId))
                            .execute();
                    if (campaign.hasObjectData()) {
                        id = campaign.get("id").toString();
                    }
                } catch (Exception e) {
                }
            }
            OperationResult result = updateCampaign(id, tags);
            json.addProperty("ret", result.result ? 1 : 0);
            json.addProperty("message", result.message);
        } else if (path.startsWith("/selectLanguageAdmobByRegion")) {
            String region = request.getParameter("regionAdmob");
            String appName = request.getParameter("appNameAdmob");

            if (region != null) {
                Map<String, String> regionLanguageAdmobRelMap = Config.getRegionLanguageRelMap();
                String[] regionAdmobArray = region.split(",");
                Set<String> languageAdmobSet = new HashSet<>();
                for (int i=0,len = regionAdmobArray.length;i<len;i++){
                    languageAdmobSet.add(regionLanguageAdmobRelMap.get(regionAdmobArray[i]));
                }
                int size = languageAdmobSet.size();
                String languageAdmob = "";
                if(size == 1){
                    languageAdmob = regionLanguageAdmobRelMap.get(regionAdmobArray[0]);
                }else{
                    languageAdmob = "English";
                }
                String sql = "select message1,message2,message3,message4 from web_ad_descript_dict_admob where app_name = '" + appName + "' and language = '" + languageAdmob +"' limit 1";
                JSObject messages = new JSObject();
                try {
                    messages = DB.findOneBySql(sql);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                json.addProperty("message1",(String)(messages.get("message1")));
                json.addProperty("message2",(String)(messages.get("message2")));
                json.addProperty("message3",(String)(messages.get("message3")));
                json.addProperty("message4",(String)(messages.get("message4")));
                json.addProperty("languageAdmob", languageAdmob);
                json.addProperty("ret", 1);
            }
        }else if (path.startsWith("/query")) {
            String word = request.getParameter("word");
            if (word != null) {
                List<JSObject> data = fetchData(word);
                json.addProperty("ret", 1);
                JsonArray array = new JsonArray();
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
                    List<String> tags = CampaignAdmob.bindTags(data.get(i).get("campaign_id"));
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
        }

        response.getWriter().write(json.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    private OperationResult updateCampaign(String id, String tags) {
        OperationResult ret = new OperationResult();

        try {
            JSObject campaign = DB.simpleScan("web_ad_campaigns_admob").select("id", "campaign_id").where(DB.filter().whereEqualTo("id", Utils.parseInt(id, 0))).execute();
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
}
