package com.bestgo.admanager.servlet;

import com.alibaba.fastjson.JSONObject;
import com.bestgo.admanager.bean.WebCampaignUninstallRateStatistics;
import com.bestgo.admanager.utils.JedisPoolUtil;
import com.bestgo.admanager.utils.NumberUtil;
import com.bestgo.admanager.utils.Utils;
import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;
import com.google.gson.JsonObject;
import org.apache.taglibs.standard.tag.common.core.Util;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mengjun
 * @date 2018/9/19 17:12
 * @desc
 */
@WebServlet(name = "AppCampaignUninstallRateStatistics", urlPatterns = {"/app_campaign_uninstall_rate_statistics/*"})
public class AppCampaignUninstallRateStatistics extends BaseHttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    HashMap<String, String> countryCodeNameMap = new HashMap<>();
    Map<String, String> tagNamePackageIdMap = new HashMap<>();
    Map<String, String> packageIdByTagNameMap = new HashMap<>();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doPost(request, response);
        if (!Utils.isAdmin(request, response)) return;

        String path = request.getPathInfo();
        String startDate = request.getParameter("startTime");
        String endDate = request.getParameter("endTime");

        String tagName = request.getParameter("tagName") == null ? "" : request.getParameter("tagName");
        String countryName = request.getParameter("countryName");
        String likeCampaignName = request.getParameter("likeCampaignName"); //模糊查询系列名称

        String jsonString = "";

        if (path.matches("/query_app_campaign_uninstall_rate_statistics")) {
            countryCodeNameMap = Utils.getCountryCodeNameMap();
            tagNamePackageIdMap = Utils.getTagNamePackageIdMap();
            packageIdByTagNameMap = Utils.getPackageIdByTagNameMap();


            WebCampaignUninstallRateStatistics statistics = new WebCampaignUninstallRateStatistics();
            JSObject one = null;
            try {
                statistics.setAppId(packageIdByTagNameMap.get(tagName));
                statistics.setCountryCode(countryName);

                List<WebCampaignUninstallRateStatistics> statisticsList = queryList(statistics, startDate, endDate, likeCampaignName);
                jsonString = JSONObject.toJSONString(statisticsList);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        response.getWriter().write(jsonString);
    }

    private List<WebCampaignUninstallRateStatistics> queryList(WebCampaignUninstallRateStatistics statistics, String startDate, String endDate, String likeCampaignName) {

        String sql = "SELECT\n" +
                "  id,\n" +
                "  installed_date,\n" +
                "  app_id,\n" +
                "  country_code,\n" +
                "  campaign_name,\n" +
                "  uninstall_rate,\n" +
                "  install_num,\n" +
                "  purchase_user\n" +
                "FROM\n" +
                "  web_campaign_uninstall_rate_statistics\n" +
                "WHERE installed_date BETWEEN '" + startDate + "'  AND '" + endDate + "'\n" +
                (statistics.getAppId() == null ? "" : "  AND app_id = '" + statistics.getAppId() + "'\n") +
                (statistics.getCountryCode() == "" ? "" : "  AND country_code = '" + statistics.getCountryCode() + "'\n") +
                (likeCampaignName == null ? "" : "  AND campaign_name LIKE '%" + likeCampaignName + "%'") +
                "ORDER BY id ASC";

        List<JSObject> list = null;
        List<WebCampaignUninstallRateStatistics> statisticsList = new ArrayList<>();
        try {
            list = DB.findListBySql(sql);
            JSObject js = null;
            WebCampaignUninstallRateStatistics currStatistics = null;
            for (int i = 0, len = list.size(); i < len; i++) {
                js = list.get(i);
                if (js.hasObjectData()) {
                    currStatistics = new WebCampaignUninstallRateStatistics();
                    currStatistics.setId(js.get("id"));
                    currStatistics.setInstalledDate(js.get("installed_date").toString());
                    currStatistics.setInstallNum(NumberUtil.parseDouble(js.get("install_num").toString(),0));
                    currStatistics.setPurchaseUser(NumberUtil.parseDouble(js.get("purchase_user").toString(),0));
                    currStatistics.setCampaignName(js.get("campaign_name").toString());
                    currStatistics.setAppId(tagNamePackageIdMap.get(js.get("app_id")));
                    currStatistics.setCountryCode(countryCodeNameMap.get(js.get("country_code")));
                    currStatistics.setUninstallRate(NumberUtil.trimDouble(NumberUtil.convertDouble(js.get("uninstall_rate"),0),3));
                    statisticsList.add(currStatistics);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statisticsList;
    }



}
