<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/rootBase.jsp" %>

<html>
<head>
    <title>广告存储</title>
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
            <label>广告语组合：</label>
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

        <input type="button" class="btn btn-primary" id="inputTranslate" value="翻译&保存"/>
        <input type="button" class="btn btn-primary" id="saveEdition" value="保存修改">
        <br>
        <div>
        <table class="table table-striped" id="advertisement">
            <thead>
            <tr>
                <%--<th><input type="checkbox" id="checkbox_facebook"></th>--%>
                <th>广告语组合</th><th>语言</th><th>广告语标题</th><th>广告语</th></tr>
            </thead>
            <tbody id="tbody_facebook"></tbody>
        </table>
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
        <input type="button" class="btn btn-primary" id="inputTranslateAdmob" value="翻译&保存"/>
        <input type="button" class="btn btn-primary" id="saveEditionAdmob" value="保存修改">
        <br>
        <div>
        <table class="table table-striped" id="advertisement_adwords">
            <thead>
            <tr><th>广告语组合</th><th>语言</th><th>广告语1</th><th>广告语2</th><th>广告语3</th><th>广告语4</th></tr>
            </thead>
            <tbody id="tbody_adwords"></tbody>
        </table>
        </div>
    </form>

</div>

</div>

<jsp:include page="common/loading_dialog.jsp"></jsp:include>

