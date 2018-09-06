package com.bestgo.admanager.servlet;

import com.bestgo.admanager.Config;
import com.bestgo.admanager.OperationResult;
import com.bestgo.admanager.bean.BatchChangeItem;
import com.bestgo.admanager.utils.*;
import com.bestgo.admanager_tools.DefaultConfig;
import com.bestgo.admanager_tools.FacebookAccountBalanceFetcher;
import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;
import com.google.gson.Gson;
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
import java.io.*;
import java.lang.System;
import java.util.*;

import static com.bestgo.admanager_tools.FacebookAccountBalanceFetcher.deleteFBCampaignMultipleConditions;
import static com.bestgo.admanager_tools.FacebookAccountBalanceFetcher.updateFBCampaignStatusMultipleConditions;

/**
 * Desc: 有关Facebook系列创建的操作
 */
@WebServlet(name = "Campaign", urlPatterns = "/campaign/*")
public class Campaign extends BaseHttpServlet {

    private static final Logger LOGGER = Logger.getLogger(Campaign.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doPost(request, response);
        if (!Utils.isAdmin(request, response)) return;
        Jedis jedis = JedisPoolUtil.getJedis();
        String path = request.getPathInfo();
        JsonObject json = new JsonObject();
        //检查帐户状态
        Map<String, Integer> facebookAccountDetailsMap = getFacebookAccountDetails();

        long delCount = 0;

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

                DefaultConfig.setProxy();
                DB.init();
                int index = 0;
                //更新系列
                if (accountIds.length > 1) {
                    for (int j = 0; j < accountIds.length; j++) {
                        index = updateFBCampaignStatusMultipleConditions(accountIds[j], Boolean.parseBoolean(containsDisabledAccountId), campaignStatus);
                    }
                } else {
                    index = updateFBCampaignStatusMultipleConditions(accountId, Boolean.parseBoolean(containsDisabledAccountId), campaignStatus);

                }
                if (index == 1) {
                    result.message = "帐号已经关闭!";
                    result.result = true;
                } else {
                    System.out.println("更新完成，请点击删除！");
                    result.message = "更新完成，请点击删除！";
                    result.result = true;
                }
            } catch (Exception e) {
                result.message = e.getMessage();
                result.result = true;
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

                DefaultConfig.setProxy();
                DB.init();

                //删除系列
                if (accountIds.length > 1) {
                    for (int i = 0; i < accountIds.length; i++) {
                        deleteFBCampaignMultipleConditions(accountIds[i], campaignStatus, appName, region);
                    }
                } else {
                    deleteFBCampaignMultipleConditions(accountId, campaignStatus, appName, region);
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
            delCount = 0;
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

                DefaultConfig.setProxy();
                DB.init();



                //删除系列个数统计
                if (accountIds.length > 1) {
                    for (int i = 0; i < accountIds.length; i++) {
                        delCount = countArchivedCampaign(accountIds[i], campaignStatus, appName, region);
                    }
                } else {
                    delCount = countArchivedCampaign(accountId, campaignStatus, appName, region);
                }

                int index1 = (int) delCount / 1000;

                System.out.println("需要删除" + delCount + "个系列，每次删除1000个。剩余" + index1 + "次操作！");
                result.message = "需要删除" + delCount + "个系列，每次删除1000个。剩余" + index1 + "次操作！";
                result.result = true;

            } catch (Exception e) {
                result.message = e.getMessage();
                result.result = false;
            }

            json.addProperty("ret", result.result ? 1 : 0);
            json.addProperty("message", result.message);
        } else if (path.startsWith("/create")) {
            String appName = request.getParameter("appName");
            String appId = request.getParameter("appId");
            String accountId = request.getParameter("accountId");
            String accountName = request.getParameter("accountName");

            String[] accountNameArr = accountName.split(",");
            String[] accountIdArr = accountId.split(",");

            //判断帐户是否有效
            boolean index = false;

            for (int j = 0, len = accountNameArr.length; j < len; j++) {
                //账户状态，如果为1则为开启；为2则为禁用
                Integer accountStatus = facebookAccountDetailsMap.get(accountIdArr[j]);
                //帐户不存在或status是关闭,直接返回
                if (accountStatus == null || accountStatus != 1) {
                    index = true;
                    break;
                }
            }

            String createCount = request.getParameter("createCount");
            String pageId = request.getParameter("FBpage[pageId]");
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
            bidding = bidding.trim();
            String maxCPA = request.getParameter("maxCPA");
            String groupIdStr = request.getParameter("groupId");
            String title = request.getParameter("title");
            String message = request.getParameter("message");
            String identification = request.getParameter("identification");
            String materialPath = request.getParameter("materialPath");
            String publisherPlatforms = request.getParameter("publisherPlatforms");
            String bidStrategy = request.getParameter("bidStrategy");
            String imagePath = new String();
            String videoPath = new String();
            if (identification.equals("image")) {
                imagePath = materialPath;
                videoPath = "";
            } else if (identification.equals("video")) {
                videoPath = materialPath;
                imagePath = "";
            }

            OperationResult result = new OperationResult();
            try {
                result.result = false;
                File imagesPath = null;
                File videosPath = null;
                Collection<File> uploadImages = null;
                Collection<File> uploadVideos = null;
                int groupId = NumberUtil.parseInt(groupIdStr, 0);

                if (groupId == 0) {
                    result.message = "广告组ID不存在！请联系管理员";
                } else if (StringUtil.isEmpty(createCount)) {
                    result.message = "创建数量不能为空";
                } else if (index) {
                    result.message = "没有这个账户ID或此账户被禁，请联系管理员";
                } else if (StringUtil.isEmpty(pageId)) {
                    result.message = "pageId不能为空，请关闭页面刷新重试";
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
                } else if (bidding.indexOf(" ") != -1) {
                    result.message = "出价不能包含空格！";
                } else if (gender == null) {
                    result.message = "性别不能为空";
                } else if (region.isEmpty()) {
                    result.message = "国家不能为空";
                } else if (publisherPlatforms.isEmpty()) {
                    result.message = "版位不能为空";
                } else {
                    double dBidding = NumberUtil.parseDouble(bidding, 0);
                    double maxBidding = 0.1;
                    String maxBiddingStr = jedis.hget("tagNameBiddingMap", appName);
                    if (maxBiddingStr == null) {
                        JSObject one = DB.findOneBySql("select max_bidding from web_tag where tag_name = '" + appName + "'");
                        if (one.hasObjectData()) {
                            maxBidding = NumberUtil.convertDouble(one.get("max_bidding"), 0);
                            jedis.hset("tagNameBiddingMap",appName,maxBidding + "");
                        }
                    } else {
                        maxBidding = NumberUtil.parseDouble(maxBiddingStr, 0);
                    }

                    if (dBidding > maxBidding) {
                        result.message = "bidding超过了本应用的最大出价,   " + bidding + " > " + maxBidding;
                    } else {
                        if (!imagePath.isEmpty()) {
                            JSObject record = DB.simpleScan("web_system_config").select("config_value").where(DB.filter().whereEqualTo("config_key", "fb_image_path")).execute();
                            String imageRoot = null;
                            if (record.hasObjectData()) {
                                imageRoot = record.get("config_value");
                            }
                            imagesPath = new File(imageRoot + File.separatorChar + imagePath);
                            if (imagesPath.exists()) {
                                uploadImages = FileUtils.listFiles(imagesPath, null, false);
                                if (uploadImages != null) {
                                    result.result = true;
                                } else {
                                    result.message = "该路径下没有图片文件";
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
                    Calendar calendar = Calendar.getInstance();
                    String campaignNameOld = campaignName.replace("Group_", "Group" + groupId) + "_";
                    accountNameArr = accountName.split(",");
                    String accountNameArrStr = accountName.replace(",", "");
                    accountIdArr = accountId.split(",");
                    int createCountInt = NumberUtil.parseInt(createCount, 0);
                    for (int j = 0, len = accountNameArr.length; j < len; j++) {
                        for (int i = 0; i < createCountInt; i++) {
                            String now = String.format("%d-%02d-%02d %02d:%02d:%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH),
                                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
                            String s = String.valueOf(System.currentTimeMillis());
                            String part = new StringBuffer(i + s + "").reverse().toString();
                            campaignName = campaignNameOld.replace(accountNameArrStr, accountNameArr[j]) + "_Strategy" + bidStrategy + "_" + part;
                            if (campaignName.length() > 150) {
                                campaignName = campaignName.substring(0, 150);
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
                                    .put("group_id", groupId)
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
                                    .put("publisher_platforms", publisherPlatforms)
                                    .put("bid_strategy", bidStrategy)
                                    .executeReturnId();
                            if (genId > 0) {
                                boolean flag = false;
                                if (uploadImages != null) {
                                    for (File file : uploadImages) {
                                        String fileName = file.getAbsolutePath().toLowerCase();
                                        if (fileName.endsWith("gif") || fileName.endsWith("jpg") || fileName.endsWith("jpeg") || fileName.endsWith("png")) {
                                            String sql = "insert into ad_ads set parent_id=" + genId + ", image_file_path='" + file.getAbsolutePath() + "'";
                                            flag = DB.updateBySql(sql);
                                        }
                                    }
                                } else if (uploadVideos != null) {
                                    String video_file_path = null;
                                    String thumbnail_image_file_path = null;
                                    for (File file : uploadVideos) {
                                        String fileAbsolutePath = file.getAbsolutePath();
                                        String fileName = fileAbsolutePath.toLowerCase();
                                        if (fileName.endsWith("mp4") || fileName.endsWith("mov") || fileName.endsWith("gif")) {
                                            video_file_path = fileAbsolutePath;
                                        } else if (fileName.endsWith("jpg") || fileName.endsWith("jpeg") || fileName.endsWith("png")) {
                                            thumbnail_image_file_path = fileAbsolutePath;
                                        }
                                    }
                                    if (video_file_path != null && thumbnail_image_file_path != null) {
                                        String sql = "insert into ad_ads set parent_id = '" + genId + "', video_file_path = '" + video_file_path + "', thumbnail_image_file_path = '" + thumbnail_image_file_path + "'";
                                        flag = DB.updateBySql(sql);
                                    }
                                }
                                if (!flag) {
                                    DB.delete("ad_campaigns").where(DB.filter().whereEqualTo("campaign_name", campaignName)).execute();
                                    //返回前端提醒
                                    System.out.println("由于Media导致第" + (i + 1) + "个系列创建失败,名为[" + campaignName + "]的系列已经被删除！");
                                    result.warning = "由于Media导致第" + (i + 1) + "个系列创建失败,名为[" + campaignName + "]的系列已经被删除！";
                                }
                            } else {
                                Logger logger = Logger.getRootLogger();
                                logger.debug("facebook_app_id=" + appId + ", account_id=" + accountIdArr[j] + ", country_region=" + region +
                                        ", excluded_region=" + excludedRegion + ", create_time=" + now + ", language=" + language +
                                        ", campaign_name=" + campaignName + ", page_id=" + pageId + ", bugdet=" + bugdet +
                                        ", bidding=" + bidding + ", bugdet=" + bugdet + ", bidding=" + bidding + ", title=" + title + ", message=" + message +
                                        ", app_name=" + appName + ", age=" + age + ", gender=" + gender + ", user_devices=" + userDevice + ", detail_target=" + interest);
                            }

                        }
                    }
                }
            } catch (Exception ex) {
                result.message = ex.getMessage();
                result.result = false;
            }
            json.addProperty("ret", result.result ? 1 : 0);
            json.addProperty("message", result.message);
            json.addProperty("warning", result.warning); //目前仅在Media导致的系列创建后又删除下有用
        } else if (path.startsWith("/update")) {
            String id = request.getParameter("id");
            String tags = request.getParameter("tags");

            OperationResult result = new OperationResult();
            if (id != null) {
                try {
                    String sql = "SELECT id FROM web_tag WHERE tag_name = '" + tags + "'";
                    JSObject one = DB.findOneBySql(sql);
                    if (one.hasObjectData()) {
                        long tagId = one.get("id");
                        sql = "UPDATE web_ad_campaigns SET tag_id = " + tagId + " WHERE id = " + id;
                        boolean b = DB.updateBySql(sql);
                        if (b) {
                            result.result = true;
                            result.message = "更新成功！";
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
        } else if (path.startsWith("/query_batch_change_status")) {
            String pageNow = request.getParameter("pageNow");
            int pageSize = 50;
            int pageIdx = (Integer.parseInt(pageNow) - 1) * pageSize;
            try {
                String sql = "select count(id) as count from web_ad_batch_change_campaigns where success=0";
                JSObject temp = DB.findOneBySql(sql);
                long count = temp.get("count");
                long totalPage = count / pageSize + (count % pageSize == 0 ? 0 : 1);
                String[] fields = {"id", "network", "campaign_id", "campaign_name", "failed_count", "last_error_message"};
                JsonArray array = new JsonArray();
                sql = "SELECT id,network,campaign_id,campaign_name,failed_count,last_error_message FROM web_ad_batch_change_campaigns " +
                        "WHERE success = 0 " +
                        "LIMIT " + pageSize + " OFFSET " + pageIdx;
                List<JSObject> list = DB.findListBySql(sql);
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
                json.addProperty("total_page", totalPage);
                json.addProperty("ret", 1);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (path.startsWith("/query_status")) {
            try {
                String sqlInit = "select count(id) as count from ad_campaigns where success = 0 ";
                JSObject temp = DB.findOneBySql(sqlInit);
                long sum = temp.get("count");
                sqlInit = "select count(id) as count from ad_campaigns_admob where success = 0 ";
                temp = DB.findOneBySql(sqlInit);
                long sum1 = temp.get("count");
                sum += sum1;
                int pageSize = 50;
                long totalPage = sum / pageSize + (sum % pageSize == 0 ? 0 : 1);

                //@param fields 这个才是用于campaign_status.jsp 的回显的重要数据
                //@param array 用于存放facebook和adwords的具体错误信息的数组
                String page = request.getParameter("pageNow");
                int pageSizeAlone = pageSize / 2;
                Integer pageInt = Integer.parseInt(page);
                if (pageInt == null) {
                    pageInt = 1;
                }
                int pageIdx = (pageInt - 1) * pageSizeAlone;
                JsonArray array = new JsonArray();


                //分页还没做好 总页面回显 和 页数填写
                List<JSObject> list = new ArrayList<JSObject>();
                String sql = "SELECT id,campaign_name,failed_count,last_error_message FROM ad_campaigns " +
                        "WHERE success = 0 " +
                        "LIMIT " + pageSizeAlone + " OFFSET " + pageIdx;     //这一句是可以优化的
                list = DB.findListBySql(sql);
                for (int i = 0; i < list.size(); i++) {
                    JsonObject one = new JsonObject();
                    one.addProperty("id", list.get(i).get("id").toString());
                    one.addProperty("campaign_name", list.get(i).get("campaign_name").toString());
                    one.addProperty("failed_count", list.get(i).get("failed_count").toString());
                    one.addProperty("last_error_message", list.get(i).get("last_error_message").toString());
                    one.addProperty("network", "Facebook");
                    array.add(one);
                }

                sql = "SELECT id,campaign_name,failed_count,last_error_message FROM ad_campaigns_admob " +
                        "WHERE success = 0 " +
                        "LIMIT " + pageSizeAlone + " OFFSET " + pageIdx;
                list = DB.findListBySql(sql);
                for (int i = 0; i < list.size(); i++) {
                    JsonObject one = new JsonObject();
                    one.addProperty("id", list.get(i).get("id").toString());
                    one.addProperty("campaign_name", list.get(i).get("campaign_name").toString());
                    one.addProperty("failed_count", list.get(i).get("failed_count").toString());
                    one.addProperty("last_error_message", list.get(i).get("last_error_message").toString());
                    one.addProperty("network", "AdWords");
                    array.add(one);
                }

                json.add("data", array);  //前端回显只用了这一条
                json.addProperty("total_page", totalPage);
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
                    List<String> tags = Campaign.bindTags(data.get(i).get("campaign_id"));
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
        } else if (path.startsWith("/fetch_not_exist_tag_campaigns")) {
            JsonArray array = new JsonArray();
            try {
                String sqlFilterAll = "select id,campaign_id,adset_id,account_id,campaign_name,create_time,status,budget,bidding," +
                        "total_spend,total_click,total_installed,cpa,ctr,effective_status from web_ad_campaigns where tag_id = 0 ";
                List<JSObject> data = DB.findListBySql(sqlFilterAll);
                if (data != null) {
                    for (int i = 0, len = data.size(); i < len; i++) {
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
                        double installed = NumberUtil.convertDouble(one.get("total_installed").getAsDouble(), 0);
                        double click = NumberUtil.convertDouble(one.get("total_click").getAsDouble(), 0);
                        double cvr = click > 0 ? installed / click : 0;
                        one.addProperty("cvr", NumberUtil.trimDouble(cvr, 3));
                        //one.addProperty("tagStr", "_");
                        array.add(one);

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            json.addProperty("ret", 1);
            json.add("data", array);
        } else if (path.startsWith("/find_create_data")) {
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
                String appName = request.getParameter("appName");
                JsonParser parser = new JsonParser();
                Gson gson = new Gson();
                JsonArray array = parser.parse(data).getAsJsonArray();
                String now = DateUtil.getNowTime();

                for (int i = 0, len = array.size(); i < len; i++) {
                    BatchChangeItem item = gson.fromJson(array.get(i), BatchChangeItem.class);
                    JSObject record = DB.simpleScan("web_ad_batch_change_campaigns")
                            .select("id").where(DB.filter().whereEqualTo("campaign_id", item.campaignId))
                            .and(DB.filter().whereEqualTo("success", 0)).execute();

                    int enabled = -1;
                    if (item.enabled != null) {
                        enabled = item.enabled ? 1 : 0;
                    }
                    double maxBidding = 0.0;
                    String maxBiddingStr = jedis.hget("tagNameBiddingMap", appName);
                    if (maxBiddingStr == null) {
                        JSObject one = DB.findOneBySql("select max_bidding from web_tag where tag_name = '" + appName + "'");
                        if (one.hasObjectData()) {
                            maxBidding = NumberUtil.convertDouble(one.get("max_bidding"), 0);
                            jedis.hset("tagNameBiddingMap",appName,maxBidding + "");
                        }
                    } else {
                        maxBidding = NumberUtil.parseDouble(maxBiddingStr, 0);
                    }
                    if (item.bidding > 0 && item.bidding >= maxBidding) {
                        throw new Exception("超过最大出价, 系列ID=" + item.campaignId);
                    }
                    if (enabled == 1) item.excludedCountry = null;
                    //账户状态，为1则为开启；为2则为禁用
                    Integer accountStatus = facebookAccountDetailsMap.get(item.accountId);

                    if (record.hasObjectData() && (item.excludedCountry == null || item.excludedCountry.isEmpty())) {
                        long id = record.get("id");
                        if (("admob".equals(item.network)) || ("facebook".equals(item.network) && accountStatus != null && accountStatus == 1)) {
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
                        }
                    } else {
                        int bidStrategy = 1;
                        if ("facebook".equals(item.network)) {
                            JSObject one = DB.findOneBySql("SELECT bid_strategy from ad_campaigns where campaign_id = '" + item.campaignId + "'");
                            if (one.hasObjectData()) {
                                bidStrategy = one.get("bid_strategy");
                            }
                        }
                        if (("admob".equals(item.network)) || ("facebook".equals(item.network) && accountStatus != null && accountStatus == 1)) {
                            DB.insert("web_ad_batch_change_campaigns")
                                    .put("enabled", enabled)
                                    .put("bugdet", item.budget)
                                    .put("bidding", item.bidding)
                                    .put("bid_strategy", bidStrategy)
                                    .put("network", item.network)
                                    .put("account_id", item.accountId)
                                    .put("campaign_id", item.campaignId)
                                    .put("campaign_name", item.campaignName != null ? item.campaignName : "")
                                    .put("excluded_country", item.excludedCountry != null ? item.excludedCountry : "")
                                    .put("create_time", now)
                                    .put("success", 0)
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
        } else if (path.startsWith("/selectFacebookMessage")) {
            String appName = request.getParameter("appName");
            String language = request.getParameter("language");
            try {
                String sql = "select title,message from web_ad_descript_dict where app_name = '" + appName + "' and language = '" + language + "' limit 1";
                JSObject titleMessage = new JSObject();
                try {
                    titleMessage = DB.findOneBySql(sql);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                json.addProperty("title", (String) (titleMessage.get("title")));
                json.addProperty("message", (String) (titleMessage.get("message")));
                json.addProperty("language", language);
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
                    jedis.hset("tagNameBiddingMap",appName,maxBiddingStr);
                }
            }
            if (maxBiddingStr != null) {
                json.addProperty("max_bidding", maxBiddingStr);
                json.addProperty("ret", 1);
            }
        } else if (path.startsWith("/get_title_message_by_app_and_region_and_group_id")) {
            String region = request.getParameter("region");
            String appName = request.getParameter("appName");
            String advertGroupId = request.getParameter("advertGroupId");
            String language;

            if (region != null && region.length() > 0) {
                Map<String, String> regionLanguageRelMap = Config.getRegionLanguageRelMap();
                String[] regionArray = region.split(",");
                Set<String> languageSet = new HashSet<>();

                for (int i = 0, len = regionArray.length; i < len; i++) {
                    languageSet.add(regionLanguageRelMap.get(regionArray[i]));
                }

                if (languageSet.size() == 1) {
                    language = regionLanguageRelMap.get(regionArray[0]);
                } else {
                    language = "English";
                }
                String sql = "select title,message from web_ad_descript_dict where app_name = '" + appName + "' and language = '" + language + "' and group_id = " + advertGroupId;

                JSObject titleMessage = new JSObject();
                try {
                    titleMessage = DB.findOneBySql(sql);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                json.addProperty("title", (String) (titleMessage.get("title")));
                json.addProperty("message", (String) (titleMessage.get("message")));
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

    private OperationResult updateCampaign(String id, String campaignName, String status, String budget, String bidding, String tags) {
        OperationResult ret = new OperationResult();

        try {
            JSObject campaign = DB.simpleScan("web_ad_campaigns").select("id", "campaign_id", "adset_id", "campaign_name", "status", "budget", "bidding").where(DB.filter().whereEqualTo("id", NumberUtil.parseInt(id, 0))).execute();
            if (campaign.hasObjectData()) {
                String campaign_id = campaign.get("campaign_id");
                String adset_id = campaign.get("adset_id");
                String oldCampaignName = campaign.get("campaign_name");
                String oldStatus = campaign.get("status");
                double oldBudget = (double) campaign.get("budget") / 100;
                double oldBidding = (double) campaign.get("bidding") / 100;

                if (!oldCampaignName.equals(campaignName) || oldBudget != NumberUtil.parseDouble(budget, 0) || oldBidding != NumberUtil.parseDouble(bidding, 0)) {
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

                        if (oldBudget != NumberUtil.parseDouble(budget, 0) || oldBidding != NumberUtil.parseDouble(bidding, 0)) {
                            cmd += "-adset_id " + adset_id + " ";
                            if (oldBudget != NumberUtil.parseDouble(budget, 0))
                                cmd += "-budget \"" + budget + "\" ";
                            if (oldBidding != NumberUtil.parseDouble(bidding, 0))
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
                                    .put("budget", NumberUtil.parseDouble(budget, 0) * 100)
                                    .put("bidding", NumberUtil.parseDouble(bidding, 0) * 100)
                                    .where(DB.filter().whereEqualTo("id", NumberUtil.parseInt(id, 0)))
                                    .execute();
                        } else {
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

    /**
     * 获取facebook的账户ID和状态，1为开启，2为关闭
     *
     * @return
     * @throws Exception
     */
    private static Map<String, Integer> getFacebookAccountDetails() {
        Map<String, Integer> map = new HashMap<>();
        try {
            List<JSObject> list = DB.findListBySql("SELECT account_id,status FROM web_account_id");
            for (int i = 0, len = list.size(); i < len; i++) {
                JSObject js = list.get(i);
                if (js.hasObjectData()) {
                    String accountId = js.get("account_id");
                    Integer status = js.get("status");
                    if (status != null && accountId != null) {
                        map.put(accountId, status);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    /**
     * 多情况下删除FB的广告系列个数统计
     *
     * @param accountId
     * @param status
     * @param tag
     * @param country
     */
    public static long countArchivedCampaign(String accountId, String status, String tag, String country) {
        try {
            String sql = "";
            JSObject oneBySql;
            long geShu = 0;
            if (!(null == accountId) && !"".equals(accountId)) {//帐户非空
                if (!(null == status) && !"".equals(status)) {//帐号+状态
                    sql = "SELECT COUNT(*) AS geShu  FROM web_ad_campaigns WHERE account_id = '" + accountId + "' AND STATUS = '" + status + "' ";
                    oneBySql = DB.findOneBySql(sql);
                    geShu = oneBySql.get("geShu");
                } else if (!(null == tag) && !"".equals(tag)) {//帐号+应用
                    Map<String, Integer> facebookTagDetails = FacebookAccountBalanceFetcher.getFacebookTagDetails();
                    Integer tag_id = facebookTagDetails.get(tag);
                    sql = "SELECT COUNT(*) AS geShu FROM web_ad_campaigns WHERE account_id = '" + accountId + "' AND tag_id = '" + tag_id + "' AND status != 'ARCHIVED' ";
                    oneBySql = DB.findOneBySql(sql);
                    geShu = oneBySql.get("geShu");
                } else {//帐户下所有系列
                    sql = "SELECT COUNT(*) AS geShu FROM web_ad_campaigns where account_id = '" + accountId + "' AND status != 'ARCHIVED' ";
                    oneBySql = DB.findOneBySql(sql);
                    geShu = oneBySql.get("geShu");
                }
            } else if (!(null == tag) && !(null == country) && !"".equals(tag) && !"".equals(country)) {//应用+国家
                Map<String, Integer> facebookTagDetails = FacebookAccountBalanceFetcher.getFacebookTagDetails();
                Integer tag_id = facebookTagDetails.get(tag);
                sql = "SELECT  COUNT(*) AS geShu FROM web_ad_campaigns WHERE tag_id = '" + tag_id + "' AND country_code LIKE '%" + country + "%' AND status != 'ARCHIVED' ";
                oneBySql = DB.findOneBySql(sql);
                geShu = oneBySql.get("geShu");
            } else {
                System.out.println("参数有误！请重新输入！！");
                LOGGER.info("参数有误！请重新输入！！");
            }
            return geShu;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


}