<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>素材分析报告</title>
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
            <span>返回条数</span>
            <input id="inputCount" class="form-control" style="display: inline; width: auto;" type="text" value="100"/>
            <button id="btnSearch" class="btn btn-default glyphicon glyphicon-search"></button>
        </div>
    </div>
    <%--<div class="panel panel-default">--%>
        <%--<div class="panel-body" id="total_result">--%>
        <%--</div>--%>
    <%--</div>--%>
    <table class="table table-hover">
        <thead id="result_header">
        <tr><th>网络</th><th>文字</th><th>图片</th><th>视频</th>
            <th>花费<span sorterId="1" class="sorter glyphicon glyphicon-arrow-down"></span></th>
            <th>安装<span sorterId="2" class="sorter glyphicon glyphicon-arrow-up"></span></th>
            <th>点击<span sorterId="3" class="sorter glyphicon glyphicon-arrow-up"></span></th>
            <th>展示<span sorterId="4" class="sorter glyphicon glyphicon-arrow-up"></span></th>
            <th>CPA<span sorterId="5" class="sorter glyphicon glyphicon-arrow-up"></span></th>
            <th>CTR<span sorterId="6" class="sorter glyphicon glyphicon-arrow-up"></span></th>
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
<script src="js/layer/layer.js" ></script>

<script>
    $("li[role='presentation']:eq(14)").addClass("active");
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

    $("#btnSearch").click(function () {
        queryData(1001);
    });

    function queryData(sortId) {
        var query = $("#inputSearch").val();
        var startTime = $('#inputStartTime').val();
        var endTime = $('#inputEndTime').val();
        var inputCount = $('#inputCount').val();
        $('#results_body > tr').remove();
        var loadingIndex = layer.load(2, {time: 10000});
        $.post('material_analysis_report/query_material_analysis_report_by_tag', {
            tagName: query,
            startTime: startTime,
            endTime: endTime,
            inputCount: inputCount,
            sortId: sortId
        }, function (data) {
            layer.close(loadingIndex);
            if (data && data.ret == 1) {
                setData(data);
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        }, 'json');
    }

    bindSortOp();
    function bindSortOp() {
        $('.sorter').click(function() {
            var sorterId = $(this).attr('sorterId');
            sorterId = parseInt(sorterId);
            if ($(this).hasClass("glyphicon-arrow-up")) {
                $(this).removeClass("glyphicon-arrow-up");
                $(this).addClass("glyphicon-arrow-down");
                sorterId += 1000;
            } else {
                $(this).removeClass("glyphicon-arrow-down");
                $(this).addClass("glyphicon-arrow-up");
            }
            queryData(sorterId);
        });
    }

    function setData(data) {
        var arr = data.array;
        var len = arr.length;
        <!-- 素材这一行 -->
        var tr = '';

        for (var i = 0; i < len; i++) {
            var one = arr[i];
            tr = $('<tr></tr>');
            var td = $('<td></td>');
            td.text(one.network);
            tr.append(td);

            td = $('<td></td>');
            if (one.network == 'adwords') {
                var html = one.message1 + "</br>" + one.message2 + "</br>" + one.message3 + "</br>" + one.message4;
                td.html(html);
            } else {
                var html = one.title + "</br>" + one.message;
                td.html(html);
            }
            tr.append(td);

            var keys = ['imagePath', 'videoPath', 'spend', 'installed', 'click', 'impressions', 'cpa', 'ctr']
            for (var j = 0; j < keys.length; j++) {
                td = $('<td></td>');
                td.text(one[keys[j]]);
                tr.append(td);
            }
            $('#results_body').append(tr);
        }
    }
</script>
</body>
</html>
