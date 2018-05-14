package com.bestgo.admanager.servlet;

import com.bestgo.admanager.utils.Utils;
import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;
import com.google.gson.JsonArray;
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

/**
 * @Author: mengjun
 * @Date: 2018/5/14 15:12
 * @Desc:
 */
@WebServlet(name = "UserServlet", urlPatterns = {"/user_servlet/*"})
public class UserServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Utils.isAdmin(request, response)) return;
        String path = request.getPathInfo();
        JsonObject jsonObject = new JsonObject();
        if(path.matches("/query_nicknames_with_all_users")){
            try {
                List<JSObject> userList = DB.findListBySql("SELECT id,nickname FROM web_ad_login_user");
                JsonArray array = new JsonArray();
                if(userList.size() > 0){
                    for(JSObject user : userList){
                        if(user.hasObjectData()){
                            JsonObject js = new JsonObject();
                            long id = user.get("id");
                            String nickname = user.get("nickname");
                            js.addProperty("id",id);
                            js.addProperty("nickname",nickname);
                            array.add(js);
                        }
                    }
                    jsonObject.add("user_array", array);
                    jsonObject.addProperty("ret", 1);
                }
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

    public static List<JSObject> getNicknamesWithAllUsers() {
        List<JSObject> list = new ArrayList<>();
        try {
            return DB.scan("web_ad_login_user").select("nickname").execute();
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return list;
    }
}
