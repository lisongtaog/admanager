<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>转化录入</title>
    <link rel="stylesheet" href="bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="bootstrap/css/bootstrap-theme.min.css"/>
    <link rel="stylesheet" href="css/core.css"/>
    <link rel="stylesheet" href="css/bootstrap-tagsinput.css"/>
    <link rel="stylesheet" href="css/bootstrap-datetimepicker.css"/>
    <link rel="stylesheet" href="jqueryui/jquery-ui.css"/>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.4/css/select2.min.css" rel="stylesheet" />
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
    <ul class="nav nav-pills">
        <li role="presentation"><a href="index.jsp">首页</a></li>
        <li role="presentation"><a href="campaigns_create.jsp">创建广告</a></li>
        <li role="presentation"><a href="adaccounts.jsp">广告账号管理</a></li>
        <li role="presentation"><a href="adaccounts_admob.jsp">广告账号管理(AdMob)</a></li>
        <li role="presentation"><a href="campaigns.jsp">广告系列管理</a></li>
        <li role="presentation"><a href="campaigns_admob.jsp">广告系列管理(AdMob)</a></li>
        <li role="presentation"><a href="tags.jsp">标签管理</a></li>
        <li role="presentation"><a href="rules.jsp">规则</a></li>
        <li role="presentation"><a href="query.jsp">查询</a></li>
        <li role="presentation"><a href="system.jsp">系统管理</a></li>
        <li role="presentation"><a href="advert_insert.jsp">广告存储</a></li>
        <li role="presentation" class="active"><a href="#">转化录入</a></li>
    </ul>

    <div class="panel panel-default" style="margin-top: 10px">
        <div class="panel-heading" id="panel_title">
            <label>
                <input type="radio" name="optionsRadios" id="checkFacebook" checked>
                Facebook 转化
            </label>
            <label>
                <input type="radio" name="optionsRadios" id="checkAdmob">
                AdWords 转化
            </label>
        </div>
    </div>


    <form class="form-horizontal" action="#" id="formFacebook">
        <div class="form-group">
            <label for="selectApp" class="col-sm-2 control-label">应用</label>
            <div class="col-sm-10">
                <select class="form-control" id="selectApp">
                </select>
            </div>
        </div>
        <div class="form-group">
            <label for="inputID" class="col-sm-2 control-label">转化ID</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputID" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputName" class="col-sm-2 control-label">转化名称</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputName" />
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-10" style="text-align: center">
                <input type="submit" class="btn btn-primary" id="btnInsert" value="保存"/>
            </div>
        </div>
    </form>


    <form class="form-horizontal" action="#" id="formAdmob">
        <div class="form-group">
            <label for="selectApp" class="col-sm-2 control-label">应用</label>
            <div class="col-sm-10">
                <select class="form-control" id="selectAppAdmob">

                </select>
            </div>
        </div>
        <div class="form-group">
            <label for="inputID" class="col-sm-2 control-label">转化ID</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputIDAdmob" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputName" class="col-sm-2 control-label">转化名称</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputNameAdmob" />
            </div>
        </div>

        <div class="form-group">
            <div class="col-sm-10" style="text-align: center">
                <input type="submit" class="btn btn-primary" id="btnInsertAdmob" value="保存"/>
            </div>
        </div>
    </form>

</div>

</div>

<jsp:include page="loading_dialog.jsp"></jsp:include>


<script src="js/jquery.js"></script>
<script src="bootstrap/js/bootstrap.min.js"></script>
<script src="js/core.js"></script>
<script src="js/bootstrap-datetimepicker.js"></script>
<script src="jqueryui/jquery-ui.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.4/js/select2.min.js"></script>
<script>
    function init() {
        $('.select2').select2();
        $.post('system/fb_app_id_rel/query', {
            word: '',
        }, function(data) {
            if (data && data.ret == 1) {
                appList = data.data;
                appList.forEach(function(one) {
                    $('#selectApp').append($("<option>" + one.tag_name + "</option>"));
                    $('#selectAppAdmob').append($("<option>" + one.tag_name + "</option>"));
                })
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        }, 'json');

        $('#checkAdmob').click(function () {
            if ($('#checkAdmob').prop('checked')) {
                $('#formFacebook').hide();
                $('#formAdmob').show();
            }
        });
        $('#checkFacebook').click(function () {
            if ($('#checkFacebook').prop('checked')) {
                $('#formFacebook').show();
                $('#formAdmob').hide();
            }
        });

        $('#formAdmob').hide();
    }

    init();

    $('#btnInsertAdmob').click(function() {
        var appName = $('#selectAppAdmob').val();
        var ctid = $('#inputIDAdmob').val();
        var conversionName = $('#inputNameAdmob').val();
        $.post("advert_conversion_admob/save_advert_conversion", {
            appName: appName,
            ctid: ctid,
            conversionName: conversionName
        }, function (data) {
            if (data && data.ret == 1) {
                if(data.existData == "true"){
                    admanager.showCommonDlg("提示", "更新记录成功");
                }else{
                    admanager.showCommonDlg("提示", "添加记录成功");
                }
            } else {
                admanager.showCommonDlg("提示", data.message);
            }
        }, "json");
        return false;
    });


    $('#btnInsert').click(function () {
        var appName = $('#selectApp').val();
        var ctid = $('#inputID').val();
        var conversionName = $('#inputName').val();
        $.post("advert_conversion/save_advert_conversion", {
            appName: appName,
            ctid: ctid,
            conversionName: conversionName
        }, function (data) {
            if (data && data.ret == 1) {
                if(data.existData == "true"){
                    admanager.showCommonDlg("提示", "更新记录成功");
                }else{
                    admanager.showCommonDlg("提示", "添加记录成功");
                }
            } else {
                admanager.showCommonDlg("提示", data.message);
            }
        }, "json");
        return false;
    });

</script>
</body>
</html>