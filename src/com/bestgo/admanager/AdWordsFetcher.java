package com.bestgo.admanager;

import com.bestgo.admanager_tools.DefaultConfig;
import com.bestgo.admanager_tools.bean.AdBatchChangeItem;
import com.bestgo.admanager_tools.bean.AdCampaignItemAdmob;
import com.bestgo.admanager_tools.utils.DateUtil;
import com.bestgo.admanager_tools.utils.Utils;
import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.services.Store;
import com.bestgo.common.database.utils.JSObject;
import com.google.api.ads.adwords.axis.factory.AdWordsServices;
import com.google.api.ads.adwords.axis.utils.v201802.SelectorBuilder;
import com.google.api.ads.adwords.axis.v201802.cm.*;
import com.google.api.ads.adwords.lib.client.AdWordsSession;
import com.google.api.ads.adwords.lib.client.reporting.ReportingConfiguration;
import com.google.api.ads.adwords.lib.jaxb.v201802.DateRange;
import com.google.api.ads.adwords.lib.jaxb.v201802.*;
import com.google.api.ads.adwords.lib.jaxb.v201802.Predicate;
import com.google.api.ads.adwords.lib.jaxb.v201802.PredicateOperator;
import com.google.api.ads.adwords.lib.jaxb.v201802.ReportDefinitionReportType;
import com.google.api.ads.adwords.lib.jaxb.v201802.Selector;
import com.google.api.ads.adwords.lib.selectorfields.v201802.cm.BiddingStrategyField;
import com.google.api.ads.adwords.lib.selectorfields.v201802.cm.BudgetField;
import com.google.api.ads.adwords.lib.selectorfields.v201802.cm.CampaignCriterionField;
import com.google.api.ads.adwords.lib.selectorfields.v201802.cm.CampaignField;
import com.google.api.ads.adwords.lib.utils.ReportDownloadResponse;
import com.google.api.ads.adwords.lib.utils.ReportDownloadResponseException;
import com.google.api.ads.adwords.lib.utils.v201802.ReportDownloader;
import com.google.api.ads.common.lib.auth.OfflineCredentials;
import com.google.api.ads.common.lib.auth.OfflineCredentials.Api;
import com.google.api.client.auth.oauth2.Credential;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AdWordsFetcher {
    private static final int PAGE_SIZE = 300;
    private static ExecutorService executors = Executors.newFixedThreadPool(10);
    private static JsonObject adwordsCountryCodes;
    private static Credential oAuth2Credential;
    private static Credential oAuth2Credential_P;

    public static void runFetchAllData(String date) throws Exception {
        JsonParser parser = new JsonParser();
        adwordsCountryCodes = parser.parse(DefaultConfig.ADWORDS_COUNTRY_CODES).getAsJsonObject();

        List<JSObject> accountList = fetchAccountJSObjectList();

        for (int i = 0; i < accountList.size(); i++) {
            JSObject account = accountList.get(i);
            String accountId = account.get("account_id");
            String shortName = account.get("short_name");
//            if (!accountId.equals("670-865-7184")) continue;

            executors.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        fetchOneAccount(accountId, shortName, date);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        executors.shutdown();
        while (!executors.awaitTermination(60, TimeUnit.SECONDS)) {
            Thread.sleep(1000);
        }
    }

    /**
     * 更新所有Adwords系列状态
     *
     * @throws Exception
     */
    public static void updateAllCampaignsStatus() throws Exception {
        List<String> accountList = fetchAccountList();
        for (int i = 0; i < accountList.size(); i++) {
            String accountId = accountList.get(i);
            executors.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        syncStatus(accountId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        executors.shutdown();
        while (!executors.awaitTermination(60, TimeUnit.SECONDS)) {
            Thread.sleep(1000);
        }
    }

    /**
     * 抓取所有Adwords账号ID
     *
     * @return
     */
    private static List<String> fetchAccountList() {
        List<String> accounts = new ArrayList<>();
        try {
            List<JSObject> list = DB.scan("web_account_id_admob").select("account_id").execute();
            for (int i = 0; i < list.size(); i++) {
                JSObject one = list.get(i);
                accounts.add(one.get("account_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accounts;
    }

    private static List<JSObject> fetchAccountJSObjectList() {
        List<JSObject> accounts = new ArrayList<>();
        try {
            accounts = DB.scan("web_account_id_admob").select("account_id", "short_name").execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accounts;
    }

    static String P_ACCOUNT_IDS = "398-006-2505\n" +
            "516-445-2142\n" +
            "674-188-9094\n" +
            "686-187-9574\n" +
            "994-437-7127\n" +
            "626-988-5244\n" +
            "341-189-8882\n" +
            "801-631-8876\n" +
            "105-048-9452\n" +
            "377-938-2021\n" +
            "426-864-8688\n" +
            "567-629-9300\n" +
            "864-855-7491\n" +
            "552-625-1631\n" +
            "113-067-1625\n" +
            "731-954-4723\n" +
            "794-632-3809\n" +
            "269-399-3997\n" +
            "923-239-2317\n" +
            "139-951-9137\n" +
            "120-794-1966\n" +
            "919-945-8297\n" +
            "581-236-1960\n" +
            "700-664-4570\n" +
            "272-986-4407";

    private static synchronized AdWordsSession getAdWordsSession(String accountId) throws Exception {
        if (P_ACCOUNT_IDS.indexOf(accountId) == -1) {
            // Generate a refreshable OAuth2 credential.
            if (oAuth2Credential == null || oAuth2Credential.getExpiresInSeconds() < 600) {
                oAuth2Credential = new OfflineCredentials.Builder()
                        .forApi(Api.ADWORDS)
                        .fromFile()
                        .build()
                        .generateCredential();
            }

            // Construct an AdWordsSession.
            AdWordsSession session = new AdWordsSession.Builder()
                    .fromFile()
                    .withOAuth2Credential(oAuth2Credential)
                    .build();
            session.setClientCustomerId(accountId);

            return session;
        } else {
            if (oAuth2Credential_P == null || oAuth2Credential_P.getExpiresInSeconds() < 600) {
                oAuth2Credential_P = new OfflineCredentials.Builder()
                        .forApi(Api.ADWORDS)
                        .fromFile("ads.p.properties")
                        .build()
                        .generateCredential();
            }

            // Construct an AdWordsSession.
            AdWordsSession session = new AdWordsSession.Builder()
                    .fromFile()
                    .withOAuth2Credential(oAuth2Credential_P)
                    .build();
            session.setClientCustomerId(accountId);

            return session;
        }
    }

    private static void fetchOneAccount(String accountId, String shortName, String date) throws Exception {
        ArrayList<String> searchCampaigns = new ArrayList<>();
        ArrayList<String> uacCampaigns = new ArrayList<>();
        HashMap<String, TempItemData> tempCampaignsData = new HashMap<>();

        Selector selector = new Selector();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dd = simpleDateFormat.parse(date);
        simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        date = simpleDateFormat.format(dd);
        AdWordsSession session = getAdWordsSession(accountId);

        selector.getFields().addAll(Arrays.asList("CampaignId",
                "CampaignName",
                "CampaignStatus",
                "StartDate",
                "Amount",
                "Impressions",
                "Conversions",
                "Clicks",
                "Cost",
                "AdvertisingChannelType",
                "AdvertisingChannelSubType",
                "BiddingStrategyId",
                "BiddingStrategyName",
                "BiddingStrategyType"
        ));

        // Create report definition.
        ReportDefinition reportDefinition = new ReportDefinition();
        reportDefinition.setReportName("Criteria performance report #" + System.currentTimeMillis());
        reportDefinition.setDateRangeType(ReportDefinitionDateRangeType.CUSTOM_DATE);
        reportDefinition.setReportType(ReportDefinitionReportType.CAMPAIGN_PERFORMANCE_REPORT);
        reportDefinition.setDownloadFormat(DownloadFormat.CSVFOREXCEL);

        DateRange dateRange = new DateRange();
        dateRange.setMin(date);
        dateRange.setMax(date);
        selector.setDateRange(dateRange);
        // Optional: Set the reporting configuration of the session to suppress header, column name, or
        // summary rows in the report output. You can also configure this via your ads.properties
        // configuration file. See AdWordsSession.Builder.from(Configuration) for details.
        // In addition, you can set whether you want to explicitly include or exclude zero impression
        // rows.
        ReportingConfiguration reportingConfiguration =
                new ReportingConfiguration.Builder()
                        .skipReportHeader(true)
                        .skipColumnHeader(false)
                        .skipReportSummary(true)
                        // Enable to allow rows with zero impressions to show.
                        .includeZeroImpressions(false)
                        .build();
        session.setReportingConfiguration(reportingConfiguration);

        reportDefinition.setSelector(selector);

        try {
            // Set the property api.adwords.reportDownloadTimeout or call
            // ReportDownloader.setReportDownloadTimeout to set a timeout (in milliseconds)
            // for CONNECT and READ in report downloads.
            ReportDownloadResponse response =
                    new ReportDownloader(session).downloadReport(reportDefinition);
            String str = response.getAsString();
            String[] lines = str.split("\n");
            for (int i = 0; i < lines.length; i++) {
                if (i == 0) {
//                    DB.update("web_ad_campaigns_admob")
//                            .put("status", "inactive").where(DB.filter().whereEqualTo("account_id", accountId)).execute();
                    continue;
                }
                String[] values = lines[i].split("\t");
                String campaignId = values[0];
                String campaignName = values[1];
                String state = values[2];
                String startDate = values[3];
                int budget = Utils.parseInt(values[4], 0) / 10000;
                double bidding = 0;
                int impressions = Utils.parseInt(values[5], 0);
                double totalInstalled = Utils.parseDouble(values[6].replaceAll("[,\"]", ""), 0);
                int totalClick = Utils.parseInt(values[7], 0);
                double totalSpend = Utils.parseDouble(values[8], 0) / 1000 / 1000;
                String channelType = values[9];
                String channelSubType = values[10];
                if (channelSubType.equals("Universal App Campaign")) {
                    uacCampaigns.add(campaignId);
                } else {
                    searchCampaigns.add(campaignId);
                    TempCampaignData tempCampaignData = getCampaignBudget(campaignId, session);
                    if (tempCampaignData != null) {
                        budget = (int) tempCampaignData.budget / 10000;
                        bidding = tempCampaignData.bidding / 10000;
                    }
                }
                campaignName = campaignName.replaceAll("\"", "");

                String appName = "xxx";
                JSObject app = DB.findOneBySql("SELECT app_name FROM ad_campaigns_admob WHERE campaign_id = '" + campaignId + "'");
                if (app.hasObjectData()) {
                    appName = app.get("app_name");
                }

                updateCampaign(appName, campaignId, campaignName, state, accountId, shortName, budget, bidding, startDate, impressions, totalInstalled, totalClick, totalSpend);
                updateCampaignToHistory(appName, accountId, shortName, campaignId, date, impressions, totalInstalled, totalClick, totalSpend);
            }
        } catch (ReportDownloadResponseException e) {
            System.out.printf("Report was not downloaded due to: %s%n", e);
        }

        //download country report
        if (searchCampaigns.size() > 0) {
            session = getAdWordsSession(accountId);
            selector = new Selector();

            selector.getFields().addAll(Arrays.asList("CampaignId",
                    "CampaignName",
                    "CountryCriteriaId",
                    "Impressions",
                    "Conversions",
                    "Clicks",
                    "Cost"));
            Predicate predicate = new Predicate();
            predicate.setField("CampaignId");
            predicate.setOperator(PredicateOperator.IN);
            for (int i = 0; i < searchCampaigns.size(); i++) {
                predicate.getValues().add(searchCampaigns.get(i));
            }
            selector.getPredicates().add(predicate);

            dateRange = new DateRange();
            dateRange.setMin(date);
            dateRange.setMax(date);
            selector.setDateRange(dateRange);

            // Create report definition.
            reportDefinition = new ReportDefinition();
            reportDefinition.setReportName("GEO performance report #" + System.currentTimeMillis());
            reportDefinition.setDateRangeType(ReportDefinitionDateRangeType.CUSTOM_DATE);
            reportDefinition.setReportType(ReportDefinitionReportType.GEO_PERFORMANCE_REPORT);
            reportDefinition.setDownloadFormat(DownloadFormat.CSVFOREXCEL);

            reportingConfiguration =
                    new ReportingConfiguration.Builder()
                            .skipReportHeader(true)
                            .skipColumnHeader(false)
                            .skipReportSummary(true)
                            // Enable to allow rows with zero impressions to show.
                            .includeZeroImpressions(false)
                            .build();
            session.setReportingConfiguration(reportingConfiguration);

            reportDefinition.setSelector(selector);

            try {
                // Set the property api.adwords.reportDownloadTimeout or call
                // ReportDownloader.setReportDownloadTimeout to set a timeout (in milliseconds)
                // for CONNECT and READ in report downloads.
//            String query = "SELECT AdNetworkType1,AdNetworkType2,CampaignId, CampaignName, CountryCriteriaId, Impressions, Conversions, Clicks, "
//                    + "Cost FROM GEO_PERFORMANCE_REPORT where CampaignId=931999189 "
//                    + "DURING 20171002,20171002";

                ReportDownloadResponse response =
                        new ReportDownloader(session).downloadReport(reportDefinition);
                String str = response.getAsString();
                String[] lines = str.split("\n");
                for (int i = 0; i < lines.length; i++) {
                    if (i == 0) {
                        continue;
                    }
                    String[] values = lines[i].split("\t");
                    String campaignId = values[0];
                    String countryId = values[2];
                    JsonElement jsonElement = adwordsCountryCodes.get(countryId);
                    String countryCode = "";
                    if (jsonElement != null) {
                        countryCode = jsonElement.getAsString();
                    } else {
                        countryCode = "NONE";
                    }
                    int impressions = Utils.parseInt(values[3], 0);
                    double totalInstalled = Utils.parseDouble(values[4].replaceAll("[,\"]", ""), 0);
                    int totalClick = Utils.parseInt(values[5], 0);
                    double totalSpend = Utils.parseDouble(values[6], 0) / 1000 / 1000;
                    if (countryCode.equals("NONE")) {
                        TempItemData tempItemData = tempCampaignsData.get(campaignId);
                        if (tempItemData == null) {
                            tempItemData = new TempItemData();
                            tempCampaignsData.put(campaignId, tempItemData);
                        }
                        tempItemData.impressions += impressions;
                        tempItemData.totalInstalled += totalInstalled;
                        tempItemData.totalClick += totalClick;
                        tempItemData.totalSpend += totalSpend;

                        impressions = tempItemData.impressions;
                        totalInstalled = tempItemData.totalInstalled;
                        totalClick = tempItemData.totalClick;
                        totalSpend = tempItemData.totalSpend;
                    }

                    String appName = "xxx";
                    JSObject app = DB.findOneBySql("SELECT app_name FROM ad_campaigns_admob WHERE campaign_id = '" + campaignId + "'");
                    if (app.hasObjectData()) {
                        appName = app.get("app_name");
                    }
                    insertIntoHistoryCountry(appName, accountId, shortName, date, campaignId, countryCode, totalClick, totalSpend, totalInstalled, impressions);
                }
            } catch (ReportDownloadResponseException e) {
                System.out.printf("Report was not downloaded due to: %s%n", e);
            }
        }

        tempCampaignsData.clear();
        if (uacCampaigns.size() > 0) {
            session = getAdWordsSession(accountId);
            selector = new Selector();

            selector.getFields().addAll(Arrays.asList("CampaignId",
                    "CampaignName",
                    "Id",
                    "Impressions",
                    "Conversions",
                    "Clicks",
                    "Cost"));
            Predicate predicate = new Predicate();
            predicate.setField("CampaignId");
            predicate.setOperator(PredicateOperator.IN);
            for (int i = 0; i < uacCampaigns.size(); i++) {
                predicate.getValues().add(uacCampaigns.get(i));
            }
            selector.getPredicates().add(predicate);
//            predicate = new Predicate();
//            predicate.setField("Id");
//            predicate.setOperator(PredicateOperator.EQUALS);
//            predicate.getValues().add("2368");
//            selector.getPredicates().add(predicate);

            dateRange = new DateRange();
            dateRange.setMin(date);
            dateRange.setMax(date);
            selector.setDateRange(dateRange);

            // Create report definition.
            reportDefinition = new ReportDefinition();
            reportDefinition.setReportName("Location Target performance report #" + System.currentTimeMillis());
            reportDefinition.setDateRangeType(ReportDefinitionDateRangeType.CUSTOM_DATE);
            reportDefinition.setReportType(ReportDefinitionReportType.CAMPAIGN_LOCATION_TARGET_REPORT);
            reportDefinition.setDownloadFormat(DownloadFormat.CSVFOREXCEL);

            reportingConfiguration =
                    new ReportingConfiguration.Builder()
                            .skipReportHeader(true)
                            .skipColumnHeader(false)
                            .skipReportSummary(true)
                            // Enable to allow rows with zero impressions to show.
                            .includeZeroImpressions(false)
                            .build();
            session.setReportingConfiguration(reportingConfiguration);

            reportDefinition.setSelector(selector);

            try {
                // Set the property api.adwords.reportDownloadTimeout or call
                // ReportDownloader.setReportDownloadTimeout to set a timeout (in milliseconds)
                // for CONNECT and READ in report downloads.
//            String query = "SELECT AdNetworkType1,AdNetworkType2,CampaignId, CampaignName, CountryCriteriaId, Impressions, Conversions, Clicks, "
//                    + "Cost FROM GEO_PERFORMANCE_REPORT where CampaignId=931999189 "
//                    + "DURING 20171002,20171002";

                ReportDownloadResponse response =
                        new ReportDownloader(session).downloadReport(reportDefinition);
                String str = response.getAsString();
                String[] lines = str.split("\n");
                for (int i = 0; i < lines.length; i++) {
                    if (i == 0) {
                        continue;
                    }
                    String[] values = lines[i].split("\t");
                    String campaignId = values[0];
                    String countryId = values[2];
                    JsonElement jsonElement = adwordsCountryCodes.get(countryId);
                    String countryCode = "";
                    if (jsonElement != null) {
                        countryCode = jsonElement.getAsString();
                    } else {
                        countryCode = "NONE";
                    }
                    int impressions = Utils.parseInt(values[3], 0);
                    double totalInstalled = Utils.parseDouble(values[4].replaceAll("[,\"]", ""), 0);
                    int totalClick = Utils.parseInt(values[5], 0);
                    double totalSpend = Utils.parseDouble(values[6], 0) / 1000 / 1000;
                    if (countryCode.equals("NONE")) {
                        TempItemData tempItemData = tempCampaignsData.get(campaignId);
                        if (tempItemData == null) {
                            tempItemData = new TempItemData();
                            tempCampaignsData.put(campaignId, tempItemData);
                        }
                        tempItemData.impressions += impressions;
                        tempItemData.totalInstalled += totalInstalled;
                        tempItemData.totalClick += totalClick;
                        tempItemData.totalSpend += totalSpend;

                        impressions = tempItemData.impressions;
                        totalInstalled = tempItemData.totalInstalled;
                        totalClick = tempItemData.totalClick;
                        totalSpend = tempItemData.totalSpend;
                    }

                    String appName = "xxx";
                    JSObject app = DB.findOneBySql("SELECT app_name FROM ad_campaigns_admob WHERE campaign_id = '" + campaignId + "'");
                    if (app.hasObjectData()) {
                        appName = app.get("app_name");
                    }
                    insertIntoHistoryCountry(appName, accountId, shortName, date, campaignId, countryCode, totalClick, totalSpend, totalInstalled, impressions);
                }
            } catch (ReportDownloadResponseException e) {
                System.out.printf("Report was not downloaded due to: %s%n", e);
            }
        }
    }

    /**
     * 根据账号同步Adwords状态,目前是每隔一个小时执行一次
     * <<<<<<< Updated upstream
     * =======
     *
     * @param >>>>>>>   Stashed changes
     * @param accountId
     */
    public static void syncStatus(String accountId) {
        DB.init();

        DefaultConfig.setProxy();

        String[] queryIds = null;
        try {
            AdWordsSession session = getAdWordsSession(accountId);
            List<JSObject> list = DB.scan("web_ad_campaigns_admob").select("campaign_id", "status").where(DB.filter().whereEqualTo("account_id", accountId)).execute();
            ArrayList<String> campaignIds = new ArrayList<>();
            HashMap<String, String> campaignStatus = new HashMap<>();
            for (int i = 0; i < list.size(); i++) {
                String campaignId = list.get(i).get("campaign_id");
//                JSObject record = DB.simpleScan("ad_app_remove_campaign").select("campaign_id").where(DB.filter().whereEqualTo("campaign_id", campaignId)).execute();
//                if (record.hasObjectData()) {
//                    continue;
//                }
                campaignStatus.put(campaignId, list.get(i).get("status"));
                campaignIds.add(campaignId);
            }

            int pageSize = 1000;

            for (int i = 0; i < campaignIds.size(); i += pageSize) {
                AdWordsServices adWordsServices = new AdWordsServices();
                CampaignServiceInterface campaignService =
                        adWordsServices.get(session, CampaignServiceInterface.class);

                List<String> subList = campaignIds.subList(i, (campaignIds.size() - i) > pageSize ? (i + pageSize) : campaignIds.size());
                queryIds = new String[subList.size()];
                subList.toArray(queryIds);

                SelectorBuilder builder = new SelectorBuilder();
                com.google.api.ads.adwords.axis.v201802.cm.Selector selector = builder
                        .fields(CampaignField.Id, CampaignField.Name, CampaignField.Status)
                        .offset(0)
                        .limit(pageSize)
                        .in("Id", queryIds)
                        .build();

                CampaignPage page = campaignService.get(selector);
                if (page.getEntries() != null) {
                    for (Campaign campaign : page.getEntries()) {
                        String status = campaignStatus.get(campaign.getId() + "");
                        if (!status.equals(campaign.getStatus().getValue().toLowerCase())) {
                            DB.update("web_ad_campaigns_admob").put("status", campaign.getStatus().getValue().toLowerCase())
                                    .where(DB.filter().whereEqualTo("campaign_id", campaign.getId() + ""))
                                    .execute();

                        }
                    }
                }
            }
        } catch (ApiException ex) {
            ApiError[] errors = ex.getErrors();
            if (errors != null) {
                String error = "";
                for (int i = 0; i < errors.length; i++) {
                    error = errors[i].getErrorString();
                    System.out.println("APiError: " + errors[i].getErrorString());

                    int index = errors[i].getFieldPathElements()[0].getIndex();
                    if (error.contains("OperationAccessDenied.OPERATION_NOT_PERMITTED_FOR_REMOVED_ENTITY")) {
                        String campaignId = queryIds[index];
                        try {
                            DB.updateBySql("UPDATE web_ad_campaigns_admob SET status = 'removed' WHERE campaign_id = '" + campaignId + "'");
                            insertToDeleted(queryIds[index]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static TempCampaignData getCampaignBudget(String campaignId, AdWordsSession session) throws Exception {
        TempCampaignData data = new TempCampaignData();

        AdWordsServices adWordsServices = new AdWordsServices();
        CampaignServiceInterface campaignService =
                adWordsServices.get(session, CampaignServiceInterface.class);

        int offset = 0;

        BiddingStrategyServiceInterface biddingStrategyService = adWordsServices.get(session, BiddingStrategyServiceInterface.class);

        SelectorBuilder builder = new SelectorBuilder();
        com.google.api.ads.adwords.axis.v201802.cm.Selector selector = builder
                .fields(CampaignField.Id, CampaignField.Name, CampaignField.Amount,
                        CampaignField.BiddingStrategyId)
                .orderAscBy(CampaignField.Name)
                .offset(offset)
                .limit(PAGE_SIZE)
                .in("Id", campaignId)
                .build();

        CampaignPage page = campaignService.get(selector);
        if (page.getEntries() != null) {
            for (Campaign campaign : page.getEntries()) {
                data.budget = campaign.getBudget().getAmount().getMicroAmount();
                if (campaign.getBiddingStrategyConfiguration() == null) break;
                long biddingId = campaign.getBiddingStrategyConfiguration().getBiddingStrategyId();

                BiddingStrategyPage biddingStrategyPage = biddingStrategyService.get(
                        new SelectorBuilder().fields(BiddingStrategyField.BiddingScheme)
                                .in("Id", biddingId + "")
                                .build()
                );
                if (biddingStrategyPage.getEntries() != null) {
                    for (SharedBiddingStrategy strategy : biddingStrategyPage.getEntries()) {
                        BiddingScheme scheme = strategy.getBiddingScheme();
                        if (scheme instanceof TargetCpaBiddingScheme) {
                            data.bidding = ((TargetCpaBiddingScheme) scheme).getTargetCpa().getMicroAmount();
                        }
                    }
                }
                break;
            }
        }

        return data;
    }

    static class TempCampaignData {
        public long budget;
        public long bidding;
    }

    static class TempItemData {
        public int impressions;
        public double totalInstalled;
        public int totalClick;
        public double totalSpend;

        public TempItemData() {
            impressions = 0;
            totalInstalled = 0;
            totalClick = 0;
            totalSpend = 0;
        }
    }

    private static void updateCampaignToHistory(String appName, String accountId, String shortName, String campaignId, String date, int impression, double installed, int clicked, double totalspend) {
        try {
            long tagId = 0;
            String countryCode = "";
            JSObject one = DB.findOneBySql("SELECT tag_id,country_code FROM web_ad_campaigns_admob WHERE campaign_id = '" + campaignId + "'");
            if (one.hasObjectData()) {
                tagId = one.get("tag_id");
                countryCode = one.get("country_code");
            }
            one = DB.simpleScan("web_ad_campaigns_history_admob")
                    .select("id")
                    .where(DB.filter().whereEqualTo("campaign_id", campaignId))
                    .and(DB.filter().whereEqualTo("date", date))
                    .execute();
            if (one.hasObjectData()) {
                DB.update("web_ad_campaigns_history_admob")
                        .put("total_spend", totalspend)
                        .put("total_click", clicked)
                        .put("total_installed", installed)
                        .put("total_impressions", impression)
                        .put("cpa", installed > 0 ? totalspend / installed : 0)
                        .put("ctr", impression > 0 ? clicked * 1.0 / impression : 0)
                        .where(DB.filter().whereEqualTo("campaign_id", campaignId))
                        .and(DB.filter().whereEqualTo("date", date))
                        .execute();
            } else {
                DB.insert("web_ad_campaigns_history_admob")
                        .put("date", date)
                        .put("total_spend", totalspend)
                        .put("total_click", clicked)
                        .put("total_installed", installed)
                        .put("total_impressions", impression)
                        .put("cpa", installed > 0 ? totalspend / installed : 0)
                        .put("ctr", impression > 0 ? clicked * 1.0 / impression : 0)
                        .put("campaign_id", campaignId)
                        .put("tag_id", tagId)
                        .put("country_code", countryCode)
                        .execute();
            }

            one = DB.simpleScan("`web_ad_campaigns_history_admob_" + appName.toLowerCase() + "`")
                    .select("id")
                    .where(DB.filter().whereEqualTo("campaign_id", campaignId))
                    .and(DB.filter().whereEqualTo("date", date))
                    .execute();
            if (one.hasObjectData()) {
                DB.update("`web_ad_campaigns_history_admob_" + appName.toLowerCase() + "`")
                        .put("total_spend", totalspend)
                        .put("total_click", clicked)
                        .put("total_installed", installed)
                        .put("total_impressions", impression)
                        .put("cpa", installed > 0 ? totalspend / installed : 0)
                        .put("ctr", impression > 0 ? clicked * 1.0 / impression : 0)
                        .where(DB.filter().whereEqualTo("campaign_id", campaignId))
                        .and(DB.filter().whereEqualTo("date", date))
                        .execute();
            } else {
                DB.insert("`web_ad_campaigns_history_admob_" + appName.toLowerCase() + "`")
                        .put("date", date)
                        .put("account_id", accountId)
                        .put("short_name", shortName)
                        .put("total_spend", totalspend)
                        .put("total_click", clicked)
                        .put("total_installed", installed)
                        .put("total_impressions", impression)
                        .put("cpa", installed > 0 ? totalspend / installed : 0)
                        .put("ctr", impression > 0 ? clicked * 1.0 / impression : 0)
                        .put("campaign_id", campaignId)
                        .execute();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void updateCampaign(String appName, String campaignId, String campaignName, String status, String accountId, String shortName, int budget, double bidding,
                                       String createDate, int impression, double installed, int clicked, double totalspend) {
        try {
            if (bidding == 0) {
                JSObject object = DB.simpleScan("web_ad_campaigns_admob").select("bidding").where(DB.filter().whereEqualTo("campaign_id", campaignId)).execute();
                if (object.hasObjectData()) {
                    bidding = Utils.convertDouble(object.get("bidding"), 0);
                }
            }
            JSObject jsObject = DB.simpleScan("web_ad_campaigns_admob").select("id").where(DB.filter().whereEqualTo("campaign_id", campaignId))
                    .execute();
            if (campaignName.length() > 100) {
                campaignName = campaignName.substring(0, 100);
            }
            if (jsObject.hasObjectData()) {
                DB.update("web_ad_campaigns_admob")
                        .put("budget", budget)
                        .put("campaign_name", campaignName)
                        .put("account_id", accountId)
                        .put("bidding", bidding)
                        .put("status", status)
                        .put("total_spend", totalspend)
                        .put("total_click", clicked)
                        .put("total_installed", installed)
                        .put("total_impressions", impression)
                        .put("cpa", installed > 0 ? totalspend / installed : 0)
                        .put("ctr", impression > 0 ? clicked * 1.0 / impression : 0)
                        .where(DB.filter().whereEqualTo("campaign_id", campaignId)).execute();
            } else {
                JSObject tag = DB.findOneBySql("SELECT id FROM web_tag WHERE tag_name = '" + appName + "'");
                long tagId = 0L;
                if (tag.hasObjectData()) {
                    tagId = tag.get("id");
                }
                DB.insert("web_ad_campaigns_admob")
                        .put("budget", budget)
                        .put("campaign_name", campaignName)
                        .put("create_time", createDate)
                        .put("account_id", accountId)
                        .put("bidding", bidding)
                        .put("status", status)
                        .put("total_spend", totalspend)
                        .put("total_click", clicked)
                        .put("total_installed", installed)
                        .put("total_impressions", impression)
                        .put("cpa", installed > 0 ? totalspend / installed : 0)
                        .put("ctr", impression > 0 ? clicked * 1.0 / impression : 0)
                        .put("campaign_id", campaignId)
                        .put("tag_id", tagId)
                        .execute();
            }

//            jsObject = DB.simpleScan("`web_ad_campaigns_admob_" + appName.toLowerCase() + "`").select("id").where(DB.filter().whereEqualTo("campaign_id", campaignId))
//                    .execute();
//            if (jsObject.hasObjectData()) {
//                DB.update("`web_ad_campaigns_admob_" + appName.toLowerCase() + "`")
//                        .put("budget", budget)
//                        .put("campaign_name", campaignName)
//                        .put("bidding", bidding)
//                        .put("status", status)
//                        .where(DB.filter().whereEqualTo("campaign_id", campaignId)).execute();
//            } else {
//                DB.insert("`web_ad_campaigns_admob_" + appName.toLowerCase() + "`")
//                        .put("budget", budget)
//                        .put("campaign_name", campaignName)
//                        .put("create_time", createDate)
//                        .put("account_id", accountId)
//                        .put("short_name", shortName)
//                        .put("bidding", bidding)
//                        .put("status", status)
//                        .put("campaign_id", campaignId)
//                        .execute();
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void insertIntoHistoryCountry(String appName, String accountId, String shortName, String date, String campaignId,
                                                 String countryCode, int clicked, double totalspend,
                                                 double installed, int impression) {
        try {
            JSObject record = DB.simpleScan("web_ad_campaigns_country_history_admob").select("id")
                    .where(DB.filter().whereEqualTo("date", date))
                    .and(DB.filter().whereEqualTo("campaign_id", campaignId))
                    .and(DB.filter().whereEqualTo("country_code", countryCode))
                    .execute();
            if (record.hasObjectData()) {
                DB.update("web_ad_campaigns_country_history_admob")
                        .put("total_click", clicked)
                        .put("total_spend", totalspend)
                        .put("total_installed", installed)
                        .put("total_impressions", impression)
                        .put("cpa", installed > 0 ? totalspend / installed : 0)
                        .put("ctr", impression > 0 ? clicked * 1.0 / impression : 0)
                        .where(DB.filter().whereEqualTo("id", record.get("id")))
                        .execute();
            } else {
                DB.insert("web_ad_campaigns_country_history_admob")
                        .put("campaign_id", campaignId)
                        .put("date", date)
                        .put("country_code", countryCode)
                        .put("total_click", clicked)
                        .put("total_spend", totalspend)
                        .put("total_installed", installed)
                        .put("total_impressions", impression)
                        .put("cpa", installed > 0 ? totalspend / installed : 0)
                        .put("ctr", impression > 0 ? clicked * 1.0 / impression : 0)
                        .execute();
            }

            record = DB.simpleScan("`web_ad_campaigns_country_history_admob_" + appName.toLowerCase() + "`").select("id")
                    .where(DB.filter().whereEqualTo("date", date))
                    .and(DB.filter().whereEqualTo("campaign_id", campaignId))
                    .and(DB.filter().whereEqualTo("country_code", countryCode))
                    .execute();
            if (record.hasObjectData()) {
                DB.update("`web_ad_campaigns_country_history_admob_" + appName.toLowerCase() + "`")
                        .put("total_click", clicked)
                        .put("total_spend", totalspend)
                        .put("total_installed", installed)
                        .put("total_impressions", impression)
                        .put("cpa", installed > 0 ? totalspend / installed : 0)
                        .put("ctr", impression > 0 ? clicked * 1.0 / impression : 0)
                        .where(DB.filter().whereEqualTo("id", record.get("id")))
                        .execute();
            } else {
                DB.insert("`web_ad_campaigns_country_history_admob_" + appName.toLowerCase() + "`")
                        .put("campaign_id", campaignId)
                        .put("date", date)
                        .put("account_id", accountId)
                        .put("short_name", shortName)
                        .put("country_code", countryCode)
                        .put("total_click", clicked)
                        .put("total_spend", totalspend)
                        .put("total_installed", installed)
                        .put("total_impressions", impression)
                        .put("cpa", installed > 0 ? totalspend / installed : 0)
                        .put("ctr", impression > 0 ? clicked * 1.0 / impression : 0)
                        .execute();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static boolean createCampaign(AdCampaignItemAdmob campaignItem) throws Exception {
        try {
            AdWordsSession session = getAdWordsSession(campaignItem.account_id);
            AdWordsServices adWordsServices = new AdWordsServices();

            if (campaignItem.campaign_id != null && !campaignItem.campaign_id.isEmpty()) {
                Campaign oldCampaign = new Campaign();
                oldCampaign.setId(Utils.parseLong(campaignItem.campaign_id, 0));
                return setCampaignTargetingCriteria(oldCampaign, adWordsServices, session, campaignItem);
            }

            // Get the BudgetService.
            BudgetServiceInterface budgetService =
                    adWordsServices.get(session, BudgetServiceInterface.class);

// Create the campaign budget.
            Budget budget = new Budget();
            budget.setName("App Budget #" + System.currentTimeMillis());
            Money budgetAmount = new Money();
            budgetAmount.setMicroAmount((long) (Utils.parseDouble(campaignItem.bugdet, 0) * 1000000));
            budget.setAmount(budgetAmount);
            budget.setDeliveryMethod(BudgetBudgetDeliveryMethod.STANDARD);

// Universal app campaigns don't support shared budgets.
            budget.setIsExplicitlyShared(false);
            BudgetOperation budgetOperation = new BudgetOperation();
            budgetOperation.setOperand(budget);
            budgetOperation.setOperator(Operator.ADD);

// Add the budget
            Budget addedBudget = budgetService.mutate(new BudgetOperation[]{budgetOperation}).getValue(0);

            // Get the CampaignService.
            CampaignServiceInterface campaignService =
                    adWordsServices.get(session, CampaignServiceInterface.class);

// Create the campaign.
            Campaign campaign = new Campaign();
            campaign.setName(campaignItem.campaign_name);

// Recommendation: Set the campaign to PAUSED when creating it to prevent
// the ads from immediately serving. Set to ENABLED once you've added
// targeting and the ads are ready to serve.
            //Adwords默认暂停系列，都准备好了之后再重新开启系列
            campaign.setStatus(CampaignStatus.PAUSED);

// Set the advertising channel and subchannel types for universal app campaigns.
            campaign.setAdvertisingChannelType(AdvertisingChannelType.MULTI_CHANNEL);
            campaign.setAdvertisingChannelSubType(AdvertisingChannelSubType.UNIVERSAL_APP_CAMPAIGN);

// Set the campaign's bidding strategy. universal app campaigns
// only support TARGET_CPA bidding strategy.
            BiddingStrategyConfiguration biddingConfig = new BiddingStrategyConfiguration();
            biddingConfig.setBiddingStrategyType(BiddingStrategyType.TARGET_CPA);

// Set the target CPA to $1 / app install.
            TargetCpaBiddingScheme biddingScheme = new TargetCpaBiddingScheme();
            biddingScheme.setTargetCpa(new Money());
            biddingScheme.getTargetCpa().setMicroAmount((long) (Utils.parseDouble(campaignItem.bidding, 0) * 1000000));

            biddingConfig.setBiddingScheme(biddingScheme);
            campaign.setBiddingStrategyConfiguration(biddingConfig);

// Set the campaign's budget.
            campaign.setBudget(new Budget());
            campaign.getBudget().setBudgetId(addedBudget.getBudgetId());

// Optional: Set the start date.
//        campaign.setStartDate(new DateTime().plusDays(-2).toString("yyyyMMdd"));

// Optional: Set the end date.
//        campaign.setEndDate(new DateTime().plusYears(10).toString("yyyyMMdd"));

            // Set the campaign's assets and ad text ideas. These values will be used to
// generate ads.
            UniversalAppCampaignSetting universalAppSetting = new UniversalAppCampaignSetting();
            universalAppSetting.setAppId(campaignItem.app_id);
            universalAppSetting.setAppVendor(MobileApplicationVendor.VENDOR_GOOGLE_MARKET);
            universalAppSetting.setDescription1(campaignItem.message1.length() > 25 ? campaignItem.message1.substring(0, 24) : campaignItem.message1);
            universalAppSetting.setDescription2(campaignItem.message2.length() > 25 ? campaignItem.message2.substring(0, 24) : campaignItem.message2);
            universalAppSetting.setDescription3(campaignItem.message3.length() > 25 ? campaignItem.message3.substring(0, 24) : campaignItem.message3);
            universalAppSetting.setDescription4(campaignItem.message4.length() > 25 ? campaignItem.message4.substring(0, 24) : campaignItem.message4);

            Collection<File> uploadImages = FileUtils.listFiles(new File(campaignItem.image_path), null, false);
            if (uploadImages.size() > 0) {
                MediaServiceInterface mediaService =
                        adWordsServices.get(session, MediaServiceInterface.class);

                ArrayList<Image> images = new ArrayList<>();
                ArrayList<String> md5List = new ArrayList<>();
                ArrayList<Long> cachedMediaIds = new ArrayList<>();
                for (File file : uploadImages) {
//                    new String[]{"jpg", "jpeg", "png"}
                    String fileName = file.getAbsolutePath().toLowerCase();
                    if (fileName.endsWith("gif") || fileName.endsWith("jpg") || fileName.endsWith("jpeg") || fileName.endsWith("png")) {
                        // Create image.
                        String md5 = Utils.getMD5(file);
                        if (md5List.indexOf(md5) != -1) {
                            System.out.println("已经存在同样的图片了");
                            continue;
                        }
                        JSObject one = DB.simpleScan("ad_images").select("image_hash").where(DB.filter().whereEqualTo("image_file_md5", md5))
                                .and(DB.filter().whereEqualTo("account_id", campaignItem.account_id))
                                .execute();
                        if (one.hasObjectData()) {
                            long mediaId = Utils.parseLong(one.get("image_hash"), 0);
                            if (mediaId > 0) {
                                cachedMediaIds.add(mediaId);
                                continue;
                            }
                        }
                        md5List.add(md5);

                        Image image = new Image();
                        image.setData(
                                com.google.api.ads.common.lib.utils.Media.getMediaDataFromFile(file));
                        image.setType(MediaMediaType.IMAGE);

                        images.add(image);
                    }
                }
                if (images.size() > 0 || cachedMediaIds.size() > 0) {
                    long[] mediaIds = null;
                    if (cachedMediaIds.size() > 0) {
                        mediaIds = new long[cachedMediaIds.size()];
                        for (int mI = 0; mI < cachedMediaIds.size(); mI++) {
                            mediaIds[mI] = cachedMediaIds.get(mI);
                        }
                    }
                    if (images.size() > 0) {
                        Media[] media = new Media[images.size()];
                        for (int i = 0; i < images.size(); i++) {
                            media[i] = images.get(i);
                        }
                        // Upload image.
                        Media[] result = mediaService.upload(media);

                        // Optional: You can set up to 10 image assets for your campaign.
                        // See UploadImage.java for an example on how to upload images.
                        int cacheLen = (mediaIds != null ? mediaIds.length : 0);
                        long[] mediaIdsNew = new long[cacheLen + result.length];
                        for (int i = 0; cacheLen > 0 && i < mediaIds.length; i++) {
                            mediaIdsNew[i] = mediaIds[i];
                        }
                        Logger logger = Logger.getLogger("FILE");
                        for (int i = 0; i < result.length; i++) {
                            mediaIdsNew[cacheLen + i] = result[i].getMediaId();
                            DB.insert("ad_images").put("image_file_md5", md5List.get(i))
                                    .put("account_id", campaignItem.account_id)
                                    .put("image_hash", result[i].getMediaId() + "")
                                    .execute();
                            logger.debug("upload file, md5=" + md5List.get(i) + ", id=" + result[i].getMediaId() + ", accountId=" + campaignItem.account_id);
                        }
                        mediaIds = mediaIdsNew;
                    }
                    if (mediaIds != null) {
                        universalAppSetting.setImageMediaIds(mediaIds);
                    }
                }
            }

            //如果存在转化ID，则进行应用类转化操作，否则进行安装量转化操作
            if (campaignItem.conversion_id.isEmpty()) {
                universalAppSetting.setUniversalAppBiddingStrategyGoalType(
                        UniversalAppBiddingStrategyGoalType.OPTIMIZE_FOR_INSTALL_CONVERSION_VOLUME);
            } else {
                long conversionId = Utils.parseLong(campaignItem.conversion_id, 0);
                if (conversionId != 0) {
                    universalAppSetting.setUniversalAppBiddingStrategyGoalType(
                            UniversalAppBiddingStrategyGoalType.OPTIMIZE_FOR_TARGET_IN_APP_CONVERSION);
                    SelectiveOptimization selectiveOptimization = new SelectiveOptimization();
                    selectiveOptimization.setConversionTypeIds(new long[]{conversionId});
                    campaign.setSelectiveOptimization(selectiveOptimization);
                }
            }
            // Optimize this campaign for getting new users for your app.

// If you select the OPTIMIZE_FOR_IN_APP_CONVERSION_VOLUME goal type, then also specify
// your in-app conversion types so AdWords can focus your campaign on people who are
// most likely to complete the corresponding in-app actions.
// Conversion type IDs can be retrieved using ConversionTrackerService.get.
//
// campaign.selectiveOptimization = new SelectiveOptimization();
// campaign.selectiveOptimization.conversionTypeIds =
//    new long[] { INSERT_CONVERSION_TYPE_ID_1_HERE, INSERT_CONVERSION_TYPE_ID_2_HERE };

// Optional: Set the campaign settings for Advanced location options.
            GeoTargetTypeSetting geoSetting = new GeoTargetTypeSetting();
            geoSetting.setPositiveGeoTargetType(
                    GeoTargetTypeSettingPositiveGeoTargetType.DONT_CARE);
            geoSetting.setNegativeGeoTargetType(GeoTargetTypeSettingNegativeGeoTargetType.DONT_CARE);

            campaign.setSettings(new Setting[]{universalAppSetting, geoSetting});

            // Create the operation.
            CampaignOperation operation = new CampaignOperation();
            operation.setOperand(campaign);
            operation.setOperator(Operator.ADD);

            CampaignOperation[] operations = new CampaignOperation[]{operation};

// Add the campaign.
            CampaignReturnValue result = campaignService.mutate(operations);

// Display the results.
            for (Campaign newCampaign : result.getValue()) {
                System.out.printf(
                        "Universal app campaign with name '%s' and ID %d was added.%n",
                        newCampaign.getName(), newCampaign.getId());

                campaignItem.campaign_id = newCampaign.getId() + "";
                if (campaignItem.tag_name != null) {
                    JSObject jsObject = DB.simpleScan("web_tag").select("id").where(DB.filter().whereEqualTo("tag_name", campaignItem.tag_name)).execute();
                    if (jsObject.hasObjectData()) {
                        long tagId = jsObject.get("id");
                        DB.insert("web_ad_campaign_tag_admob_rel").put("tag_id", tagId).put("campaign_id", campaignItem.campaign_id).execute();
                    }
                }

                String sql = "update ad_campaigns_admob set campaign_id=? where id=" + campaignItem.id;
                DB.updateBySql(sql, campaignItem.campaign_id);
                return setCampaignTargetingCriteria(newCampaign, adWordsServices, session, campaignItem);
            }
        } catch (ApiException apiException) {
            ApiError[] errorArr = apiException.getErrors();
            if (errorArr != null) {
                String errorsStr = "";
                for (int i = 0; i < errorArr.length; i++) {
                    ApiError error = errorArr[i];
                    errorsStr += errorArr[i].getErrorString();
                    System.out.println("APiError: " + errorArr[i].getErrorString());

                    //用于减慢速度
                    if (error instanceof RateExceededError) {
                        RateExceededError rateExceeded = (RateExceededError) error;
                        Thread.sleep(rateExceeded.getRetryAfterSeconds() * 1000);
                    } else if (error instanceof InternalApiError) {
                        Thread.sleep(30000);
                    }

                }
                if (errorsStr.contains("SettingError.MEDIA_INCOMPATIBLE_FOR_UNIVERSAL_APP_CAMPAIGN") ||
                        errorsStr.contains("EntityCountLimitExceeded.ACCOUNT_LIMIT")) {
                    String sql = "update ad_campaigns_admob set failed_count=3, last_error_message=? where id=" + campaignItem.id;
                    DB.updateBySql(sql, errorsStr);
                } else {
                    String sql = "update ad_campaigns_admob set failed_count=failed_count+1, last_error_message=? where id=" + campaignItem.id;
                    DB.updateBySql(sql, errorsStr);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            String sql = "update ad_campaigns_admob set failed_count=failed_count+1, last_error_message=? where id=" + campaignItem.id;
            DB.updateBySql(sql, ex.getMessage());
        }


        return false;
    }

    private static boolean setCampaignTargetingCriteria(Campaign campaign, AdWordsServices adWordsServices,
                                                        AdWordsSession session, AdCampaignItemAdmob campaignItem) throws RemoteException {
        // Optional: Set the campaign's location and language targeting. No other targeting
        // criteria can be used for universal app campaigns.
        // Get the CampaignCriterionService.
        CampaignCriterionServiceInterface campaignCriterionService =
                adWordsServices.get(session, CampaignCriterionServiceInterface.class);

        // Create locations. The IDs can be found in the documentation or
        // retrieved with the LocationCriterionService.
        List<Criterion> criteria = new ArrayList<>();
        List<Criterion> excludedCriteria = new ArrayList<>();
        ArrayList<Long> excludedLocationIds = new ArrayList<>();
        JsonParser parser = new JsonParser();
        JsonObject adwordsCountryCodes = parser.parse(DefaultConfig.ADWORDS_COUNTRY_CODES).getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> entries = adwordsCountryCodes.entrySet();
        if (campaignItem.excluded_region != null && !campaignItem.excluded_region.isEmpty()) {
            String[] codes = campaignItem.excluded_region.split(",");
            List<String> codes1 = Arrays.asList(campaignItem.country_region.split(","));
            for (String code : codes) {
                if (codes1.indexOf(code) != -1) continue;
                for (Map.Entry<String, JsonElement> entry : entries) {
                    if (entry.getValue().getAsString().equals(code)) {
                        Location location = new Location();
                        location.setId(Utils.parseLong(entry.getKey(), 0));
                        excludedLocationIds.add(location.getId());
                        excludedCriteria.add(location);
                    }
                }
            }
        }

        if (campaignItem.country_region != null && !campaignItem.country_region.isEmpty()) {
            String[] codes = campaignItem.country_region.split(",");
            for (String code : codes) {
                for (Map.Entry<String, JsonElement> entry : entries) {
                    if (entry.getValue().getAsString().equals(code)) {
                        Long locationId = Utils.parseLong(entry.getKey(), 0);
                        if (excludedLocationIds.indexOf(locationId) != -1) {
                            continue;
                        }
                        Location location = new Location();
                        location.setId(locationId);
                        criteria.add(location);
                    }
                }
            }
            if (criteria.size() == 0) {
                Campaign changeCampaign = new Campaign();
                changeCampaign.setId(campaign.getId());
                changeCampaign.setStatus(CampaignStatus.PAUSED);

                CampaignOperation operation = new CampaignOperation();
                operation.setOperand(changeCampaign);
                operation.setOperator(Operator.SET);

                CampaignOperation[] operations = new CampaignOperation[]{operation};

                CampaignServiceInterface campaignService =
                        adWordsServices.get(session, CampaignServiceInterface.class);

                CampaignReturnValue result = campaignService.mutate(operations);

                if (result != null && result.getValue() != null) {
                    // Display added campaign targets.
                    return true;
                }
            }
        } else {
            for (Map.Entry<String, JsonElement> entry : entries) {
                Long locationId = Utils.parseLong(entry.getKey(), 0);
                if (excludedLocationIds.indexOf(locationId) != -1) {
                    continue;
                }
                Location location = new Location();
                location.setId(locationId);
                criteria.add(location);
            }
        }
        if (campaignItem.language != null && !campaignItem.language.isEmpty()) {
            String[] codes = campaignItem.language.split(",");
            for (String code : codes) {
                long id = Utils.parseLong(code, 0);
                if (id == 0) continue;
                Language language = new Language();
                language.setId(Utils.parseLong(code, 0));
                criteria.add(language);
            }
        }

        // Create operations to add each of the criteria above.
        List<CampaignCriterionOperation> operations = Lists.<CampaignCriterionOperation>newArrayList();
        for (Criterion criterion : criteria) {
            CampaignCriterionOperation campaignCriterionOperation = new CampaignCriterionOperation();

            CampaignCriterion campaignCriterion = new CampaignCriterion();
            campaignCriterion.setCampaignId(campaign.getId());
            campaignCriterion.setCriterion(criterion);
            campaignCriterionOperation.setOperand(campaignCriterion);

            campaignCriterionOperation.setOperator(Operator.ADD);

            operations.add(campaignCriterionOperation);
        }
        for (Criterion criterion : excludedCriteria) {
            CampaignCriterionOperation campaignCriterionOperation = new CampaignCriterionOperation();

            CampaignCriterion campaignCriterion = new NegativeCampaignCriterion();
            campaignCriterion.setCampaignId(campaign.getId());
            campaignCriterion.setCriterion(criterion);
            campaignCriterionOperation.setOperand(campaignCriterion);

            campaignCriterionOperation.setOperator(Operator.ADD);

            operations.add(campaignCriterionOperation);
        }

        // Set the campaign targets.
        CampaignCriterionReturnValue returnValue =
                campaignCriterionService.mutate(
                        operations.toArray(new CampaignCriterionOperation[operations.size()]));

        if (returnValue != null && returnValue.getValue() != null) {
            Campaign changeCampaign = new Campaign();
            changeCampaign.setId(campaign.getId());
            changeCampaign.setStatus(CampaignStatus.ENABLED);

            CampaignOperation operation = new CampaignOperation();
            operation.setOperand(changeCampaign);
            operation.setOperator(Operator.SET);

            CampaignOperation[] enableOp = new CampaignOperation[]{operation};

            CampaignServiceInterface campaignService =
                    adWordsServices.get(session, CampaignServiceInterface.class);

            CampaignReturnValue result = campaignService.mutate(enableOp);

            if (result != null && result.getValue() != null) {
                return true;
            }
        }

        return false;
    }

    public static boolean batchCloseCampaigns(ArrayList<AdBatchChangeItem> items, String accountId) throws Exception {
        ArrayList<AdBatchChangeItem> operationItems = new ArrayList<>();

        try {
            Set<String> changeIds = new HashSet<>();

            AdWordsSession session = getAdWordsSession(accountId);
            AdWordsServices adWordsServices = new AdWordsServices();

            ArrayList<CampaignOperation> operations = new ArrayList<>();

            for (int i = 0; i < items.size(); i++) {
                AdBatchChangeItem item = items.get(i);
                if (changeIds.contains(item.campaignId)) {
                    continue;
                }
                changeIds.add(item.campaignId);

                operationItems.add(item);

                Campaign campaign = new Campaign();
                campaign.setId(Long.parseLong(item.campaignId));
                campaign.setStatus(CampaignStatus.PAUSED);

                CampaignOperation operation = new CampaignOperation();
                operation.setOperand(campaign);
                operation.setOperator(Operator.SET);

                operations.add(operation);
            }

            if (operations.size() > 0) {
                CampaignServiceInterface campaignService =
                        adWordsServices.get(session, CampaignServiceInterface.class);

                CampaignOperation[] operationArr = new CampaignOperation[operations.size()];
                CampaignReturnValue result = campaignService.mutate(operations.toArray(operationArr));
                if (result != null && result.getValue() != null) {
                    Campaign[] campaigns = result.getValue();
                    for (int i = 0; i < campaigns.length; i++) {
                        if (campaigns[i].getStatus() == CampaignStatus.PAUSED) {
                            for (int j = 0; j < items.size(); j++) {
                                if (items.get(j).campaignId.equals(campaigns[i].getId() + "")) {
                                    DB.update("web_ad_batch_change_campaigns")
                                            .put("update_time", DateUtil.getNowTime())
                                            .put("success", 1)
                                            .where(DB.filter().whereEqualTo("id", items.get(j).id))
                                            .execute();
                                }
                            }
                        }
                    }
                } else {
                    return false;
                }
            }
        } catch (ApiException ex) {
            ApiError[] errors = ex.getErrors();
            if (errors != null) {
                String error = "";
                for (int i = 0; i < errors.length; i++) {
                    error = errors[i].getErrorString();
                    System.out.println("APiError: " + errors[i].getErrorString());

                    int index = errors[i].getFieldPathElements()[0].getIndex();
                    if (error.contains("OperationAccessDenied.OPERATION_NOT_PERMITTED_FOR_REMOVED_ENTITY")) {
                        insertToDeleted(operationItems.get(index).campaignId);
                        DB.updateBySql("update web_ad_batch_change_campaigns set success=1, last_error_message=? where id=?", error, operationItems.get(index).id);
                    } else {
                        DB.updateBySql("update web_ad_batch_change_campaigns set failed_count=failed_count+1, last_error_message=? where id=?", error, operationItems.get(index).id);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

    public static void checkCampaignCountry() {
        try {
            JsonParser parser = new JsonParser();
            JsonObject adwordsCountryCodes = parser.parse(DefaultConfig.ADWORDS_COUNTRY_CODES).getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entries = adwordsCountryCodes.entrySet();

            String sql = "select distinct network, campaign_id, account_id, excluded_country from web_ad_batch_change_campaigns where create_time>'2018-01-15' and excluded_country != ''";
            List<JSObject> list = DB.findListBySql(sql);

            for (int i = 0; i < list.size(); i++) {
                JSObject item = list.get(i);

                executors.submit(new Runnable() {
                    @Override
                    public void run() {
                        int offset = 0;

                        String network = item.get("network");
                        String campaignId = item.get("campaign_id");
                        String accountId = item.get("account_id");
                        String excludedCountry = item.get("excluded_country");
                        if (!network.equals("admob")) return;

                        System.out.println("check campaign " + campaignId);
                        ArrayList<Long> includeIdList = new ArrayList<>();

                        try {
                            AdWordsSession session = getAdWordsSession(accountId);
                            AdWordsServices adWordsServices = new AdWordsServices();

                            SelectorBuilder builder = new SelectorBuilder();
                            com.google.api.ads.adwords.axis.v201802.cm.Selector selector = builder
                                    .fields(
                                            CampaignCriterionField.CampaignId,
                                            CampaignCriterionField.Id,
                                            CampaignCriterionField.CriteriaType,
                                            CampaignCriterionField.LocationName)
                                    .in(CampaignCriterionField.CriteriaType, "LOCATION")
//                                    .equals(CampaignCriterionField.IsNegative, "FALSE")
                                    .equals(CampaignCriterionField.CampaignId, campaignId)
                                    .offset(0)
                                    .limit(PAGE_SIZE)
                                    .build();

                            CampaignCriterionPage page = null;
                            do {
                                CampaignCriterionServiceInterface campaignCriterionService =
                                        adWordsServices.get(session, CampaignCriterionServiceInterface.class);

                                page = campaignCriterionService.get(selector);

                                if (page.getEntries() != null) {
                                    boolean found = false;
                                    for (CampaignCriterion campaignCriterion : page.getEntries()) {
                                        if (!campaignCriterion.getIsNegative()) {
                                            for (Map.Entry<String, JsonElement> entry : entries) {
                                                if (!campaignCriterion.getCriterion().getId().equals(Utils.parseLong(entry.getKey(), 0))) {
                                                    continue;
                                                }
                                                String code = entry.getValue().getAsString();
                                                includeIdList.add(campaignCriterion.getCriterion().getId());
                                                if (entry.getKey().equals(campaignCriterion.getCriterion().getId() + "") && code.equals(excludedCountry)) {
                                                    found = true;
                                                }
                                            }
                                        }
                                    }
                                    if (found && includeIdList.size() > 1) {
                                        System.out.println("check found campaignId=" + campaignId + ", accountId=" + accountId + ", country=" + excludedCountry + ", size=" + includeIdList.size());
                                    }
                                } else {
                                    System.out.println("No campaign criteria were found.");
                                }
                                offset += PAGE_SIZE;
                                selector = builder.increaseOffsetBy(PAGE_SIZE).build();
                            } while (offset < page.getTotalNumEntries());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 修改系列状态等
     *
     * @param item
     * @return
     * @throws Exception
     */
    public static boolean changeCampaign(AdBatchChangeItem item) throws Exception {
        try {
            System.out.println("changeCampaign, campaignId=" + item.campaignId);
            if (item.campaignId != null && !item.campaignId.isEmpty()) {
                JSObject removed = DB.simpleScan("ad_app_remove_campaign").select("campaign_id")
                        .where(DB.filter().whereEqualTo("campaign_id", item.campaignId)).execute();
                if (removed.hasObjectData()) {
                    DB.updateBySql("update web_ad_batch_change_campaigns set success=1, last_error_message=? where id=?", "", item.id);
                    return false;
                }

                Campaign campaign = new Campaign();
                campaign.setId(Long.parseLong(item.campaignId));
                boolean needUpdate = false;
                boolean updateCampaignRecord = false;
                AdWordsSession session = getAdWordsSession(item.accountId);
                AdWordsServices adWordsServices = new AdWordsServices();

                Store.Update updateCampaign = DB.update("web_ad_campaigns_admob");
                if (item.campaignName != null && !item.campaignName.isEmpty()) {
                    needUpdate = true;
                    updateCampaignRecord = true;
                    campaign.setName(item.campaignName);
                    updateCampaign.put("campaign_name", item.campaignName);
                }
                CampaignStatus status = null;
                if (item.enabled != -1) {
                    needUpdate = true;
                    updateCampaignRecord = true;
                    status = item.enabled == 1 ? CampaignStatus.ENABLED : CampaignStatus.PAUSED;
                    campaign.setStatus(status);
                    updateCampaign.put("status", status.toString().toLowerCase());
                }

                double budget = Utils.parseDouble(item.bugdet, 0);
                double bidding = Utils.parseDouble(item.bidding, 0);
                if (budget > 0 || bidding > 0 || !item.excludedCountry.isEmpty()) {
                    if (budget > 0) {
                        needUpdate = true;
                        BudgetServiceInterface budgetService =
                                adWordsServices.get(session, BudgetServiceInterface.class);

                        Budget newBudget = new Budget();
                        newBudget.setName("App Budget #" + System.currentTimeMillis());
                        Money budgetAmount = new Money();
                        budgetAmount.setMicroAmount((long) (budget * 1000000));
                        newBudget.setAmount(budgetAmount);
                        newBudget.setDeliveryMethod(BudgetBudgetDeliveryMethod.STANDARD);

                        newBudget.setIsExplicitlyShared(false);
                        BudgetOperation budgetOperation = new BudgetOperation();
                        budgetOperation.setOperand(newBudget);
                        budgetOperation.setOperator(Operator.ADD);

                        Budget addedBudget = budgetService.mutate(new BudgetOperation[]{budgetOperation}).getValue(0);
                        campaign.setBudget(new Budget());
                        campaign.getBudget().setBudgetId(addedBudget.getBudgetId());
                        updateCampaignRecord = true;
                        updateCampaign.put("budget", (long) (budget * 100));
                    }
                    if (bidding > 0) {
                        needUpdate = true;
                        BiddingStrategyConfiguration biddingConfig = new BiddingStrategyConfiguration();
                        biddingConfig.setBiddingStrategyType(BiddingStrategyType.TARGET_CPA);

                        TargetCpaBiddingScheme biddingScheme = new TargetCpaBiddingScheme();
                        biddingScheme.setTargetCpa(new Money());
                        biddingScheme.getTargetCpa().setMicroAmount((long) (bidding * 1000000));

                        biddingConfig.setBiddingScheme(biddingScheme);
                        campaign.setBiddingStrategyConfiguration(biddingConfig);
                        updateCampaignRecord = true;
                        updateCampaign.put("bidding", (long) (bidding * 100));
                    }
                    if (!item.excludedCountry.isEmpty()) {
                        needUpdate = true;
                        CampaignCriterionServiceInterface campaignCriterionService =
                                adWordsServices.get(session, CampaignCriterionServiceInterface.class);

                        int offset = 0;
                        ArrayList<Long> includedList = new ArrayList<>();

                        SelectorBuilder builder = new SelectorBuilder();
                        com.google.api.ads.adwords.axis.v201802.cm.Selector selector = builder
                                .fields(
                                        CampaignCriterionField.CampaignId,
                                        CampaignCriterionField.Id,
                                        CampaignCriterionField.CriteriaType,
                                        CampaignCriterionField.LocationName)
                                .in(CampaignCriterionField.CriteriaType, "LOCATION")
//                                .equals(CampaignCriterionField.IsNegative, "TRUE")
                                .equals(CampaignCriterionField.CampaignId, item.campaignId)
                                .offset(0)
                                .limit(PAGE_SIZE)
                                .build();

                        CampaignCriterionPage page = null;
                        do {
                            page = campaignCriterionService.get(selector);

                            if (page.getEntries() != null) {
                                for (CampaignCriterion campaignCriterion : page.getEntries()) {
                                    if (!campaignCriterion.getIsNegative()) {
                                        includedList.add(campaignCriterion.getCriterion().getId());
                                    }
                                }
                            } else {
                                System.out.println("No campaign criteria were found.");
                            }
                            offset += PAGE_SIZE;
                            selector = builder.increaseOffsetBy(PAGE_SIZE).build();
                        } while (offset < page.getTotalNumEntries());
                        System.out.println("includedList size=" + includedList.size());

                        boolean notFoundCountry = true;
                        List<Criterion> criteria = new ArrayList<>();
                        List<Criterion> excludedCriteria = new ArrayList<>();
                        JsonParser parser = new JsonParser();
                        JsonObject adwordsCountryCodes = parser.parse(DefaultConfig.ADWORDS_COUNTRY_CODES).getAsJsonObject();
                        Set<Map.Entry<String, JsonElement>> entries = adwordsCountryCodes.entrySet();
                        for (Map.Entry<String, JsonElement> entry : entries) {
                            String value = entry.getValue().getAsString();
                            if (value.equals(item.excludedCountry)) {
                                notFoundCountry = false;
                                Long id = Utils.parseLong(entry.getKey(), 0);
                                if (includedList.indexOf(id) == -1) {
                                    System.out.println("Add excluded country. " + value);
                                    Location location = new Location();
                                    location.setId(id);
                                    excludedCriteria.add(location);
                                    if (includedList.size() <= 1) {
                                        campaign.setStatus(CampaignStatus.PAUSED);
                                        updateCampaignRecord = true;
                                        updateCampaign.put("status", CampaignStatus.PAUSED.toString().toLowerCase());
                                    }
                                } else {
                                    System.out.println("Contains excluded country. " + value);
                                    //如果只有一个国家定位，不去掉这个国家，直接改成暂停状态
//                                    Location location = new Location();
//                                    location.setId(id);
//                                    criteria.add(location);
                                    if (includedList.size() <= 1) {
                                        campaign.setStatus(CampaignStatus.PAUSED);
                                        updateCampaignRecord = true;
                                        updateCampaign.put("status", CampaignStatus.PAUSED.toString().toLowerCase());
                                    } else {
                                        Location location = new Location();
                                        location.setId(id);
                                        criteria.add(location);
                                    }
                                }
                                break;
                            }
                        }

                        List<CampaignCriterionOperation> operations = Lists.<CampaignCriterionOperation>newArrayList();
                        for (Criterion criterion : criteria) {
                            CampaignCriterionOperation campaignCriterionOperation = new CampaignCriterionOperation();

                            CampaignCriterion campaignCriterion = new CampaignCriterion();
                            campaignCriterion.setCampaignId(Utils.parseLong(item.campaignId, 0));
                            campaignCriterion.setCriterion(criterion);
                            campaignCriterionOperation.setOperand(campaignCriterion);

                            campaignCriterionOperation.setOperator(Operator.REMOVE);

                            operations.add(campaignCriterionOperation);
                        }
                        for (Criterion criterion : excludedCriteria) {
                            CampaignCriterionOperation campaignCriterionOperation = new CampaignCriterionOperation();

                            CampaignCriterion campaignCriterion = new NegativeCampaignCriterion();
                            campaignCriterion.setCampaignId(Utils.parseLong(item.campaignId, 0));
                            campaignCriterion.setCriterion(criterion);
                            campaignCriterionOperation.setOperand(campaignCriterion);

                            campaignCriterionOperation.setOperator(Operator.ADD);

                            operations.add(campaignCriterionOperation);
                        }
                        if (operations.size() > 0) {
                            CampaignCriterionReturnValue returnValue =
                                    campaignCriterionService.mutate(
                                            operations.toArray(new CampaignCriterionOperation[operations.size()]));

                            if (returnValue != null && returnValue.getValue() != null) {
                                if (includedList.size() > 0) {
                                    includedList.clear();
                                    builder = new SelectorBuilder();
                                    offset = 0;
                                    selector = builder
                                            .fields(
                                                    CampaignCriterionField.CampaignId,
                                                    CampaignCriterionField.Id,
                                                    CampaignCriterionField.CriteriaType,
                                                    CampaignCriterionField.LocationName)
                                            .in(CampaignCriterionField.CriteriaType, "LOCATION")
                                            .equals(CampaignCriterionField.CampaignId, item.campaignId)
                                            .offset(0)
                                            .limit(PAGE_SIZE)
                                            .build();

                                    page = null;
                                    do {
                                        page = campaignCriterionService.get(selector);

                                        if (page.getEntries() != null) {
                                            for (CampaignCriterion campaignCriterion : page.getEntries()) {
                                                if (!campaignCriterion.getIsNegative()) {
                                                    includedList.add(campaignCriterion.getCriterion().getId());
                                                }
                                            }
                                            Thread.sleep(1000);
                                        } else {
                                            System.out.println("No campaign criteria were found.");
                                        }
                                        offset += PAGE_SIZE;
                                        selector = builder.increaseOffsetBy(PAGE_SIZE).build();
                                    } while (offset < page.getTotalNumEntries());
                                    System.out.println("includedList size=" + includedList.size());
                                    if (includedList.size() == 1) {
                                        item.enabled = 0;
                                        item.excludedCountry = "";
                                        changeCampaign(item);
                                    }
                                }
                            }
                        } else {
                            if (notFoundCountry) {
                                DB.updateBySql("update web_ad_batch_change_campaigns set failed_count=failed_count+1, last_error_message=? where id=?", "the excluded target location can not found", item.id);
                                return false;
                            }
                        }
                    }
                }
                if (needUpdate) {
                    CampaignServiceInterface campaignService =
                            adWordsServices.get(session, CampaignServiceInterface.class);

                    CampaignOperation operation = new CampaignOperation();
                    operation.setOperand(campaign);
                    operation.setOperator(Operator.SET);

                    CampaignOperation[] operations = new CampaignOperation[]{operation};

                    CampaignReturnValue result = campaignService.mutate(operations);
                    if (result != null && result.getValue() != null) {
                        if (updateCampaignRecord) {
                            updateCampaign.where(DB.filter().whereEqualTo("campaign_id", item.campaignId));
                            updateCampaign.execute();
                            DB.update("web_ad_batch_change_campaigns")
                                    .put("update_time", DateUtil.getNowTime())
                                    .put("success", 1)
                                    .where(DB.filter().whereEqualTo("id", item.id))
                                    .execute();
                        }

                    } else {
                        DB.updateBySql("update web_ad_batch_change_campaigns set failed_count=failed_count+1, last_error_message=? where id=?", "update failed", item.id);
                        return false;
                    }
                }
            }
            return true;
        } catch (ApiException ex) {
            ApiError[] errors = ex.getErrors();
            if (errors != null) {
                String error = "";
                for (int i = 0; i < errors.length; i++) {
                    error += errors[i].getErrorString();
                    System.out.println("APiError: " + errors[i].getErrorString());
                }
                if (error.contains("OperationAccessDenied.OPERATION_NOT_PERMITTED_FOR_REMOVED_ENTITY")) {
                    insertToDeleted(item.campaignId);
                    DB.updateBySql("update web_ad_batch_change_campaigns set success=1, last_error_message=? where id=?", error, item.id);
                } else {
                    DB.updateBySql("update web_ad_batch_change_campaigns set failed_count=failed_count+1, last_error_message=? where id=?", error, item.id);
                }
            }
            return false;
        } catch (Exception ex) {
            DB.updateBySql("update web_ad_batch_change_campaigns set failed_count=failed_count+1, last_error_message=? where id=?", ex.getMessage(), item.id);
            ex.printStackTrace();
            return false;
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

//    public static void removeCampaigns(List<JSObject> campaignList) throws Exception {
//        if(campaignList != null && campaignList.size() > 0){
//            for(JSObject campaign : campaignList){
//                String campaignId = campaign.get("campaign_id");
//
//                String accountId = campaign.get("account_id");
//                try {
//                    AdWordsSession session = getAdWordsSession(accountId);
//                    AdWordsServices adWordsServices = new AdWordsServices();
//
//                    Campaign currCampaign = new Campaign();
//                    currCampaign.setId(Long.parseLong(campaignId));
//                    currCampaign.setStatus(CampaignStatus.REMOVED);
//
//                    CampaignOperation operation = new CampaignOperation();
//                    operation.setOperand(currCampaign);
//                    operation.setOperator(Operator.SET);
//
//                    CampaignOperation[] operations = new CampaignOperation[]{operation};
//
//                    CampaignServiceInterface campaignService =
//                            adWordsServices.get(session, CampaignServiceInterface.class);
//                    CampaignReturnValue result = campaignService.mutate(operations);
//                    if (result != null && result.getValue() != null) {
//                        System.out.println("更新广告系列成功: " + campaignId);
//                        DB.update("web_ad_campaigns_admob")
//                                .put("status", "removed")
//                                .where(DB.filter().whereEqualTo("campaign_id", campaignId))
//                                .execute();
//                    }
//                } catch (Exception ex) {
//                    System.out.println("更新广告系列失败: " + campaignId);
//                }
//            }
//        }
//    }
//
//    public static void removeCampaignByCampaignId(String campaignId) throws Exception {
//        String accountId = null;
//        try {
//            JSObject record = DB.simpleScan("web_ad_campaigns_admob").select("account_id")
//                    .where(DB.filter().whereEqualTo("campaign_id", campaignId)).execute();
//            if (record.hasObjectData()) {
//                accountId = record.get("account_id");
//            } else {
//                record = DB.simpleScan("ad_campaigns_admob").select("account_id")
//                        .where(DB.filter().whereEqualTo("campaign_id", campaignId)).execute();
//                if (record.hasObjectData()) {
//                    accountId = record.get("account_id");
//                }
//            }
//        } catch (Exception ex) {
//        }
//        if (accountId == null) {
//            System.out.println("accountId is null, 更新广告系列失败: " + campaignId);
//            return;
//        }
//
//        try {
//            AdWordsSession session = getAdWordsSession(accountId);
//            AdWordsServices adWordsServices = new AdWordsServices();
//
//            Campaign campaign = new Campaign();
//            campaign.setId(Long.parseLong(campaignId));
//            campaign.setStatus(CampaignStatus.REMOVED);
//
//            CampaignOperation operation = new CampaignOperation();
//            operation.setOperand(campaign);
//            operation.setOperator(Operator.SET);
//
//            CampaignOperation[] operations = new CampaignOperation[]{operation};
//
//            CampaignServiceInterface campaignService =
//                    adWordsServices.get(session, CampaignServiceInterface.class);
//            CampaignReturnValue result = campaignService.mutate(operations);
//            if (result != null && result.getValue() != null) {
//                System.out.println("更新广告系列成功: " + campaignId);
//            }
//        } catch (Exception ex) {
//            System.out.println("更新广告系列失败: " + campaignId);
//        }
//    }

    /**
     * 根据账号批量移除系列
     *
     * @param campaignIdList 符合移除条件的系列ID
     * @param accountId      账号ID
     * @throws Exception
     */
    public static void batchRemoveCampaigns(ArrayList<String> campaignIdList, String accountId) throws Exception {
        DB.init();

        DefaultConfig.setProxy();
        try {
            Set<String> changeIds = new HashSet<>();

            AdWordsSession session = getAdWordsSession(accountId);
            AdWordsServices adWordsServices = new AdWordsServices();

            ArrayList<CampaignOperation> operations = new ArrayList<>();
            if (campaignIdList != null && campaignIdList.size() > 0) {
                for (int i = 1, len = campaignIdList.size(); i <= len; i++) {
                    String campaignId = campaignIdList.get(i - 1);
                    if (changeIds.contains(campaignId)) {
                        continue;
                    }
                    changeIds.add(campaignId);
                    Campaign campaign = new Campaign();
                    campaign.setId(Long.parseLong(campaignId));
                    campaign.setStatus(CampaignStatus.REMOVED);

                    CampaignOperation operation = new CampaignOperation();
                    operation.setOperand(campaign);
                    operation.setOperator(Operator.SET);

                    operations.add(operation);

                    //每1000条执行一次，最后不够1000条的再运行一次
                    if (i % 1000 == 0 || i == len) {
                        if (operations.size() > 0) {
                            CampaignServiceInterface campaignService =
                                    adWordsServices.get(session, CampaignServiceInterface.class);

                            CampaignReturnValue result = null;
                            try {
                                CampaignOperation[] operationArr = new CampaignOperation[operations.size()];
                                result = campaignService.mutate(operations.toArray(operationArr));
                            } catch (ApiException ex) {
                                ApiError[] errors = ex.getErrors();
                                if (errors != null) {
                                    for (int x = 0; x < errors.length; x++) {
                                        System.out.println("APiError: " + errors[x].getErrorString());
                                    }
                                }
                            }
                            if (result != null && result.getValue() != null) {
                                Campaign[] campaigns = result.getValue();
                                for (int s = 0; s < campaigns.length; s++) {
                                    if (campaigns[s].getStatus() == CampaignStatus.REMOVED) {
                                        for (int j = 0; j < campaignIdList.size(); j++) {
                                            String currCampaignId = campaignIdList.get(j);
                                            if (campaignId.equals(campaigns[s].getId() + "")) {
                                                DB.update("web_ad_campaigns_admob")
                                                        .put("status", "removed")
                                                        .where(DB.filter().whereEqualTo("campaign_id", currCampaignId))
                                                        .execute();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        operations = new ArrayList<>();
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void batchRemoveBudgets(String accountId) throws Exception {
        try {
            AdWordsSession session = getAdWordsSession(accountId);
            AdWordsServices adWordsServices = new AdWordsServices();

            BudgetServiceInterface budgetService =
                    adWordsServices.get(session, BudgetServiceInterface.class);

            SelectorBuilder builder = new SelectorBuilder();
            com.google.api.ads.adwords.axis.v201802.cm.Selector selector = builder
                    .fields(
                            BudgetField.BudgetId,
                            BudgetField.BudgetStatus,
                            BudgetField.BudgetReferenceCount)
                    .equals(BudgetField.BudgetStatus, "ENABLED")
                    .equals(BudgetField.BudgetReferenceCount, "0")
                    .offset(0)
                    .limit(PAGE_SIZE)
                    .build();

            int offset = 0;
            BudgetPage budgetPage = null;
            do {
                budgetPage = budgetService.get(selector);

                if (budgetPage.getEntries() != null) {
                    ArrayList<BudgetOperation> operations = new ArrayList<>();
                    for (Budget budget : budgetPage.getEntries()) {
                        System.out.println("budget=" + budget.getBudgetId() + ", status=" + budget.getStatus() + ", referenceCount=" + budget.getReferenceCount());
                        budget.setStatus(BudgetBudgetStatus.REMOVED);
                        BudgetOperation operation = new BudgetOperation();
                        operation.setOperand(budget);
                        operation.setOperator(Operator.REMOVE);
                        operations.add(operation);
                    }
                    if (operations.size() > 0) {
                        try {
                            BudgetOperation[] operationArr = new BudgetOperation[operations.size()];
                            BudgetReturnValue result = budgetService.mutate(operations.toArray(operationArr));
                        } catch (ApiException ex) {
                            ApiError[] errors = ex.getErrors();
                            if (errors != null) {
                                for (int x = 0; x < errors.length; x++) {
                                    System.out.println("APiError: " + errors[x].getErrorString());
                                }
                            }
                        }
                    }
                }
                offset += PAGE_SIZE;
                selector = builder.increaseOffsetBy(PAGE_SIZE).build();
            } while (offset < budgetPage.getTotalNumEntries());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void enhancedBatchRemoveCampaigns(ArrayList<String> campaignIdList, String accountId) throws Exception {
        try {
            Set<String> changeIds = new HashSet<>();

            AdWordsSession session = getAdWordsSession(accountId);
            AdWordsServices adWordsServices = new AdWordsServices();

            ArrayList<CampaignOperation> operations = new ArrayList<>();
            if (campaignIdList != null && campaignIdList.size() > 0) {
                for (int i = 1; i <= campaignIdList.size(); i++) {
                    String campaignId = campaignIdList.get(i - 1);
                    if (changeIds.contains(campaignId)) {
                        continue;
                    }
                    changeIds.add(campaignId);
                    Campaign campaign = new Campaign();
                    campaign.setId(Long.parseLong(campaignId));
                    campaign.setStatus(CampaignStatus.REMOVED);

                    CampaignOperation operation = new CampaignOperation();
                    operation.setOperand(campaign);
                    operation.setOperator(Operator.SET);

                    operations.add(operation);
                    if (i % 1000 == 0) {
                        if (operations.size() > 0) {
                            CampaignServiceInterface campaignService =
                                    adWordsServices.get(session, CampaignServiceInterface.class);

                            CampaignReturnValue result = null;
                            try {
                                CampaignOperation[] operationArr = new CampaignOperation[operations.size()];
                                result = campaignService.mutate(operations.toArray(operationArr));
                            } catch (ApiException ex) {
                                ApiError[] errors = ex.getErrors();
                                if (errors != null) {
                                    for (int x = 0; x < errors.length; x++) {
                                        System.out.println("APiError: " + errors[x].getErrorString());
                                    }
                                }
                            }
                        }
                        operations = new ArrayList<>();
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void batchRemoveCampaignsByApp(ArrayList<String> campaignIdList, String accountId, String appName) throws Exception {
        try {
            Set<String> changeIds = new HashSet<>();

            AdWordsSession session = getAdWordsSession(accountId);
            AdWordsServices adWordsServices = new AdWordsServices();

            ArrayList<CampaignOperation> operations = new ArrayList<>();
            if (campaignIdList != null && campaignIdList.size() > 0) {
                for (int i = 1; i <= campaignIdList.size(); i++) {
                    String campaignId = campaignIdList.get(i - 1);
                    if (changeIds.contains(campaignId)) {
                        continue;
                    }
                    changeIds.add(campaignId);
                    Campaign campaign = new Campaign();
                    campaign.setId(Long.parseLong(campaignId));
                    campaign.setStatus(CampaignStatus.REMOVED);

                    CampaignOperation operation = new CampaignOperation();
                    operation.setOperand(campaign);
                    operation.setOperator(Operator.SET);

                    operations.add(operation);
                    if (i % 1000 == 0) {
                        if (operations.size() > 0) {
                            CampaignServiceInterface campaignService =
                                    adWordsServices.get(session, CampaignServiceInterface.class);

                            CampaignReturnValue result = null;
                            try {
                                CampaignOperation[] operationArr = new CampaignOperation[operations.size()];
                                result = campaignService.mutate(operations.toArray(operationArr));
                            } catch (ApiException ex) {
                                ApiError[] errors = ex.getErrors();
                                if (errors != null) {
                                    for (int x = 0; x < errors.length; x++) {
                                        System.out.println("APiError: " + errors[x].getErrorString());
                                    }
                                }
                            }
                            if (result != null && result.getValue() != null) {
                                Campaign[] campaigns = result.getValue();
                                for (int s = 0; s < campaigns.length; s++) {
                                    if (campaigns[s].getStatus() == CampaignStatus.REMOVED) {
                                        for (int j = 0; j < campaignIdList.size(); j++) {
                                            String currCampaignId = campaignIdList.get(j);
                                            if (campaignId.equals(campaigns[s].getId() + "")) {
                                                DB.update("`web_ad_campaigns_admob_" + appName + "`")
                                                        .put("status", "removed")
                                                        .where(DB.filter().whereEqualTo("campaign_id", campaignId))
                                                        .execute();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        operations = new ArrayList<>();
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 批量关闭（暂停）所有的Adwords系列
     *
     * @param items
     * @param accountId 账号
     * @return
     * @throws Exception
     */
    public static boolean batchCloseAllCampaigns(ArrayList<AdBatchChangeItem> items, String accountId) throws Exception {
        ArrayList<AdBatchChangeItem> operationItems = new ArrayList<>();

        try {
            Set<String> changeIds = new HashSet<>();

            AdWordsSession session = getAdWordsSession(accountId);
            AdWordsServices adWordsServices = new AdWordsServices();

            ArrayList<CampaignOperation> operations = new ArrayList<>();

            for (int i = 0; i < items.size(); i++) {
                AdBatchChangeItem item = items.get(i);
                if (changeIds.contains(item.campaignId)) {
                    continue;
                }
                changeIds.add(item.campaignId);

                operationItems.add(item);

                Campaign campaign = new Campaign();
                campaign.setId(Long.parseLong(item.campaignId));
                campaign.setStatus(CampaignStatus.PAUSED);

                CampaignOperation operation = new CampaignOperation();
                operation.setOperand(campaign);
                operation.setOperator(Operator.SET);

                operations.add(operation);
            }

            if (operations.size() > 0) {
                CampaignServiceInterface campaignService =
                        adWordsServices.get(session, CampaignServiceInterface.class);

                CampaignOperation[] operationArr = new CampaignOperation[operations.size()];
                CampaignReturnValue result = campaignService.mutate(operations.toArray(operationArr));
                if (result != null && result.getValue() != null) {
//                    Campaign[] campaigns = result.getValue();
//                    for (int i = 0; i < campaigns.length; i++) {
//                        if (campaigns[i].getStatus() == CampaignStatus.PAUSED) {
//                            for (int j = 0; j < items.size(); j++) {
//                                if (items.get(j).campaignId.equals(campaigns[i].getId() + "")) {
//                                    DB.update("web_ad_batch_change_campaigns")
//                                            .put("update_time", DateUtil.getNowTime())
//                                            .put("success", 1)
//                                            .where(DB.filter().whereEqualTo("id", items.get(j).id))
//                                            .execute();
//                                }
//                            }
//                        }
//                    }
                } else {
                    return false;
                }
            }
        } catch (ApiException ex) {
            ApiError[] errors = ex.getErrors();
            if (errors != null) {
                String error = "";
                for (int i = 0; i < errors.length; i++) {
                    error = errors[i].getErrorString();
                    System.out.println("APiError: " + errors[i].getErrorString());

                    int index = errors[i].getFieldPathElements()[0].getIndex();
                    if (error.contains("OperationAccessDenied.OPERATION_NOT_PERMITTED_FOR_REMOVED_ENTITY")) {
                        insertToDeleted(operationItems.get(index).campaignId);
                        DB.updateBySql("update web_ad_batch_change_campaigns set success=1, last_error_message=? where id=?", error, operationItems.get(index).id);
                    } else {
                        DB.updateBySql("update web_ad_batch_change_campaigns set failed_count=failed_count+1, last_error_message=? where id=?", error, operationItems.get(index).id);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

    /**
     * 多情况下删除FB的广告系列
     *
     * @param accountId
     * @param status
     * @param tag
     * @param country
     */
    public static void deleteAdWordsCampaignMultipleConditions(String accountId, String status, String tag, String country) throws Exception {
        String sql = "";
        if (!(null == accountId)) {//帐户非空
            if (!(null == status)) {//帐号+状态
                sql = "SELECT DISTINCT campaign_id FROM web_ad_campaigns_admob WHERE account_id = '" + accountId + "' AND STATUS = '" + status + "'";
                RemoveAllAdwordsCampaignsByAccountId(sql, accountId);
            } else if (!(null == tag)) {//帐号+应用
                Map<String, Integer> facebookTagDetails = getFacebookTagDetails();
                Integer tag_id = facebookTagDetails.get(tag);
                sql = "SELECT DISTINCT campaign_id FROM web_ad_campaigns_admob WHERE account_id = '" + accountId + "' AND tag_id = '" + tag_id + "'";
                RemoveAllAdwordsCampaignsByAccountId(sql, accountId);
            } else {//帐户下所有系列
                sql = "SELECT DISTINCT campaign_id FROM web_ad_campaigns_admob where account_id = '" + accountId + "'";
                RemoveAllAdwordsCampaignsByAccountId(sql, accountId);
            }
        } else if (!(null == tag) && !(null == country)) {//应用+国家
            Map<String, Integer> facebookTagDetails = getFacebookTagDetails();
            Integer tag_id = facebookTagDetails.get(tag);
            sql = "SELECT  DISTINCT campaign_id FROM web_ad_campaigns_admob WHERE tag_id = '" + tag_id + "' AND campaign_name LIKE '%" + country + "%'";
//            RemoveAllAdwordsCampaignsByAccountId(sql);
        } else {
            System.out.println("参数有误！请重新输入！！");
        }
    }

    /**
     * 通过sql来查询出需要被关闭的campaign_id，并调用archiveCampaigns
     *
     * @param
     * @return
     */
    public static boolean RemoveAllAdwordsCampaignsByAccountId(String sql, String accountId) {
        try {
            List<JSObject> successCampaignList = DB.findListBySql(sql);
            if (successCampaignList.size() > 0) {
                ArrayList<String> adwordsRemoveCampaignList = new ArrayList<>();
                for (JSObject one : successCampaignList) {
                    if (one.hasObjectData()) {
                        String campaignId = one.get("campaign_id");
                        adwordsRemoveCampaignList.add(campaignId);
                    }
                }
                com.bestgo.admanager_tools.AdWordsFetcher.batchRemoveCampaigns(adwordsRemoveCampaignList, accountId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
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
    public static void deleteAdwordsCampaignMultipleConditions(String accountId, String status, String tag, String country) throws Exception {
        String sql = "";
        if (!(null == accountId) && !"".equals(accountId)) {//帐户非空
            if (!(null == status) && !"".equals(status)) {//帐号+状态
                sql = "SELECT DISTINCT campaign_id FROM web_ad_campaigns_admob WHERE account_id = '" + accountId + "' AND STATUS = '" + status + "'";
                removeAllAdwordsCampaignsByAccountId(sql, accountId);
            } else if (!(null == tag) && !"".equals(tag)) {//帐号+应用
                Map<String, Integer> facebookTagDetails = getFacebookTagDetails();
                Integer tag_id = facebookTagDetails.get(tag);
                sql = "SELECT DISTINCT campaign_id FROM web_ad_campaigns_admob WHERE account_id = '" + accountId + "' AND tag_id = '" + tag_id + "'";
                removeAllAdwordsCampaignsByAccountId(sql, accountId);
            } else {//帐户下所有系列
                sql = "SELECT DISTINCT campaign_id FROM web_ad_campaigns_admob where account_id = '" + accountId + "'";
                removeAllAdwordsCampaignsByAccountId(sql, accountId);
            }
        } else if (!(null == tag) && !(null == country) && !"".equals(tag) && !"".equals(country)) {//应用+国家
            Map<String, Integer> facebookTagDetails = getFacebookTagDetails();
            Integer tag_id = facebookTagDetails.get(tag);
            sql = "SELECT  DISTINCT campaign_id FROM web_ad_campaigns_admob WHERE tag_id = '" + tag_id + "' AND campaign_name LIKE '%" + country + "%'";
            removeAllAdwordsCampaignsByAccountId(sql, accountId);
        } else {
            System.out.println("参数有误！请重新输入！！");
        }
    }

    /**
     * 通过sql来查询出需要被关闭的campaign_id，并调用archiveCampaigns
     *
     * @param
     * @return
     */
    public static boolean removeAllAdwordsCampaignsByAccountId(String sql, String accountId) {
        try {
//            String sql = "SELECT DISTINCT campaign_id FROM web_ad_campaigns_admob where account_id = '" + accountId + "' AND status != 'removed' limit 1000";
            List<JSObject> successCampaignList = DB.findListBySql(sql);
            if (successCampaignList.size() > 0) {
                ArrayList<String> adwordsRemoveCampaignList = new ArrayList<>();
                for (JSObject one : successCampaignList) {
                    if (one.hasObjectData()) {
                        String campaignId = one.get("campaign_id");
                        adwordsRemoveCampaignList.add(campaignId);
                    }
                }
                AdWordsFetcher.batchRemoveCampaigns(adwordsRemoveCampaignList, accountId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


}
