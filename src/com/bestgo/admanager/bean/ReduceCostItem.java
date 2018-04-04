package com.bestgo.admanager.bean;

import java.util.HashSet;
import java.util.Set;

/**
 * Author: mengjun
 * Date: 2018/4/3 20:29
 * Desc:
 */
public class ReduceCostItem {
    public String campaignId;
    public String appName;
    public int enabled = -1;
    public double reduceCost;

    public Set<String> countryRemoved = new HashSet<>();
    public Set<String> countryExcluded = new HashSet<>();
}
