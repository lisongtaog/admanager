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
 * Created by lisongtao on 7/31/18.
 */
@WebServlet(name = "Ad_old_account_create_copy_to_new", urlPatterns = "/Ad_old_account_create_copy_to_new/*")
public class Ad_old_account_create_copy_to_new extends BaseHttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doPost(request, response);
        if (!Utils.isAdmin(request, response)) return;

        String path = request.getPathInfo();
        JsonObject json = new JsonObject();
        OperationResult result = new OperationResult();

        String tagName = request.getParameter("name");
        String bidding = request.getParameter("bidding");
        String countryName = request.getParameter("country");
        String id = request.getParameter("id");
        if (path != null) {
            if (path.startsWith("/create")) {

                String tagName0 = request.getParameter("tagname");
                String old = request.getParameter("old");
                String newC = request.getParameter("newC");
                String biddingMul = request.getParameter("biddingMul");
                String network = request.getParameter("network");


                if (!tagName0.isEmpty() && !old.isEmpty() && !newC.isEmpty() && !biddingMul.isEmpty() && !network.isEmpty()) {

                    result = create(tagName0, old, newC, biddingMul, network);
                    json.addProperty("ret", result.result ? 1 : 0);
                    json.addProperty("message", result.message);

                } else {
                    json.addProperty("ret", 0);
                    json.addProperty("message", "创建失败，请注意输入格式");
                }
            } else if (path.startsWith("/delete")) {
                result = deleteTag(id);
                json.addProperty("ret", result.result ? 1 : 0);
                json.addProperty("message", result.message);

            } else if (path.startsWith("/update")) {

                if (!tagName.isEmpty() && (bidding.matches("^\\d+(\\.\\d+)?$") || StringUtil.isEmpty(bidding)) || "".equals(countryName)) {
                    result = updateTag(id, null, countryName, bidding);
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
                        one.addProperty("country", (String) data.get(i).get("country_name"));
                        array.add(one);
                    }
                    json.add("data", array);
                } else {
                    long count = count();
                    int index = NumberUtil.parseInt(request.getParameter("page_index"), 0);
                    int size = NumberUtil.parseInt(request.getParameter("page_size"), 20);
//                    long totalPage = count / size + (count % size == 0 ? 0 : 1);
                    List<JSObject> data = fetchAllData(index, size);
                    json.addProperty("ret", 1);
                    JsonArray array = new JsonArray();
                    for (int i = 0; i < data.size(); i++) {
                        JsonObject one = new JsonObject();
                        one.addProperty("id", data.get(i).get("id").toString());
                        one.addProperty("tag_name", (String) data.get(i).get("tag_name"));
                        one.addProperty("bidding", (Double) data.get(i).get("bidding"));
                        one.addProperty("country", (String) data.get(i).get("country_name"));
                        array.add(one);
                    }
                    json.add("data", array);
                }
            } else if (path.startsWith("/selectByTagNameRegion")) {
                String app_name = request.getParameter("appName");
                String region = request.getParameter("region");
                JsonArray array = new JsonArray();
                try {
                    array = fetchBidding(app_name, region);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                json.add("data", array);
            }
        }

        response.getWriter().write(json.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * 页面刷新自动加载<table>数据
     *
     * @param index
     * @param size
     * @return
     */
    public static List<JSObject> fetchAllData(int index, int size) {
        List<JSObject> list = new ArrayList<>();
        try {
            List<JSObject> jsObjectList = DB.scan("Ad_old_account_create_copy_to_new").select("id", "tag_name", "old_account_id", "new_account_id", "bidding_mul", "network").limit(size).start(index * size).orderByAsc("id").execute();
            return jsObjectList;
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return list;
    }

    /**
     * 条件查询方法
     *
     * @param word
     * @return
     */
    public static List<JSObject> fetchData(String word) {
        List<JSObject> list = new ArrayList<>();
        try {
            String sql = "SELECT id,tag_name,bidding,country_name FROM web_tag_country_bidding WHERE tag_name LIKE ? ";
            list = DB.findListBySql(sql, "%" + word + "%");
            java.lang.System.out.println(list);
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return list;
    }

    /**
     * 对话框出现，自动加载标签
     *
     * @return
     */
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

    /**
     * 页面数据总量
     *
     * @return
     */
    public static long count() {
        try {
            JSObject object = DB.simpleScan("web_tag_country_bidding").select(DB.func(DB.COUNT, "id")).execute();
            return object.get("count(id)");
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return 0;
    }

    /**
     * 删除一条数据方法
     *
     * @param id
     * @return
     */
    private OperationResult deleteTag(String id) {
        int tagId = Integer.parseInt(id);
        OperationResult ret = new OperationResult();

        try {
            DB.delete("web_tag_country_bidding").where(DB.filter().whereEqualTo("id", tagId)).execute();

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

    /**
     * 创建一条数据方法
     *
     * @param tagName
     * @param bidding
     * @param countryName
     * @return
     */
    private OperationResult createNewTag(String tagName, String bidding, String countryName) {
        OperationResult ret = new OperationResult();

        try {
            JSObject one = DB.simpleScan("web_tag_country_bidding")
                    .select("id")
                    .where(DB.filter().whereEqualTo("tag_name", tagName))
                    .and(DB.filter().whereEqualTo("country_name", countryName))
                    .execute();

            if (!one.hasObjectData()) {
                DB.insert("web_tag_country_bidding")
                        .put("tag_name", tagName)
                        .put("bidding", Double.parseDouble(bidding))
                        .put("country_name", countryName)
                        .execute();

                ret.result = true;
                ret.message = "创建成功";
            } else {
                ret.result = false;
                ret.message = "该应用、国家的出价已经存在，请做必要的更改";
            }

        } catch (Exception e) {
            e.printStackTrace();
            ret.result = false;
            ret.message = e.getMessage();
        }

        return ret;
    }


    /**
     * 更新一条数据的方法
     *
     * @param id
     * @param tagName
     * @param countryName
     * @param bidding
     * @return
     */
    private OperationResult updateTag(String id, String tagName, String countryName, String bidding) {
        int tagId = Integer.parseInt(id);

        OperationResult ret = new OperationResult();

        try {

            ret.result = DB.update("web_tag_country_bidding")
                    .put("country_name", countryName)
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

    /**
     * 通过appName和国家地区查询出竞价
     *
     * @param appName
     * @param region
     * @return
     * @throws Exception
     */
    private JsonArray fetchBidding(String appName, String region) throws Exception {
        JsonArray biddingArray = new JsonArray();
        String[] regionArray = region.split(",");
        for (int i = 0, len = regionArray.length; i < len; i++) {
            String sql = "SELECT country_name,bidding FROM web_tag_country_bidding WHERE tag_name = '" + appName + "' AND country_name = '" + regionArray[i] + "'";
            JSObject oneBidding = DB.findOneBySql(sql);
            if (oneBidding.hasObjectData()) {
                JsonObject jsonObject = new JsonObject();

                jsonObject.addProperty("country", oneBidding.get("country_name").toString());
                jsonObject.addProperty("bidding", oneBidding.get("bidding").toString());

                biddingArray.add(jsonObject);
            }
        }
        return biddingArray;
    }

    private OperationResult create(String tagName0, String old, String newC, String biddingMul, String network) {
        OperationResult ret = new OperationResult();

        try {
            DB.insert("ad_old_account_create_copy_to_new")
                    .put("tag_name", tagName0)
                    .put("old_account_id", old)
                    .put("new_account_id", newC)
                    .put("bidding_mul", Double.parseDouble(biddingMul))
                    .put("network", network)
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
}
