<%@ page import="com.bestgo.admanager.utils.NumberUtil" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.bestgo.common.database.services.DB" %>
<%@ page import="com.bestgo.admanager.servlet.TagsBidAdmanager" %>
<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="com.bestgo.admanager.servlet.Ad_old_account_create_copy_to_new" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/rootBase.jsp" %>


<html>
<head>
    <title>模板账号to创建系列账号</title>
    <style>
        .ui-autocomplete {
            display: block;
            z-index: 99999;
        }
    </style>
</head>
<body>

<%
    Object object = session.getAttribute("isAdmin");
    if (object == null) {
        response.sendRedirect("login.jsp");
    }

    //添加按钮数据的加载fetchAllAccount
    List<JSObject> allTags = Ad_old_account_create_copy_to_new.fetchAllTags();
    JsonArray array1 = new JsonArray();
    for (int i = 0; i < allTags.size(); i++) {
        array1.add((String) allTags.get(i).get("tag_name"));
    }

    List<JSObject> allAccounts = Ad_old_account_create_copy_to_new.fetchAllAccount();
    JsonArray array2 = new JsonArray();
    for (int i = 0; i < allAccounts.size(); i++) {
        array2.add((String) allAccounts.get(i).get("account_id"));
    }

%>

<div class="container-fluid">
    <%@include file="common/navigationbar.jsp" %>

    <div class="panel panel-default">
        <!-- Default panel contents -->
        <div class="panel-heading">
            <button id="btn_add_new_tag" class="btn btn-default">添加</button>
            <input id="inputSearch" class="form-control" style="display: inline; width: auto;" type="text"/>
            <button id="btnSearch" class="btn btn-default glyphicon glyphicon-search"></button>
        </div>

        <table class="table">
            <thead>
            <tr>
                <th>id</th>
                <th>标签名称</th>
                <th>模板账号</th>
                <th>创建系列帐号</th>
                <th>出价倍数</th>
                <th>network</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>

            <%
                //页面刷新，加载全部的数据。
                List<JSObject> data = new ArrayList<>();
                long totalPage = 0;
                long count = Ad_old_account_create_copy_to_new.count();
                int index = NumberUtil.parseInt(request.getParameter("page_index"), 0);
                int size = NumberUtil.parseInt(request.getParameter("page_size"), 20);
                totalPage = count / size + (count % size == 0 ? 0 : 1);

                int preIndex = index > 0 ? index - 1 : 0;
                int nextPage = index < totalPage - 1 ? index + 1 : index;

                data = Ad_old_account_create_copy_to_new.fetchAllData(index, size);
            %>

            <%
                for (int i = 0; i < data.size(); i++) {
                    JSObject one = data.get(i);
            %>
            <tr>
                <td><%=one.get("id")%></td>
                <td><%=one.get("tag_name")%></td>
                <td><%=one.get("old_account_id")%></td>
                <td><%=one.get("new_account_id")%></td>
                <td><%=one.get("bidding_mul")%></td>
                <td><%=one.get("network")%></td>
                <td><a class="link_modify glyphicon glyphicon-pencil" href="#"></a><a
                        class="link_delete glyphicon glyphicon-remove" href="#"></a></td>
            </tr>
            <% } %>

            </tbody>
        </table>

        <nav aria-label="Page navigation">
            <ul class="pagination">
                <li>
                    <a href="ad_old_account_create_copy_to_new.jsp?page_index=<%=preIndex%>" aria-label="Previous">
                        <span aria-hidden="true">上一页</span>
                    </a>
                </li>
                <%
                    for (int i = 0; i < totalPage; i++) {
                %>
                <li><a href="ad_old_account_create_copy_to_new.jsp?page_index=<%=i%>"><span><%=i + 1%></span></a></li>
                <% } %>
                <li>
                    <a href="ad_old_account_create_copy_to_new.jsp?page_index=<%=nextPage%>" aria-label="Next">
                        <span aria-hidden="true">下一页</span>
                    </a>
                </li>
            </ul>
        </nav>
    </div>
</div>

<div id="new_tag_dlg" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="dlg_title">标签</h4>
            </div>
            <div class="modal-body">
                <form id="modify_form" class="form-horizontal" action="#" autocomplete="off">
                    <div class="form-group">
                        <label for="inputTagName" class="col-sm-2 control-label">标签名称</label>
                        <div class="col-sm-8">
                            <input class="form-control" id="inputTagName" placeholder="标签名称" type="text"
                                   style="display: inline;">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="old" class="col-sm-2 control-label">模板帐号</label>
                        <div class="col-sm-8">
                            <input class="form-control" id="old">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="new" class="col-sm-2 control-label">创建帐号</label>
                        <div class="col-sm-8">
                            <input class="form-control" id="new">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="inputBiddingMul" class="col-sm-2 control-label">出价倍数</label>
                        <div class="col-sm-8">
                            <input class="form-control" id="inputBiddingMul" autocomplete="off">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="network" class="col-sm-2 control-label">network</label>
                        <div class="col-sm-8">
                            <select id="network" class="form-control">
                                <option value="facebook">facebook</option>
                                <option value="admob">admob</option>
                            </select>
                        </div>
                    </div>

                </form>
                <p id="delete_message">确认要删除吗?</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-primary">确定</button>
            </div>
        </div>
    </div>
