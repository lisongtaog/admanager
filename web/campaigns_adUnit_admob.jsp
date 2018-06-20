<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ page import="com.bestgo.admanager.utils.Utils" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.bestgo.admanager.servlet.Campaign" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="com.bestgo.admanager.servlet.CampaignAdmob" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Admob广告系列-广告单元</title>
    <link rel="stylesheet" href="bootstrap/css/bootstrap.min.css" />
    <link rel="stylesheet" href="bootstrap/css/bootstrap-theme.min.css" />
    <link rel="stylesheet" href="css/core.css" />
    <link rel="stylesheet" href="css/bootstrap-tagsinput.css" />
    <style>
        table td{
            max-width:43em;
            word-wrap:break-word;
            text-overflow:ellipsis;
            white-space:nowrap;
            overflow:hidden;
        }
        table td:hover{
            white-space:normal;
            overflow:auto;
        }
        .subTab td{
            max-width:10em;
        }
        .subTab td input{
            width: 100%;
        }
    </style>
</head>
<body>

<%
    Object object = session.getAttribute("isAdmin");
    if (object == null) {
        response.sendRedirect("login.jsp");
    }
%>

<div class="container-fluid">
    <%@include file="common/navigationbar.jsp"%>
    <div class="form-inline panel panel-default">
        <div class="form-group form-group-sm panel-heading">
            <label class="control-label" for="inputSearch" >系列</label>
            <input class="form-control" id="inputSearch" placeholder="系列名字或系列ID" type="text" />
            <button id="btnSearch" class="btn btn-default glyphicon glyphicon-search"></button>
        </div>
        <div style="height: 53%;overflow:scroll;">
            <table class="table-condensed" id="campaigns_table">
                <thead>
                <tr>
                    <th>序号</th><th>系列ID</th><th>广告账号ID</th><th>系列名称</th><th>创建时间</th><th>状态</th><th>标签</th>
                    <th><input type="checkbox" id="AllChecked">操作/
                        <button class="btn btn-link glyphicon glyphicon-file" onclick="CampaignsCopy()">复制</button></th>
                </tr>
                </thead>
                <tbody>

                <%
                    List<JSObject> data = new ArrayList<>();
                    long totalPage = 0;
                    long count = CampaignAdmob.count();
                    int index = Utils.parseInt(request.getParameter("page_index"), 0);
                    int size = Utils.parseInt(request.getParameter("page_size"), 10);
                    totalPage = count / size + (count % size == 0 ? 0 : 1);

                    int preIndex = index > 0 ? index-1 : 0;
                    int nextPage = index < totalPage - 1 ? index+1 : index;

                    data = CampaignAdmob.fetchData(index, size);

                    List<JSObject> allTags = Tags.fetchAllTags();
                    JsonArray array = new JsonArray();
                    for (int i = 0; i < allTags.size(); i++) {
                        array.add((String)allTags.get(i).get("tag_name"));
                    }

                %>

                <%
                    for (int i = 0; i < data.size(); i++) {
                        JSObject one = data.get(i);
                        List<String> tags = CampaignAdmob.bindTags((String)one.get("campaign_id"));
                        String tagStr = "";
                        for (int ii = 0; ii < tags.size(); ii++) {
                            tagStr += (tags.get(ii) + ",");
                        }
                        if (tagStr.length() > 0) {
                            tagStr = tagStr.substring(0, tagStr.length() - 1);
                        }
                        double installed = Utils.convertDouble(one.get("total_installed"), 0);
                        double click = Utils.convertDouble(one.get("total_click"), 0);
                        double cvr = click > 0 ? installed / click : 0;
                %>
                <tr>
                    <td><%=one.get("id")%></td>
                    <td><%=one.get("campaign_id")%></td>
                    <td><%=one.get("account_id")%></td>
                    <td><%=one.get("campaign_name")%></td>
                    <td style="max-width:7em;"><%=one.get("create_time")%></td>
                    <td><%=one.get("status")%></td>
                    <td><%=tagStr%></td>
                    <td><input type="checkbox" style="cursor:pointer" class="ck"/></td>
                </tr>
                <% } %>

                </tbody>
            </table>
        </div>
        <div>
            <nav aria-label="Page navigation">
                <ul class="pagination">
                    <li>
                        <a href="campaigns_adUnit_admob.jsp?page_index=<%=preIndex%>" aria-label="Previous">
                            <span aria-hidden="true">上一页</span>
                        </a>
                    </li>
                    <li>
                        <a href="campaigns_adUnit_admob.jsp?page_index=<%=nextPage%>" aria-label="Next">
                            <span aria-hidden="true">下一页</span>
                        </a>
                    </li>
                    <li>
                        共<%=totalPage%>页
                    </li>

                </ul>
            </nav>
        </div>
    </div>

    <div class="panel panel-default">
        <div>
            <label for="gid">广告组ID</label>
            <input type="text" id="gid" />
            <label for="gname">广告组名称</label>
            <input type="text" id="gname"/>
            <button class="btn btn-info" onclick="DataCreation()">保存</button>
        </div>
        <div>
            <div class="panel panel-default col-xs-6">
                <div style="height:35%; overflow:scroll">
                    <table class="table-condensed subTab" id="campaigns_select" style="width:100%">
                        <thead>
                        <tr>
                            <th>序号</th>
                            <th>系列ID</th>
                            <th>标签</th>
                            <th>系列名称</th>
                            <th>状态</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td>0123456789</td>
                            <td>9999999999</td>
                            <td>somekinds</td>
                            <td>campaigns_name_only_for_show</td>
                            <td>paused</td>
                            <td><button type="button" class="btn btn-link" onclick="del_rows(this)">删除</button></td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
            <div class="panel panel-default col-xs-6">
                <div class="col-xs-11" style="height: 35%;overflow:scroll">
                    <table class="table-condensed subTab" id="CampaignsUnits" style="border:#4a98ff 1px solid">
                        <tr>
                            <th>广告单元ID</th>
                            <th>属性/network</th>
                            <th>广告单元名称</th>
                            <th>广告类型</th>
                            <th>操作</th>
                        </tr>
                        <tr>
                            <td><input type="text" /></td>
                            <td><select name="adNetwork" id="adNetwork"><option value="FB">FB</option><option value="ADMOB">ADMOB</option></select></td>
                            <td><input type="text" /></td>
                            <td><select name="adType" id="adType"><option value="1">Interstitial</option><option value="2">Native</option><option value="3">Banner</option></select>
                            </td>
                            <td><button type="button" class="btn btn-link" onclick="del_rows(this)">删除</button></td>
                        </tr>
                    </table>
                </div>
                <div><button class="btn btn-primary" onclick="AddCampaignUnits()">添加</button></div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="loading_dialog.jsp"></jsp:include>

