package com.bestgo.admanager.servlet;

import com.bestgo.admanager.OperationResult;
import com.bestgo.admanager.Utils;
import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "AdvertConversion", urlPatterns = "/advert_conversion/*")
public class AdvertConversion extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Utils.isAdmin(request, response)) return;

        String path = request.getPathInfo();
        JsonObject json = new JsonObject();

        if (path.startsWith("/save_advert_conversion")) {
            String appName = request.getParameter("appName");
            String ctid = request.getParameter("ctid");
            String conversionName = request.getParameter("conversionName");

            OperationResult result = new OperationResult();
            try {
                result.result = true;

                if (ctid.isEmpty()) {
                    result.result = false;
                    result.message = "转化ID不能为空！";
                }
                if (conversionName.isEmpty()) {
                    result.result = false;
                    result.message = "转化名称不能为空！";
                }

                if (result.result) {
                    String sqlQuery = "select id from web_ad_conversions where app_name = '"+appName+"' and  conversion_id = '" + ctid + "'";
                    JSObject one = DB.findOneBySql(sqlQuery);
                    if(one != null && one.hasObjectData()){
                        DB.update("web_ad_conversions")
                                .put("conversion_name", conversionName)
                                .where(DB.filter().whereEqualTo("app_name", appName))
                                .and(DB.filter().whereEqualTo("conversion_id", ctid))
                                .execute();
                        json.addProperty("existData","true");
                    }else{
                        DB.insert("web_ad_conversions")
                                .put("app_name", appName)
                                .put("conversion_id", ctid)
                                .put("conversion_name", conversionName)
                                .execute();
                        json.addProperty("existData","false");
                    }
                    result.result = true;
                }
            } catch (Exception ex) {
                result.message = ex.getMessage();
                result.result = false;
            }
            json.addProperty("ret", result.result ? 1 : 0);
            json.addProperty("message", result.message);
        }

        response.getWriter().write(json.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
