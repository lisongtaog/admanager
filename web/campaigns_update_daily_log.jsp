<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/rootBase.jsp"%>

<html>
<head>
    <title>日更记录</title>

    <style>
        td.editable {

        }
        td.editable.checkbox {

        }

        #total_result.editable {
            background-color: yellow;
        }
        .estimateResult {
            color: #ff7044;
        }
        th {
            color: #9d56ff;
        }
        .blue {
            color: #0f0;
        }
        .yellow{
            color: #ffa17a;
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
            <th>AppName</th>
            <th>系列ID</th>
            <th>广告账号ID</th>
            <th>系列名称</th>
            <th>创建时间</th>
            <th>预算</th>
            <th>竞价</th>
            <th>定位国家</th>
        </tr>
        </thead>
        <tbody id="results_body">
        </tbody>
    </table>
</div>

<div id="new_campaign_dlg" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="dlg_title">修改系列</h4>
            </div>
            <div class="modal-body">
                <form id="modify_form" class="form-horizontal" action="#" autocomplete="off">
                    <div class="form-group">
                        <label for="inputCampaignName" class="col-sm-2 control-label">系列名称</label>
                        <div class="col-sm-10">
                            <input class="form-control" id="inputCampaignName" placeholder="系列名称" autocomplete="off">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="inputStatus" class="col-sm-2 control-label">是否开启</label>
                        <div class="col-sm-10">
                            <input type="checkbox" id="inputStatus">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="inputBudget" class="col-sm-2 control-label">预算</label>
                        <div class="col-sm-10">
                            <input type="number" class="form-control" id="inputBudget" placeholder="预算" autocomplete="off">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="inputBidding" class="col-sm-2 control-label">竞价</label>
                        <div class="col-sm-10">
                            <input type="number" class="form-control" id="inputBidding" placeholder="竞价" autocomplete="off">
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-primary">确定</button>
            </div>
        </div>
    </div>
</div>

</div>

<jsp:include page="common/loading_dialog.jsp"></jsp:include>

<script>
    var data = <%=array.toString()%>;
    $("#inputSearch").autocomplete({
        source: data
    });

    function init() {
        $("li[role='presentation']:eq(12)").addClass("active");
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
    }
    init();

    function setData(data) {
        $('#results_body > tr').remove();
        for (var i = 0; i < data.length; i++) {
            var one = data[i];
            var tr = $('<tr></tr>');
            var keyset = ["app_name","campaign_id", "account_id", "campaign_name", "create_time",
                "budget", "bidding","country_region"];
            for (var j = 0; j < keyset.length; j++) {
                var td = $('<td></td>');
                td.text(one[keyset[j]]);
                tr.append(td);
            }
            $("tr:even td").css("background","#c7e5ff");
            $("tr:odd td").css("background","#eaf5ff");
            $('#results_body').append(tr);
        }
    }


    $('#btnSearch').click(function () {
        var tag = $("#inputSearch").val();
        var startTime = $('#inputStartTime').val();
        var endTime = $('#inputEndTime').val();

        $.post('tags/selectByTagName', {
            tag: tag,
            startTime: startTime,
            endTime: endTime
        }, function (data) {
            if (data && data.ret == 1) {
                $('#result_header').html("<tr><th>AppName</th><th>系列ID</th><th>广告账号ID</th><th>系列名称</th><th>创建时间</th><th>预算</th><th>竞价</th><th>定位国家</th></tr>");
                arr = data.arr;
                setData(arr);
                var str = "创建数量: " + data.total_count + "条   总预算: " + data.total_budget;
                str += "<br/><span class='estimateResult'></span>"
                $('#total_result').html(str);
                $('#total_result').addClass("estimateResult");
                $('#total_result').removeClass("editable");
                $("tr th").css("background","#85e2ff");
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        }, 'json');
    });
</script>
<script src="js/interlaced-color-change.js"></script>
</body>
</html>
