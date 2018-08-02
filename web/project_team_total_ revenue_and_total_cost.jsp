<%@ page import="com.bestgo.admanager.utils.LoginUserSessionCacheUtil" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/rootBase.jsp"%>

<html>
<head>
    <title>项目组总收支</title>

    <style>
        .green {
            color: green;
        }


        .blue {
            color: #1e5980;
        }

        .pink {
            color: #ff615d;
        }


        .orange {
            color: orange;
        }

    </style>
</head>
<body>

<%
    LoginUserSessionCacheUtil.loadSessionFromCache(application, session);
    Object object = session.getAttribute("isAdmin");
    if (object == null) {
        response.sendRedirect("login.jsp");
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
            <button id="btnSearch" class="btn btn-default glyphicon glyphicon-search"></button><br>
        </div>
    </div>
    <%--<div class="panel panel-default">--%>
        <%--<div class="panel-body" id="total_result">--%>
        <%--</div>--%>
    <%--</div>--%>
    <table class="table table-hover">
        <thead id="result_header">

        </thead>
        <tbody id="results_body">
        </tbody>
    </table>

</div>

<jsp:include page="loading_dialog.jsp"></jsp:include>

<script src="js/country-name-code-dict.js"></script>

<script>
    function init() {
        $("li[role='presentation']:eq(9)").addClass("active");

        var now = new Date(new Date().getTime() - 86400 * 1000);
        $('#inputStartDate').val(now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate());
        $('#inputEndDate').val(now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate());
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
    $("#btnSearch").click(function(){
        var endDate = $('#inputEndDate').val();
        var startDate = $('#inputStartDate').val();
        $('#result_header > tr').remove();
        $('#results_body > tr').remove();
        var loadingIndex = layer.load(2,{time: 10000});
        $.post('project_team_total_revenue_and_total_cost/query_project_team_total_revenue_and_total_cost', {
            endDate: endDate,
            startDate: startDate
        },function(data){
            layer.close(loadingIndex);
            if(data && data.ret == 1){
                $('#result_header').html("<tr><th>项目组</th><th>总花费</th><th>总收入</th><th>总盈利</th></tr>");
                setData(data);
//                    bindSortOp();
//                    var str = "Cost: " + data.total_cost + "&nbsp;&nbsp;&nbsp;&nbsp;PuserchaedUser: " + data.total_puserchaed_user +
//                        "&nbsp;&nbsp;&nbsp;&nbsp;CPA: " + data.total_cpa + "&nbsp;&nbsp;&nbsp;&nbsp;Revenue: " + data.total_revenue +
//                        "&nbsp;&nbsp;&nbsp;&nbsp;Es14: " + data.total_es14 + "&nbsp;&nbsp;&nbsp;&nbsp;Es14/Cost: " + data.es14_dev_cost;
//                    str += "<br/><span class='estimateResult'></span>"
//                    $('#total_result').html(str);
//                    $('#total_result').removeClass("editable");
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        },'json');
    });

    function setData(data) {
        var arr = data.array;
        var len = arr.length;

        for(var i=0;i<len;i++){
            var tr = $('<tr></tr>');
            var one = arr[i];
            var teamName = one['team_name'];
            var totalSpends = one['total_spends'];
            var totalRevenues = one['total_revenues'];
            var totalIncomings = one['total_incomings'];

            var td = $("<td class='pink'></td>");
            td.text(teamName);
            tr.append(td);

            td = $("<td class='orange'></td>");
            td.text(totalSpends);
            tr.append(td);

            td = $("<td class='blue'></td>");
            td.text(totalRevenues);
            tr.append(td);

            td = $("<td class='green'></td>");
            td.text(totalIncomings);
            tr.append(td);
            $('#results_body').append(tr);
        }

    }
    init();
</script>
</body>
</html>
