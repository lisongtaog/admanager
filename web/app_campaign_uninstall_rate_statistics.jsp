<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page import="com.bestgo.admanager.utils.Utils" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.bestgo.admanager.utils.DateUtil" %>
<%@ page import="com.google.gson.JsonObject" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/rootBase.jsp" %>
<html>
<head>
    <title>系列卸载率报告</title>

    <style>
        .green {
            color: #ffffff !important;
            background: #62ff63 !important;
            text-decoration: none;
        }

        td.editable {

        }

        td.editable.checkbox {

        }

        td.changed {
            background-color: #0f0;
        }

        #total_result.editable {
            background-color: yellow;
        }

        .estimateResult {
            color: red;
        }
    </style>
</head>
<body>

<%

    Object object = session.getAttribute("isAdmin");
    if (object == null) {
        response.sendRedirect("login.jsp");
    }

    JsonArray array = new JsonArray();

    List<JSObject> jsObjects = Tags.fetchAllTags();
    for (int i = 0; i < jsObjects.size(); i++) {
        array.add(jsObjects.get(i).get("tag_name").toString());
    }

    HashMap<String, String> countryCodeNameMap = Utils.getCountryCodeNameMap();
    JsonArray array2 = new JsonArray();
    JsonObject one = null;
    for (Map.Entry<String, String> entry : countryCodeNameMap.entrySet()) {
        one = new JsonObject();
        one.addProperty("value", entry.getKey());
        one.addProperty("label", entry.getValue());
        array2.add(one);
    }
%>

<div class="container-fluid">
    <%@include file="common/navigationbar.jsp" %>

    <div class="panel panel-default" style="margin-top: 10px">
        <div class="panel-heading" id="panel_title">
            <span>开始日期</span>
            <input type="text" value="2012-05-15" id="inputStartDate" readonly>
            <span>结束日期</span>
            <input type="text" value="2012-05-15" id="inputEndDate" readonly>
            <span>标签</span>
            <input id="inputSearchTagName" class="form-control" style="display: inline; width: auto;" type="text"
                   autocomplete="on"/>
            <span>国家</span>
            <input id="inputSearchCountry" class="form-control" style="display: inline; width: auto;" type="text"
                   autocomplete="on"/>
            <label for="inputLikeCampaignName">系列名称</label>
            <input type="text" id="inputLikeCampaignName" class="form-control" style="display: inline; width: auto;"/>
            <button id="btnSearch" class="btn btn-default glyphicon glyphicon-search"></button>
        </div>
    </div>

    <table class="table table-hover">
        <thead id="result_header">
        </thead>
        <tbody id="results_body">
        </tbody>
    </table>
</div>

<jsp:include page="common/loading_dialog.jsp"></jsp:include>

