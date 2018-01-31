package com.bestgo.admanager.servlet;

import com.bestgo.admanager.Utils;
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
 * Date: 2018/1/31 15:22
 * Desc: 针对web_ad_batch_change_campaigns表进行的一系列操作
 */
@WebServlet(name = "BatchChangeCampaignOperator", urlPatterns = "/batch_change_campaign_operator/*")
public class BatchChangeCampaignOperator extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Utils.isAdmin(request, response)) return;

        String path = request.getPathInfo();
        JsonObject json = new JsonObject();

        if (path.startsWith("/delete_error_message_of_batch_change_status")) {
            String delitems = request.getParameter("delitems");
            String sqlD = "DELETE from web_ad_batch_change_campaigns where id in (" + delitems + ")";
            try {
                boolean b = DB.updateBySql(sqlD);
                if(b){
                    json.addProperty("ret", 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (path.startsWith("/modified_failed_count_of_batch_change_status")) {
            String modifiedms = request.getParameter("modifiedms");
            String sqlD = "update web_ad_batch_change_campaigns set failed_count = 0 where id in (" + modifiedms + ")";
            try {
                boolean b = DB.updateBySql(sqlD);
                if(b){
                    json.addProperty("ret", 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        response.getWriter().write(json.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
