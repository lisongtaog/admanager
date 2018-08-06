<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/rootBase.jsp"%>
<html>
<head>
    <title>广告收益报告</title>

    <%--配合原来的<table>使用的--%>
    <style>
        #metricTable td,th{
            text-align:center;
            max-width: 20em;
            word-wrap:break-word;
            text-overflow:ellipsis;
            overflow:hidden;
            white-space:nowrap;
        }
        #metricTable td:hover{
            white-space:normal;
            overflow:auto;
        }
        table th{
            background-color:lightskyblue;
        }
    </style>

    <!-- DataTables -->
    <link rel="stylesheet" href="http://money.uugame.info/admin_lte/bower_components/datatables.net-bs/css/dataTables.bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/buttons/1.4.1/css/buttons.dataTables.min.css">
    <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/select/1.2.2/css/select.dataTables.min.css">

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

    <div class="box box-default">

        <div class="box-body" style="overflow-x: auto">
            <table id="metricTable" class="table table-bordered table-hover" cellspacing="0" width="100%">
                <thead>
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
            </table>
        </div>
    </div>

</div>

<jsp:include page="common/loading_dialog.jsp"></jsp:include>

<!-- DataTables -->
<script src="http://money.uugame.info/admin_lte/bower_components/datatables.net/js/jquery.dataTables.min.js"></script>
<script src="http://money.uugame.info/admin_lte/bower_components/datatables.net-bs/js/dataTables.bootstrap.min.js"></script>
<script type="text/javascript" language="javascript" src="https://cdn.datatables.net/buttons/1.4.1/js/dataTables.buttons.min.js"></script>
<script type="text/javascript" language="javascript" src="https://cdn.datatables.net/select/1.2.2/js/dataTables.select.min.js"></script>
<script type="text/javascript" src="http://money.uugame.info/admin_lte/plugins/Editor-1.6.5/js/dataTables.editor.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/1.4.2/js/buttons.html5.min.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/1.4.2/js/buttons.print.min.js"></script>

<script>
    $("li[role='presentation']:eq(2)").addClass("active");
    var now = new Date(new Date().getTime() - 86400 * 1000);
    $('#installedDate').val(now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate());

    var startDateLimit = new Date("2018-06-27");
    $('#installedDate').datetimepicker({
        minView: "month",
        format: 'yyyy-mm-dd',
        startDate:startDateLimit,
        endDate:new Date(),
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

        setData(inputTag,installedDate);
    });

    //jQuery.dateTable插件回显
    function setData(tagName,installedDate){
        // columns：与后台返回数据的list键名一致
        var columns = [
            {data:"campaign_id"},{data:"campaign_name"},{data:"country_name"},{data:"1_day"},
            {data:"2_day"},{data:"3_day"}, {data:"4_day"},{data:"5_day"},{data:"6_day"},{data:"7_day"},
            {data:"8_day"},{data:"9_day"}, {data:"10_day"},{data:"11_day"},{data:"12_day"},
            {data:"13_day"},{data:"14_day"}, {data:"15_day"},{data:"16_day"},{data:"17_day"},
            {data:"18_day"},{data:"19_day"}, {data:"20_day"}
        ];

        if ($.fn.DataTable.isDataTable("#metricTable")) {
            $('#metricTable').DataTable().clear().destroy();
        }
        $('#metricTable').DataTable({
            "ordering": true,
            "processing": true,
            "serverSide": true,
            "searching": false,
            "pageLength": 100,
            "lengthMenu": [[100, 250,500, 1000], [100,250, 500, 1000]],
            "ajax": function (data, callback, settings) {
                var postData = {};
                postData.tagName = tagName;
                postData.installedDate = installedDate;
                postData.page_index = data.start / data.length;
                postData.page_size = data.length;
                postData.order = data.order[0].column + (data.order[0].dir == 'asc' ? 1000 : 0);

                $.post("campaign_impressions_revenue_report", postData, function (data) {
                    if (data && data.ret == 1) {
                        var list = [];
                        for (var i = 0; i < data.data.length; i++) {
                            list.push(data.data[i]
                            );
                        }
                        callback(
                            {
                                "recordsTotal": data.total,
                                "recordsFiltered": data.total,
                                "data": list
                            }
                        );
                    } else {
                        alert(data.message);
                    }
                }, "json");
            },
            columns: columns,
            select: true,
            dom: 'Blfrtip',
            buttons: [{
                extend: 'collection',
                text: 'Export',
                buttons: ['copy', 'excel', 'csv', 'pdf', 'print']
            }],
        });
    }

</script>
</body>
</html>
