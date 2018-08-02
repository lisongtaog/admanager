<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page import="com.bestgo.admanager.utils.LoginUserSessionCacheUtil" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/rootBase.jsp"%>

<html>
<head>
    <title>时间分析报告</title>

    <style>
        .red {
            color: red;
        }
        .green {
            color: green;
        }
        .orange{
            color: orange;
        }
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
    LoginUserSessionCacheUtil.loadSessionFromCache(application, session);
    Object object = session.getAttribute("isAdmin");
    if (object == null) {
        response.sendRedirect("login.jsp");
    }

    List<JSObject> allTags = Tags.fetchAllTags();   //这里得到一个从表web_tag里导入的有 id，tag_name 两项的JsonObject数组
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
            <span>国家</span>
            <input id="country_filter"  style="display: inline; width: auto;" type="text" placeholder="select a country">
            <button id="btnSearch" class="btn btn-default">查找</button>
        </div>
    </div>
    <div class="panel panel-default">
        <div class="panel-body" id="total_result">
        </div>
    </div>

    <div class="box box-default">
        <div class="box-body" style="overflow-x: auto">
            <table id="metricTable" class="table table-bordered table-hover" cellspacing="0" width="100%">
                <thead id="result_header">
                <tr>
                    <th>Date</th>
                    <th>Cost</th>
                    <th>PurchasedUser</th>
                    <th>Installed</th>
                    <th>Uninstalled</th>
                    <th>UninstalledRate</th>
                    <th>TotalUser</th>
                    <th>ActiveUser</th>
                    <th>Revenue</th>
                    <th>ECPM</th>
                    <th>CPA</th>
                    <th>CPA/ECPM</th>
                    <th>Incoming</th>
                    <th>EstimatedRevenue14</th>
                    <th>Revenue14/Cost</th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>

<jsp:include page="loading_dialog.jsp"></jsp:include>

<!-- DataTables -->
<script src="http://money.uugame.info/admin_lte/bower_components/datatables.net/js/jquery.dataTables.min.js"></script>
<script src="http://money.uugame.info/admin_lte/bower_components/datatables.net-bs/js/dataTables.bootstrap.min.js"></script>
<script type="text/javascript" language="javascript" src="https://cdn.datatables.net/buttons/1.4.1/js/dataTables.buttons.min.js"></script>
<script type="text/javascript" language="javascript" src="https://cdn.datatables.net/select/1.2.2/js/dataTables.select.min.js"></script>
<script type="text/javascript" src="http://money.uugame.info/admin_lte/plugins/Editor-1.6.5/js/dataTables.editor.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/1.4.2/js/buttons.html5.min.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/1.4.2/js/buttons.print.min.js"></script>

