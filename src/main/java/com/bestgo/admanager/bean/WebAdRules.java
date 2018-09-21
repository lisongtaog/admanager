package com.bestgo.admanager.bean;

/**
 * @author mengjun
 * @date 2018/9/21 11:02
 * @desc 对应web_ad_rules表
 */
public class WebAdRules {
    private long id;
    private int ruleType; //规则类型
    private String ruleContent; //规则内容
    private int tagId;
    private String tagName;

    public WebAdRules() {
    }

    public WebAdRules(int ruleType, String ruleContent, int tagId, String tagName) {
        this.ruleType = ruleType;
        this.ruleContent = ruleContent;
        this.tagId = tagId;
        this.tagName = tagName;
    }

    public WebAdRules(long id, int ruleType, String ruleContent, int tagId, String tagName) {
        this.id = id;
        this.ruleType = ruleType;
        this.ruleContent = ruleContent;
        this.tagId = tagId;
        this.tagName = tagName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getRuleType() {
        return ruleType;
    }

    public void setRuleType(int ruleType) {
        this.ruleType = ruleType;
    }

    public String getRuleContent() {
        return ruleContent;
    }

    public void setRuleContent(String ruleContent) {
        this.ruleContent = ruleContent;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    @Override
    public String toString() {
        return "AdRules{" +
                "id=" + id +
                ", ruleType=" + ruleType +
                ", ruleContent='" + ruleContent + '\'' +
                ", tagId=" + tagId +
                ", tagName='" + tagName + '\'' +
                '}';
    }
}
