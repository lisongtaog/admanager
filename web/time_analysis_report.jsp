<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>时间分析报告</title>
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
    <button id="line_chart" class="btn btn-link glyphicon glyphicon-zoom-in">折线图</button>
    <canvas id="lineChart"></canvas>
    <table class="table table-hover">
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
<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.7.2/Chart.js"></script>

<script>
    $("canvas").hide();
    $("#line_chart").click(function(){
        $("canvas").show();
        var Date_chart = [];
        var Cost = [];
        var PurchasedUser = [];
        var Installed = [];
        var Uninstalled = [];
        var TotalUser = [];
        var ActiveUser = [];
        var Revenue = [];
        var ECPM = [];
        var CPA = [];
        var CPA_ECPM = [];
        var Incoming = [];
        var EstimatedRevenue14 = [];
        var Revenue14_Cost = [];
        var trArray = $("#results_body").children("tr");
        trArray.each(function(){
            var theTr = $(this);
            Date_chart.push(theTr.children("td:eq(0)").text());
            Cost.push(theTr.children("td:eq(1)").text());
            PurchasedUser.push(theTr.children("td:eq(2)").text());
            Installed.push(theTr.children("td:eq(3)").text());
            Uninstalled.push(theTr.children("td:eq(4)").text());
            TotalUser.push(theTr.children("td:eq(5)").text());
            ActiveUser.push(theTr.children("td:eq(6)").text());
            Revenue.push(theTr.children("td:eq(7)").text());
            ECPM.push(theTr.children("td:eq(8)").text());
            CPA.push(theTr.children("td:eq(9)").text());
            CPA_ECPM.push(theTr.children("td:eq(10)").text());
            Incoming.push(theTr.children("td:eq(11)").text());
            EstimatedRevenue14.push(theTr.children("td:eq(12)").text());
            Revenue14_Cost.push(theTr.children("td:eq(13)").text());
        });
        var chrt = $("canvas");
        var chart = new Chart(chrt, {
            type: "line",
            data: {
                datasets: [{
                    label: 'Cost',
                    data: Cost,
                    borderColor: "rgba(160,82,45,1)",
                    backgroundColor:"rgba(160,82,45,0.1)"
                }, {
                    label: 'PurchasedUser',
                    data: PurchasedUser,
                    borderColor: "rgba(238,0,0,1)",
                    backgroundColor:"rgba(238,0,0,0.1)"
                }, {
                    label: 'Installed',
                    data: Installed,
                    borderColor:"rgba(142,142,56,1)",
                    backgroundColor:"rgba(142,142,56,0.1)"
                }, {
                    label: 'Uninstalled',
                    data: Uninstalled,
                    borderColor:'rgba(124,252,0,1)',
                    backgroundColor:"rgba(124,252,0,0.1)"
                },{
                    label: 'TotalUser',
                    data: TotalUser,
                    borderColor:'rgba(0,0,170,1)',
                    backgroundColor:"rgba(0,0,170,0.1)"
                },{
                    label: 'ActiveUser',
                    data: ActiveUser,
                    borderColor:'rgba(99,184,255,1)',
                    backgroundColor:"rgba(99,184,255,0.1)"
                },{
                    label: 'Revenue',
                    data: Revenue,
                    borderColor:'rgba(122,55,139,1)',
                    backgroundColor:"rgba(122,55,139,0.1)"
                },{
                    label: 'ECPM',
                    data: ECPM,
                    borderColor:'rgba(171,130,255,1)',
                    backgroundColor:"rgba(171,130,255,0.1)"
                },{
                    label: 'CPA',
                    data: CPA,
                    borderColor:'rgba(110,110,110,1)',
                    backgroundColor:"rgba(110,110,110,0.1)"
                },{
                    label: 'CPA/ECPM',
                    data: CPA_ECPM,
                    borderColor:'rgba(124,205,124,1)',
                    backgroundColor:"rgba(124,205,124,0.1)"
                },{
                    label: 'Incoming',
                    data: Incoming,
                    borderColor:'rgba(205,173,0,1)',
                    backgroundColor:"rgba(205,173,0,0.1)"
                },{
                    label: 'EstimatedRevenue14',
                    data: EstimatedRevenue14,
                    borderColor:'rgba(255,215,0,1)',
                    backgroundColor:"rgba(255,215,0,0.1)"
                },{
                    label: 'Revenue14/Cost',
                    data: Revenue14_Cost,
                    borderColor:'rgba(160,82,45,1)',
                    backgroundColor:"rgba(255,215,0,0.1)"
                }],
                labels: Date_chart
            },
        });
    });

