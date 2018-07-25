<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/rootBase.jsp" %>

<html>
<head>
    <title>广告语替换</title>
    <style>
        .red {
            color: red;
        }
        tbody{
            overflow-x: hidden;
            overflow-y: auto;
            height:400px;
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
            <label>
                <input type="radio" name="optionsRadios" value="0" checked>
                Facebook 广告
            </label>
            <label>
                <input type="radio" name="optionsRadios" value="1">
                AdWords 广告
            </label>
        </div>
    </div>

    <div class="form-horizontal" action="#" id="formFacebook">
        <div class="form-group">
            <label for="selectApp" class="col-sm-2 control-label">即将生成的应用：</label>
            <div class="col-sm-3">
                <select class="form-control" id="selectApp">
                </select>
            </div>
        </div><br>
        <div class="form-group">
            <label for="selectApp" class="col-sm-2 control-label">已经存在的应用：</label>
            <div class="col-sm-3">
                <select class="form-control" id="selectApp1">
                </select>
            </div>
        </div><br>

        <div class="form-group">
            <div class="col-sm-7" style="text-align:center">
                <input type="button" class="btn btn-primary" id="btnInsert_replace" value="开始替换"/>
            </div>
        </div>
        <br>
    </div>

</div>

</div>

<script>

    //界面加载好以后初始化的数据
    function init() {
        $("li[role='presentation']:eq(11)").addClass("active");

        //post用于往【应用】下拉列表里动态添加选项
        $.post('adReplace/tagName/query', {
            word: '',
        }, function(data) {
            if (data && data.ret == 1) {
                appList = data.data;
                appList.forEach(function(one) {
                    $('#selectApp').append($("<option>" + one.tag_name + "</option>"));
                    $('#selectApp1').append($("<option>" + one.tag_name + "</option>"));
                })
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        }, 'json');
    }
    init();

    //【开始替换】按钮
    $('#btnInsert_replace').click(function () {
        var app0 = $('#selectApp').val();
        var app1 = $('#selectApp1').val();
        var option = $('input[name="optionsRadios"]:checked').val();

        $.post("adReplace/adReplace", {
            app0: app0,
            app1: app1,
            option:option
        }, function (data) {
            if (data.ret == 1) {
                layer.tips("广告替换成功！","#btnInsert_replace",{tips:1,time:3000});
            } else {
                admanager.showCommonDlg("提示", data.message);
            }
        }, "json");
        return false;
    });

</script>
</body>
</html>