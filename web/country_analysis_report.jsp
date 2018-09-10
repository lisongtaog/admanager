<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/rootBase.jsp" %>
<html>
<head>
    <title>国家分析报告</title>

    <style>
        table th {
            min-width: 35px;
            max-width: 120px;
            overflow: auto;
            word-wrap: break-word;
            text-overflow: ellipsis;
            white-space: normal;
        }

        td.changed {
            background-color: #0f0;
        }

        #total_result.editable {
            background-color: yellow;
        }

        /*表格可编辑的列*/
        #result_header tr th.editColumn,
        #results_body tr td.editColumn {
            padding-left: 3px;
            padding-right: 3px;
        }

        /*表格可编辑列的 input*/
        #results_body tr td.editColumn input.editInput {
            width: 60px;
            height: 25px;
            border: none;
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
    <%@include file="common/navigationbar.jsp" %>

    <div class="panel panel-default" style="margin-top: 10px">
        <div class="panel-heading" id="panel_title">
            <span>开始日期</span>
            <input type="text" value="2012-05-15" id="inputStartTime" onchange="initTableHead();" readonly>
            <span>结束日期</span>
            <input type="text" value="2012-05-15" id="inputEndTime" onchange="initTableHead();" readonly>
            <span>标签</span>
            <input id="inputSearch" class="form-control" style="display: inline; width: auto;" type="text"/>
            <button id="btnSearch" class="btn btn-default glyphicon glyphicon-search"></button>
            <button id="editBtn" class="btn btn-default glyphicon glyphicon-edit" style="float: right">修改配置</button>
        </div>
    </div>
    <div class="panel panel-default">
        <div class="panel-body" id="total_result">
        </div>
        <span style="color: #0044cc">公式：当天回本率=NewRevenue/Cost;四天的每日收入是累计值</span>
    </div>
    <table class="table table-hover">
        <thead id="result_header">
        </thead>
        <tbody id="results_body">
        </tbody>
    </table>

</div>

<jsp:include page="common/loading_dialog.jsp"></jsp:include>

