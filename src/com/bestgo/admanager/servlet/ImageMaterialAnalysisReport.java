package com.bestgo.admanager.servlet;

import com.bestgo.admanager.DateUtil;
import com.bestgo.admanager.Utils;
import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Author: mengjun
 * Date: 2018/3/16 16:57
 * Desc: 图片素材分析报告
 */
@WebServlet(name = "ImageMaterialAnalysisReport",urlPatterns = "/image_material_analysis_report/*")
public class ImageMaterialAnalysisReport extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(!Utils.isAdmin(request,response)) return;
        JsonObject json = new JsonObject();
        String path = request.getPathInfo();

        if(path.matches("/query_image_material_analysis_report_by_tag")){
            JsonArray array = new JsonArray();
            try {
                String tagName = request.getParameter("tagName");
                String startTime = request.getParameter("startTime");
                String endTime = request.getParameter("endTime");

                //create_time >= startAddOneDay AND create_time < tomorrow
                String startAddOneDay = DateUtil.addDay(startTime,1,"yyyy-MM-dd");//北京时间
                String tomorrow = DateUtil.addDay(endTime,2,"yyyy-MM-dd");//北京时间

                String sql = "SELECT country_name,country_code FROM app_country_code_dict ORDER BY country_name";
                List<JSObject> countryList = DB.findListBySql(sql);

                sql = "SELECT DISTINCT image_path FROM ad_app_image_path_rel WHERE app_name = '" + tagName + "'";
                List<JSObject> imagePathList = DB.findListBySql(sql);
                JsonObject imagePathJO = new JsonObject();
                String imagePathStr = "";

                ArrayList<JSObject> campaignList = new ArrayList<>();
                if(imagePathList != null){
                    for(int i=0,len=imagePathList.size();i<len;i++){
                        JSObject campaign = new JSObject();
                        String facebookCampaignIdsStr = null;
                        String adwordsCampaignIdsStr = null;

                        JSObject image = imagePathList.get(i);
                        String imagePath = image.get("image_path");
                        if(i == len - 1){
                            imagePathStr += imagePath;
                        }else {
                            imagePathStr += imagePath + ",";
                        }

                        //根据图片路径匹配Facebook系列
                        sql = "SELECT campaign_id FROM ad_ads a,ad_campaigns c " +
                                " WHERE a.parent_id = c.id AND create_time >= '" + startAddOneDay + "' " +
                                " AND create_time < '" + tomorrow + "' AND image_file_path LIKE '%" + imagePath + "%'";
                        List<JSObject> facebookCampaignIdList = DB.findListBySql(sql);
                        if(facebookCampaignIdList != null && facebookCampaignIdList.size() > 0) {
                            facebookCampaignIdsStr = Utils.getStrForListDistinctByAttrWithCommmas(facebookCampaignIdList, "campaign_id", true);
                        }

                        //根据图片路径匹配Adwords系列
                        sql = "SELECT campaign_id FROM ad_campaigns_admob " +
                                " WHERE create_time >= '" + startAddOneDay + "' " +
                                " AND create_time < '" + tomorrow + "' AND image_path LIKE '%" + imagePath + "%'";
                        List<JSObject> adwordsCampaignIdList = DB.findListBySql(sql);
                        if(adwordsCampaignIdList != null && adwordsCampaignIdList.size() > 0) {
                            adwordsCampaignIdsStr = Utils.getStrForListDistinctByAttrWithCommmas(adwordsCampaignIdList, "campaign_id", true);
                        }
                        facebookCampaignIdsStr = facebookCampaignIdsStr == null ? "noData" : facebookCampaignIdsStr;
                        adwordsCampaignIdsStr = adwordsCampaignIdsStr == null ? "noData" : adwordsCampaignIdsStr;
                        campaign.put("facebook_campaign_" + i,facebookCampaignIdsStr);
                        campaign.put("adwords_campaign_" + i,adwordsCampaignIdsStr);
                        campaignList.add(campaign);
                    }
                    imagePathJO.addProperty("image_paths",imagePathStr);
                    array.add(imagePathJO);
                }
                if(imagePathList != null){
                    int size = imagePathList.size();
                    for(int c=1,len=countryList.size();c<=len;c++){
                        JsonObject countryParam = new JsonObject();
                        JSObject country = countryList.get(c-1);
                        JSObject one = null;
                        String countryCode = country.get("country_code");
                        String countryName = country.get("country_name");
                        String paramStr = countryName;
                        for(int i=0;i<size;i++){
                            double totalSpends = 0;
                            double totalClicks = 0;
                            double totalImpressionses = 0;
                            double totalInstalleds = 0;
                            JSObject campaign = campaignList.get(i);
                            String facebookCampaignIdsStr = campaign.get("facebook_campaign_" + i);
                            String adwordsCampaignIdsStr = campaign.get("adwords_campaign_" + i);
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
                            if(!"noData".equals(adwordsCampaignIdsStr)){
                                sql = "SELECT SUM(total_spend) AS total_spends,SUM(total_click) AS total_clicks," +
                                        " SUM(total_impressions) AS total_impressionses,SUM(total_installed) AS total_installeds " +
                                        " FROM web_ad_campaigns_country_history_admob " +
                                        " WHERE country_code = '" + countryCode + "' " +
                                        " AND date BETWEEN '" + startTime + "' AND '" + endTime + "'" +
                                        " AND campaign_id IN(" + adwordsCampaignIdsStr + ")";
                                one = DB.findOneBySql(sql);
                                if(one.hasObjectData()){
                                    totalSpends += Utils.convertDouble(one.get("total_spends"),0);
                                    totalClicks += Utils.convertDouble(one.get("total_clicks"),0);
                                    totalImpressionses += Utils.convertDouble(one.get("total_impressionses"),0);
                                    totalInstalleds += Utils.convertDouble(one.get("total_installeds"),0);
                                }
                            }
                            double cpa = totalInstalleds == 0 ? 0 : Utils.trimDouble(totalSpends / totalInstalleds, 3);
                            double ctr = totalImpressionses == 0 ? 0 :  Utils.trimDouble(totalClicks / totalImpressionses, 3);
                            double cvr = totalClicks == 0 ? 0 :  Utils.trimDouble(totalInstalleds / totalClicks, 3);
                            totalInstalleds = Utils.trimDouble(totalInstalleds, 3);
                            paramStr += "," + cpa + "," + totalInstalleds + "," + ctr + "," + cvr;
                        }
                        countryParam.addProperty("country_param_" + c, paramStr);
                        array.add(countryParam);
                    }
                }
                json.add("array",array);
                json.addProperty("ret", 1);
            } catch (Exception e) {
                json.addProperty("ret", 0);
                json.addProperty("message", e.getMessage());
            }
        }
        response.getWriter().write(json.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }
}
