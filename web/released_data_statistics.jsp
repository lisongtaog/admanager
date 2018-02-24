<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>投放数据统计</title>
    <link rel="stylesheet" href="bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="bootstrap/css/bootstrap-theme.min.css"/>
    <link rel="stylesheet" href="css/core.css"/>
    <link rel="stylesheet" href="css/bootstrap-tagsinput.css"/>
    <link rel="stylesheet" href="css/bootstrap-datetimepicker.css"/>
    <link rel="stylesheet" href="jqueryui/jquery-ui.css"/>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.4/css/select2.min.css" rel="stylesheet" />

    <style>
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
    <%@include file="common/navigationbar.jsp"%>

    <div class="panel panel-default" style="margin-top: 10px">
        <div class="panel-heading" id="panel_title">
            <span>结束日期</span>
            <input type="text" value="2012-05-15" id="inputEndTime" readonly>
            <button id="btnSearch" class="btn btn-default glyphicon glyphicon-search"></button>
        </div>
    </div>
    <div class="panel panel-default">
        <div class="panel-body" id="total_result">
        </div>
    </div>
    <table class="table table-hover">
        <thead id="result_header">
        <%--<tr>--%>
            <%--<th rowspan="2">项目组</th>--%>
            <%--<th rowspan="2">Category</th>--%>
            <%--<th rowspan="2">AppName</th>--%>
            <%--<th></th>--%>
            <%--<th></th>--%>
            <%--<th colspan="3" id="dateA">now</th><th colspan="3" id="dateB">now-1</th><th colspan="3" id="dateC">now-2</th>--%>
            <%--<th colspan="3" id="dateD">now-3</th><th colspan="3" id="dateE">now-4</th><th colspan="3" id="dateF">now-5</th>--%>
            <%--<th colspan="3" id="dateG">now-6</th>--%>
        <%--</tr>--%>
        <%--<tr>--%>
            <%--<th>预计利润</th><th>预计收入</th><th>【利润</th><th>投放</th><th>收入】</th><th>【利润</th><th>投放</th>--%>
            <%--<th>收入】</th><th>【利润</th><th>投放</th><th>收入】</th><th>【利润</th><th>投放</th><th>收入】</th>--%>
            <%--<th>【利润</th><th>投放</th><th>收入】</th><th>【利润</th><th>投放</th><th>收入】</th><th>【利润</th>--%>
            <%--<th>投放</th><th>收入】</th>--%>
        <%--</tr>--%>
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
    function init() {

    function getDate(dd,AddDayCount) {
        dd.setDate(dd.getDate()+AddDayCount);//获取AddDayCount天后的日期
        var y = dd.getFullYear();
        var m = dd.getMonth()+1;//获取当前月份的日期
        var d = dd.getDate();
        return y+"-"+(m<10?"0"+m:m)+"-"+(d<10?"0"+d:d);
    }

        $("li[role='presentation']:eq(3)").addClass("active");
        var now = new Date(new Date().getTime() - 86400 * 1000);
        var nowDateStr = now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate();
        $('#inputEndTime').val(nowDateStr);


        $('#inputEndTime').datetimepicker({
            minView: "month",
            format: 'yyyy-mm-dd',
            autoclose: true,
            todayBtn: true
        });

        $("#btnSearch").click(function(){
            var endTime = $('#inputEndTime').val();
            $.post('released_data_statistics/query_released_data_statistics', {
                endTime: endTime,
            },function(data){
                if(data && data.ret == 1){
                    $('#result_header').html("<tr><th rowspan=\"2\">项目组</th><th rowspan=\"2\">Category</th><th rowspan=\"2\">AppName</th>" +
                        "<th></th><th></th><th colspan=\"3\" id=\"dateA\"></th><th colspan=\"3\" id=\"dateB\"></th><th colspan=\"3\" id=\"dateC\"></th>" +
                        "<th colspan=\"3\" id=\"dateD\"></th><th colspan=\"3\" id=\"dateE\"></th><th colspan=\"3\" id=\"dateF\"></th>" +
                        "<th colspan=\"3\" id=\"dateG\"></th></tr><tr>" +
                        "<th>预计利润</th><th>预计收入</th><th>【利润</th><th>投放</th><th>收入】</th><th>【利润</th><th>投放</th>" +
                        "<th>收入】</th><th>【利润</th><th>投放</th><th>收入】</th><th>【利润</th><th>投放</th><th>收入】</th>" +
                        "<th>【利润</th><th>投放</th><th>收入】</th><th>【利润</th><th>投放</th><th>收入】</th><th>【利润</th>" +
                        "<th>投放</th><th>收入】</th></tr>");

                    $('#dateA').text(endTime);
                    var nowDate = new Date(endTime);

                    var dateB = getDate(nowDate,-1);
                    $('#dateB').text(dateB);

                    var dateC = getDate(nowDate,-1);
                    $('#dateC').text(dateC);

                    var dateD = getDate(nowDate,-1);
                    $('#dateD').text(dateD);

                    var dateE = getDate(nowDate,-1);
                    $('#dateE').text(dateE);

                    var dateF = getDate(nowDate,-1);
                    $('#dateF').text(dateF);

                    var dateG = getDate(nowDate,-1);
                    $('#dateG').text(dateG);
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
                $.post('country_analysis_report/query_country_analysis_report', {
                    tagName: query,
                    startTime: startTime,
                    endTime: endTime,
                    sorterId: sorterId
                },function(data){
                    if (data && data.ret == 1) {
                        setData(data,query);
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
//            var keyset = ["team_name","category_name", "tag_name","anticipated_incoming","anticipated_revenue", "total_incoming0","total_spend0",  "total_revenue0",
//                "total_incoming-1","total_spend-1","total_revenue-1", "total_incoming-2","total_spend-2", "total_revenue-2",
//                "total_incoming-3","total_spend-3","total_revenue-3", "total_incoming-4","total_spend-4", "total_revenue-4",
//                "total_incoming-5", "total_spend-5","total_revenue-5", "total_incoming-6", "total_spend-6","total_revenue-6" ];
            $('#results_body > tr').remove();
            var arr = data.array;
            var len = arr.length;
            var currTag = "";
            var currTeam = "";
            var tagTotalAnticipatedIncoming = 0;
            var tagTotalAnticipatedRevenue = 0;
            var tagTotalSpend0 = 0;
            var tagTotalRevenue0 = 0;
            var tagTotalSpend1 = 0;
            var tagTotalRevenue1 = 0;
            var tagTotalSpend2 = 0;
            var tagTotalRevenue2 = 0;
            var tagTotalSpend3 = 0;
            var tagTotalRevenue3 = 0;
            var tagTotalSpend4 = 0;
            var tagTotalRevenue4 = 0;
            var tagTotalSpend5 = 0;
            var tagTotalRevenue5 = 0;
            var tagTotalSpend6 = 0;
            var tagTotalRevenue6 = 0;

            var teamTotalAnticipatedIncoming = 0;
            var teamTotalAnticipatedRevenue = 0;
            var teamTotalSpend0 = 0;
            var teamTotalRevenue0 = 0;
            var teamTotalSpend1 = 0;
            var teamTotalRevenue1 = 0;
            var teamTotalSpend2 = 0;
            var teamTotalRevenue2 = 0;
            var teamTotalSpend3 = 0;
            var teamTotalRevenue3 = 0;
            var teamTotalSpend4 = 0;
            var teamTotalRevenue4 = 0;
            var teamTotalSpend5 = 0;
            var teamTotalRevenue5 = 0;
            var teamTotalSpend6 = 0;
            var teamTotalRevenue6 = 0;

            for (var i = 0; i < len; i++) {
                var tr = $('<tr></tr>');
                var one = arr[i];
                var teamName = one['team_name'];
                var categoryName = one['category_name'];
                var tagName = one['tag_name'];
                if(i == 0){
                    currTeam = teamName;
                }else{
                    if(currTeam != teamName){
                        var tTr = $('<tr class="green"></tr>');
                        var tTd = $('<td colspan="3"></td>');
                        tTd.text("【"+currTeam + "】项目组汇总");
                        tTr.append(tTd);

                        tTd = $('<td></td>');
                        tTd.text(teamTotalAnticipatedIncoming);
                        tTr.append(tTd);

                        tTd = $('<td></td>');
                        tTd.text(teamTotalAnticipatedRevenue);
                        tTr.append(tTd);

                        tTd = $('<td></td>');
                        tTd.text(teamTotalRevenue0 - teamTotalSpend0);
                        tTr.append(tTd);

                        tTd = $('<td></td>');
                        tTd.text(teamTotalSpend0);
                        tTr.append(tTd);

                        tTd = $('<td></td>');
                        tTd.text(teamTotalRevenue0);
                        tTr.append(tTd);

                        tTd = $('<td></td>');
                        tTd.text(teamTotalRevenue1 - teamTotalSpend1);
                        tTr.append(tTd);

                        tTd = $('<td></td>');
                        tTd.text(teamTotalSpend1);
                        tTr.append(tTd);

                        tTd = $('<td></td>');
                        tTd.text(teamTotalRevenue1);
                        tTr.append(tTd);

                        tTd = $('<td></td>');
                        tTd.text(teamTotalRevenue2 - teamTotalSpend2);
                        tTr.append(tTd);

                        tTd = $('<td></td>');
                        tTd.text(teamTotalSpend2);
                        tTr.append(tTd);

                        tTd = $('<td></td>');
                        tTd.text(teamTotalRevenue2);
                        tTr.append(tTd);

                        tTd = $('<td></td>');
                        tTd.text(teamTotalRevenue3 - teamTotalSpend3);
                        tTr.append(tTd);

                        tTd = $('<td></td>');
                        tTd.text(teamTotalSpend3);
                        tTr.append(tTd);

                        tTd = $('<td></td>');
                        tTd.text(teamTotalRevenue3);
                        tTr.append(tTd);

                        tTd = $('<td></td>');
                        tTd.text(teamTotalRevenue4 - teamTotalSpend4);
                        tTr.append(tTd);

                        tTd = $('<td></td>');
                        tTd.text(teamTotalSpend4);
                        tTr.append(tTd);

                        tTd = $('<td></td>');
                        tTd.text(teamTotalRevenue4);
                        tTr.append(tTd);

                        tTd = $('<td></td>');
                        tTd.text(teamTotalRevenue5 - teamTotalSpend5);
                        tTr.append(tTd);

                        tTd = $('<td></td>');
                        tTd.text(teamTotalSpend5);
                        tTr.append(tTd);

                        tTd = $('<td></td>');
                        tTd.text(teamTotalRevenue5);
                        tTr.append(tTd);

                        tTd = $('<td></td>');
                        tTd.text(teamTotalRevenue6 - teamTotalSpend6);
                        tTr.append(tTd);

                        tTd = $('<td></td>');
                        tTd.text(teamTotalSpend6);
                        tTr.append(tTd);

                        tTd = $('<td></td>');
                        tTd.text(teamTotalRevenue6);
                        tTr.append(tTd);
                        $('#results_body').append(tTr);

                        currTeam = teamName;
                        teamTotalAnticipatedIncoming = 0;
                        teamTotalAnticipatedRevenue = 0;
                        teamTotalSpend0 = 0;
                        teamTotalRevenue0 = 0;
                        teamTotalSpend1 = 0;
                        teamTotalRevenue1 = 0;
                        teamTotalSpend2 = 0;
                        teamTotalRevenue2 = 0;
                        teamTotalSpend3 = 0;
                        teamTotalRevenue3 = 0;
                        teamTotalSpend4 = 0;
                        teamTotalRevenue4 = 0;
                        teamTotalSpend5 = 0;
                        teamTotalRevenue5 = 0;
                        teamTotalSpend6 = 0;
                        teamTotalRevenue6 = 0;
                    }
                }

                var td = $('<td></td>');
                td.text(teamName);
                tr.append(td);

                td = $('<td></td>');
                var categoryName = one['category_name'];
                td.text(categoryName);
                tr.append(td);

                td = $('<td></td>');
                var tagName = one['tag_name'];
                td.text(tagName);
                tr.append(td);

                td = $('<td></td>');
                var anticipatedIncoming = one['anticipated_incoming'];
                td.text(anticipatedIncoming);
                tr.append(td);
                tagTotalAnticipatedIncoming += anticipatedIncoming;
                teamTotalAnticipatedIncoming += anticipatedIncoming;

                td = $('<td></td>');
                var anticipatedRevenue = one['anticipated_revenue'];
                td.text(anticipatedRevenue);
                tr.append(td);
                tagTotalAnticipatedRevenue += anticipatedRevenue;
                teamTotalAnticipatedRevenue += anticipatedRevenue;

                td = $('<td></td>');
                var totalIncoming0 = one['total_incoming0'];
                td.text(totalIncoming0);
                tr.append(td);


                td = $('<td></td>');
                var totalSpend0 = one['total_spend0'];
                td.text(totalSpend0);
                tr.append(td);
                tagTotalSpend0 += totalSpend0;
                teamTotalSpend0 += totalSpend0;

                td = $('<td></td>');
                var totalRevenue0 = one['total_revenue0'];
                td.text(totalRevenue0);
                tr.append(td);
                tagTotalRevenue0 += totalRevenue0;
                teamTotalRevenue0 += totalRevenue0;

                td = $('<td></td>');
                var totalIncoming1 = one['total_incoming-1'];
                td.text(totalIncoming1);
                tr.append(td);


                td = $('<td></td>');
                var totalSpend1 = one['total_spend-1'];
                td.text(totalSpend1);
                tr.append(td);
                tagTotalSpend1 += totalSpend1;
                teamTotalSpend1 += totalSpend1;

                td = $('<td></td>');
                var totalRevenue1 = one['total_revenue-1'];
                td.text(totalRevenue1);
                tr.append(td);
                tagTotalRevenue1 += totalRevenue1;
                teamTotalRevenue1 += totalRevenue1;

                td = $('<td></td>');
                var totalIncoming2 = one['total_incoming-2'];
                td.text(totalIncoming2);
                tr.append(td);


                td = $('<td></td>');
                var totalSpend2 = one['total_spend-2'];
                td.text(totalSpend2);
                tr.append(td);
                tagTotalSpend2 += totalSpend2;
                teamTotalSpend2 += totalSpend2;

                td = $('<td></td>');
                var totalRevenue2 = one['total_revenue-2'];
                td.text(totalRevenue2);
                tr.append(td);
                tagTotalRevenue2 += totalRevenue2;
                teamTotalRevenue2 += totalRevenue2;

                td = $('<td></td>');
                var totalIncoming3 = one['total_incoming-3'];
                td.text(totalIncoming3);
                tr.append(td);


                td = $('<td></td>');
                var totalSpend3 = one['total_spend-3'];
                td.text(totalSpend3);
                tr.append(td);
                tagTotalSpend3 += totalSpend3;
                teamTotalSpend3 += totalSpend3;

                td = $('<td></td>');
                var totalRevenue3 = one['total_revenue-3'];
                td.text(totalRevenue3);
                tr.append(td);
                tagTotalRevenue3 += totalRevenue3;
                teamTotalRevenue3 += totalRevenue3;

                td = $('<td></td>');
                var totalIncoming4 = one['total_incoming-4'];
                td.text(totalIncoming4);
                tr.append(td);


                td = $('<td></td>');
                var totalSpend4 = one['total_spend-4'];
                td.text(totalSpend4);
                tr.append(td);
                tagTotalSpend4 += totalSpend4;
                teamTotalSpend4 += totalSpend4;

                td = $('<td></td>');
                var totalRevenue4 = one['total_revenue-4'];
                td.text(totalRevenue4);
                tr.append(td);
                tagTotalRevenue4 += totalRevenue4;
                teamTotalRevenue4 += totalRevenue4;

                td = $('<td></td>');
                var totalIncoming5 = one['total_incoming-5'];
                td.text(totalIncoming5);
                tr.append(td);


                td = $('<td></td>');
                var totalSpend5 = one['total_spend-5'];
                td.text(totalSpend5);
                tr.append(td);
                tagTotalSpend5 += totalSpend5;
                teamTotalSpend5 += totalSpend5;

                td = $('<td></td>');
                var totalRevenue5 = one['total_revenue-5'];
                td.text(totalRevenue5);
                tr.append(td);
                tagTotalRevenue5 += totalRevenue5;
                teamTotalRevenue5 += totalRevenue5;

                td = $('<td></td>');
                var totalIncoming6 = one['total_incoming-6'];
                td.text(totalIncoming6);
                tr.append(td);


                td = $('<td></td>');
                var totalSpend6 = one['total_spend-6'];
                td.text(totalSpend6);
                tr.append(td);
                tagTotalSpend6 += totalSpend6;
                teamTotalSpend6 += totalSpend6;

                td = $('<td></td>');
                var totalRevenue6 = one['total_revenue-6'];
                td.text(totalRevenue6);
                tr.append(td);
                tagTotalRevenue6 += totalRevenue6;
                teamTotalRevenue6 += totalRevenue6;

                $('#results_body').append(tr);
                if(i == len - 1){
                    var tTr = $('<tr class="green"></tr>');
                    var tTd = $('<td colspan="3"></td>');
                    tTd.text("【"+currTeam + "】项目组汇总");
                    tTr.append(tTd);

                    tTd = $('<td></td>');
                    tTd.text(teamTotalAnticipatedIncoming);
                    tTr.append(tTd);

                    tTd = $('<td></td>');
                    tTd.text(teamTotalAnticipatedRevenue);
                    tTr.append(tTd);

                    tTd = $('<td></td>');
                    tTd.text(teamTotalRevenue0 - teamTotalSpend0);
                    tTr.append(tTd);

                    tTd = $('<td></td>');
                    tTd.text(teamTotalSpend0);
                    tTr.append(tTd);

                    tTd = $('<td></td>');
                    tTd.text(teamTotalRevenue0);
                    tTr.append(tTd);

                    tTd = $('<td></td>');
                    tTd.text(teamTotalRevenue1 - teamTotalSpend1);
                    tTr.append(tTd);

                    tTd = $('<td></td>');
                    tTd.text(teamTotalSpend1);
                    tTr.append(tTd);

                    tTd = $('<td></td>');
                    tTd.text(teamTotalRevenue1);
                    tTr.append(tTd);

                    tTd = $('<td></td>');
                    tTd.text(teamTotalRevenue2 - teamTotalSpend2);
                    tTr.append(tTd);

                    tTd = $('<td></td>');
                    tTd.text(teamTotalSpend2);
                    tTr.append(tTd);

                    tTd = $('<td></td>');
                    tTd.text(teamTotalRevenue2);
                    tTr.append(tTd);

                    tTd = $('<td></td>');
                    tTd.text(teamTotalRevenue3 - teamTotalSpend3);
                    tTr.append(tTd);

                    tTd = $('<td></td>');
                    tTd.text(teamTotalSpend3);
                    tTr.append(tTd);

                    tTd = $('<td></td>');
                    tTd.text(teamTotalRevenue3);
                    tTr.append(tTd);

                    tTd = $('<td></td>');
                    tTd.text(teamTotalRevenue4 - teamTotalSpend4);
                    tTr.append(tTd);

                    tTd = $('<td></td>');
                    tTd.text(teamTotalSpend4);
                    tTr.append(tTd);

                    tTd = $('<td></td>');
                    tTd.text(teamTotalRevenue4);
                    tTr.append(tTd);

                    tTd = $('<td></td>');
                    tTd.text(teamTotalRevenue5 - teamTotalSpend5);
                    tTr.append(tTd);

                    tTd = $('<td></td>');
                    tTd.text(teamTotalSpend5);
                    tTr.append(tTd);

                    tTd = $('<td></td>');
                    tTd.text(teamTotalRevenue5);
                    tTr.append(tTd);

                    tTd = $('<td></td>');
                    tTd.text(teamTotalRevenue6 - teamTotalSpend6);
                    tTr.append(tTd);

                    tTd = $('<td></td>');
                    tTd.text(teamTotalSpend6);
                    tTr.append(tTd);

                    tTd = $('<td></td>');
                    tTd.text(teamTotalRevenue6);
                    tTr.append(tTd);
                    $('#results_body').append(tTr);
                }
            }
        }
        $("#btnSearch").click();
    }
    init();

</script>
<script src="js/interlaced-color-change.js"></script>
</body>
</html>
