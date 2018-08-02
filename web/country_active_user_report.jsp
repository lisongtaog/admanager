<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page import="com.bestgo.admanager.utils.LoginUserSessionCacheUtil" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/rootBase.jsp"%>

<html>
<head>
    <title>活跃用户报告</title>

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
    LoginUserSessionCacheUtil.loadSessionFromCache(application, session);
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
            <span>标签</span>
            <input id="inputSearch" class="form-control" style="display: inline; width: auto;" type="text"/>
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
            <th>国家</th><th>安装量总和</th><th>7DaysActiveUser</th><th>14DaysActiveUser</th><th>30DaysActiveUser</th><th>60DaysActiveUser</th><th>7DaysAvgARPU</th>
        </tr>
        </thead>
        <tbody id="results_body">
        </tbody>
    </table>

</div>

<jsp:include page="loading_dialog.jsp"></jsp:include>

<script>
    $("li[role='presentation']:eq(2)").addClass("active");
    var data = <%=array.toString()%>;
    $("#inputSearch").autocomplete({
        source: data
    });

    $("#btnSearch").click(function(){
        var query = $("#inputSearch").val();
        $.post('active_user_report/query_active_user_report', {
            tagName: query
        },function(data){
            if(data && data.ret == 1){
                $('#result_header').html("<tr><th>国家</th><th>安装量总和<span sorterId=\"2090\" class=\"sorter glyphicon glyphicon-arrow-down\"></span></th>" +
                    "<th>7DaysActiveUser<span sorterId=\"2091\" class=\"sorter glyphicon glyphicon-arrow-down\"></span></th>" +
                    "<th>14DaysActiveUser<span sorterId=\"2092\" class=\"sorter glyphicon glyphicon-arrow-down\"></span></th>" +
                    "<th>30DaysActiveUser<span sorterId=\"2093\" class=\"sorter glyphicon glyphicon-arrow-down\"></span></th>" +
                    "<th>60DaysActiveUser<span sorterId=\"2094\" class=\"sorter glyphicon glyphicon-arrow-down\"></span></th>" +
                    "<th>ARPU</th><th>7DaysActiveUser*ARPU</th><th>14DaysActiveUser*ARPU</th><th>30DaysActiveUser*ARPU</th><th>60DaysActiveUser*ARPU</th></tr>");
                data = data.array;
                setData(data);
                bindSortOp();
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
                sorterId -= 2000;
            } else {
                $(this).removeClass("glyphicon-arrow-up");
                $(this).addClass("glyphicon-arrow-down");
            }

            var query = $("#inputSearch").val();
            $.post('active_user_report/query_active_user_report', {
                tagName: query,
                sorterId: sorterId
            },function(data){
                if (data && data.ret == 1) {
                    data = data.array;
                    setData(data);
                } else {
                    admanager.showCommonDlg("错误", data.message);
                }
            }, 'json');
        });
    }


    function setData(data) {
        $('#results_body > tr').remove();
        for (var i = 0; i < data.length; i++) {
            var one = data[i];
            var tr = $('<tr></tr>');
            var keyset = ["country_name","total_installeds", "avg_7_day_active", "avg_14_day_active", "avg_30_day_active",
                "avg_60_day_active","seven_days_avg_arpu","avg_7_day_active_mul_arpu","avg_14_day_active_mul_arpu",
                "avg_30_day_active_mul_arpu","avg_60_day_active_mul_arpu"];
            for (var j = 0; j < keyset.length; j++) {
                var td = $('<td></td>');
                var field = keyset[j];
                if('avg_7_day_active' == field){
                    td = $("<td title=\""+one['seven_days_data_update_date']+"\"></td>");
                }else if('avg_14_day_active' == field){
                    td = $("<td title=\""+one['fourteen_days_data_update_date']+"\"></td>");
                }else if('avg_30_day_active' == field){
                    td = $("<td title=\""+one['thirty_days_data_update_date']+"\"></td>");
                }else if('avg_60_day_active' == field){
                    td = $("<td title=\""+one['sixty_days_data_update_date']+"\"></td>");
                }
                td.text(one[field]);
                tr.append(td);
            }
            $('#results_body').append(tr);
        }
    }
</script>
</body>
</html>
