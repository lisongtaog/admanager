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
 * @author lisongtao
 * @date 2018/8/13
 * @desc 将已有广告语信息的账号的信息复制给新的账号
 */
@WebServlet(name = "AdOldAccountCreateCopyToNew", urlPatterns = "/Ad_old_account_create_copy_to_new/*")
public class AdOldAccountCreateCopyToNew extends BaseHttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doPost(request, response);
        if (!Utils.isAdmin(request, response)) return;

        String path = request.getPathInfo();
        JsonObject json = new JsonObject();
        OperationResult result = new OperationResult();

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
                String id = request.getParameter("id");
                result = deleteAdOldAccountCreateCopyToNew(id);
                json.addProperty("ret", result.result ? 1 : 0);
                json.addProperty("message", result.message);

            } else if (path.startsWith("/update")) {
                String id = request.getParameter("id");
                String tagName0 = request.getParameter("tagname");
                String old = request.getParameter("old");
                String newC = request.getParameter("newC");
                String biddingMul = request.getParameter("biddingMul");
                String network = request.getParameter("network");

                result = updateAdOldAccountCreateCopyToNew(id, tagName0, old, newC, biddingMul, network);
                json.addProperty("ret", result.result ? 1 : 0);
                json.addProperty("message", result.message);

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
                        one.addProperty("old_account_id", (String) data.get(i).get("old_account_id"));
                        one.addProperty("new_account_id", (String) data.get(i).get("new_account_id"));
                        one.addProperty("bidding_mul", (Double) data.get(i).get("bidding_mul"));
                        one.addProperty("network", (String) data.get(i).get("network"));
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
                        one.addProperty("old_account_id", (String) data.get(i).get("old_account_id"));
                        one.addProperty("new_account_id", (String) data.get(i).get("new_account_id"));
                        one.addProperty("bidding_mul", (Double) data.get(i).get("bidding_mul"));
                        one.addProperty("network", (String) data.get(i).get("network"));
                        array.add(one);
                    }
                    json.add("data", array);
                }
            }
            response.getWriter().write(json.toString());
        }
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
            List<JSObject> jsObjectList = DB.scan("ad_old_account_create_copy_to_new").select("id", "tag_name", "old_account_id", "new_account_id", "bidding_mul", "network").limit(size).start(index * size).orderByAsc("id").execute();
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
            String sql = "SELECT id,tag_name,old_account_id,new_account_id,bidding_mul,network FROM `ad_old_account_create_copy_to_new` WHERE tag_name LIKE ? ";
            list = DB.findListBySql(sql, "%" + word + "%");
            java.lang.System.out.println(list);
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
            JSObject object = DB.simpleScan("ad_old_account_create_copy_to_new").select(DB.func(DB.COUNT, "id")).execute();
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
    private OperationResult deleteAdOldAccountCreateCopyToNew(String id) {
        int Oid = Integer.parseInt(id);
        OperationResult ret = new OperationResult();

        try {
            DB.delete("ad_old_account_create_copy_to_new").where(DB.filter().whereEqualTo("id", Oid)).execute();

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
     * 更新一条数据的方法
     *
     * @param id
     * @param tagName0
     * @param old
     * @param newC
     * @param biddingMul
     * @param network
     * @return
     */
    private OperationResult updateAdOldAccountCreateCopyToNew(String id, String tagName0, String old, String newC, String biddingMul, String network) {
        int tagId = Integer.parseInt(id);

        OperationResult ret = new OperationResult();

        try {
            ret.result = DB.update("ad_old_account_create_copy_to_new")
                    .put("tag_name", tagName0)
                    .put("old_account_id", old)
                    .put("new_account_id", newC)
                    .put("bidding_mul", Double.parseDouble(biddingMul))
                    .put("network", network)
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
     * 创建一条数据
     *
     * @param tagName0
     * @param old
     * @param newC
     * @param biddingMul
     * @param network
     * @return
     */
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
     * 对话框出现，自动加载帐户
     *
     * @return
     */
    public static List<JSObject> fetchAllAccount() {
        List<JSObject> list = new ArrayList<>();
        try {
            List<JSObject> FBAccount = DB.scan("web_account_id").select("id", "account_id").orderByAsc("id").execute();
            List<JSObject> AdmobAccount = DB.scan("web_account_id_admob").select("id", "account_id").orderByAsc("id").execute();
            FBAccount.addAll(AdmobAccount);

            return FBAccount;
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return list;
    }
}
