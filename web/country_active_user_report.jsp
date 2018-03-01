<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>活跃用户报告</title>
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
            <th>国家</th><th>安装量总和</th><th>7Days_ActiveUser</th><th>14Days_ActiveUser</th><th>30Days_ActiveUser</th><th>60Days_ActiveUser</th>
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

<script>
    $("li[role='presentation']:eq(5)").addClass("active");
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
                $('#result_header').html("<tr><th>国家</th><th>安装量总和</th><th>7Days_ActiveUser</th><th>14Days_ActiveUser</th><th>30Days_ActiveUser</th><th>60Days_ActiveUser</th></tr>");
                data = data.array;
                setData(data);
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        },'json');
    });


    function setData(data) {
        $('#results_body > tr').remove();
        for (var i = 0; i < data.length; i++) {
            var one = data[i];
            var tr = $('<tr></tr>');
            var keyset = ["country_name","total_installeds", "avg_7_day_active", "avg_14_day_active", "avg_30_day_active", "avg_60_day_active"];
            for (var j = 0; j < keyset.length; j++) {
                var td = $('<td></td>');
                var field = keyset[j];
                var field_value = one[field];
                td.text(field_value);
                tr.append(td);
            }
            $('#results_body').append(tr);
        }
    }
</script>
</body>
</html>
