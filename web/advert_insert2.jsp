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

    <form class="form-horizontal" action="#" id="formFacebook">
        <div class="form-group">
            <label for="selectApp" class="col-sm-2 control-label">应用</label>
            <div class="col-sm-3">
                <select class="form-control" id="selectApp">
                </select>
            </div>
            <select id="selectAdvertGroupId">
            </select>
        </div><br>

        <div class="form-group">
            <label for="inputTitle" class="col-sm-2 control-label">英语<br>广告标题</label>
            <div class="col-sm-7">
                <input class="form-control" id="inputTitle" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessage" class="col-sm-2 control-label">英语<br>广告语</label>
            <div class="col-sm-7">
                <input class="form-control" id="inputMessage" />
            </div>
        </div>

        <div class="form-group">
            <div class="col-sm-7" style="text-align:center">
                <input type="submit" class="btn btn-primary" id="btnInsert_English" value="保存原文"/>
            </div>
        </div>

        <div class="form-group">
            <label for="selectLanguage" class="col-sm-2 control-label">语言</label>
            <div class="col-sm-3">
                <select class="form-control" id="selectLanguage"></select>
            </div>
            <input type="button" class="btn btn-primary" id="inputTranslate" value="翻译"/>
        </div><br>

        <div class="form-group">
            <label for="inputTitle11" class="col-sm-2 control-label">广告标题</label>
            <div class="col-sm-7">
                <input class="form-control" id="inputTitle11" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessage11" class="col-sm-2 control-label">广告语</label>
            <div class="col-sm-7">
                <input class="form-control" id="inputMessage11" />
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-7" style="text-align: center">
                <input type="submit" class="btn btn-primary" id="btnInsert_Translation" value="保存译文"/>
            </div>
        </div>
    </form>


    <form class="form-horizontal" action="#" id="formAdmob">

        <div class="form-group">
            <label for="selectAppAdmob" class="col-sm-2 control-label">应用</label>
            <div class="col-sm-3">
                <select class="form-control" id="selectAppAdmob">
                </select>
            </div>
            <label>广告语组合：</label>
            <select id="selectAdvertGroupIdAdmob">
            </select>
        </div><br>

        <div class="form-group">
            <label for="inputMessageAdmob1" class="col-sm-2 control-label">英语广告语1</label>
            <div class="col-sm-7">
                <input class="form-control" id="inputMessageAdmob1" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessageAdmob2" class="col-sm-2 control-label">英语广告语2</label>
            <div class="col-sm-7">
                <input class="form-control" id="inputMessageAdmob2" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessageAdmob3" class="col-sm-2 control-label">英语广告语3</label>
            <div class="col-sm-7">
                <input class="form-control" id="inputMessageAdmob3" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessageAdmob4" class="col-sm-2 control-label">英语广告语4</label>
            <div class="col-sm-7">
                <input class="form-control" id="inputMessageAdmob4" />
            </div>
        </div>

        <div class="form-group">
            <div class="col-sm-7" style="text-align: center">
                <input type="submit" class="btn btn-primary" id="btnInsertAdmob_English" value="保存原文"/>
            </div>
        </div>

        <div class="form-group">
            <label for="selectLanguage" class="col-sm-2 control-label">语言</label>
            <div class="col-sm-3">
                <select class="form-control" id="selectLanguageAdmob"></select>
            </div>
            <input type="button" class="btn btn-primary" id="inputTranslateAdmob" value="翻译"/>
        </div><br>

        <div class="form-group">
            <label for="inputMessageAdmob11" class="col-sm-2 control-label">广告语1</label>
            <div class="col-sm-7">
                <input class="form-control" id="inputMessageAdmob11" />
            </div>
        </div>

        <div class="form-group">
            <label for="inputMessageAdmob12" class="col-sm-2 control-label">广告语2</label>
            <div class="col-sm-7">
                <input class="form-control" id="inputMessageAdmob12" />
            </div>
        </div>

        <div class="form-group">
            <label for="inputMessageAdmob13" class="col-sm-2 control-label">广告语3</label>
            <div class="col-sm-7">
                <input class="form-control" id="inputMessageAdmob13" />
            </div>
        </div>

        <div class="form-group">
            <label for="inputMessageAdmob14" class="col-sm-2 control-label">广告语4</label>
            <div class="col-sm-7">
                <input class="form-control" id="inputMessageAdmob14" />
            </div>
        </div>

        <div class="form-group">
            <div class="col-sm-7" style="text-align: center">
                <input type="submit" class="btn btn-primary" id="btnInsertAdmob_Translation" value="保存译文"/>
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
<script type="text/javascript" src="js\language-name-code-dict.js"></script>
<script>

    var languageList = ["", "Albanian","Amharic","Arabic","Armenian","Azeerbaijani","Bengali","Bosnian","Bulgarian",
        "Burmese","Catalan","Chinese","Croatian","Czech","Danish","Dutch","English","Estonian","Finnish","Filipino",
        "French","German","Georgian","Greek","Hungarian", "Hindi","Hebrew","Icelandic","Indonesian","Italian","Japanese",
        "Korean","Kyrgyz","Lao","Latvian","Lithuanian","Luxembourgish","Macedonian","Malagasy","Malay","Mongolian",
        "Nepali","Pashto","Polish","Portuguese","Romanian","Russian","Samoan","Serbian","Sinhala","Slovak","Slovenian",
        "Somali","Spanish","Swahili","Swedish","Tajik","Thai","Norwegian","Traditional","Turkish",
        "Ukrainian","Urdu","Uzbek","Vietnamese"];

    //界面加载好以后初始化的数据
    function init() {
        $("li[role='presentation']:eq(6)").addClass("active");
        $('.select2').select2();

        for(var i = 1; i<=50;i++){
            $("#selectAdvertGroupId").append("<option value='"+i+"'>"+i+"</option>");
            $("#selectAdvertGroupIdAdmob").append("<option value='"+i+"'>"+i+"</option>");
        }

        //这个类btn-more在本jsp页里已经找不到了
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

        //连着三个post用于往【应用】下拉列表里动态添加选项
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

        //以下连着两个用于判定是否隐藏表单
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

    //翻译接口(应用了回调函数)
    function translate(googleCodeTo,googleCodeFrom,text,value){
        $.post('https://translation.googleapis.com/language/translate/v2?key=AIzaSyBALihNeToXheg4Iw2E7C8FafHpfAwNdWE', {
            target: googleCodeTo,
            format: "text",
            source: googleCodeFrom,
            q: text
        }, function(data) {
            var translation =  data["data"]["translations"][0]["translatedText"];
            value(translation);  //value是一个函数式参数，translation是要传入供 value()使用的参数
        }, 'json');
    }

    //facebook form【翻译】按钮的事件绑定
    $('#inputTranslate').click(function () {
        var title = $("#inputTitle").val();
        var message = $("#inputMessage").val();
        var language = $("#selectLanguage").val();
        var googleCodeFrom = "en";
        var googleCodeTo = ""; //查语言的code
        function findCode(item){
            var languageKey = item["language"];
            if(languageKey == language){
                googleCodeTo = item["code"];
                return;
            }else{
                return;
            }
        }
        LanguageListNameCode.forEach(findCode);
        translate(googleCodeTo,googleCodeFrom,title,function(text){
            $("#inputTitle11").val(text);   //这一段匿名函数方法即回调函数本身
        });
        translate(googleCodeTo,googleCodeFrom,message,function(text){
            $("#inputMessage11").val(text);
        });
    });

    //Admob form【翻译】按钮的事件绑定
    $('#inputTranslateAdmob').click(function () {
        var message1 = $("#inputMessageAdmob1").val();
        var message2 = $("#inputMessageAdmob2").val();
        var message3 = $("#inputMessageAdmob3").val();
        var message4 = $("#inputMessageAdmob4").val();
        var message = [message1,message2,message3,message4];
        var language = $("#selectLanguageAdmob").val();
        var googleCodeFrom = "en";
        var googleCodeTo = ""; //查语言的code
        function findCode(item){
            var languageKey = item["language"];
            if(languageKey == language){
                googleCodeTo = item["code"];
                return;
            }else{
                return;
            }
        }
        LanguageListNameCode.forEach(findCode);
        for(var j=1;j<=4;j++){
            (function(j){
                var theMessage = message[j-1];
                translate(googleCodeTo,googleCodeFrom,theMessage,function(text){
                    $("#inputMessageAdmob1"+j).val(text);
                });
            })(j);
        }
    });

    /* admob表单的【保存原文】按钮
     */
    $("#btnInsertAdmob_English").click(function(){
        var appName = $('#selectAppAdmob').val();
        var language = $('#selectLanguageAdmob').val();
        var groupNumber = $("#selectAdvertGroupIdAdmob").val();
        var message1 = $("#inputMessageAdmob1").val();
        var message2 = $("#inputMessageAdmob2").val();
        var message3 = $("#inputMessageAdmob3").val();
        var message4 = $("#inputMessageAdmob4").val();
        var version = "English";
        $.post("advert_admob2/save_advert_admob",{
            appName: appName,
            language: language,
            groupNumber:groupNumber,
            message1: message1,
            message2: message2,
            message3: message3,
            message4: message4,
            version:version
        },function(data){
            if (data && data.ret == 1) {
                if(data.existData == "true"){
                    layer.tips("更新记录成功","#btnInsertAdmob_English",{tips:1,time:3000});
                }else{
                    layer.tips("添加记录成功","#btnInsertAdmob_English",{tips:1,time:3000});
                }
            } else {
                admanager.showCommonDlg("提示", data.message);
            }
        },"json")
        return false;  //为什么要返回false?
    });

    /* admob表单的【保存译文】按钮
     */
    $("#btnInsertAdmob_Translation").click(function(){
        var appName = $('#selectAppAdmob').val();
        var language = $('#selectLanguageAdmob').val();
        var groupNumber = $("#selectAdvertGroupIdAdmob").val();
        var message11 = $("#inputMessageAdmob11").val();
        var message12 = $("#inputMessageAdmob12").val();
        var message13 = $("#inputMessageAdmob13").val();
        var message14 = $("#inputMessageAdmob14").val();
        var version = "Translation";
        $.post("advert_admob2/save_advert_admob",{
            appName: appName,
            language: language,
            groupNumber:groupNumber,
            message1: message11,
            message2: message12,
            message3: message13,
            message4: message14,
            version:version
        },function(data){
            if (data && data.ret == 1) {
                if(data.existData == "true"){
                    layer.tips("更新记录成功","#btnInsertAdmob_Translation",{tips:1,time:3000});
                }else{
                    layer.tips("添加记录成功","#btnInsertAdmob_Translation",{tips:1,time:3000});
                }
            } else {
                admanager.showCommonDlg("提示", data.message);
            }
        },"json")
    });

    //facebook表单的【保存译文】按钮  记得改id为btnInsert_English
    $('#btnInsert_Translation').click(function () {
        var appName = $('#selectApp').val();
        var language = $('#selectLanguage').val();
        var groupNumber = $("#selectAdvertGroupId").val();
        var title11 = $('#inputTitle11').val();
        var message11 = $('#inputMessage11').val();
        var version = "Translation";
        $.post("advert2/save_advert_facebook", {
            appName: appName,
            language: language,
            groupNumber:groupNumber,
            title: title11,
            message: message11,
            version:version
        }, function (data) {
            if (data && data.ret == 1) {
                if(data.existData == "true"){
                    layer.tips("更新记录成功","##btnInsert_Translation",{tips:1,time:3000});
                }else{
                    layer.tips("添加记录成功","##btnInsert_Translation",{tips:1,time:3000});
                }
            } else {
                admanager.showCommonDlg("提示", data.message);
            }
        }, "json");
        return false;
    });

    //facebook表单的【保存原文】按钮
    $('#btnInsert_English').click(function () {
        var appName = $('#selectApp').val();
        var language = $('#selectLanguage').val();
        var groupNumber = $("#selectAdvertGroupId").val();
        var title = $('#inputTitle').val();
        var message = $('#inputMessage').val();
        var version = "English";
        $.post("advert2/save_advert_facebook", {
            appName: appName,
            language: language,
            groupNumber:groupNumber,
            title: title,
            message: message,
            version:version
        }, function (data) {
            if (data && data.ret == 1) {
                if(data.existData == "true"){
                    layer.tips("更新记录成功","#btnInsert_English",{tips:1,time:3000});
                }else{
                    layer.tips("添加记录成功","#btnInsert_English",{tips:1,time:3000});
                }
            } else {
                admanager.showCommonDlg("提示", data.message);
            }
        }, "json");
        return false;
    });

    //facebook 表单 当【应用】【语言】【广告语组合】三项的内容改变时
    $('#selectApp,#selectLanguage,#selectAdvertGroupId').change(function () {
        $('#inputTitle11').val("");
        $('#inputMessage11').val("");
        var appName = $("#selectApp").val();
        var language = $("#selectLanguage").val();
        var groupNumber = $("#selectAdvertGroupId").val();
        $.post("advert2/query_before_insertion", {
            appName: appName,
            language: language,
            groupNumber:groupNumber
        }, function (data) {
            if (data && data.ret == 1) {
                //只要后台有任何数据返回
                $('#inputTitle').val(data.title);
                $('#inputMessage').val(data.message);
                $('#inputTitle11').val(data.title_translation);
                $('#inputMessage11').val(data.message_translation);
            }else{
                $('#inputTitle').val("");
                $('#inputMessage').val("");
                $('#inputTitle11').val("");
                $('#inputMessage11').val("");
            }
        }, "json");
        return false;
    });

    // admob 表单，功能同上
    $('#selectAppAdmob,#selectLanguageAdmob,#selectAdvertGroupIdAdmob').change(function () {
        $('#inputMessageAdmob11').val("");
        $('#inputMessageAdmob12').val("");
        $('#inputMessageAdmob13').val("");
        $('#inputMessageAdmob14').val("");
        var appNameAdmob = $("#selectAppAdmob").val();
        var languageAdmob = $("#selectLanguageAdmob").val();
        var groupNumberAdmob = $("#selectAdvertGroupIdAdmob").val();
        //无论如何都发送请求
        $.post("advert_admob2/query_before_admob_insert", {
            appNameAdmob: appNameAdmob,
            languageAdmob: languageAdmob,
            groupNumberAdmob: groupNumberAdmob
        }, function (data) {
            if (data && data.ret == 1) {
                //只要后台有任何数据返回
                $('#inputMessageAdmob1').val(data.message1_en);
                $('#inputMessageAdmob2').val(data.message2_en);
                $('#inputMessageAdmob3').val(data.message3_en);
                $('#inputMessageAdmob4').val(data.message4_en);
                $('#inputMessageAdmob11').val(data.message1);
                $('#inputMessageAdmob12').val(data.message2);
                $('#inputMessageAdmob13').val(data.message3);
                $('#inputMessageAdmob14').val(data.message4);
            } else {
                //没有数据返回
                $('#inputMessageAdmob1').val("");
                $('#inputMessageAdmob2').val("");
                $('#inputMessageAdmob3').val("");
                $('#inputMessageAdmob4').val("");
                $('#inputMessageAdmob11').val("");
                $('#inputMessageAdmob12').val("");
                $('#inputMessageAdmob13').val("");
                $('#inputMessageAdmob14').val("");
            }
        }, "json");
        return false;
    });
</script>
</body>
</html>