<script>

    var languageList = ["", "Albanian","Amharic","Arabic","Armenian","Azeerbaijani","Bengali","Bosnian","Bulgarian",
        "Burmese","Catalan","Chinese","Croatian","Czech","Danish","Dutch","English","Estonian","Finnish","Filipino",
        "French","German","Georgian","Greek","Hungarian", "Hindi","Hebrew","Icelandic","Indonesian","Italian","Japanese",
        "Korean","Kyrgyz","Lao","Latvian","Lithuanian","Luxembourgish","Macedonian","Malagasy","Malay","Mongolian",
        "Nepali","Pashto","Polish","Portuguese","Romanian","Russian","Samoan","Serbian","Sinhala","Slovak","Slovenian",
        "Somali","Spanish","Swahili","Swedish","Tajik","Thai","Norwegian","Traditional","Turkish",
        "Ukrainian","Urdu","Uzbek","Vietnamese"];

    var LanguageNameCode=[{"language":"Albanian","code":"sq"},{"language":"Amharic","code":"am"},{"language":"Arabic","code":"ar"},
        {"language":"Armenian","code":"hy"},{"language":"Azeerbaijani","code":"az"},{"language":"Bengali","code":"bn"},
        {"language":"Bosnian","code":"bs"},{"language":"Bulgarian","code":"bg"},{"language":"Burmese","code":"my"},
        {"language":"Catalan","code":"ca"},{"language":"Chinese","code":"zh-CN"},{"language":"Croatian","code":"hr"},
        {"language":"Czech","code":"cs"},{"language":"Danish","code":"da"},{"language":"Dutch","code":"nl"},
        {"language":"French","code":"fr"},{"language":"Finnish","code":"fi"},
        {"language":"Filipino","code":"tl"},{"language":"Estonian","code":"et"},
        {"language":"German","code":"de"}, {"language":"Georgian","code":"ka"}, {"language":"Greek","code":"el"},
        {"language":"Hungarian","code":"hu"}, {"language":"Hindi","code":"hi"}, {"language":"Hebrew","code":"iw"},
        {"language":"Icelandic","code":"is"}, {"language":"Indonesian","code":"id"}, {"language":"Italian","code":"it"},
        {"language":"Japanese","code":"ja"}, {"language":"Korean","code":"ko"}, {"language":"Kyrgyz","code":"ky"},
        {"language":"Lao","code":"lo"}, {"language":"Latvian","code":"lv"}, {"language":"Lithuanian","code":"lt"},
        {"language":"Luxembourgish","code":"lb"}, {"language":"Macedonian","code":"mk"}, {"language":"Malagasy","code":"mg"},
        {"language":"Malay","code":"ms"}, {"language":"Mongolian","code":"mn"}, {"language":"Nepali","code":"ne"},
        {"language":"Pashto","code":"ps"}, {"language":"Polish","code":"pl"}, {"language":"Portuguese","code":"pt"},
        {"language":"Romanian","code":"ro"}, {"language":"Russian","code":"ru"}, {"language":"Samoan","code":"sm"},
        {"language":"Serbian","code":"sr"}, {"language":"Sinhala","code":"si"}, {"language":"Slovak","code":"sk"},
        {"language":"Slovenian","code":"sl"}, {"language":"Somali","code":"so"}, {"language":"Spanish","code":"es"},
        {"language":"Swahili","code":"sw"}, {"language":"Swedish","code":"sv"}, {"language":"Tajik","code":"tg"},
        {"language":"Thai","code":"th"}, {"language":"Norwegian","code":"no"}, {"language":"Traditional","code":"zh-TW"},
        {"language":"Turkish","code":"tr"}, {"language":"Ukrainian","code":"uk"}, {"language":"Urdu","code":"ur"},
        {"language":"Uzbek","code":"uz"}, {"language":"Vietnamese","code":"vi"}];
    //界面加载好以后初始化的数据
    function init() {
        $("li[role='presentation']:eq(6)").addClass("active");
        $('.select2').select2();
        for(var i = 1; i<=50;i++){
            $("#selectAdvertGroupId").append("<option value='"+i+"'>"+i+"</option>");
            $("#selectAdvertGroupIdAdmob").append("<option value='"+i+"'>"+i+"</option>");
        }

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
        $("#saveEdition").hide();
        $("#saveEditionAdmob").hide();
    }
    init();

    //翻译接口
    function translate(text,googleCodeTo,resolve,reject){
        $.post('https://translation.googleapis.com/language/translate/v2?key=AIzaSyBALihNeToXheg4Iw2E7C8FafHpfAwNdWE', {
            target: googleCodeTo,
            format: "text",
            source: "en",
            q: text
        }, function(data) {
            if(data){
                var translation =  data["data"]["translations"][0]["translatedText"];
                resolve(translation);
            }else{
                reject(new Error("Boom!"+ googleCodeTo+ " translation failed!"));
            }
        }, 'json');
    }

    //facebook form【翻译&保存】
    $('#inputTranslate').click(function () {
        $("#inputTranslate").prop("disabled",true);
        $("#tbody_facebook").empty();
        var appName = $("#selectApp").val();
        var title = $("#inputTitle").val();
        var message = $("#inputMessage").val();
        var groupId = $("#selectAdvertGroupId").val();
        var x = 0;
        loopArray(0);
        function loopArray(x) {
            if(x<LanguageNameCode.length){
                var googleCodeTo = LanguageNameCode[x]["code"];
                var language = LanguageNameCode[x]["language"];
                var tr = $("<tr></tr>");
                $("#tbody_facebook").append(tr);
                // tr.append("<td><input type='checkbox' class='facebookAds'></td>");
                tr.append("<td>"+groupId+"</td>");
                tr.append("<td>"+language+"</td>");
                asynPost(googleCodeTo,title,message)
                    .then(function(translation){
                        var ti = translation[0];
                        var msg = translation[1];
                        tr.append("<td>"+ti+"</td>");
                        tr.append("<td>"+msg+"</td>");
                    })
                    .then(function(){
                        x++;
                        loopArray(x);
                    })
                    .catch(function(){
                        var err = new Error("Translation to "+language+"has failed.");
                        console.log(err);
                    });
                function asynPost(googleCodeTo, title, message) {
                    var translatedTo = googleCodeTo;
                    var enTitle = title;
                    var enMessage = message;
                    var p1 = new Promise(function(resolve,reject){
                        translate(enTitle,translatedTo,resolve,reject);
                    });
                    var p2 = new Promise(function(resolve,reject){
                        translate(enMessage,translatedTo,resolve,reject);
                    });

                    var promise = Promise.all([p1,p2]);
                    return promise;
                }
            }else{
                var adsArray = [];
                var tbodyList = $("#tbody_facebook").children("tr");
                tbodyList.each(function(){
                    var trHere = $(this);
                    var ads = {};
                    ads.language = trHere.children("td:eq(1)").text();
                    ads.title = trHere.children("td:eq(2)").text();
                    ads.message = trHere.children("td:eq(3)").text();
                    adsArray.push(ads);
                });
                var adsArrayString = JSON.stringify(adsArray);
                $.post("advert/save_advert_facebook_one_key", {
                    appName: appName,
                    group_id:groupId,
                    ads:adsArrayString
                }, function (data) {
                    $("#inputTranslate").prop("disabled",false);
                    if (data && data.ret == 1) {
                        layer.tips(data.message,"#inputTranslate",{tips:1,time:3000});
                    } else {
                        admanager.showCommonDlg("提示", data.message);
                    }
                }, "json");
            }
        }
    });

    //Admob form【翻译&保存】
    $('#inputTranslateAdmob').click(function () {
        $("#inputTranslateAdmob").prop("disabled",true);
        $("#tbody_adwords").empty();
        var message1 = $("#inputMessageAdmob1").val();
        var message2 = $("#inputMessageAdmob2").val();
        var message3 = $("#inputMessageAdmob3").val();
        var message4 = $("#inputMessageAdmob4").val();
        var appName = $("#selectAppAdmob").val();
        var groupId = $("#selectAdvertGroupIdAdmob").val();
        var x = 0;
        loopArray(0);
        function loopArray(x) {
            if(x<LanguageNameCode.length){
                var googleCodeTo = LanguageNameCode[x]["code"];
                var language = LanguageNameCode[x]["language"];
                var tr = $("<tr></tr>");
                $("#tbody_adwords").append(tr);
                // tr.append("<td><input type='checkbox' class='facebookAds'></td>");
                tr.append("<td>"+groupId+"</td>");
                tr.append("<td>"+language+"</td>");
                asynPost(googleCodeTo,message1,message2,message3,message4)
                    .then(function(translation){
                        if(language == "Chinese"||language == "Japanese"||language == "Korean"||language == "Traditional"){
                            for(var j = 0;j<4;j++){
                                if(translation[j].length>12){
                                    tr.append("<td class='red'>"+translation[j]+"</td>");
                                }else{
                                    tr.append("<td>"+translation[j]+"</td>");
                                }
                            }
                        }else{
                            for(var j = 0;j<4;j++){
                                if(translation[j].length>25){
                                    tr.append("<td class='red'>"+translation[j]+"</td>");
                                }else{
                                    tr.append("<td>"+translation[j]+"</td>");
                                }
                            }
                        }
                    })
                    .then(function(){
                        x++;
                        loopArray(x);
                    })
                    .catch(function(){
                        var err = new Error("Translation to "+language+"has failed.");
                        console.log(err);
                    });
                function asynPost(googleCodeTo,message1,message2,message3,message4) {
                    var translatedTo = googleCodeTo;

                    var p1 = new Promise(function(resolve,reject){
                        translate(message1,translatedTo,resolve,reject);
                    });
                    var p2 = new Promise(function(resolve,reject){
                        translate(message2,translatedTo,resolve,reject);
                    });
                    var p3 = new Promise(function(resolve,reject){
                        translate(message3,translatedTo,resolve,reject);
                    });
                    var p4 = new Promise(function(resolve,reject){
                        translate(message4,translatedTo,resolve,reject);
                    });

                    var promise = Promise.all([p1,p2,p3,p4]);
                    return promise;
                }
            }else{
                var adsArray = [];
                var tbodyList = $("#tbody_adwords").children("tr");
                tbodyList.each(function(){
                    var trHere = $(this);
                    var ads = {};
                    ads.language = trHere.children("td:eq(1)").text();
                    ads.message1 = trHere.children("td:eq(2)").text();
                    ads.message2 = trHere.children("td:eq(3)").text();
                    ads.message3 = trHere.children("td:eq(4)").text();
                    ads.message4 = trHere.children("td:eq(5)").text();
                    adsArray.push(ads);
                });
                var adsArrayString = JSON.stringify(adsArray);
                $.post("advert_admob/save_advert_admob_one_key", {
                    appName: appName,
                    group_id:groupId,
                    ads:adsArrayString
                }, function (data) {
                    $("#inputTranslateAdmob").prop("disabled",false);
                    if (data && data.ret == 1) {
                        layer.tips(data.message,"#inputTranslateAdmob",{tips:1,time:3000});
                    } else {
                        admanager.showCommonDlg("提示", data.message);
                    }
                }, "json");
            }
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
        $.post("advert_admob/save_advert_admob",{
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
        },"json");
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
        $.post("advert/save_advert_facebook", {
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

    //facebook 表单 当【应用】【广告语组合】的内容改变时
    $('#selectApp,#selectAdvertGroupId').change(function () {
        var appName = $("#selectApp").val();
        var groupNumber = $("#selectAdvertGroupId").val();
        $.post("advert/query_before_insertion_one_key", {
            appName: appName,
            groupNumber:groupNumber
        }, function (data) {
            if (data && data.ret == 1) {
                //只要后台有任何数据返回
                $('#inputTitle').val(data.title);
                $('#inputMessage').val(data.message);
            }else{
                $('#inputTitle').val("");
                $('#inputMessage').val("");
            }
        }, "json");
        return false;
    });

    // admob 表单，功能同上
    $('#selectAppAdmob,#selectAdvertGroupIdAdmob').change(function () {
        var appNameAdmob = $("#selectAppAdmob").val();
        var groupNumberAdmob = $("#selectAdvertGroupIdAdmob").val();
        //无论如何都发送请求
        $.post("advert_admob/query_before_admob_insert_one_key", {
            appNameAdmob: appNameAdmob,
            groupNumberAdmob: groupNumberAdmob
        }, function (data) {
            if (data && data.ret == 1) {
                //只要后台有任何数据返回
                $('#inputMessageAdmob1').val(data.message1);
                $('#inputMessageAdmob2').val(data.message2);
                $('#inputMessageAdmob3').val(data.message3);
                $('#inputMessageAdmob4').val(data.message4);
            } else {
                $('#inputMessageAdmob1').val("");
                $('#inputMessageAdmob2').val("");
                $('#inputMessageAdmob3').val("");
                $('#inputMessageAdmob4').val("");
            }
        }, "json");
        return false;
    });

    //Facebook form 修改
    $("#tbody_facebook").on("click","td",function(){
        var theTd = $(this);
        var elementCheck = theTd.children("input").attr("class");
        if(elementCheck == "edition"){
            return false;
        }
        var check = $("#tbody_facebook").children("tr").toArray().length;
        var idx = theTd.index();
        if(check>0 && idx>1){
            $("#saveEdition").show();
            var content = theTd.text();
            theTd.empty().append("<input class='edition'style='width:100%'>");
            theTd.children("input").val(content);
        }
    });
    $("#saveEdition").click(function(){
        $("#inputTranslate").prop("disabled",true);
        $("#saveEdition").prop("disabled",true);
        var inputArray = $("#tbody_facebook input");
        inputArray.each(function(){
            var content = $(this).val();
            var theTd = $(this).parent();
            theTd.empty().text(content);
        });
        var adsArray = [];
        var tbodyList = $("#tbody_facebook").children("tr");
        tbodyList.each(function(){
            var trHere = $(this);
            var ads = {};
            ads.language = trHere.children("td:eq(1)").text();
            ads.title = trHere.children("td:eq(2)").text();
            ads.message = trHere.children("td:eq(3)").text();
            adsArray.push(ads);
        });
        var adsArrayString = JSON.stringify(adsArray);
        var appName = $("#selectApp").val();
        var groupId = $("#selectAdvertGroupId").val();
        $.post("advert/save_advert_facebook_one_key", {
            appName: appName,
            group_id:groupId,
            ads:adsArrayString
        }, function (data) {
            $("#inputTranslate").prop("disabled",false);
            $("#saveEdition").prop("disabled",false);
            if (data && data.ret == 1) {
                layer.tips(data.message,"#saveEdition",{tips:1,time:3000});
            } else {
                admanager.showCommonDlg("提示", data.message);
            }
        }, "json");
    });

    //Adwords form 修改
    $("#tbody_adwords").on("click","td",function(){
        var theTd = $(this);
        var elementCheck = theTd.children("input").attr("class");
        if(elementCheck == "edition_admob"){
            return false;
        }
        var check = $("#tbody_adwords").children("tr").toArray().length;
        var idx = theTd.index();
        if(check>0 && idx>1){
            $("#saveEditionAdmob").show();
            var content = theTd.text();
            theTd.empty().append("<input class='edition_admob'style='width:100%'>");
            theTd.children("input").val(content);
        }
    });
    $("#saveEditionAdmob").click(function(){
        $("#inputTranslateAdmob").prop("disabled",true);
        $("#saveEditionAdmob").prop("disabled",true);
        var inputArray = $("#tbody_adwords input");
        inputArray.each(function(){
            var content = $(this).val();
            var theTd = $(this).parent();
            theTd.empty().text(content);
        });
        var adsArray = [];
        var tbodyList = $("#tbody_adwords").children("tr");
        tbodyList.each(function(){
            var trHere = $(this);
            var ads = {};
            ads.language = trHere.children("td:eq(1)").text();
            ads.message1 = trHere.children("td:eq(2)").text();
            ads.message2 = trHere.children("td:eq(3)").text();
            ads.message3 = trHere.children("td:eq(4)").text();
            ads.message4 = trHere.children("td:eq(5)").text();
            adsArray.push(ads);
        });
        var adsArrayString = JSON.stringify(adsArray);
        var appName = $("#selectAppAdmob").val();
        var groupId = $("#selectAdvertGroupIdAdmob").val();
        $.post("advert_admob/save_advert_admob_one_key", {
            appName: appName,
            group_id:groupId,
            ads:adsArrayString
        }, function (data) {
            $("#inputTranslateAdmob").prop("disabled",false);
            $("#saveEditionAdmob").prop("disabled",false);
            if (data && data.ret == 1) {
                layer.tips(data.message,"#saveEditionAdmob",{tips:1,time:3000});
            } else {
                admanager.showCommonDlg("提示", data.message);
            }
        }, "json");
    });
</script>
</body>
</html>