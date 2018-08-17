package com.bestgo.admanager.servlet;

import com.bestgo.admanager.utils.DateUtil;
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
import java.util.List;

/**
 * Author: mengjun
 * Date: 2018/2/20 18:06
 * Desc: 对每个项目组每个品类每个应用的投放数据的统计
 */
@WebServlet(name = "ReleasedDataStatistics", urlPatterns = {"/released_data_statistics/*"}, asyncSupported = true)
public class ReleasedDataStatistics extends BaseHttpServlet {
    private static final String[] filterApps = {"CleanV3", "WeatherV6", "PedometerV1", "PedometerV2", "BrowserV1", "AntivirusV3",
            "WeatherV3", "WeatherV8", "WeatherV9", "WeatherV11", "AntivirusV1", "CleanerV1", "CleanerV2", "BatteryV2", "SecurityV3",
            "BatteryV3", "SecurityV4", "BatteryV4", "Wifi", "Plusapplock", "ApplockV9", "ApplockV11", "VideoV9", "NewMusicV2",
            "Callflash", "callflashV5", "CallflashV7", "CallflashV9", "RecorderV1", "VideoV7", "VideoV8", "ClockV1", "ClockV2", "FlashlightV35",
            "VpnV2", "VpnV4", "VpnV5", "VpnV7", "VpnV8", "VpnV9", "BubbleV1", "HtSpiderV1", "999", "HtCollectionV1", "SudokuV3",
            "SudokuV5", "pouplarsudoku", "SolitaireYang2", "SolitaireYang3", "BingoV3", "BingoV5", "SlotsV4", "WordsearchV3", "MahjongV1",
            "CpVpn", "CpTimeback", "slimmer", "dressup", "CpAilsa", "CpBattery", "CpDressup", "Cptoyland", "SolitaireV15", "SolitaireV1",
            "Solitairev12", "Solitairev13", "SolitaireV14", "SolitaireV15", "SlotsV2", "SlotsV5", "ReversiV1", "BarcodeV1", "BarcodeV4",
            "AntivirusV9", "HoroscopeV2", "VpnV1", "VpnV3", "VpnV6", "HtSolitaireV1", "FreecellV1", "CpShuangkaiV2",
            "CpShuangkaiV3", "CoinPusher", "BarcodeV2", "BarcodeV3"};

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doPost(request, response);
        if (!Utils.isAdmin(request, response)) return;

        String path = request.getPathInfo();
        JsonObject jsonObject = new JsonObject();
        String likeCategoryName = request.getParameter("likeCategoryName");
        likeCategoryName = likeCategoryName.trim();
        String likeTeamName = request.getParameter("likeTeamName");
        likeTeamName = likeTeamName.trim();
        String nickname = request.getParameter("nickname");
        String endTime = request.getParameter("endTime");

        String radio = request.getParameter("radio");

