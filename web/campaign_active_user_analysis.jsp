<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
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
        .red {
            color: red;
        }
        .green {
            color: green;
        }
        .orange {
            color: orange;
        }
        td,th{
            text-align:center;
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
            <span>安装日期</span>
            <input type="text" value="2012-05-15" id="installedDate" readonly>
            <span>标签</span>
            <input id="inputTag" class="form-control" style="display: inline; width: auto;" type="text"/>
            <button id="btnSearch" class="btn btn-default glyphicon glyphicon-search"></button>
        </div>
    </div>
    <%--<div class="panel panel-default">--%>
        <%--<div class="panel-body" id="total_result">--%>
        <%--</div>--%>
    <%--</div>--%>
    <table class="table table-hover">
        <thead id="result_header">
        <tr>
            <th>系列ID</th>
            <th>系列名称</th>
            <th>国家</th>
            <th>1_天</th><th>2_天</th><th>3_天</th><th>4_天</th><th>5_天</th><th>6_天</th>
            <th>7_天</th><th>8_天</th><th>9_天</th><th>10_天</th><th>11_天</th><th>12_天</th>
            <th>13_天</th><th>14_天</th><th>15_天</th><th>16_天</th><th>17_天</th><th>18_天</th>
            <th>19_天</th><th>20_天</th>
        </tr>
        </thead>
        <tbody id="results_body">
        </tbody>
    </table>

</div>

<jsp:include page="loading_dialog.jsp"></jsp:include>

<script>
    $("li[role='presentation']:eq(2)").addClass("active");
    var now = new Date(new Date().getTime() - 86400 * 1000);
    $('#installedDate').val(now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate());

    $('#installedDate').datetimepicker({
        minView: "month",
        format: 'yyyy-mm-dd',
        autoclose: true,
        todayBtn: true
    });

    var data = <%=array.toString()%>;
    $("#inputTag").autocomplete({
        source: data
    });

    $("#btnSearch").click(function(){
        var inputTag = $("#inputTag").val();
        var installedDate = $('#installedDate').val();

        $.post("campaign_active_user_report", {
            tagName: inputTag,
            installedDate: installedDate
        },function(data){
            if(data && data.ret == 1){
                setData(data.data);
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        },'json');
    });

    //计算两个日期的相差天数
    function Date_Diff(date,dateTarget){
        var diff = parseInt(Math.abs(dateTarget.getTime()- date.getTime())/1000/3600/24);
        return diff;
    }

    function setData(data) {
        console.log(data);
        var installedDate = new Date($("#installedDate").val());
        data.forEach(function(el){
            var tr = $("<tr></tr>");

            //插入前三列
            tr.append("<td>"+el.campaign_id+"</td>").append("<td>"+el.campaign_name+"</td>").append("<td>"+el.country_name+"</td>");

            //由 event_date 与 installed_date之间的日期差值 插入相应的天数列
            var event_date = new Date(el.event_date);
            var diff = Date_Diff(installedDate,event_date);
            console.log(el.event_date+" "+installedDate+" "+diff);
            for (var i=0;i<20;i++){
                if(i!=diff){
                    tr.append("<td></td>");
                }else{
                    tr.append("<td>"+el.active_num+"</td>");
                }
            }
            $("#results_body").append(tr);
        })
    }

</script>
</body>
</html>
