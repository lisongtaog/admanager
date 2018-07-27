package com.bestgo.admanager.servlet;

import com.bestgo.admanager.utils.NumberUtil;
import com.bestgo.admanager.bean.AppBean;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 首页的汇总
 */
@WebServlet(name = "Query", urlPatterns = {"/query"}, asyncSupported = true)
public class Query extends HttpServlet {

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

        //判断开始日期和结束日期是否相同，默认为不同
        boolean sameTime = false;
        if(startTime.equals(endTime)){
            sameTime = true;
        }

        String sorterId = request.getParameter("sorterId");
        String adwordsCheck = request.getParameter("adwordsCheck");
        String facebookCheck = request.getParameter("facebookCheck");

        int sorter = 0;
        if (sorterId != null) {
            sorter = NumberUtil.parseInt(sorterId, 0);
        }
        try {
            JsonArray arr = new JsonArray();
            if (sorter > 0) {
                    ArrayList<AppBean> appBeanList = new ArrayList<>();
                    String sqlTag = "SELECT t.id,t.tag_name,google_package_id FROM web_tag t,web_facebook_app_ids_rel air WHERE t.tag_name = air.tag_name";
                    List<JSObject> tagList = DB.findListBySql(sqlTag);

                    for (JSObject tagJSObject : tagList) {
                        AppBean appBean = new AppBean();
                        long id = tagJSObject.get("id");
                        appBean.name = tagJSObject.get("tag_name");

                        if("false".equals(adwordsCheck) && "false".equals(facebookCheck)){
                            JsonObject admob = fetchOneAppDataSummary(id, startTime, endTime, true,sameTime);
                            JsonObject facebook = fetchOneAppDataSummary(id, startTime, endTime, false,sameTime);
                            appBean.total_impressions = admob.get("total_impressions").getAsDouble() + facebook.get("total_impressions").getAsDouble();
                            if (appBean.total_impressions == 0) {
                                continue;
                            }
                            appBean.total_spend = admob.get("total_spend").getAsDouble() + facebook.get("total_spend").getAsDouble();
                            if(sameTime){
                                appBean.end_time_total_spend = appBean.total_spend;
                            }else{
                                JsonObject admob1 = fetchOneAppDataSummary(id, endTime, endTime, true,true);
                                JsonObject facebook1 = fetchOneAppDataSummary(id, endTime, endTime, false,true);
                                appBean.end_time_total_spend = NumberUtil.trimDouble(admob1.get("total_spend").getAsDouble() + facebook1.get("total_spend").getAsDouble(), 0);
                            }

                            appBean.total_installed = admob.get("total_installed").getAsDouble() + facebook.get("total_installed").getAsDouble();
                            appBean.total_click = admob.get("total_click").getAsDouble() + facebook.get("total_click").getAsDouble();
                        }else if("true".equals(adwordsCheck)){
                            JsonObject admob = fetchOneAppDataSummary(id, startTime, endTime, true,sameTime);
                            appBean.total_impressions = admob.get("total_impressions").getAsDouble();
                            if (appBean.total_impressions == 0) {
                                continue;
                            }
                            appBean.total_spend = admob.get("total_spend").getAsDouble();
                            if(sameTime){
                                appBean.end_time_total_spend = appBean.total_spend;
                            }else{
                                JsonObject admob1 = fetchOneAppDataSummary(id, endTime, endTime, true,true);
                                appBean.end_time_total_spend = NumberUtil.trimDouble(admob1.get("total_spend").getAsDouble(),0);
                            }

                            appBean.total_installed = admob.get("total_installed").getAsDouble();
                            appBean.total_click = admob.get("total_click").getAsDouble();
                        }else if("true".equals(facebookCheck)){
                            JsonObject facebook = fetchOneAppDataSummary(id, startTime, endTime, false,sameTime);
                            appBean.total_impressions = facebook.get("total_impressions").getAsDouble();
                            if (appBean.total_impressions == 0) {
                                continue;
                            }
                            appBean.total_spend = facebook.get("total_spend").getAsDouble();
                            if(sameTime){
                                appBean.end_time_total_spend = appBean.total_spend;
                            }else{
                                JsonObject facebook1 = fetchOneAppDataSummary(id, endTime, endTime, false,true);
                                appBean.end_time_total_spend = NumberUtil.trimDouble(facebook1.get("total_spend").getAsDouble(), 0);
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
                                    appBean.total_revenue = NumberUtil.convertDouble(oneR.get("revenues"), 0);
                                    appBean.end_time_total_revenue = appBean.total_revenue;
                                }
                            }else{
                                String sqlR = "select sum(revenue) as revenues " +
                                        "from web_ad_country_analysis_report_history where app_id = '"
                                        + google_package_id + "' and date BETWEEN '" + startTime + "' AND '" + endTime + "'";

                                JSObject oneR = DB.findOneBySql(sqlR);
                                if (oneR.hasObjectData()) {
                                    appBean.total_revenue = NumberUtil.convertDouble(oneR.get("revenues"), 0);
                                }
                                sqlR = "select sum(revenue) as revenues " +
                                        "from web_ad_country_analysis_report_history where app_id = '"
                                        + google_package_id + "' and date = '" + endTime + "'";
                                oneR = DB.findOneBySql(sqlR);
                                if (oneR.hasObjectData()) {
                                    appBean.end_time_total_revenue = NumberUtil.trimDouble(NumberUtil.convertDouble(oneR.get("revenues"), 0), 0);
                                }
                            }

                            //计算ECPM和Incoming
                            appBean.ecpm = appBean.total_revenue * 1000 / appBean.total_impressions;
                            appBean.incoming = appBean.total_revenue - appBean.total_spend;
                        }
                        appBeanList.add(appBean);
                    }

                    if (appBeanList != null && appBeanList.size() > 0) {
                        //对应用进行排序
                        sorting(appBeanList,sorter);

                        for (AppBean cs : appBeanList) {
                            JsonObject j = new JsonObject();
                            j.addProperty("name", cs.name);
                            String sql = "select warning_level from  web_app_logs where app_name = '" + cs.name + "' and log_date = '" + endTime + "'";
                            JSObject one = DB.findOneBySql(sql);
                            if (one.hasObjectData()) {
                                int warningLevel = one.get("warning_level");             //指标warning_level 仅仅与endTime有关
                                j.addProperty("warning_level", warningLevel);
                            }
                            j.addProperty("total_spend", NumberUtil.trimDouble(cs.total_spend, 0));
                            j.addProperty("endTime_total_spend", NumberUtil.trimDouble(cs.end_time_total_spend, 0));
                            j.addProperty("endTime_total_revenue", NumberUtil.trimDouble(cs.end_time_total_revenue, 0));
                            j.addProperty("total_installed", cs.total_installed);
                            j.addProperty("total_impressions", cs.total_impressions);
                            j.addProperty("total_click", cs.total_click);
                            j.addProperty("total_ctr", NumberUtil.trimDouble(cs.total_ctr, 3));
                            j.addProperty("total_cpa", NumberUtil.trimDouble(cs.total_cpa, 3));
                            j.addProperty("total_cvr", NumberUtil.trimDouble(cs.total_cvr, 3));
                            j.addProperty("total_revenue", NumberUtil.trimDouble(cs.total_revenue, 0));
                            j.addProperty("ecpm",NumberUtil.trimDouble(cs.ecpm,3));
                            j.addProperty("incoming",NumberUtil.trimDouble(cs.incoming,0));

                            arr.add(j);
                        }
                    }
                    json.add("data", arr);
            } else {   //这里是sorter=0 的条件时,默认也是0
                    String sqlTag = "SELECT t.id,t.tag_name,google_package_id from web_tag t LEFT JOIN web_facebook_app_ids_rel air ON t.tag_name = air.tag_name ORDER BY t.tag_name";
                    List<JSObject> tagList = DB.findListBySql(sqlTag);
                    JsonObject admob = null;
                    JsonObject facebook = null;
                    for (JSObject tagJSObject : tagList) {
                        long id = tagJSObject.get("id");
                        String tagName = tagJSObject.get("tag_name");
                        double total_impressions = 0;
                        double total_spend = 0;
                        double total_installed = 0;
                        double total_click = 0;

                        if(facebookCheck.equals("false") && adwordsCheck.equals("false")){
                            admob = fetchOneAppDataSummary(id, startTime, endTime, true,sameTime);
                            facebook = fetchOneAppDataSummary(id, startTime, endTime, false,sameTime);
                            total_impressions = admob.get("total_impressions").getAsDouble() + facebook.get("total_impressions").getAsDouble();
                            if (total_impressions == 0) {
                                continue;
                            }
                            total_spend = admob.get("total_spend").getAsDouble() + facebook.get("total_spend").getAsDouble();
                            total_installed = admob.get("total_installed").getAsDouble() + facebook.get("total_installed").getAsDouble();
                            total_click = admob.get("total_click").getAsDouble() + facebook.get("total_click").getAsDouble();
                        }else if(facebookCheck.equals("true")){
                            facebook = fetchOneAppDataSummary(id, startTime, endTime, false,sameTime);
                            total_impressions = facebook.get("total_impressions").getAsDouble();
                            if (total_impressions == 0) {
                                continue;
                            }
                            total_spend =  facebook.get("total_spend").getAsDouble();
                            total_installed =  facebook.get("total_installed").getAsDouble();
                            total_click =  facebook.get("total_click").getAsDouble();
                        }else if(adwordsCheck.equals("true")){
                            admob = fetchOneAppDataSummary(id, startTime, endTime, true,sameTime);
                            total_impressions = admob.get("total_impressions").getAsDouble();
                            if (total_impressions == 0) {
                                continue;
                            }
                            total_spend = admob.get("total_spend").getAsDouble();
                            total_installed = admob.get("total_installed").getAsDouble();
                            total_click = admob.get("total_click").getAsDouble();
                        }

                        double endTime_total_spend = 0;
                        if(sameTime){
                            endTime_total_spend = total_spend;
                        }else{
                            if(facebookCheck.equals("false") && adwordsCheck.equals("false")){
                                JsonObject admob1 = fetchOneAppDataSummary(id, endTime, endTime, true,true);
                                JsonObject facebook1 = fetchOneAppDataSummary(id, endTime, endTime, false,true);
                                endTime_total_spend = admob1.get("total_spend").getAsDouble() + facebook1.get("total_spend").getAsDouble();
                            }else if(facebookCheck.equals("true")){
                                JsonObject facebook1 = fetchOneAppDataSummary(id, endTime, endTime, false,true);
                                endTime_total_spend = facebook1.get("total_spend").getAsDouble();
                            }else if(adwordsCheck.equals("true")){
                                JsonObject admob1 = fetchOneAppDataSummary(id, endTime, endTime, true,true);
                                endTime_total_spend = admob1.get("total_spend").getAsDouble();
                            }
                        }

                        double total_ctr = total_impressions > 0 ? total_click / total_impressions : 0;
                        double total_cpa = total_installed > 0 ? total_spend / total_installed : 0;
                        double total_cvr = total_click > 0 ? total_installed / total_click : 0;

                        JsonObject j = new JsonObject();
                        j.addProperty("endTime_total_spend", NumberUtil.trimDouble(endTime_total_spend, 0));
                        j.addProperty("total_spend", NumberUtil.trimDouble(total_spend, 0));
                        j.addProperty("total_installed", total_installed);
                        j.addProperty("total_impressions", total_impressions);
                        j.addProperty("total_click", total_click);
                        j.addProperty("total_ctr", NumberUtil.trimDouble(total_ctr, 3));
                        j.addProperty("total_cpa", NumberUtil.trimDouble(total_cpa, 3));
                        j.addProperty("total_cvr", NumberUtil.trimDouble(total_cvr, 3));
                        j.addProperty("name", tagName);
                        String sql = "select warning_level from  web_app_logs where app_name = '" + tagName + "' and log_date = '" + endTime + "'";
                        JSObject one = DB.findOneBySql(sql);
                        if (one.hasObjectData()) {
                            int warningLevel = one.get("warning_level");
                            j.addProperty("warning_level", warningLevel);
                        }

                        //计算这个应用的总营收和最后一天的营收
                        double totalRevenue = 0;
                        double endTimeTotalRevenue = 0;
                        String googlePackageId = tagJSObject.get("google_package_id");
                        if (googlePackageId != null && googlePackageId != "") {
                            if(sameTime){
                                String sqlR = "select sum(revenue) as revenues " +
                                        "from web_ad_country_analysis_report_history where app_id = '"
                                        + googlePackageId + "' and date = '" + endTime + "'";
                                JSObject oneR = DB.findOneBySql(sqlR);
                                if (oneR.hasObjectData()) {
                                    endTimeTotalRevenue = NumberUtil.convertDouble(oneR.get("revenues"), 0);
                                    totalRevenue = endTimeTotalRevenue;
                                }
                            }else{
                                String sqlR = "select sum(revenue) as revenues " +
                                        "from web_ad_country_analysis_report_history where app_id = '"
                                        + googlePackageId + "' and date BETWEEN '" + startTime + "' AND '" + endTime + "'";
                                JSObject oneR = DB.findOneBySql(sqlR);
                                if (oneR.hasObjectData()) {
                                    totalRevenue = NumberUtil.convertDouble(oneR.get("revenues"), 0);
                                }
                                sqlR = "select sum(revenue) as revenues " +
                                        "from web_ad_country_analysis_report_history where app_id = '"
                                        + googlePackageId + "' and date = '" + endTime + "'";
                                oneR = DB.findOneBySql(sqlR);
                                if (oneR.hasObjectData()) {
                                    endTimeTotalRevenue = NumberUtil.convertDouble(oneR.get("revenues"), 0);
                                }
                            }

                        }
                        j.addProperty("endTime_total_revenue", NumberUtil.trimDouble(endTimeTotalRevenue, 0));
                        j.addProperty("total_revenue", NumberUtil.trimDouble(totalRevenue, 0));

                        //计算ECPM和incoming
                        double ecpm = totalRevenue * 1000 / total_impressions;
                        double incoming = totalRevenue - total_spend;
                        j.addProperty("ecpm",NumberUtil.trimDouble(ecpm,3));
                        j.addProperty("incoming",NumberUtil.trimDouble(incoming,0));

                        arr.add(j);
                    }
                    json.add("data", arr);
                }
            //json.add("data", arr);