        if (path.matches("/query_released_data_statistics")) {
            JsonArray jsonArray = new JsonArray();
            try {

                String sql = "select team_name,category_name,t.tag_name,anticipated_incoming,anticipated_revenue,t.user_id,is_display " +
                        "from web_ad_category_team ct, web_ad_tag_category tc, web_tag t " +
                        "where tc.id = t.tag_category_id and ct.id = tc.team_id and is_statistics = 1" +
                        (StringUtil.isEmpty(likeTeamName) ? " " : " and team_name like '%" + likeTeamName + "%' ") +
                        (StringUtil.isEmpty(likeCategoryName) ? " " : " and category_name like '%" + likeCategoryName + "%' ") +
                        " ORDER BY ct.id,tc.id,t.id ";
                if (StringUtil.isNotEmpty(nickname)) {
                    JSObject one = DB.findOneBySql("select id from web_ad_login_user where nickname = '" + nickname + "'");
                    if (one.hasObjectData()) {
                        long id = one.get("id");
                        sql = "select team_name,category_name,t.tag_name,anticipated_incoming,anticipated_revenue,t.user_id,is_display " +
                                "from web_ad_category_team ct, web_ad_tag_category tc, web_tag t " +
                                "where tc.id = t.tag_category_id and ct.id = tc.team_id and is_statistics = 1 and t.user_id = " + id +
                                (StringUtil.isEmpty(likeTeamName) ? " " : " and team_name like '%" + likeTeamName + "%' ") +
                                (StringUtil.isEmpty(likeCategoryName) ? " " : " and category_name like '%" + likeCategoryName + "%' ") +
                                " ORDER BY ct.id,tc.id,t.id ";
                    }
                }


                List<JSObject> listTag = DB.findListBySql(sql);
                if (listTag != null && listTag.size() > 0) {
                    for (JSObject t : listTag) {
                        if (t.hasObjectData()) {
                            Integer is_display = t.get("is_display");

                            String teamName = t.get("team_name");
                            String categoryName = t.get("category_name");
                            JsonObject d = new JsonObject();
                            d.addProperty("team_name", teamName);
                            d.addProperty("category_name", categoryName);
                            String tagName = t.get("tag_name");
                            boolean found = false;
                            for (int ii = 0; ii < filterApps.length; ii++) {
                                if (filterApps[ii].equalsIgnoreCase(tagName)) {
                                    found = true;
                                }
                            }
                            if (found) continue;

                            double anticipatedIncoming = t.get("anticipated_incoming");
                            double anticipatedRevenue = t.get("anticipated_revenue");
                            int userId = t.get("user_id");
                            JSObject user = DB.findOneBySql("select nickname from web_ad_login_user where id = " + userId);
                            if (user.hasObjectData()) {
                                String currNickname = user.get("nickname");
                                d.addProperty("nickname", currNickname);
                            } else {
                                d.addProperty("nickname", "--");
                            }
                            d.addProperty("tag_name", tagName);
                            d.addProperty("anticipated_incoming", anticipatedIncoming);
                            d.addProperty("anticipated_revenue", anticipatedRevenue);

                            for (int i = 0; i > -7; i--) {
                                double totalRevenue = 0;
                                double totalSpend = 0;
                                double totalIncoming = 0;
                                String date = DateUtil.addDay(endTime, i, "yyyy-MM-dd");
                                sql = "SELECT incoming,spend,revenue FROM web_ad_tag_released_data_statistics " +
                                        "WHERE team_name = '" + teamName + "' AND date = '" + date + "' " +
                                        "AND category_name = '" + categoryName + "' AND tag_name = '" + tagName + "'";
                                JSObject one = DB.findOneBySql(sql);
                                if (one.hasObjectData()) {
                                    totalIncoming = one.get("incoming");
                                    totalSpend = one.get("spend");
                                    totalRevenue = one.get("revenue");
                                }
                                d.addProperty("total_revenue" + i, NumberUtil.trimDouble(totalRevenue, 0));
                                d.addProperty("total_spend" + i, NumberUtil.trimDouble(totalSpend, 0));
                                d.addProperty("total_incoming" + i, NumberUtil.trimDouble(totalIncoming, 0));
                                if (totalSpend > 0 || totalRevenue >= 100) {//收入小月100刀，或者没有投放的，都隐藏掉
                                    found = false;
                                } else {
                                    found = true;
                                }
                            }
//                            if (found) continue;
                            if ("0".equals(radio)) {
                                d.addProperty("is_display",1);
                            }else {
                                if (1 == is_display){
                                    d.addProperty("is_display",1);
                                }else {
                                    d.addProperty("is_display",0);
                                }
                            }

                            jsonArray.add(d);

                        }
                    }
                }
                jsonObject.add("array", jsonArray);
                jsonObject.addProperty("ret", 1);
            } catch (Exception e) {
                jsonObject.addProperty("ret", 0);
                jsonObject.addProperty("message", e.getMessage());
            }
        }
        response.getWriter().write(jsonObject.toString());
    }

}