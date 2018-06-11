<%@ page import="com.bestgo.admanager.utils.Utils" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.bestgo.admanager.servlet.AdAccountAdmob" %>


<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="bootstrap/css/bootstrap.min.css" />
<link rel="stylesheet" href="bootstrap/css/bootstrap-theme.min.css" />

<html>
  <head>
    <title>Admob广告账号管理</title>
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

    <div class="panel panel-default">
      <!-- Default panel contents -->
      <div class="panel-heading">广告账号
        <button id="btn_add_new_account" class="btn btn-default">添加</button>
        <input id="inputSearch" class="form-control" style="display: inline; width: auto;" type="text" />
        <button id="btnSearch" class="btn btn-default glyphicon glyphicon-search"></button></div>

      <table class="table">
        <thead>
        <tr><th>序号</th><th>广告账号</th><th>缩写</th><th>操作</th></tr>
        </thead>
        <tbody>

        <%
          List<JSObject> data = new ArrayList<>();
          long totalPage = 0;
          long count = AdAccountAdmob.count();
          int index = Utils.parseInt(request.getParameter("page_index"), 0);
          int size = Utils.parseInt(request.getParameter("page_size"), 20);
          totalPage = count / size + (count % size == 0 ? 0 : 1);

          int preIndex = index > 0 ? index-1 : 0;
          int nextPage = index < totalPage - 1 ? index+1 : index;

          data = AdAccountAdmob.fetchData(index, size);
        %>

        <%
          for (int i = 0; i < data.size(); i++) {
            JSObject one = data.get(i);
        %>
        <tr>
          <td><%=one.get("id")%></td>
          <td><%=one.get("account_id")%></td>
          <td><%=one.get("short_name")%></td>
          <td><a class="link_modify glyphicon glyphicon-pencil" href="#"></a>&nbsp;<a class="link_delete glyphicon glyphicon-remove" href="#"></a></td>
        </tr>
        <% } %>

        </tbody>
      </table>

      <nav aria-label="Page navigation">
        <ul class="pagination">
          <li>
            <a href="adaccounts_admob.jsp?page_index=<%=preIndex%>" aria-label="Previous">
              <span aria-hidden="true">上一页</span>
            </a>
          </li>
          <li>
            <a href="adaccounts_admob.jsp?page_index=<%=nextPage%>" aria-label="Next">
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

  <div id="new_account_dlg" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
          <h4 class="modal-title" id="dlg_title">添加广告账号</h4>
        </div>
        <div class="modal-body">
          <form id="modify_form" class="form-horizontal" action="#" autocomplete="off">
            <div class="form-group">
              <label for="inputAdAccount" class="col-sm-2 control-label">广告账号</label>
              <div class="col-sm-10">
                <input class="form-control" id="inputAdAccount" placeholder="广告账号" autocomplete="off">
              </div>
            </div>
            <div class="form-group">
              <label for="inputShortName" class="col-sm-2 control-label">缩写</label>
              <div class="col-sm-10">
                <input type="text" class="form-control" id="inputShortName" placeholder="缩写" autocomplete="off">
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

    $("li[role='presentation']:eq(8)").addClass("active");

    $("#btn_add_new_account").click(function() {
      modifyType = 'new';
      $('#delete_message').hide();
      $('#modify_form').show();
      $("#dlg_title").text("添加广告账号");
      $("#new_account_dlg").modal("show");
    });

    $("#new_account_dlg .btn-primary").click(function() {
      var account = $("#inputAdAccount").val();
      var shortName = $("#inputShortName").val();

      if (modifyType == 'new') {
        $.post('adaccount_admob/create', {
          account: account,
          shortName: shortName
        }, function(data) {
          if (data && data.ret == 1) {
            $("#new_account_dlg").modal("hide");
            location.reload();
          } else {
            admanager.showCommonDlg("错误", data.message);
          }
        }, 'json');
      } else if (modifyType == 'update') {
        $.post('adaccount_admob/update', {
          id: id,
          account: account,
          shortName: shortName
        }, function(data) {
          if (data && data.ret == 1) {
            $("#new_account_dlg").modal("hide");
            location.reload();
          } else {
            admanager.showCommonDlg("错误", data.message);
          }
        }, 'json');
      } else if (modifyType == 'delete') {
        $.post('adaccount_admob/delete', {
          id: id,
          account: account,
          shortName: shortName
        }, function(data) {
          if (data && data.ret == 1) {
            $("#new_account_dlg").modal("hide");
            location.reload();
          } else {
            admanager.showCommonDlg("错误", data.message);
          }
        }, 'json');
      }
    });

    $('#btnSearch').click(function() {
      var query = $("#inputSearch").val();
      $.post('adaccount_admob/query', {
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
        td.text(one.account_id);
        tr.append(td);
        td = $('<td></td>');
        td.text(one.short_name);
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

        $("#dlg_title").text("修改广告账号");

        var tds = $(this).parents("tr").find('td');
        id = $(tds.get(0)).text();
        var account = $(tds.get(1)).text();
        var shortName = $(tds.get(2)).text();
        $("#inputAdAccount").val(account);
        $("#inputShortName").val(shortName);

        $("#new_account_dlg").modal("show");
      });

      $(".link_delete").click(function() {
        modifyType = "delete";
        $('#delete_message').show();
        $('#modify_form').hide();

        $("#dlg_title").text("删除广告账号");

        var tds = $(this).parents("tr").find('td');
        id = $(tds.get(0)).text();
        var account = $(tds.get(1)).text();
        var shortName = $(tds.get(2)).text();
        $("#inputAdAccount").val(account);
        $("#inputShortName").val(shortName);

        $("#new_account_dlg").modal("show");
      });
    }

    bindOp();
  </script>
  <script src="js/interlaced-color-change.js"></script>
  </body>
</html>
