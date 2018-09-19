<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page import="com.bestgo.admanager.utils.Utils" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.bestgo.admanager.utils.DateUtil" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/rootBase.jsp"%>
<html>
<head>
    <title>系列卸载率报告</title>

    <style>
        .green {
            color: #ffffff !important;
            background:#62ff63 !important;
            text-decoration:none;
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

    List<JSObject> allTags = Tags.fetchAllTags();
    JsonArray array = new JsonArray();
    for (int i = 0; i < allTags.size(); i++) {
        array.add((String) allTags.get(i).get("tag_name"));
    }

    HashMap<String,String> countryCodeNameMap = Utils.getCountryCodeNameMap();
    JsonArray array2 = new JsonArray();
    for (Map.Entry<String,String> entry : countryCodeNameMap.entrySet()) {
        array2.add(entry.getValue());
    }
%>

<div class="container-fluid">
    <%@include file="common/navigationbar.jsp"%>

    <div class="panel panel-default" style="margin-top: 10px">
        <div class="panel-heading" id="panel_title">
            <span>开始日期</span>
            <input type="text" value="2012-05-15" id="inputStartDate" readonly>
            <span>结束日期</span>
            <input type="text" value="2012-05-15" id="inputEndDate" readonly>
            <span>标签</span>
            <input id="inputSearch" class="form-control" style="display: inline; width: auto;" type="text"/>
            <span>国家</span>
            <input id="inputSearchCountry" class="form-control" style="display: inline; width: auto;" type="text"/>
            <label for="inputLikeCampaignName">系列名称</label>
            <input type="text" id="inputLikeCampaignName" class="form-control" style="display: inline; width: auto;" />
            <button id="btnSearch" class="btn btn-default glyphicon glyphicon-search"></button>
        </div>
    </div>
    <%--<div class="panel panel-default">
        <div class="panel-body" id="total_result">
        </div>
    </div>--%>
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
        


        $('#btnSearch').click(function () {
            var tag_name = $("#inputSearch").val();
            var startTime = $('#inputStartTime').val();
            var endTime = $('#inputEndTime').val();
            $.post('country_revenue_spend', {
                tag_name: tag_name,
                startTime: startTime,
                endTime: endTime
            }, function (data) {
                if (data && data.ret == 1) {
                    var arr = data.arr;
                    $('#results_body > tr').remove();
                    $('#result_header').html("<tr><th>国家名称</th><th>总收入</th><th>总花费</th><th>总盈利</th><th>总安装</th><th>CPA</th><th>国家对应的系列</th></tr>");

                    for (var i = 0; i < arr.length; i++) {
                        var one = arr[i];

                        var tr = $('<tr></tr>');
                        var keySet = ["country_code", "revenue", "spend", "incoming", "installed", "cpa"];
                        var country_code = '';
                        var country_name = '';
                        for (var j = 0; j < keySet.length; j++) {
                            var e = one[keySet[j]];
                            var td = $('<td></td>');
                            if(j == 0){
                                country_code = e;
                                country_name = countryMap[e];
                                td.text(country_name);
                            }else{
                                td.text(e);
                            }
                            tr.append(td);
                        }

                        tr.append("<td><a class='ui-button' href=\"index.jsp?tag_name=" +tag_name+"&startTime="+startTime+"&endTime="+endTime+"&country_code="+country_code +  "\" class='ui-button'  target='_blank'>详情</a></td>");

                        $('#results_body').append(tr);
                    }
                    $(".ui-button").click(function(){
                        $(this).addClass("green");
                    });
                }else {
                    admanager.showCommonDlg("错误", data.message);
                }
            }, 'json');
        });

    }


    function setData(data) {
        $('#results_body > tr').remove();
        for (var i = 0; i < data.array.length; i++) {
            var one = data.array[i];
            var tr = $('<tr></tr>');
            var countryCheck = $('#countryCheck').is(':checked');
            var keyset = ["campaign_id", "account_id", "short_name","campaign_name", "create_time",
                "status", "budget", "bidding", "spend", "installed", "click", "cpa", "ctr", "cvr"];
            var modifyColumns = ["campaign_name", "budget", "bidding"];
            if (countryCheck) {
                keyset = ["country_name",
                    "impressions","spend", "installed", "click", "cpa", "ctr", "cvr"];
            }
            for (var j = 0; j < keyset.length; j++) {
                var campaignId = one['campaign_id'];
                var totalSpend = 0;
                for (var jj = 0; jj < appQueryData.length; jj++) {
                    if (appQueryData[jj]['campaign_id'] == campaignId) {
                        totalSpend = appQueryData[jj]['spend'];
                        break;
                    }
                }
                var td = $('<td></td>');
                if (keyset[j] == 'budget' || keyset[j] == 'bidding') {
                    td.text(one[keyset[j]] / 100);
                } else {
                    if (keyset[j] == 'spend') {
                        td.text(one[keyset[j]] + " / " + totalSpend);
                    } else {
                        td.text(one[keyset[j]]);
                    }
                }
                if (modifyColumns.indexOf(keyset[j]) != -1) {
                    td.addClass("editable");
                    td[0].cloumnName = keyset[j];
                }
                tr.append(td);
            }
            tr[0].origCampaignData = one;
            tr[0].changedCampainData = {};
            var admobCheck = $('#admobCheck').is(':checked');
            var countryCheck = $('#countryCheck').is(':checked');
            if (!admobCheck && !countryCheck) {
//                var td = $('<td><a class="link_modify" href="javascript:void(0)">修改</a><a class="link_copy" href="javascript:void(0)">复制</a></td>');
//                tr.append(td);
            }
            $('#results_body').append(tr);
        }
        bindOp();
    }

    function bindOp() {
        $(".link_modify").click(function() {
            $('#modify_form').show();

            $("#dlg_title").text("修改系列");

            var tds = $(this).parents("tr").find('td');
            campaignId = $(tds.get(0)).text();
            var campaignName = $(tds.get(2)).text();
            var status = $(tds.get(4)).text();
            var budget = $(tds.get(5)).text();
            var bidding = $(tds.get(6)).text();

            $('#inputCampaignName').val(campaignName);
            if (status.toLowerCase() == 'active') {
                $('#inputStatus').prop('checked', true);
            } else {
                $('#inputStatus').prop('checked', false);
            }
            $('#inputBudget').val(budget);
            $('#inputBidding').val(bidding);

            $("#new_campaign_dlg").modal("show");
        });

        $(".link_copy").click(function() {
            var tds = $(this).parents("tr").find('td');
            $.post('campaign/find_create_data', {
                campaignId: $(tds.get(0)).text(),
            }, function (data) {
                if (data && data.ret == 1) {
                    var list = [];
                    var keys = ["tag_name", "app_name", "facebook_app_id", "account_id", "country_region",
                        "language","age", "gender", "detail_target", "campaign_name", "page_id", "bugdet", "bidding", "max_cpa", "title", "message"];
                    var data = data.data;
                    for (var i = 0; i < keys.length; i++) {
                        list.push(data[keys[i]]);
                    }
                    admanager.showCommonDlg("请手动创建", list.join("\t"));
                } else {
                    admanager.showCommonDlg("错误", data.message);
                }
            }, 'json');
        });
    }

    function bindSortOp() {
        $('.sorter').click(function() {
            var sorterId = $(this).attr('sorterId');
            sorterId = parseInt(sorterId);
            if ($(this).hasClass("glyphicon-arrow-up")) {
                $(this).removeClass("glyphicon-arrow-up");
                $(this).addClass("glyphicon-arrow-down");
                sorterId += 1000;
            } else {
                $(this).removeClass("glyphicon-arrow-down");
                $(this).addClass("glyphicon-arrow-up");
            }

            var query = $("#inputSearch").val();
            var startTime = $('#inputStartTime').val();
            var endTime = $('#inputEndTime').val();
            var countryName = $('#inputCountry').val();
            var countryCode = '';
            var adwordsCheck = $('#adwordsCheck').is(':checked');
            var countryCheck = $('#countryCheck').is(':checked');
            var facebookCheck = $('#facebookCheck').is(':checked');
            for (var i = 0; i < regionList.length; i++) {
                if (countryName == regionList[i].name) {
                    countryCode = regionList[i].country_code;
                    break;
                }
            }

            $.post('query', {
                tag: query,
                startTime: startTime,
                endTime: endTime,
                adwordsCheck: adwordsCheck,
                countryCheck: countryCheck,
                facebookCheck: facebookCheck,
            }, function (data) {
                if (data && data.ret == 1) {
                    appQueryData = data.data.array;
                    $.post('query', {
                        tag: query,
                        startTime: startTime,
                        endTime: endTime,
                        adwordsCheck: adwordsCheck,
                        countryCheck: countryCheck,
                        facebookCheck: facebookCheck,
                        countryCode: countryCode,
                        sorterId: sorterId
                    }, function (data) {
                        if (data && data.ret == 1) {
                            data = data.data;
                            setData(data);
                            var str = "总花费: " + data.total_spend + " 总安装: " + data.total_installed +
                                " 总展示: " + data.total_impressions + " 总点击: " + data.total_click +
                                " CTR: " + data.total_ctr + " CPA: " + data.total_cpa + " CVR: " + data.total_cvr;

                            str += "<br/><span class='estimateResult'></span>"
                            $('#total_result').removeClass("editable");
                            $('#total_result').html(str);
                        } else {
                            admanager.showCommonDlg("错误", data.message);
                        }
                    }, 'json');
                }
            }, 'json');
        });
    }

    init();
    var data = <%=array.toString()%>;
    $("#inputSearch").autocomplete({
        source: data
    });
    data = <%=array2.toString()%>;
    $("#inputSearchCountry").autocomplete({
        source: data
    });
</script>
</body>
</html>