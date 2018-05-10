
/**
 * 把一个参数数组，根据 explodeParam 交叉合并得到新的参数数组
 * @param params [{x:1,y:2},{x:1,y:3}]
 * @param explodeParam {key:z, values:[4,5,6]}
 * @return Array [{x:1,y:2,z:4}, {x:1,y:2,z:5}, {x:1,y:2,z:6},{x:1,y:3,z:4},{x:1,y:3,z:5},{x:1,y:3,z:6}]
 **/
function getExplodeParams(params, explodeParam) {    //这里的参数由reduce()传入，params是 accumulator，explodeParam是reduce()对象数组当前值
    if (params.length === 0 && explodeParam.values.length > 0) {
        params.push({})
    }
    var createdParams = [];
    params.forEach(function (p) {   // p 是调用对象的一个数组元素
        explodeParam.values.forEach(function (p2) {   // explodeParam.values 是explodeParam里名为"values"的键对应的值，p2为值数组的元素
            var np = $.extend({}, p); //clone
            np[explodeParam.key] = p2; //增加新的键值对
            createdParams.push(np);
        })
    });
    return createdParams;
}

/**
 * @param {{region: string, gender: string, age: string, bidding: string}}
 *
 **/
//以下在Facebook表单的"广告系列名称"中拼凑 系列名称 字符串
function generateFacebookCampaignName(params) {
    // var campaignName = [];
    if (!params) {
        params = {};
    }
    var dims = [];
    var appName = $('#selectApp').val();
    dims.push(appName);

    dims.push("Group_");

    var region = $('#selectRegion').val();
    var countryAlisa = $('#selectRegion')[0].countryAlisa;
    if (countryAlisa) {
        dims.push(countryAlisa);
    } else {
        if (params.region) {
            dims.push(params.region);
        } else {
            dims.push(region.join(","));
        }
    }
    var gender = $('#selectGender').val();
    if (typeof params.gender !== 'undefined') {
        dims.push(params.gender);
    } else {
        dims.push(gender);
    }
    if (params.age) {
        dims.push(params.age);
    } else {
        dims.push($('#inputAge').val());
    }

    if (params.userDevice) {
        dims.push(params.userDevice);
    } else {
        var userDevice = $('#inputUserDevices').val();
        dims.push(userDevice);
    }

    if (params.userOs) {
        dims.push(params.userOs);
    } else {
        var userOs = $('#selectUserOs').val();
        dims.push(userOs);
    }
    var language = $('#selectLanguage').val();
    dims.push(language);

    var accountName = $('#selectAccount option:selected').text();
    dims.push(accountName);

    if (params.identification) {
        if(params.identification=="image"){
            var imagePath = params.materialPath;
            dims.push(imagePath);
        }else if(params.identification == "video"){
            var videoPath = params.materialPath.replace(/.*\//,"视频");
            dims.push(videoPath);
        }
    } else {
        //这是一个在 回显过程中只显示图片路径的
        var imagePath = $("#inputImagePath").val().trim().replace(/,$/,"");
        dims.push(imagePath);
    }
    return dims.join("_");
}
//以下拼凑 admob系列名称 字符串
function generateAdmobCampaignName(params) {
    if (!params) {
        params = {};
    }
    var dims = [];
    var now = new Date();
    dims.push($('#selectAppAdmob').val());

    dims.push("Group_");

    var region = $('#selectRegionAdmob option:selected').text();
    var countryAlisa = $('#selectRegionAdmob')[0].countryAlisa;
    if (params.region) {
        dims.push(params.region);
    } else if (countryAlisa) {
        dims.push(params.countryAlisa);
    } else {
        dims.push(region);
    }

    dims.push($('#selectLanguageAdmob option:selected').text());

    var curr_event = $('#selectIncidentAdmob option:selected').text();
    if (curr_event != "null" && curr_event != "") {
        dims.push("event_" + $('#selectIncidentAdmob option:selected').text());
    }

    if (params.bidding) {
        dims.push(params.bidding);
    } else {
        dims.push($('#inputBiddingAdmob').val());
    }

    if (params.imagePath) {
        dims.push(params.imagePath);
    } else {
        dims.push($('#inputImagePathAdmob').val());
    }

    dims.push(now.getFullYear() + "" + (now.getMonth() + 1) + "" + now.getDate());

    return dims.join("_");
}

//以下两项决定隐藏哪个表单
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

/**
 * @param Array params 一个数组，存放所有的等待请求的参数,这里即
 * @param Function send 处理每一个参数的请求
 * @param onFinish 队列全部处理完成后，调用一下
 **/
function batchRequest(params, send, onFinish) {
    var idx = -1;
    var errLog = [];//[{param:object, errMsg:string}]
    var stop = false;

    function getProgress() {
        return idx + " / " + params.length; //根据JS的规则这里会拼成一个字符串
    }

    function getFullLog() {
        var logs = ["统计 " + getProgress()];
        errLog.forEach(function (log) {
            logs.push(log.errMsg + " : " + JSON.stringify(log.param));
        });
        return logs.join("\n");
    }

    function next() {
        if (stop) {
            admanager.showCommonDlg("终止", getProgress(), function () {
                onFinish(errLog);
            });
            return;
        }
        setTimeout(function () {   //定时任务，直到 idx === param.length的时候 定时任务才结束
            idx++;
            if (idx === params.length) {
                setTimeout(function(){
                    $('#common_message_dialog').modal('hide');
                },1500);
                // console.log("你能看到我，说明执行了这个setTimeout（）");
                admanager.showCommonDlg("完成", getFullLog(), function () {
                    onFinish(errLog);
                });
                return;
            }
            if(errLog.length>0){
                admanager.showCommonDlg("进度", getProgress() + " 。有 " + errLog.length + " 个错误，看console", function () {
                    stop = true;
                });
            }
            request();
        }, 50);
    }

    function request() {
        send(params[idx], function () {
            //请求一个成功
            next();
        }, function (errMsg) {
            //请求一个失败，要不要重试？
            errLog.push({param: params[idx], errMsg: errMsg});
            console.log(errLog[errLog.length - 1]);
            next();
        })
    }
    next();
}

$('#selectApp').change(function () {
    var appName = $('#selectApp').val();
    $.post("campaign/selectMaxBiddingByAppName", {
        appName: appName
    }, function (data) {
        if (data && data.ret == 1) {
            $('#inputBidding')[0].placeholder = "最大出价："+data.max_bidding;
        } else {
            $('#inputBidding')[0].placeholder = "还未设置最大出价";
        }
    }, "json");
    return false;
});
$('#selectAppAdmob').change(function () {
    $("#tbody_admob").empty();
    var appNameAdmob = $('#selectAppAdmob').val();
    $.post('advert_conversion_admob/query_advert_conversion_by_app_name', {appName: appNameAdmob}, function (result) {
        if (result && result.ret == 1) {
            var incidentList = result.data;
            $('#selectIncidentAdmob option').remove();
            $('#selectIncidentAdmob').append($("<option value=''>null</option>"));
            incidentList.forEach(function (one) {
                $('#selectIncidentAdmob').append($("<option value='" + one.conversion_id + "'>" + one.conversion_name + "</option>"));
            });
        } else {
            admanager.showCommonDlg("错误", data.message);
        }
    }, 'json');

    $.post("campaign_admob/selectMaxBiddingByAppName", {
        appName: appNameAdmob
    }, function (data) {
        if (data && data.ret == 1) {
            $('#inputBiddingAdmob')[0].placeholder = "最大出价："+data.max_bidding;
        } else {
            $('#inputBiddingAdmob')[0].placeholder = "还未设置最大出价";
        }
    }, "json");

    return false;
});

//在并非country_analysis_report.jsp 创建、非campaign_auto_create.jsp 创建、非首页创建的情况下，执行由app_name自动补充图片和视频路径
if(!isIndexCreate && !isAutoCreate)  {
    $("#selectApp").change(function() {
        $("#tbody_facebook").empty();
        $("#inputImagePath").val("");
        $("#inputVideoPath").val("");
        $('#checkFacebook').click();
        var checkFacebook = $("#checkFacebook").prop("checked");
        if (checkFacebook) {
            var appName = $("#selectApp").val();
            $.post("app_image_video_rel/query_facebook_path_by_app", {
                app_name: appName
            }, function (data) {
                if(data.ret === 1){
                    var image_path = [];
                    if(data.image_array != null && data.image_array.length >0){
                        for(var i=0;i<data.image_array.length; i++){
                            var img = data.image_array[i];
                            var imgTrimed = img["image_path"].replace(/home\/\w+\/\w+\/\w+\//,"");
                            imgTrimed = imgTrimed.replace(/(\/.+\/)(.*\.\w+)/,"$1");
                            image_path[i] = imgTrimed;
                        }
                        multiSelectAutocomplete("inputImagePath",image_path);
                        $("#inputImagePath").val(image_path[0]);
                    }
                    var video_path = [];
                    if(data.video_array != null && data.video_array.length >0){
                        for(var i=0;i<data.video_array.length; i++){
                            var vdo = data.video_array[i];
                            var vdoTrimed = vdo["video_path"].replace(/home\/\w+\/\w+\/\w+\//,"");
                            vdoTrimed = vdoTrimed.replace(/(\/.+\/)(.*\.\w+)/,"$1");
                            video_path[i] = vdoTrimed;
                        }
                        multiSelectAutocomplete("inputVideoPath",video_path);
                        $("#inputVideoPath").val(video_path[0]);
                    }
                }
            }, "json");
        }
    });
    $("#selectAppAdmob").change(function(){
        $("#inputImagePath").val("");
        $('#checkAdmob').click();
        var checkAdmob = $("#checkAdmob").prop("checked");
        if (checkAdmob) {
            var appName = $("#selectAppAdmob").val();
            $.post("app_image_video_rel/query_admob_path_by_app",
                {app_name: appName},
                function (data) {
                    var image_path = [];
                    if(data.image_array.length >0){
                        for(var i=0;i<data.image_array.length; i++){
                            var img = data.image_array[i];
                            var imgTrimed = img["image_path"].replace(/home\/\w+\/\w+\/\w+\//,"");
                            imgTrimed = imgTrimed.replace(/(\/.+\/)(.*\.\w+)/,"$1");
                            image_path[i] = imgTrimed;
                        }
                        multiSelectAutocomplete("inputImagePathAdmob",image_path);
                        $("#inputImagePathAdmob").val(image_path[0]);
                    }
                }, "json");
        }
    });
}

//根据[国家地区][应用名称]回显已创建好的广告语
$("#selectRegion").change(function () {
    // if (isAutoCreate && !firstInitForm) {
    //     firstInitForm = true;
    //     return;
    // }
    $("#tbody_facebook").empty();
    var region = $('#selectRegion').val();
    if (region != null && region.length > 0) {
        var appName = $('#selectApp').val();
        if(appName != ""){
            $.post("campaign_create_ads_show_up/facebook", {
                appName: appName,
                region: region.join(",")
            }, function (data) {
                if(data && data.ret==1){
                    var ads = data.ads;
                    var tbody = $("#advertisement").children("tbody");
                    ads.forEach(function(ad){
                        var tr = $("<tr></tr>");
                        tr.append("<input type='checkbox' class='check_group'>");
                        var field = ["group_id","language","title","message"];
                        for(var i=0;i<4;i++){
                            var td = $("<td></td>");
                            var value = ad[field[i]];
                            td.text(value);
                            tr.append(td);
                        }
                        tbody.append(tr);
                    });
                }else if(data && data.ret==0){
                    admanager.showCommonDlg("Warning",data.message);
                }
                $("#checkbox_facebook").prop("checked",false);
                $("#checkbox_facebook").click();
            }, "json");
        }
    }
    return false;
});
$("#selectRegionAdmob").change(function () {
    // if (isAutoCreate && !firstInitForm) {
    //     firstInitForm = true;
    //     return;
    // }
    $("#tbody_admob").empty();
    var selectOptions = $('#selectRegionAdmob option:selected');
    var regionAdmob = [];
    selectOptions.each(function () {
        regionAdmob.push($(this).text())
    });
    if (regionAdmob != null && regionAdmob.length > 0) {
        var appNameAdmob = $('#selectAppAdmob').val();
        $.post("campaign_create_ads_show_up/adwords", {
            appName: appNameAdmob,
            region: regionAdmob.join(","),
        }, function (data) {
            if(data && data.ret==1){
                var ads = data.ads;
                var tbody = $("#advertisement_admob").children("tbody");
                ads.forEach(function(ad){
                    var tr = $("<tr></tr>");
                    tr.append("<input type='checkbox' class='check_group_admob'>");
                    var field = ["group_id","language","message1","message2","message3","message4"];
                    for(var i=0;i<6;i++){
                        var td = $("<td></td>");
                        var value = ad[field[i]];
                        td.text(value);
                        tr.append(td);
                    }
                    tbody.append(tr);
                });
            }else if(data && data.ret==0){
                admanager.showCommonDlg("Warning",data.message);
            }
            $("#checkbox_admob").prop("checked",false);
            $("#checkbox_admob").click();
        }, "json");
    }
    return false;
});

//两个表单广告语的全选
$("#checkbox_facebook").click(function(){
    if($("#checkbox_facebook").prop("checked")){
        $(".check_group").prop("checked",true);
    }else{
        $(".check_group").prop("checked",false);
    }
});
$("#checkbox_admob").click(function(){
    if($("#checkbox_admob").prop("checked")){
        $(".check_group_admob").prop("checked",true);
    }else{
        $(".check_group_admob").prop("checked",false);
    }
});

//以下用于读取admob表单数据（手动输入时）
$('#btnCreateAdmob').click(function () {
    var appName = $('#selectAppAdmob').val();
    var selectOptionsAdmob = $('#selectAccountAdmob option:selected');
    var accountNameAdmob = [];
    var accountIdAdmob = [];
    selectOptionsAdmob.each(function () {
        accountNameAdmob.push($(this).text());
        accountIdAdmob.push($(this).val());
    });
    var createCountAdmob = $('#inputCreateCountAdmob').val();

    var region = $('#selectRegionAdmob').val();
    var excludedRegion = $('#selectRegionUnselectedAdmob').val();
    var language = $('#selectLanguageAdmob').val();
    var conversion_id = $('#selectIncidentAdmob').val();
    var campaignName = $('#inputCampaignNameAdmob').val();
    var bugdet = $('#inputBudgetAdmob').val();
    var bidding = $('#inputBiddingAdmob').val();
    var maxCPA = $('#inputMaxCpaAdmob').val();

    //得到选中行的广告语信息
    var checkedTr = $("#tbody_admob input:checked").parent();
    var adsGroup = [];
    checkedTr.each(function(idx){
        var group = {};
        group.groupId = $(this).children("td:eq(0)").text();
        group.message1 = $(this).children("td:eq(2)").text();
        group.message2 = $(this).children("td:eq(3)").text();
        group.message3 = $(this).children("td:eq(4)").text();
        group.message4 = $(this).children("td:eq(5)").text();
        adsGroup.push(group);
    });

    var imagePath = $('#inputImagePathAdmob').val();

    var app = null;
    for (var i = 0; i < appList.length; i++) {
        if (appList[i].tag_name == appName) {
            app = appList[i];
            break;
        }
    }

    //处理分离到国家的字段
    var explodeList = [];//{key:x, values:[]}
    if ($("#selectRegionAdmobExplode").prop("checked")) {
        explodeList.push({
            key: 'region',
            values: region.map(function (x) {
                return x.trim();
            })
        })
    } else {
        explodeList.push({
            key: 'region',
            values: [region.join(",")]
        })
    }
    if(isAutoCreate && modifyRecordId>0){
        $("#inputBiddingAdmobExplode").prop("checked",false);
    }else if (!$("#inputBiddingAdmobExplode").prop("checked") && bidding.indexOf(",") !== -1) {
        admanager.showCommonDlg("错误", "不分离的情况下不允许出价多选");
        return false;
    }
    if($("#inputBiddingAdmobExplode").prop("checked")){
        explodeList.push({
            key: 'bidding',
            values: bidding.split(",").map(function (x) {
                return x.trim();
            })
        });
    }else{
        explodeList.push({
            key: 'bidding',
            values: [bidding]
        });
    }
    //处理图片路径
    if($("#inputImageAdmobExplode").prop("checked")) {
        var valueList = imagePath.trim().replace(/,$/, "").split(",");    //确保正确地切分为数组
        explodeList.push({
            key: 'imagePath',
            values: valueList
        });
    }else{
        var valueStr = imagePath.trim().replace(/,$/, "");
        explodeList.push({
            key: 'imagePath',
            values:[valueStr]
        });
    }
    explodeList.push({
        key:"adsGroup",
        values:adsGroup
    });

    var explodeParams = explodeList.length > 0 ? explodeList.reduce(function (params, explodeParam) {
        return getExplodeParams(params, explodeParam);
    }, []) : [];

    var baseParam = {
        appName: appName,
        accountId: accountIdAdmob.join(","),
        accountName: accountNameAdmob.join(","),
        createCount: createCountAdmob,
        campaignName: campaignName,
        //region: region.join(','),
        excludedRegion: excludedRegion.join(','),
        language: language,
        conversion_id: conversion_id,
        bugdet: bugdet,
        //bidding: bidding,
        gpPackageId: app.google_package_id,
        maxCPA: maxCPA,
    }
//弹一个进度条出来
    var onlyAutoCreateCheck = $('#onlyCheckAdmobAutoCreate').prop('checked');
    if (onlyAutoCreateCheck) {
        var onlyAutoRequestPool = [];
        var explodeCountry = $("#selectRegionAdmobExplode").prop("checked");  //这两个量是选中【仅设为自动创建】特有的
        var explodeBidding = $("#inputBiddingAdmobExplode").prop("checked");
        var url = "auto_create_campaign/adwords/create";
        if (isAutoCreate && modifyRecordId > 0) {
            baseParam.id = modifyRecordId;   //这个是另外新增的字段
            url = "auto_create_campaign/adwords/modify";
        }
        explodeParams.forEach(function (p) {
            var onlyAutoCloned = $.extend({}, baseParam);
            $.extend(onlyAutoCloned, p);
            onlyAutoCloned.campaignName = generateAdmobCampaignName({  //动态生成系列名字
                bidding: p.bidding,
                region: p.region,
                imagePath:p.imagePath
            });
            onlyAutoCloned.explodeCountry = explodeCountry;
            onlyAutoCloned.explodeBidding = explodeBidding;
            onlyAutoCloned.groupId = p.adsGroup.groupId;
            onlyAutoCloned.message1 = p.adsGroup.message1;
            onlyAutoCloned.message2 = p.adsGroup.message2;
            onlyAutoCloned.message3 = p.adsGroup.message3;
            onlyAutoCloned.message4 = p.adsGroup.message4;
            onlyAutoRequestPool.push(onlyAutoCloned);
        });
        // 以下if是使用 campaigns_create.jsp页面传来的数据决定新的url 和 参数id
        batchRequest(onlyAutoRequestPool, function (param, onSuccess, onFail) {
            //fake
            console.log("start.. ", param);
            $.post(url, param, function (data) {
                if (data && data.ret == 1) {
                    onSuccess();
                } else {
                    onFail(data.message)
                }
            }, "json");
        }, function (errorLog) {
            //[仅设置为自动创建]队列全部处理完成
            layer.tips("仅自动创建队列处理完毕","#btnCreateAdmob",{tips:1,time:2000});
        });

    }else {
        var requestPool = [];
        explodeParams.forEach(function (p) {
            var cloned = $.extend({}, baseParam);
            $.extend(cloned, p);
            cloned.campaignName = generateAdmobCampaignName({
                bidding: p.bidding,
                region: p.region,
                imagePath:p.imagePath
            });
            cloned.groupId = p.adsGroup.groupId;
            cloned.message1 = p.adsGroup.message1;
            cloned.message2 = p.adsGroup.message2;
            cloned.message3 = p.adsGroup.message3;
            cloned.message4 = p.adsGroup.message4;
            requestPool.push(cloned);
        });
        var bFinished = false;
        batchRequest(requestPool, function (param, onSuccess, onFail) {  //param是怎么传递进去的
            //fake
            console.log("start.. ", param);
            /*setTimeout(function(){
             if( Math.random()< 0.5){
             onSuccess();
             }else{
             onFail("随机错误");
             }
             },100);*/

            $.post("campaign_admob/create", param, function (data) {
                if (data && data.ret == 1) {
                    onSuccess();
                } else {
                    onFail(data.message)
                }
            }, "json");

        }, function (errorLog) {
            //队列全部处理完成
            var checked = $('#checkAdmobAutoCreate').prop('checked');
            if (checked && !bFinished && errorLog && errorLog.length == 0) {
                // bFinished = true;
                var onlyAutoRequestPool = [];
                var explodeCountry = $("#selectRegionAdmobExplode").prop("checked");
                var explodeBidding = $("#inputBiddingAdmobExplode").prop("checked");
                explodeParams.forEach(function (p) {
                    var onlyAutoCloned = $.extend({}, baseParam);
                    $.extend(onlyAutoCloned, p);
                    onlyAutoCloned.campaignName = generateAdmobCampaignName({  //动态生成系列名字
                        bidding: p.bidding,
                        region: p.region,
                        imagePath:p.imagePath
                    });
                    onlyAutoCloned.explodeCountry = explodeCountry;
                    onlyAutoCloned.explodeBidding = explodeBidding;
                    onlyAutoCloned.groupId = p.adsGroup.groupId;
                    onlyAutoCloned.message1 = p.adsGroup.message1;
                    onlyAutoCloned.message2 = p.adsGroup.message2;
                    onlyAutoCloned.message3 = p.adsGroup.message3;
                    onlyAutoCloned.message4 = p.adsGroup.message4;
                    onlyAutoRequestPool.push(onlyAutoCloned);
                });
                var url = "auto_create_campaign/adwords/create";
                // var messageBody = "创建成功";
                if (isAutoCreate && modifyRecordId > 0) {
                    baseParam.id = modifyRecordId;
                    url = "auto_create_campaign/adwords/modify";
                }
                batchRequest(onlyAutoRequestPool, function (param, onSuccess, onFail) {
                    //fake
                    console.log("start.. ", param);
                    $.post(url, param, function (data) {
                        if (data && data.ret == 1) {
                            onSuccess();
                        } else {
                            onFail(data.message)
                        }
                    }, "json");

                }, function (errorLog) {
                    //[设置为自动创建]队列全部处理完成
                    if(isAutoCreate && modifyRecordId>0){
                        alert("更新队列处理完毕")
                    }else{
                        alert("自动创建队列处理完毕");
                    }
                });
            }
        });
    }

    return false;
});
//以下触发读取facebook表单数据的行为
$('#btnCreate').click(function () {
    var appName = $('#selectApp').val();
    var selectOptions = $('#selectAccount option:selected');
    var accountName = [];
    var accountId = [];
    selectOptions.each(function () {
        accountName.push($(this).text());
        accountId.push($(this).val());
    });

    var createCount = $("#inputCreateCount").val();
    var region = $('#selectRegion').val();
    var excludedRegion = $('#selectRegionUnselected').val();
    var language = $('#selectLanguage').val();
    var age = $('#inputAge').val();
    var gender = $('#selectGender').val();
    var interest = $('#inputInterest').val();
    var userOs = $('#selectUserOs').val();
    var userDevice = $('#inputUserDevices').val();
    //var campaignName = $('#inputCampaignName').val();
    var bugdet = $('#inputBudget').val();
    var bidding = $('#inputBidding').val();
    var maxCPA = $('#inputMaxCpa').val();

    //定位已经选了的广告系列，存进数组
    var checkedTr = $("#tbody_facebook input:checked").parent();
    var adsGroup = [];
    checkedTr.each(function(idx){
        var group = {};
        group.groupId = $(this).children("td:eq(0)").text();
        group.title = $(this).children("td:eq(2)").text();
        group.message = $(this).children("td:eq(3)").text();
        adsGroup.push(group);
    });

    var imagePath = $('#inputImagePath').val();
    var videoPath = $('#inputVideoPath').val();

    var app = null;
    for (var i = 0; i < appList.length; i++) {
        if (appList[i].tag_name == appName) {
            app = appList[i];
            break;
        }
    }

    //处理分离到国家的字段
    var explodeListImage = [];//{key:x, values:[]}
    //从图片路径和视频路径开始把List分裂
    if($("#inputImagePath").val()||$("#inputImagePath").prop("checked")){
        explodeListImage.push({
            key:"identification",
            values:["image"]
        });
        if ($("#selectRegionExplode").prop("checked")) {
            explodeListImage.push({
                key: 'region',
                values: region.map(function (x) {
                    return x.trim();
                })
            })
        } else {
            explodeListImage.push({
                key: 'region',
                values: [region.join(",")]
            })
        }
        if ($("#selectUserOsExplode").prop("checked")) {
            explodeListImage.push({
                key: 'userOs',
                values: userOs.map(function (x) {
                    return x.trim();
                })
            })
        } else {
            explodeListImage.push({
                key: 'userOs',
                values: [userOs.join(",")]
            })
        }
        if ($("#selectUserDevicesExplode").prop("checked")) {
            explodeListImage.push({
                key: 'userDevice',
                values: userDevice.split(',')
            })
        } else {
            explodeListImage.push({
                key: 'userDevice',
                values: [userDevice]
            })
        }
        //确保在从 campaigns_auto_create.jsp 跳转的情况下允许性别多选
        if(isAutoCreate && modifyRecordId>0){
            $("#selectGender").prop("checked",false);
        }else if(($("#selectGenderExplode").prop("checked")==false) && gender.length > 1) {
            admanager.showCommonDlg("错误", "不分离的情况下不允许性别多选");
            return false;
        }
        if($("#selectGenderExplode").prop("checked")==true){
            explodeListImage.push({
                key: 'gender',
                values: gender.map(function (x) {
                    return x.trim();
                })
            });
        }else{
            explodeListImage.push({
                key: 'gender',
                values: [gender.join(",")]
            });
        }

        if (!$("#inputAgeExplode").prop("checked") && age.indexOf(",") !== -1) {
            admanager.showCommonDlg("错误", "不分离的情况下不允许年龄多选");
            return false;
        }
        if($("#inputAgeExplode").prop("checked")){
            explodeListImage.push({
                key: 'age',
                values: age.split(",").map(function (x) {
                    return x.trim();
                })
            });
        }else{
            explodeListImage.push({
                key: 'age',
                values: [age]
            });
        }

        if(isAutoCreate && modifyRecordId>0){
            $("#inputBiddingExplode").prop("checked",false);
        }else if (!$("#inputBiddingExplode").prop("checked") && bidding.indexOf(",") !== -1) {
            admanager.showCommonDlg("错误", "不分离的情况下不允许出价多选");
            return false;
        }
        if($("#inputBiddingExplode").prop("checked")){
            explodeListImage.push({
                key: 'bidding',
                values: bidding.split(",").map(function (x) {
                    return x.trim();
                })
            });
        }else{
            explodeListImage.push({
                key: 'bidding',
                values: [bidding]
            });
        }
        if($("#inputImageExplode").prop("checked")){
            var valueList = imagePath.trim().replace(/,$/, "").split(",");
            explodeListImage.push({
                key: 'materialPath',
                values: valueList
            });
        }else{
            var valueStr = imagePath.trim().replace(/,$/, "");
            explodeListImage.push({
                key: 'materialPath',
                values: [valueStr]
            });
        }
        explodeListImage.push({
            key:"adsGroup",
            values:adsGroup
        });
    }
    var explodeListVideo = [];
    if($("#inputVideoPath").val()||$("#inputVideoPath").prop("checked")){
        explodeListVideo.push({
            key:"identification",
            values:["video"]
        });
        if ($("#selectRegionExplode").prop("checked")) {
            explodeListVideo.push({
                key: 'region',
                values: region.map(function (x) {
                    return x.trim();
                })
            })
        } else {
            explodeListVideo.push({
                key: 'region',
                values: [region.join(",")]
            })
        }
        if ($("#selectUserOsExplode").prop("checked")) {
            explodeListVideo.push({
                key: 'userOs',
                values: userOs.map(function (x) {
                    return x.trim();
                })
            })
        } else {
            explodeListVideo.push({
                key: 'userOs',
                values: [userOs.join(",")]
            })
        }
        if ($("#selectUserDevicesExplode").prop("checked")) {
            explodeListVideo.push({
                key: 'userDevice',
                values: userDevice.split(',')
            })
        } else {
            explodeListVideo.push({
                key: 'userDevice',
                values: [userDevice]
            })
        }
        //确保在从 campaigns_auto_create.jsp 跳转的情况下允许性别多选
        if(isAutoCreate && modifyRecordId>0){
            $("#selectGender").prop("checked",false);
        }else if(($("#selectGenderExplode").prop("checked")==false) && gender.length > 1) {
            admanager.showCommonDlg("错误", "不分离的情况下不允许性别多选");
            return false;
        }
        if($("#selectGenderExplode").prop("checked")==true){
            explodeListVideo.push({
                key: 'gender',
                values: gender.map(function (x) {
                    return x.trim();
                })
            });
        }else{
            explodeListVideo.push({
                key: 'gender',
                values: [gender.join(",")]
            });
        }

        if (!$("#inputAgeExplode").prop("checked") && age.indexOf(",") !== -1) {
            admanager.showCommonDlg("错误", "不分离的情况下不允许年龄多选");
            return false;
        }
        if($("#inputAgeExplode").prop("checked")){
            explodeListVideo.push({
                key: 'age',
                values: age.split(",").map(function (x) {
                    return x.trim();
                })
            });
        }else{
            explodeListVideo.push({
                key: 'age',
                values: [age]
            });
        }

        if(isAutoCreate && modifyRecordId>0){
            $("#inputBiddingExplode").prop("checked",false);
        }else if (!$("#inputBiddingExplode").prop("checked") && bidding.indexOf(",") !== -1) {
            admanager.showCommonDlg("错误", "不分离的情况下不允许出价多选");
            return false;
        }
        if($("#inputBiddingExplode").prop("checked")){
            explodeListVideo.push({
                key: 'bidding',
                values: bidding.split(",").map(function (x) {
                    return x.trim();
                })
            });
        }else{
            explodeListVideo.push({
                key: 'bidding',
                values: [bidding]
            });
        }
        if($("#inputVideoExplode").prop("checked")){
            var valueList = videoPath.trim().replace(/,$/, "").split(",");
            explodeListVideo.push({
                key: 'materialPath',
                values: valueList
            });
        }else{
            var valueStr = videoPath.trim().replace(/,$/, "");
            explodeListVideo.push({
                key: 'materialPath',
                values: [valueStr]
            });
        }
        explodeListVideo.push({
            key:"adsGroup",
            values:adsGroup
        });
    }

    var explodeParamsImage = explodeListImage.length > 0 ? explodeListImage.reduce(function (params, explodeParam){
        return getExplodeParams(params, explodeParam);
    }, []) : [];
    var explodeParamsVideo = explodeListVideo.length > 0 ? explodeListVideo.reduce(function (params, explodeParam){
        return getExplodeParams(params, explodeParam);
    }, []) : [];
    var explodeParams = explodeParamsImage.concat(explodeParamsVideo);

    //用 explodeParams 构造新的请求
    var baseParam = {
        appName: appName,
        appId: app.fb_app_id,
        accountName: accountName.join(","),
        accountId: accountId.join(","),
        createCount: createCount,
        pageId: app.page_id,
        //region: region.join(","),
        excludedRegion: excludedRegion.join(","),
        language: language,
        //age: age,
        //gender: gender,
        interest: interest,
//                userOs: userOs,
//                userDevice: userDevice,
        campaignName: "",
        bugdet: bugdet,
        //bidding: bidding,
        maxCPA: maxCPA,
    };
    var onlyAutoCreateCheck = $('#onlyCheckAutoCreate').prop('checked');
    if (onlyAutoCreateCheck) {
        var onlyAutoRequestPool = [];
        var explodeCountry = $("#selectRegionExplode").prop("checked");
        var explodeAge = $("#inputAgeExplode").prop("checked");
        var explodeGender = $("#selectGenderExplode").prop("checked");
        var explodeBidding = $("#inputBiddingExplode").prop("checked");
        var url = "auto_create_campaign/facebook/create";
        if (isAutoCreate && modifyRecordId > 0) {
            baseParam.id = modifyRecordId;
            url = "auto_create_campaign/facebook/modify";
        }
        explodeParams.forEach(function (p) {
            var onlyAutoCloned = $.extend({}, baseParam);
            $.extend(onlyAutoCloned, p);
            onlyAutoCloned.campaignName = generateFacebookCampaignName({
                identification:p.identification,
                age: p.age,
                gender: p.gender,
                bidding: p.bidding,
                region: p.region,
                userOs: p.userOs,
                userDevice: p.userDevice,
                materialPath:p.materialPath //改为material_path ,在后台再根据正则表达式匹配系列名决定存image还是video
            });
            onlyAutoCloned.explodeCountry = explodeCountry;
            onlyAutoCloned.explodeBidding = explodeBidding;
            onlyAutoCloned.explodeAge = explodeAge;
            onlyAutoCloned.explodeGender=explodeGender;
            onlyAutoCloned.groupId=p.adsGroup.groupId;
            onlyAutoCloned.title=p.adsGroup.title;
            onlyAutoCloned.message=p.adsGroup.message;
            onlyAutoRequestPool.push(onlyAutoCloned);
        });
        //function(param,onSuccess,onFail)整个函数体作为一个对象传递给了 batchRequest(params,send,onFinish)的 send参数
        //但 这里的 onSuccess 和 onFail 都只是一个形式上的函数，真正执行的方法体在batchRequest(params,send,onFinish)内部
        batchRequest(onlyAutoRequestPool, function (param, onSuccess, onFail) {
            //fake
            console.log("start.. ", param);
            $.post(url, param, function (data) {
                if (data && data.ret == 1) {
                    onSuccess();
                }else {
                    onFail(data.message)
                }
            }, "json");
        }, function (errorLog) {
            //[仅设置为自动创建]队列全部处理完成
            if(isAutoCreate && modifyRecordId > 0){
                layer.tips("更新队列处理完毕","#btnCreate",{tips:1,time:3000});
            }else{
                layer.tips("自动创建队列处理完毕","#btnCreate",{tips:1,time:3000});
            }
        });
    } else {
//弹一个进度条出来
        var requestPool = [];
        explodeParams.forEach(function (p) {    //拆分好的键值对数组
            var cloned = $.extend({}, baseParam);
            $.extend(cloned, p);
            cloned.campaignName = generateFacebookCampaignName({
                identification:p.identification,
                age: p.age,
                gender: p.gender,
                bidding: p.bidding,
                region: p.region,
                userOs: p.userOs,
                userDevice: p.userDevice,
                materialPath:p.materialPath
            });
            cloned.groupId=p.adsGroup.groupId;
            cloned.title=p.adsGroup.title;
            cloned.message=p.adsGroup.message;
            requestPool.push(cloned);
        });
        var bFinished = false;
        batchRequest(requestPool, function (param, onSuccess, onFail) {
            //fake
            console.log("start.. ", param);
            $.post("campaign/create", param, function (data) {
                if (data && data.ret == 1) {
                    onSuccess()
                } else {
                    onFail(data.message)
                }
            }, "json");

        }, function (errorLog) {
            //队列全部处理完成
            var checked = $('#checkAutoCreate').prop('checked');
            if (checked && !bFinished && errorLog && errorLog.length == 0) {
                // bFinished = true;
                var onlyAutoRequestPool = [];
                var explodeCountry = $("#selectRegionExplode").prop("checked");
                var explodeAge = $("#inputAgeExplode").prop("checked");
                var explodeGender = $("#selectGenderExplode").prop("checked");
                var explodeBidding = $("#inputBiddingExplode").prop("checked");
                var autoCreateParams = $.extend({}, baseParam);
                var url = "auto_create_campaign/facebook/create";
                if (isAutoCreate && modifyRecordId > 0) {
                    autoCreateParams.id = modifyRecordId;
                    url = "auto_create_campaign/facebook/modify";
                }
                // var messageBody = "创建成功";
                explodeParams.forEach(function (p) {
                    var onlyAutoCloned = $.extend({}, baseParam);
                    $.extend(onlyAutoCloned, p);
                    onlyAutoCloned.campaignName = generateFacebookCampaignName({
                        identification:p.identification,
                        age: p.age,
                        gender: p.gender,
                        bidding: p.bidding,
                        region: p.region,
                        userOs: p.userOs,
                        userDevice: p.userDevice,
                        materialPath:p.materialPath
                    });
                    onlyAutoCloned.explodeCountry = explodeCountry;
                    onlyAutoCloned.explodeBidding = explodeBidding;
                    onlyAutoCloned.explodeAge = explodeAge;
                    onlyAutoCloned.explodeGender=explodeGender;
                    onlyAutoCloned.groupId=p.adsGroup.groupId;
                    onlyAutoCloned.title=p.adsGroup.title;
                    onlyAutoCloned.message=p.adsGroup.message;
                    onlyAutoRequestPool.push(onlyAutoCloned);
                });
                batchRequest(onlyAutoRequestPool, function (param, onSuccess, onFail) {
                    //fake
                    console.log("start.. ", param);
                    $.post(url, param, function (data) {
                        if (data && data.ret == 1) {
                            onSuccess();
                        } else {
                            onFail(data.message)
                        }
                    }, "json");
                }, function () {
                    //[仅设置为自动创建]队列全部处理完成
                    layer.tips("自动创建队列处理完毕","#btnCreate",{tips:1,time:3000});
                });
            }
        });
    }
    return false;
});

//以下两个change用于随输入随时生成系列名称
$('#formFacebook input, #formFacebook select').change(function () {
//            标签名_地理位置&性别&年龄&设备&操作系统_语言_账号_广告图路径
    if ($(this).attr('id') == 'inputCampaignName') return;
    $('#inputCampaignName').val(generateFacebookCampaignName());
});
$('#formAdmob input, #formAdmob select').change(function () {
//            标签名_地理位置&语言&出价_创建时间
    if ($(this).attr('id') == 'inputCampaignNameAdmob') return;

    $('#inputCampaignNameAdmob').val(generateAdmobCampaignName());
});

//在路径选了多个的情况下，用于决定是否默认"分离到系列"
$("#inputVideoPath,#inputImagePath,#inputImagePathAdmob").change(function(){
    function existMutipleSelection(str){
        var array = str.trim().replace(/,$/,"").split(",");
        if(array.length>1){
            return true;
        }else{
            return false;
        }
    }
    var elementId = $(this).attr("id");
    if(elementId == "inputImagePath"){
        var val = $(this).val();
        if(existMutipleSelection(val)){
            $("#inputImageExplode").prop("checked",true);
        }else{
            $("#inputImageExplode").prop("checked",false);
        }
    }else if(elementId == "inputVideoPath"){
        var val = $(this).val();
        if(existMutipleSelection(val)){
            $("#inputVideoExplode").prop("checked",true);
        }else{
            $("#inputVideoExplode").prop("checked",false);
        }
    }else if(elementId == "inputImagePathAdmob"){
        var val = $(this).val();
        if(existMutipleSelection(val)){
            $("#inputImageAdmobExplode").prop("checked",true);
        }else{
            $("#inputImageAdmobExplode").prop("checked",false);
        }
    }
});
