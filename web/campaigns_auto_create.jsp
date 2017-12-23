<%@ page import="com.bestgo.admanager.Utils" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.bestgo.admanager.servlet.AutoCreateCampaign" %>
<%@ page import="java.lang.reflect.Array" %>


<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="bootstrap/css/bootstrap.min.css" />
<link rel="stylesheet" href="bootstrap/css/bootstrap-theme.min.css" />

<html>
  <head>
    <title>自动创建系列管理</title>
  </head>
  <body>

  <%
    Object object = session.getAttribute("isAdmin");
    if (object == null) {
      response.sendRedirect("login.jsp");
    }
  %>

  <div class="container-fluid">
    <div class="panel panel-default">
      <!-- Default panel contents -->
      <div class="panel-heading"><label>
        <input type="radio" name="optionsRadios" id="checkFacebook" checked>
        Facebook 广告
        </label>
        <label>
          <input type="radio" name="optionsRadios" id="checkAdwords">
          AdWords 广告
        </label>
        <input id="inputSearch" class="form-control" style="display: inline; width: auto;" type="text" />
        <button id="btnSearch" class="btn btn-default">查找</button></div>

      <%
        List<JSObject> data = new ArrayList<>();
        long totalPage = 0;
        int preIndex = 0;
        int nextPage = 0;
        String network = request.getParameter("network");
        ArrayList<String> networks = new ArrayList<>();
        networks.add("facebook");
        networks.add("adwords");
        if (networks.indexOf(network) == -1) {
          network = "facebook";
        }
        if ("facebook".equals(network)) {
          long count = AutoCreateCampaign.facebookCount();
          int index = Utils.parseInt(request.getParameter("page_index"), 0);
          int size = Utils.parseInt(request.getParameter("page_size"), 20);
          totalPage = count / size + (count % size == 0 ? 0 : 1);

          preIndex = index > 0 ? index - 1 : 0;
          nextPage = index < totalPage - 1 ? index + 1 : index;

          data = AutoCreateCampaign.facebookFetchData(index, size);
        } else {
          long count = AutoCreateCampaign.adwordsCount();
          int index = Utils.parseInt(request.getParameter("page_index"), 0);
          int size = Utils.parseInt(request.getParameter("page_size"), 20);
          totalPage = count / size + (count % size == 0 ? 0 : 1);

          preIndex = index > 0 ? index - 1 : 0;
          nextPage = index < totalPage - 1 ? index + 1 : index;

          data = AutoCreateCampaign.adwordsFetchData(index, size);
        }
      %>

      <table class="table">
        <thead>
        <tr><th>序号</th><th>应用</th><th>国家</th><th>语言</th><th>系列名称</th><th>预算</th><th>出价</th><th>操作</th><th>开启</th></tr>
        </thead>
        <tbody>
        <%
          for (int i = 0; i < data.size(); i++) {
            JSObject one = data.get(i);
        %>
        <tr>
          <td><%=one.get("id")%></td>
          <td><%=one.get("app_name")%></td>
          <td><%=one.get("country_region")%></td>
          <td><%=one.get("language")%></td>
          <td><%=one.get("campaign_name")%></td>
          <td><%=one.get("bugdet")%></td>
          <td><%=one.get("bidding")%></td>
          <td><a class="link_modify" target="_blank" href="campaigns_create.jsp?type=auto_create&network=<%=network%>&id=<%=one.get("id")%>">修改</a>&nbsp;&nbsp;<a class="link_delete" href="#">删除</a></td>
          <td><input class="checkbox_campaign_enable" type="checkbox" <% if (one.get("enabled").equals(1)) { %> checked <% }%> /></td>
        </tr>
        <% } %>

        </tbody>
      </table>

      <nav aria-label="Page navigation">
        <ul class="pagination">
          <li>
            <a href="campaigns_auto_create.jsp?network=<%=network%>&page_index=<%=preIndex%>" aria-label="Previous">
              <span aria-hidden="true">上一页</span>
            </a>
          </li>
          <li>
            <a href="campaigns_auto_create.jsp?network=<%=network%>&page_index=<%=nextPage%>" aria-label="Next">
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

  <div id="delete_dlg" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
          <h4 class="modal-title">提示</h4>
        </div>
        <div class="modal-body">
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
    var network = "<%=network%>";
    if (network == 'facebook') {
      $('#checkFacebook').prop('checked', true);
    } else {
      $('#checkAdwords').prop('checked', true);
    }
    $('#checkFacebook').click(function () {
      window.location.href = "campaigns_auto_create.jsp?network=facebook";
    });
    $('#checkAdwords').click(function() {
      window.location.href = "campaigns_auto_create.jsp?network=adwords";
    });
    $('#btnSearch').click(function() {
      var query = $("#inputSearch").val();
      $.post('auto_create_campaign/<%=network%>/query', {
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
        td.text(one.app_name);
        tr.append(td);
        td = $('<td></td>');
        td.text(one.country_region);
        tr.append(td);
        td = $('<td></td>');
        td.text(one.language);
        tr.append(td);
        td = $('<td></td>');
        td.text(one.campaign_name);
        tr.append(td);
        td = $('<td></td>');
        td.text(one.bugdet);
        tr.append(td);
        td = $('<td></td>');
        td.text(one.bidding);
        tr.append(td);
        td = $('<td><a class="link_modify" target="_blank" href="campaigns_create.jsp?type=auto_create&network=<%=network%>&id=' + one.id + '">修改</a><a class="link_delete" href="#">删除</a></td>');
        tr.append(td);
        td = $('<td></td>');
        td.html('<input class="checkbox_campaign_enable" type="checkbox" ' + (one.enabled == 1 ? 'checked' : '') + ' />');
        tr.append(td);
        $('.table tbody').append(tr);
      }
    }

    function bindOp() {
      $(".checkbox_campaign_enable").click(function() {
        var tr = $(this).parents("tr");
        var tds = tr.find('td');
        var id = $(tds.get(0)).text();

        var checked = $(this).prop('checked');
        $.post('auto_create_campaign/<%=network%>/enable', {
          id: id,
          enable: checked,
        }, function(data) {
          if (data && data.ret == 1) {
            admanager.showCommonDlg("成功", data.message);
          } else {
            admanager.showCommonDlg("错误", data.message);
          }
        }, 'json');

      });

      $(".link_delete").click(function() {
        var tr = $(this).parents("tr");
        var tds = tr.find('td');
        var id = $(tds.get(0)).text();

        $("#delete_dlg .btn-primary").unbind('click');
        $("#delete_dlg .btn-primary").click(function() {
          $('#delete_dlg').modal('hide');
          setTimeout(function () {
            $.post('auto_create_campaign/<%=network%>/delete', {
              id: id
            }, function(data) {
              if (data && data.ret == 1) {
                tr.remove();
                admanager.showCommonDlg("成功", data.message);
              } else {
                admanager.showCommonDlg("错误", data.message);
              }
            }, 'json');
          }, 10);
        });
        $('#delete_dlg').modal('show');
      });
    }

    bindOp();
  </script>
  <script src="js/interlaced-color-change.js"></script>
  </body>
</html>