<script>
    init();//初始化日期控件、下拉框

    /**触发查询*/
    function doResearch(sorterId) {
        var query = $("#inputSearch").val();
        var startTime = $('#inputStartTime').val();
        var endTime = $('#inputEndTime').val();

        if (!query || "" == query) {
            admanager.showCommonDlg("错误", "标签为必录项!");
            return false;
        }
        $('#results_body').html("");//清空结果表
        $('#total_result').html("");

        var params = {"tagName": query, "startTime": startTime, "endTime": endTime};
        if (sorterId && sorterId >= 0) {
            params.sorterId = sorterId;
        }
        $("#btnSearch").prop("disabled", true);
        admanager.showCommonDlg("Please wait", "Querying data...");
        $.post('country_analysis_report/query_country_analysis_report', params, function (data) {
            if (data && data.ret == 1) {
                var sameDate = data.same_date == 1 ? 1 : 0;
                setData(data, sameDate);
                renderSummary(data);//设置表头汇总信息
                admanager.hideCommonDlg();
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
            $("#btnSearch").prop("disabled", false);
        }, 'json');
    }

    $("#btnSearch").click(doResearch);//绑定查询事件
    //绑定排序 查询事件
    function bindSortOp() {
        $('.sorter').click(function () {
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
            doResearch(sorterId);//触发查询
        });
    }

    /**
     * 顶部表头汇总信息
     */
    function renderSummary(data) {
        var str = "标签：" + $("#inputSearch").val() + "&nbsp;&nbsp;&nbsp;&nbsp;TotalCost: " + data.total_cost + "&nbsp;&nbsp;&nbsp;&nbsp;PuserchaedUser: " + data.total_puserchaed_user +
            "&nbsp;&nbsp;&nbsp;&nbsp;CPA: " + data.total_cpa + "&nbsp;&nbsp;&nbsp;&nbsp;Revenue: " + data.total_revenue;
        if (data.same_date == 1) {
            str = "标签：" + $("#inputSearch").val() + "&nbsp;&nbsp;&nbsp;&nbsp;PuserchaedUser: " + data.total_puserchaed_user +
                "&nbsp;&nbsp;&nbsp;&nbsp;CPA: " + data.total_cpa + "&nbsp;&nbsp;&nbsp;&nbsp;Revenue: " + data.total_revenue
                + "&nbsp;&nbsp;&nbsp;&nbsp;TotalNewRevenue/TotalCost = " + data.total_new_revenue
                + "/" + data.total_cost + " = " + data.total_new_revenue_div_cost;
        }
        str += "<br/><span class='estimateResult'></span>"
        $('#total_result').html(str);
        $('#total_result').removeClass("editable");
    }

    function setData(data, same_date) {
        $('#results_body > tr').remove();
        var arr = data.array;
        var len = arr.length;
        if (len == 0) {
            $("#editMaxCostBtn").css("display", "none");
            $("#editBtn").css("display", "none");
        } else {
            $("#editMaxCostBtn").css("display", "");
            if (same_date == 1) {
                $("#editBtn").css("display", "");
            }
        }

        var keyset = ["costs", "cost_upper_limit", "purchasedUser", "installed", "uninstalledRate",
            "activeUser", "revenue", "incoming", "ecpm", "cpa",
            "bidding_summary"];
        if (same_date == 1) {
//            keyset = ["costs","cost_upper_limit","purchasedUser", "installed", "uninstalledRate",
//                "activeUser","revenue", "newRevenue","sample_user","total_new_user","recoveryCostRatio","incoming", "ecpm","cpa","tag_cpa",
//                 "newUserEcpm","tag_ecpm","newUserAvgImpression","tag_impression","newUserTagRevenue",
//                "first_day_revenue","second_day_revenue","third_day_revenue","fourth_day_revenue","bidding_summary"];
            keyset = ["costs", "cost_upper_limit", "purchasedUser", "installed", "uninstalledRate",
                "activeUser", "revenue", "incoming", "cpa", "ecpm", "cpa_div_ecpm", "newRevenue", "recoveryCostRatio", "new_user_revenues",
                "newUserEcpm", "newUserAvgImpression", "sample_user", "total_new_user", "newUserTagRevenue",
                "first_day_revenue", "second_day_revenue", "third_day_revenue", "fourth_day_revenue", "seven_days_ltv", "fourteen_days_ltv", "bidding_summary"];
        }


        var one, key, val, floatKey;
        var tr, td;
        var isColumnEditable;//列是否可编辑
        for (var i = 0; i < len; i++) {
            one = arr[i];
            tr = $('<tr></tr>');
            td = $('<td>' + one['country_name'] + '<input type="hidden" value="' + one['country_code'] + '"/></td>');
            tr.append(td);//第一列 国家

            for (var j = 0; j < keyset.length; j++) {
                isColumnEditable = false;//默认不可编辑
                key = keyset[j];
                val = one[key];

                if ('cost_upper_limit' == key) {//国家花费上限
                    isColumnEditable = true;
                    if (val == "") {
                        td = $("<td><input type='text' class='editInput' onchange='editInputChange(this);'  /></td>");
                    } else {
                        td = $("<td><input type='text' class='editInput' onchange='editInputChange(this);' value='" + one['cost_upper_limit'] + "' /></td>");
                    }
                    td.addClass("editColumn");
                } else if ('tag_cpa' == key || 'tag_ecpm' == key || 'tag_impression' == key) {//期望 cpa、期望 ecpm、期望 用户平均展示次数
                    isColumnEditable = true;
                    value = one[key];
                    if (!value || undefined === value) {
                        value = "";
                    }
                    td = $("<td><input type='text' class='editInput' onchange='editInputChange(this);' value='" + value + "' /></td>");
                    td.addClass("editColumn");
                } else if (key.indexOf("_") > -1) {//key包含下划线 则该列没有浮动数据显示；反之则有
                    td = $('<td></td>');
                } else {
                    floatKey = "float_" + key;
                    td = $('<td ' + getFloatTitle(same_date, one[floatKey]) + '></td>');
                    if ('incoming' == key && val < 0) {
                        td.addClass("red");
                    }
                }
                if (!isColumnEditable) {//不可编辑列 输出td文本
                    td.text(val);
                }
                tr.append(td);
            }
            /*var td_outer = $('<td></td>');
            var btn = $('<input type="button" value="跳转更新">');
            btn.data("country_name", one['country_name']);
            btn.click(function(){
                var country_name = $(this).data("country_name");
                $.post('country_analysis_report/query_id_of_auto_create_campaigns', {
                    tagName: tagName,
                    curr_country_name: country_name
            },function(data){
                    if (data && data.ret == 1) {
                        window.open("campaigns_create.jsp?type=auto_create&network=facebook&id="+data.id_facebook,"_blank");
                        window.open("campaigns_create.jsp?type=auto_create&network=adwords&id="+data.id_adwords,"_blank");
                    } else {
                        admanager.showCommonDlg("错误", data.message);
                    }
                }, 'json');
            });
            td_outer.append(btn);
            tr.append(td_outer);*/
            $('#results_body').append(tr);
        }
    }

    function getFloatTitle(same_date, value) {
        var floatTitleStr = same_date == 1 ? 'title="' + value + '"' : "";
        return floatTitleStr;
    }

    /**初始化*/
    function init() {
        $("li[role='presentation']:eq(2)").addClass("active");
        var now = new Date();
        var pre = new Date(new Date().getTime() - 86400 * 1000);//当前时间前一天
        var date = getDateStr(pre);
        $('#inputStartTime').val(date);
        $('#inputEndTime').val(date);
        $('#inputStartTime').datetimepicker({
            minView: "month",
            format: 'yyyy-mm-dd',
            endDate: now,
            autoclose: true,
            todayBtn: true
        });
        $('#inputEndTime').datetimepicker({
            minView: "month",
            format: 'yyyy-mm-dd',
            endDate: now,
            autoclose: true,
            todayBtn: true
        });
        initTableHead();//页面初始化表头
        var data = <%=array.toString()%>;
        $("#inputSearch").autocomplete({
            source: data
        });
    }

    function initTableHead() {
        var startTime = $('#inputStartTime').val();
        var endTime = $('#inputEndTime').val();
        var isSameDate = startTime == endTime ? true : false;
        $("#editBtn").css("display", "none");
        var headHtml;
        if (isSameDate) {
            headHtml =
                '<tr>'
                + '    <th>国家</th>'
                + '    <th>Cost<span sorterid="1031" class="sorter glyphicon glyphicon-arrow-down"></span></th>'
                + '    <th class="editColumn">花费<br>上限</th>'
                + '    <th>Purchased<br>User<span sorterid="1033" class="sorter glyphicon glyphicon-arrow-down"></span></th>'
                + '    <th>Installed<span sorterid="1034" class="sorter glyphicon glyphicon-arrow-down"></span></th> '
                + '    <th>卸载率</th> '
                + '    <th>ActiveUser<span sorterid="1038" class="sorter glyphicon glyphicon-arrow-down"></span></th>'
                + '    <th>Revenue<span sorterid="1039" class="sorter glyphicon glyphicon-arrow-down"></span></th>'
                + '    <th>Incoming<span sorterid="1042" class="sorter glyphicon glyphicon-arrow-down"></span></th>  '
                + '    <th>CPA<span sorterid="1041" class="sorter glyphicon glyphicon-arrow-down"></span></th> '
                + '    <th>ECPM<span sorterid="1040" class="sorter glyphicon glyphicon-arrow-down"></span></th>'
                + '    <th>CPA/<br>Ecpm</th>'
                + '    <th>NewRevenue<br>(估算)<span sorterid="1038" class="sorter glyphicon glyphicon-arrow-down"></span></th>'
                + '    <th>当天<br>回本率</th>'
                + '    <th>抽样用户<br>收入</th>'
                + '    <th>抽样用户<br>ECPM</th>'
                + '    <th>抽样用户<br>人均展示</th>'
                + '    <th>抽样<br>用户</th>'
                + '    <th>总的<br>新用户</th>'
                + '    <th title="老Ecpm * 新用户平均展示 / 1000">当日变现能力</th>'
                + '    <th>1Day<br>Revenue</th>'
                + '    <th>2Day<br>Revenue</th>'
                + '    <th>3Day<br>Revenue</th>'
                + '    <th>4Day<br>Revenue</th>'
                + '    <th>7Day<br>&nbsp;LTV</th>'
                + '    <th>14Day<br>&nbsp;&nbsp;LTV</th>'
                + '    <th>竞价</th>'
                + '</tr>';

//                +'    <th class="editColumn">期望CPA</th>'//ML

//                +'    <th class="editColumn">期望Ecpm</th>' //ML
//                +'    <th class="editColumn">期望展示</th>'//ML

        } else {
            headHtml =
                '<tr>'
                + '    <th>国家</th>'
                + '    <th>Cost<span sorterid="1031" class="sorter glyphicon glyphicon-arrow-down"></span></th>'
                + '    <th class="editColumn">花费<br>上限</th>'
                + '    <th>Purchased<br>User<span sorterid="1033" class="sorter glyphicon glyphicon-arrow-down"></span></th>'
                + '    <th>Installed<span sorterid="1034" class="sorter glyphicon glyphicon-arrow-down"></span></th>'
                + '    <th>卸载率</th>'
                + '    <th>ActiveUser<span sorterid="1038" class="sorter glyphicon glyphicon-arrow-down"></span></th>'
                + '    <th>Revenue<span sorterid="1039" class="sorter glyphicon glyphicon-arrow-down"></span></th>'
                + '    <th>Incoming<span sorterid="1042" class="sorter glyphicon glyphicon-arrow-down"></span></th>'
                + '    <th>ECPM<span sorterid="1040" class="sorter glyphicon glyphicon-arrow-down"></span></th>'
                + '    <th>CPA<span sorterid="1041" class="sorter glyphicon glyphicon-arrow-down"></span></th> '
                + '    <th>竞价</th>'
                + '</tr>'
        }
        $('#result_header').html(headHtml);
        $('#results_body').html("");//清空结果表
        var editMaxCostBtnHtml = '<button id="editMaxCostBtn" onclick="saveEditMaxCost();" class="btn btn-link glyphicon glyphicon-pencil" style="display: none" title="修改花费上限"></button></th>';
        $("#result_header tr th.editColumn:first").append(editMaxCostBtnHtml);
        bindSortOp();//绑定排序事件
    }

    //var countryMaxCost = [];//最大花费 数组存储
    //var appCountryTarget = [];//app country期望值

    $("#editBtn").click(saveEdit);//期望cpa、期望新用户ECPM、期望用户平均展示次数
    function saveEditMaxCost() {
        $("#editMaxCostBtn").prop("disabled", true);
        $("#editBtn").prop("disabled", true);
        var countryName, countryCode, maxCost;//国家,花费上限
        var cost_array = [];//花费上限
        var tr, td, input, costGroup;
        $("#results_body > tr").each(function (index, element) {
            tr = $(element);
            //countryName = tr.children("td:eq(0)").text();
            countryCode = tr.children("td:eq(0)").children("input").val();
            input = tr.children("td:eq(2)").children("input.editInput");
            maxCost = $(input).val();
            //console.info(maxCost);

            costGroup = {};
            //costGroup.countryName = countryName; //这里得到的是完整的国家名
            costGroup.countryCode = countryCode; //国家代码
            costGroup.cost_upper_limit = maxCost;//花费上限
            cost_array.push(costGroup);
        });

        var cost_array_string = JSON.stringify(cost_array);
        var app_name = $("#inputSearch").val();
        admanager.showCommonDlg("提示", "修改中，请稍后...");

        $.post("country_analysis_report/modify_web_ad_rules", {
            app_name: app_name,
            cost_array: cost_array_string
        }, function (data) {
            if (data && data.ret == 1) {
                admanager.showCommonDlg("提示", data.message + "✪ω✪");
                setTimeout(function () {
                    doResearch();
                }, 2000);
            } else {
                admanager.showCommonDlg("提示", data.message);
            }
            $("#editMaxCostBtn").prop("disabled", false);
            $("#editBtn").prop("disabled", false);
        }, "json");
    }

    //app country期望值
    function saveEdit() {
        $("#editMaxCostBtn").prop("disabled", true);
        $("#editBtn").prop("disabled", true);
        var countryCode, tag_cpa, tag_ecpm, tag_impression;//国家
        var dataArray = [];
        var tr, item;//12 15 18
        $("#results_body > tr").each(function (index, element) {
            tr = $(element);
            countryCode = tr.children("td:eq(0)").children("input").val();
            tag_cpa = tr.children("td:eq(13)").children("input.editInput").val();//期望cpa
            tag_ecpm = tr.children("td:eq(16)").children("input.editInput").val();//期望ecpm
            tag_impression = tr.children("td:eq(19)").children("input.editInput").val();//期望 用户展示次数
            //console.info(maxCost);
            item = {};
            item.countryCode = countryCode; //国家代码
            item.tagCpa = tag_cpa;
            item.tagEcpm = tag_ecpm;
            item.tagImpression = tag_impression;
            dataArray.push(item);
        });

        var arrayStr = JSON.stringify(dataArray);
        var app_name = $("#inputSearch").val();
        admanager.showCommonDlg("提示", "修改中，请稍后");
        $.post("country_analysis_report/modify_app_country_target", {
            app_name: app_name,
            tag_array: arrayStr
        }, function (data) {
            if (data && data.ret == 1) {
                admanager.showCommonDlg("提示", data.message + "✪ω✪");
                setTimeout(function () {
                    doResearch();
                }, 2000);
            } else {
                admanager.showCommonDlg("提示", data.message);
            }
            $("#editMaxCostBtn").prop("disabled", false);
            $("#editBtn").prop("disabled", false);
        }, "json");
    }

    //表格 可编辑区域：输入值校验
    function editInputChange(thizz) {
        var input = $(thizz);
        var value = input.val();
        var reg = /^\d+(\.\d+)?$/;
        if (value != "" && !reg.test(value)) {
            alert("输入不合规，请输入正数数值");
            input.focus();
        }
    }

</script>
</body>
</html>
