package com.bestgo.admanager.servlet;

import com.bestgo.admanager.OperationResult;
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

@WebServlet(name = "AdvertConversionAdmob", urlPatterns = "/advert_conversion_admob/*")
public class AdvertConversionAdmob extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Utils.isAdmin(request, response)) return;

        String path = request.getPathInfo();
        JsonObject json = new JsonObject();
        String appName = request.getParameter("appName");
        if (path.startsWith("/save_advert_conversion")) {

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
                    String sqlQuery = "select id from web_ad_conversions_admob where app_name = '"+appName+"' and  conversion_id = '" + ctid + "' and conversion_name = '" + conversionName + "'";
                    JSObject one = DB.findOneBySql(sqlQuery);
                    if(one != null && one.hasObjectData()){
                        DB.update("web_ad_conversions_admob")
                                .put("conversion_name", conversionName)
                                .where(DB.filter().whereEqualTo("app_name", appName))
                                .and(DB.filter().whereEqualTo("conversion_id", ctid))
                                .execute();
                        json.addProperty("existData","true");
                    }else{
                        DB.insert("web_ad_conversions_admob")
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
        } else if (path.startsWith("/query_advert_conversion_by_app_name")) {
            String sqlQuery = "select conversion_id, conversion_name from web_ad_conversions_admob where app_name = '" + appName + "'";
            try {
                List<JSObject> list = DB.findListBySql(sqlQuery);
                JsonArray array = new JsonArray();
                for(JSObject j : list){
                    String  conversion_id = j.get("conversion_id");
                    String  conversion_name = j.get("conversion_name");
                    if(conversion_id != null && conversion_name != null){
                        JsonObject js = new JsonObject();
                        js.addProperty("conversion_id",conversion_id);
                        js.addProperty("conversion_name",conversion_name);
                        array.add(js);
                    }
                }
                json.addProperty("ret", 1);
                json.add("data", array);
            } catch (Exception ex) {
               ex.printStackTrace();
            }

        }

        response.getWriter().write(json.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
