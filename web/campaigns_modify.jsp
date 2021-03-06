<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.bestgo.admanager.utils.NumberUtil" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/rootBase.jsp" %>

<html>
<head>
    <title>修改自动创建广告</title>
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

    String type = request.getParameter("type");

    //以下是从 campaigns_auto_create.jsp 传来的参数
    boolean isAutoCreate = "auto_create".equals(type);
    String network = request.getParameter("network");
    ArrayList<String> networks = new ArrayList<>();
    networks.add("facebook");
    networks.add("adwords");
    if (networks.indexOf(network) == -1) {
        network = "facebook";
    }
    int recordId = NumberUtil.parseInt(request.getParameter("id"), 0);

    //以下接收的是从index2传来的页面
    String campaignId = request.getParameter("campaignId");
    String budget = request.getParameter("budget");
    String bidStrategy = request.getParameter("bidStrategy");
    String bidding = request.getParameter("bidding");
    boolean isIndexCreate = "auto_create".equals(type);
%>

<div class="container-fluid">
    <%@include file="common/navigationbar.jsp" %>

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
            <input type="button" class="btn btn-default" id="btnCampaignStatus" value="创建状态监控"/>
            <a href="campaigns_auto_create.jsp" target="_blank">自动创建系列管理</a>
        </div>
    </div>
    <!-- 以下是个模态框用于批量输入国家 -->
    <div id="moreCountryDlg" class="modal fade" tabindex="-1" role="dialog">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                            aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title" id="dlg_title">输入国家，每行一个</h4>
                </div>
                <div class="modal-body">
                    <form id="modify_form" class="form-horizontal" action="#" autocomplete="off">
                        <div class="form-group">
                            <label for="inputCustomCountryPart" class="col-sm-2 control-label">自定义字段</label>
                            <div class="col-sm-10">
                                <input id="inputCustomCountryPart" style="width:100%;"/>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="textareaCountry" class="col-sm-2 control-label">国家</label>
                            <div class="col-sm-10">
                                <textarea id="textareaCountry" style="width:100%; height:400px;"></textarea>
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
            <div class="col-sm-8">
                <select class="form-control" id="selectApp">
                </select>
            </div>
        </div>


        <div class="form-group">
            <label for="selectAccount" class="col-sm-2 control-label">账号</label>
            <div class="col-sm-8">
                <select class="form-control select2" id="selectAccount" multiple="multiple">
                </select>
            </div>
        </div>

        <div class="form-group">
            <label for="inputCreateCount" class="col-sm-2 control-label">创建数量</label>
            <div class="col-sm-8">
                <input class="form-control" id="inputCreateCount" value="1"/>
            </div>
        </div>

        <div class="form-group">
            <label for="selectFBPage" class="col-sm-2 control-label">FB主页</label>
            <div class="col-sm-8">
                <select class="form-control select2" id="selectFBPage">
                </select>
            </div>
        </div>

        <div class="form-group">
            <label for="selectRegion" class="col-sm-2 control-label">国家地区</label>
            <div class="col-sm-7">
                <select class="form-control select2" id="selectRegion" multiple="multiple">

                </select>
            </div>
            <div class="col-sm-1">
                <input type="button" class="btn-more btn btn-default" id="btnSelectRegionMore" value="批量输入"/>
            </div>
        </div>
        <div class="form-group alert-warning" id="customCountryPartDiv">
            <label for="customCountryPart" class="col-sm-2 control-label">自定义国家字段</label>
            <div class="col-sm-7">
                <input class="form-control" id="customCountryPart"/>
            </div>
        </div>
        <div class="form-group">
            <label for="selectRegionUnselected" class="col-sm-2 control-label">排除国家地区</label>
            <div class="col-sm-7">
                <select class="form-control select2" id="selectRegionUnselected" multiple="multiple">

                </select>
            </div>
            <div class="col-sm-1">
                <input type="button" class="btn-more btn btn-default" id="btnSelectRegionUnselectedMore" value="批量输入"/>
            </div>
        </div>
        <div class="form-group">
            <label for="selectLanguage" class="col-sm-2 control-label">语言</label>
            <div class="col-sm-8">
                <select class="form-control" id="selectLanguage">
                </select>
            </div>
        </div>
        <div class="form-group">
            <label for="inputAge" class="col-sm-2 control-label">年龄</label>
            <div class="col-sm-8">
                <input class="form-control" id="inputAge" type="text"/>
            </div>
        </div>
        <div class="form-group">
            <label for="selectGender" class="col-sm-2 control-label">性别</label>
            <div class="col-sm-8">
                <select class="form-control select2" id="selectGender" multiple="multiple">
                </select>
            </div>
        </div>
        <div class="form-group">
            <label for="inputInterest" class="col-sm-2 control-label">兴趣</label>
            <div class="col-sm-8">
                <input class="form-control" id="inputInterest"/>
            </div>
        </div>
        <div class="form-group">
            <label for="selectUserOs" class="col-sm-2 control-label">用户操作系统</label>
            <div class="col-sm-8">
                <select class="form-control select2" id="selectUserOs" multiple="multiple">
                </select>
            </div>
        </div>
        <div class="form-group">
            <label for="inputUserDevices" class="col-sm-2 control-label">用户设备</label>
            <div class="col-sm-8">
                <input class="form-control" id="inputUserDevices"/>
            </div>
        </div>
        <div class="form-group">
            <label for="selectPublisherPlatforms" class="col-sm-2 control-label">版位</label>
            <div class="col-sm-8">
                <select class="form-control select2" id="selectPublisherPlatforms" multiple="multiple">
                </select>
            </div>
        </div>
        <div class="form-group">
            <label for="inputBudget" class="col-sm-2 control-label">预算</label>
            <div class="col-sm-8">
                <input class="form-control" id="inputBudget"/>
            </div>
        </div>
        <div class="form-group">
            <label for="selectBidStrategy" class="col-sm-2 control-label">竞价策略</label>
            <div class="col-sm-8">
                <select class="form-control" id="selectBidStrategy">
                    <option value="1">TARGET_COST</option>
                    <%--设置 出价 平均费用--%>
                    <option value="2">LOWEST_COST_WITH_BID_CAP</option>
                    <%--需要 设置竞价上限--%>
                </select>
            </div>
        </div>


        <div class="form-group">
            <label for="inputBidding" class="col-sm-2 control-label">应用出价/竞价上限</label>
            <div class="col-sm-8">
                <input class="form-control" id="inputBidding" type="text"/>
            </div>
        </div>

        <%--国家出价/竞价上限--%>
        <div class="form-group">
            <label class="col-sm-2 control-label"></label>
            <div class="col-sm-8">
                <span id="appCountryBidding"></span>
            </div>
        </div>

        <div class="form-group">
            <label for="inputMaxCpa" class="col-sm-2 control-label">关闭价格</label>
            <div class="col-sm-8">
                <input class="form-control" id="inputMaxCpa"/>
            </div>
        </div>
        <div class="form-group">
            <label for="inputImagePath" class="col-sm-2 control-label">图片路径</label>
            <div class="col-sm-8">
                <input class="form-control" id="inputImagePath"/>
            </div>
        </div>
        <div class="form-group">
            <label for="inputVideoPath" class="col-sm-2 control-label">视频路径</label>
            <div class="col-sm-8">
                <input class="form-control" id="inputVideoPath"/>
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-10" style="text-align: center">
                <input type="submit" class="btn btn-primary" style="width: 100px;" id="btnUpdate" value="更新"/>
            </div>
        </div>
        <br>
        <table class="table table-hover" id="advertisement">
            <thead>
            <tr>
                <th><input type="checkbox" id="checkbox_facebook"></th>
                <th>广告语组合</th>
                <th>语言</th>
                <th>广告语标题</th>
                <th>广告语</th>
            </tr>
            </thead>
            <tbody id="tbody_facebook"></tbody>
        </table>
    </form>


    <form class="form-horizontal" action="#" id="formAdmob">
        <div class="form-group">
            <label for="selectAppAdmob" class="col-sm-2 control-label">应用</label>
            <div class="col-sm-8">
                <select class="form-control" id="selectAppAdmob">

                </select>
            </div>
        </div>

        <div class="form-group">
            <label for="selectAccountAdmob" class="col-sm-2 control-label">广告账号</label>
            <div class="col-sm-8">
                <select class="form-control select2" id="selectAccountAdmob" multiple="multiple">
                </select>
            </div>
        </div>
        <div class="form-group">
            <label for="inputCreateCountAdmob" class="col-sm-2 control-label">创建数量</label>
            <div class="col-sm-8">
                <input class="form-control" id="inputCreateCountAdmob" value="1"/>
            </div>
        </div>
        <div class="form-group">
            <label for="selectRegionAdmob" class="col-sm-2 control-label">国家地区</label>
            <div class="col-sm-7">
                <select class="form-control select2" id="selectRegionAdmob" multiple="multiple">
                </select>
            </div>
            <div class="col-sm-1">
                <input type="button" class="btn-more btn btn-default" id="btnSelectRegionAdmobMore" value="批量输入"/>
            </div>
        </div>
        <div class="form-group alert-warning" id="customCountryPartAdmobDiv">
            <label for="customCountryPartAdmob" class="col-sm-2 control-label">自定义国家字段</label>
            <div class="col-sm-7">
                <input class="form-control" id="customCountryPartAdmob"/>
            </div>
        </div>
        <div class="form-group">
            <label for="selectRegionUnselected" class="col-sm-2 control-label">排除国家地区</label>
            <div class="col-sm-7">
                <select class="form-control select2" id="selectRegionUnselectedAdmob" multiple="multiple">
                </select>
            </div>
            <div class="col-sm-1">
                <input type="button" class="btn-more btn btn-default" id="btnSelectRegionUnselectedAdmobMore"
                       value="批量输入"/>
            </div>
        </div>


        <div class="form-group">
            <label for="selectLanguage" class="col-sm-2 control-label">语言</label>
            <div class="col-sm-8">
                <select class="form-control" id="selectLanguageAdmob">
                </select>
            </div>
        </div>

        <div class="form-group">
            <label for="selectIncidentAdmob" class="col-sm-2 control-label">事件</label>
            <div class="col-sm-8">
                <select class="form-control" id="selectIncidentAdmob">
                </select>
            </div>
        </div>

        <div class="form-group">
            <label for="inputBudget" class="col-sm-2 control-label">预算</label>
            <div class="col-sm-8">
                <input class="form-control" id="inputBudgetAdmob"/>
            </div>
        </div>


        <div class="form-group">
            <label for="inputBiddingAdmob" class="col-sm-2 control-label">出价</label>
            <div class="col-sm-8">
                <input class="form-control" id="inputBiddingAdmob"/>
            </div>
        </div>

        <%--国家出价/竞价上限--%>
        <div class="form-group">
            <label class="col-sm-2 control-label"></label>
            <div class="col-sm-8">
                <span id="appCountryBiddingAdWords"></span>
            </div>
        </div>

        <div class="form-group">
            <label for="inputMaxCpa" class="col-sm-2 control-label">关闭价格</label>
            <div class="col-sm-8">
                <input class="form-control" id="inputMaxCpaAdmob"/>
            </div>
        </div>
        <div class="form-group">
            <label for="inputImagePath" class="col-sm-2 control-label">图片路径</label>
            <div class="col-sm-8">
                <input class="form-control" id="inputImagePathAdmob"/>
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-10" style="text-align: center">
                <input type="submit" class="btn btn-primary" style="width: 100px;" id="btnUpdateAdmob" value="更新"/>
            </div>
        </div>
        <br>
        <table class="table table-hover" id="advertisement_admob">
            <thead>
            <tr>
                <th><input type="checkbox" id="checkbox_admob"></th>
                <th>广告语组合</th>
                <th>语言</th>
                <th>广告语1</th>
                <th>广告语2</th>
                <th>广告语3</th>
                <th>广告语4</th>
            </tr>
            </thead>
            <tbody id="tbody_admob"></tbody>
        </table>
    </form>

</div>

</div>


<jsp:include page="common/loading_dialog.jsp"></jsp:include>
<script>
    var isAutoCreate = <%=isAutoCreate%>;
    var modifyNetwork = "<%=network%>";
    var modifyRecordId1 = <%=recordId%>;
    var isIndexCreate = <%=isIndexCreate%>;
    var campaign_id = "<%=campaignId%>";
    var IndexBudget = <%=budget%>;
    var bidStrategy = <%=bidStrategy%>;
    var IndexBidding = <%=bidding%>;

</script>
<script src="js/campaign_modify.js?t=<%=Math.random()%>"></script>

</body>
</html>