</div>

<jsp:include page="common/loading_dialog.jsp"></jsp:include>

<script type="text/javascript">
    $("li[role='presentation']:eq(12)").addClass("active");

    //页面刷新，标签名称自动加载
    var data1 = <%=array1.toString()%>;
    $("#inputTagName").autocomplete({
        source: data1
    });

    //页面刷新，帐户名称自动加载
    var data2 = <%=array2.toString()%>;
    $("#old").autocomplete({
        source: data2
    });

    $("#new").autocomplete({
        source: data2
    });

    var modifyType = 'new';
    var id;
    //添加按钮事件
    $("#btn_add_new_tag").click(function () {
        modifyType = 'new';
        $("#inputTagName").val("");

        $("#inputTagName").val("");
        $("#old").val("");
        $("#new").val("");
        $("#inputBiddingMul").val("");
        $("#network").val("facebook");

        $("#inputTagName").prop("disabled", false);
        $('#delete_message').hide();
        $('#modify_form').show();
        $("#dlg_title").text("添加");
        $("#new_tag_dlg").modal("show");
    });

    $("#new_tag_dlg .btn-primary").click(function () {
        // var tagName = $("#inputTagName").val();
        // var country = $("#inputCountry").val();
        // var bidding = $("#inputBidding").val();
         var tagName = $("#inputTagName").val();
         var old = $("#old").val();
         var newC = $("#new").val();
         var biddingMul = $("#inputBiddingMul").val();
         var network = $("#network").val();
        if (modifyType == 'new') {
            $.post('Ad_old_account_create_copy_to_new/create', {
                tagname: tagName,
                old: old,
                newC: newC,
                biddingMul: biddingMul,
                network: network
            }, function (data) {
                if (data && data.ret == 1) {
                    $("#new_tag_dlg").modal("hide");
                    alert("添加成功！");
                    location.reload();
                } else {
                    admanager.showCommonDlg("错误", data.message);
                }
            }, 'json');
        } else if (modifyType == 'update') {
            $.post('Ad_old_account_create_copy_to_new/update', {
                id: id,
                tagname: tagName,
                old: old,
                newC: newC,
                biddingMul: biddingMul,
                network: network
            }, function (data) {
                $("#inputTagName").prop("disabled", false);
                if (data && data.ret == 1) {
                    $("#new_tag_dlg").modal("hide");
                    location.reload();
                    alert("更新成功！");
                } else {
                    admanager.showCommonDlg("错误", data.message);
                }
            }, 'json');
        } else if (modifyType == 'delete') {
            $.post('Ad_old_account_create_copy_to_new/delete', {
                id: id
            }, function (data) {
                if (data && data.ret == 1) {
                    $("#new_tag_dlg").modal("hide");
                    location.reload();
                    alert("删除成功！");
                } else {
                    admanager.showCommonDlg("错误", data.message);
                }
            }, 'json');
        }
    });

    $('#btnSearch').click(function () {
        var query = $("#inputSearch").val();
        $.post("Ad_old_account_create_copy_to_new/query", {
            word: query
        }, function (data) {
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
            var td = $('<td></td>');
            td.text(one.id);
            tr.append(td);

            td = $('<td></td>');
            td.text(one.tag_name);
            tr.append(td);

            td = $('<td></td>');
            td.text(one.old_account_id);
            tr.append(td);

            td = $('<td></td>');
            td.text(one.new_account_id);
            tr.append(td);

            td = $('<td></td>');
            td.text(one.bidding_mul);
            tr.append(td);

            td = $('<td></td>');
            td.text(one.network);
            tr.append(td);

            td = $('<td><a class="link_modify glyphicon glyphicon-pencil" href="#"></a><a class="link_delete glyphicon glyphicon-remove" href="#"></a></td>');
            tr.append(td);
            $('.table tbody').append(tr);
        }
    }

    function bindOp() {
        $(".link_modify").click(function () {
            modifyType = "update";
            $('#delete_message').hide();
            $('#modify_form').show();

            $("#dlg_title").text("修改");
            //获取table里要回显的数据。
            var tds = $(this).parents("tr").find('td');
            id = $(tds.get(0)).text();
            var tagName = $(tds.get(1)).text();
            var old_account_id = $(tds.get(2)).text();
            var new_account_id = $(tds.get(3)).text();
            var bidding_mul = $(tds.get(4)).text();
            var network = $(tds.get(5)).text();

            $("#inputTagName").val(tagName).prop("disabled", true);
            $("#old").val(old_account_id);
            $("#new").val(new_account_id);
            $("#inputBiddingMul").val(bidding_mul);
            //下拉选择框的数据回显，正常的是一样的，回显其value即可
            $("#network").val(network);

            $("#new_tag_dlg").modal("show");
        });

        $(".link_delete").click(function () {
            modifyType = "delete";
            $('#delete_message').show();
            $('#modify_form').hide();

            $("#dlg_title").text("删除");

            var tds = $(this).parents("tr").find('td');
            id = $(tds.get(0)).text();

            $("#new_tag_dlg").modal("show");

        });
    }

    bindOp();
</script>
<script src="js/interlaced-color-change.js"></script>
</body>
</html>
