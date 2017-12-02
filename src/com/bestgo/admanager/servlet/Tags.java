package com.bestgo.admanager.servlet;

import com.bestgo.admanager.DateUtil;
import com.bestgo.admanager.OperationResult;
import com.bestgo.admanager.Utils;
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
@WebServlet(name = "tags", urlPatterns = "/tags/*")
public class Tags extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Utils.isAdmin(request, response)) return;

        String path = request.getPathInfo();
        JsonObject json = new JsonObject();
        OperationResult result = new OperationResult();
        String tag = request.getParameter("tag");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");

        if (path != null) {
            if (path.startsWith("/create")) {
                String tagName = request.getParameter("name");

                result = createNewTag(tagName);
                json.addProperty("ret", result.result ? 1 : 0);
                json.addProperty("message", result.message);
            } else if (path.startsWith("/delete")) {
                String tagName = request.getParameter("name");
                result = deleteTag(tagName);
                json.addProperty("ret", result.result ? 1 : 0);
                json.addProperty("message", result.message);
            } else if (path.startsWith("/update")) {
                String tagName = request.getParameter("name");
                int id = Utils.parseInt(request.getParameter("id"), 0);
                result = updateTag(id, tagName);
                json.addProperty("ret", result.result ? 1 : 0);
                json.addProperty("message", result.message);
            } else if (path.startsWith("/query")) {
                String word = request.getParameter("word");
                if (word != null) {
                    List<JSObject> data = fetchData(word);
                    json.addProperty("ret", 1);
                    JsonArray array = new JsonArray();
                    for (int i = 0; i < data.size(); i++) {
                        JsonObject one = new JsonObject();
                        one.addProperty("tag_name", (String)data.get(i).get("tag_name"));
                        one.addProperty("id", (long)data.get(i).get("id"));
                        array.add(one);
                    }
                    json.add("data", array);
                } else {
                    long count = count();
                    int index = Utils.parseInt(request.getParameter("page_index"), 0);
                    int size = Utils.parseInt(request.getParameter("page_size"), 20);
                    long totalPage = count / size + (count % size == 0 ? 0 : 1);
                    List<JSObject> data = fetchData(index, size);
                    json.addProperty("ret", 1);
                    JsonArray array = new JsonArray();
                    for (int i = 0; i < data.size(); i++) {
                        JsonObject one = new JsonObject();
                        one.addProperty("tag_name", (String)data.get(i).get("tag_name"));
                        one.addProperty("id", (long)data.get(i).get("id"));
                        array.add(one);
                    }
                    json.add("data", array);
                }
            }else if (path.startsWith("/selectByTagName")) {
                result.result = true;
                if (tag.isEmpty()) {
                    result.result = false;
                    result.message = "标签不能为空";
                }
                if(result.result){
                    String sqlFacebook = "select app_name,campaign_id,account_id,campaign_name,create_time,bugdet,bidding,country_region from ad_campaigns where tag_name = '"+tag+"' and create_time between '"+startTime+"' and '"+endTime+"'";
                    String sqlAdmob = "select app_name,campaign_id,account_id,campaign_name,create_time,bugdet,bidding,country_region from ad_campaigns_admob where tag_name = '"+tag+"' and create_time between '"+startTime+"' and '"+endTime+"'";
                    try {
                        List<JSObject> listF = DB.findListBySql(sqlFacebook);
                        List<JSObject> listA = DB.findListBySql(sqlAdmob);
                        listF.addAll(listA);
                        JsonArray array = new JsonArray();
                        double total_budget = 0;
                        for (JSObject j : listF) {
                            JsonObject one = new JsonObject();
                            String app_name = j.get("app_name");
                            String campaign_id = j.get("campaign_id");
                            String account_id = j.get("account_id");
                            String campaign_name = j.get("campaign_name");
                            String create_time = DateUtil.timeStamp2Date(j.get("create_time"),"yyyy-MM-dd HH:mm:ss");

                            double budget = Utils.parseDouble(j.get("bugdet"),0);
                            total_budget += budget;
                            double bidding = Utils.parseDouble(j.get("bidding"),0);
                            String country_region = j.get("country_region");
                            one.addProperty("app_name", app_name);
                            one.addProperty("campaign_id", campaign_id);
                            one.addProperty("account_id", account_id);
                            one.addProperty("campaign_name", campaign_name);
                            one.addProperty("create_time", create_time);
                            one.addProperty("budget", budget);
                            one.addProperty("bidding", bidding);
                            one.addProperty("country_region", country_region);
                            array.add(one);
                        }
                        json.add("arr", array);
                        json.addProperty("total_budget",total_budget);
                        json.addProperty("total_count",listF.size());
                    } catch (Exception ex) {
                        result.message = ex.getMessage();
                        result.result = false;
                    }
                }
                json.addProperty("ret", result.result ? 1 : 0);
                json.addProperty("message", result.message);
            }
        }

        response.getWriter().write(json.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public static List<JSObject> fetchData(String word) {
        List<JSObject> list = new ArrayList<>();
        try {
            return DB.scan("web_tag").select("id", "tag_name")
                    .where(DB.filter().whereLikeTo("tag_name", "%" + word + "%")).orderByAsc("id").execute();
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return list;
    }

    public static List<JSObject> fetchAllTags() {
        List<JSObject> list = new ArrayList<>();
        try {
            return DB.scan("web_tag").select("id", "tag_name").orderByAsc("id").execute();
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return list;
    }

    public static List<JSObject> fetchData(int index, int size) {
        List<JSObject> list = new ArrayList<>();
        try {
            return DB.scan("web_tag").select("id", "tag_name").limit(size).start(index * size).orderByAsc("id").execute();
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return list;
    }

    public static long count() {
        try {
            JSObject object = DB.simpleScan("web_tag").select(DB.func(DB.COUNT, "id")).execute();
            return object.get("count(id)");
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return 0;
    }

    private OperationResult deleteTag(String name) {
        OperationResult ret = new OperationResult();

        try {
            DB.delete("web_tag").where(DB.filter().whereEqualTo("tag_name", name)).execute();

            ret.result = true;
            ret.message = "执行成功";
        } catch (Exception e) {
            ret.result = false;
            ret.message = e.getMessage();
            Logger logger = Logger.getRootLogger();
            logger.error(e.getMessage(), e);
        }

        return ret;
    }

    private OperationResult createNewTag(String name) {
        OperationResult ret = new OperationResult();

        try {
            JSObject one = DB.simpleScan("web_tag").select("tag_name").where(DB.filter().whereEqualTo("tag_name", name)).execute();
            if (one.get("tag_name") != null) {
                ret.result = false;
                ret.message = "已经存在这个标签了";
            } else {
                DB.insert("web_tag").put("tag_name", name).execute();

                ret.result = true;
                ret.message = "创建成功";
            }
        } catch (Exception e) {
            ret.result = false;
            ret.message = e.getMessage();
            Logger logger = Logger.getRootLogger();
            logger.error(e.getMessage(), e);
        }

        return ret;
    }

    private OperationResult updateTag(int id, String name) {
        OperationResult ret = new OperationResult();

        try {
            JSObject one = DB.simpleScan("web_tag").select("tag_name").where(DB.filter().whereEqualTo("tag_name", name)).execute();
            if (one.get("tag_name") != null) {
                ret.result = false;
                ret.message = "已经存在这个标签了";
            } else {
                DB.update("web_tag").put("tag_name", name)
                        .where(DB.filter().whereEqualTo("id", id)).execute();

                ret.result = true;
                ret.message = "修改成功";
            }
        } catch (Exception e) {
            ret.result = false;
            ret.message = e.getMessage();
            Logger logger = Logger.getRootLogger();
            logger.error(e.getMessage(), e);
        }

        return ret;
    }
}
