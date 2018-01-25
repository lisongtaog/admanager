package com.bestgo.admanager.servlet;

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
 * Created by jikai on 8/16/17.
 */
@WebServlet(name = "System", urlPatterns = "/system/*")
public class System extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Utils.isAdmin(request, response)) return;

        String path = request.getPathInfo();
        JsonObject json = new JsonObject();

        if (path != null) {
            if (path.startsWith("/update")) {
                String key = request.getParameter("configKey");
                String value = request.getParameter("configValue");
                OperationResult result = updateConfig(key, value);
                json.addProperty("ret", result.result ? 1 : 0);
                json.addProperty("message", result.message);
            } else {
                int id = Utils.parseInt(request.getParameter("id"), 0);
                String tagName = request.getParameter("tagName");
                if (tagName != null) tagName = tagName.trim();
                String accountId = request.getParameter("accountId");
                if (accountId != null) accountId = accountId.trim();
                String fbAppId = request.getParameter("fbAppId");
                if (fbAppId != null) fbAppId = fbAppId.trim();
                String pageId = request.getParameter("pageId");
                if (pageId != null) pageId = pageId.trim();
                String gpPackageId = request.getParameter("gpPackageId");
                if (gpPackageId != null) gpPackageId = gpPackageId.trim();
                String firebaseProjectId = request.getParameter("firebaseProjectId");
                if (gpPackageId != null) gpPackageId = gpPackageId.trim();
                switch (path) {
                    case "/fb_app_id_rel/create": {
                        if (!tagName.isEmpty()) {
                            try {
                                JSObject one = DB.simpleScan("web_tag").select("id").where(DB.filter().whereEqualTo("tag_name", tagName)).execute();
                                if (!one.hasObjectData()) {
                                    DB.insert("web_tag").put("tag_name", tagName).execute();
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        OperationResult result = createNewFacebookAppRelation(tagName, accountId, fbAppId, pageId, gpPackageId,firebaseProjectId);
                        json.addProperty("ret", result.result ? 1 : 0);
                        json.addProperty("message", result.message);
                    }
                        break;
                    case "/fb_app_id_rel/update": {
                        if (!tagName.isEmpty()) {
                            try {
                                JSObject one = DB.simpleScan("web_tag").select("id").where(DB.filter().whereEqualTo("tag_name", tagName)).execute();
                                if (!one.hasObjectData()) {
                                    DB.insert("web_tag").put("tag_name", tagName).execute();
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        OperationResult result = updateFacebookAppRelation(id, tagName, accountId, fbAppId, pageId, gpPackageId,firebaseProjectId);
                        json.addProperty("ret", result.result ? 1 : 0);
                        json.addProperty("message", result.message);
                    }
                        break;
                    case "/fb_app_id_rel/delete": {
                        OperationResult result = deleteFacebookAppRelation(id);
                        json.addProperty("ret", result.result ? 1 : 0);
                        json.addProperty("message", result.message);
                    }
                        break;
                    case "/fb_app_id_rel/query": {
                        String word = request.getParameter("word");
                        if (word != null) {
                            List<JSObject> data = fetchFacebookAppRelationData(word);
                            json.addProperty("ret", 1);
                            JsonArray array = new JsonArray();
                            for (int i = 0; i < data.size(); i++) {
                                JsonObject one = new JsonObject();
                                one.addProperty("tag_name", (String)data.get(i).get("tag_name"));
                                one.addProperty("account_id", (String)data.get(i).get("account_id"));
                                one.addProperty("fb_app_id", (String)data.get(i).get("fb_app_id"));
                                one.addProperty("page_id", (String)data.get(i).get("page_id"));
                                one.addProperty("google_package_id", (String)data.get(i).get("google_package_id"));
                                one.addProperty("firebase_project_id", (String)data.get(i).get("firebase_project_id"));
                                one.addProperty("id", (long)data.get(i).get("id"));
                                array.add(one);
                            }
                            json.add("data", array);
                        } else {
                            long count = countFacebookAppRelation();
                            int index = Utils.parseInt(request.getParameter("page_index"), 0);
                            int size = Utils.parseInt(request.getParameter("page_size"), 20);
                            long totalPage = count / size + (count % size == 0 ? 0 : 1);
                            List<JSObject> data = fetchFacebookAppRelationData(index, size);
                            json.addProperty("ret", 1);
                            JsonArray array = new JsonArray();
                            for (int i = 0; i < data.size(); i++) {
                                JsonObject one = new JsonObject();
                                one.addProperty("tag_name", (String)data.get(i).get("tag_name"));
                                one.addProperty("account_id", (String)data.get(i).get("account_id"));
                                one.addProperty("fb_app_id", (String)data.get(i).get("fb_app_id"));
                                one.addProperty("page_id", (String)data.get(i).get("page_id"));
                                one.addProperty("google_package_id", (String)data.get(i).get("google_package_id"));
                                one.addProperty("firebase_project_id", (String)data.get(i).get("firebase_project_id"));
                                one.addProperty("id", (long)data.get(i).get("id"));
                                array.add(one);
                            }
                            json.add("data", array);
                        }
                    }
                        break;
                }
            }
        } else {

        }

        response.getWriter().write(json.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }



    private OperationResult updateConfig(String key, String value) {
        OperationResult ret = new OperationResult();

        try {
            DB.update("web_system_config")
                    .put("config_value", value)
                    .where(DB.filter().whereEqualTo("config_key", key)).execute();

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

    public static List<JSObject> fetchFacebookAppRelationData(String word) {
        List<JSObject> list = new ArrayList<>();
        try {
            return DB.scan("web_facebook_app_ids_rel").select("id", "tag_name", "account_id", "fb_app_id", "page_id", "google_package_id","firebase_project_id")
                    .where(DB.filter().whereLikeTo("tag_name", "%" + word + "%")).orderByAsc("tag_name").execute();
//                  .where(DB.filter().whereLikeTo("tag_name", "%" + word + "%")).orderByAsc("id").execute();
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return list;
    }

    public static List<JSObject> fetchFacebookAppRelationData(int index, int size) {
        List<JSObject> list = new ArrayList<>();
        try {
            return DB.scan("web_facebook_app_ids_rel").select("id", "tag_name", "account_id", "fb_app_id", "page_id", "google_package_id","firebase_project_id")
                    .limit(size).start(index * size).orderByAsc("id").execute();
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return list;
    }

    public static long countFacebookAppRelation() {
        try {
            JSObject object = DB.simpleScan("web_facebook_app_ids_rel").select(DB.func(DB.COUNT, "id")).execute();
            return object.get("count(id)");
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return 0;
    }

    private OperationResult deleteFacebookAppRelation(int id) {
        OperationResult ret = new OperationResult();

        try {
            DB.delete("web_facebook_app_ids_rel").where(DB.filter().whereEqualTo("id", id)).execute();

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

    private OperationResult createNewFacebookAppRelation(String tagName, String accountId, String fbAppId, String pageId, String gpPackageId,String firebaseProjectId) {
        OperationResult ret = new OperationResult();

        try {
            JSObject one = DB.simpleScan("web_facebook_app_ids_rel").select("tag_name").where(DB.filter().whereEqualTo("tag_name", tagName)).execute();
            if (one.get("tag_name") != null) {
                ret.result = false;
                ret.message = "已经存在这个账号了";
            } else {
                DB.insert("web_facebook_app_ids_rel")
                        .put("tag_name", tagName)
                        .put("account_id", accountId)
                        .put("fb_app_id", fbAppId)
                        .put("page_id", pageId)
                        .put("google_package_id", gpPackageId)
                        .put("firebase_project_id", firebaseProjectId)
                        .execute();

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

    private OperationResult updateFacebookAppRelation(int id, String tagName, String accountId, String fbAppId, String pageId, String gpPackageId,String firebaseProjectId) {
        OperationResult ret = new OperationResult();

        try {
            DB.update("web_facebook_app_ids_rel")
                    .put("tag_name", tagName)
                    .put("account_id", accountId)
                    .put("fb_app_id", fbAppId)
                    .put("page_id", pageId)
                    .put("google_package_id", gpPackageId)
                    .put("firebase_project_id", firebaseProjectId)
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
