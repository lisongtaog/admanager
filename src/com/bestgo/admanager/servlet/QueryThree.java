package com.bestgo.admanager.servlet;

import com.bestgo.admanager.Utils;
import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebServlet(name = "QueryThree", urlPatterns = {"/query_three/*"}, asyncSupported = true)
public class QueryThree extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Utils.isAdmin(request, response)) return;

        String path = request.getPathInfo();
        JsonObject jsonObject = new JsonObject();

        String tagName = request.getParameter("tagName");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");


        if (path.startsWith("/query_country_analysis_report")) {
            try {
                String sqlG = "select google_package_id from web_facebook_app_ids_rel WHERE tag_name = '" + tagName + "'";
                JSObject oneG = DB.findOneBySql(sqlG);
                if(oneG != null){
                    String google_package_id = oneG.get("google_package_id");
                    if(google_package_id != null){
                        JsonArray jsonArray = new JsonArray();
                        String sql = "select country_code, sum(cost) as total_cost, sum(purchased_user) as total_purchased_user, " +
                                "sum(total_installed) as installed, sum(total_uninstalled) as uninstalled, sum(today_uninstalled) as total_today_uninstalled, " +
                                "sum(total_user) as users, sum(active_user) as active_users, sum(impression) as impressions, sum(revenue) as revenues, " +
                                "sum(estimated_revenue) as estimated_revenues from web_ad_country_analysis_report_history where app_id = '"+google_package_id+"' " +
                                "and date BETWEEN '" + startTime + "' AND '" + endTime + "' GROUP BY country_code;";
                        List<JSObject> countryDetailJSObjectList = DB.findListBySql(sql);


                        for(JSObject j : countryDetailJSObjectList){
                            if(j != null && j.hasObjectData()){
                                String country_code = j.get("country_code");
                                String sqlC = "select country_name from app_country_code_dict where country_code = '" + country_code + "'";
                                JSObject oneC = DB.findOneBySql(sqlC);
                                String countryName = "";
                                if(oneC != null && oneC.hasObjectData()){
                                    countryName = oneC.get("country_name");
                                }else{
                                    countryName = country_code;
                                }
                                double costs = j.get("total_cost");
                                double purchased_users = Utils.convertDouble(j.get("total_purchased_user"),0);
                                double installed = Utils.convertDouble(j.get("installed"),0);
                                double uninstalled = Utils.convertDouble(j.get("uninstalled"),0);
                                double total_today_uninstalled = Utils.convertDouble(j.get("total_today_uninstalled"),0);
                                double uninstalledRate = installed != 0 ? total_today_uninstalled / installed : 0;
                                double cpa = installed != 0 ? costs / installed : 0;

                                double users = Utils.convertDouble(j.get("users"),0);
                                double active_users = Utils.convertDouble(j.get("active_users"),0);
                                double impressions = Utils.convertDouble(j.get("impressions"),0);
                                double revenues = j.get("revenues");
                                double estimated_revenues = j.get("estimated_revenues");
                                double ecpm = impressions != 0 ? revenues / impressions / 1000 : 0;
                                double incoming = revenues - costs;
                                double estRevDevCost = costs != 0 ? estimated_revenues / costs : 0;


                                String sqlAB = "select bidding from ad_campaigns_admob_auto_create where app_name = '"
                                                       + tagName + "' and country_region like '%" + country_code + "%'";
                                List<JSObject> adwordsBiddingList = DB.findListBySql(sqlAB);

                                String sqlFB = "select bidding from ad_campaigns_auto_create where app_name = '"
                                                       + tagName + "' and country_region like '%" + countryName + "%'";
                                List<JSObject> facebookBiddingList = DB.findListBySql(sqlFB);


                                Set<String> biddingSet = new HashSet<>();
                                for(JSObject ff : facebookBiddingList){
                                    if(ff != null && ff.hasObjectData()){
                                        String bidding = ff.get("bidding");
                                        String[] split = bidding.split(",");
                                        for(String s : split){
                                            biddingSet.add(s);
                                        }
                                    }
                                }
                                for(JSObject aa : adwordsBiddingList){
                                    if(aa != null && aa.hasObjectData()){
                                        String bidding = aa.get("bidding");
                                        String[] split = bidding.split(",");
                                        for(String s : split){
                                            biddingSet.add(s);
                                        }
                                    }
                                }
                                String biddingsStr = "";
                                if(biddingSet != null && biddingSet.size()>0){
                                    for(String s : biddingSet){
                                        biddingsStr += s + "，";
                                    }
                                }else{
                                    biddingsStr = "--";
                                }

                                JsonObject d = new JsonObject();
                                d.addProperty("country_name", countryName);
                                d.addProperty("costs", costs);
                                d.addProperty("purchased_users", purchased_users);
                                d.addProperty("installed", installed);
                                d.addProperty("uninstalled", uninstalled);
                                d.addProperty("uninstalled_rate", Utils.trimDouble3(uninstalledRate));
                                d.addProperty("users", users);
                                d.addProperty("active_users", active_users);
                                d.addProperty("revenues", Utils.trimDouble3(revenues));
                                d.addProperty("ecpm", Utils.trimDouble3(ecpm));
                                d.addProperty("incoming", Utils.trimDouble3(incoming));
                                d.addProperty("estimated_revenues", Utils.trimDouble3(estimated_revenues));
                                d.addProperty("estimated_revenues_dev_cost", Utils.trimDouble3(estRevDevCost));
                                String sqlP = "select price from web_ad_country_analysis_report_price where app_id = '"+google_package_id+"' and country_code = '"+country_code+"'";
                                JSObject oneP = DB.findOneBySql(sqlP);
                                double price = 0;
                                if(oneP != null && oneP.hasObjectData()){
                                    price = oneP.get("price");
                                }
                                d.addProperty("price", Utils.trimDouble3(price));
                                d.addProperty("bidding", biddingsStr);
                                d.addProperty("cpa", Utils.trimDouble3(cpa));
                                jsonArray.add(d);
                            }

                        }
                        jsonObject.addProperty("ret", 1);
                        jsonObject.add("array", jsonArray);
                    }
                }

                jsonObject.addProperty("message", "执行成功");

            } catch (Exception e) {
                jsonObject.addProperty("ret", 0);
                jsonObject.addProperty("message", e.getMessage());
            }
        }
        response.getWriter().write(jsonObject.toString());
    }
}