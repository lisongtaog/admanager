package com.bestgo.admanager;

import com.facebook.ads.sdk.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.AsyncContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jikai on 5/16/17.
 */
public class TaskManager {
    public static ExecutorService DOWNLOAD_EXECUTOR = (ExecutorService) Executors.newFixedThreadPool(5);

//    static {
//        Config.setProxy();
//    }

    public static void runTask(AsyncContext context, String token) {
        DOWNLOAD_EXECUTOR.submit(new Runnable() {
            @Override
            public void run() {
                APIContext apiContext = new APIContext(token, Config.APP_SECRET);
                ServletRequest request = context.getRequest();
                String method = request.getParameter("method");
                if (method.equals("get_accounts")) {
                    ServletResponse response = context.getResponse();
                    ArrayList<String> accountFileds = new ArrayList<>();
                    accountFileds.add("account_id");
                    accountFileds.add("account_status");
                    accountFileds.add("balance");
                    accountFileds.add("name");
                    User user = new User("me", apiContext);
                    try {
                        APINodeList<AdAccount> accounts = user.getAdAccounts().requestFields(accountFileds).execute();
                        (response).setCharacterEncoding("utf-8");
                        response.setContentType("application/json");
                        response.getWriter().write(accounts.toString());
                    } catch (APIException e) {
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                    } finally {
                        try {
                            response.getWriter().write("");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        context.complete();
                    }
                } else if (method.equals("get_campaigns")) {
                    String accountId = request.getParameter("account_id");
                    JsonArray jsonArray = new JsonArray();
                    ServletResponse response = context.getResponse();

                    try {
                        AdAccount account = new AdAccount(accountId, apiContext);
                        APINodeList<Campaign> campaigns = account.getCampaigns().requestFields(Arrays.asList("id", "name")).execute();
                        while (campaigns != null) {
                            for (Campaign campaign : campaigns) {
                                APINodeList<AdsInsights> insightses = campaign.getInsights().requestFields(Arrays.asList("reach", "clicks",
                                        "cpc", "cpm", "cpp", "ctr", "frequency", "impressions", "spend", "cost_per_action_type",
                                        "actions", "action_values")).setDatePreset(AdsInsights.EnumDatePreset.VALUE_LAST_7D).execute();
                                System.out.println(insightses);
                                if (insightses.size() > 0) {
                                    AdsInsights insights = insightses.get(0);
                                    List<AdsActionStats> list = insights.getFieldCostPerActionType();
                                    String costPreResult = "";
                                    String result = "";
                                    if (list != null) {
                                        for (AdsActionStats stats : list) {
                                            if (stats.getFieldActionType().equals("app_custom_event.fb_mobile_activate_app")) {
                                                costPreResult = stats.getFieldValue();
                                            }
                                        }
                                    }
                                    list = insights.getFieldActions();
                                    if (list != null) {
                                        for (AdsActionStats stats : list) {
                                            if (stats.getFieldActionType().equals("app_custom_event.fb_mobile_activate_app")) {
                                                result = stats.getFieldValue();
                                            }
                                        }
                                    }
                                    JsonObject jsonObject = new JsonObject();
                                    jsonObject.addProperty("campaign_name", campaign.getFieldName());
                                    jsonObject.addProperty("campaign_id", campaign.getId());
                                    jsonObject.addProperty("clicks", insights.getFieldClicks());
                                    jsonObject.addProperty("impressions", insights.getFieldImpressions());
                                    jsonObject.addProperty("cpc", insights.getFieldCpc());
                                    jsonObject.addProperty("ctr", insights.getFieldCtr());
                                    jsonObject.addProperty("cpm", insights.getFieldCpm());
                                    jsonObject.addProperty("spend", insights.getFieldSpend());
                                    jsonObject.addProperty("cost_per_result", costPreResult);
                                    jsonObject.addProperty("result", result);
                                    jsonArray.add(jsonObject);
                                }
                            }
                            campaigns = null;//campaigns.nextPage();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        try {
                            (response).setCharacterEncoding("utf-8");
                            response.setContentType("application/json");
                            response.getWriter().write(jsonArray.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        context.complete();
                    }
                }
            }
        });
    }
}
