<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ page import="com.bestgo.admanager.Utils" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.bestgo.admanager.servlet.Campaign" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.google.gson.JsonArray" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>广告系列管理</title>
    <link rel="stylesheet" href="bootstrap/css/bootstrap.min.css" />
    <link rel="stylesheet" href="bootstrap/css/bootstrap-theme.min.css" />
    <link rel="stylesheet" href="css/core.css" />
    <link rel="stylesheet" href="css/bootstrap-tagsinput.css" />
</head>
<body>

<%
    Object object = session.getAttribute("isAdmin");
    if (object == null) {
        response.sendRedirect("login.jsp");
    }
%>

<div class="container-fluid">
    <ul class="nav nav-pills">
        <li role="presentation"><a href="index.jsp">首页</a></li>
        <li role="presentation"><a href="campaigns_create.jsp">创建广告</a></li>
        <li role="presentation"><a href="adaccounts.jsp">广告账号管理</a></li>
        <li role="presentation"><a href="adaccounts_admob.jsp">广告账号管理(AdMob)</a></li>
        <li role="presentation" class="active"><a href="#">广告系列管理</a></li>
        <li role="presentation"><a href="campaigns_admob.jsp">广告系列管理(AdMob)</a></li>
        <li role="presentation"><a href="tags.jsp">标签管理</a></li>
        <li role="presentation"><a href="rules.jsp">规则</a></li>
        <li role="presentation"><a href="query.jsp">查询</a></li>
        <li role="presentation"><a href="system.jsp">系统管理</a></li>
        <li role="presentation"><a href="advert_insert.jsp">广告存储</a></li>
    </ul>

    <div class="panel panel-default">
        <div class="panel-heading">广告系列列表
            <input id="inputSearch" class="form-control" placeholder="系列名字或系列ID，系列名字可以模糊查询" style="display: inline; width: auto;" type="text" />
            <button id="btnSearch" class="btn btn-default">查找</button></div>

        <table class="table">
            <thead>
            <tr><th>序号</th><th>系列ID</th><th>广告组ID</th><th>广告账号ID</th><th>系列名称</th><th>创建时间</th><th>状态</th><th>预算</th><th>竞价</th><th>总花费</th><th>总安装</th><th>总点击</th><th>CPA</th><th>CTR</th><th>CVR</th><th>标签</th><th>操作</th></tr>
            </thead>
            <tbody>

            <%
                List<JSObject> data = new ArrayList<>();
                long totalPage = 0;
                long count = Campaign.count();
                int index = Utils.parseInt(request.getParameter("page_index"), 0);
                int size = Utils.parseInt(request.getParameter("page_size"), 20);
                totalPage = count / size + (count % size == 0 ? 0 : 1);

                int preIndex = index > 0 ? index-1 : 0;
                int nextPage = index < totalPage - 1 ? index+1 : index;

                data = Campaign.fetchData(index, size);

                List<JSObject> allTags = Tags.fetchAllTags();
                JsonArray array = new JsonArray();
                for (int i = 0; i < allTags.size(); i++) {
                    array.add((String)allTags.get(i).get("tag_name"));
                }

            %>

            <%
                for (int i = 0; i < data.size(); i++) {
                    JSObject one = data.get(i);
                    List<String> tags = Campaign.bindTags((String)one.get("campaign_id"));
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
                <td><%=one.get("adset_id")%></td>
                <td><%=one.get("account_id")%></td>
                <td><%=one.get("campaign_name")%></td>
                <td><%=one.get("create_time")%></td>
                <td><%=one.get("status")%></td>
                <td><%=(double)one.get("budget") / 100 %></td>
                <td><%=(double)one.get("bidding") / 100%></td>
                <td><%=one.get("total_spend")%></td>
                <td><%=one.get("total_installed")%></td>
                <td><%=one.get("total_click")%></td>
                <td><fmt:formatNumber value='<%=one.get("cpa")%>' pattern="0.0000"/> </td>
                <td><fmt:formatNumber value='<%=one.get("ctr")%>' pattern="0.0000"/> </td>
                <td><fmt:formatNumber value="<%=cvr%>" pattern="0.0000"/> </td>
                <td><%=tagStr%></td>
                <td><a class="link_modify" href="javascript:void(0)">修改</a></td>
            </tr>
            <% } %>

            </tbody>
        </table>

        <nav aria-label="Page navigation">
            <ul class="pagination">
                <li>
                    <a href="campaigns.jsp?page_index=<%=preIndex%>" aria-label="Previous">
                        <span aria-hidden="true">上一页</span>
                    </a>
                </li>
                <li>
                    <a href="campaigns.jsp?page_index=<%=nextPage%>" aria-label="Next">
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
                    <div class="form-group">
                        <label for="inputTags" class="col-sm-2 control-label">标签</label>
                        <div class="col-sm-10">
                            <input data-role="tagsinput" class="form-control" id="inputTags" placeholder="标签" autocomplete="off">
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

