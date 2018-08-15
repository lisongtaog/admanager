var countryMapFunction = function () {
    var map = {};
    for (var i = 0; i < regionList.length; i++) {
        var item = regionList[i];
        var key = item["country_code"];
        map[key] = item["name"];
    }
    return map;
}
var countryMap = countryMapFunction();
var countryNames = [];
for (var i = 0; i < regionList.length; i++) {
    countryNames.push(regionList[i].name);
}

var campaignId;
var countryRevenueSpendReturn = "false";
$("#new_campaign_dlg .btn-primary").click(function () {
    $("#new_campaign_dlg").modal("hide");
    var campaignName = $('#inputCampaignName').val();
    var status = $('#inputStatus').prop('checked') ? 'ACTIVE' : 'PAUSED';
    var budget = $('#inputBudget').val();
    var bidding = $('#inputBidding').val();

    var tags = $('#inputTags').val();

    $.post('campaign/update', {
        campaignId: campaignId,
        campaignName: campaignName,
        status: status,
        budget: budget,
        bidding: bidding
    }, function (data) {
        if (data && data.ret == 1) {
//                $("#new_campaign_dlg").modal("hide");
            $('#btnSearch').click();
        } else {
            admanager.showCommonDlg("错误", data.message);
        }
    }, 'json');
});

