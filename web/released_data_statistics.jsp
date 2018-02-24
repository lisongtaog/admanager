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
        <tr>
            <th rowspan="2">项目组</th>
            <th rowspan="2">Category</th>
            <th rowspan="2">AppName</th>
            <th></th>
            <th></th>
            <th colspan="3" id="dateA">now</th><th colspan="3" id="dateB">now-1</th><th colspan="3" id="dateC">now-2</th>
            <th colspan="3" id="dateD">now-3</th><th colspan="3" id="dateE">now-4</th><th colspan="3" id="dateF">now-5</th>
            <th colspan="3" id="dateG">now-6</th>
        </tr>
        <tr>
            <th>预计利润</th><th>预计收入</th><th>【利润</th><th>投放</th><th>收入】</th><th>【利润</th><th>投放</th>
            <th>收入】</th><th>【利润</th><th>投放</th><th>收入】</th><th>【利润</th><th>投放</th><th>收入】</th>
            <th>【利润</th><th>投放</th><th>收入】</th><th>【利润</th><th>投放</th><th>收入】</th><th>【利润</th>
            <th>投放</th><th>收入】</th>
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

        $('#dateA').text(nowDateStr);
        var nowDate = new Date(nowDateStr);

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
            $('#results_body > tr').remove();
            var arr = data.array;
            var len = arr.length;
            var one;
            for (var i = 0; i < len; i++) {
                one = arr[i];
                var tr = $('<tr></tr>');
                var keyset = ["team_name","category_name", "tag_name","anticipated_incoming","anticipated_revenue", "total_revenue0",
                    "total_spend0", "totalIncoming0", "total_revenue-1", "total_spend-1", "totalIncoming-1","total_revenue-2", "total_spend-2", "totalIncoming-2",
                    "total_revenue-3", "total_spend-3", "totalIncoming-3","total_revenue-4", "total_spend-4", "totalIncoming-4",
                    "total_revenue-5", "total_spend-5", "totalIncoming-5","total_revenue-6", "total_spend-6", "totalIncoming-6"];
                for (var j = 0; j < keyset.length; j++) {
                    var key = keyset[j];
                    var td = $('<td></td>');
                    var r = one[key];
                    td.text(r);
                    tr.append(td);
                }
                $('#results_body').append(tr);
            }
        }
    }
    init();
</script>
<script src="js/interlaced-color-change.js"></script>
</body>
</html>
