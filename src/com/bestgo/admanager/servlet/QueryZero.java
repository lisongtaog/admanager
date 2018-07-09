package com.bestgo.admanager.servlet;

import com.bestgo.admanager.utils.DateUtil;
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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@WebServlet(name = "QueryZero", urlPatterns = {"/query_zero/*"}, asyncSupported = true)
public class QueryZero extends HttpServlet {
    private static ExecutorService executors = Executors.newFixedThreadPool(1);

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Utils.isAdmin(request, response)) return;

        String path = request.getPathInfo();
        JsonObject json = new JsonObject();

        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        String costOp = request.getParameter("costOp");
        String conversionOp = request.getParameter("conversionOp");
        String cost = request.getParameter("cost");
        String conversion = request.getParameter("conversion");

        if (path.startsWith("/query")) {
            try {
                JsonObject jsonObject = fetchAppData(startTime, endTime, costOp, cost, conversionOp, conversion);
                json.add("data", jsonObject);

                json.addProperty("ret", 1);
                json.addProperty("message", "执行成功");
            } catch (Exception ex) {
                json.addProperty("ret", 0);
                json.addProperty("message", ex.getMessage());
                Logger logger = Logger.getRootLogger();
                logger.error(ex.getMessage(), ex);
            }
        } else if (path.startsWith("/close")) {
            try {
                executors.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            closeCampaigns(startTime, endTime, costOp, cost, conversionOp, conversion);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                json.addProperty("ret", 1);
                json.addProperty("message", "执行成功");
            } catch (Exception ex) {
                json.addProperty("ret", 0);
                json.addProperty("message", ex.getMessage());
                Logger logger = Logger.getRootLogger();
                logger.error(ex.getMessage(), ex);
            }
        }

        response.getWriter().write(json.toString());
    }

    private boolean closeCampaigns(String startTime, String endTime,
                                    String costOp, String cost, String conversionOp, String conversion) throws Exception {
        String[] adNetwork = {"admob", "facebook"};
        double dCost = NumberUtil.parseDouble(cost, 0);

        for (String network : adNetwork) {
            String op = "1".equals(conversionOp) ? ">=" : "<=";

            String sql = "select history.campaign_id, account_id, campaign_name, create_time, status, ad.budget, ad.bidding, \n" +
                    "history.total_spend, history.total_installed, history.total_click, history.total_impressions\n" +
                    "from web_ad_campaigns_history history, web_ad_campaigns ad\n" +
                    "where date between ? and ? and history.campaign_id=ad.campaign_id and status='ACTIVE'\n" +
                    "and history.total_installed " + op + " ?";
            if ("admob".equals(network)) {
                sql = "select history.campaign_id, account_id, campaign_name, create_time, status, ad.budget, ad.bidding, \n" +
                        "history.total_spend, history.total_installed, history.total_click, history.total_impressions\n" +
                        "from web_ad_campaigns_history_admob history, web_ad_campaigns_admob ad\n" +
                        "where date between ? and ? and history.campaign_id=ad.campaign_id and status='enabled'\n" +
                        "and history.total_installed " + op + " ?";
            }

            List<JSObject> list = DB.findListBySql(sql, startTime, endTime, NumberUtil.parseInt(conversion, 0));

            for (int i = 0; i < list.size(); i++) {
                JSObject one = list.get(i);
                if(one != null && one.hasObjectData()){
                    String campaign_id = one.get("campaign_id");
                    String account_id = one.get("account_id");
                    double budget = one.get("budget");
                    double spend = NumberUtil.convertDouble(one.get("total_spend"), 0);

                    if ("1".equals(costOp)) {
                        if (spend * 100 / budget < dCost) {
                            continue;
                        }
                    } else {
                        if (spend * 100 / budget > dCost) {
                            continue;
                        }
                    }

                    JSObject record = DB.simpleScan("web_ad_batch_change_campaigns")
                            .select("id").where(DB.filter().whereEqualTo("campaign_id", campaign_id))
                            .and(DB.filter().whereEqualTo("success", 0)).execute();
                    int enabled = 0;
                    if (record.hasObjectData()) {
                        long id = record.get("id");
                        DB.update("web_ad_batch_change_campaigns")
                                .put("enabled", enabled)
                                .put("success", 0)
                                .where(DB.filter().whereEqualTo("id", id))
                                .execute();
                    } else {
                        String now = DateUtil.getNowTime();
                        DB.insert("web_ad_batch_change_campaigns")
                                .put("enabled", enabled)
                                .put("bugdet", 0)
                                .put("bidding", 0)
                                .put("network", network)
                                .put("account_id", account_id)
                                .put("campaign_id", campaign_id)
                                .put("campaign_name", "")
                                .put("excluded_country", "")
                                .put("create_time", now)
                                .put("success", 0)
                                .execute();
                    }
                }

            }
        }
        return true;
    }

    private JsonObject fetchAppData(String startTime, String endTime,
                                       String costOp, String cost, String conversionOp, String conversion) throws Exception {
        String[] adNetwork = {"admob", "facebook"};
        JsonObject jsonObject = new JsonObject();
        JsonArray array = new JsonArray();
        double total_bugdet = 0;
        double total_spend = 0;
        double total_installed = 0;
        double total_impressions = 0;
        double total_click = 0;
        double total_ctr = 0;
        double total_cpa = 0;
        double total_cvr = 0;
        double dCost = NumberUtil.parseDouble(cost, 0);

        for (String network : adNetwork) {
            String op = "1".equals(conversionOp) ? ">=" : "<=";

            String sql = "select history.campaign_id, account_id, campaign_name, create_time, status, ad.budget, ad.bidding, \n" +
                    "history.total_spend, history.total_installed, history.total_click, history.total_impressions\n" +
                    "from web_ad_campaigns_history history, web_ad_campaigns ad\n" +
                    "where date between ? and ? and history.campaign_id=ad.campaign_id and status='ACTIVE'\n" +
                    "and history.total_installed " + op + " ?";
            if (network.equals("admob")) {
                sql = "select history.campaign_id, account_id, campaign_name, create_time, status, ad.budget, ad.bidding, \n" +
                        "history.total_spend, history.total_installed, history.total_click, history.total_impressions\n" +
                        "from web_ad_campaigns_history_admob history, web_ad_campaigns_admob ad\n" +
                        "where date between ? and ? and history.campaign_id=ad.campaign_id and status='enabled'\n" +
                        "and history.total_installed " + op + " ?";
            }

            List<JSObject> list = DB.findListBySql(sql, startTime, endTime, NumberUtil.parseInt(conversion, 0));

            for (int i = 0; i < list.size(); i++) {
                JSObject one = list.get(i);
                String campaign_id = one.get("campaign_id");
                String account_id = one.get("account_id");
                String campaign_name = one.get("campaign_name");
                String status = one.get("status");
                String create_time = one.get("create_time").toString();
                create_time = create_time.substring(0, create_time.length() - 5);
                double budget = one.get("budget");
                double bidding = one.get("bidding");
                double spend = NumberUtil.convertDouble(one.get("total_spend"), 0);
                double installed = NumberUtil.convertDouble(one.get("total_installed"), 0);
                double impressions = NumberUtil.convertDouble(one.get("total_impressions"), 0);
                double click = NumberUtil.convertDouble(one.get("total_click"), 0);
                double ctr = impressions > 0 ? click / impressions : 0;
                double cpa = installed > 0 ? spend / installed : 0;
                double cvr = click > 0 ? installed / click : 0;

                if ("1".equals(costOp)) {
                    if (spend * 100 / budget < dCost) {
                        continue;
                    }
                } else {
                    if (spend * 100 / budget > dCost) {
                        continue;
                    }
                }

                total_bugdet += budget;
                total_spend += spend;
                total_installed += installed;
                total_impressions += impressions;
                total_click += click;
                total_ctr = total_impressions > 0 ? total_click / total_impressions : 0;
                total_cpa = total_installed > 0 ? total_spend / total_installed : 0;
                total_cvr = total_click > 0 ? total_installed / total_click : 0;

                JsonObject d = new JsonObject();
                d.addProperty("campaign_id", campaign_id);
                d.addProperty("account_id", account_id);
                d.addProperty("campaign_name", campaign_name);
                d.addProperty("status", status);
                d.addProperty("create_time", create_time);
                d.addProperty("budget", budget);
                d.addProperty("bidding", bidding);
                d.addProperty("impressions", impressions);
                d.addProperty("spend", spend);
                d.addProperty("installed", installed);
                d.addProperty("click", click);
                d.addProperty("ctr", NumberUtil.trimDouble(ctr,4));
                d.addProperty("cpa", NumberUtil.trimDouble(cpa,4));
                d.addProperty("cvr", NumberUtil.trimDouble(cvr,4));
                if (network.equals("admob")) {
                    d.addProperty("network", "admob");
                } else {
                    d.addProperty("network", "facebook");
                }
                array.add(d);
            }
        }
        jsonObject.add("array", new JsonArray());
        jsonObject.addProperty("total_bugdet", total_bugdet / 100);
        jsonObject.addProperty("total_cost_rate", total_spend / total_bugdet / 100);
        jsonObject.addProperty("total_spend", total_spend);
        jsonObject.addProperty("total_installed", total_installed);
        jsonObject.addProperty("total_impressions", total_impressions);
        jsonObject.addProperty("total_click", total_click);
        jsonObject.addProperty("total_ctr", NumberUtil.trimDouble(total_ctr,4));
        jsonObject.addProperty("total_cpa", NumberUtil.trimDouble(total_cpa,4));
        jsonObject.addProperty("total_cvr", NumberUtil.trimDouble(total_cvr,4));
        return jsonObject;
    }
}
