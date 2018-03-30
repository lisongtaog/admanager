<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>应用日更记录</title>
    <link rel="stylesheet" href="bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="bootstrap/css/bootstrap-theme.min.css"/>
    <link rel="stylesheet" href="css/core.css"/>
    <link rel="stylesheet" href="css/bootstrap-tagsinput.css"/>
    <link rel="stylesheet" href="css/bootstrap-datetimepicker.css"/>
    <link rel="stylesheet" href="jqueryui/jquery-ui.css"/>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.4/css/select2.min.css" rel="stylesheet" />

    <style>
        .content1{
            float:left;
            width:23cm;
            height: 5.5cm;
            padding-top: 10px;
        }
        .content2{
            width:23cm;
        }
        #inputContent{
            width: 20cm;
            height: 5cm;
            padding: 10px;
            font-family:sans-serif;
        }
        .recordFont{
            vertical-align: top;
            font-family: Microsoft Himalaya;
            font-weight: bold;
            font-size:larger;
        }
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
            <span>日期</span>
            <input type="text" value="2018-1-1" id="inputDate" readonly>

            <span>标签</span>
            <input id="inputSearch" class="form-control" style="display: inline; width: auto;" type="text"/>
            <button id="btnSearch" class="btn btn-default glyphicon glyphicon-search"></button>
        </div>
    </div>

    <div class="panel panel-default">
        <div class="panel-body" id="total_result">
                <div class="content1">
                    <span class="recordFont">日活动记录</span>
                    <textarea id="inputContent"></textarea>
                </div>
                <div class="content2">
                    <button id="deleteContent" class="btn btn-default">清除</button>
                    &nbsp;&nbsp;
                    <button id="createContent" class="btn btn_default">更新</button>
                </div>
        </div>
    </div>

    <table class="table table-hover">
        <thead id="result_header">
        <tr>
            <th>国家</th>
            <th>应用日活动信息</th>
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

<jsp:include page="loading_dialog.jsp"></jsp:include>


<script src="js/jquery.js"></script>
<script src="bootstrap/js/bootstrap.min.js"></script>
<script src="js/core.js"></script>
<script src="js/bootstrap-datetimepicker.js"></script>
<script src="jqueryui/jquery-ui.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.4/js/select2.min.js"></script>
<script src="js/country-name-code-dict.js"></script>
<script>
    var data = <%=array.toString()%>;
//    var textLast = $("#inputContent").val();
    $("#inputSearch").autocomplete({
        source: data
    });

    function init() {
        $("li[role='presentation']:eq(12)").addClass("active");
        var now = new Date(new Date().getTime() - 86400 * 1000);
        $('#inputDate').val(now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate());

        $('#inputDate').datetimepicker({
            minView: "month",
            format: 'yyyy-mm-dd',
            autoclose: true,
            todayBtn: true
        });
    }
    init();

    function setData(data) {
        $('#results_body > tr').remove();
        if(data!=null){
            var contentRecorded = data["content"];
            $("#inputContent").val(contentRecorded);
            var array = data["resultArray"];
            for (var i = 0; i < array.length; i++) {
                var one = array[i];
                var tr = $('<tr></tr>');
                var keyset = ["country","content"];
                for (var j = 0; j < 2; j++) {
                    var td = $('<td></td>');
                    td.text(one[keyset[j]]);
                    tr.append(td);
                }
                $("tr:even td").css("background","#c7e5ff");
                $("tr:odd td").css("background","#eaf5ff");
                $('#results_body').append(tr);
            }
        }else{
            alert("No response!");
        }
    }


    $('#btnSearch').click(function () {
        var tag = $("#inputSearch").val();
        var date = $('#inputDate').val();
        $.post("app_update_daily_log/search", {
            tag_name: tag,
            date: date
        }, function (data) {
            setData(data);
        }, "json");
    });

    $("#deleteContent").click(function(){
        $("#inputContent").val("");
    });

    $("#createContent").click(function(){
        var textNow = $("#inputContent").val();
        var tag = $("#inputSearch").val();
        var date = $('#inputDate').val();
        if(tag ===""|| textNow ===""){
            alert("请勿创建空白记录");
        }else{
            $.post("app_update_daily_log/create",{
                content:textNow,
                date:date,
                tag_name:tag
            },function(data){
                //这里有创建的提示
                var flag = data["flag"];
                switch(flag){
                    case 1: alert(data["message"]);break;
                    case 0: alert(data["message"]);break;
                }
            },"json")
        }
//            textLast = $("#inputContent").val();
    });
</script>
<script src="js/interlaced-color-change.js"></script>
</body>
</html>
