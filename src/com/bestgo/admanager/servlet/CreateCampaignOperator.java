package com.bestgo.admanager.servlet;

import com.bestgo.admanager.utils.Utils;
import com.bestgo.common.database.services.DB;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Author: mengjun
 * Date: 2018/1/31 19:22
 * Desc: 针对创建系列监控页面进行的一系列操作
 */
@WebServlet(name = "CreateCampaignOperator", urlPatterns = "/create_campaign_operator/*")
public class CreateCampaignOperator extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Utils.isAdmin(request, response)) return;

        String path = request.getPathInfo();
        JsonObject json = new JsonObject();

        if (path.startsWith("/delete_error_message_of_campaign")) {
            String delitems = request.getParameter("delitems");
            String[] splitOuter = delitems.split(",");
            String facebook_ids = "";
            String adwords_ids = "";
            for(String s : splitOuter){
                if(s.contains("Facebook")){
                    facebook_ids += s.substring(0,s.indexOf("-")) + ",";
                }else if(s.contains("AdWords")){
                    adwords_ids += s.substring(0,s.indexOf("-")) + ",";
                }
            }

            if(facebook_ids != ""){
                facebook_ids = facebook_ids.substring(0,facebook_ids.length()-1);
            }
            if(adwords_ids != ""){
                adwords_ids = adwords_ids.substring(0,adwords_ids.length()-1);
            }
            if(facebook_ids != ""){
                String sqlD = "DELETE from ad_campaigns where id in (" + facebook_ids + ")";
                try {
                    DB.updateBySql(sqlD);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(adwords_ids != ""){
                String sqlD = "DELETE from ad_campaigns_admob where id in (" + adwords_ids + ")";
                try {
                    DB.updateBySql(sqlD);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            json.addProperty("ret", 1);
        } else if (path.startsWith("/modified_failed_count_of_campaign")) {
            String modifiedms = request.getParameter("modifiedms");
            String[] splitOuter = modifiedms.split(",");
            String facebook_ids = "";
            String adwords_ids = "";
            for(String s : splitOuter){
                if(s.contains("Facebook")){
                    facebook_ids += s.substring(0,s.indexOf("-")) + ",";
                }else if(s.contains("AdWords")){
                    adwords_ids += s.substring(0,s.indexOf("-")) + ",";
                }
            }

            if(facebook_ids != ""){
                facebook_ids = facebook_ids.substring(0,facebook_ids.length()-1);
            }
            if(adwords_ids != ""){
                adwords_ids = adwords_ids.substring(0,adwords_ids.length()-1);
            }
            if(facebook_ids != ""){
                String sqlD = "update ad_campaigns set failed_count = 0 where id in (" + facebook_ids + ")";
                try {
                    DB.updateBySql(sqlD);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(adwords_ids != ""){
                String sqlD = "update ad_campaigns_admob set failed_count = 0 where id in (" + adwords_ids + ")";
                try {
                    DB.updateBySql(sqlD);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            json.addProperty("ret", 1);
        }

        response.getWriter().write(json.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