<script>
    $("li[role='presentation']:eq(2)").addClass("active");

    var data = <%=array.toString()%>;
    $("#inputSearchTagName").autocomplete({
        source: data
    });
    data = <%=array2.toString()%>;
    $("#inputSearchCountry").autocomplete({
        source: data
    });

    function init() {
        var now = new Date();
        var day = now.getDate();
        //如果是夏令时小于16，其他时小于15
        if (now.getHours() < 15) {
            day = now.getDate() - 1;
        }
        $('#inputStartDate').val(now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + day);
        $('#inputEndDate').val(now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + day);

        $('#inputStartDate').datetimepicker({
            minView: "month",
            format: 'yyyy-mm-dd',
            autoclose: true,
            todayBtn: true
        });
        $('#inputEndDate').datetimepicker({
            minView: "month",
            format: 'yyyy-mm-dd',
            autoclose: true,
            todayBtn: true
        });

    }

    init();

    $('#btnSearch').click(function () {
        $('#btnSearch').prop("disabled", true);

        var tagName = $('#inputSearchTagName').val();
        var countryName = $('#inputSearchCountry').val();
        var likeCampaignName = $('#inputLikeCampaignName').val();


        var startTime = $('#inputStartDate').val();
        var endTime = $('#inputEndDate').val();

        $.post('app_campaign_uninstall_rate_statistics/query_app_campaign_uninstall_rate_statistics', {
            tagName: tagName,
            countryName: countryName,
            likeCampaignName: likeCampaignName,
            startTime: startTime,
            endTime: endTime
        }, function (data) {
            if (data) {
                $('#results_body > tr').remove();
                $('#result_header').html("<tr><th>ID</th><th>应用名称</th><th>系列名称</th><th>国家</th><th>安装日期</th><th>卸载率</th></tr>");

                for (var i = 0; i < data.length; i++) {
                    var one = data[i];

                    var tr = $('<tr></tr>');
                    var keySet = ["id", "appId", "campaignName", "countryCode", "installedDate", "uninstallRate"];

                    for (var j = 0; j < keySet.length; j++) {
                        var e = one[keySet[j]];
                        var td = $('<td></td>');
                        td.text(e);
                        tr.append(td);
                    }

                    $('#results_body').append(tr);
                }
                $('#btnSearch').prop("disabled", false);
            } else {
                admanager.showCommonDlg("错误", "没找到数据！！");
                $('#btnSearch').prop("disabled", false);
            }
        }, 'json');
    });

    //     function setData(data) {
        //         $('#results_body > tr').remove();
        //         for (var i = 0; i < data.array.length; i++) {
        //             var one = data.array[i];
        //             var tr = $('<tr></tr>');
        //             var countryCheck = $('#countryCheck').is(':checked');
        //             var keyset = ["campaign_id", "account_id", "short_name", "campaign_name", "create_time",
        //                 "status", "budget", "bidding", "spend", "installed", "click", "cpa", "ctr", "cvr"];
        //             var modifyColumns = ["campaign_name", "budget", "bidding"];
        //             if (countryCheck) {
        //                 keyset = ["country_name",
        //                     "impressions", "spend", "installed", "click", "cpa", "ctr", "cvr"];
        //             }
        //             for (var j = 0; j < keyset.length; j++) {
        //                 var campaignId = one['campaign_id'];
        //                 var totalSpend = 0;
        //                 for (var jj = 0; jj < appQueryData.length; jj++) {
        //                     if (appQueryData[jj]['campaign_id'] == campaignId) {
        //                         totalSpend = appQueryData[jj]['spend'];
        //                         break;
        //                     }
        //                 }
        //                 var td = $('<td></td>');
        //                 if (keyset[j] == 'budget' || keyset[j] == 'bidding') {
        //                     td.text(one[keyset[j]] / 100);
        //                 } else {
        //                     if (keyset[j] == 'spend') {
        //                         td.text(one[keyset[j]] + " / " + totalSpend);
        //                     } else {
        //                         td.text(one[keyset[j]]);
        //                     }
        //                 }
        //                 if (modifyColumns.indexOf(keyset[j]) != -1) {
        //                     td.addClass("editable");
        //                     td[0].cloumnName = keyset[j];
        //                 }
        //                 tr.append(td);
        //             }
        //             tr[0].origCampaignData = one;
        //             tr[0].changedCampainData = {};
        //             var admobCheck = $('#admobCheck').is(':checked');
        //             var countryCheck = $('#countryCheck').is(':checked');
        //             if (!admobCheck && !countryCheck) {
        // //                var td = $('<td><a class="link_modify" href="javascript:void(0)">修改</a><a class="link_copy" href="javascript:void(0)">复制</a></td>');
        // //                tr.append(td);
        //             }
        //             $('#results_body').append(tr);
        //         }
        //         bindOp();
        //     }

    // function bindOp() {
    //     $(".link_modify").click(function() {
    //         $('#modify_form').show();
    //
    //         $("#dlg_title").text("修改系列");
    //
    //         var tds = $(this).parents("tr").find('td');
    //         campaignId = $(tds.get(0)).text();
    //         var campaignName = $(tds.get(2)).text();
    //         var status = $(tds.get(4)).text();
    //         var budget = $(tds.get(5)).text();
    //         var bidding = $(tds.get(6)).text();
    //
    //         $('#inputCampaignName').val(campaignName);
    //         if (status.toLowerCase() == 'active') {
    //             $('#inputStatus').prop('checked', true);
    //         } else {
    //             $('#inputStatus').prop('checked', false);
    //         }
    //         $('#inputBudget').val(budget);
    //         $('#inputBidding').val(bidding);
    //
    //         $("#new_campaign_dlg").modal("show");
    //     });
    //
    //     $(".link_copy").click(function() {
    //         var tds = $(this).parents("tr").find('td');
    //         $.post('campaign/find_create_data', {
    //             campaignId: $(tds.get(0)).text(),
    //         }, function (data) {
    //             if (data && data.ret == 1) {
    //                 var list = [];
    //                 var keys = ["tag_name", "app_name", "facebook_app_id", "account_id", "country_region",
    //                     "language","age", "gender", "detail_target", "campaign_name", "page_id", "bugdet", "bidding", "max_cpa", "title", "message"];
    //                 var data = data.data;
    //                 for (var i = 0; i < keys.length; i++) {
    //                     list.push(data[keys[i]]);
    //                 }
    //                 admanager.showCommonDlg("请手动创建", list.join("\t"));
    //             } else {
    //                 admanager.showCommonDlg("错误", data.message);
    //             }
    //         }, 'json');
    //     });
    // }

    // function bindSortOp() {
    //     $('.sorter').click(function () {
    //         var sorterId = $(this).attr('sorterId');
    //         sorterId = parseInt(sorterId);
    //         if ($(this).hasClass("glyphicon-arrow-up")) {
    //             $(this).removeClass("glyphicon-arrow-up");
    //             $(this).addClass("glyphicon-arrow-down");
    //             sorterId += 1000;
    //         } else {
    //             $(this).removeClass("glyphicon-arrow-down");
    //             $(this).addClass("glyphicon-arrow-up");
    //         }
    //
    //         var query = $("#inputSearch").val();
    //         var startTime = $('#inputStartTime').val();
    //         var endTime = $('#inputEndTime').val();
    //         var countryName = $('#inputCountry').val();
    //         var countryCode = '';
    //         var adwordsCheck = $('#adwordsCheck').is(':checked');
    //         var countryCheck = $('#countryCheck').is(':checked');
    //         var facebookCheck = $('#facebookCheck').is(':checked');
    //         for (var i = 0; i < regionList.length; i++) {
    //             if (countryName == regionList[i].name) {
    //                 countryCode = regionList[i].country_code;
    //                 break;
    //             }
    //         }
    //
    //         $.post('query', {
    //             tag: query,
    //             startTime: startTime,
    //             endTime: endTime,
    //             adwordsCheck: adwordsCheck,
    //             countryCheck: countryCheck,
    //             facebookCheck: facebookCheck,
    //         }, function (data) {
    //             if (data && data.ret == 1) {
    //                 appQueryData = data.data.array;
    //                 $.post('query', {
    //                     tag: query,
    //                     startTime: startTime,
    //                     endTime: endTime,
    //                     adwordsCheck: adwordsCheck,
    //                     countryCheck: countryCheck,
    //                     facebookCheck: facebookCheck,
    //                     countryCode: countryCode,
    //                     sorterId: sorterId
    //                 }, function (data) {
    //                     if (data && data.ret == 1) {
    //                         data = data.data;
    //                         setData(data);
    //                         var str = "总花费: " + data.total_spend + " 总安装: " + data.total_installed +
    //                             " 总展示: " + data.total_impressions + " 总点击: " + data.total_click +
    //                             " CTR: " + data.total_ctr + " CPA: " + data.total_cpa + " CVR: " + data.total_cvr;
    //
    //                         str += "<br/><span class='estimateResult'></span>"
    //                         $('#total_result').removeClass("editable");
    //                         $('#total_result').html(str);
    //                     } else {
    //                         admanager.showCommonDlg("错误", data.message);
    //                     }
    //                 }, 'json');
    //             }
    //         }, 'json');
    //     });
    // }


</script>
</body>
</html>