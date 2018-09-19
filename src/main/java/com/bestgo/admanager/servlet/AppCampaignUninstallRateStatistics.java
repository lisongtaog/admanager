package com.bestgo.admanager.servlet;

import com.bestgo.admanager.bean.WebCampaignUninstallRateStatistics;
import com.bestgo.admanager.utils.JedisPoolUtil;
import com.bestgo.admanager.utils.Utils;
import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;
import com.google.gson.JsonObject;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mengjun
 * @date 2018/9/19 17:12
 * @desc
 */
@WebServlet(name = "AppCampaignUninstallRateStatistics", urlPatterns = {"/app_campaign_uninstall_rate_statistics/*"})
public class AppCampaignUninstallRateStatistics extends BaseHttpServlet{
    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException,IOException {
        doPost(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doPost(request, response);
        if (!Utils.isAdmin(request, response)) return;
        String path = request.getPathInfo();
        JsonObject json = new JsonObject();
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        Jedis jedis = JedisPoolUtil.getJedis();
        String tagName = request.getParameter("tagName");
        String countryName = request.getParameter("countryName");
        String likeCampaignName = request.getParameter("likeCampaignName"); //模糊查询系列名称
        if (path.matches("query_app_campaign_uninstall_rate_statistics")) {
            WebCampaignUninstallRateStatistics statistics = new WebCampaignUninstallRateStatistics();
            JSObject one = null;
            try {
                statistics.setAppId(jedis.hget("tagNameAppIdMap",tagName));
                if (statistics.getAppId() == null) {
                    one = DB.findOneBySql("SELECT google_package_id FROM web_facebook_app_ids_rel WHERE tag_name = '" + tagName + "'");
                    if (one.hasObjectData()) {
                        statistics.setAppId(one.get("google_package_id"));
                        jedis.hset("tagNameAppIdMap",tagName,statistics.getAppId());
                    }
                }
                statistics.setCountryCode(jedis.hget("countryNameCodeMap",countryName));
                if (statistics.getCountryCode() == null) {
                    one = DB.findOneBySql("SELECT country_code FROM app_country_code_dict WHERE country_name = '" + countryName + "'");
                    if (one.hasObjectData()) {
                        statistics.setCountryCode(one.get("country_code"));
                        jedis.hset("countryNameCodeMap",countryName,statistics.getCountryCode());
                    }
                }
                List<WebCampaignUninstallRateStatistics> statisticsList = queryList(statistics, startDate, endDate, likeCampaignName);

            }catch (Exception e) {
                e.printStackTrace();
            }


        }
        response.getWriter().write(json.toString());
    }

    private List<WebCampaignUninstallRateStatistics> queryList(WebCampaignUninstallRateStatistics statistics,String startDate,String endDate,String likeCampaignName){
        String sql = "SELECT id,installed_date,app_id,country_code,campaign_name,uninstall_rate \n" +
                "FROM web_campaign_uninstall_rate_statistics\n" +
                "WHERE installed_date BETWEEN '" + startDate + "' AND '" + endDate + "'\n" +
                statistics.getAppId() == null ? "" : "AND app_id = '" + statistics.getAppId() + "'\n" +
                statistics.getCountryCode() == null ? "" : "AND country_code = '"+statistics.getCountryCode()+"'\n" +
                likeCampaignName == null ? "" : "AND campaign_name LIKE '%" + likeCampaignName + "%'";
        List<JSObject> list = null;
        List<WebCampaignUninstallRateStatistics> statisticsList = new ArrayList<>();
        try {
            list = DB.findListBySql(sql);
            JSObject js = null;
            WebCampaignUninstallRateStatistics currStatistics = null;
            for (int i = 0,len = list.size();i < len;i++){
                js = list.get(i);
                if (js.hasObjectData()) {
                    currStatistics = new WebCampaignUninstallRateStatistics();
                    currStatistics.setId(js.get("id"));
                    currStatistics.setInstalledDate(js.get("installed_date").toString());
                    currStatistics.setAppId(js.get("app_id"));
                    currStatistics.setCountryCode(js.get("country_code"));
                    currStatistics.setUninstallRate(js.get("uninstall_rate"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statisticsList;
    }
}
