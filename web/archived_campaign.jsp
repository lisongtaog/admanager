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
    <title>删除系列</title>
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
        </div>
    </div>

    <form class="form-horizontal" action="#" id="formFacebook">

        <div class="form-group">
            <label for="selectAccount" class="col-sm-2 control-label">账号</label>
            <div class="col-sm-8">
                <select class="form-control select2" id="selectAccount" multiple="multiple">
                </select>
            </div>
            <div class="col-sm-2">
                <label><input type="checkbox" class="form-check-input" id="containsDisabledAccountId">包含被禁账号</label>
            </div>
        </div>
        <br>

        <div class="form-group">
            <label for="selectCampaignStatus" class="col-sm-2 control-label">系列状态</label>
            <div class="col-sm-8">
                <select class="form-control select2" id="selectCampaignStatus" multiple="multiple">
                </select>
            </div>
        </div>
        <br>

        <div class="form-group">
            <label for="selectApp" class="col-sm-2 control-label">应用</label>
            <div class="col-sm-8">
                <select class="form-control" id="selectApp">
                    <option></option>
                </select>
            </div>
        </div>
        <br>

        <div class="form-group">
            <label for="selectRegion" class="col-sm-2 control-label">国家地区</label>
            <div class="col-sm-8">
                <select class="form-control select2" id="selectRegion" multiple="multiple">

                </select>
            </div>
        </div>

        <br>

        <div class="form-group">
            <div class="col-sm-10" style="text-align: center">
                <input type="submit" class="btn btn-primary" style="width: 100px;" id="btnUpdate" value="更新"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <input type="submit" class="btn btn-primary" style="width: 100px;" id="btnDelete" value="删除"/>
            </div>
            <br>
        </div>

        <br>

        <div class="form-group">
            <label class="col-sm-2"></label>
            <div class="col-sm-8">
                <br>
                <h4>1.对一个账号不管什么状态系列全部删除的</h4>
                <br>
                <h4>2.对一个账号里的一个状态进行删除的</h4>
                <br>
                <h4>3.对一个账号中指定对某个应用进行删除</h4>
                <br>
                <h4>4.对一个应用的一个国家进行删除（不需要更新）</h4>
            </div>
        </div>
    </form>

    <form class="form-horizontal" action="#" id="formAdmob">

        <div class="form-group">
            <label for="selectAccountAdmob" class="col-sm-2 control-label">账号</label>
            <div class="col-sm-8">
                <select class="form-control select2" id="selectAccountAdmob" multiple="multiple">
                </select>
            </div>
        </div>
        <br>

        <div class="form-group">
            <label for="selectCampaignStatusAdmob" class="col-sm-2 control-label">系列状态</label>
            <div class="col-sm-8">
                <select class="form-control select2" id="selectCampaignStatusAdmob" multiple="multiple">
                </select>
            </div>
        </div>
        <br>

        <div class="form-group">
            <label for="selectAppAdmob" class="col-sm-2 control-label">应用</label>
            <div class="col-sm-8">
                <select class="form-control" id="selectAppAdmob">
                    <option></option>
                </select>
            </div>
        </div>
        <br>

        <div class="form-group">
            <label for="selectRegionAdmob" class="col-sm-2 control-label">国家地区</label>
            <div class="col-sm-8">
                <select class="form-control select2" id="selectRegionAdmob" multiple="multiple">

                </select>
            </div>
        </div>

        <br>

        <div class="form-group">
            <div class="col-sm-10" style="text-align: center">
                <input type="submit" class="btn btn-primary" style="width: 100px;" id="btnUpdateAdmob" value="更新"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <input type="submit" class="btn btn-primary" style="width: 100px;" id="btnDeleteAdmob" value="删除"/>
            </div>
            <br>
        </div>

        <br>

        <div class="form-group">
            <label class="col-sm-2"></label>
            <div class="col-sm-8">
                <br>
                <h4 >1.对（一个或多个）账号不管什么状态系列全部删除的</h4>
                <br>
                <h4>2.对（一个或多个）账号里的一个状态进行删除的</h4>
                <br>
                <h4>3.对（一个或多个）账号中指定对某个应用进行删除</h4>
            </div>
        </div>
    </form>

</div>

</div>

<script>
    $("li[role='presentation']:eq(3)").addClass("active");
    var campaignStatus = ["", "ARCHIVED", "ACTIVE", "PAUSED"];

</script>
<script src="js/archived_campaign.js?t=<%=Math.random()%>"></script>

</body>
</html>
