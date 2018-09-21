package com.bestgo.admanager.servlet;

import com.bestgo.admanager.OperationResult;
import com.bestgo.admanager.utils.NumberUtil;
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
 * 有关Adwords的规则,暂停使用，目前规则已经由两个表并成一个表
 */
@WebServlet(name = "AdRulesAdmobServlet", urlPatterns = {"/rules_admob/*"})
public class AdRulesAdmobServlet extends BaseHttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doPost(request, response);
        if (!Utils.isAdmin(request, response)) return;

        String path = request.getPathInfo();
        JsonObject json = new JsonObject();

        if (path != null) {
            if (path.startsWith("/create")) {
                String ruleType = request.getParameter("ruleType");
                String ruleContent = request.getParameter("ruleContent");

                OperationResult result = createNewRule(ruleType, ruleContent);
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
                String text = request.getParameter("text");
                List<JSObject> data = fetchData(text);
                json.addProperty("ret", 1);
                JsonArray array = new JsonArray();
                for (int i = 0; i < data.size(); i++) {
                    JsonObject one = new JsonObject();
                    one.addProperty("rule_type", (Integer)data.get(i).get("rule_type"));
                    one.addProperty("rule_content", (String) data.get(i).get("rule_content"));
                    one.addProperty("id", (long) data.get(i).get("id"));
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

    public static List<JSObject> fetchData(int index, int size) {
        List<JSObject> list = new ArrayList<>();
        try {
            return DB.scan("web_ad_rules_admob").select("id", "rule_type", "rule_content").limit(size).start(index * size).orderByAsc("id").execute();
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return list;
    }

    public static List<JSObject> fetchData(String query) {
        List<JSObject> list = new ArrayList<>();
        try {
            return DB.scan("web_ad_rules_admob").select("id", "rule_type", "rule_content").where(DB.filter().whereLikeTo("rule_content", "%" + query + "%")).orderByAsc("id").execute();
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return list;
    }

    public static long count() {
        try {
            JSObject object = DB.simpleScan("web_ad_rules_admob").select(DB.func(DB.COUNT, "id")).execute();
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
            DB.delete("web_ad_rules_admob").where(DB.filter().whereEqualTo("id", id)).execute();

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

    private OperationResult createNewRule(String ruleType, String ruleContent) {
        OperationResult ret = new OperationResult();

        try {
            DB.insert("web_ad_rules_admob").put("rule_type", ruleType)
                    .put("rule_content", ruleContent).execute();

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
            DB.update("web_ad_rules_admob").put("rule_type", ruleType)
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
}
