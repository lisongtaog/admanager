package com.bestgo.admanager.servlet;

import com.bestgo.admanager.Config;
import com.bestgo.admanager.OperationResult;
import com.bestgo.admanager.bean.BatchChangeItem;
import com.bestgo.admanager.bean.User;
import com.bestgo.admanager.utils.DateUtil;
import com.bestgo.admanager.utils.StringUtil;
import com.bestgo.admanager.utils.Utils;
import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;
import com.google.gson.*;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.lang.System;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Auth:maliang
 * Date:2018-06-11 11:21 AM
 * Desc:广告系列 与 广告单元 管理功能
 */
@WebServlet(name = "CampaignAdUnit", urlPatterns = "/campaignAdUnit/*")
public class CampaignAdUnit extends BaseHttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doPost(request, response);
        if (!Utils.isAdmin(request, response)) return;
        handleRequest(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

   /* private Map<String,Object>handleParams(HttpServletRequest request){
        String key = null;
        String[] values = null;
        Map<String,String[]> paramMap = request.getParameterMap();
        Iterator<String> ite = paramMap.keySet().iterator();
        while (ite.hasNext()){
            key = ite.next();
            values = paramMap.get(key);

        }

        return null;
    }*/

    /**
     * servlet请求处理
     * @param request   servlet请求
     * @param response  servlet相应
     * @throws ServletException
     * @throws IOException
     */
    protected void handleRequest (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        JsonObject json = null;

        if (path.startsWith("/create")) {
            json = handleCreate(request,response);
        } else if (path.startsWith("/update")) {

        }else if (path.startsWith("/query")) {
            String word = request.getParameter("word");
            if (word != null) {
            }
            //json.add("data", array);
        }
        response.getWriter().write(json.toString());
    }

    /**
     * 创建广告系列与广告单元的关联关系
     * @param request
     * @param response
     * @return  创建处理结果
     * @throws ServletException
     * @throws IOException
     */
    private JsonObject  handleCreate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        JsonObject json = new JsonObject();
        String gName = request.getParameter("gName");

        HttpSession session = request.getSession();
        User loginUser = (User) session.getAttribute("loginUser");

        String adUnits = request.getParameter("adUnits");//广告单元多条记录json对象字符串
        String adCampaigns = request.getParameter("campaigns");//已选的广告系列多条记录json对象字符串
        OperationResult result = new OperationResult();
        JsonParser parser = new JsonParser();
        JsonArray campaigns = parser.parse(adCampaigns).getAsJsonArray();
        JsonArray units = parser.parse(adUnits).getAsJsonArray();
        try {
            result.result = false;
            DB.insert("ad_campaigns_adgroup").put("gname",gName).execute();
            JSObject gId = DB.findOneBySql("select gid from ad_campaigns_adgroup where gname = ?",gName);
            for(int i=0;i<campaigns.size();i++){
                JsonObject jCam = campaigns.get(i).getAsJsonObject();
                String campaign_name = jCam.get("campaign_name").getAsString();
                String campaign_id = jCam.get("campaign_id").getAsString();
                String validstatus = jCam.get("validstatus").getAsString();
                if(validstatus == "enabled"){
                    validstatus = "1";
                }else if(validstatus == "paused"){
                    validstatus = "0";
                }else{
                    validstatus = "2";  // "removed"
                }
                for(int k=0;k<units.size();k++){
                    JsonObject jUnit = units.get(k).getAsJsonObject();
                    String adunit_id = jUnit.get("adunit_id").getAsString();
                    String network = jUnit.get("network").getAsString();
                    String name = jUnit.get("name").getAsString();
                    String type = jUnit.get("type").getAsString();
                    Date now = new Date();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String operatetime = dateFormat.format( now );
                    result.result=DB.insert("ad_campaigns_adunit")
                            .put("gid",gId.get("gid"))
                            .put("campaign_id",campaign_id)
                            .put("campaign_name",campaign_name)
                            .put("adunit_id",adunit_id)
                            .put("network",network)
                            .put("name",name)
                            .put("type",type)
                            .put("validstatus",validstatus)
                            .put("operatetime",operatetime)
                            .execute();
                }
            }
            if (result.result) {
                    /*Calendar calendar = Calendar.getInstance();
                    String campaignNameOld = campaignName.replace("Group_","Group" + groupId) + "_";
                    String[] accountNameArr = accountName.split(",");
                    String accountNameArrStr = accountName.replace(",", "");
                    String[] accountIdArr = accountId.split(",");
                    int createCountInt = Integer.parseInt(createCount);*/
                            /*
                            long genId = DB.insert("ad_campaigns")
                                    .put("facebook_app_id", appId)
                                    .put("account_id", accountIdArr[j])
                                    .put("country_region", region)
                                    .put("excluded_region", excludedRegion)
                                    .put("create_time", now)
                                    .put("language", language)
                                    .put("campaign_name", campaignName)
                                    .put("page_id", pageId)
                                    .put("bugdet", bugdet)
                                    .put("bidding", bidding)
                                    .put("group_id", groupId)
                                    .put("title", title)
                                    .put("message", message)
                                    .put("app_name", appName)
                                    .put("tag_name", appName)
                                    .put("age", age)
                                    .put("gender", gender)
                                    .put("detail_target", interest)
                                    .put("max_cpa", maxCPA)
                                    .put("user_devices", userDevice)
                                    .put("user_os", userOs)
                                    .put("publisher_platforms", publisherPlatforms)
                                    .executeReturnId();
                            */
            }
        } catch (Exception ex) {
            result.message = ex.getMessage();
            result.result = false;
        }
        json.addProperty("ret", result.result ? 1 : 0);
        json.addProperty("message", result.message);
        return json;
    }
/*
    public static List<JSObject> fetchData(String word) {
        List<JSObject> list = new ArrayList<>();
        try {
            return DB.scan("web_ad_campaigns").select("id", "campaign_id", "adset_id", "account_id", "campaign_name", "create_time",
                    "status", "budget", "bidding", "total_spend", "total_installed", "total_click", "cpa", "ctr")
                    .where(DB.filter().whereLikeTo("campaign_name", "%" + word + "%")).or(DB.filter().whereEqualTo("campaign_id", word)).orderByAsc("id").execute();
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return list;
    }

    public static List<JSObject> fetchData(int index, int size) {
        List<JSObject> list = new ArrayList<>();
        try {
            return DB.scan("web_ad_campaigns").select("id", "campaign_id", "adset_id", "account_id", "campaign_name", "create_time",
                    "status", "budget", "bidding", "total_spend", "total_installed", "total_click", "cpa", "ctr")
                    .limit(size).start(index * size).orderByAsc("id").execute();
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return list;
    }


    private static List<JSObject> findBindTags(String campaignId) {
        ArrayList<JSObject> retList = new ArrayList<>();
        String sql = "select tag_id, tag_name from web_ad_campaign_tag_rel,web_tag where web_tag.id=web_ad_campaign_tag_rel.tag_id and campaign_id=?";
        try {
            return DB.findListBySql(sql, campaignId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retList;
    }

    public static List<String> bindTags(String campaignId) {
        ArrayList<String> retList = new ArrayList<>();
        String sql = "select tag_name from web_ad_campaign_tag_rel,web_tag where web_tag.id=web_ad_campaign_tag_rel.tag_id and campaign_id=?";
        try {
            List<JSObject> list = DB.findListBySql(sql, campaignId);
            for (int i = 0; i < list.size(); i++) {
                retList.add(list.get(i).get("tag_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retList;
    }*/
}

