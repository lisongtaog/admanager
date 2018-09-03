package com.bestgo.admanager.bean;

/**
 * Author: mengjun
 * Date: 2018/4/3 20:37
 * Desc: 系列汇总字段
 */
public class AppBean {
    public String name;
    public double total_spend;
    public double seven_days_total_spend;
    public double total_installed;
    public double total_impressions;
    public double total_click;
    public double total_ctr;
    public double total_cpa;
    public double total_cvr;
    public double total_revenue;
    public double seven_days_total_revenue;
    public String network;
    public double ecpm;
    public double incoming;
    public int warningLevel;//警戒级别
    public String appId; //包ID,应用ID
    public double totalNewRevenue; //新用户总收入
    public double roi;//回本率
    public long tagId;
    public double sampleUser;
    public double totalNewUser;
    public double newUserAvgImpressions;
    public double newUserSampleImpression;
    public double sampleNewUserRevenue; //抽样新用户收入
    public double total_impression; //变现端总展示

    @Override
    public String toString() {
        return "AppBean{" +
                "name='" + name + '\'' +
                ", total_spend=" + total_spend +
                ", seven_days_total_spend=" + seven_days_total_spend +
                ", total_installed=" + total_installed +
                ", total_impressions=" + total_impressions +
                ", total_click=" + total_click +
                ", total_ctr=" + total_ctr +
                ", total_cpa=" + total_cpa +
                ", total_cvr=" + total_cvr +
                ", total_revenue=" + total_revenue +
                ", seven_days_total_revenue=" + seven_days_total_revenue +
                ", network='" + network + '\'' +
                ", ecpm=" + ecpm +
                ", incoming=" + incoming +
                ", warningLevel=" + warningLevel +
                ", appId='" + appId + '\'' +
                ", totalNewRevenue=" + totalNewRevenue +
                ", roi=" + roi +
                ", tagId=" + tagId +
                ", sampleUser=" + sampleUser +
                ", totalNewUser=" + totalNewUser +
                ", newUserAvgImpressions=" + newUserAvgImpressions +
                ", newUserSampleImpression=" + newUserSampleImpression +
                ", sampleNewUserRevenue=" + sampleNewUserRevenue +
                ", total_impression=" + total_impression +
                '}';
    }
}
