package com.bestgo.admanager.servlet;

import com.bestgo.admanager.DateUtil;
import com.bestgo.admanager.Utils;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Author: mengjun
 * Date: 2018/3/19 20:08
 * Desc: 素材分析报告
 */
@WebServlet(name = "MaterialAnalysisReport",urlPatterns = "/material_analysis_report/*")
public class MaterialAnalysisReport extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Utils.isAdmin(request, response)) return;
        JsonObject json = new JsonObject();
        String path = request.getPathInfo();

        if (path.matches("/query_material_analysis_report_by_tag")) {
            JsonArray array = new JsonArray();
            try {
                JsonObject pathJO = new JsonObject();
                String tagName = request.getParameter("tagName");
                String startTime = request.getParameter("startTime");
                String endTime = request.getParameter("endTime");

                //create_time >= startAddOneDay AND create_time < tomorrow
                String startAddOneDay = DateUtil.addDay(startTime, 1, "yyyy-MM-dd");//北京时间
                String tomorrow = DateUtil.addDay(endTime, 2, "yyyy-MM-dd");//北京时间

                //查询所有国家
                String sql = "SELECT country_name,country_code FROM app_country_code_dict ORDER BY country_name";
                List<JSObject> countryList = DB.findListBySql(sql);

                //查询标签对应的所有图片路径
                ArrayList<JSObject> imageCampaignList = new ArrayList<>();
                sql = "SELECT DISTINCT image_path FROM ad_app_image_path_rel WHERE app_name = '" + tagName + "'";
                List<JSObject> imagePathList = DB.findListBySql(sql);
                String pathStr = "";
                if (imagePathList != null) {
                    for (int i = 0, len = imagePathList.size(); i < len; i++) {
                        JSObject campaign = new JSObject();
                        String facebookCampaignIdsStr = null;
                        String adwordsCampaignIdsStr = null;
                        JSObject image = imagePathList.get(i);
                        String imagePath = image.get("image_path");
                        pathStr += "imagePath=" + imagePath + ",";

                        //根据图片路径匹配Facebook系列
                        sql = "SELECT campaign_id FROM ad_ads a,ad_campaigns c " +
                                " WHERE a.parent_id = c.id AND create_time >= '" + startAddOneDay + "' " +
                                " AND create_time < '" + tomorrow + "' AND image_file_path LIKE '%" + imagePath + "%'";
                        List<JSObject> facebookCampaignIdList = DB.findListBySql(sql);
                        if (facebookCampaignIdList != null && facebookCampaignIdList.size() > 0) {
                            facebookCampaignIdsStr = Utils.getStrForListDistinctByAttrWithCommmas(facebookCampaignIdList, "campaign_id", true);
                        }

                        //根据图片路径匹配Adwords系列
                        sql = "SELECT campaign_id FROM ad_campaigns_admob " +
                                " WHERE create_time >= '" + startAddOneDay + "' " +
                                " AND create_time < '" + tomorrow + "' AND image_path LIKE '%" + imagePath + "%'";
                        List<JSObject> adwordsCampaignIdList = DB.findListBySql(sql);
                        if (adwordsCampaignIdList != null && adwordsCampaignIdList.size() > 0) {
                            adwordsCampaignIdsStr = Utils.getStrForListDistinctByAttrWithCommmas(adwordsCampaignIdList, "campaign_id", true);
                        }
                        facebookCampaignIdsStr = facebookCampaignIdsStr == null ? "noData" : facebookCampaignIdsStr;
                        adwordsCampaignIdsStr = adwordsCampaignIdsStr == null ? "noData" : adwordsCampaignIdsStr;
                        campaign.put("facebook_campaign_image_" + i, facebookCampaignIdsStr);
                        campaign.put("adwords_campaign_image_" + i, adwordsCampaignIdsStr);
                        imageCampaignList.add(campaign);
                    }
                }


                //查询标签对应的所有视频路径
                ArrayList<JSObject> videoCampaignList = new ArrayList<>();
                sql = "SELECT DISTINCT video_path FROM ad_app_video_path_rel WHERE app_name = '" + tagName + "'";
                List<JSObject> videoPathList = DB.findListBySql(sql);
                if(videoPathList != null){
                    for(int i=0,len=videoPathList.size();i<len;i++){
                        JSObject campaign = new JSObject();
                        String facebookCampaignIdsStr = null;

                        JSObject video = videoPathList.get(i);
                        String videoPath = video.get("video_path");
                        if(i == len - 1){
                            pathStr += "facebookVideoPath=" + videoPath;
                        }else {
                            pathStr += "facebookVideoPath=" + videoPath + ",";
                        }

                        //根据视频路径匹配Facebook系列
                        sql = "SELECT campaign_id FROM ad_ads a,ad_campaigns c " +
                                " WHERE a.parent_id = c.id AND create_time >= '" + startAddOneDay + "' " +
                                " AND create_time < '" + tomorrow + "' AND video_file_path LIKE '%" + videoPath + "%'";
                        List<JSObject> facebookCampaignIdList = DB.findListBySql(sql);
                        if(facebookCampaignIdList != null && facebookCampaignIdList.size() > 0) {
                            facebookCampaignIdsStr = Utils.getStrForListDistinctByAttrWithCommmas(facebookCampaignIdList, "campaign_id", true);
                        }

                        facebookCampaignIdsStr = facebookCampaignIdsStr == null ? "noData" : facebookCampaignIdsStr;
                        campaign.put("facebook_campaign_video_" + i,facebookCampaignIdsStr);
                        videoCampaignList.add(campaign);
                    }
                }

                //查询标签对应的所有广告组1
                String groupOneCampaignsStr = "";
                sql = "SELECT campaign_id FROM ad_campaigns WHERE app_name = '" + tagName + "' " +
                        " AND create_time >= '" + startAddOneDay + "'" + " AND create_time < '" + tomorrow + "'" +
                        " AND group_id = 1";
                List<JSObject> facebookGroupOneCampaignList = DB.findListBySql(sql);
                if(facebookGroupOneCampaignList != null && facebookGroupOneCampaignList.size() > 0){
                    String facebookGroupOneCampaignsStr = Utils.getStrForListDistinctByAttrWithCommmas(facebookGroupOneCampaignList,"campaign_id", true);
                    groupOneCampaignsStr = facebookGroupOneCampaignsStr == null ? "" : facebookGroupOneCampaignsStr;
                }
                sql = "SELECT campaign_id FROM ad_campaigns_admob WHERE app_name = '" + tagName + "' " +
                        " AND create_time >= '" + startAddOneDay + "'" + " AND create_time < '" + tomorrow + "'" +
                        " AND group_id = 1";
                List<JSObject> admobGroupOneCampaignList = DB.findListBySql(sql);
                if(admobGroupOneCampaignList != null && admobGroupOneCampaignList.size() > 0){
                    String admobGroupOneCampaignsStr = Utils.getStrForListDistinctByAttrWithCommmas(admobGroupOneCampaignList,"campaign_id", true);
                    groupOneCampaignsStr = admobGroupOneCampaignsStr == null ? groupOneCampaignsStr : groupOneCampaignsStr + "," + admobGroupOneCampaignsStr;
                }

                //查询标签对应的所有广告组2
                String groupTwoCampaignsStr = "";
                sql = "SELECT campaign_id FROM ad_campaigns WHERE app_name = '" + tagName + "' " +
                        " AND create_time >= '" + startAddOneDay + "'" + " AND create_time < '" + tomorrow + "'" +
                        " AND group_id = 2";
                List<JSObject> facebookGroupTwoCampaignList = DB.findListBySql(sql);
                if(facebookGroupTwoCampaignList != null && facebookGroupTwoCampaignList.size() > 0){
                    String facebookGroupTwoCampaignsStr = Utils.getStrForListDistinctByAttrWithCommmas(facebookGroupTwoCampaignList,"campaign_id", true);
                    groupTwoCampaignsStr = facebookGroupTwoCampaignsStr == null ? "" : facebookGroupTwoCampaignsStr;
                }
                sql = "SELECT campaign_id FROM ad_campaigns_admob WHERE app_name = '" + tagName + "' " +
                        " AND create_time >= '" + startAddOneDay + "'" + " AND create_time < '" + tomorrow + "'" +
                        " AND group_id = 2";
                List<JSObject> admobGroupTwoCampaignList = DB.findListBySql(sql);
                if(admobGroupTwoCampaignList != null && admobGroupTwoCampaignList.size() > 0){
                    String admobGroupTwoCampaignsStr = Utils.getStrForListDistinctByAttrWithCommmas(admobGroupTwoCampaignList,"campaign_id", true);
                    groupTwoCampaignsStr = admobGroupTwoCampaignsStr == null ? groupTwoCampaignsStr : groupTwoCampaignsStr + "," + admobGroupTwoCampaignsStr;
                }

                //查询标签对应的所有广告组3
                String groupThreeCampaignsStr = "";
                sql = "SELECT campaign_id FROM ad_campaigns WHERE app_name = '" + tagName + "' " +
                        " AND create_time >= '" + startAddOneDay + "'" + " AND create_time < '" + tomorrow + "'" +
                        " AND group_id = 3";
                List<JSObject> facebookGroupThreeCampaignList = DB.findListBySql(sql);
                if(facebookGroupThreeCampaignList != null && facebookGroupThreeCampaignList.size() > 0){
                    String facebookGroupThreeCampaignsStr = Utils.getStrForListDistinctByAttrWithCommmas(facebookGroupThreeCampaignList,"campaign_id", true);
                    groupThreeCampaignsStr = facebookGroupThreeCampaignsStr == null ? "" : facebookGroupThreeCampaignsStr;
                }
                sql = "SELECT campaign_id FROM ad_campaigns_admob WHERE app_name = '" + tagName + "' " +
                        " AND create_time >= '" + startAddOneDay + "'" + " AND create_time < '" + tomorrow + "'" +
                        " AND group_id = 3";
                List<JSObject> admobGroupThreeCampaignList = DB.findListBySql(sql);
                if(admobGroupThreeCampaignList != null && admobGroupThreeCampaignList.size() > 0){
                    String admobGroupThreeCampaignsStr = Utils.getStrForListDistinctByAttrWithCommmas(admobGroupThreeCampaignList,"campaign_id", true);
                    groupThreeCampaignsStr = admobGroupThreeCampaignsStr == null ? groupThreeCampaignsStr : groupThreeCampaignsStr + "," + admobGroupThreeCampaignsStr;
                }


                //查询标签对应的所有广告组4
                String groupFourCampaignsStr = "";
                sql = "SELECT campaign_id FROM ad_campaigns WHERE app_name = '" + tagName + "' " +
                        " AND create_time >= '" + startAddOneDay + "'" + " AND create_time < '" + tomorrow + "'" +
                        " AND group_id = 4";
                List<JSObject> facebookGroupFourCampaignList = DB.findListBySql(sql);
                if(facebookGroupFourCampaignList != null && facebookGroupFourCampaignList.size() > 0){
                    String facebookGroupFourCampaignsStr = Utils.getStrForListDistinctByAttrWithCommmas(facebookGroupFourCampaignList,"campaign_id", true);
                    groupFourCampaignsStr = facebookGroupFourCampaignsStr == null ? "" : facebookGroupFourCampaignsStr;
                }
                sql = "SELECT campaign_id FROM ad_campaigns_admob WHERE app_name = '" + tagName + "' " +
                        " AND create_time >= '" + startAddOneDay + "'" + " AND create_time < '" + tomorrow + "'" +
                        " AND group_id = 4";
                List<JSObject> admobGroupFourCampaignList = DB.findListBySql(sql);
                if(admobGroupFourCampaignList != null && admobGroupFourCampaignList.size() > 0){
                    String admobGroupFourCampaignsStr = Utils.getStrForListDistinctByAttrWithCommmas(admobGroupFourCampaignList,"campaign_id", true);
                    groupFourCampaignsStr = admobGroupFourCampaignsStr == null ? groupFourCampaignsStr : groupFourCampaignsStr + "," + admobGroupFourCampaignsStr;
                }


                pathJO.addProperty("paths",pathStr + ",广告语组1" + ",广告语组2" + ",广告语组3" + ",广告语组4");
                array.add(pathJO);

                double totalSpends = 0;
                double totalClicks = 0;
                double totalImpressionses = 0;
                double totalInstalleds = 0;
                for (int c = 1, len = countryList.size(); c <= len; c++) {
                    JsonObject countryParam = new JsonObject();
                    JSObject country = countryList.get(c - 1);
                    JSObject one = null;
                    String countryCode = country.get("country_code");
                    String countryName = country.get("country_name");
                    String paramStr = countryName;
                    if(imagePathList != null){
                        for (int i = 0,length = imagePathList.size(); i < length; i++) {
                            JSObject campaign = imageCampaignList.get(i);
                            String facebookCampaignIdsStr = campaign.get("facebook_campaign_image_" + i);
                            String adwordsCampaignIdsStr = campaign.get("adwords_campaign_image_" + i);
                            if (!"noData".equals(facebookCampaignIdsStr)) {
                                sql = "SELECT SUM(total_spend) AS total_spends,SUM(total_click) AS total_clicks," +
                                        " SUM(total_impressions) AS total_impressionses,SUM(total_installed) AS total_installeds " +
                                        " FROM web_ad_campaigns_country_history " +
                                        " WHERE country_code = '" + countryCode + "' " +
                                        " AND date BETWEEN '" + startTime + "' AND '" + endTime + "'" +
                                        " AND campaign_id IN(" + facebookCampaignIdsStr + ")";
                                one = DB.findOneBySql(sql);
                                if (one.hasObjectData()) {
                                    totalSpends = Utils.convertDouble(one.get("total_spends"), 0);
                                    totalClicks = Utils.convertDouble(one.get("total_clicks"), 0);
                                    totalImpressionses = Utils.convertDouble(one.get("total_impressionses"), 0);
                                    totalInstalleds = Utils.convertDouble(one.get("total_installeds"), 0);
                                }
                            }
                            if (!"noData".equals(adwordsCampaignIdsStr)) {
                                sql = "SELECT SUM(total_spend) AS total_spends,SUM(total_click) AS total_clicks," +
                                        " SUM(total_impressions) AS total_impressionses,SUM(total_installed) AS total_installeds " +
                                        " FROM web_ad_campaigns_country_history_admob " +
                                        " WHERE country_code = '" + countryCode + "' " +
                                        " AND date BETWEEN '" + startTime + "' AND '" + endTime + "'" +
                                        " AND campaign_id IN(" + adwordsCampaignIdsStr + ")";
                                one = DB.findOneBySql(sql);
                                if (one.hasObjectData()) {
                                    totalSpends += Utils.convertDouble(one.get("total_spends"), 0);
                                    totalClicks += Utils.convertDouble(one.get("total_clicks"), 0);
                                    totalImpressionses += Utils.convertDouble(one.get("total_impressionses"), 0);
                                    totalInstalleds += Utils.convertDouble(one.get("total_installeds"), 0);
                                }
                            }
                            double cpa = totalInstalleds == 0 ? 0 : Utils.trimDouble(totalSpends / totalInstalleds, 3);
                            double ctr = totalImpressionses == 0 ? 0 : Utils.trimDouble(totalClicks / totalImpressionses, 3);
                            double cvr = totalClicks == 0 ? 0 : Utils.trimDouble(totalInstalleds / totalClicks, 3);
                            totalInstalleds = Utils.trimDouble(totalInstalleds, 3);
                            paramStr += "," + cpa + "," + totalInstalleds + "," + ctr + "," + cvr;
                            totalSpends = 0;
                            totalClicks = 0;
                            totalImpressionses = 0;
                            totalInstalleds = 0;
                        }
                    }
                    if(videoPathList != null){
                        for(int i = 0,length = videoPathList.size();i < length;i++){
                            JSObject campaign = videoCampaignList.get(i);
                            String facebookCampaignIdsStr = campaign.get("facebook_campaign_video_" + i);
                            if(!"noData".equals(facebookCampaignIdsStr)){
                                sql = "SELECT SUM(total_spend) AS total_spends,SUM(total_click) AS total_clicks," +
                                        " SUM(total_impressions) AS total_impressionses,SUM(total_installed) AS total_installeds " +
                                        " FROM web_ad_campaigns_country_history " +
                                        " WHERE country_code = '" + countryCode + "' " +
                                        " AND date BETWEEN '" + startTime + "' AND '" + endTime + "'" +
                                        " AND campaign_id IN(" + facebookCampaignIdsStr + ")";
                                one = DB.findOneBySql(sql);
                                if(one.hasObjectData()){
                                    totalSpends = Utils.convertDouble(one.get("total_spends"),0);
                                    totalClicks = Utils.convertDouble(one.get("total_clicks"),0);
                                    totalImpressionses = Utils.convertDouble(one.get("total_impressionses"),0);
                                    totalInstalleds = Utils.convertDouble(one.get("total_installeds"),0);
                                }
                            }

                            double cpa = totalInstalleds == 0 ? 0 : Utils.trimDouble(totalSpends / totalInstalleds, 3);
                            double ctr = totalImpressionses == 0 ? 0 :  Utils.trimDouble(totalClicks / totalImpressionses, 3);
                            double cvr = totalClicks == 0 ? 0 :  Utils.trimDouble(totalInstalleds / totalClicks, 3);
                            totalInstalleds = Utils.trimDouble(totalInstalleds, 3);
                            paramStr += "," + cpa + "," + totalInstalleds + "," + ctr + "," + cvr;
                            totalSpends = 0;
                            totalClicks = 0;
                            totalImpressionses = 0;
                            totalInstalleds = 0;
                        }
                    }

                    if(groupOneCampaignsStr == ""){
                        paramStr += ",0,0,0,0";
                    }else {
                        sql = "SELECT SUM(total_spend) AS total_spends,SUM(total_click) AS total_clicks," +
                                " SUM(total_impressions) AS total_impressionses,SUM(total_installed) AS total_installeds " +
                                " FROM web_ad_campaigns_country_history " +
                                " WHERE country_code = '" + countryCode + "' " +
                                " AND date BETWEEN '" + startTime + "' AND '" + endTime + "'" +
                                " AND campaign_id IN(" + groupOneCampaignsStr + ")";
                        one = DB.findOneBySql(sql);
                        if(one.hasObjectData()){
                            totalSpends = Utils.convertDouble(one.get("total_spends"),0);
                            totalClicks = Utils.convertDouble(one.get("total_clicks"),0);
                            totalImpressionses = Utils.convertDouble(one.get("total_impressionses"),0);
                            double cpa = totalInstalleds == 0 ? 0 : Utils.trimDouble(totalSpends / totalInstalleds, 3);
                            double ctr = totalImpressionses == 0 ? 0 :  Utils.trimDouble(totalClicks / totalImpressionses, 3);
                            double cvr = totalClicks == 0 ? 0 :  Utils.trimDouble(totalInstalleds / totalClicks, 3);
                            totalInstalleds = Utils.convertDouble(one.get("total_installeds"),0);
                            paramStr += "," + cpa + "," + totalInstalleds + "," + ctr + "," + cvr;
                            totalSpends = 0;
                            totalClicks = 0;
                            totalImpressionses = 0;
                            totalInstalleds = 0;
                        }
                    }

                    if(groupTwoCampaignsStr == ""){
                        paramStr += ",0,0,0,0";
                    }else {
                        sql = "SELECT SUM(total_spend) AS total_spends,SUM(total_click) AS total_clicks," +
                                " SUM(total_impressions) AS total_impressionses,SUM(total_installed) AS total_installeds " +
                                " FROM web_ad_campaigns_country_history " +
                                " WHERE country_code = '" + countryCode + "' " +
                                " AND date BETWEEN '" + startTime + "' AND '" + endTime + "'" +
                                " AND campaign_id IN(" + groupTwoCampaignsStr + ")";
                        one = DB.findOneBySql(sql);
                        if(one.hasObjectData()){
                            totalSpends = Utils.convertDouble(one.get("total_spends"),0);
                            totalClicks = Utils.convertDouble(one.get("total_clicks"),0);
                            totalImpressionses = Utils.convertDouble(one.get("total_impressionses"),0);
                            double cpa = totalInstalleds == 0 ? 0 : Utils.trimDouble(totalSpends / totalInstalleds, 3);
                            double ctr = totalImpressionses == 0 ? 0 :  Utils.trimDouble(totalClicks / totalImpressionses, 3);
                            double cvr = totalClicks == 0 ? 0 :  Utils.trimDouble(totalInstalleds / totalClicks, 3);
                            totalInstalleds = Utils.convertDouble(one.get("total_installeds"),0);
                            paramStr += "," + cpa + "," + totalInstalleds + "," + ctr + "," + cvr;
                            totalSpends = 0;
                            totalClicks = 0;
                            totalImpressionses = 0;
                            totalInstalleds = 0;
                        }
                    }


                    if(groupThreeCampaignsStr == ""){
                        paramStr += ",0,0,0,0";
                    }else {
                        sql = "SELECT SUM(total_spend) AS total_spends,SUM(total_click) AS total_clicks," +
                                " SUM(total_impressions) AS total_impressionses,SUM(total_installed) AS total_installeds " +
                                " FROM web_ad_campaigns_country_history " +
                                " WHERE country_code = '" + countryCode + "' " +
                                " AND date BETWEEN '" + startTime + "' AND '" + endTime + "'" +
                                " AND campaign_id IN(" + groupThreeCampaignsStr + ")";
                        one = DB.findOneBySql(sql);
                        if(one.hasObjectData()){
                            totalSpends = Utils.convertDouble(one.get("total_spends"),0);
                            totalClicks = Utils.convertDouble(one.get("total_clicks"),0);
                            totalImpressionses = Utils.convertDouble(one.get("total_impressionses"),0);
                            double cpa = totalInstalleds == 0 ? 0 : Utils.trimDouble(totalSpends / totalInstalleds, 3);
                            double ctr = totalImpressionses == 0 ? 0 :  Utils.trimDouble(totalClicks / totalImpressionses, 3);
                            double cvr = totalClicks == 0 ? 0 :  Utils.trimDouble(totalInstalleds / totalClicks, 3);
                            totalInstalleds = Utils.convertDouble(one.get("total_installeds"),0);
                            paramStr += "," + cpa + "," + totalInstalleds + "," + ctr + "," + cvr;
                            totalSpends = 0;
                            totalClicks = 0;
                            totalImpressionses = 0;
                            totalInstalleds = 0;
                        }
                    }


                    if(groupFourCampaignsStr == ""){
                        paramStr += ",0,0,0,0";
                    }else {
                        sql = "SELECT SUM(total_spend) AS total_spends,SUM(total_click) AS total_clicks," +
                                " SUM(total_impressions) AS total_impressionses,SUM(total_installed) AS total_installeds " +
                                " FROM web_ad_campaigns_country_history " +
                                " WHERE country_code = '" + countryCode + "' " +
                                " AND date BETWEEN '" + startTime + "' AND '" + endTime + "'" +
                                " AND campaign_id IN(" + groupFourCampaignsStr + ")";
                        one = DB.findOneBySql(sql);
                        if(one.hasObjectData()){
                            totalSpends = Utils.convertDouble(one.get("total_spends"),0);
                            totalClicks = Utils.convertDouble(one.get("total_clicks"),0);
                            totalImpressionses = Utils.convertDouble(one.get("total_impressionses"),0);
                            double cpa = totalInstalleds == 0 ? 0 : Utils.trimDouble(totalSpends / totalInstalleds, 3);
                            double ctr = totalImpressionses == 0 ? 0 :  Utils.trimDouble(totalClicks / totalImpressionses, 3);
                            double cvr = totalClicks == 0 ? 0 :  Utils.trimDouble(totalInstalleds / totalClicks, 3);
                            totalInstalleds = Utils.convertDouble(one.get("total_installeds"),0);
                            paramStr += "," + cpa + "," + totalInstalleds + "," + ctr + "," + cvr;
                            totalSpends = 0;
                            totalClicks = 0;
                            totalImpressionses = 0;
                            totalInstalleds = 0;
                        }
                    }


                    countryParam.addProperty("country_param_" + c, paramStr);
                    array.add(countryParam);
                }
                json.add("array", array);
                json.addProperty("ret", 1);
            } catch (Exception e) {
                json.addProperty("ret", 0);
                json.addProperty("message", e.getMessage());
            }
        }
        response.getWriter().write(json.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}