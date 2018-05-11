package com.bestgo.admanager.servlet;

import com.bestgo.admanager.utils.Utils;
import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: mengjun
 * Date: 2018/3/19 20:08
 * Desc: 素材分析报告
 */
@WebServlet(name = "MaterialAnalysisReport",urlPatterns = "/material_analysis_report/*")
public class MaterialAnalysisReport extends HttpServlet {
    class CampaignResourceItem {
        public String campaignId;
        public String network;
        public String title;
        public String message;
        public String appName;
        public String message1;
        public String message2;
        public String message3;
        public String message4;
        public String imagePath;
        public String videoPath;
    }

    class ResourceAnalysisItem {
        public String network;
        public String title;
        public String message;
        public String message1;
        public String message2;
        public String message3;
        public String message4;
        public String imagePath;
        public String videoPath;

        public double spend;
        public long installed;
        public long impressions;
        public long click;
        public double cpa;
        public double ctr;
    }
    static ConcurrentHashMap<String, CampaignResourceItem> cacheCampaigns = new ConcurrentHashMap<>();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Utils.isAdmin(request, response)) return;
        JsonObject json = new JsonObject();
        String path = request.getPathInfo();

        if (path.matches("/query_material_analysis_report_by_tag")) {
            JsonArray array = new JsonArray();
            try {
                String tagName = request.getParameter("tagName");
                String startTime = request.getParameter("startTime");
                String endTime = request.getParameter("endTime");
                int sortId = Utils.parseInt(request.getParameter("sortId"), 1001);
                int inputCount = Utils.parseInt(request.getParameter("inputCount"), 100);
                if (tagName != null && tagName.isEmpty()) {
                    json.addProperty("ret", 0);
                    json.addProperty("message", "标签不能为空");
                    response.getWriter().write(json.toString());
                    return;
                }

                HashMap<String, ResourceAnalysisItem> resultMap = new HashMap<>();

                JSObject tagObject = DB.simpleScan("web_tag")
                        .select("id", "tag_name")
                        .where(DB.filter().whereEqualTo("tag_name", tagName)).execute();
                if (!tagObject.hasObjectData()) {
                    json.addProperty("ret", 0);
                    json.addProperty("message", "标签不能为空");
                    response.getWriter().write(json.toString());
                    return;
                }

                long tagId = tagObject.get("id");

                String sql = "select h.campaign_id, sum(total_click) as total_click, sum(total_impressions) as total_impressions, " +
                        "sum(total_spend) as total_spend, sum(total_installed) as total_installed from web_ad_campaigns_history h, web_ad_campaign_tag_rel r " +
                        "where date between ? and ? and h.campaign_id=r.campaign_id and r.tag_id=? group by campaign_id having total_installed > 0;";
                List<JSObject> list = DB.findListBySql(sql, startTime, endTime, tagId);
                for (int i = 0; i < list.size(); i++) {
                    String campaignId = list.get(i).get("campaign_id");
                    double spend = Utils.convertDouble(list.get(i).get("total_spend"), 0);
                    long installed = Utils.convertLong(list.get(i).get("total_installed"), 0);
                    long impressions = Utils.convertLong(list.get(i).get("total_impressions"), 0);
                    long click = Utils.convertLong(list.get(i).get("total_click"), 0);

                    CampaignResourceItem item = cacheCampaigns.get(campaignId);
                    if (item == null) {
                        String sql1 = "select app_name, title, message, image_file_path, video_file_path, thumbnail_image_file_path from ad_campaigns, ad_ads " +
                                "where campaign_id=? and ad_campaigns.id=ad_ads.parent_id";
                        JSObject one = DB.findOneBySql(sql1, campaignId);
                        if (one.hasObjectData()) {
                            item = new CampaignResourceItem();
                            cacheCampaigns.put(campaignId, item);
                            item.network = "facebook";
                            item.appName = one.get("app_name");
                            item.title = one.get("title");
                            item.message = one.get("message");
                            item.imagePath = one.get("image_file_path");
                            if (item.imagePath.isEmpty()) {
                                item.imagePath = one.get("thumbnail_image_file_path");
                            }
                            item.videoPath = one.get("video_file_path");
                            item.campaignId = campaignId;
                        }
                        if (item == null || !item.appName.equals(tagName)) {
                            continue;
                        }
                        String key = String.format("%s_%s_%s_%s", item.title, item.message, item.imagePath, item.videoPath);
                        ResourceAnalysisItem resourceAnalysisItem = resultMap.get(key);
                        if (resourceAnalysisItem == null) {
                            resourceAnalysisItem = new ResourceAnalysisItem();
                            resultMap.put(key, resourceAnalysisItem);
                        }
                        resourceAnalysisItem.network = "facebook";
                        resourceAnalysisItem.title = item.title;
                        resourceAnalysisItem.message = item.message;
                        resourceAnalysisItem.imagePath = item.imagePath;
                        resourceAnalysisItem.videoPath = item.videoPath;
                        resourceAnalysisItem.spend += spend;
                        resourceAnalysisItem.installed += installed;
                        resourceAnalysisItem.impressions += impressions;
                        resourceAnalysisItem.click += click;
                        resourceAnalysisItem.ctr = Utils.trimDouble(resourceAnalysisItem.impressions > 0 ? resourceAnalysisItem.click * 1.0 / resourceAnalysisItem.impressions : 0, 3);
                        resourceAnalysisItem.cpa = Utils.trimDouble(resourceAnalysisItem.installed > 0 ? resourceAnalysisItem.spend / resourceAnalysisItem.installed : 0, 3);
                    }
                }

                sql = "select h.campaign_id, sum(total_click) as total_click, sum(total_impressions) as total_impressions, " +
                        "sum(total_spend) as total_spend, sum(total_installed) as total_installed from web_ad_campaigns_history_admob h, web_ad_campaign_tag_admob_rel r " +
                        "where date between ? and ? and h.campaign_id=r.campaign_id and r.tag_id=? group by campaign_id having total_installed > 0;";
                list = DB.findListBySql(sql, startTime, endTime, tagId);
                for (int i = 0; i < list.size(); i++) {
                    String campaignId = list.get(i).get("campaign_id");
                    double spend = Utils.convertDouble(list.get(i).get("total_spend"), 0);
                    long installed = Utils.convertLong(list.get(i).get("total_installed"), 0);
                    long impressions = Utils.convertLong(list.get(i).get("total_impressions"), 0);
                    long click = Utils.convertLong(list.get(i).get("total_click"), 0);

                    CampaignResourceItem item = cacheCampaigns.get(campaignId);
                    if (item == null) {
                        String sql1 = "select app_name, message1, message2, message3, message4, image_path from ad_campaigns_admob where campaign_id=?";
                        JSObject one = DB.findOneBySql(sql1, campaignId);
                        if (one.hasObjectData()) {
                            item = new CampaignResourceItem();
                            cacheCampaigns.put(campaignId, item);
                            item.network = "adwords";
                            item.appName = one.get("app_name");
                            item.message1 = one.get("message1");
                            item.message2 = one.get("message2");
                            item.message3 = one.get("message3");
                            item.message4 = one.get("message4");
                            item.imagePath = one.get("image_path");
                            item.campaignId = campaignId;
                        }
                    }
                    if (item == null || !item.appName.equals(tagName)) {
                        continue;
                    }
                    String key = String.format("%s_%s_%s_%s_%s", item.message1, item.message2, item.message3, item.message4, item.imagePath);
                    ResourceAnalysisItem resourceAnalysisItem = resultMap.get(key);
                    if (resourceAnalysisItem == null) {
                        resourceAnalysisItem = new ResourceAnalysisItem();
                        resultMap.put(key, resourceAnalysisItem);
                    }
                    resourceAnalysisItem.network = "adwords";
                    resourceAnalysisItem.message1 = item.message1;
                    resourceAnalysisItem.message2 = item.message2;
                    resourceAnalysisItem.message3 = item.message3;
                    resourceAnalysisItem.message4 = item.message4;
                    resourceAnalysisItem.imagePath = item.imagePath;
                    resourceAnalysisItem.spend += spend;
                    resourceAnalysisItem.installed += installed;
                    resourceAnalysisItem.impressions += impressions;
                    resourceAnalysisItem.click += click;
                    resourceAnalysisItem.ctr = Utils.trimDouble(resourceAnalysisItem.impressions > 0 ? resourceAnalysisItem.click * 1.0 / resourceAnalysisItem.impressions : 0, 3);
                    resourceAnalysisItem.cpa = Utils.trimDouble(resourceAnalysisItem.installed > 0 ? resourceAnalysisItem.spend / resourceAnalysisItem.installed : 0, 3);
                }

                Collection<ResourceAnalysisItem> results = resultMap.values();

                ArrayList<ResourceAnalysisItem> sortArr = new ArrayList<>(results);
                sortArr.sort(new Comparator<ResourceAnalysisItem>() {
                    @Override
                    public int compare(ResourceAnalysisItem o1, ResourceAnalysisItem o2) {
                        int ret = 0;
                        switch (sortId) {
                            case 1:
                            case 1001:
                                if (o1.spend > o2.spend) {
                                    ret = sortId > 1000 ? -1 : 1;
                                } else if (o1.spend < o2.spend) {
                                    ret = sortId > 1000 ? 1 : -1;
                                }
                                break;
                            case 2:
                            case 1002:
                                if (o1.installed > o2.installed) {
                                    ret = sortId > 1000 ? -1 : 1;
                                } else if (o1.installed < o2.installed) {
                                    ret = sortId > 1000 ? 1 : -1;
                                }
                                break;
                            case 3:
                            case 1003:
                                if (o1.click > o2.click) {
                                    ret = sortId > 1000 ? -1 : 1;
                                } else if (o1.click < o2.click) {
                                    ret = sortId > 1000 ? 1 : -1;
                                }
                                break;
                            case 4:
                            case 1004:
                                if (o1.impressions > o2.impressions) {
                                    ret = sortId > 1000 ? -1 : 1;
                                } else if (o1.impressions < o2.impressions) {
                                    ret = sortId > 1000 ? 1 : -1;
                                }
                                break;
                            case 5:
                            case 1005:
                                if (o1.cpa > o2.cpa) {
                                    ret = sortId > 1000 ? -1 : 1;
                                } else if (o1.cpa < o2.cpa) {
                                    ret = sortId > 1000 ? 1 : -1;
                                }
                                break;
                            case 6:
                            case 1006:
                                if (o1.ctr > o2.ctr) {
                                    ret = sortId > 1000 ? -1 : 1;
                                } else if (o1.ctr < o2.ctr) {
                                    ret = sortId > 1000 ? 1 : -1;
                                }
                                break;
                            default:
                                if (o1.spend > o2.spend) {
                                    ret = sortId > 1000 ? -1 : 1;
                                } else if (o1.spend < o2.spend) {
                                    ret = sortId > 1000 ? 1 : -1;
                                }
                        }
                        return ret;
                    }
                });
                for (int i = 0; i < sortArr.size(); i++) {
                    if (sortId > 1000) {
                        if (i >= inputCount) {
                            continue;
                        }
                    } else {
                        if (i < sortArr.size() - inputCount) {
                            continue;
                        }
                    }
                    JsonObject one = new JsonObject();
                    one.addProperty("network", sortArr.get(i).network);
                    one.addProperty("title", sortArr.get(i).title);
                    one.addProperty("message", sortArr.get(i).message);
                    one.addProperty("message1", sortArr.get(i).message1);
                    one.addProperty("message2", sortArr.get(i).message2);
                    one.addProperty("message3", sortArr.get(i).message3);
                    one.addProperty("message4", sortArr.get(i).message4);
                    one.addProperty("imagePath", sortArr.get(i).imagePath);
                    one.addProperty("videoPath", sortArr.get(i).videoPath);
                    one.addProperty("installed", sortArr.get(i).installed);
                    one.addProperty("click", sortArr.get(i).click);
                    one.addProperty("impressions", sortArr.get(i).impressions);
                    one.addProperty("spend", Utils.trimDouble(sortArr.get(i).spend, 2));
                    one.addProperty("ctr", sortArr.get(i).ctr);
                    one.addProperty("cpa", sortArr.get(i).cpa);
                    array.add(one);
                }

                json.add("array", array);
                json.addProperty("ret", 1);
            } catch (Exception e) {
                e.printStackTrace();
                json.addProperty("ret", 0);
                json.addProperty("message", e.getMessage());
            }
        }
        response.getWriter().write(json.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    private void initCache() {
        if (cacheCampaigns.size() == 0) {
            String sql = "select ad_campaigns.campaign_id, app_name, title, message, image_file_path, video_file_path, thumbnail_image_file_path from ad_campaigns, ad_ads, " +
                    "(select campaign_id, sum(total_installed) as installed from web_ad_campaigns_history where date > '2018-3-1' group by campaign_id having installed > 0) h " +
                    "where ad_campaigns.id=ad_ads.parent_id and h.campaign_id=ad_campaigns.campaign_id";
            try {
                List<JSObject> list = DB.findListBySql(sql);
                for (int i = 0; i < list.size(); i++) {
                    JSObject one = list.get(i);
                    String campaignId = one.get("campaign_id");
                    CampaignResourceItem item = cacheCampaigns.get(campaignId);
                    if (item == null) {
                        item = new CampaignResourceItem();
                        cacheCampaigns.put(campaignId, item);
                        item.network = "facebook";
                        item.appName = one.get("app_name");
                        item.title = one.get("title");
                        item.message = one.get("message");
                        item.imagePath = one.get("image_file_path");
                        if (item.imagePath.isEmpty()) {
                            item.imagePath = one.get("thumbnail_image_file_path");
                        }
                        item.videoPath = one.get("video_file_path");
                        item.campaignId = campaignId;
                    }
                }
            } catch (Exception e) {
            }

            sql = "select c.campaign_id, app_name, message1, message2, message3, message4, image_path from ad_campaigns_admob c, " +
                    "(select campaign_id, sum(total_installed) as installed from web_ad_campaigns_history_admob where date > '2018-3-1' group by campaign_id having installed > 0) h " +
                    "where c.campaign_id=h.campaign_id";
            try {
                List<JSObject> list = DB.findListBySql(sql);
                for (int i = 0; i < list.size(); i++) {
                    JSObject one = list.get(i);
                    String campaignId = one.get("campaign_id");
                    CampaignResourceItem item = cacheCampaigns.get(campaignId);
                    if (item == null) {
                        item = new CampaignResourceItem();
                        cacheCampaigns.put(campaignId, item);
                        item.network = "adwords";
                        item.appName = one.get("app_name");
                        item.message1 = one.get("message1");
                        item.message2 = one.get("message2");
                        item.message3 = one.get("message3");
                        item.message4 = one.get("message4");
                        item.imagePath = one.get("image_path");
                        item.campaignId = campaignId;
                    }
                }
            } catch (Exception e) {
            }
        }
    }
}