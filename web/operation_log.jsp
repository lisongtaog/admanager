<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>操作日志</title>
    <link rel="stylesheet" href="bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="bootstrap/css/bootstrap-theme.min.css"/>
    <link rel="stylesheet" href="css/core.css"/>
    <link rel="stylesheet" href="css/bootstrap-tagsinput.css"/>
    <link rel="stylesheet" href="css/bootstrap-datetimepicker.css"/>
    <link rel="stylesheet" href="jqueryui/jquery-ui.css"/>

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
        <li role="presentation"><a href="advert_insert.jsp">广告存储</a></li>
        <li role="presentation" class="active"><a href="#">操作日志</a></li>
        <li role="presentation"><a href="temp_index2.jsp">临时用的2</a></li>
    </ul>

    <div class="panel panel-default" style="margin-top: 10px">
        <div class="panel-heading" id="panel_title">
            <span>标签</span>
            <input id="inputSearch" class="form-control" style="display: inline; width: auto;" type="text"/>
            <span>国家</span>
            <input id="inputCountry" class="form-control" style="display: inline; width: auto;" type="text"/>
            <button id="btnSearch" class="btn btn-default glyphicon glyphicon-search"></button>
        </div>
    </div>
    <table class="table table-hover">
        <thead id="result_header">
        <tr>
            <th>操作时间</th>
            <th>系列ID</th>
            <th>系列名称</th>
            <th>操作日志</th>
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

</div>

<jsp:include page="loading_dialog.jsp"></jsp:include>


<script src="js/jquery.js"></script>
<script src="bootstrap/js/bootstrap.min.js"></script>
<script src="js/core.js"></script>
<script src="js/bootstrap-datetimepicker.js"></script>
<script src="jqueryui/jquery-ui.min.js"></script>
<script src="js/country-name-code-dict.js"></script>
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
    var countryNames = [];
    for (var i = 0; i < regionList.length; i++) {
        countryNames.push(regionList[i].name);
    }

    function init() {
        $("#inputCountry").autocomplete({
            source: countryNames
        });

        $("#inputSearch").autocomplete({
            source: data
        });

        $('#btnSearch').click(function () {
            var query = $("#inputSearch").val();
            var countryName = $('#inputCountry').val();
            $.post('query_two/query_operation_log', {
                tagName: query,
                countryName: countryName
            }, function (data) {
                if (data && data.ret == 1) {
                    $('#result_header').html("<tr><th>操作时间</th><th>系列ID</th><th>操作日志</th></tr>");
                    $('#results_body > tr').remove();
                    for (var i = 0; i < data.array.length; i++) {
                        var one = data.array[i];
                        var tr = $('<tr></tr>');
                        var keyset = ["operation_date", "campaign_id", "details_text"];
                        for (var j = 0; j < keyset.length; j++) {
                            var td = $('<td></td>');
                            td.text(one[keyset[j]]);
                            tr.append(td);
                        }
                        $('#results_body').append(tr);
                    }
                } else {
                    admanager.showCommonDlg("错误", data.message);
                }
            }, 'json');
        });

    }

    init();
</script>
</body>
</html>
