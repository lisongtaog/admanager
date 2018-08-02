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
 * Created by jikai on 5/31/17.
 */
@WebServlet(name = "Logs", urlPatterns = {"/logs/*"})
public class Logs extends BaseHttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doPost(request, response);
        if (!Utils.isAdmin(request, response)) return;

        String path = request.getPathInfo();
        JsonObject json = new JsonObject();

        if (path != null) {
            if (path.startsWith("/query")) {
                String text = request.getParameter("text");
                String category = request.getParameter("category");
                String subCategory = request.getParameter("subCategory");
                List<JSObject> data = fetchData(category, subCategory, text);
                json.addProperty("ret", 1);
                JsonArray array = new JsonArray();
                for (int i = 0; i < data.size(); i++) {
                    JsonObject one = new JsonObject();
                    one.addProperty("id", (String) data.get(i).get("category"));
                    one.addProperty("category", (String) data.get(i).get("category"));
                    one.addProperty("sub_category", (String) data.get(i).get("sub_category"));
                    one.addProperty("log_time", data.get(i).get("log_time").toString());
                    one.addProperty("content", (String)data.get(i).get("content"));
                    array.add(one);
                }
                json.add("data", array);

            }
        } else {

        }

        response.getWriter().write(json.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public static List<JSObject> fetchData(String category, String subCategory, String query) {
        List<JSObject> list = new ArrayList<>();
        try {
            return DB.scan("web_logs").select("id", "log_time", "category", "sub_category", "content")
                    .where(DB.filter().whereEqualTo("category", category))
                    .and(DB.filter().whereEqualTo("sub_category", subCategory))
                    .and(DB.filter().whereLikeTo("content", "%" + query + "%"))
                    .orderByDesc("id").execute();
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return list;
    }

    public static List<JSObject> fetchData(String category, String subCategory, int index, int size) {
        List<JSObject> list = new ArrayList<>();
        try {
            return DB.scan("web_logs").select("id", "log_time", "category", "sub_category", "content")
                    .where(DB.filter().whereEqualTo("category", category))
                    .and(DB.filter().whereEqualTo("sub_category", subCategory))
                    .limit(size).start(index * size).orderByDesc("id").execute();
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return list;
    }

    public static long count(String category, String subCategory) {
        try {
            JSObject object = DB.simpleScan("web_logs")
                    .select(DB.func(DB.COUNT, "id"))
                    .where(DB.filter().whereEqualTo("category", category))
                    .and(DB.filter().whereEqualTo("sub_category", subCategory))
                    .execute();
            return object.get("count(id)");
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return 0;
    }
}
