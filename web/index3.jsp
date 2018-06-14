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
        .danhuangse{
            color: #ffb74d;
        }
        .td_left_border {
            border-left: #daff92 solid 1px;
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

    <div class="panel panel-default" style="margin-top: 10px"  id="panel_title">
        <form class="form-inline">
            <div class="form-group form-group-sm panel-heading">
                <label class="control-label" for="inputStartTime">开始日期</label>
                <input class="form-control" type="text" value="2012-05-15" id="inputStartTime" readonly>
                <label class="control-label" for="inputEndTime">结束日期</label>
                <input class="form-control" type="text" value="2012-05-15" id="inputEndTime" readonly>
                <input class="form-control" type="checkbox" name="adnetwork" id="facebookCheck"/>
                <label class="control-label" for="facebookCheck">只显示Facebook</label>
                <input class="form-control" type="checkbox" name="adnetwork" id="adwordsCheck"/>
                <label class="control-label" for="adwordsCheck">只显示AdWords</label>
                <button id="btnSummary" class="btn btn-success">汇总数据</button>
            </div>
            <div class="form-group form-group-sm panel-heading">
                <label class="control-label" for="inputSearch">标签</label>
                <div class="input-group input-group-sm">
                    <input id="inputSearch" class="form-control" type="text"/>
                    <span class="input-group-btn">
                    <button id="updateAppMaterialPath" class="btn btn-warning glyphicon glyphicon-refresh" type="button">路径</button>
                  </span>
                <%--<input type="text" class="form-control">--%>
                </div>
                <label class="control-label" for="inputCountry">国家</label>
                <input id="inputCountry" class="form-control" type="text"/>
                <input class="form-control" type="checkbox" id="countryCheck"/><label for="countryCheck">细分到国家</label>
                <label class="control-label" for="inputCampaignCreateTime">系列创建时间</label>
                <input class="form-control" type="text" id="inputCampaignCreateTime"  placeholder="ChinaTime">
                <label class="control-label" for="inputLikeCampaignName">系列名称</label>
                <input class="form-control" type="text" id="inputLikeCampaignName" />
            </div>
            <div class="form-group form-group-sm panel-heading">
                <label class="control-label" for="totalInstallOperator">总安装</label>
                <select class="form-control" id="totalInstallOperator">
                    <option value="1" selected="true">大于</option>
                    <option value="2">小于</option>
                    <option value="3">等于</option>
                </select>
                <input class="form-control" id="inputTotalInstallComparisonValue" type="text"/>

                <label class="control-label" for="cpaOperator">CPA</label>
                <select class="form-control" id="cpaOperator">
                    <option value="4" selected="true">大于</option>
                    <option value="5">小于</option>
                    <option value="6">等于</option>
                </select>
                <input class="form-control" id="inputCpaComparisonValue" type="text" />

                <label class="control-label" for="biddingOperator">竞价</label>
                <select class="form-control" id="biddingOperator">
                    <option value="7" selected="true">大于</option>
                    <option value="8">小于</option>
                    <option value="9"  selected="true">等于</option>
                </select>
                <input class="form-control" id="inputBiddingComparisonValue" type="text"/>
                <input class="form-control" type="checkbox" name="filtrateCampaign" id="containsNoDataCampaignCheck"/>
                <label class="control-label" for="containsNoDataCampaignCheck">包含无数据的系列</label>
            </div>

            <div class="form-group form-group-sm panel-heading">
                <button id="btnSearch" class="btn btn-info glyphicon glyphicon-search"></button> |
                <button id="btnQueryNoData" class="btn btn-info">查询无数据的系列</button>
            </div>
            <div class="panel-heading">
                <label for="selectCostOp">花费比例</label>
                <select class="form-control" id="selectCostOp">
                    <option value="1">大于等于</option>
                    <option value="2" selected="true">小于等于</option>
                </select>
                <input id="inputCostRate" class="form-control" style="display: inline; width: auto;" type="text" value="0.5"/>
                <label for="selectConversionOp">转化</label>
                <select class="form-control" id="selectConversionOp">
                    <option value="1">大于等于</option>
                    <option value="2" selected="true">小于等于</option>
                </select>
                <input id="inputConversion" class="form-control" type="text" value="1"/>

                <button id="btnQueryZero" class="btn btn-danger">查询Zero</button>&nbsp;&nbsp;
            </div>

            <div class="form-group form-group-sm panel-heading">
                <label class="control-label" for="inputBatchBidding">出价</label>
                <input class="form-control" id="inputBatchBidding" type="text" value="0"/>

                <button id="btnBatchModifyBidding" class="btn btn-default">批量修改出价</button> |
                <button id="btnModifyBatch" class="btn btn-default">批量修改</button> |
                <button type="button" class="btn btn-default" id="btnBatchChangeStatus" >修改状态监控</button>
            </div>
        </form>
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
<script src="js/index.js?t=20180614"></script>

<script>
    $("li[role='presentation']:eq(0)").addClass("active");
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
