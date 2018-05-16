package com.bestgo.admanager.servlet;

import com.bestgo.admanager.OperationResult;
import com.bestgo.admanager.utils.Utils;
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

@WebServlet(name = "Advert2", urlPatterns = "/advert2/*")
public class Advert2 extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Utils.isAdmin(request, response)) return;

        String path = request.getPathInfo();
        JsonObject json = new JsonObject();

        if (path.startsWith("/save_advert_facebook")) {
            String appName = request.getParameter("appName");
            String language = request.getParameter("language");
            String groupNumber = request.getParameter("groupNumber");
            String title = request.getParameter("title");
            String message = request.getParameter("message");
            String saveVersion = request.getParameter("version");  //仅在把保存部分用于判断
            try{
                if(saveVersion.equals("English")){
                    language = "English";
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            String sql = "select group_id from web_ad_descript_dict where app_name='" + appName + "' and language = '" + language + "'and group_id = '"+groupNumber+"'";
            JSObject item = fetchOneData(sql);
            OperationResult result = new OperationResult();
            try {
                if(item.hasObjectData()){
                    DB.update("web_ad_descript_dict")
                            .put("title", title)
                            .put("message", message)
                            .where(DB.filter().whereEqualTo("app_name", appName))
                            .and(DB.filter().whereEqualTo("language", language))
                            .and(DB.filter().whereEqualTo("group_id", groupNumber))
                            .execute();
                    json.addProperty("existData","true");
                }else{
                    DB.insert("web_ad_descript_dict")
                            .put("language", language)
                            .put("title", title)
                            .put("message",message)
                            .put("group_id", groupNumber)
                            .put("app_name", appName)
                            .execute();
                    json.addProperty("existData","false");
                }
                result.result = true;
            } catch (Exception ex) {
                result.message = ex.getMessage();
                result.result = false;
            }
            json.addProperty("ret", result.result ? 1 : 0);
            json.addProperty("message", result.message);
        } else if (path.startsWith("/query_before_insertion")) {
            String appName = request.getParameter("appName");
            String language = request.getParameter("language");
            String groupNumber = request.getParameter("groupNumber");
            JSObject item_translation = new JSObject();
            JSObject item_english = new JSObject();
            try {
                if(appName != null && language != null){

                    String sql = "select title,message from web_ad_descript_dict where app_name = '"
                            + appName + "' and language = '" + language + "'and group_id='"+groupNumber+"'";
                    item_translation = DB.findOneBySql(sql);
                    sql = "select title,message from web_ad_descript_dict where app_name = '"
                            + appName + "' and language = 'English'and group_id='"+groupNumber+"'";
                    item_english  = DB.findOneBySql(sql);
                }
                if(item_translation.hasObjectData() || item_english.hasObjectData()){
                    //返回用于【英语广告标题】【英语广告语】【广告语标题】【广告语】回显的数据
                    if(item_english.hasObjectData()){
                        String title = item_english.get("title");
                        String message = item_english.get("message");
                        json.addProperty("title", title);
                        json.addProperty("message", message);
                    }else{
                        json.addProperty("title", "");
                        json.addProperty("message", "");
                    }
                    if(item_translation.hasObjectData()){
                        String title_translation = item_translation.get("title");
                        String message_translation = item_translation.get("message");
                        json.addProperty("title_translation", title_translation);
                        json.addProperty("message_translation", message_translation);
                    }else{
                        json.addProperty("title_translation", "");
                        json.addProperty("message_translation","");
                    }

                    json.addProperty("ret", 1);
                }

            } catch (Exception e) {
                json.addProperty("ret", 0);
                e.printStackTrace();
            }
        }

        response.getWriter().write(json.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public static JSObject fetchOneData(String sql) {
        JSObject item = new JSObject();
        try {
            return DB.findOneBySql(sql);
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return item;
    }
}
