<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>分析报告</title>
    <link rel="stylesheet" href="bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="bootstrap/css/bootstrap-theme.min.css"/>
    <link rel="stylesheet" href="css/core.css"/>
    <link rel="stylesheet" href="css/bootstrap-tagsinput.css"/>
    <link rel="stylesheet" href="css/bootstrap-datetimepicker.css"/>
    <link rel="stylesheet" href="jqueryui/jquery-ui.css"/>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.4/css/select2.min.css" rel="stylesheet" />

    <style>
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
        .red {
            color: red;
        }
        .green {
            color: green;
        }
        .orange{
            color: orange;
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
%>

<div class="container-fluid">
    <ul class="nav nav-pills">
        <li role="presentation"><a href="index.jsp">首页</a></li>
        <li role="presentation"><a href="campaigns_create.jsp">创建广告</a></li>
        <li role="presentation"><a href="campaigns_update_daily_log.jsp">日更记录</a></li>
        <li role="presentation" class="active"><a href="#">分析报告</a></li>
        <li role="presentation"><a href="adaccounts.jsp">广告账号管理</a></li>
        <li role="presentation"><a href="adaccounts_admob.jsp">广告账号管理(AdMob)</a></li>
        <li role="presentation"><a href="campaigns.jsp">广告系列管理</a></li>
        <li role="presentation"><a href="campaigns_admob.jsp">广告系列管理(AdMob)</a></li>
        <li role="presentation"><a href="tags.jsp">标签管理</a></li>
        <li role="presentation"><a href="rules.jsp">规则</a></li>
        <li role="presentation"><a href="query.jsp">查询</a></li>
        <li role="presentation"><a href="system.jsp">系统管理</a></li>
        <li role="presentation"><a href="advert_insert.jsp">广告存储</a></li>
        <li role="presentation"><a href="country_revenue_spend.jsp">国家收支</a></li>
        <li role="presentation"><a href="operation_log.jsp">操作日志</a></li>
        <li role="presentation"><a href="temp_index2.jsp">临时用的2</a></li>

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
    <div class="panel panel-default">
        <div class="panel-body" id="total_result">
        </div>
    </div>
    <table class="table table-hover">
        <thead id="result_header">
        <tr>
            <th>国家</th>
            <th>Cost</th>
            <th>PurchasedUser</th>
            <th>Installed</th>
            <th>Uninstalled</th>
            <th>UninstalledRate</th>
            <th>TotalUser</th>
            <th>ActiveUser</th>
            <th>Revenue</th>
            <th>ECPM</th>
            <th>Incoming</th>
            <th>EstimatedRevenue14</th>
            <th>Revenue14/Cost</th>
            <th>成本价</th>
            <th>出价</th>
            <th>CPA</th>
        </tr>
        </thead>
        <tbody id="results_body">
        </tbody>
    </table>

</div>

<jsp:include page="loading_dialog.jsp"></jsp:include>


<script src="js/jquery.js"></script>
<script src="bootstrap/js/bootstrap.min.js"></script>
<script src="js/core.js"></script>
<script src="js/bootstrap-datetimepicker.js"></script>
<script src="jqueryui/jquery-ui.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.4/js/select2.min.js"></script>
<script src="js/country-name-code-dict.js"></script>

<script>
    var now = new Date(new Date().getTime() - 86400 * 1000);
    $('#inputStartTime').val(now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate());
    $('#inputEndTime').val(now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate());
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
    var data = <%=array.toString()%>;
    $("#inputSearch").autocomplete({
        source: data
    });

    $("#btnSearch").click(function(){
        var query = $("#inputSearch").val();
        var startTime = $('#inputStartTime').val();
        var endTime = $('#inputEndTime').val();
        $.post('query_three/query_country_analysis_report', {
            tagName: query,
            startTime: startTime,
            endTime: endTime,
        },function(data){
            if(data && data.ret == 1){
                $('#result_header').html("<tr><th>国家</th><th>Cost<span sorterId=\"1031\" class=\"sorter glyphicon glyphicon-arrow-down\"></span></th><th>PurchasedUser<span sorterId=\"1032\" class=\"sorter glyphicon glyphicon-arrow-down\"></span></th><th>Installed<span sorterId=\"1033\" class=\"sorter glyphicon glyphicon-arrow-down\"></span></th><th>Uninstalled<span sorterId=\"1034\" class=\"sorter glyphicon glyphicon-arrow-down\"></span></th><th>UninstalledRate</th><th>TotalUser<span sorterId=\"1036\" class=\"sorter glyphicon glyphicon-arrow-down\"></span></th><th>ActiveUser<span sorterId=\"1037\" class=\"sorter glyphicon glyphicon-arrow-down\"></span></th><th>Revenue<span sorterId=\"1038\" class=\"sorter glyphicon glyphicon-arrow-down\"></span></th><th>ECPM</th><th>Incoming</th><th>EstimatedRevenue14<span sorterId=\"1041\" class=\"sorter glyphicon glyphicon-arrow-down\"></span></th><th>Revenue14/Cost<span sorterId=\"1042\" class=\"sorter glyphicon glyphicon-arrow-down\"></span></th><th>成本价</th><th>出价</th><th>CPA</th></tr>");
                setData(data);
                bindSortOp();
                var str = "Cost: " + data.total_cost + "&nbsp;&nbsp;&nbsp;&nbsp;PuserchaedUser: " + data.total_puserchaed_user +
                    "&nbsp;&nbsp;&nbsp;&nbsp;CPA: " + data.total_cpa + "&nbsp;&nbsp;&nbsp;&nbsp;Revenue: " + data.total_revenue +
                    "&nbsp;&nbsp;&nbsp;&nbsp;Es14: " + data.total_es14 + "&nbsp;&nbsp;&nbsp;&nbsp;Es14/Cost: " + data.es14_dev_cost;
                str += "<br/><span class='estimateResult'></span>"
                $('#total_result').html(str);
                $('#total_result').removeClass("editable");
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        },'json');
    });
    function bindSortOp() {
        $('.sorter').click(function() {
            var sorterId = $(this).attr('sorterId');
            sorterId = parseInt(sorterId);
            if ($(this).hasClass("glyphicon-arrow-down")) {
                $(this).removeClass("glyphicon-arrow-down");
                $(this).addClass("glyphicon-arrow-up");
                sorterId -= 1000;
            } else {
                $(this).removeClass("glyphicon-arrow-up");
                $(this).addClass("glyphicon-arrow-down");
            }

            var query = $("#inputSearch").val();
            var startTime = $('#inputStartTime').val();
            var endTime = $('#inputEndTime').val();
            $.post('query_three/query_country_analysis_report', {
                tagName: query,
                startTime: startTime,
                endTime: endTime,
                sorterId: sorterId
            },function(data){
                if (data && data.ret == 1) {
                    setData(data);
                    var str = "Cost: " + data.total_cost + "&nbsp;&nbsp;&nbsp;&nbsp;PuserchaedUser: " + data.total_puserchaed_user +
                        "&nbsp;&nbsp;&nbsp;&nbsp;CPA: " + data.total_cpa + "&nbsp;&nbsp;&nbsp;&nbsp;Revenue: " + data.total_revenue +
                        "&nbsp;&nbsp;&nbsp;&nbsp;Es14: " + data.total_es14 + "&nbsp;&nbsp;&nbsp;&nbsp;Es14/Cost: " + data.es14_dev_cost;

                    str += "<br/><span class='estimateResult'></span>"
                    $('#total_result').removeClass("editable");
                    $('#total_result').html(str);
                } else {
                    admanager.showCommonDlg("错误", data.message);
                }
            }, 'json');
        });
    }
    function setData(data) {
        $('#results_body > tr').remove();
        var arr = data.array;
        var len = arr.length;
        for (var i = 0; i < len; i++) {
            var one = arr[i];
            var tr = $('<tr></tr>');

            var keyset = ["country_name", "costs", "purchased_users", "installed",
                "uninstalled", "uninstalled_rate", "users", "active_users", "revenues",
                "ecpm", "incoming", "estimated_revenues","estimated_revenues_dev_cost","price","bidding","cpa"];
            for (var j = 0; j < keyset.length; j++) {
                var td = $('<td></td>');
                var r = one[keyset[j]];
                if('incoming' == keyset[j]){
                    if(r <0){
                        td.addClass("red");
                    }
                }else if('estimated_revenues_dev_cost' == keyset[j]){
                    if(r > data.es14_dev_cost){
                        td.addClass("green");
                    }else if(r < data.es14_dev_cost){
                        td.addClass("orange");
                    }
                }
                td.text(r);
                tr.append(td);
            }
            $('#results_body').append(tr);
        }
    }
</script>
<script src="js/interlaced-color-change.js"></script>
</body>
</html>
