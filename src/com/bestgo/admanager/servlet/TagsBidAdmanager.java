package com.bestgo.admanager.servlet;

import com.bestgo.admanager.OperationResult;
import com.bestgo.admanager.utils.DateUtil;
import com.bestgo.admanager.utils.NumberUtil;
import com.bestgo.admanager.utils.StringUtil;
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
 * Created by lisongtao on 7/31/18.
 */
@WebServlet(name = "tagsBidAdmanager", urlPatterns = "/tagsBidAdmanager/*")
public class TagsBidAdmanager extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Utils.isAdmin(request, response)) return;

        String path = request.getPathInfo();
        JsonObject json = new JsonObject();
        OperationResult result = new OperationResult();
//        String tag = request.getParameter("tag");
//        String startTime = request.getParameter("startTime");
//        String endTime = request.getParameter("endTime");

        if (path != null) {
            if (path.startsWith("/create")) {
                String tagName = request.getParameter("name");
                String bidding = request.getParameter("bidding");
                String country = request.getParameter("country");

                if (!tagName.isEmpty() && (bidding.matches("^\\d+(\\.\\d+)?$") || StringUtil.isEmpty(bidding)) || "".equals(country)) {

                    result = createNewTag(tagName, bidding, country);
                    json.addProperty("ret", result.result ? 1 : 0);
                    json.addProperty("message", result.message);
                } else {
                    json.addProperty("ret", 0);
                    json.addProperty("message", "创建失败，请注意输入格式");
                }
            } else if (path.startsWith("/delete")) {

                String id = request.getParameter("id");
                result = deleteTag(id);
                json.addProperty("ret", result.result ? 1 : 0);
                json.addProperty("message", result.message);

            } else if (path.startsWith("/update")) {

                String id = request.getParameter("id");
                String tagName = request.getParameter("name");
                String country = request.getParameter("country");
                String bidding = request.getParameter("bidding");

                if (!tagName.isEmpty() && (bidding.matches("^\\d+(\\.\\d+)?$") || StringUtil.isEmpty(bidding)) || "".equals(country)) {

                    result = updateTag(id, tagName, country, bidding);
                    json.addProperty("ret", result.result ? 1 : 0);
                    json.addProperty("message", result.message);
                } else {
                    json.addProperty("ret", 0);
                    json.addProperty("message", "更新失败，请注意输入格式");
                }

            } else if (path.startsWith("/query")) {
                String word = request.getParameter("word");
                if (StringUtil.isNotEmpty(word)) {
                    List<JSObject> data = fetchData(word);
                    json.addProperty("ret", 1);
                    JsonArray array = new JsonArray();
                    for (int i = 0; i < data.size(); i++) {
                        JsonObject one = new JsonObject();
                        one.addProperty("id", data.get(i).get("id").toString());
                        one.addProperty("tag_name", (String) data.get(i).get("tag_name"));
                        one.addProperty("bidding", (Double) data.get(i).get("bidding"));
                        one.addProperty("country", (String) data.get(i).get("country"));
                        array.add(one);
                    }
                    json.add("data", array);
                } else {
                    long count = count();
                    int index = NumberUtil.parseInt(request.getParameter("page_index"), 0);
                    int size = NumberUtil.parseInt(request.getParameter("page_size"), 20);
                    long totalPage = count / size + (count % size == 0 ? 0 : 1);
                    List<JSObject> data = fetchAllData(index, size);
                    json.addProperty("ret", 1);
                    JsonArray array = new JsonArray();
                    for (int i = 0; i < data.size(); i++) {
                        JsonObject one = new JsonObject();
                        one.addProperty("id", data.get(i).get("id").toString());
                        one.addProperty("tag_name", (String) data.get(i).get("tag_name"));
                        one.addProperty("bidding", (Double) data.get(i).get("bidding"));
                        one.addProperty("country", (String) data.get(i).get("country"));
                        array.add(one);
                    }
                    json.add("data", array);
                }
            } else if (path.startsWith("/selectByTagName")) {
//                result.result = true;
//                if (tag.isEmpty()) {
//                    result.result = false;
//                    result.message = "标签不能为空";
//                }
//                if (result.result) {
//                    endTime = DateUtil.addDay(endTime, 1, "yyyy-MM-dd");//加一天
//                    String sqlFacebook = "select app_name,campaign_id,account_id,campaign_name,create_time,bugdet,bidding,country_region" +
//                            " from ad_campaigns where tag_name = '" + tag + "' and create_time >= '" + startTime + "' and create_time < '" + endTime + "'";
//                    String sqlAdmob = "select app_name,campaign_id,account_id,campaign_name,create_time,bugdet,bidding,ccd.country_name" +
//                            " from ad_campaigns_admob ca,app_country_code_dict ccd " +
//                            "where ca.country_region = ccd.country_code and " +
//                            "tag_name = '" + tag + "' and create_time >= '" + startTime + "' and create_time < '" + endTime + "'";
//                    try {
//                        List<JSObject> listF = DB.findListBySql(sqlFacebook);
//                        List<JSObject> listA = DB.findListBySql(sqlAdmob);
//                        JsonArray array = new JsonArray();
//                        double total_budget = 0;
//                        for (JSObject j : listA) {
//                            JsonObject one = new JsonObject();
//                            String app_name = j.get("app_name");
//                            String campaign_id = j.get("campaign_id");
//                            String account_id = j.get("account_id");
//                            String campaign_name = j.get("campaign_name");
//                            String create_time = DateUtil.timeStamp2Date(j.get("create_time"), "yyyy-MM-dd HH:mm:ss");
//                            double budget = NumberUtil.parseDouble(j.get("bugdet"), 0);
//                            total_budget += budget;
//                            double bidding = NumberUtil.parseDouble(j.get("bidding"), 0);
//                            String country_region = j.get("country_name");
//                            one.addProperty("app_name", app_name);
//                            one.addProperty("campaign_id", campaign_id);
//                            one.addProperty("account_id", account_id);
//                            one.addProperty("campaign_name", campaign_name);
//                            one.addProperty("create_time", create_time);
//                            one.addProperty("budget", budget);
//                            one.addProperty("bidding", bidding);
//                            one.addProperty("country_region", country_region);
//                            array.add(one);
//                        }
//                        for (JSObject j : listF) {
//                            JsonObject one = new JsonObject();
//                            String app_name = j.get("app_name");
//                            String campaign_id = j.get("campaign_id");
//                            String account_id = j.get("account_id");
//                            String campaign_name = j.get("campaign_name");
//                            String create_time = DateUtil.timeStamp2Date(j.get("create_time"), "yyyy-MM-dd HH:mm:ss");
//
//                            double budget = NumberUtil.parseDouble(j.get("bugdet"), 0);
//                            total_budget += budget;
//                            double bidding = NumberUtil.parseDouble(j.get("bidding"), 0);
//                            String country_region = j.get("country_region");
//                            one.addProperty("app_name", app_name);
//                            one.addProperty("campaign_id", campaign_id);
//                            one.addProperty("account_id", account_id);
//                            one.addProperty("campaign_name", campaign_name);
//                            one.addProperty("create_time", create_time);
//                            one.addProperty("budget", budget);
//                            one.addProperty("bidding", bidding);
//                            one.addProperty("country_region", country_region);
//                            array.add(one);
//                        }
//                        json.add("arr", array);
//                        json.addProperty("total_budget", total_budget);
//                        json.addProperty("total_count", array.size());
//                    } catch (Exception ex) {
//                        result.message = ex.getMessage();
//                        result.result = false;
//                    }
//                }
//                json.addProperty("ret", result.result ? 1 : 0);
//                json.addProperty("message", result.message);
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
            String sql = "SELECT id,tag_name,bidding,country FROM web_tag_bid_admanager WHERE tag_name LIKE ? ";
            list = DB.findListBySql(sql, "%" + word + "%");
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


    public static List<JSObject> fetchAllData(int index, int size) {
        List<JSObject> list = new ArrayList<>();
        try {
            List<JSObject> jsObjectList = DB.scan("web_tag_bid_admanager").select("id", "tag_name", "country", "bidding").limit(size).start(index * size).orderByAsc("id").execute();
            return jsObjectList;
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return list;
    }

    public static long count() {
        try {
            JSObject object = DB.simpleScan("web_tag_bid_admanager").select(DB.func(DB.COUNT, "id")).execute();
            return object.get("count(id)");
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return 0;
    }

    private OperationResult deleteTag(String id) {
        int tagId = Integer.parseInt(id);
        OperationResult ret = new OperationResult();

        try {
            DB.delete("web_tag_bid_admanager").where(DB.filter().whereEqualTo("id", tagId)).execute();

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

    private OperationResult createNewTag(String tagName, String bidding, String country) {
        OperationResult ret = new OperationResult();


        try {
            DB.insert("web_tag_bid_admanager")
                    .put("tag_name", tagName)
                    .put("bidding", Double.parseDouble(bidding))
                    .put("country", country)
                    .execute();

            ret.result = true;
            ret.message = "创建成功";

        } catch (Exception e) {
            e.printStackTrace();
            ret.result = false;
            ret.message = e.getMessage();
        }

        return ret;
    }


    private OperationResult updateTag(String id, String tagName, String country, String bidding) {
        int tagId = Integer.parseInt(id);

        OperationResult ret = new OperationResult();

        try {

            ret.result = DB.update("web_tag_bid_admanager")
                    .put("country", country)
                    .put("bidding", Double.parseDouble(bidding))
                    .where(DB.filter().whereEqualTo("id", tagId))
                    .execute();
            ret.message = "修改成功";
        } catch (Exception e) {
            ret.result = false;
            ret.message = e.getMessage();
            Logger logger = Logger.getRootLogger();
            logger.error(e.getMessage(), e);
        }
        return ret;
    }
}
