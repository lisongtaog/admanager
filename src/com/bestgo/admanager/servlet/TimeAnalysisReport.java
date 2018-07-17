package com.bestgo.admanager.servlet;

import com.bestgo.admanager.utils.StringUtil;
import com.bestgo.admanager.utils.NumberUtil;
import com.bestgo.admanager.utils.Utils;
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
        String tagName = request.getParameter("tagName");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        String idx = request.getParameter("page_index");
        String sz = request.getParameter("page_size");
        String ord = request.getParameter("order");
        int index = 0;
        int size = 0;
        int order = 0;
        boolean desc = false;
        if(idx != null && sz != null && ord != null){
            index = Integer.parseInt(idx);
            size = Integer.parseInt(sz);
            order = Integer.parseInt(ord);
            desc = order < 1000;
            if (order >= 1000) order = order - 1000;
        }
        List<String> orders = new ArrayList<>();
        if(country_filter != null && country_filter.isEmpty()) {
            orders.add(" order by date ");
            orders.add(" order by total_cost ");
            orders.add(" order by total_purchased_user ");
            orders.add(" order by installed ");
            orders.add(" order by uninstalled ");
            orders.add(" order by uninstalledRate "); //这列是程序计算出来无法后端排序（前端禁用，这里占位）
            orders.add(" order by users ");
            orders.add(" order by active_users ");
            orders.add(" order by revenues ");
            orders.add(" order by ecpm ");
            orders.add(" order by cpa ");
            orders.add(" order by cpa/ecpm"); //这列是程序计算出来无法后端排序（前端禁用，这里占位）
            orders.add(" order by incoming ");
            orders.add(" order by estimated_revenues ");
            orders.add(" order by est_rev_dev_cost");
        }else{
            orders.add(" order by date ");
            orders.add(" order by total_cost ");
            orders.add(" order by total_purchased_user ");
            orders.add(" order by installed ");
            orders.add(" order by uninstalled ");
            orders.add(" order by uninstalledRate "); //这列是程序计算出来无法后端排序（前端禁用，这里占位）
            orders.add(" order by users ");
            orders.add(" order by active_users ");
            orders.add(" order by revenues ");
            orders.add(" order by pi ");
            orders.add(" order by ecpm ");
            orders.add(" order by cpa ");
            orders.add(" order by a_cpa ");
            orders.add(" order by cpa/ecpm"); //这列是程序计算出来无法后端排序（前端禁用，这里占位）
            orders.add(" order by incoming ");
            orders.add(" order by estimated_revenues ");
            orders.add(" order by est_rev_dev_cost");
        }

        //以下的if用于处理动态添加选项的问题
        List<JSObject> country_array = null;
        if (path.startsWith("/setOption")) {
            String sql = "select country_name from app_country_code_dict";
            try {
                country_array = DB.findListBySql(sql);
                response.getWriter().write(country_array.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

            try {
                String sqlG = "select google_package_id from web_facebook_app_ids_rel WHERE tag_name = '" + tagName + "'";
                JSObject oneG = DB.findOneBySql(sqlG);
                if (oneG.hasObjectData()) {
                    String google_package_id = oneG.get("google_package_id");
                    if (google_package_id != null) {
                        JsonArray jsonArray = new JsonArray();

                        //这里用于匹配相关的country_code
                        String sql_tem = "select country_code from app_country_code_dict where country_name = '" + country_filter + "'";
                        JSObject country_fil_code = DB.findOneBySql(sql_tem);
                        String country_filter_code = country_fil_code.get("country_code");

                        String sql = null;
                        if (path.matches(".*/time_query")) {
                            sql = "select date,sum(cost) as total_cost, sum(purchased_user) as total_purchased_user, " +
                                    "sum(total_installed) as installed, sum(total_uninstalled) as uninstalled, sum(today_uninstalled) as total_today_uninstalled, " +
                                    "sum(total_user) as users, sum(active_user) as active_users, sum(impression) as impressions, sum(revenue) as revenues, " +
                                    "sum(estimated_revenue) as estimated_revenues, " +
                                    " (sum(revenue) - sum(cost)) as incoming, " +                 //SQL语句里面：字段名1-字段名2意思为两列的值相减
                                    "(case when sum(impression) > 0 then sum(revenue) * 1000 / sum(impression) else 0 end) as ecpm," +
                                    "(case when sum(purchased_user) > 0 then sum(cost) / sum(purchased_user) else 0 end) as cpa, " +
                                    " (case when sum(cost) > 0 then sum(estimated_revenue) / sum(cost) else 0 end) as est_rev_dev_cost " +
                                    "from web_ad_country_analysis_report_history where app_id = '" + google_package_id + "' " +
                                    "and date BETWEEN '" + startTime + "' AND '" + endTime + "' GROUP BY date"; //增加一个时间列,减少一个国家列
                            if(country_filter != null){
                                if(!country_filter.isEmpty()) {
                                    sql = "select a.date,sum(a.cost) as total_cost, sum(a.purchased_user) as total_purchased_user, " +
                                            "sum(a.total_installed) as installed, sum(a.total_uninstalled) as uninstalled, sum(a.today_uninstalled) as total_today_uninstalled, " +
                                            "sum(a.total_user) as users, sum(a.active_user) as active_users, sum(a.impression) as impressions, sum(a.revenue) as revenues, " +
                                            "sum(a.estimated_revenue) as estimated_revenues, " +
                                            " (sum(a.revenue) - sum(cost)) as incoming, " +
                                            "(case when sum(a.impression) > 0 then sum(a.revenue) * 1000 / sum(a.impression) else 0 end) as ecpm," +
                                            "(case when sum(a.purchased_user) > 0 then sum(a.cost) / sum(a.purchased_user) else 0 end) as cpa, " +
                                            " (case when sum(a.cost) > 0 then sum(a.estimated_revenue) / sum(a.cost) else 0 end) as est_rev_dev_cost " +
                                            " ,b.pi, b.a_cpa "+
                                            "from web_ad_country_analysis_report_history a,web_ad_country_analysis_report_history_by_date b " +
                                            " where a.app_id = b.app_id AND a.country_code = b.country_code AND a.date = b.date AND " +
                                            "a.app_id = '" + google_package_id + "' " +
                                            "and a.country_code = '" + country_filter_code + "' " +
                                            "and a.date BETWEEN '" + startTime + "' AND '" + endTime + "' GROUP BY date";
                                }
                            }
                        }
                        int count = DB.findListBySql(sql).size();

                        if (order < orders.size()) {//单列排序
                            sql += orders.get(order) + (desc ? " desc" : "");
                        }
                        sql += " limit " + index * size + "," + size;

                        List<JSObject> timeDetailJSObjectList = DB.findListBySql(sql);

                        double total_cost = 0;
                        double total_puserchaed_user = 0;
                        double total_revenue = 0;
                        double total_es14 = 0;


                        for (JSObject j : timeDetailJSObjectList) {
                            if (j != null && j.hasObjectData()) {
                                double pi = 0;
                                double a_cpa = 0;
                                pi = NumberUtil.convertDouble(j.get("pi"),0);
                                a_cpa = NumberUtil.convertDouble(j.get("a_cpa"),0);
                                SimpleDateFormat date_wfc = new SimpleDateFormat("yyyy-MM-dd");
                                String date = date_wfc.format(j.get("date"));

                                if (path.matches(".*/time_query") || path.matches(".*/country_filter")) {
                                    date_wfc = new SimpleDateFormat("yyyy-MM-dd");  //这里是设置一个日期的格式
                                    date = date_wfc.format(j.get("date"));    //这里要把 SQL里的 date 格式转成 String 类型
                                }
                                double costs = NumberUtil.convertDouble(j.get("total_cost"), 0);
                                double purchased_users = NumberUtil.convertDouble(j.get("total_purchased_user"), 0);
                                double installed = NumberUtil.convertDouble(j.get("installed"), 0);
                                double uninstalled = NumberUtil.convertDouble(j.get("uninstalled"), 0);
                                double total_today_uninstalled = NumberUtil.convertDouble(j.get("total_today_uninstalled"), 0);
                                double uninstalledRate = installed != 0 ? total_today_uninstalled / installed : 0;
                                double users = NumberUtil.convertDouble(j.get("users"), 0);
                                double active_users = NumberUtil.convertDouble(j.get("active_users"), 0);
                                double revenues = NumberUtil.convertDouble(j.get("revenues"), 0);
                                double estimated_revenues = NumberUtil.convertDouble(j.get("estimated_revenues"), 0);
                                double ecpm = NumberUtil.convertDouble(j.get("ecpm"), 0);
                                double estRevDevCost = NumberUtil.convertDouble(j.get("est_rev_dev_cost"), 0);
                                double cpa = NumberUtil.convertDouble(j.get("cpa"), 0);
                                double incoming = NumberUtil.convertDouble(j.get("incoming"), 0);
                                double cpa_dev_ecpm = (ecpm == 0) ? 0 : (cpa / ecpm);

                                total_cost += costs;
                                total_puserchaed_user += purchased_users;
                                total_revenue += revenues;
                                total_es14 += estimated_revenues;  //前面声明的这四个变量，在每一次循环中累加

                                JsonObject d = new JsonObject(); // 仍在由 List<JSObject> countryDetailJSObjectList控制的大循环里，每次 d 只得到一行的数据

                                d.addProperty("date", date);
                                d.addProperty("costs", NumberUtil.trimDouble(costs, 0));
                                d.addProperty("purchased_users", purchased_users);
                                d.addProperty("installed", installed);
                                d.addProperty("uninstalled", uninstalled);
                                d.addProperty("uninstalled_rate", NumberUtil.trimDouble(uninstalledRate, 3));
                                d.addProperty("users", users);
                                d.addProperty("active_users", active_users);
                                d.addProperty("revenues", NumberUtil.trimDouble(revenues, 0));
                                if (StringUtil.isNotEmpty(country_filter)) {
                                    d.addProperty("pi", NumberUtil.trimDouble(pi, 3));
                                    d.addProperty("a_cpa", NumberUtil.trimDouble(a_cpa, 3));
                                }
                                d.addProperty("ecpm", NumberUtil.trimDouble(ecpm, 3));
                                d.addProperty("cpa_dev_ecpm", NumberUtil.trimDouble(cpa_dev_ecpm, 3));
                                d.addProperty("incoming", NumberUtil.trimDouble(incoming, 0));
                                d.addProperty("estimated_revenues", NumberUtil.trimDouble(estimated_revenues, 0));
                                d.addProperty("estimated_revenues_dev_cost", NumberUtil.trimDouble(estRevDevCost, 3));
                                d.addProperty("cpa", NumberUtil.trimDouble(cpa, 3));
                                jsonArray.add(d);
                            }
                        }
                        double es14_dev_cost = total_cost != 0 ? total_es14 / total_cost : 0;
                        double total_cpa = total_puserchaed_user != 0 ? total_cost / total_puserchaed_user : 0;
                        jsonObject.add("array", jsonArray);

                        jsonObject.addProperty("total_cost", NumberUtil.trimDouble(total_cost, 0));
                        jsonObject.addProperty("total_puserchaed_user", NumberUtil.trimDouble(total_puserchaed_user, 0));
                        jsonObject.addProperty("total_cpa", NumberUtil.trimDouble(total_cpa, 3));
                        jsonObject.addProperty("total_revenue", NumberUtil.trimDouble(total_revenue, 0));
                        jsonObject.addProperty("total_es14", NumberUtil.trimDouble(total_es14, 0));
                        jsonObject.addProperty("es14_dev_cost", NumberUtil.trimDouble(es14_dev_cost, 3));
                        jsonObject.addProperty("ret", 1);
                        jsonObject.addProperty("total",count);
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
