<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.bestgo.admanager.Utils" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>创建广告</title>
    <link rel="stylesheet" href="bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="bootstrap/css/bootstrap-theme.min.css"/>
    <link rel="stylesheet" href="css/core.css"/>
    <link rel="stylesheet" href="css/bootstrap-tagsinput.css"/>
    <link rel="stylesheet" href="css/bootstrap-datetimepicker.css"/>
    <link rel="stylesheet" href="jqueryui/jquery-ui.css"/>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.4/css/select2.min.css" rel="stylesheet" />
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
    boolean isAutoCreate = "auto_create".equals(type);
    String network = request.getParameter("network");
    ArrayList<String> networks = new ArrayList<>();
    networks.add("facebook");
    networks.add("adwords");
    if (networks.indexOf(network) == -1) {
        network = "facebook";
    }
    int recordId = Utils.parseInt(request.getParameter("id"), 0);
%>

<div class="container-fluid">
    <ul class="nav nav-pills">
        <li role="presentation"><a href="index.jsp">首页</a></li>
        <li role="presentation" class="active"><a href="#">创建广告</a></li>
        <li role="presentation"><a href="adaccounts.jsp">广告账号管理</a></li>
        <li role="presentation"><a href="adaccounts_admob.jsp">广告账号管理(AdMob)</a></li>
        <li role="presentation"><a href="campaigns.jsp">广告系列管理</a></li>
        <li role="presentation"><a href="campaigns_admob.jsp">广告系列管理(AdMob)</a></li>
        <li role="presentation"><a href="tags.jsp">标签管理</a></li>
        <li role="presentation"><a href="rules.jsp">规则</a></li>
        <li role="presentation"><a href="query.jsp">查询</a></li>
        <li role="presentation"><a href="system.jsp">系统管理</a></li>
        <li role="presentation"><a href="advert_insert.jsp">广告存储</a></li>
        <li role="presentation"><a href="advert_conversions_insert.jsp">转化录入</a></li>
    </ul>

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
                        <div class="form-group">
                            <label for="inputCampaignName" class="col-sm-2 control-label">国家</label>
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
            <div class="col-sm-10">
                <select class="form-control" id="selectApp">
                </select>
            </div>
        </div>


        <div class="form-group">
            <label for="selectAccount" class="col-sm-2 control-label">账号</label>
            <div class="col-sm-10">
                <select class="form-control select2" id="selectAccount" multiple="multiple">

                </select>
            </div>
        </div>

        <div class="form-group">
            <label for="inputCreateCount" class="col-sm-2 control-label">创建数量</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputCreateCount" />
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
            <div class="col-sm-2">
                <label title="选中后每个选项创建一个或多个广告系列"><input type="checkbox" class="form-check-input" id="selectRegionExplode">分离到系列</label>
            </div>
        </div>
        <div class="form-group">
            <label for="selectRegionUnselected" class="col-sm-2 control-label">排除国家地区</label>
            <div class="col-sm-8">
                <select class="form-control select2" id="selectRegionUnselected" multiple="multiple">

                </select>
            </div>
            <div class="col-sm-1">
                <input type="button" class="btn-more btn btn-default" id="btnSelectRegionUnselectedMore" value="批量输入"/>
            </div>
        </div>
        <div class="form-group">
            <label for="selectLanguage" class="col-sm-2 control-label">语言</label>
            <div class="col-sm-10">
                <select class="form-control" id="selectLanguage">
                </select>
            </div>
        </div>
        <div class="form-group">
            <label for="inputAge" class="col-sm-2 control-label">年龄</label>
            <div class="col-sm-8">
                <input class="form-control" id="inputAge" type="text"/>
            </div>
            <div class="col-sm-2">
                <label title="选中后每个选项创建一个或多个广告系列"><input type="checkbox" class="form-check-input" id="inputAgeExplode">分离到系列</label>
            </div>
        </div>
        <div class="form-group">
            <label for="selectGender" class="col-sm-2 control-label">性别</label>
            <div class="col-sm-8">
                <select class="form-control select2" id="selectGender" multiple="multiple">
                </select>
            </div>

            <div class="col-sm-2">
                <label title="选中后每个选项创建一个或多个广告系列"><input type="checkbox" class="form-check-input" id="selectGenderExplode">分离到系列</label>
            </div>
        </div>
        <div class="form-group">
            <label for="inputInterest" class="col-sm-2 control-label">兴趣</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputInterest" />
            </div>
        </div>
        <div class="form-group">
            <label for="selectUserOs" class="col-sm-2 control-label">用户操作系统</label>
            <div class="col-sm-8">
                <select class="form-control select2" id="selectUserOs" multiple="multiple">
                </select>
            </div>
            <div class="col-sm-2">
                <label title="选中后每个选项创建一个或多个广告系列"><input type="checkbox" class="form-check-input" id="selectUserOsExplode">分离到系列</label>
            </div>
        </div>
        <div class="form-group">
            <label for="inputUserDevices" class="col-sm-2 control-label">用户设备</label>
            <div class="col-sm-8">
                <input class="form-control" id="inputUserDevices" />
            </div>
            <div class="col-sm-2">
                <label title="选中后每个选项创建一个或多个广告系列"><input type="checkbox" class="form-check-input" id="selectUserDevicesExplode">分离到系列</label>
            </div>
        </div>
        <div class="form-group">
            <label for="inputCampaignName" class="col-sm-2 control-label">广告系列名称</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputCampaignName" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputBudget" class="col-sm-2 control-label">预算</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputBudget" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputBidding" class="col-sm-2 control-label">出价</label>
            <div class="col-sm-8">
                <input class="form-control" id="inputBidding" type="text"/>
            </div>

            <div class="col-sm-2">
                <label title="选中后每个选项创建一个或多个广告系列"><input type="checkbox" class="form-check-input" id="inputBiddingExplode">分离到系列</label>
            </div>
        </div>
        <div class="form-group">
            <label for="inputMaxCpa" class="col-sm-2 control-label">关闭价格</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMaxCpa" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputTitle" class="col-sm-2 control-label">广告标题</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputTitle" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessage" class="col-sm-2 control-label">广告语</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessage" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputImagePath" class="col-sm-2 control-label">图片路径</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputImagePath"/>
            </div>
        </div>
        <div class="form-group">
            <label for="inputVideoPath" class="col-sm-2 control-label">视频路径</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputVideoPath" />
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-10" style="text-align: center">
                <label><input type="checkbox" id="checkAutoCreate"/>设置为自动创建</label> <input type="submit" class="btn btn-primary" style="width: 100px;" id="btnCreate" value="创建"/>
                <label><input type="checkbox" id="onlyCheckAutoCreate"/>仅设置为自动创建</label>
            </div>
        </div>
    </form>


    <form class="form-horizontal" action="#" id="formAdmob">
        <div class="form-group">
            <label for="selectApp" class="col-sm-2 control-label">应用</label>
            <div class="col-sm-10">
                <select class="form-control" id="selectAppAdmob">

                </select>
            </div>
        </div>

        <div class="form-group">
            <label for="selectAccountAdmob" class="col-sm-2 control-label">广告账号</label>
            <div class="col-sm-10">
                <select class="form-control select2" id="selectAccountAdmob" multiple="multiple">

                </select>
            </div>
        </div>
        <div class="form-group">
            <label for="inputCreateCountAdmob" class="col-sm-2 control-label">创建数量</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputCreateCountAdmob" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputCampaignName" class="col-sm-2 control-label">广告系列名称</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputCampaignNameAdmob" />
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
            <div class="col-sm-2">
                <label title="选中后每个选项创建一个或多个广告系列"><input type="checkbox" class="form-check-input" id="selectRegionAdmobExplode">分离到系列</label>
            </div>
        </div>
        <div class="form-group">
            <label for="selectRegionUnselected" class="col-sm-2 control-label">排除国家地区</label>
            <div class="col-sm-9">
                <select class="form-control select2" id="selectRegionUnselectedAdmob" multiple="multiple">

                </select>
            </div>
            <div class="col-sm-1">
                <input type="button" class="btn-more btn btn-default" id="btnSelectRegionUnselectedAdmobMore" value="批量输入"/>
            </div>
        </div>


        <div class="form-group">
            <label for="selectLanguage" class="col-sm-2 control-label">语言</label>
            <div class="col-sm-10">
                <select class="form-control" id="selectLanguageAdmob">
                </select>
            </div>
        </div>

        <div class="form-group">
            <label for="selectIncidentAdmob" class="col-sm-2 control-label">事件</label>
            <div class="col-sm-10">
                <select class="form-control" id="selectIncidentAdmob">
                </select>
            </div>
        </div>

        <div class="form-group">
            <label for="inputBudget" class="col-sm-2 control-label">预算</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputBudgetAdmob" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputBiddingAdmob" class="col-sm-2 control-label">出价</label>
            <div class="col-sm-8">
                <input class="form-control" id="inputBiddingAdmob" />
            </div>
            <div class="col-sm-2">
                <label title="选中后每个选项创建一个或多个广告系列"><input type="checkbox" class="form-check-input" id="inputBiddingAdmobExplode">分离到系列</label>
            </div>
        </div>
        <div class="form-group">
            <label for="inputMaxCpa" class="col-sm-2 control-label">关闭价格</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMaxCpaAdmob" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessage" class="col-sm-2 control-label">广告语1</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessage1" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessage" class="col-sm-2 control-label">广告语2</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessage2" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessage" class="col-sm-2 control-label">广告语3</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessage3" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessage" class="col-sm-2 control-label">广告语4</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessage4" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputImagePath" class="col-sm-2 control-label">图片路径</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputImagePathAdmob" />
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-10" style="text-align: center">
                <label><input type="checkbox" id="checkAdmobAutoCreate"/>设置为自动创建</label> <input type="submit" class="btn btn-primary" style="width: 100px;" id="btnCreateAdmob" value="创建"/>
                <label><input type="checkbox" id="onlyCheckAdmobAutoCreate"/>仅设置为自动创建</label>
            </div>
        </div>
    </form>

</div>

</div>


<jsp:include page="loading_dialog.jsp"></jsp:include>


<script src="js/jquery.js"></script>
<script src="bootstrap/js/bootstrap.min.js"></script>
<script src="js/core.js?t=20171210"></script>
<script src="js/bootstrap-datetimepicker.js"></script>
<script src="jqueryui/jquery-ui.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.4/js/select2.min.js"></script>
<script>
    var isAutoCreate = <%=isAutoCreate%>;
    var modifyNetwork = "<%=network%>";
    var modifyRecordId = <%=recordId%>;
</script>

<script src="js/campaign_create.js?t=20180118"></script>
</body>
</html>
