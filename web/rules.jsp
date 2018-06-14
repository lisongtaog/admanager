<%@ page import="com.bestgo.admanager.utils.Utils" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.bestgo.admanager.servlet.Rules" %>


<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="bootstrap/css/bootstrap.min.css" />
<link rel="stylesheet" href="bootstrap/css/bootstrap-theme.min.css" />

<html>
  <head>
    <title>规则</title>
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
      <div class="panel-heading">
        <a href="campain_monitor_log.jsp" target="_blank">关停记录</a>
        <button id="btn_add_new_rule" class="btn btn-default">添加</button>
        <input id="inputQueryText" type="text"/>
        <button id="btnQuery" class="btn btn-default glyphicon glyphicon-search"></button>
      </div>

      <table class="table">
        <thead>
        <tr><th>序号</th><th>规则类型</th><th>内容</th><th>操作</th></tr>
        </thead>
        <tbody>

        <%
          List<JSObject> data = new ArrayList<>();
          long totalPage = 0;
          long count = Rules.count();
          int index = Utils.parseInt(request.getParameter("page_index"), 0);
          int size = Utils.parseInt(request.getParameter("page_size"), 20);
          totalPage = count / size + (count % size == 0 ? 0 : 1);

          int preIndex = index > 0 ? index-1 : 0;
          int nextPage = index < totalPage - 1 ? index+1 : index;

          data = Rules.fetchData(index, size);
        %>

        <%
          for (int i = 0; i < data.size(); i++) {
            JSObject one = data.get(i);
        %>
        <tr>
          <td><%=one.get("id")%></td>
          <td><%=one.get("rule_type")%></td>
          <td><%=one.get("rule_content")%></td>
          <td><a class="link_modify glyphicon glyphicon-pencil" href="#"></a>&nbsp;&nbsp;<a class="link_delete glyphicon glyphicon-remove" href="#"></a></td>
        </tr>
        <% } %>

        </tbody>
      </table>

      <nav aria-label="Page navigation">
        <ul class="pagination">
          <li>
            <a href="rules.jsp?page_index=<%=preIndex%>" aria-label="Previous">
              <span aria-hidden="true">上一页</span>
            </a>
          </li>
          <li>
            <a href="rules.jsp?page_index=<%=nextPage%>" aria-label="Next">
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

  <div id="new_rule_dlg" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
          <h4 class="modal-title" id="dlg_title">添加规则</h4>
        </div>
        <div class="modal-body">
          <form id="modify_form" class="form-horizontal" action="#" autocomplete="off">
            <div class="form-group">
              <label for="inputRuleType" class="col-sm-2 control-label">规则类型</label>
              <div class="col-sm-10">
                <select class="form-control" id="inputRuleType">
                  <option value="1">类型1（监控广告系列）</option>
                  <option value="2">类型2（监控应用）</option>
                  <option value="3">类型3（监控国家）</option>
                </select>
              </div>
            </div>
            <div class="form-group">
              <label for="inputRuleContent" class="col-sm-2 control-label">缩写</label>
              <div class="col-sm-10">
                <input type="text" class="form-control" id="inputRuleContent" placeholder="规则内容" autocomplete="off">
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
    $("li[role='presentation']:eq(4)").addClass("active");

    $("#btn_add_new_rule").click(function() {
      modifyType = 'new';
      $('#delete_message').hide();
      $('#modify_form').show();
      $("#dlg_title").text("添加规则");
      $("#new_rule_dlg").modal("show");
    });
    $('#inputRuleContent').attr('placeholder', "campaign_id=xxx,conversions>xxx,cpa>xxx");

    $('#inputRuleType').change(function() {
      var ruleContent = $('#inputRuleContent').val();
      var ruleType = $('#inputRuleType').val();
      if (ruleContent.indexOf('xxx') >= 0 || ruleContent == '') {
        if (ruleType == 1) {
          $('#inputRuleContent').val("campaign_id=xxx,conversions>xxx,cpa>xxx")
        } else if (ruleType == 2) {
          $('#inputRuleContent').val("app_name=xxx,cost>xxx,cpa>xxx")
        }
      }
    });

    $("#new_rule_dlg .btn-primary").click(function() {
      var ruleType = $("#inputRuleType").val();
      var ruleContent = $("#inputRuleContent").val();

      if (modifyType == 'new') {
        $.post('rules/create', {
          ruleType: ruleType,
          ruleContent: ruleContent
        }, function(data) {
          if (data && data.ret == 1) {
            $("#new_rule_dlg").modal("hide");
            location.reload();
          } else {
            admanager.showCommonDlg("错误", data.message);
          }
        }, 'json');
      } else if (modifyType == 'update') {
        $.post('rules/update', {
          id: id,
          ruleType: ruleType,
          ruleContent: ruleContent
        }, function(data) {
          if (data && data.ret == 1) {
            $("#new_rule_dlg").modal("hide");
            location.reload();
          } else {
            admanager.showCommonDlg("错误", data.message);
          }
        }, 'json');
      } else if (modifyType == 'delete') {
        $.post('rules/delete', {
          id: id,
          ruleType: ruleType,
          ruleContent: ruleContent
        }, function(data) {
          if (data && data.ret == 1) {
            $("#new_rule_dlg").modal("hide");
            location.reload();
          } else {
            admanager.showCommonDlg("错误", data.message);
          }
        }, 'json');
      }
    });

    function setData(data) {
      for (var i = 0; i < data.length; i++) {
        var one = data[i];
        var tr = $('<tr></tr>');
        var td = $('<td></td>');
        td.text(one.id);
        tr.append(td);
        td = $('<td></td>');
        td.text(one.rule_type);
        tr.append(td);
        td = $('<td></td>');
        td.text(one.rule_content);
        tr.append(td);
        td = $('<td><a class="link_modify glyphicon glyphicon-pencil" href="#"></a><a class="link_delete glyphicon glyphicon-remove" href="#"></a></td>');
        tr.append(td);
        $('.table tbody').append(tr);
      }
    }

    function bindOp() {
      $(".link_modify").click(function() {
        modifyType = "update";
        $('#delete_message').hide();
        $('#modify_form').show();

        $("#dlg_title").text("修改规则");

        var tds = $(this).parents("tr").find('td');
        id = $(tds.get(0)).text();
        var ruleType = $(tds.get(1)).text();
        var ruleContent = $(tds.get(2)).text();
        $("#inputRuleType").val(ruleType);
        $("#inputRuleContent").val(ruleContent);

        $("#new_rule_dlg").modal("show");
      });

      $(".link_delete").click(function() {
        modifyType = "delete";
        $('#delete_message').show();
        $('#modify_form').hide();

        $("#dlg_title").text("删除规则");

        var tds = $(this).parents("tr").find('td');
        id = $(tds.get(0)).text();
        var ruleType = $(tds.get(1)).text();
        var ruleContent = $(tds.get(2)).text();
        $("#inputRuleType").val(ruleType);
        $("#inputRuleContent").val(ruleContent);

        $("#new_rule_dlg").modal("show");
      });
    }

    $('#btnQuery').click(function() {
      var query = $('#inputQueryText').val();
      $.post('rules/query', {
        text: query,
      }, function(data) {
        if (data && data.ret == 1) {
          $('.table tbody tr').remove();
          for (var i = 0; i < data.data.length; i++) {
            var one = data.data[i];
            var tr = $('<tr></tr>');
            var td = $('<td></td>');
            td.text(one.id);
            tr.append(td);
            td = $('<td></td>');
            td.text(one.rule_type);
            tr.append(td);
            td = $('<td></td>');
            td.text(one.rule_content);
            tr.append(td);
            td = $('<td><a class="link_modify" href="#">修改</a><a class="link_delete" href="#">删除</a></td>');
            tr.append(td);
            $('.table tbody').append(tr);
          }
          bindOp();
        } else {
          admanager.showCommonDlg("错误", data.message);
        }
      }, 'json');
    });

    bindOp();
  </script>
  <script src="js/interlaced-color-change.js"></script>
  </body>
</html>
