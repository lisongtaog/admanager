<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>图片素材分析报告</title>
    <link rel="stylesheet" href="bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="bootstrap/css/bootstrap-theme.min.css"/>
    <link rel="stylesheet" href="css/core.css"/>
    <link rel="stylesheet" href="css/bootstrap-tagsinput.css"/>
    <link rel="stylesheet" href="css/bootstrap-datetimepicker.css"/>
    <link rel="stylesheet" href="jqueryui/jquery-ui.css"/>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.4/css/select2.min.css" rel="stylesheet" />

    <style>
        .td_bottom_border {
            border-bottom: #ff2516 solid 1px;
            /*border-top: #ff2516 solid 1px;*/
        }
        .td_left_border {
            border-left: #2f17ff solid 1px;
        }
        .td_right_border {
            border-right: #2f17ff solid 1px;
        }
        .purple {
            color: #c374ff;
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
            <span>开始日期</span>
            <input type="text" value="2012-05-15" id="inputStartTime" readonly>
            <span>结束日期</span>
            <input type="text" value="2012-05-15" id="inputEndTime" readonly>
            <span>标签</span>
            <input id="inputSearch" class="form-control" style="display: inline; width: auto;" type="text"/>
            <button id="btnSearch" class="btn btn-default glyphicon glyphicon-search"></button>
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

<script src="js/jquery.js"></script>
<script src="bootstrap/js/bootstrap.min.js"></script>
<script src="js/core.js"></script>
<script src="js/bootstrap-datetimepicker.js"></script>
<script src="jqueryui/jquery-ui.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.4/js/select2.min.js"></script>
<script src="js/country-name-code-dict.js"></script>
<script src="js/layer/layer.js" ></script>

<script>
    $("li[role='presentation']:eq(20)").addClass("active");
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
        $('#results_body > tr').remove();
        var loadingIndex = layer.load(2,{time: 10000});
        $.post('image_material_analysis_report/query_image_material_analysis_report_by_tag', {
            tagName: query,
            startTime: startTime,
            endTime: endTime
        },function(data){
            layer.close(loadingIndex);
            if(data && data.ret == 1){
                setData(data);
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        },'json');
    });

    function setData(data) {
        var arr = data.array;
        var len = arr.length;

        <!-- image_path行 -->
        var one = arr[0];
        var imagePathsStr = one['image_paths'];
        var imagePathArr = imagePathsStr.split(",");
        var imagePathArrLength = imagePathArr.length;
        var tr = $('<tr></tr>');
        var th = $("<th class='td_left_border td_right_border'></th>");
        th.text("图片路径");
        tr.append(th);
        for(var m = 0; m < imagePathArrLength; m++){
            var imagePath = imagePathArr[m];
            th = $("<th colspan = '4' class='td_right_border'></th>");
            th.text(imagePath);
            tr.append(th);
        }
        $('#results_body').append(tr);

        <!-- cpa&install&crt&cvr行 -->
        tr = $('<tr></tr>');
        th = $('<th class="td_left_border td_right_border"></th>');
        tr.append(th);
        for(var m = 0; m < imagePathArrLength; m++){
            th = $("<th class='red'></th>");
            th.text("CPA");
            tr.append(th);
            th = $("<th class='green'></th>");
            th.text("Install");
            tr.append(th);
            th = $("<th class='orange'></th>");
            th.text("CTR");
            tr.append(th);
            th = $("<th class='purple td_right_border'></th>");
            th.text("CVR");
            tr.append(th);
        }
        $('#results_body').append(tr);




        for (var i = 1; i < len; i++) {
            one = arr[i];
            var tr = $("<tr></tr>");
            if(i == len - 1){
                tr = $("<tr class='td_bottom_border'></tr>");
            }
            var countryParam = one['country_param_'+i];
            var countryParamArr = countryParam.split(",");
            var countryParamArrLength = countryParamArr.length;
//            var countryParamArrMaxLength = imagePathArrLength * 4 + 1;

            for (var j = 0; j < countryParamArrLength; j++) {
                var td = $("<td></td>");
                if(j == 0){
                    td = $("<td class='td_left_border td_right_border'></td>");
                }else if(j % 4 == 0){
                    td = $("<td class='td_right_border'></td>");
                }
                td.text(countryParamArr[j]);
                tr.append(td);
            }
            $('#results_body').append(tr);
        }
    }
</script>
</body>
</html>
