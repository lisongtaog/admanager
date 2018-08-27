<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/rootBase.jsp" %>

<html>
<head>
    <title>转化录入</title>
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
        <%--<div class="form-group">--%>
            <%--<label for="existConversionNameAdmob" class="col-sm-2 control-label">转化名称</label>--%>
            <%--<div class="col-sm-10">--%>
                <%--<input type="hidden"  class="form-control" id="existConversionNameAdmob" />--%>
            <%--</div>--%>
        <%--</div>--%>
        <input type="hidden" id="existConversionNameAdmob">
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

<jsp:include page="common/loading_dialog.jsp"></jsp:include>

<script>
    function init() {
        $("li[role='presentation']:eq(8)").addClass("active");
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
        var existConversionName = $('#existConversionNameAdmob').val();
        $('#inputIDAdmob').val("");
        $('#existConversionNameAdmob').val("false");
        $.post("advert_conversion_admob/save_advert_conversion", {
            appName: appName,
            ctid: ctid,
            conversionName: conversionName,
            existConversionName: existConversionName
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

    $('#selectAppAdmob,#inputIDAdmob').change(function() {
        var appName = $('#selectAppAdmob').val();
        var ctid = $('#inputIDAdmob').val();
        if(appName != "" && ctid != ""){
            $.post("advert_conversion_admob/query_advert_conversion_by_app_name_and_ctid", {
                appName: appName,
                ctid: ctid
            }, function (data) {
                if (data && data.ret == 1) {
                    $('#inputNameAdmob').val(data.conversion_name);
                    if(data.conversion_name != ""){
                        $('#existConversionNameAdmob').val("true");
                    }
                }
            }, "json");
            return false;
        }
    });

</script>
</body>
</html>