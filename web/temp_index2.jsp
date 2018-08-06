<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/rootBase.jsp"%>

<html>
<head>
    <title>首页</title>

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
%>

<div class="container-fluid">
    <ul class="nav nav-pills">
        <li role="presentation"><a href="index.jsp">首页</a></li>
        <li role="presentation"><a href="campaigns_create.jsp">创建广告</a></li>
        <li role="presentation"><a href="adaccounts.jsp">广告账号管理</a></li>
        <li role="presentation"><a href="adaccounts_admob.jsp">广告账号管理(AdMob)</a></li>
        <li role="presentation"><a href="campaigns.jsp">广告系列管理</a></li>
        <li role="presentation"><a href="campaigns_admob.jsp">广告系列管理(AdMob)</a></li>
        <li role="presentation"><a href="tags.jsp">标签管理</a></li>
        <li role="presentation"><a href="rules.jsp">规则</a></li>
        <li role="presentation"><a href="query.jsp">查询</a></li>
        <li role="presentation"><a href="system.jsp">系统管理</a></li>
        <li role="presentation"><a href="temp_index.jsp">临时用的</a></li>
        <li role="presentation" class="active"><a href="#">临时用的2</a></li>
    </ul>

    <div class="panel panel-default" style="margin-top: 10px">
        <div class="panel-heading" id="panel_title">
            <span>开始时间</span>
            <input type="text" value="2012-05-15" id="inputStartTime" readonly>
            <span>结束时间</span>
            <input type="text" value="2012-05-15" id="inputEndTime" readonly>
            <span>标签</span>
            <input id="inputSearch" class="form-control" style="display: inline; width: auto;" type="text"/>
            <button id="btnSearch" class="btn btn-default">查找</button>
            <button id="btnSummary" class="btn btn-default">汇总数据</button>

            <div>
                <input type="checkbox" id="emptyCampaignCheck"/><label for="emptyCampaignCheck">当前没数据的</label>
                <input type="checkbox" id="admobCheck"/><label for="admobCheck">显示admob数据</label>
                <input type="checkbox" id="countryCheck"/><label for="countryCheck">细分到国家</label>
                <input type="checkbox" id="plusAdmobCheck"/><label for="plusAdmobCheck">计算AdMob+Facebook</label>
            </div>
        </div>
    </div>
    <table class="table table-hover">
        <thead id="result_header">
        <tr>
            <th>系列ID</th>
            <th>广告账号ID</th>
            <th>系列名称</th>
            <th>创建时间</th>
            <th>状态</th>
            <th>预算</th>
            <th>竞价</th>
            <th>总花费</th>
            <th>总安装</th>
            <th>总点击</th>
            <th>CPA</th>
            <th>CTR</th>
            <th>CVR</th>
            <th>修改</th>
        </tr>
        </thead>
        <tbody id="results_body">
        </tbody>
    </table>
    <div class="panel panel-default">
        <div class="panel-body" id="total_result">
        </div>
    </div>
</div>

<div id="new_campaign_dlg" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="dlg_title">修改系列</h4>
            </div>
            <div class="modal-body">
                <form id="modify_form" class="form-horizontal" action="#" autocomplete="off">
                    <div class="form-group">
                        <label for="inputCampaignName" class="col-sm-2 control-label">系列名称</label>
                        <div class="col-sm-10">
                            <input class="form-control" id="inputCampaignName" placeholder="系列名称" autocomplete="off">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="inputStatus" class="col-sm-2 control-label">是否开启</label>
                        <div class="col-sm-10">
                            <input type="checkbox" id="inputStatus">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="inputBudget" class="col-sm-2 control-label">预算</label>
                        <div class="col-sm-10">
                            <input type="number" class="form-control" id="inputBudget" placeholder="预算" autocomplete="off">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="inputBidding" class="col-sm-2 control-label">竞价</label>
                        <div class="col-sm-10">
                            <input type="number" class="form-control" id="inputBidding" placeholder="竞价" autocomplete="off">
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-primary">确定</button>
            </div>
        </div>
    </div>
