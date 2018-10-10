<%@ page import="com.bestgo.admanager.utils.NumberUtil" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.bestgo.common.database.services.DB" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/rootBase.jsp" %>


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
    <%@include file="common/navigationbar.jsp" %>

    <div class="panel panel-default">
        <!-- Default panel contents -->
        <div class="panel-heading">标签列表
            <button id="btn_add_new_tag" class="btn btn-default">添加</button>
            <input id="inputSearch" class="form-control" style="display: inline; width: auto;" type="text"/>
            <button id="btnSearch" class="btn btn-default glyphicon glyphicon-search"></button>
        </div>

        <table class="table">
            <thead>
            <tr>
                <td colspan="10">是否计入统计---针对投放数据统计中的应用是否纳入到统计中。&nbsp;&nbsp;是否显示---显示的是正在投放的产品；不显示的是被暂停投放的产品，只是为了便于查看数据
                </td>
            </tr>
            <tr>
                <td colspan="10">标签名称---与标签ID唯一并存，命名必须是前后两端中间位置都不能有空格，并且是驼峰命名AaaBbbCcc</td>
            </tr>
            <tr>
                <th>标签ID</th>
                <th>标签名称</th>
                <th>最大出价</th>
                <th>应用品类名称</th>
                <th>期望收入</th>
                <th>期望盈利</th>
                <th>投放人员</th>
                <th>是否计入统计</th>
                <th>是否显示</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>

            <%
                List<JSObject> data = new ArrayList<>();
                long totalPage = 0;
                long count = Tags.count();
                int index = NumberUtil.parseInt(request.getParameter("page_index"), 0);
                int size = NumberUtil.parseInt(request.getParameter("page_size"), 20);
                totalPage = count / size + (count % size == 0 ? 0 : 1);

                int preIndex = index > 0 ? index - 1 : 0;
                int nextPage = index < totalPage - 1 ? index + 1 : index;

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
                <td><%=one.get("category_name")%></td>
                <td><%=one.get("anticipated_revenue")%></td>
                <td><%=one.get("anticipated_incoming")%></td>
                <td><%=one.get("nickname")%></td>
                <td><%=Integer.parseInt(one.get("is_statistics").toString()) == 1 ? "是" : "否"%></td>
                <td><%=Integer.parseInt(one.get("is_display").toString()) == 1 ? "是" : "否"%></td>
                <td><a class="link_modify glyphicon glyphicon-pencil" href="#"></a><a class="link_delete glyphicon glyphicon-remove" href="#"></a></td>
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
                <%
                    for (int i = 0; i < totalPage; i++) {
                %>
                <li><a href="tags.jsp?page_index=<%=i%>"><span><%=i + 1%></span></a></li>
                <% } %>
                <li>
                    <a href="tags.jsp?page_index=<%=nextPage%>" aria-label="Next">
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
                <h4 class="modal-title" id="dlg_title">添加标签</h4>
            </div>
            <div class="modal-body">
                <form id="modify_form" class="form-horizontal" action="#" autocomplete="off">
                    <div class="form-group">
                        <label for="inputTagName" class="col-sm-2 control-label">标签名称</label>
                        <div class="col-sm-8">
                            <input class="form-control" id="inputTagName" placeholder="标签名称" autocomplete="off">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="inputMaxBidding" class="col-sm-2 control-label">最大出价</label>
                        <div class="col-sm-8">
                            <input class="form-control" id="inputMaxBidding" placeholder="最大出价" autocomplete="off">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="category" class="col-sm-2 control-label">应用品类</label>
                        <div class="col-sm-8">
                            <select id="category" class="form-control">
                                <%
                                    List<JSObject> cag = DB.scan("web_ad_tag_category").select("id").select("category_name").execute();
                                    for (JSObject j : cag) {
                                %>
                                <option value="<%=j.get("id")%>"><%=j.get("category_name")%>
                                </option>
                                <% } %>

                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="inputRevenue" class="col-sm-2 control-label">期望收入</label>
                        <div class="col-sm-8">
                            <input class="form-control" id="inputRevenue" autocomplete="off">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="inputIncoming" class="col-sm-2 control-label">期望盈利</label>
                        <div class="col-sm-8">
                            <input class="form-control" id="inputIncoming" autocomplete="off">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="user" class="col-sm-2 control-label">投放人员</label>
                        <div class="col-sm-8">
                            <select id="user" class="form-control">
                                <%
                                    List<JSObject> user = DB.scan("web_ad_login_user").select("id").select("nickname").execute();
                                    for (JSObject j : user) {
                                %>
                                <option value="<%=j.get("id")%>"><%=j.get("nickname")%>
                                </option>
                                <% } %>

                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="isStatistics" class="col-sm-2 control-label">是否计入统计</label>
                        <div class="col-sm-8">
                            <select id="isStatistics" class="form-control">
                                <option value="1">是</option>
                                <option value="0">否</option>
                            </select>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="isDisplay" class="col-sm-2 control-label">是否显示</label>
                        <div class="col-sm-8">
                            <select id="isDisplay" class="form-control">
                                <option value="1">是</option>
                                <option value="0">否</option>
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
    var modifyType = 'new';
    var id;
    $("li[role='presentation']:eq(3)").addClass("active");
    $("#btn_add_new_tag").click(function () {
        $("#inputTagName").prop("disabled", false);
        $("#inputTagName").val("");
        $("#inputMaxBidding").val("");
        $("#inputRevenue").val("");
        $("#inputIncoming").val("");

        modifyType = 'new';
        $('#delete_message').hide();
        $('#modify_form').show();
        $("#dlg_title").text("添加标签");
        $("#new_tag_dlg").modal("show");
    });

    $("#new_tag_dlg .btn-primary").click(function () {
        var tagName = $("#inputTagName").val();
        var maxBidding = $("#inputMaxBidding").val();
        var tagCategoryId = $("#category").val();
        var anticipated_revenue = $("#inputRevenue").val();
        var anticipated_incoming = $("#inputIncoming").val();
        var user_id = $("#user").val();
        var is_statistics = $("#isStatistics").val();
        var is_display = $("#isDisplay").val();
        if (modifyType == 'new') {
            $.post('tags/create', {
                name: tagName,
                maxBidding: maxBidding,
                tagCategoryId: tagCategoryId,
                anticipated_revenue: anticipated_revenue,
                anticipated_incoming: anticipated_incoming,
                user_id: user_id,
                is_statistics: is_statistics,
                is_display: is_display
            }, function (data) {
                if (data && data.ret == 1) {
                    $("#new_tag_dlg").modal("hide");
                    location.reload();
                } else {
                    admanager.showCommonDlg("错误", data.message);
                }
            }, 'json');
        } else if (modifyType == 'update') {

            //《是否显示》为否，清空该标签的规则。
            if (is_display == 0) {
                $.post('rules/delByTagName', {
                    tagName: tagName,
                }, function (data) {

                }, 'json');
            }

            $.post('tags/update', {
                id: id,
                name: tagName,
                maxBidding: maxBidding,
                tagCategoryId: tagCategoryId,
                anticipated_revenue: anticipated_revenue,
                anticipated_incoming: anticipated_incoming,
                user_id: user_id,
                is_statistics: is_statistics,
                is_display: is_display
            }, function (data) {
                $("#inputTagName").prop("disabled", false);
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
            }, function (data) {
                if (data && data.ret == 1) {
                    $("#new_tag_dlg").modal("hide");
                    location.reload();
                } else {
                    admanager.showCommonDlg("错误", data.message);
                }
            }, 'json');
        }
    });

    $('#btnSearch').click(function () {
        var query = $("#inputSearch").val();
        $.post("tags/query", {
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
            td.text(one.max_bidding);
            tr.append(td);

            td = $('<td></td>');
            td.text(one.category_name);
            tr.append(td);

            td = $('<td></td>');
            td.text(one.anticipated_revenue);
            tr.append(td);

            td = $('<td></td>');
            td.text(one.anticipated_incoming);
            tr.append(td);

            td = $('<td></td>');
            td.text(one.user);
            tr.append(td);

            td = $('<td></td>');
            td.text(one.is_statistics == "1" ? "是" : "否");
            tr.append(td);

            td = $('<td></td>');
            td.text(one.is_display == "1" ? "是" : "否");
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

            $("#dlg_title").text("修改标签");

            var tds = $(this).parents("tr").find('td');
            id = $(tds.get(0)).text();
            var tagName = $(tds.get(1)).text();
            var maxBidding = $(tds.get(2)).text();
            var tagCategoryId = $(tds.get(3)).text();
            var revenue = $(tds.get(4)).text();
            var incoming = $(tds.get(5)).text();
            var user = $(tds.get(6)).text();
            var isStatistics = $(tds.get(7)).text();
            var isDisplay = $(tds.get(8)).text();
            $("#inputTagName").val(tagName).prop("disabled", true);
            $("#inputMaxBidding").val(maxBidding);
            $("#category option").each(function (idx) {
                var name = $(this).text().trim();
                if (name == tagCategoryId) {
                    $("#category").val($(this).val());
                    return;
                }
            });
            $("#inputRevenue").val(revenue);
            $("#inputIncoming").val(incoming);
            $("#user option").each(function (idx) {
                var name = $(this).text().trim();
                if (name == user) {
                    $("#user").val($(this).val());
                    return;
                }
            });
            $("#isStatistics option").each(function (idx) {
                var name = $(this).text();
                if (name == isStatistics) {
                    $("#isStatistics").val($(this).val());
                    return;
                }
            });

            $("#isDisplay option").each(function (idx) {
                var name = $(this).text();
                if (name == isDisplay) {
                    $("#isDisplay").val($(this).val());
                    return;
                }
            });

            $("#new_tag_dlg").modal("show");
        });
/**
 * 删除标签的功能是危险操作。这个功能取消
 */
        /*$(".link_delete").click(function () {
            return;
            modifyType = "delete";
            $('#delete_message').show();
            $('#modify_form').hide();

            $("#dlg_title").text("删除标签");

            var tds = $(this).parents("tr").find('td');
            id = $(tds.get(0)).text();
            var tagName = $(tds.get(1)).text();
            var maxBidding = $(tds.get(2)).text();
            var tagCategoryId = $(tds.get(3)).text();
            $("#inputTagName").val(tagName);
            $("#inputMaxBidding").val(maxBidding);
            $("#category").val(tagCategoryId);

            $("#new_tag_dlg").modal("show");
        });*/
    }

    bindOp();
</script>
<script src="js/interlaced-color-change.js"></script>
</body>
</html>
