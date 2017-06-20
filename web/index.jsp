<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>首页</title>
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
        <li role="presentation" class="active"><a href="#">首页</a></li>
        <li role="presentation"><a href="adaccounts.jsp">广告账号管理</a></li>
        <li role="presentation"><a href="campaigns.jsp">广告系列管理</a></li>
        <li role="presentation"><a href="tags.jsp">标签管理</a></li>
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
        </div>
    </div>
    <table class="table table-hover">
        <thead>
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

<script>
    var data = <%=array.toString()%>;

    function init() {
        var now = new Date();
        $('#inputStartTime').val(now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate());
        $('#inputEndTime').val(now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate());
        $('#inputStartTime').datetimepicker({
            format: 'yyyy-mm-dd',
            autoclose: true,
            todayBtn: true
        });
        $('#inputEndTime').datetimepicker({
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
            $.post('query', {
                tag: query,
                startTime: startTime,
                endTime: endTime
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

        function setData(data) {
            $('#results_body > tr').remove();
            for (var i = 0; i < data.array.length; i++) {
                var one = data.array[i];
                var tr = $('<tr></tr>');
                var keyset = ["campaign_id", "account_id", "campaign_name", "create_time",
                    "status", "budget", "bidding", "spend", "installed", "click", "cpa", "ctr", "cvr"];
                for (var j = 0; j < keyset.length; j++) {
                    var td = $('<td></td>');
                    if (keyset[j] == 'budget' || keyset[j] == 'bidding') {
                        td.text(one[keyset[j]] / 100);
                    } else {
                        td.text(one[keyset[j]]);
                    }
                    tr.append(td);
                }
                $('#results_body').append(tr);
            }
        }
    }

    init();
</script>
</body>
</html>
