<%@ page import="com.bestgo.admanager.Utils" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>


<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="bootstrap/css/bootstrap.min.css" />
<link rel="stylesheet" href="bootstrap/css/bootstrap-theme.min.css" />

<html>
<head>
    <title>标签管理</title>
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
        <li role="presentation"><a href="campaigns.jsp">广告系列管理</a></li>
        <li role="presentation"><a href="campaigns_admob.jsp">广告系列管理(AdMob)</a></li>
        <li role="presentation" class="active"><a href="#">标签管理</a></li>
        <li role="presentation"><a href="rules.jsp">规则</a></li>
        <li role="presentation"><a href="query.jsp">查询</a></li>
        <li role="presentation"><a href="system.jsp">系统管理</a></li>
        <li role="presentation"><a href="advert_insert.jsp">广告存储</a></li>
        <li role="presentation"><a href="summary.jsp">七天汇总</a></li>
    </ul>

    <div class="panel panel-default">
        <!-- Default panel contents -->
        <div class="panel-heading">标签列表
            <button id="btn_add_new_tag" class="btn btn-default">添加</button>
            <input id="inputSearch" class="form-control" style="display: inline; width: auto;" type="text" />
            <button id="btnSearch" class="btn btn-default">查找</button></div>

        <table class="table">
            <thead>
            <tr><th>标签ID</th><th>标签名称</th><th>最大出价</th><th>标签类型ID</th><th>标签类型名称</th><th>操作</th></tr>
            </thead>
            <tbody>

            <%
                List<JSObject> data = new ArrayList<>();
                long totalPage = 0;
                long count = Tags.count();
                int index = Utils.parseInt(request.getParameter("page_index"), 0);
                int size = Utils.parseInt(request.getParameter("page_size"), 20);
                totalPage = count / size + (count % size == 0 ? 0 : 1);

                int preIndex = index > 0 ? index-1 : 0;
                int nextPage = index < totalPage - 1 ? index+1 : index;

                data = Tags.fetchData(index, size);
            %>

            <%
                for (int i = 0; i < data.size(); i++) {
                    JSObject one = data.get(i);
            %>
            <tr>
                <td><%=one.get("id")%></td>
                <td><%=one.get("tag_name")%></td>
                <td><%=one.get("max_bidding")%></td>
                <td><%=one.get("tag_category_id")%></td>
                <td><%=one.get("category_name")%></td>
                <td><a class="link_modify" href="#">修改</a><a class="link_delete" href="#">删除</a></td>
            </tr>
            <% } %>

            </tbody>
        </table>

        <nav aria-label="Page navigation">
            <ul class="pagination">
                <li>
                    <a href="tags.jsp?page_index=<%=preIndex%>" aria-label="Previous">
                        <span aria-hidden="true">上一页</span>
                    </a>
                </li>
                <li>
                    <a href="tags.jsp?page_index=<%=nextPage%>" aria-label="Next">
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

<div id="new_tag_dlg" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="dlg_title">添加标签</h4>
            </div>
            <div class="modal-body">
                <form id="modify_form" class="form-horizontal" action="#" autocomplete="off">
                    <div class="form-group">
                        <label for="inputTagName" class="col-sm-2 control-label">标签名称</label>
                        <div class="col-sm-10">
                            <input class="form-control" id="inputTagName" placeholder="标签名称" autocomplete="off">
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

<jsp:include page="loading_dialog.jsp"></jsp:include>

<script src="js/jquery.js"></script>
<script src="bootstrap/js/bootstrap.min.js"></script>
<script src="js/core.js"></script>

<script type="text/javascript">
    var modifyType = 'new';
    var id;

    $("#btn_add_new_tag").click(function() {
        modifyType = 'new';
        $('#delete_message').hide();
        $('#modify_form').show();
        $("#dlg_title").text("添加标签");
        $("#new_tag_dlg").modal("show");
    });

    $("#new_tag_dlg .btn-primary").click(function() {
        var tagName = $("#inputTagName").val();

        if (modifyType == 'new') {
            $.post('tags/create', {
                name: tagName
            }, function(data) {
                if (data && data.ret == 1) {
                    $("#new_tag_dlg").modal("hide");
                    location.reload();
                } else {
                    admanager.showCommonDlg("错误", data.message);
                }
            }, 'json');
        } else if (modifyType == 'update') {
            $.post('tags/update', {
                id: id,
                name: tagName
            }, function(data) {
                if (data && data.ret == 1) {
                    $("#new_tag_dlg").modal("hide");
                    location.reload();
                } else {
                    admanager.showCommonDlg("错误", data.message);
                }
            }, 'json');
        } else if (modifyType == 'delete') {
            $.post('tags/delete', {
                id: id,
                name: tagName
            }, function(data) {
                if (data && data.ret == 1) {
                    $("#new_tag_dlg").modal("hide");
                    location.reload();
                } else {
                    admanager.showCommonDlg("错误", data.message);
                }
            }, 'json');
        }
    });

    $('#btnSearch').click(function() {
        var query = $("#inputSearch").val();
        $.post('tags/query', {
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
            var td = $('<td></td>');
            td.text(one.id);
            tr.append(td);
            td = $('<td></td>');
            td.text(one.tag_name);
            tr.append(td);
            td = $('<td><a class="link_modify" href="#">修改</a><a class="link_delete" href="#">删除</a></td>');
            tr.append(td);
            $('.table tbody').append(tr);
        }
    }

    function bindOp() {
        $(".link_modify").click(function() {
            modifyType = "update";
            $('#delete_message').hide();
            $('#modify_form').show();

            $("#dlg_title").text("修改标签");

            var tds = $(this).parents("tr").find('td');
            id = $(tds.get(0)).text();
            var tagName = $(tds.get(1)).text();
            $("#inputTagName").val(tagName);

            $("#new_tag_dlg").modal("show");
        });

        $(".link_delete").click(function() {
            modifyType = "delete";
            $('#delete_message').show();
            $('#modify_form').hide();

            $("#dlg_title").text("删除标签");

            var tds = $(this).parents("tr").find('td');
            id = $(tds.get(0)).text();
            var tagName = $(tds.get(1)).text();
            $("#inputTagName").val(tagName);

            $("#new_tag_dlg").modal("show");
        });
    }

    bindOp();
</script>
<script src="js/interlaced-color-change.js"></script>
</body>
</html>