</script>
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

    $("#btnSearch").click(function(){
        $("canvas").hide();
        var query = $("#inputSearch").val();
        var country_filter = $("#country_filter").val();
        var startTime = $('#inputStartTime').val();
        var endTime = $('#inputEndTime').val();
        $.post('time_analysis_report/time_query', {   //用于排序
            tagName: query,
            startTime: startTime,
            endTime: endTime,
            country_filter: country_filter
        },function(data){
            if(data && data.ret == 1){
                if(!country_filter){
                    $('#result_header').html("<tr><th>Date<button sorterId=\"1032\" class=\"btn btn-link glyphicon glyphicon-arrow-down\"></button></th>"+
                        "<th>Cost<button sorterId=\"1031\" class=\"btn btn-link glyphicon glyphicon-arrow-down\"></button></th>" +
                        "<th>PurchasedUser<button sorterId=\"1033\" class=\"btn btn-link glyphicon glyphicon-arrow-down\"></button></th>" +
                        "<th>Installed<button sorterId=\"1034\" class=\"btn btn-link glyphicon glyphicon-arrow-down\"></button></th>" +
                        "<th>Uninstalled<button sorterId=\"1035\" class=\"btn btn-link glyphicon glyphicon-arrow-down\"></button></th>" +
                        "<th>UninstalledRate</th><th>TotalUser<button sorterId=\"1037\" class=\"btn btn-link glyphicon glyphicon-arrow-down\"></button></th>" +
                        "<th>ActiveUser<button sorterId=\"1038\" class=\"btn btn-link glyphicon glyphicon-arrow-down\"></button></th>" +
                        "<th>Revenue<button sorterId=\"1039\" class=\"btn btn-link glyphicon glyphicon-arrow-down\"></button></th>" +
                        "<th>ECPM<button sorterId=\"1040\" class=\"btn btn-link glyphicon glyphicon-arrow-down\"></button></th>" +
                        "<th>CPA<button sorterId=\"1041\" class=\"btn btn-link glyphicon glyphicon-arrow-down\"></button></th>" +
                        "<th>CPA/ECPM</th>" +
                        "<th>Incoming<button sorterId=\"1042\" class=\"btn btn-link glyphicon glyphicon-arrow-down\"></button></th>" +
                        "<th>EstimatedRevenue14<button sorterId=\"1044\" class=\"btn btn-link glyphicon glyphicon-arrow-down\"></button></th>" +
                        "<th>Revenue14/Cost<button sorterId=\"1045\" class=\"btn btn-link glyphicon glyphicon-arrow-down\"></button></th>");
                }else{
                    $('#result_header').html("<tr><th>Date<button sorterId=\"1032\" class=\"btn btn-link glyphicon glyphicon-arrow-down\"></button></th>"+
                        "<th>Cost<button sorterId=\"1031\" class=\"btn btn-link glyphicon glyphicon-arrow-down\"></button></th>" +
                        "<th>PurchasedUser<button sorterId=\"1033\" class=\"btn btn-link glyphicon glyphicon-arrow-down\"></button></th>" +
                        "<th>Installed<button sorterId=\"1034\" class=\"btn btn-link glyphicon glyphicon-arrow-down\"></button></th>" +
                        "<th>Uninstalled<button sorterId=\"1035\" class=\"btn btn-link glyphicon glyphicon-arrow-down\"></button></th>" +
                        "<th>UninstalledRate</th><th>TotalUser<button sorterId=\"1037\" class=\"btn btn-link glyphicon glyphicon-arrow-down\"></button></th>" +
                        "<th>ActiveUser<button sorterId=\"1038\" class=\"btn btn-link glyphicon glyphicon-arrow-down\"></button></th>" +
                        "<th>Revenue<button sorterId=\"1039\" class=\"btn btn-link glyphicon glyphicon-arrow-down\"></button></th>" +
                        "<th>PI</th>"  +
                        "<th>ECPM<button sorterId=\"1040\" class=\"btn btn-link glyphicon glyphicon-arrow-down\"></button></th>" +
                        "<th>CPA<button sorterId=\"1041\" class=\"btn btn-link glyphicon glyphicon-arrow-down\"></button></th>" +
                        "<th>ACPA</th>"+
                        "<th>CPA/ECPM</th>" +
                        "<th>Incoming<button sorterId=\"1042\" class=\"btn btn-link glyphicon glyphicon-arrow-down\"></button></th>" +
                        "<th>EstimatedRevenue14<button sorterId=\"1044\" class=\"btn btn-link glyphicon glyphicon-arrow-down\"></button></th>" +
                        "<th>Revenue14/Cost<button sorterId=\"1045\" class=\"btn btn-link glyphicon glyphicon-arrow-down\"></button></th>");
                }
                setData(data,query,country_filter);  //这里是往表格里添加项目，并设置某三列的颜色
                var str = "Cost: " + data.total_cost + "&nbsp;&nbsp;&nbsp;&nbsp;PuserchaedUser: " + data.total_puserchaed_user +
                    "&nbsp;&nbsp;&nbsp;&nbsp;CPA: " + data.total_cpa + "&nbsp;&nbsp;&nbsp;&nbsp;Revenue: " + data.total_revenue +
                    "&nbsp;&nbsp;&nbsp;&nbsp;Es14: " + data.total_es14 + "&nbsp;&nbsp;&nbsp;&nbsp;Es14/Cost: " + data.es14_dev_cost;
                str += "<br/><span class='estimateResult'></span>";
                $('#total_result').html(str);
                $('#total_result').removeClass("editable");
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        },'json');
    });

    //排序
    $("#result_header").on('click',"button",function(){
        var table = $(this).parents('table').eq(0);
        if ($(this).hasClass("glyphicon-arrow-up")) {
            $(this).removeClass("glyphicon-arrow-up");
            $(this).addClass("glyphicon-arrow-down");
        } else {
            $(this).removeClass("glyphicon-arrow-down");
            $(this).addClass("glyphicon-arrow-up");
        }
        var rows = table.find('tr:gt(0)').toArray().sort(comparer($(this).parents("th").index()));
        this.asc = !this.asc;
        if (!this.asc){rows = rows.reverse();}
        table.children('tbody').empty().html(rows);
    });
    function comparer(index) {
        return function(a, b) {
            var valA = getCellValue(a, index), valB = getCellValue(b, index);
            return $.isNumeric(valA) && $.isNumeric(valB) ?
                valA - valB : valA.localeCompare(valB);
        };
    }
    function getCellValue(row, index){
        return $(row).children('td').eq(index).text();
    }

    // function bindSortOp() {    //在后端排序
    //     $('.sorter').click(function() {
    //         var sorterId = $(this).attr('sorterId');
    //         sorterId = parseInt(sorterId);
    //         if ($(this).hasClass("glyphicon-arrow-down")) {
    //             $(this).removeClass("glyphicon-arrow-down");
    //             $(this).addClass("glyphicon-arrow-up");
    //             sorterId -= 1000; //便于在升序和降序间切换
    //         } else {
    //             $(this).removeClass("glyphicon-arrow-up");
    //             $(this).addClass("glyphicon-arrow-down");
    //         }
    //
    //         var query = $("#inputSearch").val();
    //         var startTime = $('#inputStartTime').val();
    //         var endTime = $('#inputEndTime').val();
    //         var country_filter = $("#country_filter").val();
    //         $.post('time_analysis_report/time_query', {
    //             tagName: query,   //query是在标签一栏输入的应用名(实操时候用了输入才会触发的下拉列表)
    //             startTime: startTime,
    //             endTime: endTime,
    //             sorterId: sorterId,
    //             country_filter: country_filter
    //         },function(data){
    //             if (data && data.ret == 1) {
    //                 setData(data,query,country_filter);
    //                 var str = "Cost: " + data.total_cost + "&nbsp;&nbsp;&nbsp;&nbsp;PuserchaedUser: " + data.total_puserchaed_user +  // &nbsp; 在html语言里表示空格
    //                     "&nbsp;&nbsp;&nbsp;&nbsp;CPA: " + data.total_cpa + "&nbsp;&nbsp;&nbsp;&nbsp;Revenue: " + data.total_revenue +
    //                     "&nbsp;&nbsp;&nbsp;&nbsp;Es14: " + data.total_es14 + "&nbsp;&nbsp;&nbsp;&nbsp;Es14/Cost: " + data.es14_dev_cost;
    //
    //                 str += "<br/><span class='estimateResult'></span>"
    //                 $('#total_result').removeClass("editable");  //移除 类editable
    //                 $('#total_result').html(str); //把选中元素的内容替换成 str：结果是页面动态出现了一行字
    //             } else {
    //                 admanager.showCommonDlg("错误", data.message);
    //             }
    //         }, 'json');
    //     });
    // }

    function setData(data,tagName,countryFilter) {
        $('#results_body > tr').remove();  //2018-2-9：多层级选择器
        var arr = data.array;   //array是从后台取出来的原始、未改动数据
        var len = arr.length;
        var one;
        //底下这个for循环是用于往数组array的其中三项添加属性以显示不同颜色,并在表格里添加数据
        for (var i = 0; i < len; i++) {
            one = arr[i];  //每个数组成员都是一个JS对象

            var tr = $('<tr></tr>');   //创建一个空的行元素：$("<></>")
            var keyset = [];
            if(countryFilter){
                keyset = ["date","costs", "purchased_users", "installed",
                    "uninstalled", "uninstalled_rate", "users", "active_users", "revenues","pi",
                    "ecpm","cpa","a_cpa","cpa_dev_ecpm", "incoming","estimated_revenues","estimated_revenues_dev_cost"];
            }else{
                keyset = ["date","costs", "purchased_users", "installed",
                    "uninstalled", "uninstalled_rate", "users", "active_users", "revenues",
                    "ecpm","cpa","cpa_dev_ecpm", "incoming","estimated_revenues","estimated_revenues_dev_cost"];
            }

            for (var j = 0; j < keyset.length; j++) {
                var r = one[keyset[j]]; //得到集里的某个键的value
                //下面先判是否有空键
                if(r === null || r === ""){
                    var td = $('<td></td>');
                    td.text(" -- ");
                    tr.append(td);
                    continue;
                }
                var td = $('<td></td>');
                if('incoming' == keyset[j]){
                    if(r <0){
                        td.addClass("red");
                    }''
                }else if('estimated_revenues_dev_cost' == keyset[j]){
                    if(r > data.es14_dev_cost){
                        td.addClass("green");
                    }else if(r < data.es14_dev_cost){
                        td.addClass("orange");
                    }
                }
                td.text(r);  //把某键的值以文本形式返回
                tr.append(td);
            }
            $('#results_body').append(tr);
        }
    }
</script>
</body>
</html>

