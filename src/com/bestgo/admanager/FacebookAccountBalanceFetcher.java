package com.bestgo.admanager;

import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;
import com.facebook.ads.sdk.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class FacebookAccountBalanceFetcher {
    public static final String ACCESS_TOKEN = DefaultConfig.ACCESS_TOKEN;
    public static final String APP_SECRET = DefaultConfig.APP_SECRET;


    private static final int ONE_PAGE_SIZE = 500;

    public static APIContext context = new APIContext(ACCESS_TOKEN, APP_SECRET).enableDebug(false);

    private static ExecutorService executors = Executors.newFixedThreadPool(20);

    public static void init() {
        initContext();
    }

    public static boolean run() throws IOException, APIException, InterruptedException {
        initContext();

        ArrayList<String> accountIds = fetchAccountList();

        for (int i = 0; i < accountIds.size(); i++) {
            String accountId = accountIds.get(i);
            executors.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        fetchOneAccount(accountId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        executors.shutdown();
        while (!executors.awaitTermination(60, TimeUnit.MINUTES)) {
            Thread.sleep(1000);
        }

        return true;
    }

    /**
     * 根据账户ID更新它的状态，spendCap，amountSpent
     *
     * @param accountId
     * @throws Exception
     */
    private static void fetchOneAccount(String accountId) throws Exception {
        AdAccount account = new AdAccount(accountId, context);
        AdAccount.APIRequestGet apiRequestGet = account.get();
        /*AdAccount adAccount1 = apiRequestGet.requestBalanceField().execute();
        String balance = adAccount1.getFieldBalance();*/

        AdAccount adAccount2 = apiRequestGet.requestAmountSpentField().execute();
        String amountSpent = adAccount2.getFieldAmountSpent();

        AdAccount adAccount3 = apiRequestGet.requestSpendCapField().execute();
        String spendCap = adAccount3.getFieldSpendCap();

        AdAccount adAccount4 = apiRequestGet.requestAccountStatusField().execute();
        Long status = adAccount4.getFieldAccountStatus();
        DB.update("web_account_id")
                .put("amount_spent", amountSpent)
                .put("spend_cap", spendCap)
                .put("status", status)
                .where(DB.filter().whereEqualTo("account_id", accountId))
                .execute();
    }

    private static void initContext() {
        try {
            JSObject object = DB.simpleScan("ad_app_config").select("app_secure", "access_token").execute();
            String accessToken = object.get("access_token");
            String appSecure = object.get("app_secure");
            context = new APIContext(accessToken, appSecure).enableDebug(true);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private static ArrayList<String> fetchAccountList() {
        ArrayList<String> accounts = new ArrayList<>();
        try {
            List<JSObject> list = DB.scan("web_account_id").select("account_id").execute();
            for (int i = 0; i < list.size(); i++) {
                JSObject one = list.get(i);
                accounts.add(one.get("account_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accounts;
    }

    /**
     * 对Facebook某个系列进行归档
     *
     * @param campaignId
     * @throws Exception
     */
    public static void archiveCampaignByCampaignId(String campaignId) throws Exception {
        init();
        String accountId = null;
        try {
            JSObject record = DB.simpleScan("web_ad_campaigns").select("account_id")
                    .where(DB.filter().whereEqualTo("campaign_id", campaignId)).execute();
            if (record.hasObjectData()) {
                accountId = record.get("account_id");
            } else {
                record = DB.simpleScan("ad_campaigns").select("account_id")
                        .where(DB.filter().whereEqualTo("campaign_id", campaignId)).execute();
                if (record.hasObjectData()) {
                    accountId = record.get("account_id");
                }
            }
        } catch (Exception ex) {
        }
        if (accountId == null) {
            System.out.println("accountId is null, 更新广告系列失败: " + campaignId);
            return;
        }

        try {
            Campaign campaign = new Campaign(campaignId, context);
            campaign.update().setStatus(Campaign.EnumStatus.VALUE_ARCHIVED).execute();
            System.out.println("更新广告系列成功: " + campaignId);
        } catch (Exception ex) {
            System.out.println("更新广告系列失败: " + campaignId);
        }
    }


    public static void enhancedArchiveCampaigns(Set<String> campaignSet) throws Exception {
        init();
        if (campaignSet != null && campaignSet.size() > 0) {
            for (String campaignId : campaignSet) {
                try {
                    Campaign currCampaign = new Campaign(campaignId, context);
                    currCampaign.update().setStatus(Campaign.EnumStatus.VALUE_ARCHIVED).execute();
                    System.out.println("更新广告系列状态为ARCHIVED成功: " + campaignId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void insertToDeleted(String campaignId) {
        try {
            DB.insert("ad_app_remove_campaign").put("campaign_id", campaignId)
                    .execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static final Logger LOGGER = Logger.getLogger(FacebookAccountBalanceFetcher.class);

    public static void updateFBAllCampaignStatus(String first) throws Exception {
        init();
        String sql = null;
        if ("yes".equalsIgnoreCase(first)) {
            sql = "SELECT account_id FROM web_account_id";
        } else {
            sql = "SELECT account_id FROM web_account_id WHERE status = 1";
        }
        List<JSObject> accountList = DB.findListBySql(sql);

        StringBuffer updateSql = null;
        Long begin0 = System.currentTimeMillis();

        Long begin = null;
        Long end = null;

        //一个帐号进行一次本地更新
        for (int j = 0, leng = accountList.size(); j < leng; j++) {

            begin = System.currentTimeMillis();

            JSObject jsaccount = accountList.get(j);
            String accountId = jsaccount.get("account_id");
            LOGGER.info("第" + j + "个帐户" + accountId + "开始进行状态更新操作！");

            AdAccount account = new AdAccount(accountId, context);

            APINodeList<Campaign> campaigns = account
                    .getCampaigns()
                    .setParam("limit", ONE_PAGE_SIZE)
                    .requestFields(Arrays.asList("campaign_id", "status"))
                    .setEffectiveStatus(Arrays.asList(Campaign.EnumEffectiveStatus.VALUE_ARCHIVED))
                    .execute();

            while (campaigns != null && campaigns.size() > 0) {
                updateSql = new StringBuffer();

                updateSql.append(" update web_ad_campaigns set ")
                        .append(" status = 'ARCHIVED' ")
                        .append(" where campaign_id in (");
                for (int i = 0; i < campaigns.size(); i++) {
                    if (i != campaigns.size() - 1) {
                        updateSql.append("'" + campaigns.get(i).getId() + "',");
                    } else {
                        updateSql.append("'" + campaigns.get(i).getId() + "')");
                    }
                }
                DB.updateBySql(updateSql.toString());
                campaigns = campaigns.nextPage(ONE_PAGE_SIZE);
            }
            end = System.currentTimeMillis();
            LOGGER.info("第" + j + "个帐户" + accountId + "状态更新完成！耗时 " + (end - begin) / 1000);
        }

        Long end0 = System.currentTimeMillis();
        LOGGER.info("$ update $更新状态为ARCHIVED的系列到本地总耗时 " + (end0 - begin0) / 1000);
    }


    /**
     * 多个条件下更新系列的状态
     *
     * @param accountId0                帐号id
     * @param containsDisabledAccountId 是否包含禁用帐号
     * @param campaignStatus            系列状态
     * @throws Exception
     */
    public static void updateFBCampaignStatusMultipleConditions(String accountId0, boolean containsDisabledAccountId, String campaignStatus) throws Exception {

        String sql = "";
        if (null == accountId0 || accountId0.isEmpty()) {//所有帐户
            if (containsDisabledAccountId) {//包含被禁的帐户
                sql = "SELECT account_id FROM web_account_id";
            } else { //不包含被禁用帐户
                sql = "SELECT account_id FROM web_account_id WHERE status = 1";
            }
            List<JSObject> accountList = DB.findListBySql(sql);

            if (null == campaignStatus || campaignStatus.isEmpty()) { //所有系列状态
                updateFBCampaignStatusByOneStatus(accountList, "ARCHIVED");
                updateFBCampaignStatusByOneStatus(accountList, "ACTIVE");
                updateFBCampaignStatusByOneStatus(accountList, "PAUSE");
            } else {//单独系列状态
                updateFBCampaignStatusByOneStatus(accountList, campaignStatus);
            }
        } else {//单个帐户
            if (!containsDisabledAccountId) {
                Map<String, Integer> facebookAccountDetailsMap = getFacebookAccountDetails();
                Integer integer = facebookAccountDetailsMap.get(accountId0);
                if (integer != 1) {
                    LOGGER.info(accountId0 + "帐号已经关闭");
                    return;
                }
            }
            JSObject jsObject = new JSObject();
            jsObject.put("account_id", accountId0);
            List<JSObject> accountList = new ArrayList<>();
            accountList.add(jsObject);

            if (null == campaignStatus || campaignStatus.isEmpty()) { //所有系列状态
                updateFBCampaignStatusByOneStatus(accountList, "ARCHIVED");
                updateFBCampaignStatusByOneStatus(accountList, "ACTIVE");
                updateFBCampaignStatusByOneStatus(accountList, "PAUSE");
            } else {//单独系列状态
                updateFBCampaignStatusByOneStatus(accountList, campaignStatus);
            }
        }
    }

    /**
     * 更新FB系列状态，通过一个状态
     * @param accountList 帐号id List
     * @param status      状态
     * @throws Exception
     */
    public static void updateFBCampaignStatusByOneStatus(List<JSObject> accountList, String status) throws Exception {
        DefaultConfig.setProxy();
        DB.init();
        init();

        StringBuffer updateSql = null;
        Long begin0 = System.currentTimeMillis();

        Long begin = null;
        Long end = null;

        //一个帐号进行一次本地更新
        for (int j = 0, leng = accountList.size(); j < leng; j++) {

            begin = System.currentTimeMillis();

            JSObject jsaccount = accountList.get(j);
            String accountId = jsaccount.get("account_id");
            LOGGER.info("第" + j + "个帐户" + accountId + "开始进行状态更新操作！");

            AdAccount account = new AdAccount(accountId, context);

            APINodeList<Campaign> campaigns = null;
            if ("ARCHIVED".equalsIgnoreCase(status)) {
                campaigns = account
                        .getCampaigns()
                        .setParam("limit", ONE_PAGE_SIZE)
                        .requestFields(Arrays.asList("campaign_id", "status"))
                        .setEffectiveStatus(Arrays.asList(Campaign.EnumEffectiveStatus.VALUE_ARCHIVED))
                        .execute();
            } else if ("ACTIVE".equalsIgnoreCase(status)) {
                campaigns = account
                        .getCampaigns()
                        .setParam("limit", ONE_PAGE_SIZE)
                        .requestFields(Arrays.asList("campaign_id", "status"))
                        .setEffectiveStatus(Arrays.asList(Campaign.EnumEffectiveStatus.VALUE_ACTIVE))
                        .execute();
            } else if ("PAUSED".equalsIgnoreCase(status)) {
                campaigns = account
                        .getCampaigns()
                        .setParam("limit", ONE_PAGE_SIZE)
                        .requestFields(Arrays.asList("campaign_id", "status"))
                        .setEffectiveStatus(Arrays.asList(Campaign.EnumEffectiveStatus.VALUE_PAUSED))
                        .execute();
            } else {
                LOGGER.info("传入的状态不存在！");
            }

            while (campaigns != null && campaigns.size() > 0) {
                updateSql = new StringBuffer();

                updateSql.append(" update web_ad_campaigns set status = '")
                        .append(status).append("' ")
                        .append(" where campaign_id in (");
                for (int i = 0; i < campaigns.size(); i++) {
                    if (i != campaigns.size() - 1) {
                        updateSql.append("'" + campaigns.get(i).getId() + "',");
                    } else {
                        updateSql.append("'" + campaigns.get(i).getId() + "')");
                    }
                }
                DB.updateBySql(updateSql.toString());
                campaigns = campaigns.nextPage(ONE_PAGE_SIZE);
            }
            end = System.currentTimeMillis();
            LOGGER.info("第" + j + "个帐户" + accountId + "状态更新完成！耗时 " + (end - begin) / 1000);
        }
        Long end0 = System.currentTimeMillis();
        LOGGER.info("$ update $更新状态为ARCHIVED的系列到本地总耗时 " + (end0 - begin0) / 1000);
    }


    /**
     * 所有的帐号的id和status的map
     *
     * @return map
     */
    private static Map<String, Integer> getFacebookAccountDetails() {
        Map<String, Integer> map = new HashMap<>();
        try {
            List<JSObject> list = DB.findListBySql("SELECT account_id,status FROM web_account_id");
            for (int i = 0, len = list.size(); i < len; i++) {
                JSObject js = list.get(i);
                if (js.hasObjectData()) {
                    String accountId = js.get("account_id");
                    Integer status = js.get("status");
                    if (status != null && accountId != null) {
                        map.put(accountId, status);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    /**
     * 所有的tag的id和name的map
     *
     * @return map
     */
    private static Map<String, Integer> getFacebookTagDetails() {
        Map<String, Integer> map = new HashMap<>();
        try {
            List<JSObject> list = DB.findListBySql("SELECT id,tag_name FROM web_tag");
            for (int i = 0, len = list.size(); i < len; i++) {
                JSObject js = list.get(i);
                if (js.hasObjectData()) {
                    Long id = js.get("id");
                    int idInt = new Long(id).intValue();
                    String tag_name = js.get("tag_name");
                    if (id != null && tag_name != null) {
                        map.put(tag_name, idInt);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    /**
     * 多情况下删除FB的广告系列
     *
     * @param accountId
     * @param status
     * @param tag
     * @param country
     */
    public static void deleteFBCampaignMultipleConditions(String accountId, String status, String tag, String country) throws Exception {
        String sql = "";
        if (!(null == accountId) && !"".equals(accountId)) {//帐户非空
            if (!(null == status) && !"".equals(status)) {//帐号+状态
                sql = "SELECT DISTINCT campaign_id FROM web_ad_campaigns WHERE account_id = '" + accountId + "' AND STATUS = '" + status + "'";
                archiveFacebookCampaignsByCampaignId(sql);
            } else if (!(null == tag) && !"".equals(tag)) {//帐号+应用
                Map<String, Integer> facebookTagDetails = getFacebookTagDetails();
                Integer tag_id = facebookTagDetails.get(tag);
                sql = "SELECT DISTINCT campaign_id FROM web_ad_campaigns WHERE account_id = '" + accountId + "' AND tag_id = '" + tag_id + "'";
                archiveFacebookCampaignsByCampaignId(sql);
            } else {//帐户下所有系列
                sql = "SELECT DISTINCT campaign_id FROM web_ad_campaigns where account_id = '" + accountId + "'";
                archiveFacebookCampaignsByCampaignId(sql);
            }
        } else if (!(null == tag) && !(null == country) && !"".equals(tag) && !"".equals(country)) {//应用+国家
            Map<String, Integer> facebookTagDetails = getFacebookTagDetails();
            Integer tag_id = facebookTagDetails.get(tag);


            sql = "SELECT  DISTINCT campaign_id FROM web_ad_campaigns WHERE tag_id = '" + tag_id + "' AND country_code LIKE '%" + country + "%'";
            archiveFacebookCampaignsByCampaignId(sql);
        } else {
            System.out.println("参数有误！请重新输入！！");
            LOGGER.info("参数有误！请重新输入！！");
        }
    }

    /**
     * 通过sql来查询出需要被关闭的campaign_id，并调用archiveCampaigns
     *
     * @param
     * @return
     */
    public static boolean archiveFacebookCampaignsByCampaignId(String sql) {
        try {
            List<JSObject> successCampaignList = DB.findListBySql(sql);
            if (successCampaignList.size() > 0) {
                Set<String> facebookArchiveCampaignSet = new HashSet<>();
                for (JSObject one : successCampaignList) {
                    if (one.hasObjectData()) {
                        String campaignId = one.get("campaign_id");
                        facebookArchiveCampaignSet.add(campaignId);
                    }
                }
                FacebookAccountBalanceFetcher.archiveCampaigns(facebookArchiveCampaignSet);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 对某些Facebook系列进行归档
     *
     * @param campaignSet
     * @throws Exception
     */
    public static boolean archiveCampaigns(Set<String> campaignSet) throws Exception {
        DefaultConfig.setProxy();
        DB.init();
        init();
        ExecutorService currExecutors = Executors.newFixedThreadPool(30);
        ConcurrentHashMap<Long, Boolean> result = new ConcurrentHashMap<>();
        if (campaignSet != null && campaignSet.size() > 0) {
            for (String campaignId : campaignSet) {
                currExecutors.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
//                            Campaign currCampaign = new Campaign(campaignId, context);
//                            currCampaign.update().setStatus(Campaign.EnumStatus.VALUE_ARCHIVED).execute();
                            System.out.println("更新广告系列状态为ARCHIVED成功: " + campaignId);
                            DB.update("web_ad_campaigns")
                                    .put("status", "ARCHIVED")
                                    .where(DB.filter().whereEqualTo("campaign_id", campaignId))
                                    .execute();
                        } catch (Exception e) {
                            String message = e.getMessage();
                            if (message.contains("Deleted Campaigns Can't Be Edited")) {
                                insertToDeleted(campaignId);
                                try {
                                    DB.update("web_ad_campaigns")
                                            .put("status", "ARCHIVED")
                                            .where(DB.filter().whereEqualTo("campaign_id", campaignId))
                                            .execute();
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            } else {
                                System.out.println("更新广告系列状态为ARCHIVED失败: " + campaignId + "," + message);
                            }
                        }
                    }
                });
            }
        }
        currExecutors.shutdown();
        long lastFinishedTaskCount = 0;
        while (!currExecutors.awaitTermination(60, TimeUnit.SECONDS)) {
            System.out.println("getActiveCount=" + ((ThreadPoolExecutor) currExecutors).getActiveCount());
            System.out.println("getQueue=" + ((ThreadPoolExecutor) currExecutors).getQueue().size());
            System.out.println("getCompletedTaskCount=" + ((ThreadPoolExecutor) currExecutors).getCompletedTaskCount());
            long count = ((ThreadPoolExecutor) currExecutors).getCompletedTaskCount();
            if (count > lastFinishedTaskCount) {
                lastFinishedTaskCount = count;
            } else {
                System.out.println("job execute timeout");
                Logger logger = Logger.getLogger("FILE");
                logger.error("job execute timeout");
                currExecutors.shutdownNow();
                break;
            }
            Thread.sleep(1000);
        }

        return result.size() == 0;
    }

}
