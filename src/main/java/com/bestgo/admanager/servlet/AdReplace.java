package com.bestgo.admanager.servlet;

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
 * Created by LiSongTao on 7/24/17.
 */
@WebServlet(name = "AdReplace", urlPatterns = "/adReplace/*")
public class AdReplace extends BaseHttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doPost(request, response);
        if (!Utils.isAdmin(request, response)) return;

        String path = request.getPathInfo();
        JsonObject json = new JsonObject();

        if (path != null) {
            if ("/tagName/query".equals(path)) {
                String word = request.getParameter("word");
                if (word != null && !"".equals(word)) {

                    List<JSObject> data = fetchTagNameData(word);
                    json.addProperty("ret", 1);
                    JsonArray array = new JsonArray();

                    List<JSObject> fbPage = null;//facebook主页信息
                    String tagName = null;
                    for (int i = 0; i < data.size(); i++) {
                        JsonObject one = new JsonObject();
                        tagName = (String) data.get(i).get("tag_name");
                        one.addProperty("tag_name", tagName);
                        one.addProperty("id", (long) data.get(i).get("id"));

                        array.add(one);
                    }
                    json.add("data", array);
                } else {
                    int count = (int) countWebTag();
                    int index = NumberUtil.parseInt(request.getParameter("page_index"), 0);
                    int size = NumberUtil.parseInt(request.getParameter("page_size"), count);
                    List<JSObject> data = fetchTagNameData(index, size);
                    json.addProperty("ret", 1);

                    JsonArray array = new JsonArray();
                    String tagName = "";
                    for (int i = 0; i < data.size(); i++) {
                        JsonObject one = new JsonObject();
                        tagName = (String) data.get(i).get("tag_name");
                        one.addProperty("tag_name", tagName);
                        one.addProperty("id", (long) data.get(i).get("id"));

                        array.add(one);
                    }
                    json.add("data", array);
                }


            } else if ("/adReplace".equals(path)) {
                String app0 = request.getParameter("app0");
                String app1 = request.getParameter("app1");
                String option = request.getParameter("option");
                if ("0".equals(option)) {
                    String message = fbAdReplace(app0, app1);
                    if ("1".equals(message)) {
                        json.addProperty("ret", "1");
                    } else {
                        json.addProperty("message", message);
                    }

                } else if ("1".equals(option)) {
                    String message = adWordsAdReplace(app0, app1);
                    if ("1".equals(message)) {
                        json.addProperty("ret", "1");
                    } else {
                        json.addProperty("message", message);
                    }

                }
            }
        }
        response.getWriter().write(json.toString());
    }

    /**
     * 条件查询方法
     *
     * @param word
     * @return
     */
    private List<JSObject> fetchTagNameData(String word) {
        List<JSObject> list = new ArrayList<>();
        try {
            return DB.scan("web_tag").select("id", "tag_name")
                    .where(DB.filter().whereLikeTo("tag_name", "%" + word + "%")).orderByAsc("tag_name").execute();
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return list;
    }

    /**
     * 刷新页面无条件查询方法
     *
     * @param index
     * @param size
     * @return
     */
    private List<JSObject> fetchTagNameData(int index, int size) {
        List<JSObject> list = new ArrayList<>();
        try {
            return DB.scan("web_tag").select("id", "tag_name")
                    .limit(size).start(index * size).orderByAsc("tag_name").execute();
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return list;
    }

    /**
     * 查询数据的总数方法
     *
     * @return
     */
    private long countWebTag() {
        try {
            JSObject object = DB.simpleScan("web_tag").select(DB.func(DB.COUNT, "id")).execute();
            return object.get("count(id)");
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return 0;
    }

    /**
     * FB的两个广告语的替换方法
     *
     * @param app0
     * @param app1
     * @return
     */
    private String fbAdReplace(String app0, String app1) {

        try {

            DB.updateBySql("DROP TABLE IF EXISTS web_ad_descript_dict_copy");

            DB.delete("web_ad_descript_dict").where(DB.filter().whereEqualTo("app_name", app0)).execute();

            DB.updateBySql("CREATE TABLE web_ad_descript_dict_copy AS (SELECT * FROM web_ad_descript_dict WHERE app_name =  '" + app1 + "')");

            DB.update("web_ad_descript_dict_copy").put("app_name", app0).execute();

            DB.updateBySql("INSERT INTO web_ad_descript_dict (app_name,`language`,group_id,title,message)" +
                    " SELECT app_name,`language`,group_id,title,message FROM web_ad_descript_dict_copy");

            DB.updateBySql("DROP TABLE IF EXISTS web_ad_descript_dict_copy");

        } catch (Exception e) {
            return e.getMessage();
        }
        return "1";
    }

    /**
     * adWords的两个广告语的替换方法
     *
     * @param app0
     * @param app1
     * @return
     */
    private String adWordsAdReplace(String app0, String app1) {

        try {

            DB.updateBySql("DROP TABLE IF EXISTS web_ad_descript_dict_admob_copy");

            DB.delete("web_ad_descript_dict_admob").where(DB.filter().whereEqualTo("app_name", app0)).execute();

            DB.updateBySql("CREATE TABLE web_ad_descript_dict_admob_copy AS (SELECT * FROM web_ad_descript_dict_admob WHERE app_name =  '" + app1 + "')");

            DB.update("web_ad_descript_dict_admob_copy").put("app_name", app0).execute();

            DB.updateBySql(" INSERT INTO web_ad_descript_dict_admob (app_name,`language`,group_id,message1,message2,message3,message4) " +
                    " SELECT app_name,`language`,group_id,message1,message2,message3,message4 FROM web_ad_descript_dict_admob_copy ");

            DB.updateBySql("DROP TABLE IF EXISTS web_ad_descript_dict_admob_copy");

        } catch (Exception e) {
            return e.getMessage();
        }
        return "1";
    }
}
