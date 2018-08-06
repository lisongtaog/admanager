<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/rootBase.jsp" %>

<html>
<head>
    <title>应用日更记录</title>

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
        .demo_head{
            color: white;
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
            color:#0f0;
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
            <input type="text" value="2018-1-1" id="inputStartDate" readonly>
            <span>结束日期</span>
            <input type="text" value="2018-1-1" id="inputEndDate" readonly>

            <span>标签</span>
            <input id="inputSearch" class="form-control" style="display: inline; width: auto;" type="text"/>
            <button id="btnSearch" class="btn btn-default glyphicon glyphicon-search"></button>
        </div>
    </div>

    <div class="panel panel-default">
        <div class="panel-body" id="total_result">
                <div class="content1">
                    <span class="recordFont">应用日志</span>
                    <textarea id="inputContent"></textarea>
                </div>
                <div class="content2 btn-group">
                    <button id="deleteContent" class="btn btn-default">清除</button>
                    <button id="createContent" class="btn btn-default">更新</button>
                </div>
        </div>
    </div>

    <table class="table table-hover">
        <thead id="result_header">
        <tr>
            <th class = "demo_head">应用日活动信息</th>
        </tr>
        </thead>
        <tbody id="results_body">
        </tbody>
    </table>
</div>


<jsp:include page="common/loading_dialog.jsp"></jsp:include>

<script src="js/country-name-code-dict.js"></script>

<script>
    var data = <%=array.toString()%>;
//    var textLast = $("#inputContent").val();
    $("#inputSearch").autocomplete({
        source: data
    });

    function init() {
        $("li[role='presentation']:eq(8)").addClass("active");
        var now = new Date(new Date().getTime() - 86400 * 1000); //得到一个该行代码运行时的时间
        var defaultYear = now.getFullYear();
        var defaultMonth = now.getMonth()<9 ? "0"+ (now.getMonth()+1) : ""+(now.getMonth()+1);
        var defaultDate = now.getDate()<10 ? "0"+(now.getDate()) : ""+now.getDate();
        $('#inputStartDate').val(defaultYear + "-" + defaultMonth + "-" + defaultDate);
        $('#inputEndDate').val(defaultYear + "-" + defaultMonth + "-" + defaultDate);

        $('#inputStartDate').datetimepicker({
            minView: "month",
            format: 'yyyy-mm-dd',
            autoclose: true,
            todayBtn: true
        });
        $('#inputEndDate').datetimepicker({
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
            $.post("app_activity_daily/create",{
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
<script src="js/app_activity_daily.js"></script>
<script src="js/interlaced-color-change.js"></script>
</body>
</html>
