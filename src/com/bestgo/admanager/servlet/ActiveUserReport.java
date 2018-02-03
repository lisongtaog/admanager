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
import java.util.List;

/**
 * Author: mengjun
 * Date: 2018/2/2 21:15
 * Desc:
 */
@WebServlet(name = "ActiveUserReport", urlPatterns = {"/active_user_report/*"})
public class ActiveUserReport extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Utils.isAdmin(request, response)) return;

        String path = request.getPathInfo();
        JsonObject jsonObject = new JsonObject();
        String tagName = request.getParameter("tagName");


        if (path.startsWith("/query_active_user_report")) {
            List<JSObject> list = null;
            JsonArray jsonArray = new JsonArray();
            try {
                String sql = "select country_code,avg_7_day_active,avg_14_day_active,avg_30_day_active,avg_60_day_active " +
                        " from ad_report_active_user_admob_rel_result where tag_name = '" + tagName + "' ORDER BY country_code";
                list = DB.findListBySql(sql);
                for (JSObject j : list) {
                    JsonObject jo = new JsonObject();
                    String countryCode = j.get("country_code");
                    double avg_7_day_active = Utils.convertDouble(j.get("avg_7_day_active"), 0);
                    double avg_14_day_active = Utils.convertDouble(j.get("avg_14_day_active"), 0);
                    double avg_30_day_active = Utils.convertDouble(j.get("avg_30_day_active"), 0);
                    double avg_60_day_active = Utils.convertDouble(j.get("avg_60_day_active"), 0);
                    jo.addProperty("country_code", countryCode);
                    jo.addProperty("avg_7_day_active", avg_7_day_active);
                    jo.addProperty("avg_14_day_active", avg_14_day_active);
                    jo.addProperty("avg_30_day_active", avg_30_day_active);
                    jo.addProperty("avg_60_day_active", avg_60_day_active);
                    jsonArray.add(jo);
                }

                jsonObject.add("array", jsonArray);
                jsonObject.addProperty("ret", 1);
                jsonObject.addProperty("message", "执行成功");
            } catch (Exception e) {
                jsonObject.addProperty("ret", 0);
                jsonObject.addProperty("message", e.getMessage());
            }
        }
        response.getWriter().write(jsonObject.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
