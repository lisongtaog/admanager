var countryMapFunction = function () {
    var map = {};
    for(var i = 0;i<regionList.length;i++){
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
$("#new_campaign_dlg .btn-primary").click(function() {
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
function init() {

    var now = new Date(new Date().getTime() - 86400 * 1000);
    $('#inputStartTime').val(now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate());
    $('#inputEndTime').val(now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate());
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

    $("input:checkbox").on('click', function() {
        var $box = $(this);
        if ($box.is(":checked")) {
            var group = "input:checkbox[name='" + $box.attr("name") + "']";
            $(group).prop("checked", false);
            $box.prop("checked", true);
        } else {
            $box.prop("checked", false);
        }
    });

    $("#btnQueryNoData").click(function(){
        var query = $("#inputSearch").val();
        var startTime = $('#inputStartTime').val();
        var endTime = $('#inputEndTime').val();
        var adwordsCheck = $('#adwordsCheck').is(':checked');
        var facebookCheck = $('#facebookCheck').is(':checked');

        $.post('query_one/query_not_has_data', {
            tag: query,
            startTime: startTime,
            endTime: endTime,
            adwordsCheck: adwordsCheck,
            facebookCheck: facebookCheck
        },function(data){
            if(data && data.ret == 1){
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
        },'json');
    });
    $('#btnSearch').click(function () {
        var query = $("#inputSearch").val();
        var startTime = $('#inputStartTime').val();
        var endTime = $('#inputEndTime').val();
        var campaignCreateTime = $('#inputCampaignCreateTime').val();
        var countryCode = '';
        var adwordsCheck = $('#adwordsCheck').is(':checked');
        var countryCheck = $('#countryCheck').is(':checked');
        var facebookCheck = $('#facebookCheck').is(':checked');
        var countryName = $('#inputCountry').val();
        if(countryName != ""){
            for (var i = 0; i < regionList.length; i++) {
                if (countryName == regionList[i].name) {
                    countryCode = regionList[i].country_code;
                    break;
                }
            }
        }

        $.post('query', {
                tag: query,
                startTime: startTime,
                endTime: endTime,
                adwordsCheck: adwordsCheck,
                countryCheck: countryCheck,
                facebookCheck: facebookCheck,
                countryCode: countryCode,
                campaignCreateTime: campaignCreateTime
            },function(data){
                if(data && data.ret == 1){
                    appQueryData = data.data.array;
                    if (countryCheck) {
                        $('#result_header').html("<tr><th>国家</th><th>总展示<span sorterId=\"21\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>总花费<span sorterId=\"22\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>总安装<span sorterId=\"23\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>总点击<span sorterId=\"24\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CPA<span sorterId=\"25\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CTR<span sorterId=\"26\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CVR<span sorterId=\"27\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>ROI<span sorterId=\"28\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th></tr>");
                    } else if (adwordsCheck) {
                        $('#result_header').html("<tr><th>系列ID</th><th>账户ID</th><th>账户简称</th><th>系列名称</th><th>创建时间<span sorterId=\"1\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>状态<span sorterId=\"2\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>预算<span sorterId=\"3\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>竞价<span sorterId=\"4\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>总花费<span sorterId=\"5\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>总安装<span sorterId=\"6\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>总点击<span sorterId=\"7\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CPA<span sorterId=\"8\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CTR<span sorterId=\"9\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CVR<span sorterId=\"10\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>ROI<span sorterId=\"11\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th></tr>");
                    } else {
                        $('#result_header').html("<tr><th>系列ID</th><th>账户ID</th><th>账户简称</th><th>系列名称</th><th>创建时间<span sorterId=\"1\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>状态<span sorterId=\"2\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>预算<span sorterId=\"3\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>竞价<span sorterId=\"4\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>总花费<span sorterId=\"5\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>总安装<span sorterId=\"6\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>总点击<span sorterId=\"7\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CPA<span sorterId=\"8\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CTR<span sorterId=\"9\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CVR<span sorterId=\"10\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>ROI<span sorterId=\"11\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th></tr>");
                    }
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
            },'json');
        countryRevenueSpendReturn = "true";
    });


    strFullPath = window.document.location.href;
    if(strFullPath.indexOf("?") != -1){
        strFullPath = strFullPath.substr(strFullPath.indexOf("?")+1,strFullPath.length);
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
        var query = $("#inputSearch").val();
        var startTime = $('#inputStartTime').val();
        var endTime = $('#inputEndTime').val();
        var adwordsCheck = $('#adwordsCheck').is(':checked');
        var facebookCheck = $('#facebookCheck').is(':checked');

        $.post('query', {
            tag: query,
            summary: true,
            startTime: startTime,
            endTime: endTime,
            adwordsCheck: adwordsCheck,
            facebookCheck: facebookCheck,
        }, function (data) {
            if (data && data.ret == 1) {
                var keyset = ["name", "total_spend", "total_revenue", "total_installed", "total_impressions", "total_click",
                    "total_ctr", "total_cpa", "total_cvr"];
                if(adwordsCheck || facebookCheck){
                    $('#result_header').html("<tr><th>应用名称</th><th>总花费</th><th>总安装</th><th>总展示</th><th>总点击</th><th>CTR</th><th>CPA</th><th>CVR</th></tr>");
                    keyset = ["name", "total_spend", "total_installed", "total_impressions", "total_click",
                        "total_ctr", "total_cpa", "total_cvr"];
                }else{
                    $('#result_header').html("<tr><th>应用名称</th><th>总花费</th><th>总营收</th><th>总安装</th><th>总展示</th><th>总点击</th><th>CTR</th><th>CPA</th><th>CVR</th></tr>");
                }

                data = data.data;

                var total_spend = 0;
                var total_revenue = 0;
                var total_installed = 0;
                var total_impressions = 0;
                var total_click = 0;
                var total_ctr = 0;
                var total_cpa = 0;
                var total_cvr = 0;

                $('#results_body > tr').remove();
                for (var i = 0; i < data.length; i++) {
                    var one = data[i];
                    var tr = $('<tr></tr>');
                    for (var j = 0; j < keyset.length; j++) {
                        var td = $('<td></td>');
                        td.text(one[keyset[j]]);
                        tr.append(td);
                    }
                    total_spend += one['total_spend'];
                    total_revenue += one['total_revenue'];
                    total_installed += one['total_installed'];
                    total_impressions += one['total_impressions'];
                    total_click += one['total_click'];

                    total_ctr = total_impressions > 0 ? total_click / total_impressions : 0;
                    total_cpa = total_installed > 0 ? total_spend / total_installed : 0;
                    total_cvr = total_click > 0 ? total_installed / total_click : 0;
                    $('#results_body').append(tr);
                }
                if(adwordsCheck || facebookCheck){
                    var str = "总花费: " + total_spend + " 总安装: " + total_installed +
                        " 总展示: " + total_impressions + " 总点击: " + total_click +
                        " CTR: " + total_ctr + " CPA: " + total_cpa + " CVR: " + total_cvr;
                    $('#total_result').text(str);
                }else{
                    var str = "总花费: " + total_spend + "  总营收: " + total_revenue + " 总安装: " + total_installed +
                        " 总展示: " + total_impressions + " 总点击: " + total_click +
                        " CTR: " + total_ctr + " CPA: " + total_cpa + " CVR: " + total_cvr;
                    $('#total_result').text(str);
                }

                $('#total_result').removeClass("editable");
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        }, 'json');
    });
}

function setData(data) {
    $('#results_body > tr').remove();
    for (var i = 0; i < data.array.length; i++) {
        var one = data.array[i];
        var tr = $('<tr></tr>');
        var countryCheck = $('#countryCheck').is(':checked');
        var keyset = ["campaign_id", "account_id", "short_name", "campaign_name", "create_time",
            "status", "budget", "bidding", "spend", "installed", "click", "cpa", "ctr", "cvr","roi"];
        var modifyColumns = ["campaign_name", "budget", "bidding"];
        if (countryCheck) {
            keyset = ["country_name",
                "impressions","spend", "installed", "click", "cpa", "ctr", "cvr","roi"];
        }
        for (var j = 0; j < keyset.length; j++) {
            var campaignId = one['campaign_id'];
            var totalSpend = 0;
            for (var jj = 0; jj < appQueryData.length; jj++) {
                if (appQueryData[jj]['campaign_id'] == campaignId) {
                    var countryName = $('#inputCountry').val();
                    if(countryName != ""){
                        totalSpend = appQueryData[jj]['campaign_spends'];
                    }else{
                        totalSpend = appQueryData[jj]['spend'];
                    }
                    break;
                }
            }
            var td = $('<td></td>');
            if (keyset[j] == 'budget' || keyset[j] == 'bidding') {
                td.text(one[keyset[j]] / 100);
            } else {
                if (keyset[j] == 'spend') {
                    td.text(one[keyset[j]] + " / " + totalSpend);
                } else if(keyset[j] == 'roi'){
                    var r = one[keyset[j]];
                    if(r <0){
                        td.addClass("red");
                    }else if(r>0){
                        td.addClass("blue");
                    }else{
                        td.addClass("yellow");
                    }
                    td.text(r);
                }else{
                    td.text(one[keyset[j]]);
                }
            }
            if (modifyColumns.indexOf(keyset[j]) != -1) {
                td.addClass("editable");
                td[0].cloumnName = keyset[j];
            }
            tr.append(td);
        }
        tr[0].origCampaignData = one;
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
    $(".link_modify").click(function() {
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

    $(".link_copy").click(function() {
        var tds = $(this).parents("tr").find('td');
        $.post('campaign/find_create_data', {
            campaignId: $(tds.get(0)).text(),
        }, function (data) {
            if (data && data.ret == 1) {
                var list = [];
                var keys = ["tag_name", "app_name", "facebook_app_id", "account_id", "country_region",
                    "language","age", "gender", "detail_target", "campaign_name", "page_id", "bugdet", "bidding", "max_cpa", "title", "message"];
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

function bindSortOp() {
    $('.sorter').click(function() {
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

        var query = $("#inputSearch").val();
        var startTime = $('#inputStartTime').val();
        var endTime = $('#inputEndTime').val();
        var countryName = $('#inputCountry').val();
        var countryCode = '';
        var adwordsCheck = $('#adwordsCheck').is(':checked');
        var countryCheck = $('#countryCheck').is(':checked');
        var facebookCheck = $('#facebookCheck').is(':checked');
        var  likeCampaignName = $("#inputQueryByCampaignNameText").val();
        for (var i = 0; i < regionList.length; i++) {
            if (countryName == regionList[i].name) {
                countryCode = regionList[i].country_code;
                break;
            }
        }

        $.post('query', {
            tag: query,
            startTime: startTime,
            endTime: endTime,
            adwordsCheck: adwordsCheck,
            countryCheck: countryCheck,
            facebookCheck: facebookCheck,
            countryCode: countryCode,
            sorterId: sorterId,
            likeCampaignName: likeCampaignName
        }, function (data) {
            if (data && data.ret == 1) {
                data = data.data;
                setData(data);
                var str = "总花费: " + data.total_spend + " 总安装: " + data.total_installed +
                            " 总展示: " + data.total_impressions + " 总点击: " + data.total_click +
                            " CTR: " + data.total_ctr + " CPA: " + data.total_cpa + " CVR: " + data.total_cvr;

                str += "<br/><span class='estimateResult'></span>"
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

    $('#results_body tr').each(function() {
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
    $('#results_body tr').each(function() {
        if (!this.changedCampainData) return;
        if (this.changedCampainData.budget > 0 && this.changedCampainData.budget != this.origCampaignData.budget) {
            total_spend += (this.changedCampainData.budget * 100 - this.origCampaignData.budget) / this.origCampaignData.budget * this.origCampaignData.spend;
            var count = (this.changedCampainData.budget * 100 - this.origCampaignData.budget) / this.origCampaignData.budget  * this.origCampaignData.installed;
            total_installed += parseInt(count);
        }
    });

    total_ctr = total_impressions > 0 ? total_click / total_impressions : 0;
    total_cpa = total_installed > 0 ? total_spend / total_installed : 0;
    total_cvr = total_click > 0 ? total_installed / total_click : 0;

    var str = "总花费: " + total_spend + " 总安装: " + total_installed +
        " 总展示: " + total_impressions + " 总点击: " + total_click +
        " CTR: " + total_ctr + " CPA: " + total_cpa + " CVR: " + total_cvr;

    $('.estimateResult').text(str);
}

function bindBatchModifyOperation() {
    $(document).click(function() {
        $('#results_body td.editable').removeClass('editing');
        $(".inputTemp").replaceWith(function() {
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
                        tr.changedCampainData.bidding = this.value;
                        break;
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

    $('#btnModifyBatch').click(function() {
        var checkbox = $('#result_header input[type=checkbox]');
        if (checkbox.length > 0) return;

        $('#result_header tr th:first').before($("<th><input id='ckSelectAll' type='checkbox' /></th>"));
        $('#results_body tr').each(function() {
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
        $('.ckEnableCampaign').change(function() {
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
        $('#ckSelectAll').change(function() {
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
            $(currentEle).html('<input class="inputTemp" type="text" width="2" value="' + value + '" />');
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
                .click(function(e) {
                e.stopPropagation();
            });
        }
    });

    $('#btnModifySubmit').click(function() {
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

        $('#results_body td.changed').each(function() {
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
}

function bindQueryZero() {
    $('#btnCloseZero').click(function() {
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
            }, function(data) {
                if (data && data.ret == 1) {
                    admanager.showCommonDlg("提示", "提交任务成功");
                } else {
                    admanager.showCommonDlg("错误", data.message);
                }
            }, 'json');
        }
    });
    $('#btnQueryZero').click(function() {
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
        }, function(data) {
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

init();
bindQueryZero();
bindBatchModifyOperation();

if(countryRevenueSpendReturn == "false"){
    $('#btnSummary').click();
}