var appQueryData = [];
var strFullPath = "";
//功能：将浮点数四舍五入，取小数点后2位
// function toDecimal(x) {
//     var f = parseFloat(x);
//     if (isNaN(f)) {
//         return;
//     }
//     f = Math.round(x*100)/100;
//     return f;
// }
function init() {
    var now = new Date(new Date().getTime() - 86400 * 1000);
    var month = now.getMonth() + 1;
    var date = now.getDate();
    month = month < 10 ? "0"+month : month;
    date = date < 10 ? "0" + date : date;
    $('#inputStartTime').val(now.getFullYear() + "-" + month + "-" + date);
    $('#inputEndTime').val(now.getFullYear() + "-" + month + "-" + date);
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

    $('#inputCampaignCreateTime').datetimepicker({
        minView: "month",
        format: 'yyyy-mm-dd',
        autoclose: true,
        todayBtn: true
    });

    $("#inputCountry").autocomplete({
        source: countryNames
    });

    $("input:checkbox").on('click', function () {
        var $box = $(this);
        if ($box.is(":checked")) {
            var group = "input:checkbox[name='" + $box.attr("name") + "']";
            $(group).prop("checked", false);
            $box.prop("checked", true);
        } else {
            $box.prop("checked", false);
        }
    });

    $("#btnQueryNoData").click(function () {
        var startTime = $('#inputStartTime').val();
        var endTime = $('#inputEndTime').val();
        var query = $("#inputSearch").val();
        var campaignCreateTime = $('#inputCampaignCreateTime').val();
        var countryCode = '';
        var adwordsCheck = $('#adwordsCheck').is(':checked');
        var countryCheck = $('#countryCheck').is(':checked');
        var facebookCheck = $('#facebookCheck').is(':checked');
        var likeCampaignName = $('#inputLikeCampaignName').val();
        var containsNoDataCampaignCheck = $('#containsNoDataCampaignCheck').is(':checked');

        //非负整数
        var reg = /^\d+$/;
        var totalInstallComparisonValue = $('#inputTotalInstallComparisonValue').val();
        var totalInstallOperator = $('#totalInstallOperator option:selected').val();
        if (reg.test(totalInstallComparisonValue)) {
            if (totalInstallOperator == "1") {
                totalInstallOperator = " > ";
            } else if (totalInstallOperator == "2") {
                totalInstallOperator = " < ";
            } else {
                totalInstallOperator = " = ";
            }
            totalInstallComparisonValue = totalInstallOperator + totalInstallComparisonValue;
        } else {
            totalInstallComparisonValue = "";
        }

        //非负数（>=0的任意数）
        reg = /^\d+(\.{0,1}\d+){0,1}$/;
        var cpaComparisonValue = $('#inputCpaComparisonValue').val();
        var cpaOperator = $('#cpaOperator option:selected').val();
        if (reg.test(cpaComparisonValue)) {
            if (cpaOperator == "4") {
                cpaOperator = " > ";
            } else if (cpaOperator == "5") {
                cpaOperator = " < ";
            } else {
                cpaOperator = " = ";
            }
            cpaComparisonValue = cpaOperator + cpaComparisonValue;
        } else {
            cpaComparisonValue = "";
        }

        var countryName = $('#inputCountry').val();
        if (countryName != "") {
            for (var i = 0; i < regionList.length; i++) {
                if (countryName == regionList[i].name) {
                    countryCode = regionList[i].country_code;
                    break;
                }
            }
        }
        $.post('query_not_exist_data', {
            tag: query,
            startTime: startTime,
            endTime: endTime,
            totalInstallComparisonValue: totalInstallComparisonValue,
            adwordsCheck: adwordsCheck,
            countryCheck: countryCheck,
            facebookCheck: facebookCheck,
            countryCode: countryCode,
            likeCampaignName: likeCampaignName,
            campaignCreateTime: campaignCreateTime,
            containsNoDataCampaignCheck: containsNoDataCampaignCheck,
            cpaComparisonValue: cpaComparisonValue
            // onlyQueryNoDataCampaignCheck: onlyQueryNoDataCampaignCheck
        }, function (data) {
            if (data && data.ret == 1) {
                appQueryData = data.data.array;
                $('#result_header').html("<tr><th>系列ID</th><th>账户ID</th><th>账户简称</th><th>系列名称</th><th>创建时间</th><th>状态</th><th>预算</th><th>竞价</th><th>总花费</th><th>总安装</th><th>总点击</th><th>CPA</th><th>CTR</span></th><th>CVR</th><th>ROI</th></tr>");
                data = data.data;
                setData(data);
                bindSortOp();
                var str = "总花费: " + data.total_spend + " 总安装: " + data.total_installed +
                    " 总展示: " + data.total_impressions + " 总点击: " + data.total_click +
                    " CTR: " + data.total_ctr + " CPA: " + data.total_cpa + " CVR: " + data.total_cvr;
                str += "<br/><span class='estimateResult'></span>"
                $('#total_result').html(str);
                $('#total_result').removeClass("editable");
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        }, 'json');
    });
    $('#btnSearch').click(function () {
        $("#btnSearch").prop("disabled", true);
        var startTime = $('#inputStartTime').val();
        var endTime = $('#inputEndTime').val();
        var query = $("#inputSearch").val();
        var campaignCreateTime = $('#inputCampaignCreateTime').val();
        var countryCode = '';
        var adwordsCheck = $('#adwordsCheck').is(':checked');
        var countryCheck = $('#countryCheck').is(':checked');
        var facebookCheck = $('#facebookCheck').is(':checked');
        var likeCampaignName = $('#inputLikeCampaignName').val();
        var containsNoDataCampaignCheck = $('#containsNoDataCampaignCheck').is(':checked');
        // var onlyQueryNoDataCampaignCheck = $('#onlyQueryNoDataCampaignCheck').is(':checked');

        //总安装筛选  非负整数
        var reg = /^\d+$/;
        var totalInstallComparisonValue = $('#inputTotalInstallComparisonValue').val();
        var totalInstallOperator = $('#totalInstallOperator option:selected').val();
        if (reg.test(totalInstallComparisonValue)) {
            if (totalInstallComparisonValue == "0" && totalInstallOperator == "2") {
                totalInstallComparisonValue = "";
                totalInstallOperator = "";
            } else if (totalInstallOperator == "1") {
                totalInstallOperator = " > ";
                containsNoDataCampaignCheck = false;
            } else if (totalInstallOperator == "2") {
                totalInstallOperator = " < ";
            } else {
                totalInstallOperator = " = ";
                if (totalInstallComparisonValue > 0) {
                    containsNoDataCampaignCheck = false;
                }
            }
        } else {
            totalInstallComparisonValue = "";
            totalInstallOperator = "";
        }

        // CPA筛选  非负数（>=0的任意数）
        reg = /^\d+(\.{0,1}\d+){0,1}$/;
        var cpaComparisonValue = $('#inputCpaComparisonValue').val();
        var cpaOperator = $('#cpaOperator option:selected').val();
        if (reg.test(cpaComparisonValue)) {
            if (cpaComparisonValue == "0" && cpaOperator == "5") {
                cpaComparisonValue = "";
                cpaOperator = "";
            } else if (cpaOperator == "4") {
                cpaOperator = " > ";
                containsNoDataCampaignCheck = false;
            } else if (cpaOperator == "5") {
                cpaOperator = " < ";
            } else {
                cpaOperator = " = ";
                if (cpaComparisonValue > 0) {
                    containsNoDataCampaignCheck = false;
                }
            }
        } else {
            cpaComparisonValue = "";
            cpaOperator = "";
        }
        //竞价筛选
        var biddingComparisonValue = $('#inputBiddingComparisonValue').val().trim();//这里我做一个提示？竞价必须小于0.8
        var biddingOperator = $("#biddingOperator option:selected").val();
        if (biddingComparisonValue != "" && parseFloat(biddingComparisonValue) > 0) {
            biddingComparisonValue = parseFloat(biddingComparisonValue) * 100;
            if (biddingOperator === "7") {
                biddingOperator = " > ";
            } else if (biddingOperator === "8") {
                biddingOperator = " < ";
            } else {
                biddingOperator = " = ";
            }
        } else {
            biddingOperator = "";
        }
        //状态判断
        var statusOperator = $("#statusOperator option:selected").val();


        var countryName = $('#inputCountry').val();
        if (countryName != "") {
            for (var i = 0; i < regionList.length; i++) {
                if (countryName == regionList[i].name) {
                    countryCode = regionList[i].country_code;
                    break;
                }
            }
        }

        $.post('query_by_mul_conditions', {

            tag: query,
            startTime: startTime,
            endTime: endTime,
            adwordsCheck: adwordsCheck,
            countryCheck: countryCheck,
            facebookCheck: facebookCheck,
            countryCode: countryCode,
            likeCampaignName: likeCampaignName,
            campaignCreateTime: campaignCreateTime,
            cpaComparisonValue: cpaComparisonValue,
            cpaOperator: cpaOperator,
            totalInstallComparisonValue: totalInstallComparisonValue,
            totalInstallOperator: totalInstallOperator,
            containsNoDataCampaignCheck: containsNoDataCampaignCheck,
            biddingComparisonValue: biddingComparisonValue,
            biddingOperator: biddingOperator,
            statusOperator:statusOperator
        }, function (data) {
            $("#btnSearch").prop("disabled", false);
            if (data && data.ret == 1) {
                appQueryData = data.data.array;
                if (countryCheck) {
                    $('#result_header').html("<tr><th>国家</th>" +
                        "<th>总展示<span sorterId=\"21\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th>" +
                        "<th>总花费<span sorterId=\"22\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th>" +
                        "<th>总安装<span sorterId=\"23\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th>" +
                        "<th>总点击<span sorterId=\"24\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th>" +
                        "<th>CPA<span sorterId=\"25\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th>" +
                        "<th>CTR<span sorterId=\"26\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th>" +
                        "<th>CVR<span sorterId=\"27\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th>" +
                        "</tr>");
                } else {
                    $('#result_header').html("<tr><th>系列ID</th><th>账户ID</th><th>账户简称</th><th>系列名称</th>" +
                        "<th>创建时间<span sorterId=\"1\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th>" +
                        "<th>状态<span sorterId=\"2\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th>" +
                        "<th>预算<span sorterId=\"3\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th>" +
                        "<th>竞价<span sorterId=\"4\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th>" +
                        "<th>总花费<span sorterId=\"5\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th>" +
                        "<th>总安装<span sorterId=\"6\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th>" +
                        "<th>总点击<span sorterId=\"7\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th>" +
                        "<th>CPA<span sorterId=\"8\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th>" +
                        "<th>CECPM</th>" +
                        "<th>CTR<span sorterId=\"9\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th>" +
                        "<th>CVR<span sorterId=\"10\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th>" +
                        "<th>CTR*CVR</th>" +
                        "<th>UnRate<span sorterId=\"11\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th>" +
                        "</tr>");
                }
                data = data.data;
                setData(data);
                bindSortOp();
                var str = "总花费: " + data.total_spend + " 总安装: " + data.total_installed +
                    " 总展示: " + data.total_impressions + " 总点击: " + data.total_click +
                    " CTR: " + data.total_ctr + " CPA: " + data.total_cpa + " CVR: " + data.total_cvr;
                str += "<br/><span class='estimateResult'></span>";
                str += "<br/>";
                str += "facebook_ARCHIVED:&nbsp" + data.total_ARCHIVED +"&nbsp&nbsp&nbsp&nbsp&nbsp&nbspfacebook_ACTIVE:&nbsp"+data.total_ACTIVE+"&nbsp&nbsp&nbsp&nbsp&nbsp&nbspfacebook_PAUSED:&nbsp"+data.total_PAUSED+"&nbsp&nbsp&nbsp&nbsp&nbsp&nbspadWords_paused:&nbsp"+data.total_paused+"&nbsp&nbsp&nbsp&nbsp&nbsp&nbspadWords_removed:&nbsp"+data.total_removed+"&nbsp&nbsp&nbsp&nbsp&nbsp&nbspadWords_enabled:&nbsp"+data.total_enabled;
                $('#total_result').html(str);
                $('#total_result').removeClass("editable");
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        }, 'json');
        countryRevenueSpendReturn = "true";
    });

    strFullPath = window.document.location.href;
    if (strFullPath.indexOf("?") != -1) {
        strFullPath = strFullPath.substr(strFullPath.indexOf("?") + 1, strFullPath.length);
        var thisArr = strFullPath.split("&");
        countryRevenueSpendReturn = "true";
        //顺序：tag_name startTime endTime country_code
        $("#inputSearch").val(thisArr[0].split("=")[1]);
        $('#inputStartTime').val(thisArr[1].split("=")[1]);
        $('#inputEndTime').val(thisArr[2].split("=")[1]);
        var country_code = thisArr[3].split("=")[1];
        $('#inputCountry').val(countryMap[country_code]);
        $('#btnSearch').click();
    }

    $('#btnSummary').click(function () {
        $("#btnSummary").prop("disabled", true);
        var startTime = $('#inputStartTime').val();
        var endTime = $('#inputEndTime').val();
        var adwordsCheck = $('#adwordsCheck').is(':checked');  //is是一个方法的标签名
        var facebookCheck = $('#facebookCheck').is(':checked');
        $.post('query', {
            summary: true,
            startTime: startTime,
            endTime: endTime,
            adwordsCheck: adwordsCheck,
            facebookCheck: facebookCheck,
        }, function (data) {
            $("#btnSummary").prop("disabled", false);
            if (data && data.ret == 1) {
                if (data.same_time && data.same_time == 1) {
                    $('#result_header').html("<tr><th>应用名称</th>" +
                        "<th>总花费<span sorterId=\"70\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th>" +
                        "<th>总营收<span sorterId=\"72\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th>" +
                        "<th>新用户总营收</th>" +
                        "<th>新用户回本率</th>" +
                        "<th>总安装<span sorterId=\"74\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th>" +
                        "<th>总展示<span sorterId=\"75\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th>" +
                        "<th>总点击<span sorterId=\"76\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CTR<span sorterId=\"77\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th>" +
                        "<th>CPA<span sorterId=\"78\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CVR<span sorterId=\"79\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th>" +
                        "<th>ECPM<span sorterId=\"80\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>Incoming<span sorterId=\"81\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th></tr>");
                    setDataSummary(data,1);
                } else {
                    $('#result_header').html("<tr><th>应用名称</th><th>总花费<span sorterId=\"70\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th>" +
                        "<th>总营收<span sorterId=\"72\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th>" +
                        "<th>总安装<span sorterId=\"74\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>总展示<span sorterId=\"75\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th>" +
                        "<th>总点击<span sorterId=\"76\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CTR<span sorterId=\"77\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th>" +
                        "<th>CPA<span sorterId=\"78\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CVR<span sorterId=\"79\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th>" +
                        "<th>ECPM<span sorterId=\"80\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>Incoming<span sorterId=\"81\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th></tr>");
                    setDataSummary(data,0);
                }
                bindSortOpSummary();
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        }, 'json');
    });
}

//把各个应用的汇总信息（合并国家）显示出来
function setDataSummary(result,same_time) {
    var total_spend = 0;
    var total_revenue = 0;
    var total_installed = 0;
    var total_impressions = 0;
    var total_click = 0;
    var total_ctr = 0;
    var total_cpa = 0;
    var total_cvr = 0;
    var total_incoming = 0;
    var total_new_revenue = 0;

    var keyset = [];
    if (same_time == 1) {
        keyset = ["name", "total_spend", "total_revenue", "total_new_revenue","roi", "total_installed", "total_impressions", "total_click",
            "total_ctr", "total_cpa", "total_cvr", "ecpm", "incoming"];
    } else {
        keyset = ["name", "total_spend", "total_revenue", "total_installed", "total_impressions", "total_click",
            "total_ctr", "total_cpa", "total_cvr", "ecpm", "incoming"];
    }


    // var d = new Date(); //创建一个Date对象:这个对象返回创建时的本机系统时间
    // var localTime = d.getTime();
    // var localOffset = d.getTimezoneOffset() * 60000; //获得当地时间与UTC（1970）偏移的毫秒数
    // var utc = localTime + localOffset; //utc即GMT时间
    // var offset = 8; //美西时间，西八区差值
    // var Los = utc - (3600000 * offset);    //得到相应美西时间 Date类型
    // var nd = new Date(Los);
    // var westTime = nd.getHours();  //返回美西时间的小时数
    // westTime = westTime - 1;
    // var minutes = nd.getMinutes();
    // var d_minutes = westTime * 60 + minutes;   //得到北京时间与美西时间相差小时数


    $('#results_body > tr').remove();
    var data = result.data;
    for (var i = 0; i < data.length; i++) {
        var one = data[i];
        var tr = $("<tr></tr>");
        var con;
        for (var j = 0; j < keyset.length; j++) {
            var key = keyset[j];
            if (key == "total_spend") {
                var td = $('<td title ="' + data[i]["spend_14"] + '"></td>');
            }
            else if (key == "total_revenue") {
                var td = $('<td title ="' + data[i]["revenue_14"] + '"></td>');
            }
            else if (key == "total_installed") {
                var td = $('<td title ="' + data[i]["installed_14"] + '"></td>');
            }
            else if (key == "total_cpa") {
                var td = $('<td title ="' + data[i]["cpa_14"] + '"></td>');
            }
            else if (key == "total_cvr") {
                var td = $('<td title ="' + data[i]["cvr_14"] + '"></td>');
            } else {
                var td = $("<td></td>");
            }
            if (key == 'total_spend') {     //对total_spend 条目进行颜色处理
                if (one['warning_level'] == 1) {
                    td.addClass("yellow");
                } else if (one['warning_level'] == 2) {
                    td.addClass("red");
                }
            }
            con = one[key];
            td.text(con);
            tr.append(td);
        }
        total_spend += one['total_spend'];
        total_revenue += one['total_revenue'];
        total_installed += one['total_installed'];
        total_impressions += one['total_impressions'];
        total_click += one['total_click'];
        total_incoming += one['incoming'];
        if (same_time == 1) {
            total_new_revenue += one['total_new_revenue'];
        }

        total_ctr = total_impressions > 0 ? total_click / total_impressions : 0;
        total_cpa = total_installed > 0 ? total_spend / total_installed : 0;
        total_cvr = total_click > 0 ? total_installed / total_click : 0;
        $('#results_body').append(tr);
    }
    var str = "";
    total_ctr = Math.round(total_ctr*1000)/1000;
    total_cpa = Math.round(total_cpa*1000)/1000;
    total_cvr = Math.round(total_cvr*1000)/1000;
    if (same_time == 1) {
        var total_roi = 0;
        if (total_spend > 0) {
            total_roi = total_new_revenue / total_spend;
        }
        total_roi = Math.round(total_roi*1000)/1000;
        total_new_revenue = Math.round(total_new_revenue*1000)/1000;
        str = "总花费:" + total_spend
            + "&nbsp;&nbsp;&nbsp;&nbsp;总营收:" + total_revenue
            + "&nbsp;&nbsp;&nbsp;&nbsp;新用户总营收:" + total_new_revenue
            + "&nbsp;&nbsp;&nbsp;&nbsp;新用户回本率:" + total_roi
            + "&nbsp;&nbsp;&nbsp;&nbsp;总安装:" + total_installed
            + "&nbsp;&nbsp;&nbsp;&nbsp;总展示:" + total_impressions
            + "&nbsp;&nbsp;&nbsp;&nbsp;总点击:" + total_click
            + "&nbsp;&nbsp;&nbsp;&nbsp;CTR:" + total_ctr
            + "&nbsp;&nbsp;&nbsp;&nbsp;CPA:" + total_cpa
            + "&nbsp;&nbsp;&nbsp;&nbsp;CVR:" + total_cvr
            + "&nbsp;&nbsp;&nbsp;&nbsp;Incoming:" + total_incoming;
    } else {
        str = "&nbsp;&nbsp;&nbsp;&nbsp;总花费: "
             + total_spend + "&nbsp;&nbsp;&nbsp;&nbsp;总营收: "
            + total_revenue + "&nbsp;&nbsp;&nbsp;&nbsp;总安装: " + total_installed +
            "&nbsp;&nbsp;&nbsp;&nbsp;总展示: " + total_impressions + "&nbsp;&nbsp;&nbsp;&nbsp;总点击: " + total_click +
            "&nbsp;&nbsp;&nbsp;&nbsp;CTR: " + total_ctr + "&nbsp;&nbsp;&nbsp;&nbsp;CPA: " + total_cpa
            + "&nbsp;&nbsp;&nbsp;&nbsp;CVR: " + total_cvr
            + "&nbsp;&nbsp;&nbsp;&nbsp;Incoming: " + total_incoming;

    }
    $('#total_result').html(str);
    $('#total_result').removeClass("editable");
}

function setData(data) {
    $('#results_body > tr').remove();
    for (var i = 0; i < data.array.length; i++) {
        var one = data.array[i];
        var tr = $('<tr></tr>');
        var countryCheck = $('#countryCheck').is(':checked');
        var keyset = ["campaign_id", "account_id", "short_name", "campaign_name", "create_time",
            "status", "budget", "bidding", "spend", "installed", "click", "cpa", "ecpm", "ctr", "cvr", "ctr_mul_cvr", "un_rate"];
        var modifyColumns = ["campaign_name", "budget", "bidding"];
        if (countryCheck) {
            keyset = ["country_name", "impressions", "spend", "installed", "click", "cpa", "ctr", "cvr"];
        }
        for (var j = 0; j < keyset.length; j++) {
            var campaignId = one['campaign_id'];
            var totalSpend = 0;
            for (var jj = 0; jj < appQueryData.length; jj++) {
                if (appQueryData[jj]['campaign_id'] == campaignId) {
                    var countryName = $('#inputCountry').val();
                    if (countryName != "") {
                        totalSpend = appQueryData[jj]['campaign_spends'];
                    } else {
                        totalSpend = appQueryData[jj]['spend'];
                    }
                    break;
                }
            }
            var td = $("<td class='td_left_border'></td>");
            var field = keyset[j];
            var field_value = one[field];

            if (field == 'budget' || field == 'bidding') {
                td.text(field_value / 100);
            } else if (field == 'spend') {
                var currSpend = one['spend'];
                if (currSpend > (one['budget'] * 9 / 1000)) {
                    td.addClass("danhuangse");
                }
                td.text(field_value + " / " + totalSpend);
            } else if (field == 'un_rate') {
                if (field_value == -100000) {
                    td.text("--");
                } else if (field_value == 0) {
                    td.text("");
                } else {
                    td.text(field_value);
                }
            } else {
                td.text(field_value);
            }

            if (modifyColumns.indexOf(field) != -1) {
                td.addClass("editable");
                td[0].cloumnName = field;
            }

            tr.append(td);
        }

        //这里增加【新建】按钮
        if (!countryCheck) {
            var btn = $('<input type="button" value="新建">');
            btn.data("campaign_id", one['campaign_id']);
            btn.data("budget", one['budget']);
            btn.data("bidding", one['bidding']);
            btn.click(function () {
                var campaign_id = $(this).data("campaign_id");  //选中当前元素中键campaign_id的值
                var budget = $(this).data("budget");
                budget = budget / 100;
                var bidding = $(this).data("bidding");
                bidding = bidding / 100;
                window.open("campaigns_create.jsp?type=auto_create&campaignId=" + campaign_id + "&budget=" + budget + "&bidding=" + bidding, "_blank");
                //window.open(url,name,features,replace)，四个参数分别针对url，新窗口target属性或窗口名称，窗口特征和浏览器历史
            });
        }
        tr.append(btn);

        if (one["impressions"] == 0) {
            tr.addClass("lilac");
        }
        //为当前的 DOM元素 tr[0] 添加属性 origCampaignData
        tr[0].origCampaignData = one;  //@param one 这是后台返回数据的第 i 个数组元素——Json格式
        tr[0].changedCampainData = {};
        var admobCheck = $('#admobCheck').is(':checked');
        var countryCheck = $('#countryCheck').is(':checked');
        if (!admobCheck && !countryCheck) {
//                var td = $('<td><a class="link_modify" href="javascript:void(0)">修改</a><a class="link_copy" href="javascript:void(0)">复制</a></td>');
//                tr.append(td);
        }
        $('#results_body').append(tr);
    }
    bindOp();
}

function bindOp() {
    $(".link_modify").click(function () {
        $('#modify_form').show();

        $("#dlg_title").text("修改系列");

        var tds = $(this).parents("tr").find('td');
        campaignId = $(tds.get(0)).text();
        var campaignName = $(tds.get(2)).text();
        var status = $(tds.get(4)).text();
        var budget = $(tds.get(5)).text();
        var bidding = $(tds.get(6)).text();

        $('#inputCampaignName').val(campaignName);
        if (status.toLowerCase() == 'active') {
            $('#inputStatus').prop('checked', true);
        } else {
            $('#inputStatus').prop('checked', false);
        }
        $('#inputBudget').val(budget);
        $('#inputBidding').val(bidding);

        $("#new_campaign_dlg").modal("show");
    });

    $(".link_copy").click(function () {
        var tds = $(this).parents("tr").find('td');
        $.post('campaign/find_create_data', {
            campaignId: $(tds.get(0)).text(),
        }, function (data) {
            if (data && data.ret == 1) {
                var list = [];
                var keys = ["tag_name", "app_name", "facebook_app_id", "account_id", "country_region",
                    "language", "age", "gender", "detail_target", "campaign_name", "page_id", "bugdet", "bidding", "max_cpa", "title", "message"];
                var data = data.data;
                for (var i = 0; i < keys.length; i++) {
                    list.push(data[keys[i]]);
                }
                admanager.showCommonDlg("请手动创建", list.join("\t"));
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        }, 'json');
    });
}

function bindSortOpSummary() {
    $('.sorter').click(function () {
        var sorterId = $(this).attr('sorterId');
        sorterId = parseInt(sorterId);
        if ($(this).hasClass("glyphicon-arrow-up")) {
            $(this).removeClass("glyphicon-arrow-up");
            $(this).addClass("glyphicon-arrow-down");
            sorterId += 1000;
        } else {
            $(this).removeClass("glyphicon-arrow-down");
            $(this).addClass("glyphicon-arrow-up");
        }

        var startTime = $('#inputStartTime').val();
        var endTime = $('#inputEndTime').val();
        var adwordsCheck = $('#adwordsCheck').is(':checked');
        var facebookCheck = $('#facebookCheck').is(':checked');
        $.post('query', {
            startTime: startTime,
            endTime: endTime,
            adwordsCheck: adwordsCheck,
            facebookCheck: facebookCheck,
            sorterId: sorterId,
            summary: true
        }, function (data) {
            if (data && data.ret == 1) {
                if (data.same_time && data.same_time == 1) {
                    setDataSummary(data,1);
                } else {
                    setDataSummary(data,0);
                }
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        }, 'json');
    });
}

function bindSortOp() {
    $('.sorter').click(function () {
        var sorterId = $(this).attr('sorterId');
        sorterId = parseInt(sorterId);
        if ($(this).hasClass("glyphicon-arrow-up")) {
            $(this).removeClass("glyphicon-arrow-up");
            $(this).addClass("glyphicon-arrow-down");
            sorterId += 1000;
        } else {
            $(this).removeClass("glyphicon-arrow-down");
            $(this).addClass("glyphicon-arrow-up");
        }

        var startTime = $('#inputStartTime').val();
        var endTime = $('#inputEndTime').val();
        var query = $("#inputSearch").val();
        var campaignCreateTime = $('#inputCampaignCreateTime').val();
        var countryCode = '';
        var adwordsCheck = $('#adwordsCheck').is(':checked');
        var countryCheck = $('#countryCheck').is(':checked');
        var facebookCheck = $('#facebookCheck').is(':checked');
        var likeCampaignName = $('#inputLikeCampaignName').val();
        var containsNoDataCampaignCheck = $('#containsNoDataCampaignCheck').is(':checked');
        // var onlyQueryNoDataCampaignCheck = $('#onlyQueryNoDataCampaignCheck').is(':checked');

        //非负整数
        var reg = /^\d+$/;
        var totalInstallComparisonValue = $('#inputTotalInstallComparisonValue').val();
        var totalInstallOperator = $('#totalInstallOperator option:selected').val();
        if (reg.test(totalInstallComparisonValue)) {
            if (totalInstallComparisonValue == "0" && totalInstallOperator == "2") {
                totalInstallComparisonValue = "";
                totalInstallOperator = "";
            } else if (totalInstallOperator == "1") {
                totalInstallOperator = " > ";
                containsNoDataCampaignCheck = false;
            } else if (totalInstallOperator == "2") {
                totalInstallOperator = " < ";
            } else {
                totalInstallOperator = " = ";
                if (totalInstallComparisonValue > 0) {
                    containsNoDataCampaignCheck = false;
                }
            }
        } else {
            totalInstallComparisonValue = "";
            totalInstallOperator = "";
        }

        //非负数（>=0的任意数）
        reg = /^\d+(\.{0,1}\d+){0,1}$/;
        var cpaComparisonValue = $('#inputCpaComparisonValue').val();
        var cpaOperator = $('#cpaOperator option:selected').val();
        if (reg.test(cpaComparisonValue)) {
            if (cpaComparisonValue == "0" && cpaOperator == "5") {
                cpaComparisonValue = "";
                cpaOperator = "";
            } else if (cpaOperator == "4") {
                cpaOperator = " > ";
                containsNoDataCampaignCheck = false;
            } else if (cpaOperator == "5") {
                cpaOperator = " < ";
            } else {
                cpaOperator = " = ";
                if (cpaComparisonValue > 0) {
                    containsNoDataCampaignCheck = false;
                }
            }
        } else {
            cpaComparisonValue = "";
            cpaOperator = "";
        }

        var biddingComparisonValue = $('#inputBiddingComparisonValue').val();
        if (!reg.test(biddingComparisonValue)) {
            biddingComparisonValue = "";
        }

        var countryName = $('#inputCountry').val();
        if (countryName != "") {
            for (var i = 0; i < regionList.length; i++) {
                if (countryName == regionList[i].name) {
                    countryCode = regionList[i].country_code;
                    break;
                }
            }
        }

        //状态判断
        var statusOperator = $("#statusOperator option:selected").val();

        $.post('query_by_mul_conditions', {
            tag: query,
            startTime: startTime,
            endTime: endTime,
            adwordsCheck: adwordsCheck,
            countryCheck: countryCheck,
            facebookCheck: facebookCheck,
            countryCode: countryCode,
            sorterId: sorterId,
            likeCampaignName: likeCampaignName,
            campaignCreateTime: campaignCreateTime,
            cpaComparisonValue: cpaComparisonValue,
            totalInstallComparisonValue: totalInstallComparisonValue,
            containsNoDataCampaignCheck: containsNoDataCampaignCheck,
            biddingComparisonValue: biddingComparisonValue,
            totalInstallOperator: totalInstallOperator,
            cpaOperator: cpaOperator,
            statusOperator:statusOperator
        }, function (data) {
            if (data && data.ret == 1) {
                data = data.data;
                setData(data);
                var str = "总花费: " + data.total_spend + " 总安装: " + data.total_installed +
                    " 总展示: " + data.total_impressions + " 总点击: " + data.total_click +
                    " CTR: " + data.total_ctr + " CPA: " + data.total_cpa + " CVR: " + data.total_cvr;

                str += "<br/><span class='estimateResult'></span>";
                str += "<br/>";
                str += "facebook_ARCHIVED:&nbsp" + data.total_ARCHIVED +"&nbsp&nbsp&nbsp&nbsp&nbsp&nbspfacebook_ACTIVE:&nbsp"+data.total_ACTIVE+"&nbsp&nbsp&nbsp&nbsp&nbsp&nbspfacebook_PAUSED:&nbsp"+data.total_PAUSED+"&nbsp&nbsp&nbsp&nbsp&nbsp&nbspadWords_paused:&nbsp"+data.total_paused+"&nbsp&nbsp&nbsp&nbsp&nbsp&nbspadWords_removed:&nbsp"+data.total_removed+"&nbsp&nbsp&nbsp&nbsp&nbsp&nbspadWords_enabled:&nbsp"+data.total_enabled;
                $('#total_result').removeClass("editable");
                $('#total_result').html(str);
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        }, 'json');
    });
}

function estimateCost() {
    var total_spend = 0;
    var total_installed = 0;
    var total_impressions = 0;
    var total_click = 0;
    var total_ctr = 0;
    var total_cpa = 0;
    var total_cvr = 0;

    $('#results_body tr').each(function () {
        if (!this.origCampaignData) return;
        total_spend += this.origCampaignData.spend;
        if (this.changedCampainData.enabled === false) {
            total_spend -= this.origCampaignData.spend;
        }
        total_installed += this.origCampaignData.installed;
        if (this.changedCampainData.enabled === false) {
            total_installed -= this.origCampaignData.installed;
        }
        total_impressions += this.origCampaignData.impressions;
        total_click += this.origCampaignData.click;
    });
    $('#results_body tr').each(function () {
        if (!this.changedCampainData) return;
        if (this.changedCampainData.budget > 0 && this.changedCampainData.budget != this.origCampaignData.budget) {
            total_spend += (this.changedCampainData.budget * 100 - this.origCampaignData.budget) / this.origCampaignData.budget * this.origCampaignData.spend;
            var count = (this.changedCampainData.budget * 100 - this.origCampaignData.budget) / this.origCampaignData.budget * this.origCampaignData.installed;
            total_installed += parseInt(count);
        }
    });

    total_ctr = total_impressions > 0 ? total_click / total_impressions : 0;
    total_cpa = total_installed > 0 ? total_spend / total_installed : 0;
    total_cvr = total_click > 0 ? total_installed / total_click : 0;

    var str = "总花费: " + total_spend + " 总安装: " + total_installed +
        " 总展示: " + total_impressions + " 总点击: " + total_click +
        " CTR: " + total_ctr + " CPA: " + total_cpa + " CVR: " + total_cvr;

    $('.estimateResult').text(str);    // $(".estimateResult") 是 div#total_result 里跳出的估计值
}

function bindBatchModifyOperation() {
    $(document).click(function () {
        $('#results_body td.editable').removeClass('editing');
        // .inputTemp 以下这个函数究竟要干什么？
        $(".inputTemp").replaceWith(function () {
            var td = $(".inputTemp").parent('td')[0];
            var tr = $(td).parents('tr')[0];
            if (td.origValue != this.value) {
                switch (td.cloumnName) {
                    case 'campaign_name':
                        tr.changedCampainData.campaignName = this.value;
                        break;
                    case 'budget':
                        tr.changedCampainData.budget = this.value;
                        break;
                    case 'bidding':
                        if (this.value > 0.8) {
                            admanager.showCommonDlg("警告", "竞价不能超过 0.8");
                            break;
                        } else {
                            tr.changedCampainData.bidding = this.value;
                            break;
                        }
                }
                $(td).addClass("changed");
            } else {
                switch (td.cloumnName) {
                    case 'campaign_name':
                        tr.changedCampainData.campaignName = null;
                        break;
                    case 'budget':
                        tr.changedCampainData.budget = null;
                        break;
                    case 'bidding':
                        tr.changedCampainData.bidding = null;
                        break;
                }
                $(td).removeClass("changed");
            }
            return this.value;
        });
        estimateCost();
    });

    // 【批量修改】
    $('#btnModifyBatch').click(function () {
        var checkbox = $('#result_header input[type=checkbox]');
        if (checkbox.length > 0) return;

        $('#result_header tr th:first').before($("<th><input id='ckSelectAll' type='checkbox' /></th>"));
        $('#results_body tr').each(function () {
            $(this).find('td:first').before($("<td><input class='ckEnableCampaign' type='checkbox' /></td>"));
            if (this.origCampaignData.network == 'admob') {
                if (this.origCampaignData.status == 'enabled') {
                    this.origCampaignData.campainEnabled = true;
                    $(this).find('input[type=checkbox]').prop('checked', true);
                } else {
                    this.origCampaignData.campainEnabled = false;
                    $(this).find('input[type=checkbox]').prop('checked', false);
                }
            } else {
                if (this.origCampaignData.status == 'ACTIVE') {
                    this.origCampaignData.campainEnabled = true;
                    $(this).find('input[type=checkbox]').prop('checked', true);
                } else {
                    this.origCampaignData.campainEnabled = false;
                    $(this).find('input[type=checkbox]').prop('checked', false);
                }
            }
        });
        $('.ckEnableCampaign').change(function () {
            var tr = $(this).parents('tr')[0];
            var data = tr.origCampaignData;
            if ($(this).is(':checked') != data.campainEnabled) {
                tr.changedCampainData.enabled = $(this).is(':checked');
                $(this).parents('td').addClass('changed');
            } else {
                $(this).parents('td').removeClass('changed');
                tr.changedCampainData.enabled = null;
            }
            estimateCost();
        });
        $('#ckSelectAll').change(function () {
            if ($(this).is(":checked")) {
                $('#results_body input[type=checkbox]').prop('checked', true);
            } else {
                $('#results_body input[type=checkbox]').prop('checked', false);
            }
            $('#results_body input[type=checkbox]').trigger('change');
        });

        $('#total_result').addClass("editable");

        $('#results_body td.editable').click(function (e) {
            e.stopPropagation();
            if ($(this).hasClass('editing')) {
                return;
            }
            $(this).addClass('editing');
            var value = $(this).text();
            updateVal(this, value);
        });

        function updateVal(currentEle, value) {
            if (!currentEle.origValue) {
                currentEle.origValue = value;
            }
            $(currentEle).html('<input class="inputTemp" type="text" style="width:100%" value="' + value + '" />');
            $(".inputTemp", currentEle).focus()
            // .keyup(function (event) {
            //     if (event.keyCode == 13) {
            //         $(currentEle).removeClass('editing');
            //         $(currentEle).text($(".inputTemp").val().trim());
            //         if ($(currentEle).text() != currentEle.origValue) {
            //             $(currentEle).addClass("changed");
            //         } else {
            //             $(currentEle).removeClass("changed");
            //         }
            //     }
            // })
                .click(function (e) {
                    e.stopPropagation();
                });
        }
    });

    $('#btnModifySubmit').click(function () {
        var list = [];
        var countryName = $('#inputCountry').val();
        var countryCode = '';
        for (var i = 0; i < regionList.length; i++) {
            if (countryName == regionList[i].name) {
                countryCode = regionList[i].country_code;
                break;
            }
        }
        if (countryCode == '' && countryName != '') {
            return;
        }
        //读取被修改行
        $('#results_body td.changed').each(function () {
            var tr = $(this).parents('tr')[0];
            var one = tr.changedCampainData;
            if (countryCode != '') {
                if (one.enabled === false) {
                    one.excludedCountry = countryCode;
                    delete one.enabled;
                }
            }
            one.network = tr.origCampaignData.network;
            one.campaignId = tr.origCampaignData.campaign_id;
            one.accountId = tr.origCampaignData.account_id;
            one.campaignName = tr.origCampaignData.campaign_name;
            for (var i = 0; i < list.length; i++) {
                if (list[i].campaignId == one.campaignId) {
                    return;
                }
            }
            list.push(one);
        });

        var query = $("#inputSearch").val();
        $.post('campaign/batch_change', {
            data: JSON.stringify(list),
            appName: query,
            countryCode: countryCode
        }, function (data) {
            if (data && data.ret == 1) {
                admanager.showCommonDlg("提示", "修改任务提交成功");
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        }, 'json');
    });

    //【批量修改出价】（对“竞价”列进行修改）
    $('#btnBatchModifyBidding').click(function () {
        var checkbox = $('#result_header input[type=checkbox]');
        if (checkbox.length <= 0) return;

        var bidding = $('#inputBatchBidding').val();
        bidding = parseFloat(bidding);
        if (bidding > 0) {
            $('#results_body tr').each(function () {
                var tr = this;
                var one = tr.changedCampainData;
                one.bidding = bidding;

                var tds = $(tr).find('td');
                if (tds.length > 9) {
                    var td = tds[8];
                    if ($(td).hasClass('editable')) {
                        var t = $(td).text();
                        if (t != bidding) {
                            $(td).text(bidding);
                            if (bidding != tr.origCampaignData.bidding / 100) {
                                $(td).addClass('changed');
                            } else {
                                $(td).removeClass('changed');
                            }
                        }
                    }
                }
            });
        }
    });
}

function bindQueryZero() {
    $('#btnCloseZero').click(function () {
        var yes = confirm("小心啊，确认关闭吗？");
        if (yes) {
            var startTime = $('#inputStartTime').val();
            var endTime = $('#inputEndTime').val();

            var costOp = $('#selectCostOp').val();
            var conversionOp = $('#selectConversionOp').val();
            var cost = $('#inputCostRate').val();
            var conversion = $('#inputConversion').val();

            $.post("query_zero/close", {
                startTime: startTime,
                endTime: endTime,
                costOp: costOp,
                conversionOp: conversionOp,
                cost: cost,
                conversion: conversion
            }, function (data) {
                if (data && data.ret == 1) {
                    admanager.showCommonDlg("提示", "提交任务成功");
                } else {
                    admanager.showCommonDlg("错误", data.message);
                }
            }, 'json');
        }
    });
    $('#btnQueryZero').click(function () {
        var startTime = $('#inputStartTime').val();
        var endTime = $('#inputEndTime').val();

        var costOp = $('#selectCostOp').val();
        var conversionOp = $('#selectConversionOp').val();
        var cost = $('#inputCostRate').val();
        var conversion = $('#inputConversion').val();

        $.post("query_zero/query", {
            startTime: startTime,
            endTime: endTime,
            costOp: costOp,
            conversionOp: conversionOp,
            cost: cost,
            conversion: conversion,
        }, function (data) {
            if (data && data.ret == 1) {
                $('#result_header').html("<tr><th>系列ID</th><th>广告账号ID</th><th>系列名称</th><th>创建时间<span sorterId=\"1\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>状态<span sorterId=\"2\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>预算<span sorterId=\"3\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>竞价<span sorterId=\"4\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>总花费<span sorterId=\"5\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>总安装<span sorterId=\"6\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>总点击<span sorterId=\"7\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CPA<span sorterId=\"8\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CTR<span sorterId=\"9\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CVR<span sorterId=\"10\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th></tr>");
                data = data.data;
                setData(data);
                bindSortOp();

                var now = new Date();
                var six = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 16, 0, 0);
                var estimatedCost = ((six - now) / 1000) * (data.total_spend / (86400 - (six - now) / 1000)) + data.total_spend;
                var str = "<span style='color:red;'>只算还在开启状态的系列</span>" + " 总预算: " + data.total_bugdet + " 总花费: " + data.total_spend + " <span style='color:red'>预计花费: " + estimatedCost + "</span> 总安装: " + data.total_installed +
                    " 总展示: " + data.total_impressions + " 总点击: " + data.total_click +
                    " CTR: " + data.total_ctr + " CPA: " + data.total_cpa + " CVR: " + data.total_cvr;
                str += "<br/><span class='estimateResult'></span>"
                $('#total_result').html(str);
                $('#total_result').removeClass("editable");
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        }, "json");
    });
}

//由“标签”刷新路径
$("#updateAppMaterialPath").click(function () {
    var app_name_for_path = $("#inputSearch").val();
    if (!app_name_for_path || "" === app_name_for_path) {
        admanager.showCommonDlg("提示", "标签为必录信息!");
        return;
    }
    $("#updateAppMaterialPath").prop("disabled", true);
    $.post("update_app_material_path_rel", {
        app_name: app_name_for_path
    }, function (data) {
        if (data.err == 0) {
            admanager.showCommonDlg("提示", data.message);
            setTimeout(function () {
                $("#common_message_dialog").modal("hide");
            }, 1500);
        } else {
            admanager.showCommonDlg("提示", data.message);
        }
        $("#updateAppMaterialPath").prop("disabled", false);
    }, "json");
});

init();
bindQueryZero();
bindBatchModifyOperation();
