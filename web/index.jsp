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
        .red {
            color: red;
        }
        .blue {
            color: #0f0;
        }
        .yellow{
            color: #ffa17a;
        }
        .ens{
            color: #bdf7ff;
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
        <li role="presentation"><a href="campaigns_update_daily_log.jsp">日更记录</a></li>
        <li role="presentation"><a href="country_analysis_report.jsp">分析报告</a></li>
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
        <li role="presentation"><a href="summary.jsp">七天汇总</a></li>
        <li role="presentation"><a href="advert_conversions_insert.jsp">转化录入</a></li>


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
            <input type="text" id="inputCampaignCreateTime"  placeholder="ChinaTime">
            <span>系列名称</span>
            <input type="text" id="inputLikeCampaignName" />

            <span>总安装</span>
            <select id="totalInstallOperator"><option value="1" selected="true">大于</option><option value="2">小于</option><option value="3">等于</option></select>
            <input id="inputTotalInstallComparisonValue" class="form-control" style="display: inline; width: auto;" type="text"/>

            <span>CPA</span>
            <select id="cpaOperator"><option value="4" selected="true">大于</option><option value="5">小于</option><option value="6">等于</option></select>
            <input id="inputCpaComparisonValue" class="form-control" style="display: inline; width: auto;" type="text" />

            <span>竞价等于</span>
            <%--<select id="biddingOperator"><option value="7" selected="true">大于</option><option value="8">小于</option><option value="9"  selected="true">等于</option></select>--%>
            <input id="inputBiddingComparisonValue" class="form-control" style="display: inline; width: auto;" type="text" />

            <button id="btnSearch" class="btn btn-default">条件查找</button><br>

            <input type="checkbox" name="adnetwork" id="facebookCheck"/><label for="facebookCheck">只显示Facebook</label>
            <input type="checkbox" name="adnetwork" id="adwordsCheck"/><label for="adwordsCheck">只显示AdWords</label>
            <input type="checkbox" id="countryCheck"/><label for="countryCheck">细分到国家</label>
            <input type="checkbox" name="filtrateCampaign" id="containsNoDataCampaignCheck"/><label for="containsNoDataCampaignCheck">包含无数据的系列</label>
            <%--<input type="checkbox" name="filtrateCampaign" id="onlyQueryNoDataCampaignCheck"/><label for="onlyQueryNoDataCampaignCheck">只查询无数据的系列</label>--%>

            <div>
                <label>花费比例</label><select id="selectCostOp"><option value="1">大于等于</option><option value="2" selected="true">小于等于</option></select>
                <input id="inputCostRate" class="form-control" style="display: inline; width: auto;" type="text" value="0.5"/>
                <label>转化</label><select id="selectConversionOp"><option value="1">大于等于</option><option value="2" selected="true">小于等于</option></select>
                <input id="inputConversion" class="form-control" style="display: inline; width: auto;" type="text" value="1"/>
                <button id="btnQueryZero" class="btn btn-default">查询Zero</button>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

                <button id="btnSummary" class="btn btn-default">汇总数据</button>
                <button id="btnModifyBatch" class="btn btn-default">批量修改</button>
                <button id="btnQueryNoData" class="btn btn-default">查询无数据的系列</button>
                <input type="button" class="btn btn-default" id="btnBatchChangeStatus" value="修改状态监控"/>
            </div>

            <div>
                <label>出价</label>
                <input id="inputBatchBidding" class="form-control" style="display: inline; width: auto;" type="text" value="0"/>
                <button id="btnBatchModifyBidding" class="btn btn-default">批量修改出价</button>
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
            <th>总营收</th>
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
<script src="js/index.js?t=20180123"></script>

<script>
    var data = <%=array.toString()%>;
    $("#inputSearch").autocomplete({
        source: data
    });

    $('#btnBatchChangeStatus').click(function() {
        popupCenter("batch_change_status.jsp", "修改状态监控", 600, 480);
    });
</script>
</body>
</html>
