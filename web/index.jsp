<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>首页</title>
    <link rel="stylesheet" href="bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="bootstrap/css/bootstrap-theme.min.css"/>
    <link rel="stylesheet" href="css/core.css"/>
    <link rel="stylesheet" href="css/bootstrap-tagsinput.css"/>
    <link rel="stylesheet" href="css/bootstrap-datetimepicker.css"/>
    <link rel="stylesheet" href="jqueryui/jquery-ui.css"/>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.4/css/select2.min.css" rel="stylesheet" />

    <style>
        td.editable {

        }
        td.editable.checkbox {

        }
        td.changed {
            background-color: #0f0;
        }
        #total_result.editable {
            background-color: yellow;
        }
        .estimateResult {
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
    <ul class="nav nav-pills">
        <li role="presentation" class="active"><a href="#">首页</a></li>
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
        <li role="presentation"><a href="country_revenue_spend.jsp">国家收支</a></li>
        <li role="presentation"><a href="temp_index.jsp">临时用的</a></li>
        <li role="presentation"><a href="temp_index2.jsp">临时用的2</a></li>

    </ul>

    <div class="panel panel-default" style="margin-top: 10px">
        <div class="panel-heading" id="panel_title">
            <span>开始时间</span>
            <input type="text" value="2012-05-15" id="inputStartTime" readonly>
            <span>结束时间</span>
            <input type="text" value="2012-05-15" id="inputEndTime" readonly>
            <span>标签</span>
            <input id="inputSearch" class="form-control" style="display: inline; width: auto;" type="text"/>
            <span>国家</span>
            <input id="inputCountry" class="form-control" style="display: inline; width: auto;" type="text"/>
            <span>系列创建时间</span>
            <input type="text" value="2017-01-01" id="inputCampaignCreateTime" readonly>
            <button id="btnSearch" class="btn btn-default">条件查找</button>
            <button id="btnSummary" class="btn btn-default">汇总数据</button>
            <button id="btnModifyBatch" class="btn btn-default">批量修改</button>


            <div>
                <input type="checkbox" name="adnetwork" id="facebookCheck"/><label for="facebookCheck">只显示Facebook</label>
                <input type="checkbox" name="adnetwork" id="adwordsCheck"/><label for="adwordsCheck">只显示AdWords</label>
                <input type="checkbox" id="countryCheck"/><label for="countryCheck">细分到国家</label>
                <input type="button" class="btn btn-default" id="btnBatchChangeStatus" value="修改状态监控"/>
                <input id="inputQueryByCampaignNameText" type="text"/>
                <button id="btnQueryByCampaignName" class="btn btn-default">系列名称查询</button>
            </div>

            <div>
                <label>花费比例</label><select id="selectCostOp"><option value="1">大于等于</option><option value="2" selected="true">小于等于</option></select>
                <input id="inputCostRate" class="form-control" style="display: inline; width: auto;" type="text" value="0.5"/>
                <label>转化</label><select id="selectConversionOp"><option value="1">大于等于</option><option value="2" selected="true">小于等于</option></select>
                <input id="inputConversion" class="form-control" style="display: inline; width: auto;" type="text" value="1"/>
                <button id="btnQueryZero" class="btn btn-default">查询Zero</button>
                <button id="btnCloseZero" class="btn btn-default">关闭这些</button>
            </div>
        </div>
    </div>
    <div class="panel panel-default">
        <div class="panel-body" id="total_result">
        </div>
    </div>
    <table class="table table-hover">
        <thead id="result_header">
        <tr>
            <th>系列ID</th>
            <th>广告账号ID</th>
            <th>系列名称</th>
            <th>创建时间</th>
            <th>状态</th>
            <th>预算</th>
            <th>竞价</th>
            <th>总花费</th>
            <th>总安装</th>
            <th>总点击</th>
            <th>CPA</th>
            <th>CTR</th>
            <th>CVR</th>
            <th>修改</th>
        </tr>
        </thead>
        <tbody id="results_body">
        </tbody>
    </table>

    <div style="text-align: center">
        <input id="btnModifySubmit" type="button" class="btn btn-primary" value="提交修改"/>
    </div>
</div>

<div id="new_campaign_dlg" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="dlg_title">修改系列</h4>
            </div>
            <div class="modal-body">
                <form id="modify_form" class="form-horizontal" action="#" autocomplete="off">
                    <div class="form-group">
                        <label for="inputCampaignName" class="col-sm-2 control-label">系列名称</label>
                        <div class="col-sm-10">
                            <input class="form-control" id="inputCampaignName" placeholder="系列名称" autocomplete="off">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="inputStatus" class="col-sm-2 control-label">是否开启</label>
                        <div class="col-sm-10">
                            <input type="checkbox" id="inputStatus">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="inputBudget" class="col-sm-2 control-label">预算</label>
                        <div class="col-sm-10">
                            <input type="number" class="form-control" id="inputBudget" placeholder="预算" autocomplete="off">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="inputBidding" class="col-sm-2 control-label">竞价</label>
                        <div class="col-sm-10">
                            <input type="number" class="form-control" id="inputBidding" placeholder="竞价" autocomplete="off">
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

</div>

<jsp:include page="loading_dialog.jsp"></jsp:include>


<script src="js/jquery.js"></script>
<script src="bootstrap/js/bootstrap.min.js"></script>
<script src="js/core.js"></script>
<script src="js/bootstrap-datetimepicker.js"></script>
<script src="jqueryui/jquery-ui.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.4/js/select2.min.js"></script>
<script src="js/country-name-code-dict.js"></script>
<script src="js/index.js"></script>
<script src="js/index.js?t=20171128"></script>

<script>
    var data = <%=array.toString()%>;
    $("#inputSearch").autocomplete({
        source: data
    });

    $('#btnBatchChangeStatus').click(function() {
        popupCenter("batch_change_status.jsp", "修改状态监控", 600, 480);
    });


    $('#btnQueryByCampaignName').click(function () {
        var  likeCampaignName = $("#inputQueryByCampaignNameText").val();
        var query = $("#inputSearch").val();
        var startTime = $('#inputStartTime').val();
        var endTime = $('#inputEndTime').val();
        var countryName = $('#inputCountry').val();
        var countryCode = '';
        var adwordsCheck = $('#adwordsCheck').is(':checked');
        var countryCheck = $('#countryCheck').is(':checked');
        var facebookCheck = $('#facebookCheck').is(':checked');
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
            likeCampaignName: likeCampaignName
        }, function (data) {
            if (data && data.ret == 1) {
                appQueryData = data.data.array;
                $.post('query', {
                    tag: query,
                    startTime: startTime,
                    endTime: endTime,
                    adwordsCheck: adwordsCheck,
                    countryCheck: countryCheck,
                    facebookCheck: facebookCheck,
                    countryCode: countryCode,
                    likeCampaignName: likeCampaignName
                }, function (data) {
                    if (data && data.ret == 1) {
                        if (countryCheck) {
                            $('#result_header').html("<tr><th>国家</th><th>总展示</th><th>总花费</th><th>总安装</th><th>总点击</th><th>CPA</th><th>CTR</th><th>CVR</th></tr>");
                        } else if (adwordsCheck) {
                            $('#result_header').html("<tr><th>系列ID</th><th>广告账号ID</th><th>系列名称</th><th>创建时间<span sorterId=\"1\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>状态<span sorterId=\"2\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>预算<span sorterId=\"3\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>竞价<span sorterId=\"4\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>总花费<span sorterId=\"5\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>总安装<span sorterId=\"6\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>总点击<span sorterId=\"7\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CPA<span sorterId=\"8\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CTR<span sorterId=\"9\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CVR<span sorterId=\"10\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th></tr>");
                        } else {
                            $('#result_header').html("<tr><th>系列ID</th><th>广告账号ID</th><th>系列名称</th><th>创建时间<span sorterId=\"1\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>状态<span sorterId=\"2\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>预算<span sorterId=\"3\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>竞价<span sorterId=\"4\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>总花费<span sorterId=\"5\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>总安装<span sorterId=\"6\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>总点击<span sorterId=\"7\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CPA<span sorterId=\"8\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CTR<span sorterId=\"9\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CVR<span sorterId=\"10\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th></tr>");
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
                }, 'json');
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        }, 'json');
    });
</script>
<script src="js/interlaced-color-change.js"></script>
</body>
</html>
