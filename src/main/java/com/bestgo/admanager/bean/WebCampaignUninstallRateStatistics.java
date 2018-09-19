package com.bestgo.admanager.bean;

/**
 * @author mengjun
 * @date 2018/9/18 19:32
 * @desc web_campaign_uninstall_rate_statistics表对应类
 */
public class WebCampaignUninstallRateStatistics {
    private long id;
    private String installedDate;
    private String appId;
    private String countryCode;
    private String campaignName;
    private double uninstallRate;

    public WebCampaignUninstallRateStatistics() {
    }

    public WebCampaignUninstallRateStatistics(String installedDate, String appId, String countryCode, String campaignName, double uninstallRate) {
        this.installedDate = installedDate;
        this.appId = appId;
        this.countryCode = countryCode;
        this.campaignName = campaignName;
        this.uninstallRate = uninstallRate;
    }

    public WebCampaignUninstallRateStatistics(long id, String installedDate, String appId, String countryCode, String campaignName, double uninstallRate) {
        this.id = id;
        this.installedDate = installedDate;
        this.appId = appId;
        this.countryCode = countryCode;
        this.campaignName = campaignName;
        this.uninstallRate = uninstallRate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getInstalledDate() {
        return installedDate;
    }

    public void setInstalledDate(String installedDate) {
        this.installedDate = installedDate;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public double getUninstallRate() {
        return uninstallRate;
    }

    public void setUninstallRate(double uninstallRate) {
        this.uninstallRate = uninstallRate;
    }

    @Override
    public String toString() {
        return "WebCampaignUninstallRateStatistics{" +
                "id=" + id +
                ", installedDate='" + installedDate + '\'' +
                ", appId='" + appId + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", campaignName='" + campaignName + '\'' +
                ", uninstallRate=" + uninstallRate +
                '}';
    }
}
