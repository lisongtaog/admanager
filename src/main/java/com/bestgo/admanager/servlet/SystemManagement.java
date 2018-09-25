package com.bestgo.admanager.servlet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bestgo.admanager.OperationResult;
import com.bestgo.admanager.utils.NumberUtil;
import com.bestgo.admanager.utils.Utils;
import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
@WebServlet(name = "SystemManagement", urlPatterns = "/system/*")
public class SystemManagement extends BaseHttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doPost(request, response);
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
                int id = NumberUtil.parseInt(request.getParameter("id"), 0);
                String tagName = request.getParameter("tagName");
                if (tagName != null) tagName = tagName.trim();
                String accountId = request.getParameter("accountId");
                if (accountId != null) accountId = accountId.trim();
                String fbAppId = request.getParameter("fbAppId");
                if (fbAppId != null) fbAppId = fbAppId.trim();
                String pageId = request.getParameter("pageId");

                String fbPagesJson = request.getParameter("fbPages");//获取用户添加的facebook主页信息数组json
                //facebook首页对象集合
                List<JSONObject> fbPages = null;
                if (fbPagesJson != null) {
                    JSONArray jsonArray = JSONArray.parseArray(fbPagesJson);
                    fbPages = jsonArray.toJavaList(JSONObject.class);
                    //做兼容处理，如果没有 默认取传过来的数组的第一个
                    if(null == pageId) pageId = (String) fbPages.get(0).get("page_id");
                }


                if (pageId != null) pageId = pageId.trim();

                String gpPackageId = request.getParameter("gpPackageId");
                if (gpPackageId != null) gpPackageId = gpPackageId.trim();
                String firebaseProjectId = request.getParameter("firebaseProjectId");
                if (gpPackageId != null) gpPackageId = gpPackageId.trim();
                switch (path) {
                    case "/fb_app_id_rel/queryFBPage": {
                        //facebook主页信息
                        List<JSObject> fbPage = fetchFacebookPages(tagName);//facebook主页信息
                        Gson gson = new Gson();
                        json.addProperty("data",new Gson().toJson(fbPage,List.class));
                    }break;
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
                        OperationResult result = createNewFacebookAppRelation(tagName, accountId, fbAppId,gpPackageId,firebaseProjectId,fbPages);
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
                        OperationResult result = updateFacebookAppRelation(id, tagName, accountId, fbAppId,gpPackageId,firebaseProjectId,fbPages);
                        json.addProperty("ret", result.result ? 1 : 0);
                        json.addProperty("message", result.message);
                    }
                        break;
                    case "/fb_app_id_rel/delete": {
                        OperationResult result = deleteFacebookAppRelation(id,tagName);
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

                            List<JSObject> fbPage = null;//facebook主页信息
                            String tag_name = null;
                            for (int i = 0; i < data.size(); i++) {
                                JsonObject one = new JsonObject();
                                tagName = (String)data.get(i).get("tag_name");
                                one.addProperty("tag_name", tagName);
                                one.addProperty("account_id", (String)data.get(i).get("account_id"));
                                one.addProperty("fb_app_id", (String)data.get(i).get("fb_app_id"));
                                one.addProperty("page_id", (String)data.get(i).get("page_id"));
                                one.addProperty("google_package_id", (String)data.get(i).get("google_package_id"));
                                one.addProperty("firebase_project_id", (String)data.get(i).get("firebase_project_id"));
                                one.addProperty("id", (long)data.get(i).get("id"));

                                fbPage = fetchFacebookPages(tagName);//facebook主页信息
                                Gson gson = new Gson();
                                one.addProperty("fbPages", new Gson().toJson(fbPage,List.class));

                                array.add(one);
                            }
                            json.add("data", array);
                        } else {
                            long count = countFacebookAppRelation();
                            int index = NumberUtil.parseInt(request.getParameter("page_index"), 0);
                            int size = NumberUtil.parseInt(request.getParameter("page_size"), 20);
                            long totalPage = count / size + (count % size == 0 ? 0 : 1);
                            List<JSObject> data = fetchFacebookAppRelationData(index, size);
                            json.addProperty("ret", 1);

                            List<JSObject> fbPage = null;//facebook主页信息
                            JsonArray array = new JsonArray();
                            for (int i = 0; i < data.size(); i++) {
                                JsonObject one = new JsonObject();
                                tagName = (String)data.get(i).get("tag_name");
                                one.addProperty("tag_name", tagName);
                                one.addProperty("account_id", (String)data.get(i).get("account_id"));
                                one.addProperty("fb_app_id", (String)data.get(i).get("fb_app_id"));
                                one.addProperty("page_id", (String)data.get(i).get("page_id"));
                                one.addProperty("google_package_id", (String)data.get(i).get("google_package_id"));
                                one.addProperty("firebase_project_id", (String)data.get(i).get("firebase_project_id"));
                                one.addProperty("id", (long)data.get(i).get("id"));

                                fbPage = fetchFacebookPages(tagName);//facebook主页信息
                                Gson gson = new Gson();
                                one.addProperty("fbPages", new Gson().toJson(fbPage,List.class));
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

    /**
     * 获取facebook主页信息列表
     * @param tagName   标签、应用名称
     * @return
     */
    public List<JSObject> fetchFacebookPages(String tagName){
        List<JSObject> list = null;
        try {
            list = DB.scan("web_facebook_app_ids_page").select("id","page_id", "page_name")
                    .where(DB.filter().whereEqualTo("tag_name", tagName)).execute();
        } catch (Exception e) {
            list = new ArrayList<JSObject>();
            e.printStackTrace();
        }finally {
            return list;
        }
    }


    private List<JSObject> fetchFacebookAppRelationData(String word) {
        List<JSObject> list = new ArrayList<>();
        try {
            return DB.scan("web_facebook_app_ids_rel").select("id", "tag_name", "account_id", "fb_app_id", "page_id", "google_package_id","firebase_project_id")
                    .where(DB.filter().whereLikeTo("tag_name", "%" + word + "%")).orderByAsc("tag_name").execute();
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return list;
    }

    private List<JSObject> fetchFacebookAppRelationData(int index, int size) {
        List<JSObject> list = new ArrayList<>();
        try {
            return DB.scan("web_facebook_app_ids_rel").select("id", "tag_name", "account_id", "fb_app_id", "page_id", "google_package_id","firebase_project_id")
                    .limit(size).start(index * size).orderByAsc("tag_name").execute();
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return list;
    }

    private long countFacebookAppRelation() {
        try {
            JSObject object = DB.simpleScan("web_facebook_app_ids_rel").select(DB.func(DB.COUNT, "id")).execute();
            return object.get("count(id)");
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return 0;
    }

    private OperationResult deleteFacebookAppRelation(int id,String tagName) {
        OperationResult ret = new OperationResult();

        try {
            DB.delete("web_facebook_app_ids_rel").where(DB.filter().whereEqualTo("id", id)).execute();
            DB.delete("web_facebook_app_ids_page").where(DB.filter().whereEqualTo("tag_name", tagName)).execute();

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

    private OperationResult createNewFacebookAppRelation(String tagName, String accountId, String fbAppId, String gpPackageId,String firebaseProjectId,List<JSONObject> fbPages) {
        OperationResult ret = new OperationResult();

        try {
            JSObject one = DB.simpleScan("web_facebook_app_ids_rel").select("tag_name").where(DB.filter().whereEqualTo("tag_name", tagName)).execute();
            if (one.get("tag_name") != null) {
                ret.result = false;
                ret.message = "已经存在这个账号了";
            } else {
                String pageIdString = new String();
                for(JSONObject fbPage : fbPages){
                    pageIdString += (String)fbPage.get("page_id")+ ",";
                }
                pageIdString = pageIdString.substring(0,pageIdString.length()-2);
                DB.insert("web_facebook_app_ids_rel")
                        .put("tag_name", tagName)
                        .put("account_id", accountId)
                        .put("fb_app_id", fbAppId)
                        .put("page_id", pageIdString)
                        .put("google_package_id", gpPackageId)
                        .put("firebase_project_id", firebaseProjectId)
                        .execute();
                String page_id = null;
                String page_name = null;
                for (JSONObject fbPage : fbPages){
                    page_id = (String)fbPage.get("page_id");
                    page_name = (String)fbPage.get("page_name");
                    DB.insert("web_facebook_app_ids_page")//facebook主页关系表
                            .put("tag_name",tagName)
                            .put("fb_app_id",fbAppId)
                            .put("page_id",page_id)
                            .put("page_name",page_name)
                            .execute();
                }

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

    private OperationResult updateFacebookAppRelation(int id, String tagName, String accountId, String fbAppId,String gpPackageId,String firebaseProjectId,List<JSONObject> fbPages) {
        OperationResult ret = new OperationResult();

        try {
            String pageIdString = new String();
            for(JSONObject fbPage : fbPages){
                pageIdString += (String)fbPage.get("page_id")+ ",";
            }
            pageIdString = pageIdString.substring(0,pageIdString.length()-2);
            DB.update("web_facebook_app_ids_rel")
                    .put("tag_name", tagName)
                    .put("account_id", accountId)
                    .put("fb_app_id", fbAppId)
                    .put("page_id", pageIdString)
                    .put("google_package_id", gpPackageId)
                    .put("firebase_project_id", firebaseProjectId)
                    .where(DB.filter().whereEqualTo("id", id)).execute();

            //modify by maliang 实现修改逻辑，先删除原有的 再插入新的
            DB.delete("web_facebook_app_ids_page").where(DB.filter().whereEqualTo("tag_name", tagName)).execute();
            String page_id = null;
            String page_name = null;
            for (JSONObject fbPage : fbPages){
                page_id = (String)fbPage.get("page_id");
                page_name = (String)fbPage.get("page_name");
                DB.insert("web_facebook_app_ids_page")//facebook主页关系表
                        .put("tag_name",tagName)
                        .put("fb_app_id",fbAppId)
                        .put("page_id",page_id)
                        .put("page_name",page_name)
                        .execute();
            }
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