<script src="js/jquery.js"></script>
<script src="bootstrap/js/bootstrap.min.js"></script>
<script src="js/core.js"></script>
<script src="js/typeahead.js"></script>
<script src="js/bootstrap-tagsinput.js"></script>

<script type="text/javascript">
    $("li[role='presentation']:eq(3)").addClass("active");


    $('#btnSearch').click(function() {
        var query = $("#inputSearch").val();//系列名称或ID
        $.post('campaign_admob/query', {
            word: query
        }, function(data) {
            if (data && data.ret == 1) {
                $('#campaing_talbe tbody > tr').remove();
                setData(data.data);
                bindOp();
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        }, 'json');
    });

    function setData(data) {
        for (var i = 0; i < data.length; i++) {
            var one = data[i];
            var tr = $('<tr></tr>');
            var keyset = ["id", "campaign_id", "account_id", "campaign_name", "create_time",
                "status", "tagStr"];
            for (var j = 0; j < keyset.length; j++) {
                var td = $('<td></td>');
                if (keyset[j] == 'budget' || keyset[j] == 'bidding') {
                    td.text(one[keyset[j]] / 100);
                } else {
                    td.text(one[keyset[j]]);
                }
                tr.append(td);
            }
            td = $('<td><input type="checkbox" style="cursor:pointer" class="ck"/></td>');
            tr.append(td);
            $('#campaing_talbe tbody').append(tr);
        }
    }

    function bindOp() {
        $(".link_modify").click(function() {
            $('#modify_form').show();

            $("#dlg_title").text("修改系列");

            var tds = $(this).parents("tr").find('td');
            id = $(tds.get(0)).text();
            var tags = $(tds.get(14)).text().split(',');

            $('#inputTags').tagsinput('removeAll');
            for (var i = 0; i < tags.length; i++) {
                if (tags[i] != '') {
                    $('#inputTags').tagsinput('add', tags[i]);
                }
            }

            $("#new_campaign_dlg").modal("show");
        });
    }

    bindOp();

    $("#AllChecked").click(function(){
        if($("#AllChecked").prop("checked")){
            $(".ck").prop("checked",true);
        }else{
            $(".ck").prop("checked",false);
        }
    });

    //Adwords查询信息复制到待保存表格
    function CampaignsCopy(){
        var campaigns = $("#campaigns_table tbody").find("input:checked").parents("tr");
        $("#campaigns_select tbody").empty();
        campaigns.each(function(idx,e){
            var tr = $(e);
            var order = tr.children("td:eq(0)").text();
            var campaignId = tr.children("td:eq(1)").text();
            var tag = tr.children("td:eq(6)").text();
            var campaignName = tr.children("td:eq(3)").text(); //数据库里的名称有双引号
            var status = tr.children("td:eq(5)").text();
            $("#campaigns_select tbody").append("<tr><td>"+order+"</td><td>"+campaignId+"</td><td>"+tag +"</td><td>"+campaignName
                +"</td><td>"+status+"</td><td><button type=\"button\" class=\"btn btn-link\" onclick=\"del_rows(this)\">删除</button></td></tr>");
        });
    }

    //添加额外横列到广告单元
    function AddCampaignUnits(){
        $("#CampaignsUnits").append("<tr>\n" +
            "<td><input type=\"text\" /></td>\n" +
            "<td><select name=\"adNetwork\" id=\"adNetwork\"><option value=\"FB\">FB</option><option value=\"ADMOB\">ADMOB</option></select></td>\n" +
            "<td><input type=\"text\" /></td>\n" +
            "<td><select name=\"adType\" id=\"adType\"><option value=\"1\">Interstitial</option><option value=\"2\">Native</option><option value=\"3\">Banner</option></select>\n" +
            "</td>\n" +
            "<td><button type=\"button\" class=\"btn btn-link\" onclick=\"del_rows(this)\">删除</button></td>\n" +
            "</tr>");
    }

    //行删除
    function del_rows(thizz) {
        var tr = $(thizz).parents("tr");
        var table = tr.parents("table");
        var countTr = table.find("tr").length;
        //仅保留 表头和第一条数据，至少保留一条数据
        if(countTr > 2){
            tr.remove();
        }else(
            alert("至少保留一条信息")
        )
    }

    //创建入库
    function DataCreation(){
        var campaigns = $("#campaigns_select").find("tr:gt(0)");
        var campaignUnits = $("#CampaignsUnits").find("tr:gt(0)");
        var gid = $("#gid").val();
        var gname = $("#gname").val();

        if( gid == "" || gname == ""){
            admanager.showCommonDlg("warning","广告组ID或广告组名称不能为空")
        }else{
            var emptyInputs = 0;
            campaignUnits.find("input").each(function(idx){
                if($(this).val() == ""){
                    emptyInputs++;
                    return false;
                }
            });
            if(emptyInputs>0){
                admanager.showCommonDlg("警告","广告单元不能有空输入!");
            }else{
                var CamJsonArray = [];
                var UnitsJsonArray = [];
                campaigns.each(function(i){
                    var json = {};
                    json.campaign_id = $(this).find("td:eq(1)").text();
                    json.campaign_name = $(this).find("td:eq(3)").text();
                    json.validstatus = $(this).find("td:eq(4)").text();
                    CamJsonArray.push(json);
                });
                campaignUnits.each(function(i){
                    var json = {};
                    json.adunit_id = $(this).find("input:eq(0)").val();
                    json.network = $(this).find("select:eq(0)").val();
                    json.name = $(this).find("input:eq(1)").val();
                    json.type = $(this).find("select:eq(1)").val();
                    UnitsJsonArray.push(json);
                });
                var adUnits = JSON.stringify(UnitsJsonArray);
                var campaigns = JSON.stringify(CamJsonArray);
                $.post("campaignAdUnit/create",{
                    gName: gname,
                    gId: gid,
                    adUnits:adUnits ,
                    campaigns:campaigns
                },function(data){
                    if(data.ret == 1){
                        admanager.showCommonDlg("提示","广告单元创建完毕");
                        setTimeout(function(){
                            $("#common_message_dialog").modal("hide");
                        },1500)
                    }else{
                        admanager.showCommonDlg("错误",data.message);
                    }
                },"json")
            }
        }
    }
</script>
<script src="js/interlaced-color-change.js"></script>
</body>
</html>
