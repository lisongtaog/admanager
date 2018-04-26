package com.bestgo.admanager.servlet;

import com.bestgo.admanager.utils.Utils;
import com.bestgo.admanager.bean.AppBean;
import com.bestgo.admanager.bean.PageBean;
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
 * 首页的汇总
 */
@WebServlet(name = "Query2", urlPatterns = {"/query2"}, asyncSupported = true)
public class Query2 extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Utils.isAdmin(request, response)) return;
        JsonObject json = new JsonObject();
        //开始日期
        String startTime = request.getParameter("startTime");

        //结束日期
        String endTime = request.getParameter("endTime");

        //当前页码字符串
        String pageNumStr = request.getParameter("pageNum");
        int pageNum = 1;
        if(pageNumStr != "" && pageNumStr != null){
            pageNum = Utils.parseInt(pageNumStr,0);
        }
        //每页显示的条数
        int pageSize = 10;

        //判断开始日期和结束日期是否相同，默认为不同
        boolean sameTime = false;
        if(startTime.equals(endTime)){
            sameTime = true;
        }
        String adwordsCheck = request.getParameter("adwordsCheck");
        String facebookCheck = request.getParameter("facebookCheck");
        try {
            PageBean<AppBean> pageBean = FindAllWithPage(pageNum, pageSize, facebookCheck, adwordsCheck, startTime, endTime, sameTime);
            request.setAttribute("pageBean",pageBean);
            request.getRequestDispatcher("/index2.jsp").forward(request,response);
        } catch (Exception ex) {
            ex.printStackTrace();
            json.addProperty("ret", 0);
            json.addProperty("message", ex.getMessage());
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
    }


    private PageBean<AppBean> FindAllWithPage(int pageNum,int pageSize,String facebookCheck,String adwordsCheck,String startTime,String endTime,boolean sameTime) throws Exception {
        JsonObject json = new JsonObject();
        JsonArray arr = new JsonArray();
        String sql = "SELECT COUNT(id) AS record_count FROM web_tag";
        JSObject one = DB.findOneBySql(sql);

        //总记录数
        Long totalRecord = one.get("record_count");
        sql = "SELECT t.id,t.tag_name,google_package_id from web_tag t LEFT JOIN web_facebook_app_ids_rel air ON t.tag_name = air.tag_name " +
                "ORDER BY t.tag_name LIMIT "+ (pageNum - 1) * pageSize + "," + pageSize;
        List<JSObject> tagList = DB.findListBySql(sql);
        json.add("data", arr);

        json.addProperty("ret", 1);
        json.addProperty("message", "执行成功");
        PageBean<AppBean> pageBean = new PageBean<AppBean>(pageNum,pageSize,totalRecord.intValue());

        List<AppBean> appBeanList = fetchAllAppDataSummary(tagList, startTime, endTime, facebookCheck, adwordsCheck, sameTime);
        pageBean.setList(appBeanList);
        return pageBean;
    }


    /**
     * 查询所有应用的汇总数据，包括Facebook和Adwords
     * @param tagList
     * @param startTime
     * @param endTime
     * @param facebookCheck facebook被勾选
     * @param adwordsCheck
     * @param sameTime
     * @return
     * @throws Exception
     */
    private List<AppBean> fetchAllAppDataSummary(List<JSObject> tagList, String startTime, String endTime, String facebookCheck, String adwordsCheck, boolean sameTime) throws Exception {
        List<AppBean> appBeanList = new ArrayList<>();
        JsonObject admob = null;
        JsonObject facebook = null;
        for (JSObject tagJSObject : tagList) {
            AppBean appBean = new AppBean();
            long id = tagJSObject.get("id");
            appBean.name = tagJSObject.get("tag_name");

            if("false".equals(adwordsCheck) && "false".equals(facebookCheck)){
                admob = fetchOneAppDataSummaryByAdmobCheck(id, startTime, endTime, true,sameTime);
                facebook = fetchOneAppDataSummaryByAdmobCheck(id, startTime, endTime, false,sameTime);
                appBean.total_impressions = admob.get("total_impressions").getAsDouble() + facebook.get("total_impressions").getAsDouble();
                if (appBean.total_impressions == 0) {
                    continue;
                }
                appBean.total_spend = admob.get("total_spend").getAsDouble() + facebook.get("total_spend").getAsDouble();
                if(sameTime){
                    appBean.end_time_total_spend = appBean.total_spend;
                }else{
                    JsonObject admob1 = fetchOneAppDataSummaryByAdmobCheck(id, endTime, endTime, true,true);
                    JsonObject facebook1 = fetchOneAppDataSummaryByAdmobCheck(id, endTime, endTime, false,true);
                    appBean.end_time_total_spend = Utils.trimDouble(admob1.get("total_spend").getAsDouble() + facebook1.get("total_spend").getAsDouble(), 0);
                }

                appBean.total_installed = admob.get("total_installed").getAsDouble() + facebook.get("total_installed").getAsDouble();
                appBean.total_click = admob.get("total_click").getAsDouble() + facebook.get("total_click").getAsDouble();
            }else if("true".equals(adwordsCheck)){
                admob = fetchOneAppDataSummaryByAdmobCheck(id, startTime, endTime, true,sameTime);
                appBean.total_impressions = admob.get("total_impressions").getAsDouble();
                if (appBean.total_impressions == 0) {
                    continue;
                }
                appBean.total_spend = admob.get("total_spend").getAsDouble();
                if(sameTime){
                    appBean.end_time_total_spend = appBean.total_spend;
                }else{
                    JsonObject admob1 = fetchOneAppDataSummaryByAdmobCheck(id, endTime, endTime, true,true);
                    appBean.end_time_total_spend = Utils.trimDouble(admob1.get("total_spend").getAsDouble(),0);
                }

                appBean.total_installed = admob.get("total_installed").getAsDouble();
                appBean.total_click = admob.get("total_click").getAsDouble();
            }else if("true".equals(facebookCheck)){
                facebook = fetchOneAppDataSummaryByAdmobCheck(id, startTime, endTime, false,sameTime);
                appBean.total_impressions = facebook.get("total_impressions").getAsDouble();
                if (appBean.total_impressions == 0) {
                    continue;
                }
                appBean.total_spend = facebook.get("total_spend").getAsDouble();
                if(sameTime){
                    appBean.end_time_total_spend = appBean.total_spend;
                }else{
                    JsonObject facebook1 = fetchOneAppDataSummaryByAdmobCheck(id, endTime, endTime, false,true);
                    appBean.end_time_total_spend = Utils.trimDouble(facebook1.get("total_spend").getAsDouble(), 0);
                }

                appBean.total_installed = facebook.get("total_installed").getAsDouble();
                appBean.total_click = facebook.get("total_click").getAsDouble();
            }

            appBean.total_ctr = appBean.total_impressions > 0 ? appBean.total_click / appBean.total_impressions : 0;
            appBean.total_cpa = appBean.total_installed > 0 ? appBean.total_spend / appBean.total_installed : 0;
            appBean.total_cvr = appBean.total_click > 0 ? appBean.total_installed / appBean.total_click : 0;
            String google_package_id = tagJSObject.get("google_package_id");
            if (google_package_id != null && google_package_id != "") {
                if(sameTime){
                    String sqlR = "select sum(revenue) as revenues " +
                            "from web_ad_country_analysis_report_history where app_id = '"
                            + google_package_id + "' and date = '" + endTime + "'";
                    JSObject oneR = DB.findOneBySql(sqlR);
                    if (oneR.hasObjectData()) {
                        appBean.total_revenue = Utils.convertDouble(oneR.get("revenues"), 0);
                        appBean.end_time_total_revenue = appBean.total_revenue;
                    }
                }else{
                    String sqlR = "select sum(revenue) as revenues " +
                            "from web_ad_country_analysis_report_history where app_id = '"
                            + google_package_id + "' and date BETWEEN '" + startTime + "' AND '" + endTime + "'";

                    JSObject oneR = DB.findOneBySql(sqlR);
                    if (oneR.hasObjectData()) {
                        appBean.total_revenue = Utils.convertDouble(oneR.get("revenues"), 0);
                    }
                    sqlR = "select sum(revenue) as revenues " +
                            "from web_ad_country_analysis_report_history where app_id = '"
                            + google_package_id + "' and date = '" + endTime + "'";
                    oneR = DB.findOneBySql(sqlR);
                    if (oneR.hasObjectData()) {
                        appBean.end_time_total_revenue = Utils.trimDouble(Utils.convertDouble(oneR.get("revenues"), 0), 0);
                    }
                }


                //计算ECPM和Incoming
                appBean.ecpm = appBean.total_revenue * 1000 / appBean.total_impressions;
                appBean.incoming = appBean.total_revenue - appBean.total_spend;

                String sql = "select warning_level from  web_app_logs where app_name = '" + appBean.name + "' and log_date = '" + endTime + "'";
                JSObject one = DB.findOneBySql(sql);
                if (one.hasObjectData()) {
                    appBean.warningLevel = one.get("warning_level");             //指标warning_level 仅仅与endTime有关
                }
            }
            appBeanList.add(appBean);
        }
        return appBeanList;
    }

    /**
     * 分Facebook或Adwords抓取每个应用的汇总
     * @param tagId
     * @param startTime
     * @param endTime
     * @param admobCheck
     * @param sameTime
     * @return
     * @throws Exception
     */
    private JsonObject fetchOneAppDataSummaryByAdmobCheck(long tagId, String startTime, String endTime, boolean admobCheck,boolean sameTime) throws Exception {
        String webAdCampaignTagRelTable = "web_ad_campaign_tag_rel";
        String webAdCampaignsTable = "web_ad_campaigns";
        String webAdCampaignsHistoryTable = "web_ad_campaigns_history";
        if (admobCheck) {
            webAdCampaignTagRelTable = "web_ad_campaign_tag_admob_rel";
            webAdCampaignsTable = "web_ad_campaigns_admob";
            webAdCampaignsHistoryTable = "web_ad_campaigns_history_admob";
        }

        String sql = "select sum(ch.total_spend) as spend, " +
                "sum(ch.total_installed) as installed, sum(ch.total_impressions) as impressions " +
                ",sum(ch.total_click) as click from " + webAdCampaignsTable + " c, " + webAdCampaignsHistoryTable + " ch, " +
                "(select distinct campaign_id from " + webAdCampaignTagRelTable + " where tag_id = " + tagId + ") rt " +
                "where rt.campaign_id = ch.campaign_id and c.campaign_id = ch.campaign_id " +
                "and date between '" + startTime + "' and '" + endTime + "'";
        if(sameTime){
            sql = "select sum(ch.total_spend) as spend, " +
                    "sum(ch.total_installed) as installed, sum(ch.total_impressions) as impressions " +
                    ",sum(ch.total_click) as click from " + webAdCampaignsTable + " c, " + webAdCampaignsHistoryTable + " ch, " +
                    "(select distinct campaign_id from " + webAdCampaignTagRelTable + " where tag_id = " + tagId + ") rt " +
                    "where rt.campaign_id = ch.campaign_id and c.campaign_id = ch.campaign_id " +
                    "and date = '" + endTime + "'";
        }
        JSObject one = DB.findOneBySql(sql);

        JsonObject jsonObject = new JsonObject();
        double total_spend = Utils.convertDouble(one.get("spend"), 0);
        double total_installed = Utils.convertDouble(one.get("installed"), 0);
        double total_impressions = Utils.convertDouble(one.get("impressions"), 0);
        double total_click = Utils.convertDouble(one.get("click"), 0);

        double total_ctr = total_impressions > 0 ? total_click / total_impressions : 0;
        double total_cpa = total_installed > 0 ? total_spend / total_installed : 0;
        double total_cvr = total_click > 0 ? total_installed / total_click : 0;

        jsonObject.addProperty("total_spend", Utils.trimDouble(total_spend, 0));
        jsonObject.addProperty("total_installed", total_installed);
        jsonObject.addProperty("total_impressions", total_impressions);
        jsonObject.addProperty("total_click", total_click);
        jsonObject.addProperty("total_ctr", Utils.trimDouble(total_ctr, 3));
        jsonObject.addProperty("total_cpa", Utils.trimDouble(total_cpa, 3));
        jsonObject.addProperty("total_cvr", Utils.trimDouble(total_cvr, 3));
        return jsonObject;
    }

}
