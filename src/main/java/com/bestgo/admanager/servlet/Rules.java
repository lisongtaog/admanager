package com.bestgo.admanager.servlet;

import com.bestgo.admanager.OperationResult;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jikai on 5/31/17.
 * 目前web_rules包含了Facebook和Adwords两个的所有规则
 */
@WebServlet(name = "Rules", urlPatterns = {"/rules/*"})
public class Rules extends BaseHttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doPost(request, response);
        if (!Utils.isAdmin(request, response)) return;

        String path = request.getPathInfo();
        JsonObject json = new JsonObject();

        if (path != null) {
            if (path.startsWith("/create")) {
                String ruleType = request.getParameter("ruleType");
                String ruleContent = request.getParameter("ruleContent");
                String tag_id = request.getParameter("tag_id");
                String tag_name = request.getParameter("tag_name");

                OperationResult result = createNewRule(ruleType, ruleContent, tag_id, tag_name);
                json.addProperty("ret", result.result ? 1 : 0);
                json.addProperty("message", result.message);
            } else if (path.startsWith("/delete")) {
                String id = request.getParameter("id");
                OperationResult result = deleteRule(NumberUtil.parseInt(id, 0));
                json.addProperty("ret", result.result ? 1 : 0);
                json.addProperty("message", result.message);
            } else if (path.startsWith("/update")) {
                String ruleType = request.getParameter("ruleType");
                String ruleContent = request.getParameter("ruleContent");
                int id = NumberUtil.parseInt(request.getParameter("id"), 0);
                OperationResult result = updateRule(id, ruleType, ruleContent);
                json.addProperty("ret", result.result ? 1 : 0);
                json.addProperty("message", result.message);
            } else if (path.startsWith("/query")) {
                String ruleType = request.getParameter("ruleType");
                String tagName = request.getParameter("tagName");
                String ruleText = request.getParameter("ruleText");
                List<JSObject> data = fetchData(ruleType,tagName,ruleText);
                json.addProperty("ret", 1);
                JsonArray array = new JsonArray();
                for (int i = 0; i < data.size(); i++) {
                    JsonObject one = new JsonObject();
                    one.addProperty("rule_type", (Integer) data.get(i).get("rule_type"));

                    one.addProperty("rule_content", (String) data.get(i).get("rule_content"));

                    one.addProperty("tag_name3", (String) data.get(i).get("tag_name"));

                    one.addProperty("id", (long) data.get(i).get("id"));

                    one.addProperty("tag_id3", (Integer) data.get(i).get("tag_id"));
                    array.add(one);
                }
                json.add("data", array);

            } else if (path.startsWith("/selectTagId")) {
                String tag_name = request.getParameter("tag_name");
                JSObject oneBySql = null;
                JsonObject jsonObject = new JsonObject();

                try {
                    oneBySql = DB.findOneBySql("SELECT id FROM web_tag WHERE tag_name = '" + tag_name + "'");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                jsonObject.addProperty("id", (long) oneBySql.get("id"));
                json.add("data", jsonObject);
            } else if (path.startsWith("/delByTagName")) {
                String tagName = request.getParameter("tagName");
                deleteByTagName(tagName);
            }
        } else {

        }

        response.getWriter().write(json.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public static List<JSObject> fetchData(int index, int size) {
        List<JSObject> list = new ArrayList<>();
        try {
            return DB.scan("web_ad_rules").select("id", "rule_type", "rule_content", "tag_id", "tag_name").limit(size).start(index * size).orderByAsc("id").execute();
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return list;
    }

    public static List<JSObject> fetchData(String ruleType,String tagName, String query) {
        List<JSObject> list = new ArrayList<>();
        try {
            String sql = "select id,rule_type,rule_content,tag_id,tag_name from web_ad_rules where 1=1 ";
            if(StringUtil.isNotEmpty(ruleType)){
                sql += " and rule_type = " + ruleType ;
            }
            if(StringUtil.isNotEmpty(tagName)){
                sql += " and tag_name like '" + tagName + "%' " ;
            }
            if(StringUtil.isNotEmpty(query)){
                sql += " and rule_content like '%" + query + "%'";
            }
            return DB.findListBySql(sql);
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return list;
    }

    public static long count() {
        try {
            JSObject object = DB.simpleScan("web_ad_rules").select(DB.func(DB.COUNT, "id")).execute();
            return object.get("count(id)");
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return 0;
    }

    private OperationResult deleteRule(int id) {
        OperationResult ret = new OperationResult();

        try {
            DB.delete("web_ad_rules").where(DB.filter().whereEqualTo("id", id)).execute();

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

    private OperationResult createNewRule(String ruleType, String ruleContent, String tag_id, String tag_name) {
        OperationResult ret = new OperationResult();

        try {
            DB.insert("web_ad_rules")
                    .put("rule_type", ruleType)
                    .put("rule_content", ruleContent)
                    .put("tag_id", Integer.parseInt(tag_id))
                    .put("tag_name", tag_name)
                    .execute();

            ret.result = true;
            ret.message = "添加成功";
        } catch (Exception e) {
            ret.result = false;
            ret.message = e.getMessage();
            Logger logger = Logger.getRootLogger();
            logger.error(e.getMessage(), e);
        }

        return ret;
    }

    private OperationResult updateRule(int id, String ruleType, String ruleContent) {
        OperationResult ret = new OperationResult();

        try {
            DB.update("web_ad_rules").put("rule_type", ruleType)
                    .put("rule_content", ruleContent)
                    .where(DB.filter().whereEqualTo("id", id)).execute();

            ret.result = true;
            ret.message = "修改成功";
        } catch (Exception e) {
            ret.result = false;
            ret.message = e.getMessage();
            Logger logger = Logger.getRootLogger();
            logger.error(e.getMessage(), e);
        }

        return ret;
    }

    /**
     * 根据标签名称删除标签规则的方法
     * @param tagName
     */
    private void deleteByTagName(String tagName) {
        try {
            DB.delete("web_ad_rules").where(DB.filter().whereEqualTo("tag_name",tagName)).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
