package com.bestgo.admanager.servlet;

import com.bestgo.admanager.DateUtil;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.lang.String;

@WebServlet(name = "TimeAnalysisReport", urlPatterns = {"/time_analysis_report/*"}, asyncSupported = true)
public class TimeAnalysisReport extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Utils.isAdmin(request, response)) return;
        String path = request.getPathInfo(); //getPathInfo()截取传来路径除去servlet的urlPattern的部分
        JsonObject jsonObject = new JsonObject();
        String country_filter = request.getParameter("country_filter");
        String sorterId = request.getParameter("sorterId");
        String tagName = request.getParameter("tagName");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
//        String beforeSevenDays = DateUtil.addDay(endTime, -6, "yyyy-MM-dd"); //返回一个endTime-6 的日期

        //以下的if用于处理动态添加选项的问题
        List<JSObject> country_array = null;
        if(path.matches(".*/setOption")){
            String sql = "select country_name from app_country_code_dict";
            try {
                country_array = DB.findListBySql(sql);
                response.getWriter().write(country_array.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

//        if (path.startsWith("/query_country_analysis_report")) {
        try {
            String sqlG = "select google_package_id from web_facebook_app_ids_rel WHERE tag_name = '" + tagName + "'";
            JSObject oneG = DB.findOneBySql(sqlG);
            if (oneG != null) {
                String google_package_id = oneG.get("google_package_id");
                if (google_package_id != null) {
                    JsonArray jsonArray = new JsonArray();
                    String sql = null;
                    if (path.matches(".*/time_query")) {
                        sql = "select country_code,date,sum(cost) as total_cost, sum(purchased_user) as total_purchased_user, " +
                                "sum(total_installed) as installed, sum(total_uninstalled) as uninstalled, sum(today_uninstalled) as total_today_uninstalled, " +
                                "sum(total_user) as users, sum(active_user) as active_users, sum(impression) as impressions, sum(revenue) as revenues, " +
                                "sum(estimated_revenue) as estimated_revenues, " +
                                " (sum(revenue) - sum(cost)) as incoming, " +                 //SQL语句里面：字段名1-字段名2意思为两列的值相减
                                "(case when sum(impression) > 0 then sum(revenue) * 1000 / sum(impression) else 0 end) as ecpm," +
                                "(case when sum(purchased_user) > 0 then sum(cost) / sum(purchased_user) else 0 end) as cpa, " +
                                " (case when sum(cost) > 0 then sum(estimated_revenue) / sum(cost) else 0 end) as est_rev_dev_cost " +
                                "from web_ad_country_analysis_report_history where app_id = '" + google_package_id + "' " +
                                "and date BETWEEN '" + startTime + "' AND '" + endTime + "' GROUP BY date"; //增加一个时间列,减少一个国家列
                    }else if(path.matches(".*/country_filter")){
                        String sql_tem = "select country_code from app_country_code_dict where country_name = '"
                                          + country_filter +"'";
                        JSObject country_fil_code = DB.findOneBySql(sql_tem);
                        String country_filter_code = country_fil_code.get("country_code");

                        sql = "select country_code,date,sum(cost) as total_cost, sum(purchased_user) as total_purchased_user, " +
                                "sum(total_installed) as installed, sum(total_uninstalled) as uninstalled, sum(today_uninstalled) as total_today_uninstalled, " +
                                "sum(total_user) as users, sum(active_user) as active_users, sum(impression) as impressions, sum(revenue) as revenues, " +
                                "sum(estimated_revenue) as estimated_revenues, " +
                                " (sum(revenue) - sum(cost)) as incoming, " +
                                "(case when sum(impression) > 0 then sum(revenue) * 1000 / sum(impression) else 0 end) as ecpm," +
                                "(case when sum(purchased_user) > 0 then sum(cost) / sum(purchased_user) else 0 end) as cpa, " +
                                " (case when sum(cost) > 0 then sum(estimated_revenue) / sum(cost) else 0 end) as est_rev_dev_cost " +
                                "from web_ad_country_analysis_report_history where app_id = '" + google_package_id + "' " +
                                "and country_code = '"+ country_filter_code + "'" +
                                "and date BETWEEN '" + startTime + "' AND '" + endTime + "' GROUP BY date";
                    }
                    //上面的 sql 把各项都加起来，是为了让之后的sorter能进行排序
                    //这些数据取出来形成了一个临时表，并没有改变数据表的结构和内容
                    int sorter = 0;
                    if (sorterId != null) {
                        sorter = Utils.parseInt(sorterId, 0);
                    }
                    //根据sorterId拼凑不同的SQL排序语句（以不同列进行排序）
                    switch (sorter) {
                        case 1031:
                            sql += " order by total_cost desc";   //sum(cost) as total_cost
                            break;
                        case 31:
                            sql += " order by total_cost"; //默认 asc
                            break;
                        case 1032:
                            sql += " order by date desc";   //这是一起被筛选出来的日期
                            break;
                        case 32:
                            sql += " order by date";
                            break;
                        case 1033:
                            sql += " order by total_purchased_user desc"; //sum(purchased_user) as total_purchased_user
                            break;
                        case 33:
                            sql += " order by total_purchased_user";
                            break;
                        case 1034:
                            sql += " order by installed desc"; //sum(total_installed) as installed
                            break;
                        case 34:
                            sql += " order by installed";
                            break;

                        case 1035:
                            sql += " order by uninstalled desc";//sum(total_uninstalled) as uninstalled
                            break;
                        case 35:
                            sql += " order by uninstalled";
                            break;
                        case 1037:
                            sql += " order by users desc"; //sum(total_user) as users
                            break;
                        case 37:
                            sql += " order by users";
                            break;
                        case 1038:
                            sql += " order by active_users desc"; //sum(active_user) as active_users
                            break;
                        case 38:
                            sql += " order by active_users";
                            break;
                        case 1039:
                            sql += " order by revenues desc"; //sum(revenue) as revenues
                            break;
                        case 39:
                            sql += " order by revenues";
                            break;
                        case 1040:
                            sql += " order by ecpm desc"; //when sum(impression) > 0 then sum(revenue) * 1000 / sum(impression) else 0 end) as ecpm
                            break;
                        case 40:
                            sql += " order by ecpm";
                            break;
                        case 1041:
                            sql += " order by cpa desc"; // when sum(purchased_user) > 0 then sum(cost) / sum(purchased_user) else 0 end) as cpa
                            break;
                        case 41:
                            sql += " order by cpa";
                            break;
                        case 1042:
                            sql += " order by incoming desc"; //(sum(revenue) - sum(cost)) as incoming
                            break;
                        case 42:
                            sql += " order by incoming";
                            break;
                        case 1044:
                            sql += " order by estimated_revenues desc"; //sum(estimated_revenue) as estimated_revenues
                            break;
                        case 44:
                            sql += " order by estimated_revenues";
                            break;
                        case 1045:
                            sql += " order by est_rev_dev_cost desc";
                            break;
                        /* case when sum(cost) > 0 then sum(estimated_revenue) / sum(cost) else 0 end) as est_rev_dev_cost
                         * 当 sum(cost)> 0，作处理：sum(estimated_revenue) / sum(cost)，否则 返回0 。 case指令结束。
                         * 输出列在字段名为 est_rev_dev_cost 的列里
                         */
                        case 45:
                            sql += " order by est_rev_dev_cost";
                            break;
                        default:
                            sql += " order by total_cost desc";  //默认排序方式
                    }


                    List<JSObject> timeDetailJSObjectList = DB.findListBySql(sql);

                    double total_cost = 0;
                    double total_puserchaed_user = 0;
                    double total_revenue = 0;
                    double total_es14 = 0;


                    for (JSObject j : timeDetailJSObjectList) {
                        if (j != null && j.hasObjectData()) {
                            String country_code = j.get("country_code");
/*                                sql = "select sum(cost) as seven_days_costs, sum(revenue) as seven_days_revenues " +
                                        "from web_ad_country_analysis_report_history where app_id = '"+google_package_id+"' " +
                                        " and country_code = '" + country_code + "' and date BETWEEN '" + beforeSevenDays + "' AND '" + endTime + "'";
                                // 先选择好了日期区间，才在区间的范围内进行SUM()运算

                                JSObject oneC = DB.findOneBySql(sql);

                                double seven_days_costs  = 0;
                                double seven_days_incoming  = 0;
                                double seven_days_revenues = 0;

                                if(oneC != null && oneC.hasObjectData()){     //hasObjectData()用于判断是否含名称/值对
                                    seven_days_costs  = Utils.convertDouble(oneC.get("seven_days_costs"),0);
                                    seven_days_revenues  = Utils.convertDouble(oneC.get("seven_days_revenues"),0);
                                    seven_days_incoming = Utils.convertDouble(seven_days_revenues - seven_days_costs,0);
                                }
                                */

                            sql = "select country_name from app_country_code_dict where country_code = '" + country_code + "'";
                            JSObject oneC = null;
                            oneC = DB.findOneBySql(sql);   //按这里的SQL语句进行操作返回 JsonObject；每个循环只检索一个country_code
                            String countryName = "";
                            if (oneC != null && oneC.hasObjectData()) {
                                countryName = oneC.get("country_name");
                            } else {
                                countryName = country_code;
                            }
                            String date = null;
                            if (path.matches(".*/time_query")||path.matches(".*/country_filter")) {
                                SimpleDateFormat date_wfc = new SimpleDateFormat("yyyy-MM-dd");  //这里是设置一个日期的格式
                                date = date_wfc.format(j.get("date"));    //这里要把 SQL里的 date 格式转成 String 类型
                            }
                            double costs = Utils.convertDouble(j.get("total_cost"), 0);
                            double purchased_users = Utils.convertDouble(j.get("total_purchased_user"), 0);
                            double installed = Utils.convertDouble(j.get("installed"), 0);
                            double uninstalled = Utils.convertDouble(j.get("uninstalled"), 0);
                            double total_today_uninstalled = Utils.convertDouble(j.get("total_today_uninstalled"), 0);
                            double uninstalledRate = installed != 0 ? total_today_uninstalled / installed : 0;


                            double users = Utils.convertDouble(j.get("users"), 0);
                            double active_users = Utils.convertDouble(j.get("active_users"), 0);
                            double revenues = Utils.convertDouble(j.get("revenues"), 0);
                            double estimated_revenues = Utils.convertDouble(j.get("estimated_revenues"), 0);
//                                double ecpm = impressions == 0 ? 0 : Utils.trimDouble3(revenues * 1000 / impressions );
                            double ecpm = Utils.convertDouble(j.get("ecpm"), 0);
                            double estRevDevCost = Utils.convertDouble(j.get("est_rev_dev_cost"), 0);
                            double cpa = Utils.convertDouble(j.get("cpa"), 0);
                            double incoming = Utils.convertDouble(j.get("incoming"), 0);
                            double cpa_dev_ecpm = (ecpm == 0) ? 0 : (cpa / ecpm);
                            String sqlAB = "select bidding from ad_campaigns_admob_auto_create where app_name = '"
                                    + tagName + "' and country_region like '%" + country_code + "%'";
                            List<JSObject> adwordsBiddingList = DB.findListBySql(sqlAB);

                            String sqlFB = "select bidding from ad_campaigns_auto_create where app_name = '"
                                    + tagName + "' and country_region like '%" + countryName + "%'";
                            List<JSObject> facebookBiddingList = DB.findListBySql(sqlFB);


                            Set<String> biddingSet = new HashSet<>();
                            for (JSObject ff : facebookBiddingList) {
                                if (ff != null && ff.hasObjectData()) {
                                    String bidding = ff.get("bidding");
                                    String[] split = bidding.split(",");
                                    for (String s : split) {
                                        biddingSet.add(s);   //字符串从 ,处拆掉一一存进biddingSet里
                                    }
                                }
                            }
                            for (JSObject aa : adwordsBiddingList) {
                                if (aa != null && aa.hasObjectData()) {
                                    String bidding = aa.get("bidding");
                                    String[] split = bidding.split(",");
                                    for (String s : split) {
                                        biddingSet.add(s);   //到这里，两张表里的bidding都被插入集 biddingSet 里了
                                    }
                                }
                            }
                            String biddingsStr = "";
                            if (biddingSet != null && biddingSet.size() > 0) {
                                for (String s : biddingSet) {
                                    biddingsStr += s + ","; //重新把集变成由,分隔的字符串
                                }
                            } else {
                                biddingsStr = "--";
                            }

                            total_cost += costs;
                            total_puserchaed_user += purchased_users;
                            total_revenue += revenues;
                            total_es14 += estimated_revenues;  //前面声明的这四个变量，在每一次循环中累加

                            JsonObject d = new JsonObject(); // 仍在由 List<JSObject> countryDetailJSObjectList控制的大循环里，每次 d 只得到一行的数据
                            d.addProperty("country_name", countryName);
                            d.addProperty("date", date);
                            d.addProperty("costs", Utils.trimDouble(costs, 0));
                            d.addProperty("purchased_users", purchased_users);
                            d.addProperty("installed", installed);
                            d.addProperty("uninstalled", uninstalled);
                            d.addProperty("uninstalled_rate", Utils.trimDouble(uninstalledRate, 3));
                            d.addProperty("users", users);
                            d.addProperty("active_users", active_users);
                            d.addProperty("revenues", Utils.trimDouble(revenues, 0));
                            d.addProperty("ecpm", Utils.trimDouble(ecpm, 3));
                            d.addProperty("cpa_dev_ecpm", Utils.trimDouble(cpa_dev_ecpm, 3));
//                                d.addProperty("seven_days_costs", Utils.trimDouble(seven_days_costs,0));
//                                d.addProperty("seven_days_incoming", Utils.trimDouble(seven_days_incoming,0));
//                                d.addProperty("seven_days_revenues", Utils.trimDouble(seven_days_revenues,0));
                            d.addProperty("incoming", Utils.trimDouble(incoming, 0));
                            d.addProperty("estimated_revenues", Utils.trimDouble(estimated_revenues, 0));
                            d.addProperty("estimated_revenues_dev_cost", Utils.trimDouble(estRevDevCost, 3));
                            String sqlP = "select price from web_ad_country_analysis_report_price where app_id = '" + google_package_id + "' and country_code = '" + country_code + "'";
                            JSObject oneP = DB.findOneBySql(sqlP);  // 得到price
                            double price = 0;
                            if (oneP != null && oneP.hasObjectData()) {
                                price = Utils.convertDouble(oneP.get("price"), 0);
                            }

                            d.addProperty("cpa", Utils.trimDouble(cpa, 3));
                            jsonArray.add(d);
                        }
                    }
                    double es14_dev_cost = total_cost != 0 ? total_es14 / total_cost : 0;
                    double total_cpa = total_puserchaed_user != 0 ? total_cost / total_puserchaed_user : 0;
                    jsonObject.add("array", jsonArray);

                    jsonObject.addProperty("total_cost", Utils.trimDouble(total_cost, 0));
                    jsonObject.addProperty("total_puserchaed_user", Utils.trimDouble(total_puserchaed_user, 0));
                    jsonObject.addProperty("total_cpa", Utils.trimDouble(total_cpa, 3));
                    jsonObject.addProperty("total_revenue", Utils.trimDouble(total_revenue, 0));
                    jsonObject.addProperty("total_es14", Utils.trimDouble(total_es14, 0));
                    jsonObject.addProperty("es14_dev_cost", Utils.trimDouble(es14_dev_cost, 3));
                    jsonObject.addProperty("ret", 1);

                }
            }

            jsonObject.addProperty("message", "执行成功");

        } catch (Exception e) {
            jsonObject.addProperty("ret", 0);
            jsonObject.addProperty("message", e.getMessage());
        }
        response.getWriter().write(jsonObject.toString());  //最终的结果会处理成一个JsonObject字符串返回
    }
}