</div>

</div>

<jsp:include page="common/loading_dialog.jsp"></jsp:include>

<script>
    var campaignId;

    var data = <%=array.toString()%>;

    $("#new_campaign_dlg .btn-primary").click(function() {
        $("#new_campaign_dlg").modal("hide");
        var campaignName = $('#inputCampaignName').val();
        var status = $('#inputStatus').prop('checked') ? 'ACTIVE' : 'PAUSED';
        var budget = $('#inputBudget').val();
        var bidding = $('#inputBidding').val();

        var tags = $('#inputTags').val();

        $.post('campaign/update', {
            campaignId: campaignId,
            campaignName: campaignName,
            status: status,
            budget: budget,
            bidding: bidding
        }, function (data) {
            if (data && data.ret == 1) {
//                $("#new_campaign_dlg").modal("hide");
                $('#btnSearch').click();
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        }, 'json');
    });

    function init() {
        var now = new Date();
        $('#inputStartTime').val("2017-10-10");
        $('#inputEndTime').val("2017-10-17");
        $('#inputStartTime').datetimepicker({
            minView: "month",
            format: 'yyyy-mm-dd',
            autoclose: true,
            todayBtn: true
        });
        $('#inputEndTime').datetimepicker({
            minView: "month",
            format: 'yyyy-mm-dd',
            autoclose: true,
            todayBtn: true
        });

        $("#inputSearch").autocomplete({
            source: data
        });

        $('#btnSearch').click(function () {
            var query = $("#inputSearch").val();
            var startTime = $('#inputStartTime').val();
            var endTime = $('#inputEndTime').val();
            var emptyCampaign = $('#emptyCampaignCheck').is(':checked');
            var admobCheck = $('#admobCheck').is(':checked');
            var countryCheck = $('#countryCheck').is(':checked');
            var plusAdmobCheck = $('#plusAdmobCheck').is(':checked');

            $.post('temp_query2', {
                tag: query,
                startTime: startTime,
                endTime: endTime,
                emptyCampaign: emptyCampaign,
                admobCheck: admobCheck,
                countryCheck: countryCheck,
                plusAdmobCheck: plusAdmobCheck,
            }, function (data) {
                if (data && data.ret == 1) {
                    if (countryCheck) {
                        $('#result_header').html("<tr><th>国家</th><th>总展示</th><th>总花费</th><th>总安装</th><th>总点击</th><th>CPA</th><th>CTR</th><th>CVR</th></tr>");
                    } else if (admobCheck) {
                        $('#result_header').html("<tr><th>系列ID</th><th>广告账号ID</th><th>系列名称</th><th>创建时间<span sorterId=\"1\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>状态<span sorterId=\"2\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>预算<span sorterId=\"3\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>竞价<span sorterId=\"4\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>总花费<span sorterId=\"5\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>总安装<span sorterId=\"6\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>总点击<span sorterId=\"7\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CPA<span sorterId=\"8\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CTR<span sorterId=\"9\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CVR<span sorterId=\"10\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th></tr>");
                    } else {
                        $('#result_header').html("<tr><th>系列ID</th><th>广告账号ID</th><th>系列名称</th><th>创建时间<span sorterId=\"1\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>状态<span sorterId=\"2\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>预算<span sorterId=\"3\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>竞价<span sorterId=\"4\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>总花费<span sorterId=\"5\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>总安装<span sorterId=\"6\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>总点击<span sorterId=\"7\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CPA<span sorterId=\"8\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CTR<span sorterId=\"9\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CVR<span sorterId=\"10\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>操作</th></tr>");
                    }
                    data = data.data;
                    setData(data);
                    bindSortOp();
                    var str = "总花费: " + data.total_spend + " 总安装: " + data.total_installed +
                            " 总展示: " + data.total_impressions + " 总点击: " + data.total_click +
                                    " CTR: " + data.total_ctr + " CPA: " + data.total_cpa + " CVR: " + data.total_cvr;
                    $('#total_result').text(str);
                } else {
                    admanager.showCommonDlg("错误", data.message);
                }
            }, 'json');
        });

        $('#btnSummary').click(function () {
            var query = $("#inputSearch").val();
            var startTime = $('#inputStartTime').val();
            var endTime = $('#inputEndTime').val();
            var emptyCampaign = $('#emptyCampaignCheck').is(':checked');
            var admobCheck = $('#admobCheck').is(':checked');
            var plusAdmobCheck = $('#plusAdmobCheck').is(':checked');

            $.post('temp_query2', {
                summary: true,
                startTime: startTime,
                endTime: endTime,
                emptyCampaign: emptyCampaign,
                admobCheck: admobCheck,
                plusAdmobCheck: plusAdmobCheck,
            }, function (data) {
                if (data && data.ret == 1) {
                    $('#result_header').html("<tr><th>应用名称</th><th>总花费</th><th>总安装</th><th>总展示</th><th>总点击</th><th>CTR</th><th>CPA</th><th>CVR</th></tr>");
                    data = data.data;

                    $('#results_body > tr').remove();
                    for (var i = 0; i < data.length; i++) {
                        var one = data[i];
                        var tr = $('<tr></tr>');
                        var keyset = ["name", "total_spend", "total_installed", "total_impressions", "total_click",
                            "total_ctr", "total_cpa", "total_cvr"];
                        for (var j = 0; j < keyset.length; j++) {
                            var td = $('<td></td>');
                            td.text(one[keyset[j]]);
                            tr.append(td);
                        }
                        $('#results_body').append(tr);
                    }
                    $('#total_result').text("");
                } else {
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
            var keyset = ["campaign_id", "account_id", "campaign_name", "create_time",
                "status", "budget", "bidding", "spend", "installed", "click", "cpa", "ctr", "cvr"];
            if (countryCheck) {
                keyset = ["country_name",
                    "impressions","spend", "installed", "click", "cpa", "ctr", "cvr"];
            }
            for (var j = 0; j < keyset.length; j++) {
                var td = $('<td></td>');
                if (keyset[j] == 'budget' || keyset[j] == 'bidding') {
                    td.text(one[keyset[j]] / 100);
                } else {
                    td.text(one[keyset[j]]);
                }
                tr.append(td);
            }
            var admobCheck = $('#admobCheck').is(':checked');
            var countryCheck = $('#countryCheck').is(':checked');
            if (!admobCheck && !countryCheck) {
                var td = $('<td><a class="link_modify" href="javascript:void(0)">修改</a><a class="link_copy" href="javascript:void(0)">复制</a></td>');
                tr.append(td);
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
            var emptyCampaign = $('#emptyCampaignCheck').is(':checked');
            var admobCheck = $('#admobCheck').is(':checked');
            var countryCheck = $('#countryCheck').is(':checked');

            $.post('temp_query2', {
                tag: query,
                startTime: startTime,
                endTime: endTime,
                emptyCampaign: emptyCampaign,
                admobCheck: admobCheck,
                countryCheck: countryCheck,
                sorterId: sorterId
            }, function (data) {
                if (data && data.ret == 1) {
                    data = data.data;
                    setData(data);
                    var str = "总花费: " + data.total_spend + " 总安装: " + data.total_installed +
                            " 总展示: " + data.total_impressions + " 总点击: " + data.total_click +
                            " CTR: " + data.total_ctr + " CPA: " + data.total_cpa + " CVR: " + data.total_cvr;
                    $('#total_result').text(str);
                } else {
                    admanager.showCommonDlg("错误", data.message);
                }
            }, 'json');
        });
    }

    init();

    $('#btnSummary').click();
</script>
</body>
</html>
