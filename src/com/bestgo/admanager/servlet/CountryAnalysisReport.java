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
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebServlet(name = "CountryAnalysisReport", urlPatterns = {"/country_analysis_report/*"}, asyncSupported = true)
public class CountryAnalysisReport extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Utils.isAdmin(request, response)) return;

        String path = request.getPathInfo();
        JsonObject jsonObject = new JsonObject();
        String sorterId = request.getParameter("sorterId");
        String tagName = request.getParameter("tagName");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");

//        DecimalFormat df   = new DecimalFormat("######0.00000000");
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
                                "sum(estimated_revenue) as estimated_revenues, " +
                                " (sum(revenue) - sum(cost)) as incoming, "+
                                "(case when sum(impression) > 0 then sum(revenue) * 1000 / sum(impression) else 0 end) as ecpm,"+
                                "(case when sum(purchased_user) > 0 then sum(cost) / sum(purchased_user) else 0 end) as cpa, "+
                                " (case when sum(cost) > 0 then sum(estimated_revenue) / sum(cost) else 0 end) as est_rev_dev_cost " +
                                "from web_ad_country_analysis_report_history where app_id = '"+google_package_id+"' " +
                                "and date BETWEEN '" + startTime + "' AND '" + endTime + "' GROUP BY country_code";

                        int sorter = 0;
                        if (sorterId != null) {
                            sorter = Utils.parseInt(sorterId, 0);
                        }
                        switch(sorter){
                            case 1031:
                                sql += " order by total_cost desc";
                                break;
                            case 31:
                                sql += " order by total_cost";
                                break;
                            case 1032:
                                sql += " order by total_purchased_user desc";
                                break;
                            case 32:
                                sql += " order by total_purchased_user";
                                break;
                            case 1033:
                                sql += " order by installed desc";
                                break;
                            case 33:
                                sql += " order by installed";
                                break;

                            case 1034:
                                sql += " order by uninstalled desc";
                                break;
                            case 34:
                                sql += " order by uninstalled";
                                break;
                            case 1036:
                                sql += " order by users desc";
                                break;
                            case 36:
                                sql += " order by users";
                                break;
                            case 1037:
                                sql += " order by active_users desc";
                                break;
                            case 37:
                                sql += " order by active_users";
                                break;
                            case 1038:
                                sql += " order by revenues desc";
                                break;
                            case 38:
                                sql += " order by revenues";
                                break;
                            case 1039:
                                sql += " order by ecpm desc";
                                break;
                            case 39:
                                sql += " order by ecpm";
                                break;
                            case 1040:
                                sql += " order by cpa desc";
                                break;
                            case 40:
                                sql += " order by cpa";
                                break;
                            case 1041:
                                sql += " order by estimated_revenues desc";
                                break;
                            case 41:
                                sql += " order by estimated_revenues";
                                break;
                            case 1042:
                                sql += " order by est_rev_dev_cost desc";
                                break;
                            case 42:
                                sql += " order by est_rev_dev_cost";
                                break;
                            case 1043:
                                sql += " order by incoming desc";
                                break;
                            case 43:
                                sql += " order by incoming";
                                break;
                            default:
                                sql += " order by total_cost desc";
                        }
                        List<JSObject> countryDetailJSObjectList = DB.findListBySql(sql);

                        double total_cost = 0;
                        double total_puserchaed_user = 0;
                        double total_revenue = 0;
                        double total_es14 = 0;


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
                                double cpa = purchased_users != 0 ? costs / purchased_users : 0;

                                double users = Utils.convertDouble(j.get("users"),0);
                                double active_users = Utils.convertDouble(j.get("active_users"),0);
                                double impressions = Utils.convertDouble(j.get("impressions"),0);
                                double revenues = j.get("revenues");
                                double estimated_revenues = j.get("estimated_revenues");
                                double ecpm = impressions == 0 ? 0 : Utils.trimDouble3(revenues * 1000 / impressions );
//                                String ecpm = impressions == 0 ? "0" : (df.format(revenues / impressions ));
                                double incoming = revenues - costs;
                                double estRevDevCost = Utils.convertDouble(j.get("est_rev_dev_cost"),0);


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
                                        biddingsStr += s + ",";
                                    }
                                }else{
                                    biddingsStr = "--";
                                }

                                total_cost += costs;
                                total_puserchaed_user += purchased_users;
                                total_revenue += revenues;
                                total_es14 += estimated_revenues;

                                JsonObject d = new JsonObject();
                                d.addProperty("country_name", countryName);
                                d.addProperty("costs", Utils.trimDouble3(costs));
                                d.addProperty("purchased_users", purchased_users);
                                d.addProperty("installed", installed);
                                d.addProperty("uninstalled", uninstalled);
                                d.addProperty("uninstalled_rate", Utils.trimDouble3(uninstalledRate));
                                d.addProperty("users", users);
                                d.addProperty("active_users", active_users);
                                d.addProperty("revenues", Utils.trimDouble3(revenues));
                                d.addProperty("ecpm", ecpm);
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
                        double es14_dev_cost = total_cost != 0 ? total_es14 / total_cost : 0;
                        double total_cpa = total_puserchaed_user != 0 ? total_cost / total_puserchaed_user : 0;
                        jsonObject.add("array", jsonArray);

                        jsonObject.addProperty("total_cost", Utils.trimDouble3(total_cost));
                        jsonObject.addProperty("total_puserchaed_user", Utils.trimDouble3(total_puserchaed_user));
                        jsonObject.addProperty("total_cpa", Utils.trimDouble3(total_cpa));
                        jsonObject.addProperty("total_revenue", Utils.trimDouble3(total_revenue));
                        jsonObject.addProperty("total_es14", Utils.trimDouble3(total_es14));
                        jsonObject.addProperty("es14_dev_cost", Utils.trimDouble3(es14_dev_cost));
                        jsonObject.addProperty("ret", 1);

                    }
                }

                jsonObject.addProperty("message", "执行成功");

            } catch (Exception e) {
                jsonObject.addProperty("ret", 0);
                jsonObject.addProperty("message", e.getMessage());
            }
        }else if(path.startsWith("/query_id_of_auto_create_campaigns")){
            try{
                String curr_country_name = request.getParameter("curr_country_name");
                String sqlF = "select id from ad_campaigns_auto_create where app_name = '" + tagName + "' and country_region like '%" + curr_country_name + "%'";
                JSObject oneF = DB.findOneBySql(sqlF);
                long id_facebook = -1;
                if(oneF != null && oneF.hasObjectData()){
                    id_facebook = oneF.get("id");
                }
                String sqlC = "select country_code from app_country_code_dict where country_name = '" + curr_country_name + "'";
                JSObject oneC = DB.findOneBySql(sqlC);
                String curr_country_code = null;
                if(oneC != null && oneC.hasObjectData()){
                    curr_country_code = oneC.get("country_code");
                }
                String sqlA = "select id from ad_campaigns_admob_auto_create where app_name = '" + tagName + "' and country_region like '%" + curr_country_code + "%'";
                JSObject oneA = DB.findOneBySql(sqlA);
                long id_adwords = -1;
                if(oneA != null && oneA.hasObjectData()){
                    id_adwords = oneA.get("id");
                }
                jsonObject.addProperty("id_facebook", id_facebook);
                jsonObject.addProperty("id_adwords", id_adwords);
                jsonObject.addProperty("ret", 1);
                jsonObject.addProperty("message", "执行成功");
            } catch (Exception e) {
                jsonObject.addProperty("ret", 0);
                jsonObject.addProperty("message", e.getMessage());
            }
        }
        response.getWriter().write(jsonObject.toString());
    }
}