<script>
    $.post("time_analysis_report/setOption",function(data){
        var options = new Array();
        var len = data.length;
        for(var i=0;i<len;i++){
            var op = data[i];
            options[i] = op["country_name"];
        }
        $("#country_filter").autocomplete({
            source: options
        });
    },"json")

    $("li[role='presentation']:eq(2)").addClass("active");
    //首先由默认的时间显示
    var now = new Date(new Date().getTime() - 86400 * 1000);
    $('#inputStartTime').val(now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate());
    $('#inputEndTime').val(now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate());   //不太明白这里为什么要+1
    //datetimepicker是个现成插件
    $('#inputStartTime').datetimepicker({
        minView: "month",    //从月视图开始，选天
        format: 'yyyy-mm-dd',  //显示格式
        autoclose: true,    //选完以后是否自动关闭
        todayBtn: true      //如果此值为true或者linked的话，在时间框底下显示“today”按钮以选择当前日期
    });
    $('#inputEndTime').datetimepicker({
        minView: "month",
        format: 'yyyy-mm-dd',
        autoclose: true,
        todayBtn: true
    });
    var data = <%=array.toString()%>;  //array是这样的结构：[{},{},{},...]
    $("#inputSearch").autocomplete({
        source: data   //label属性显示在建议菜单里
    });

    //最主要的事件
    $("#btnSearch").click(function(){
        var tagName = $("#inputSearch").val();
        var country_filter = $("#country_filter").val();
        var startTime = $('#inputStartTime').val();
        var endTime = $('#inputEndTime').val();

        if ($.fn.DataTable.isDataTable("#metricTable")) {
            $('#metricTable').DataTable().clear().destroy();
        }

        if(!country_filter){
            $('#result_header').html("<tr><th>Date</th><th>Cost</th><th>PurchasedUser</th><th>Installed</th><th>Uninstalled</th>" +
                "<th>UninstalledRate</th><th>TotalUser</th><th>ActiveUser</th><th>Revenue</th><th>ECPM</th><th>CPA</th>" +
                "<th>CPA/ECPM</th><th>Incoming</th><th>EstimatedRevenue14</th><th>Revenue14/Cost</th>");
        }else{
            $('#result_header').html("<tr><th>Date</th><th>Cost</th><th>PurchasedUser</th><th>Installed</th><th>Uninstalled</th>" +
                "<th>UninstalledRate</th><th>TotalUser</th><th>ActiveUser</th><th>Revenue</th><th>PI</th><th>ECPM</th><th>CPA</th>" +
                "<th>ACPA</th><th>CPA/ECPM</th><th>Incoming</th><th>EstimatedRevenue14</th><th>Revenue14/Cost</th>");
        }
        setData(tagName,country_filter,startTime,endTime);

    });

    //这里输入的 data 是一个array
    function setData(tagName,country_filter,startTime,endTime) {
        var UninstalledRate_idx = $("th:contains('UninstalledRate')").index();
        var CPA_ECPM_idx = $("th:contains('CPA/ECPM')").index();

        var columns;
        //jQuery.dateTable插件回显
        if(!country_filter){
            columns = [{data:"date"},{data:"costs"},{data:"purchased_users"},{data:"installed"},{data:"uninstalled"},
                {data:"uninstalled_rate"},{data:"users"},{data:"active_users"},{data:"revenues"},{data:"ecpm"},{data:"cpa"},
                {data:"cpa_dev_ecpm"},{data:"incoming"},{data:"estimated_revenues"},{data:"estimated_revenues_dev_cost"}];
        }else{
            columns = [{data:"date"},{data:"costs"},{data:"purchased_users"},{data:"installed"},{data:"uninstalled"},
                {data:"uninstalled_rate"},{data:"users"},{data:"active_users"},{data:"revenues"},{data:"pi"},{data:"ecpm"},{data:"cpa"},
                {data:"a_cpa"},{data:"cpa_dev_ecpm"},{data:"incoming"},{data:"estimated_revenues"},{data:"estimated_revenues_dev_cost"}];
        }

        $('#metricTable').DataTable({
            destroy:true,
            ordering: true,
            processing: true,
            serverSide: true,
            searching: false,
            pageLength: 20,
            lengthMenu: [[20,25,30,35,50,100],[20,25,30,35,50,100]],
            columnDefs: [
                { "orderable": false, "targets": UninstalledRate_idx },
                { "orderable": false, "targets": CPA_ECPM_idx }
            ],
            ajax: function (data, callback, settings) {
                var postData = {};
                postData.tagName = tagName;
                postData.country_filter = country_filter;
                postData.startTime = startTime;
                postData.endTime = endTime;
                postData.page_index = data.start / data.length;
                postData.page_size = data.length;
                postData.order = data.order[0].column + (data.order[0].dir == 'asc' ? 1000 : 0);

                //使用ajax作为数据源，但内部函数使用回调给dataTable的参数赋值
                $.post("time_analysis_report/time_query", postData, function (data) {
                    if (data && data.ret == 1) {
                        var list = [];
                        for (var i = 0; i < data.array.length; i++) {
                            list.push(data.array[i]);
                        }
                        callback(
                            {
                                recordsTotal: data.total,
                                recordsFiltered: data.total,
                                data: list
                            }
                        );
                        var str = "Cost: " + data.total_cost + "&nbsp;&nbsp;&nbsp;&nbsp;PuserchaedUser: " + data.total_puserchaed_user +
                            "&nbsp;&nbsp;&nbsp;&nbsp;CPA: " + data.total_cpa + "&nbsp;&nbsp;&nbsp;&nbsp;Revenue: " + data.total_revenue +
                            "&nbsp;&nbsp;&nbsp;&nbsp;Es14: " + data.total_es14 + "&nbsp;&nbsp;&nbsp;&nbsp;Es14/Cost: " + data.es14_dev_cost;
                        $('#total_result').html(str);
                        //这里加入标色（红色，绿色，橙色）
                        marking(data.es14_dev_cost);
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
            }]
        });
    }

    function marking(es14_dev_cost){
        var IncmIdx = $("th:contains('Incoming')").index();
        var RvDevCstIdx = $("th:contains('Revenue14/Cost')").index();
        var tbodyTr = $("#metricTable tbody tr");
        //遍历渲染
        tbodyTr.each(function(idx,el){
            var Incm = parseInt($(el).find("td:eq("+IncmIdx+")").text().toString());
            if( Incm < 0){
                $(el).find("td:eq("+IncmIdx+")").addClass("red");
            }
            var RvDevCst = parseFloat($(el).find("td:eq("+RvDevCstIdx+")").text().toString());
            if(RvDevCst < parseFloat(es14_dev_cost)){
                $(el).find("td:eq("+RvDevCstIdx+")").addClass("orange");
            }else{
                $(el).find("td:eq("+RvDevCstIdx+")").addClass("green");
            }
        });
    }
</script>
</body>
</html>

