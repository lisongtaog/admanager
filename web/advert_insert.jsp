<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>广告存储</title>
    <link rel="stylesheet" href="bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="bootstrap/css/bootstrap-theme.min.css"/>
    <link rel="stylesheet" href="css/core.css"/>
    <link rel="stylesheet" href="css/bootstrap-tagsinput.css"/>
    <link rel="stylesheet" href="css/bootstrap-datetimepicker.css"/>
    <link rel="stylesheet" href="jqueryui/jquery-ui.css"/>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.4/css/select2.min.css" rel="stylesheet" />
    <style>
        .red {
            color: red;
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
                <input type="radio" name="optionsRadios" id="checkFacebook" checked>
                Facebook 广告
            </label>
            <label>
                <input type="radio" name="optionsRadios" id="checkAdmob">
                AdWords 广告
            </label>
        </div>
    </div>

    <div id="moreCountryDlg" class="modal fade" tabindex="-1" role="dialog">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title" id="dlg_title">输入国家，每行一个</h4>
                </div>
                <div class="modal-body">
                    <form id="modify_form" class="form-horizontal" action="#" autocomplete="off">
                        <div class="form-group">
                            <label for="inputCountryAlias" class="col-sm-2 control-label">国家缩写</label>
                            <div class="col-sm-10">
                                <input id="inputCountryAlias" style="width:100%;" />
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

    <form class="form-horizontal" action="#" id="formFacebook">
        <div class="form-group">
            <label for="selectApp" class="col-sm-2 control-label">应用</label>
            <div class="col-sm-10">
                <select class="form-control" id="selectApp">
                </select>
            </div>
        </div>
        <div class="form-group">
            <label for="selectLanguage" class="col-sm-2 control-label">语言</label>
            <div class="col-sm-10">
                <select class="form-control" id="selectLanguage">
                </select>
            </div>
        </div><br>

        <div class="form-group">
            <label for="inputGroupOne" class="col-sm-2 control-label">组合</label>
            <div class="col-sm-1">
                <input type="text" value="1" readonly id="inputGroupOne" style="width: 10px;">
            </div>
        </div>
        <div class="form-group">
            <label for="inputTitle11" class="col-sm-2 control-label">广告标题</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputTitle11" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessage11" class="col-sm-2 control-label">广告语</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessage11" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputGroupTwo" class="col-sm-2 control-label">组合</label>
            <div class="col-sm-2">
                <input type="text" value="2" readonly id="inputGroupTwo" style="width: 20px;">
            </div>
        </div>
        <div class="form-group">
            <label for="inputTitle22" class="col-sm-2 control-label">广告标题</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputTitle22" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessage22" class="col-sm-2 control-label">广告语</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessage22" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputGroupThree" class="col-sm-2 control-label">组合</label>
            <div class="col-sm-3">
                <input type="text" value="3" readonly id="inputGroupThree" style="width: 30px;">
            </div>
        </div>
        <div class="form-group">
            <label for="inputTitle33" class="col-sm-2 control-label">广告标题</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputTitle33" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessage33" class="col-sm-2 control-label">广告语</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessage33" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputGroupFour" class="col-sm-2 control-label">组合</label>
            <div class="col-sm-4">
                <input type="text" value="4" readonly id="inputGroupFour" style="width: 40px;">
            </div>
        </div>
        <div class="form-group">
            <label for="inputTitle44" class="col-sm-2 control-label">广告标题</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputTitle44" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessage44" class="col-sm-2 control-label">广告语</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessage44" />
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
            <label for="selectAppAdmob" class="col-sm-2 control-label">应用</label>
            <div class="col-sm-10">
                <select class="form-control" id="selectAppAdmob">

                </select>
            </div>
        </div>

        <div class="form-group">
            <label for="selectLanguageAdmob" class="col-sm-2 control-label">语言</label>
            <div class="col-sm-10">
                <select class="form-control" id="selectLanguageAdmob">
                </select>
            </div>
        </div>

        <div class="form-group">
            <label for="inputGroupOneAdmob10" class="col-sm-2 control-label">组合</label>
            <div class="col-sm-4">
                <input type="text" value="1" readonly id="inputGroupOneAdmob10" style="width: 10px;">
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessageAdmob11" class="col-sm-2 control-label">广告语1</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessageAdmob11" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessageAdmob12" class="col-sm-2 control-label">广告语2</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessageAdmob12" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessageAdmob13" class="col-sm-2 control-label">广告语3</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessageAdmob13" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessageAdmob14" class="col-sm-2 control-label">广告语4</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessageAdmob14" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputGroupOneAdmob20" class="col-sm-2 control-label">组合</label>
            <div class="col-sm-4">
                <input type="text" value="2" readonly id="inputGroupOneAdmob20" style="width: 20px;">
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessageAdmob21" class="col-sm-2 control-label">广告语1</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessageAdmob21" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessageAdmob22" class="col-sm-2 control-label">广告语2</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessageAdmob22" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessageAdmob23" class="col-sm-2 control-label">广告语3</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessageAdmob23" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessageAdmob24" class="col-sm-2 control-label">广告语4</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessageAdmob24" />
            </div>
        </div>

        <div class="form-group">
            <label for="inputGroupOneAdmob30" class="col-sm-2 control-label">组合</label>
            <div class="col-sm-4">
                <input type="text" value="3" readonly id="inputGroupOneAdmob30" style="width: 30px;">
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessageAdmob31" class="col-sm-2 control-label">广告语1</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessageAdmob31" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessageAdmob32" class="col-sm-2 control-label">广告语2</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessageAdmob32" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessageAdmob33" class="col-sm-2 control-label">广告语3</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessageAdmob33" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessageAdmob34" class="col-sm-2 control-label">广告语4</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessageAdmob34" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputGroupOneAdmob40" class="col-sm-2 control-label">组合</label>
            <div class="col-sm-4">
                <input type="text" value="4" readonly id="inputGroupOneAdmob40" style="width: 40px;">
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessageAdmob41" class="col-sm-2 control-label">广告语1</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessageAdmob41" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessageAdmob42" class="col-sm-2 control-label">广告语2</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessageAdmob42" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessageAdmob43" class="col-sm-2 control-label">广告语3</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessageAdmob43" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessageAdmob44" class="col-sm-2 control-label">广告语4</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessageAdmob44" />
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
<script src="js/layer/layer.js" ></script>
<script>
    var languageList = ["", "Albanian","Amharic","Arabic","Armenian","Azerbaijani","Bengali","Bosnian","Bulgarian","Burmese","Catalan","Chinese","Croatian","Czech","Danish","Dutch","English","Estonian","Finnish","Filipino","French","German","Georgian","Greek","Hungarian", "Hindi","Hebrew","Icelandic","Indonesian","Italian","Japanese","Korean","Kyrgyz","Lao","Latvian","Lithuanian","Luxembourgish","Macedonian","Malagasy","Malay","Mongolian","Nepali","Pashto","Polish","Portuguese","Romanian","Russian","Samoan","Serbian","Sinhala","Slovak","Slovenian", "Somali","Spanish","Swahili","Swedish","Tajik","Thai","Norwegian","Traditional","Turkish","Ukrainian","Urdu","Uzbek","Vietnamese"];

    var appList = [];

    function init() {
        $("li[role='presentation']:eq(9)").addClass("active");
        $('.select2').select2();

        $('.btn-more').click(function() {
            var id = $(this).attr('id');
            var targetId = '';
            if (id == 'btnSelectRegionMore') {
                targetId = '#selectRegion';
            } else if (id == 'btnSelectRegionUnselectedMore') {
                targetId = '#selectRegionUnselected';
            } else if (id == 'btnSelectRegionUnselectedAdmobMore') {
                targetId = '#selectRegionUnselectedAdmob';
            } else if (id == 'btnSelectRegionAdmobMore') {
                targetId = '#selectRegionAdmob';
            }
            $('#moreCountryDlg').modal("show");
            $('#moreCountryDlg .btn-primary').off('click');
            $('#moreCountryDlg .btn-primary').click(function() {
                console.log(id);
                var data = $('#textareaCountry').val();
                var countryList = data.split('\n');
                var countryNames = [];
                countryList.forEach(function(one) {
                    for (var i = 0; i < regionList.length; i++) {
                        one = one.trim();
                        if (regionList[i].name.toLocaleLowerCase() == one.toLocaleLowerCase()) {
                            if (targetId == '#selectRegionUnselectedAdmob' || targetId == '#selectRegionAdmob') {
                                countryNames.push(regionList[i].country_code);
                            } else {
                                countryNames.push(regionList[i].name);
                            }
                            break;
                        }
                    }
                });

                if (targetId != '') {
                    $(targetId).val(countryNames);
                    $(targetId).trigger('change');
                }
                $('#moreCountryDlg').modal("hide");
            });
        });



        languageList.forEach(function (one) {
            $('#selectLanguage').append($("<option>" + one + "</option>"));
            $('#selectLanguageAdmob').append($("<option>" + one + "</option>"));
        });

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

        $.post('adaccount_admob/query',{word:''}, function(data) {
            if (data && data.ret == 1) {
                var accountList = data.data;
                accountList.forEach(function(one) {
                    $('#selectAccountAdmob').append($("<option value='" + one.account_id + "'>" + one.short_name + "</option>"));
                })
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        }, 'json');

        $.post('adaccount/query',{word:''}, function(data) {
            if (data && data.ret == 1) {
                var accountList = data.data;
                accountList.forEach(function(one) {
                    $('#selectAccount').append($("<option value='" + one.account_id + "'>" + one.short_name + "</option>"));
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
        var language = $('#selectLanguageAdmob').val();
        var message11 = $('#inputMessageAdmob11').val();
        var message12 = $('#inputMessageAdmob12').val();
        var message13 = $('#inputMessageAdmob13').val();
        var message14 = $('#inputMessageAdmob14').val();
        var message21 = $('#inputMessageAdmob21').val();
        var message22 = $('#inputMessageAdmob22').val();
        var message23 = $('#inputMessageAdmob23').val();
        var message24 = $('#inputMessageAdmob24').val();
        var message31 = $('#inputMessageAdmob31').val();
        var message32 = $('#inputMessageAdmob32').val();
        var message33 = $('#inputMessageAdmob33').val();
        var message34 = $('#inputMessageAdmob34').val();
        var message41 = $('#inputMessageAdmob41').val();
        var message42 = $('#inputMessageAdmob42').val();
        var message43 = $('#inputMessageAdmob43').val();
        var message44 = $('#inputMessageAdmob44').val();

        $.post("advert_admob/save_advert_admob", {
            appName: appName,
            language: language,
            message11: message11,
            message12: message12,
            message13: message13,
            message14: message14,
            message21: message21,
            message22: message22,
            message23: message23,
            message24: message24,
            message31: message31,
            message32: message32,
            message33: message33,
            message34: message34,
            message41: message41,
            message42: message42,
            message43: message43,
            message44: message44
        }, function (data) {
            if (data && data.ret == 1) {
                if(data.existData == "true"){
                    layer.tips("更新记录成功","#btnInsertAdmob",{tips:1,time:3000});
                }else{
                    layer.tips("添加记录成功","#btnInsertAdmob",{tips:1,time:3000});
                }
            } else {
                admanager.showCommonDlg("提示", data.message);
            }
        }, "json");
        return false;
    });


    $('#btnInsert').click(function () {
        var appName = $('#selectApp').val();
        var language = $('#selectLanguage').val();
        var title11 = $('#inputTitle11').val();
        var message11 = $('#inputMessage11').val();
        var title22 = $('#inputTitle22').val();
        var message22 = $('#inputMessage22').val();
        var title33 = $('#inputTitle33').val();
        var message33 = $('#inputMessage33').val();
        var title44 = $('#inputTitle44').val();
        var message44 = $('#inputMessage44').val();
        $.post("advert/save_advert_facebook", {
            appName: appName,
            language: language,
            title11: title11,
            message11: message11,
            title22: title22,
            message22: message22,
            title33: title33,
            message33: message33,
            title44: title44,
            message44: message44
        }, function (data) {
            if (data && data.ret == 1) {
                if(data.existData == "true"){
                    layer.tips("更新记录成功","#btnInsert",{tips:1,time:3000});
                }else{
                    layer.tips("添加记录成功","#btnInsert",{tips:1,time:3000});
                }
            } else {
                admanager.showCommonDlg("提示", data.message);
            }
        }, "json");
        return false;
    });

    $('#selectApp,#selectLanguage').change(function () {
        $('#inputTitle11').val("");
        $('#inputMessage11').val("");
        $('#inputTitle22').val("");
        $('#inputMessage22').val("");
        $('#inputTitle33').val("");
        $('#inputMessage33').val("");
        $('#inputTitle44').val("");
        $('#inputMessage44').val("");
        var appName = $("#selectApp").val();
        var language = $("#selectLanguage").val();
        if(language != null &&  language.length > 0){
            $.post("advert/query_before_insertion", {
                appName: appName,
                language: language
            }, function (data) {
                if (data && data.ret == 1) {
                    var arr = data.array;
                    for(var i =0;i< arr.length;i++){
                        var one = arr[i];
                        if(one['group_id'] == 1){
                            $('#inputTitle11').val(one['title']);
                            $('#inputMessage11').val(one['message']);
                        }else if(one['group_id'] == 2){
                            $('#inputTitle22').val(one['title']);
                            $('#inputMessage22').val(one['message']);
                        }else if(one['group_id'] == 3){
                            $('#inputTitle33').val(one['title']);
                            $('#inputMessage33').val(one['message']);
                        }else if(one['group_id'] == 4){
                            $('#inputTitle44').val(one['title']);
                            $('#inputMessage44').val(one['message']);
                        }

                    }
                } else {
                    $('#inputTitle11').val("");
                    $('#inputMessage11').val("");
                    $('#inputTitle22').val("");
                    $('#inputMessage22').val("");
                    $('#inputTitle33').val("");
                    $('#inputMessage33').val("");
                    $('#inputTitle44').val("");
                    $('#inputMessage44').val("");
                }
            }, "json");
        }else{
            $('#inputTitle11').val("");
            $('#inputMessage11').val("");
            $('#inputTitle22').val("");
            $('#inputMessage22').val("");
            $('#inputTitle33').val("");
            $('#inputMessage33').val("");
            $('#inputTitle44').val("");
            $('#inputMessage44').val("");
        }
        return false;
    });

    $('#selectAppAdmob,#selectLanguageAdmob').change(function () {
        $('#inputMessageAdmob11').val("");
        $('#inputMessageAdmob12').val("");
        $('#inputMessageAdmob13').val("");
        $('#inputMessageAdmob14').val("");
        $('#inputMessageAdmob21').val("");
        $('#inputMessageAdmob22').val("");
        $('#inputMessageAdmob23').val("");
        $('#inputMessageAdmob24').val("");
        $('#inputMessageAdmob31').val("");
        $('#inputMessageAdmob32').val("");
        $('#inputMessageAdmob33').val("");
        $('#inputMessageAdmob34').val("");
        $('#inputMessageAdmob41').val("");
        $('#inputMessageAdmob42').val("");
        $('#inputMessageAdmob43').val("");
        $('#inputMessageAdmob44').val("");
        var appNameAdmob = $("#selectAppAdmob").val();
        var languageAdmob = $("#selectLanguageAdmob").val();
        if(languageAdmob != null &&  languageAdmob.length > 0){
            $.post("advert_admob/query_before_admob_insert", {
                appNameAdmob: appNameAdmob,
                languageAdmob: languageAdmob
            }, function (data) {
                if (data && data.ret == 1) {
                    var arr = data.array;
                    for(var i =0;i< arr.length;i++){
                        var one = arr[i];
                        if(one['group_id'] == 1){
                            $('#inputMessageAdmob11').val(one['message1']);
                            $('#inputMessageAdmob12').val(one['message2']);
                            $('#inputMessageAdmob13').val(one['message3']);
                            $('#inputMessageAdmob14').val(one['message4']);
                        } else if(one['group_id'] == 2){
                            $('#inputMessageAdmob21').val(one['message1']);
                            $('#inputMessageAdmob22').val(one['message2']);
                            $('#inputMessageAdmob23').val(one['message3']);
                            $('#inputMessageAdmob24').val(one['message4']);
                        }else if(one['group_id'] == 3){
                            $('#inputMessageAdmob31').val(one['message1']);
                            $('#inputMessageAdmob32').val(one['message2']);
                            $('#inputMessageAdmob33').val(one['message3']);
                            $('#inputMessageAdmob34').val(one['message4']);
                        }else if(one['group_id'] == 4){
                            $('#inputMessageAdmob41').val(one['message1']);
                            $('#inputMessageAdmob42').val(one['message2']);
                            $('#inputMessageAdmob43').val(one['message3']);
                            $('#inputMessageAdmob44').val(one['message4']);
                        }
                    }

                } else {
                    $('#inputMessageAdmob11').val("");
                    $('#inputMessageAdmob12').val("");
                    $('#inputMessageAdmob13').val("");
                    $('#inputMessageAdmob14').val("");
                    $('#inputMessageAdmob21').val("");
                    $('#inputMessageAdmob22').val("");
                    $('#inputMessageAdmob23').val("");
                    $('#inputMessageAdmob24').val("");
                    $('#inputMessageAdmob31').val("");
                    $('#inputMessageAdmob32').val("");
                    $('#inputMessageAdmob33').val("");
                    $('#inputMessageAdmob34').val("");
                    $('#inputMessageAdmob41').val("");
                    $('#inputMessageAdmob42').val("");
                    $('#inputMessageAdmob43').val("");
                    $('#inputMessageAdmob44').val("");
                }
            }, "json");
        }else{
            $('#inputMessageAdmob11').val("");
            $('#inputMessageAdmob12').val("");
            $('#inputMessageAdmob13').val("");
            $('#inputMessageAdmob14').val("");
            $('#inputMessageAdmob21').val("");
            $('#inputMessageAdmob22').val("");
            $('#inputMessageAdmob23').val("");
            $('#inputMessageAdmob24').val("");
            $('#inputMessageAdmob31').val("");
            $('#inputMessageAdmob32').val("");
            $('#inputMessageAdmob33').val("");
            $('#inputMessageAdmob34').val("");
            $('#inputMessageAdmob41').val("");
            $('#inputMessageAdmob42').val("");
            $('#inputMessageAdmob43').val("");
            $('#inputMessageAdmob44').val("");
        }
        return false;
    });
</script>
</body>
</html>