<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>国家分析报告</title>
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
    <div class="panel panel-default">
        <div class="panel-body" id="total_result">
        </div>
    </div>
    <table class="table table-hover">
        <thead id="result_header">
        <tr>
            <th>国家</th>
            <th>Cost</th>
            <th>PurchasedUser</th>
            <th>Installed</th>
            <th>UninstalledRate</th>
            <th>ActiveUser</th>
            <th>Revenue</th>
            <th>ECPM</th>
            <th>PI</th>
            <th>ARPU</th>
            <th>CPA</th>
            <th>ACpa</th>
            <th>Incoming</th>
            <th>RT</th>
            <th>30DaysActiveUser</th>
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
    $("li[role='presentation']:eq(2)").addClass("active");
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
        $.post('country_analysis_report/query_country_analysis_report', {
            tagName: query,
            startTime: startTime,
            endTime: endTime
        },function(data){
            if(data && data.ret == 1){
                $('#result_header').html("<tr><th>国家</th>" +
                    "<th>Cost<span sorterId=\"1031\" class=\"sorter glyphicon glyphicon-arrow-down\"></span></th>" +
                    "<th>PurchasedUser<span sorterId=\"1033\" class=\"sorter glyphicon glyphicon-arrow-down\"></span></th>" +
                    "<th>Installed<span sorterId=\"1034\" class=\"sorter glyphicon glyphicon-arrow-down\"></span></th>" +
                    "<th>UninstalledRate</th>" +
                    "<th>ActiveUser<span sorterId=\"1038\" class=\"sorter glyphicon glyphicon-arrow-down\"></span></th>" +
                    "<th>Revenue<span sorterId=\"1039\" class=\"sorter glyphicon glyphicon-arrow-down\"></span></th>" +
                    "<th>7daysCost</th>" +
                    "<th>7daysRevenue</th>" +
                    "<th>7daysIncoming</th>" +
                    "<th>PI</th>" +
                    "<th>ARPU</th>" +
                    "<th>ECPM<span sorterId=\"1040\" class=\"sorter glyphicon glyphicon-arrow-down\"></span></th>" +
                    "<th>CECPM</th>" +
                    "<th>成本价/2</th>" +
                    "<th>CPA<span sorterId=\"1041\" class=\"sorter glyphicon glyphicon-arrow-down\"></span></th>" +
                    "<th>ACpa</th>" +
                    "<th>CPA/ECPM</th>" +
                    "<th>Incoming<span sorterId=\"1042\" class=\"sorter glyphicon glyphicon-arrow-down\"></span></th>" +
                    "<th>RT</th>" +
                    "<th>30DaysActiveUser</th>" +
                    "<th>花费上限</th>" +
                    "<th>竞价</th>" +
                    "</tr>");
                setData(data);
                bindSortOp();
                var str = "Cost: " + data.total_cost + "&nbsp;&nbsp;&nbsp;&nbsp;PuserchaedUser: " + data.total_puserchaed_user +
                    "&nbsp;&nbsp;&nbsp;&nbsp;CPA: " + data.total_cpa + "&nbsp;&nbsp;&nbsp;&nbsp;Revenue: " + data.total_revenue;
                str += "<br/><span class='estimateResult'></span>"
                $('#total_result').html(str);
                $('#total_result').removeClass("editable");
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
                    setData(data);
                    var str = "Cost: " + data.total_cost + "&nbsp;&nbsp;&nbsp;&nbsp;PuserchaedUser: " + data.total_puserchaed_user +
                        "&nbsp;&nbsp;&nbsp;&nbsp;CPA: " + data.total_cpa + "&nbsp;&nbsp;&nbsp;&nbsp;Revenue: " + data.total_revenue;

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
        var keyset = ["costs","purchased_users", "installed", "uninstalled_rate",
            "active_users", "revenues", "seven_days_costs", "seven_days_revenues",
            "seven_days_incoming","pi", "arpu", "ecpm","c_ecpm", "revenue_per_install","cpa","a_cpa","cpa_div_ecpm", "incoming",
            "rt","thirty_days_active_user","cost_upper_limit","bidding_summary"];
        for (var i = 0; i < len; i++) {
            one = arr[i];
            var tr = $('<tr></tr>');
            var td_outer_a = $('<td></td>');
            td_outer_a.text(one['country_name']);
            tr.append(td_outer_a);
            for (var j = 0; j < keyset.length; j++) {
                var key = keyset[j];
                var td = $('<td></td>');
                var r = one[key];
                if('costs' == key){
                    td = $('<td title="'+ one['every_day_cost_for_fourteen_days'] + '"></td>');
                }else if('purchased_users' == key){
                    td = $('<td title="'+ one['every_day_purchased_user_for_fourteen_days'] + '"></td>');
                }else if('installed' == key){
                    td = $('<td title="'+ one['every_day_installed_for_fourteen_days'] + '"></td>');
                }else if('uninstalled_rate' == key){
                    td = $('<td title="'+ one['every_day_uninstalled_rate_for_fourteen_days'] + '"></td>');
                }else if('active_users' == key){
                    td = $('<td title="'+ one['every_day_active_user_for_fourteen_days'] + '"></td>');
                }else if('revenues' == key){
                    td = $('<td title="'+ one['every_day_revenue_for_fourteen_days'] + '"></td>');
                }else if('pi' == key){
                    td = $('<td title="'+ one['every_day_pi_for_fourteen_days'] + '"></td>');
                }else if('arpu' == key){
                    td = $('<td></td>');
                }else if('ecpm' == key){
                    td = $('<td title="'+ one['every_day_ecpm_for_fourteen_days'] + '"></td>');
                }else if('cpa' == key){
                    td = $('<td title="'+ one['every_day_cpa_for_fourteen_days'] + '"></td>');
                }else if('cpa_div_ecpm' == key){
                    td = $('<td title="'+ one['every_day_cpa_div_ecpm_for_fourteen_days'] + '"></td>');
                }else if('incoming' == key){
                    td = $('<td title="'+ one['every_day_incoming_for_fourteen_days'] + '"></td>');
                    if(r < 0){
                        td.addClass("red");
                    }
                }else if('revenue_per_install' == key){
                    r = r / 2;
                }else if('cost_upper_limit' == key){
                    td = $("<td class='cost_upper_limit'></td>");
                    if(r ==""){
                        r = "--";
                    }
                }
                td.text(r);
                tr.append(td);
            }
//            var td_outer = $('<td></td>');
//            var btn = $('<input type="button" value="跳转更新">');
//            btn.data("country_name", one['country_name']);
//            btn.click(function(){
//                var country_name = $(this).data("country_name");
//                $.post('country_analysis_report/query_id_of_auto_create_campaigns', {
//                    tagName: tagName,
//                    curr_country_name: country_name
//            },function(data){
//                    if (data && data.ret == 1) {
//                        window.open("campaigns_create.jsp?type=auto_create&network=facebook&id="+data.id_facebook,"_blank");
//                        window.open("campaigns_create.jsp?type=auto_create&network=adwords&id="+data.id_adwords,"_blank");
//                    } else {
//                        admanager.showCommonDlg("错误", data.message);
//                    }
//                }, 'json');
//            });
//            td_outer.append(btn);
//            tr.append(td_outer);
            $('#results_body').append(tr);
        }
    }

    //实现修改花费上限的输入框功能
    $("#results_body").on("click",".cost_upper_limit",function(){
        $("#result_header tr").children("th:eq(21)").empty();
        $("#result_header tr").children("th:eq(21)").append("花费上限<button class='btn btn-link glyphicon glyphicon-pencil' title='修改花费上限'></button>");
        var elementCheck = $(this).children("input[type='text']").attr("class");
        if(elementCheck=="new_cost_upper_limit"){
            return false;
        }
        var value = $(this).text();
        $(this).empty();
        if(value=="--"){
            $(this).append("<input class='new_cost_upper_limit' type='text' style='width:60px;height:25px'>");
        }else{
            $(this).append("<input class='new_cost_upper_limit' type='text' style='width:60px;height:25px'value='"+value+"'>");
        }
    });
    $("#result_header").on("click",".btn-link",function(){
        var cost_array = [];
        var collection = $(".new_cost_upper_limit");
        collection.each(function(idx){
            var value = $(this).val().trim().replace(/(\D*)(\d+)(\D*)/,"$2");
            if(value==""){
                var tr = $(this).parent();
                tr.text("--");
                return true;
            }
            var tr = $(this).parents("tr");
            var costGroup = {};
            var countryName = tr.children("td:eq(0)").text(); //这里得到的是完整的国家名
            regionList.forEach(function(region){
                if(region.name == countryName){
                    costGroup.country_code = region.country_code;
                }
            });
            costGroup.cost_upper_limit = value;
            cost_array.push(costGroup);
        });
        var cost_array_string = JSON.stringify(cost_array);
        var app_name = $("#inputSearch").val();
        // var cost_array_json = $.toJSON(cost_array);
        $.post("country_analysis_report/modify_web_ad_rules",{
            app_name:app_name,
            cost_array:cost_array_string
        },function(data){
            if(data && data.ret == 1){
                admanager.showCommonDlg("提示",data.message+"✪ω✪");
                setTimeout(function(){
                    $("#common_message_dialog").modal("hide");
                    $("#btnSearch").click();
                },1500)
            }else{
                admanager.showCommonDlg("提示",data.message);
            }
        },"json");
    });

</script>
</body>
</html>
