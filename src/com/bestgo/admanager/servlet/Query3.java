package com.bestgo.admanager.servlet;

import com.bestgo.admanager.utils.Utils;
import com.bestgo.admanager.bean.AppBean;
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
@WebServlet(name = "Query3", urlPatterns = {"/query3"}, asyncSupported = true)
public class Query3 extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String[] fourteen_arr = null;
        if (!Utils.isAdmin(request, response)) return;
        JsonObject json = new JsonObject();
        JsonArray arr = new JsonArray();
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
        String pageNow = request.getParameter("pageNow");
        String btnSummaryFlag = request.getParameter("btnSummary");
        boolean btnSummary=false;
        if(btnSummaryFlag != null && btnSummaryFlag.equals("true")){
            btnSummary = true;
        }
        int sorter = 0;
        if (sorterId != null) {
            sorter = Utils.parseInt(sorterId, 0);
        }
        try {
            //应用的个数
            long count = getAppCount();

            if(count > 0){
                int pageIndex = 0;
                if(pageNow != "" && pageNow != null){
                    int pageNowInt = Integer.parseInt(pageNow);
                    pageIndex = pageNowInt - 1;
                }
                int pageSize = 10;
                long totalPage = count / pageSize + (count % pageSize == 0 ? 0 : 1);
                arr = fetchAllDataWithPage(facebookCheck,adwordsCheck,pageIndex,pageSize,sorter,startTime,endTime,sameTime,false);
                if(btnSummary){
                    JsonArray arr_result = new JsonArray();
                    arr_result = fetchAllDataWithPage(facebookCheck,adwordsCheck,pageIndex,pageSize,sorter,startTime,endTime,sameTime,btnSummary);
                    json.add("total_result",arr_result);
                    json.addProperty("ret", 1);
                }else{
                    json.addProperty("ret", 2);
                }
                json.add("arr", arr);
                json.addProperty("total_page",totalPage);
                json.addProperty("pageNow",pageIndex+1);
                json.addProperty("message", "执行成功");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            json.addProperty("ret", 0);
            json.addProperty("message", ex.getMessage());
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        response.getWriter().write(json.toString());
    }


    /**
     * 分页查询多个应用的数据
     * @param facebookCheck 如果等于字符串"true"，则代表只查询Facebook
     * @param adwordsCheck 如果等于字符串"true"，则代表只查询Adwords
     * @param pageIndex
     * @param pageSize
     * @param sorter
     * @param startTime
     * @param endTime
     * @param sameTime
     * @return
     * @throws Exception
     */
    private JsonArray fetchAllDataWithPage(String facebookCheck,String adwordsCheck,int pageIndex, int pageSize,int sorter,String startTime,String endTime,boolean sameTime,boolean btnSummary) throws Exception {
        JsonArray arr = new JsonArray();
        ArrayList<AppBean> appBeanList = new ArrayList<>();
        String sql = "";
        if (btnSummary) {
            sql = "SELECT t.id,t.tag_name,google_package_id from web_tag t LEFT JOIN web_facebook_app_ids_rel air ON t.tag_name = air.tag_name " +
                    "ORDER BY t.tag_name";
        } else {
            sql = "SELECT t.id,t.tag_name,google_package_id from web_tag t LEFT JOIN web_facebook_app_ids_rel air ON t.tag_name = air.tag_name " +
                    "ORDER BY t.tag_name LIMIT " + pageIndex * pageSize + "," + pageSize;
        }
        List<JSObject> tagList = DB.findListBySql(sql);
        for (JSObject tagJSObject : tagList) {
            AppBean appBean = new AppBean();
            long id = tagJSObject.get("id");
            appBean.name = tagJSObject.get("tag_name");

            if ("false".equals(adwordsCheck) && "false".equals(facebookCheck)) { //Facebook和Adwords全查询
                JsonObject admob = fetchOneAppDataSummaryByAdmobCheck(id, startTime, endTime, true, sameTime);
                JsonObject facebook = fetchOneAppDataSummaryByAdmobCheck(id, startTime, endTime, false, sameTime);
                appBean.total_impressions = admob.get("total_impressions").getAsDouble() + facebook.get("total_impressions").getAsDouble();
                if (appBean.total_impressions == 0) {
                    continue;
                }
                appBean.total_spend = admob.get("total_spend").getAsDouble() + facebook.get("total_spend").getAsDouble();
                if (sameTime) {
                    appBean.end_time_total_spend = appBean.total_spend;
                } else {
                    JsonObject admob1 = fetchOneAppDataSummaryByAdmobCheck(id, endTime, endTime, true, true);
                    JsonObject facebook1 = fetchOneAppDataSummaryByAdmobCheck(id, endTime, endTime, false, true);
                    appBean.end_time_total_spend = Utils.trimDouble(admob1.get("total_spend").getAsDouble() + facebook1.get("total_spend").getAsDouble(), 0);
                }

                appBean.total_installed = admob.get("total_installed").getAsDouble() + facebook.get("total_installed").getAsDouble();
                appBean.total_click = admob.get("total_click").getAsDouble() + facebook.get("total_click").getAsDouble();

            } else if ("true".equals(adwordsCheck)) {//只选中Adwords
                JsonObject admob = fetchOneAppDataSummaryByAdmobCheck(id, startTime, endTime, true, sameTime);
                appBean.total_impressions = admob.get("total_impressions").getAsDouble();
                if (appBean.total_impressions == 0) {
                    continue;
                }
                appBean.total_spend = admob.get("total_spend").getAsDouble();
                if (sameTime) {
                    appBean.end_time_total_spend = appBean.total_spend;
                } else {
                    JsonObject admob1 = fetchOneAppDataSummaryByAdmobCheck(id, endTime, endTime, true, true);
                    appBean.end_time_total_spend = Utils.trimDouble(admob1.get("total_spend").getAsDouble(), 0);
                }

                appBean.total_installed = admob.get("total_installed").getAsDouble();
                appBean.total_click = admob.get("total_click").getAsDouble();

            } else { //只选中Facebook
                JsonObject facebook = fetchOneAppDataSummaryByAdmobCheck(id, startTime, endTime, false, sameTime);
                appBean.total_impressions = facebook.get("total_impressions").getAsDouble();
                if (appBean.total_impressions == 0) {
                    continue;
                }
                appBean.total_spend = facebook.get("total_spend").getAsDouble();
                if (sameTime) {
                    appBean.end_time_total_spend = appBean.total_spend;
                } else {
                    JsonObject facebook1 = fetchOneAppDataSummaryByAdmobCheck(id, endTime, endTime, false, true);
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
                if (sameTime) {
                    String sqlR = "select sum(revenue) as revenues " +
                            "from web_ad_country_analysis_report_history where app_id = '"
                            + google_package_id + "' and date = '" + endTime + "'";
                    JSObject oneR = DB.findOneBySql(sqlR);
                    if (oneR.hasObjectData()) {
                        appBean.total_revenue = Utils.convertDouble(oneR.get("revenues"), 0);
                        appBean.end_time_total_revenue = appBean.total_revenue;
                    }
                } else {
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

                //14行悬浮窗：用一个静态方法FourteenData 来生成一个用于返回的数组 fourteen_arr,
//                            fourteen_arr = FourteenData(id, google_package_id, endTime);

                //计算ECPM和Incoming
                appBean.ecpm = appBean.total_revenue * 1000 / appBean.total_impressions;
                appBean.incoming = appBean.total_revenue - appBean.total_spend;
            }
            appBeanList.add(appBean);
        }

        if (appBeanList != null && appBeanList.size() > 0) {
            //对应用进行排序
            if (sorter > 0) {
                sorting(appBeanList, sorter);
            }
            //判断是否是直接点击 btnSummary键进行的查询
            if (btnSummary) {
                double total_spend_result = 0;
                double total_revenue_result = 0;
                double total_installed_result = 0;
                double total_impression_result = 0;
                double total_click_result = 0;
                double total_incoming_result = 0;
                double total_ctr_result = 0;
                double total_cpa_result = 0;
                double total_cvr_result = 0;
                for (AppBean cs : appBeanList) {
                    total_spend_result += Utils.trimDouble(cs.total_spend, 0);
                    total_revenue_result += Utils.trimDouble(cs.total_revenue, 0);
                    total_installed_result += cs.total_installed;
                    total_impression_result += cs.total_impressions;
                    total_click_result += cs.total_click;
                    total_incoming_result += Utils.trimDouble(cs.incoming, 0);
                }
                total_ctr_result = total_impression_result>0 ? total_click_result/total_impression_result : 0;
                total_cpa_result = total_installed_result>0 ? total_spend_result/total_installed_result : 0;
                total_cvr_result = total_click_result>0 ? total_installed_result/total_click_result : 0;

                JsonObject j = new JsonObject();
                j.addProperty("total_spend_result", total_spend_result);
                j.addProperty("total_revenue_result", total_revenue_result);
                j.addProperty("total_installed_result", total_installed_result);
                j.addProperty("total_impression_result", total_impression_result);
                j.addProperty("total_click_result", total_click_result);
                j.addProperty("total_incoming_result", total_incoming_result);
                j.addProperty("total_ctr_result", total_ctr_result);
                j.addProperty("total_cpa_result", total_cpa_result);
                j.addProperty("total_cvr_result", total_cvr_result);
                arr.add(j);
            } else {
                for (AppBean cs : appBeanList) {
                    JsonObject j = new JsonObject();
                    j.addProperty("name", cs.name);
                    sql = "select warning_level from  web_app_logs where app_name = '" + cs.name + "' and log_date = '" + endTime + "'";
                    JSObject one = DB.findOneBySql(sql);
                    if (one.hasObjectData()) {
                        int warningLevel = one.get("warning_level");             //指标warning_level 仅仅与endTime有关
                        j.addProperty("warning_level", warningLevel);
                    }
                    j.addProperty("total_spend", Utils.trimDouble(cs.total_spend, 0));
                    j.addProperty("endTime_total_spend", Utils.trimDouble(cs.end_time_total_spend, 0));
                    j.addProperty("endTime_total_revenue", Utils.trimDouble(cs.end_time_total_revenue, 0));
                    j.addProperty("total_installed", cs.total_installed);
                    j.addProperty("total_impressions", cs.total_impressions);
                    j.addProperty("total_click", cs.total_click);
                    j.addProperty("total_ctr", Utils.trimDouble(cs.total_ctr, 3));
                    j.addProperty("total_cpa", Utils.trimDouble(cs.total_cpa, 3));
                    j.addProperty("total_cvr", Utils.trimDouble(cs.total_cvr, 3));
                    j.addProperty("total_revenue", Utils.trimDouble(cs.total_revenue, 0));
                    j.addProperty("ecpm", Utils.trimDouble(cs.ecpm, 3));
                    j.addProperty("incoming", Utils.trimDouble(cs.incoming, 0));


                    //在 数组arr 里添加 fourteen系列键值对，用于传回jsp生成悬浮窗
//                            j.addProperty("spend_14", fourteen_arr[0]);
//                            j.addProperty("installed_14", fourteen_arr[1]);
//                            j.addProperty("cpa_14", fourteen_arr[2]);
//                            j.addProperty("cvr_14", fourteen_arr[3]);
//                            j.addProperty("revenue_14", fourteen_arr[4]);

                    arr.add(j);
                }
            }
        }
        return arr;
    }

    /**
     * 获取所有应用的个数
     * @return
     * @throws Exception
     */
    private long getAppCount() throws Exception {
        String sql = "SELECT COUNT(id) AS record_count FROM web_tag";
        JSObject one = DB.findOneBySql(sql);
        if(one.hasObjectData()){
            return one.get("record_count");
        }
        return 0L;
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


    //设计一个静态方法用于初始化数组 fourteen_arr,该数组存储14天数据
//    public static String[] FourteenData(long id, String end, String google_package_id) throws Exception {
//        //存储14天浮窗数据：形式为字符串
//        Query4 query = new Query4();
//        List<JsonObject> admob_14 = query.AttrTitleData(id, end, true);
//        List<JsonObject> facebook_14 = query.AttrTitleData(id, end, false);
//        List<JsonObject> revenue_14 = query.AttrTitleData_revenue(google_package_id, end);
//        FourteenDays FourteenList = new FourteenDays();
//        String[] fourteen_arr = new String[5];
//
//        for (int j = 0; j < 14; j++) {
//            JsonObject a = admob_14.get(j);
//            JsonObject f = facebook_14.get(j);
//            JsonObject r = revenue_14.get(j);
//            double spend = Utils.trimDouble(a.get("one_day_spend").getAsDouble() + f.get("one_day_spend").getAsDouble(), 0);
//            double installed = Utils.trimDouble(a.get("one_day_installed").getAsDouble() + f.get("one_day_spend").getAsDouble(), 0);
//            double click = a.get("one_day_click").getAsDouble() + f.get("one_day_click").getAsDouble();
//            double cpa_rough = installed > 0 ? spend / installed : 0;
//            double cpa = Utils.trimDouble(cpa_rough, 3);
//            double cvr_rough = click > 0 ? installed / click : 0;
//            double cvr = Utils.trimDouble(cvr_rough, 3);
//            double revenue = Utils.trimDouble(r.get("revenue").getAsDouble(),0);   //如这类，取到空值时会报异常
//            //以下开始拼接用于悬浮显示的字符串
//            FourteenList.one_day_spend_for_fourteen_days += spend + "\n";
//            FourteenList.one_day_installed_for_fourteen_days += installed + "\n";
//            FourteenList.one_day_cpa_for_fourteen_days += cpa + "\n";
//            FourteenList.one_day_cvr_for_fourteen_days += cvr + "\n";
//            FourteenList.one_day_revenue_for_fourteen_days += revenue + "\n";
//        }
//        fourteen_arr[0] = FourteenList.one_day_spend_for_fourteen_days;
//        fourteen_arr[1] = FourteenList.one_day_installed_for_fourteen_days;
//        fourteen_arr[2] = FourteenList.one_day_cpa_for_fourteen_days;
//        fourteen_arr[3] = FourteenList.one_day_cvr_for_fourteen_days;
//        fourteen_arr[4] = FourteenList.one_day_revenue_for_fourteen_days;
//        return fourteen_arr;
//    }


    //14天数据的总方法（设置在title属性里）
//    private List<JsonObject> AttrTitleData(long tagId, String endTime, boolean admobCheck) throws Exception {
//        String webAdCampaignTagRelTable = "web_ad_campaign_tag_rel";
//        String webAdCampaignsTable = "web_ad_campaigns";
//        String webAdCampaignsHistoryTable = "web_ad_campaigns_history";
//        if (admobCheck) {
//            webAdCampaignTagRelTable = "web_ad_campaign_tag_admob_rel";
//            webAdCampaignsTable = "web_ad_campaigns_admob";
//            webAdCampaignsHistoryTable = "web_ad_campaigns_history_admob";
//        }
//        List<JsonObject> title = new ArrayList<>();
//        for (int i = 0; i < 14; i++) {
//            //DayCount : 每循环一次得到相比上次的前一天
//            JsonObject jsonObject = new JsonObject(); //在static context 里实例化非静态类，需要通过对象进行实例化
//            String DayCount = DateUtil.addDay(endTime, -i, "yyyy-MM-dd");
//            String sql = "select sum(ch.total_spend) as spend, " +
//                    "sum(ch.total_installed) as installed, sum(ch.total_impressions) as impressions " +
//                    ",sum(ch.total_click) as click from " + webAdCampaignsTable + " c, " + webAdCampaignsHistoryTable + " ch, " +
//                    "(select distinct campaign_id from " + webAdCampaignTagRelTable + " where tag_id = " + tagId + ") rt " +
//                    "where rt.campaign_id = ch.campaign_id and c.campaign_id = ch.campaign_id " +
//                    "and date = '" + DayCount + "'";
//            JSObject one = DB.findOneBySql(sql);
//            double total_installed = Utils.convertDouble(one.get("installed"), 0);
//            double total_spend = Utils.convertDouble(one.get("spend"), 0);
//            double total_click = Utils.convertDouble(one.get("click"), 0);
//            double total_cpa = total_installed > 0 ? total_spend / total_installed : 0;
//            double total_cvr = total_click > 0 ? total_installed / total_click : 0;
//
//            jsonObject.addProperty("one_day_spend", total_spend);
//            jsonObject.addProperty("one_day_installed", total_installed);
//            jsonObject.addProperty("one_day_click", total_click);
//            jsonObject.addProperty("one_day_cpa", Utils.trimDouble(total_cpa, 3));
//            jsonObject.addProperty("one_day_cvr", Utils.trimDouble(total_cvr, 3));
//            title.add(jsonObject);
//        }
//        return title;
//    }

    //以下是取14天的revenue
//    private List<JsonObject> AttrTitleData_revenue(String google_package_id, String endTime) {
//
//        List<JsonObject> revenue = new ArrayList<>();
//        for (int i = 0; i < 14; i++) {
//            JsonObject jsonObject = new JsonObject();
//            String DayCount = DateUtil.addDay(endTime, -i, "yyyy-MM-dd");
//            String sqlR = "select sum(revenue) as revenues " +
//                    "from web_ad_country_analysis_report_history where app_id = '"
//                    + google_package_id + "' and date = '" + DayCount + "'";
//            //           JSObject one = DB.findOneBySql(sqlR); //这么写的时候，由于方法findOneBySql()声明过异常，则调用该方法的时候要截住异常。
//
//            JSObject one= null;
//            Double revenues=0.0;
//
//            /*
//             * 被try/catch对捕捉到的异常不会在控制台里报错
//             * 被catch的异常可以方便断点调试，比如在控制台里观察出错变量的信息
//             */
//            try {
//                one = DB.findOneBySql(sqlR);
//                revenues = one.get("revenues");
//            } catch (Exception e) {}
//
//            jsonObject.addProperty("revenue", Utils.trimDouble(revenues,0));
//            revenue.add(jsonObject);   //List是一个接口而不是实际类，接口内只有方法。其对象的实例化需要调用方法。
//        }
//        return revenue;
//    }


    /**
     * 对应用数据进行排序
     * @param appBeanList
     * @param sorter
     */
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

//    static class FourteenDays {
//        public String one_day_revenue_for_fourteen_days = "";
//        public String one_day_spend_for_fourteen_days = "";
//        public String one_day_installed_for_fourteen_days = "";
//        public String one_day_cpa_for_fourteen_days = "";
//        public String one_day_cvr_for_fourteen_days = "";
//    }
}