<jsp:include page="loading_dialog.jsp"></jsp:include>

<script src="js/jquery.js"></script>
<script src="bootstrap/js/bootstrap.min.js"></script>
<script src="js/core.js"></script>
<script src="js/typeahead.js"></script>
<script src="js/bootstrap-tagsinput.js"></script>

<script type="text/javascript">
    var id;

    var data = <%=array.toString()%>;

    var tagNames = new Bloodhound({
        datumTokenizer: Bloodhound.tokenizers.obj.whitespace('name'),
        queryTokenizer: Bloodhound.tokenizers.whitespace,
        local: $.map(data, function (tag) {
            return {
                name: tag
            };
        })
    });
    tagNames.initialize();

    $('#inputTags').tagsinput({
        typeaheadjs: [{
            minLength: 1,
            highlight: true,
        },{
            minlength: 1,
            name: 'name',
            displayKey: 'name',
            valueKey: 'name',
            source: tagNames.ttAdapter()
        }],
        freeInput: false
    });

    $("#new_campaign_dlg .btn-primary").click(function() {
        var campaignName = $('#inputCampaignName').val();
        var status = $('#inputStatus').prop('checked') ? 'ACTIVE' : 'PAUSED';
        var budget = $('#inputBudget').val();
        var bidding = $('#inputBidding').val();

        var tags = $('#inputTags').val();

        $.post('campaign/update', {
            id: id,
            campaignName: campaignName,
            status: status,
            budget: budget,
            bidding: bidding,
            tags: tags
        }, function (data) {
            if (data && data.ret == 1) {
                $("#new_campaign_dlg").modal("hide");
//                location.reload();
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        }, 'json');
    });

    $('#btnSearch').click(function() {
        var query = $("#inputSearch").val();
        $.post('campaign/query', {
            word: query
        }, function(data) {
            if (data && data.ret == 1) {
                $('.table tbody > tr').remove();
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
            var keyset = ["id", "campaign_id", "adset_id", "account_id", "campaign_name", "create_time",
                "status", "budget", "bidding", "total_spend", "total_installed", "total_click", "cpa", "ctr", "cvr", "tagStr"];
            for (var j = 0; j < keyset.length; j++) {
                var td = $('<td></td>');
                if (keyset[j] == 'budget' || keyset[j] == 'bidding') {
                    td.text(one[keyset[j]] / 100);
                } else {
                    td.text(one[keyset[j]]);
                }
                tr.append(td);
            }
            td = $('<td><a class="link_modify" href="javascript:void(0)">修改</a>');
            tr.append(td);
            $('.table tbody').append(tr);
        }
    }

    function bindOp() {
        $(".link_modify").click(function() {
            $('#modify_form').show();

            $("#dlg_title").text("修改系列");

            var tds = $(this).parents("tr").find('td');
            id = $(tds.get(0)).text();
            var campaignName = $(tds.get(4)).text();
            var status = $(tds.get(6)).text();
            var budget = $(tds.get(7)).text();
            var bidding = $(tds.get(8)).text();
            var tags = $(tds.get(15)).text().split(',');

            $('#inputCampaignName').val(campaignName);
            if (status.toLowerCase() == 'active') {
                $('#inputStatus').prop('checked', true);
            } else {
                $('#inputStatus').prop('checked', false);
            }
            $('#inputBudget').val(budget);
            $('#inputBidding').val(bidding);

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
</script>
</body>
</html>
