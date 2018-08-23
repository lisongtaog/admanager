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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jikai on 5/31/17.
 */
@WebServlet(name = "AdAccountAdMob", urlPatterns = {"/adaccount_admob/*"})
public class AdAccountAdmob extends BaseHttpServlet {

    public static Map<String, String> accountIdNameAdmobRelationMap;

    static {
        if (accountIdNameAdmobRelationMap == null) {
            accountIdNameAdmobRelationMap = new HashMap<>();
        }
        String sql = "SELECT account_id,short_name FROM web_account_id_admob";
        List<JSObject> list = null;
        try {
            list = DB.findListBySql(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list != null && list.size() > 0) {
            for (JSObject j : list) {
                String account_id = j.get("account_id");
                String short_name = j.get("short_name");
                accountIdNameAdmobRelationMap.put(account_id, short_name);
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doPost(request, response);
        if (!Utils.isAdmin(request, response)) return;

        String path = request.getPathInfo();
        JsonObject json = new JsonObject();

        if (path != null) {
            if (path.startsWith("/create")) {
                String account = request.getParameter("account");
                String shortName = request.getParameter("shortName");

                OperationResult result = createNewAdAccount(account, shortName);
                json.addProperty("ret", result.result ? 1 : 0);
                json.addProperty("message", result.message);
            } else if (path.startsWith("/delete")) {
                String account = request.getParameter("account");
                OperationResult result = deleteAccount(account);
                json.addProperty("ret", result.result ? 1 : 0);
                json.addProperty("message", result.message);
            } else if (path.startsWith("/update")) {
                String account = request.getParameter("account");
                String shortName = request.getParameter("shortName");
                int id = NumberUtil.parseInt(request.getParameter("id"), 0);
                OperationResult result = updateAdAccount(id, account, shortName);
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
                        one.addProperty("account_id", (String)data.get(i).get("account_id"));
                        one.addProperty("short_name", (String)data.get(i).get("short_name"));
                        one.addProperty("id", (long)data.get(i).get("id"));
                        array.add(one);
                    }
                    json.add("data", array);
                } else {
                    long count = count();
                    int index = NumberUtil.parseInt(request.getParameter("page_index"), 0);
                    int size = NumberUtil.parseInt(request.getParameter("page_size"), 20);
                    long totalPage = count / size + (count % size == 0 ? 0 : 1);
                    List<JSObject> data = fetchData(index, size);
                    json.addProperty("ret", 1);
                    JsonArray array = new JsonArray();
                    for (int i = 0; i < data.size(); i++) {
                        JsonObject one = new JsonObject();
                        one.addProperty("account_id", (String)data.get(i).get("account_id"));
                        one.addProperty("short_name", (String)data.get(i).get("short_name"));
                        one.addProperty("id", (long)data.get(i).get("id"));
                        array.add(one);
                    }
                    json.add("data", array);
                }
            }
        } else {

        }

        response.getWriter().write(json.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public static List<JSObject> fetchData(String word) {
        List<JSObject> list = new ArrayList<>();
        try {
            return DB.scan("web_account_id_admob").select("id", "account_id", "short_name")
                    .where(DB.filter().whereLikeTo("account_id", "%" + word + "%"))
                    .or(DB.filter().whereLikeTo("short_name", "%" + word + "%")).orderByAsc("id").execute();
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return list;
    }

    public static List<JSObject> fetchData(int index, int size) {
        List<JSObject> list = new ArrayList<>();
        try {
            return DB.scan("web_account_id_admob").select("id", "account_id", "short_name").limit(size).start(index * size).orderByAsc("id").execute();
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return list;
    }

    public static long count() {
        try {
            JSObject object = DB.simpleScan("web_account_id_admob").select(DB.func(DB.COUNT, "id")).execute();
            return object.get("count(id)");
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return 0;
    }

    private OperationResult deleteAccount(String account) {
        OperationResult ret = new OperationResult();

        try {
            DB.delete("web_account_id_admob").where(DB.filter().whereEqualTo("account_id", account)).execute();

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

    private OperationResult createNewAdAccount(String account, String shortName) {
        OperationResult ret = new OperationResult();

        try {
            JSObject one = DB.simpleScan("web_account_id_admob").select("account_id").where(DB.filter().whereEqualTo("account_id", account)).execute();
            if (one.get("account_id") != null) {
                ret.result = false;
                ret.message = "已经存在这个账号了";
            } else {
                DB.insert("web_account_id_admob").put("account_id", account)
                        .put("short_name", shortName).execute();

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

    private OperationResult updateAdAccount(int id, String account, String shortName) {
        OperationResult ret = new OperationResult();

        try {
            JSObject one = DB.simpleScan("web_account_id_admob").select("account_id").where(DB.filter().whereEqualTo("account_id", account)).execute();
            if (one.get("account_id") != null) {
                ret.result = false;
                ret.message = "已经存在这个账号了";
            } else {
                DB.update("web_account_id_admob").put("account_id", account)
                        .put("short_name", shortName)
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