            json.addProperty("ret", 1);
            json.addProperty("message", "执行成功");
        } catch (Exception ex) {
            ex.printStackTrace();
            json.addProperty("ret", 0);
            json.addProperty("message", ex.getMessage());
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }


        response.getWriter().write(json.toString());
    }


    //以下是在 startTime和endTime之间取一堆值初始化
    private JsonObject fetchOneAppDataSummary(long tagId, String startTime, String endTime, boolean admobCheck,boolean sameTime) throws Exception {
        String webAdCampaignsTable = "web_ad_campaigns";
        String webAdCampaignsHistoryTable = "web_ad_campaigns_history";
        if (admobCheck) {
            webAdCampaignsTable = "web_ad_campaigns_admob";
            webAdCampaignsHistoryTable = "web_ad_campaigns_history_admob";
        }
        String sql = "select sum(ch.total_spend) as spend, " +
                " sum(ch.total_installed) as installed, sum(ch.total_impressions) as impressions, " +
                " sum(ch.total_click) as click from " + webAdCampaignsHistoryTable + " ch, " + webAdCampaignsTable + " c " +
                " where c.campaign_id = ch.campaign_id and c.tag_id = " + tagId +
                " and date between '" + startTime + "' and '" + endTime + "'";
        if(sameTime){
            sql = "select sum(ch.total_spend) as spend, " +
                    " sum(ch.total_installed) as installed, sum(ch.total_impressions) as impressions, " +
                    " sum(ch.total_click) as click from " + webAdCampaignsHistoryTable + " ch, " + webAdCampaignsTable + " c " +
                    " where c.campaign_id = ch.campaign_id and c.tag_id = " + tagId +
                    " and date = '" + endTime + "'";
        }
        JSObject one = DB.findOneBySql(sql);

        JsonObject jsonObject = new JsonObject();
        double total_spend = NumberUtil.convertDouble(one.get("spend"), 0);
        double total_installed = NumberUtil.convertDouble(one.get("installed"), 0);
        double total_impressions = NumberUtil.convertDouble(one.get("impressions"), 0);
        double total_click = NumberUtil.convertDouble(one.get("click"), 0);

        double total_ctr = total_impressions > 0 ? total_click / total_impressions : 0;
        double total_cpa = total_installed > 0 ? total_spend / total_installed : 0;
        double total_cvr = total_click > 0 ? total_installed / total_click : 0;

        jsonObject.addProperty("total_spend", NumberUtil.trimDouble(total_spend, 0));
        jsonObject.addProperty("total_installed", total_installed);
        jsonObject.addProperty("total_impressions", total_impressions);
        jsonObject.addProperty("total_click", total_click);
        jsonObject.addProperty("total_ctr", NumberUtil.trimDouble(total_ctr, 3));
        jsonObject.addProperty("total_cpa", NumberUtil.trimDouble(total_cpa, 3));
        jsonObject.addProperty("total_cvr", NumberUtil.trimDouble(total_cvr, 3));
        return jsonObject;
    }


    //以下封装了用于首页数据排序的方法
    private void sorting (ArrayList<AppBean> appBeanList, int sorter){
        if (appBeanList != null && appBeanList.size() > 0) {
            switch (sorter) {
                case 70:
                    Collections.sort(appBeanList, new Comparator<AppBean>() {
                        @Override
                        public int compare(AppBean a, AppBean b) {
                            if (a.total_spend > b.total_spend) {
                                return 1;
                            } else if (a.total_spend < b.total_spend) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    });
                    break;
                case 1070:
                    Collections.sort(appBeanList, new Comparator<AppBean>() {
                        @Override
                        public int compare(AppBean a, AppBean b) {
                            if (a.total_spend < b.total_spend) {
                                return 1;
                            } else if (a.total_spend > b.total_spend) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    });
                    break;
                case 71:
                    Collections.sort(appBeanList, new Comparator<AppBean>() {
                        @Override
                        public int compare(AppBean a, AppBean b) {
                            if (a.seven_days_total_spend > b.seven_days_total_spend) {
                                return 1;
                            } else if (a.seven_days_total_spend < b.seven_days_total_spend) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    });
                    break;
                case 1071:
                    Collections.sort(appBeanList, new Comparator<AppBean>() {
                        @Override
                        public int compare(AppBean a, AppBean b) {
                            if (a.seven_days_total_spend < b.seven_days_total_spend) {
                                return 1;
                            } else if (a.seven_days_total_spend > b.seven_days_total_spend) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    });
                    break;
                case 72:
                    Collections.sort(appBeanList, new Comparator<AppBean>() {
                        @Override
                        public int compare(AppBean a, AppBean b) {
                            if (a.total_revenue > b.total_revenue) {
                                return 1;
                            } else if (a.total_revenue < b.total_revenue) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    });
                    break;
                case 1072:
                    Collections.sort(appBeanList, new Comparator<AppBean>() {
                        @Override
                        public int compare(AppBean a, AppBean b) {
                            if (a.total_revenue < b.total_revenue) {
                                return 1;
                            } else if (a.total_revenue > b.total_revenue) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    });
                    break;
                case 73:
                    Collections.sort(appBeanList, new Comparator<AppBean>() {
                        @Override
                        public int compare(AppBean a, AppBean b) {
                            if (a.seven_days_total_revenue > b.seven_days_total_revenue) {
                                return 1;
                            } else if (a.seven_days_total_revenue < b.seven_days_total_revenue) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    });
                    break;
                case 1073:
                    Collections.sort(appBeanList, new Comparator<AppBean>() {
                        @Override
                        public int compare(AppBean a, AppBean b) {
                            if (a.seven_days_total_revenue < b.seven_days_total_revenue) {
                                return 1;
                            } else if (a.seven_days_total_revenue > b.seven_days_total_revenue) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    });
                    break;
                case 74:
                    Collections.sort(appBeanList, new Comparator<AppBean>() {
                        @Override
                        public int compare(AppBean a, AppBean b) {
                            if (a.total_installed > b.total_installed) {
                                return 1;
                            } else if (a.total_installed < b.total_installed) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    });
                    break;
                case 1074:
                    Collections.sort(appBeanList, new Comparator<AppBean>() {
                        @Override
                        public int compare(AppBean a, AppBean b) {
                            if (a.total_installed < b.total_installed) {
                                return 1;
                            } else if (a.total_installed > b.total_installed) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    });
                    break;
                case 75:
                    Collections.sort(appBeanList, new Comparator<AppBean>() {
                        @Override
                        public int compare(AppBean a, AppBean b) {
                            if (a.total_impressions > b.total_impressions) {
                                return 1;
                            } else if (a.total_impressions < b.total_impressions) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    });
                    break;
                case 1075:
                    Collections.sort(appBeanList, new Comparator<AppBean>() {
                        @Override
                        public int compare(AppBean a, AppBean b) {
                            if (a.total_impressions < b.total_impressions) {
                                return 1;
                            } else if (a.total_impressions > b.total_impressions) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    });
                    break;
                case 76:
                    Collections.sort(appBeanList, new Comparator<AppBean>() {
                        @Override
                        public int compare(AppBean a, AppBean b) {
                            if (a.total_click > b.total_click) {
                                return 1;
                            } else if (a.total_click < b.total_click) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    });
                    break;
                case 1076:
                    Collections.sort(appBeanList, new Comparator<AppBean>() {
                        @Override
                        public int compare(AppBean a, AppBean b) {
                            if (a.total_click < b.total_click) {
                                return 1;
                            } else if (a.total_click > b.total_click) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    });
                    break;
                case 77:
                    Collections.sort(appBeanList, new Comparator<AppBean>() {
                        @Override
                        public int compare(AppBean a, AppBean b) {
                            if (a.total_ctr > b.total_ctr) {
                                return 1;
                            } else if (a.total_ctr < b.total_ctr) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    });
                    break;
                case 1077:
                    Collections.sort(appBeanList, new Comparator<AppBean>() {
                        @Override
                        public int compare(AppBean a, AppBean b) {
                            if (a.total_ctr < b.total_ctr) {
                                return 1;
                            } else if (a.total_ctr > b.total_ctr) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    });
                    break;
                case 78:
                    Collections.sort(appBeanList, new Comparator<AppBean>() {
                        @Override
                        public int compare(AppBean a, AppBean b) {
                            if (a.total_cpa > b.total_cpa) {
                                return 1;
                            } else if (a.total_cpa < b.total_cpa) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    });
                    break;
                case 1078:
                    Collections.sort(appBeanList, new Comparator<AppBean>() {
                        @Override
                        public int compare(AppBean a, AppBean b) {
                            if (a.total_cpa < b.total_cpa) {
                                return 1;
                            } else if (a.total_cpa > b.total_cpa) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    });
                    break;
                case 79:
                    Collections.sort(appBeanList, new Comparator<AppBean>() {
                        @Override
                        public int compare(AppBean a, AppBean b) {
                            if (a.total_cvr > b.total_cvr) {
                                return 1;
                            } else if (a.total_cvr < b.total_cvr) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    });
                    break;
                case 1079:
                    Collections.sort(appBeanList, new Comparator<AppBean>() {
                        @Override
                        public int compare(AppBean a, AppBean b) {
                            if (a.total_cvr < b.total_cvr) {
                                return 1;
                            } else if (a.total_cvr > b.total_cvr) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    });
                    break;
                case 80:
                    Collections.sort(appBeanList, new Comparator<AppBean>() {
                        @Override
                        public int compare(AppBean a, AppBean b) {
                            if (a.ecpm > b.ecpm) {
                                return 1;
                            } else if (a.ecpm < b.ecpm) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    });
                    break;
                case 1080:
                    Collections.sort(appBeanList, new Comparator<AppBean>() {
                        @Override
                        public int compare(AppBean a, AppBean b) {
                            if (a.ecpm < b.ecpm) {
                                return 1;
                            } else if (a.ecpm > b.ecpm) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    });
                    break;
                case 81:
                    Collections.sort(appBeanList, new Comparator<AppBean>() {
                        @Override
                        public int compare(AppBean a, AppBean b) {
                            if (a.incoming > b.incoming) {
                                return 1;
                            } else if (a.incoming < b.incoming) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    });
                    break;
                case 1081:
                    Collections.sort(appBeanList, new Comparator<AppBean>() {
                        @Override
                        public int compare(AppBean a, AppBean b) {
                            if (a.incoming < b.incoming) {
                                return 1;
                            } else if (a.incoming > b.incoming) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    });
                    break;
            }
        }
    }
}
