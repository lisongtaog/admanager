<%@ page import="com.bestgo.admanager.Utils" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.bestgo.admanager.servlet.Logs" %>


<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="bootstrap/css/bootstrap.min.css" />
<link rel="stylesheet" href="bootstrap/css/bootstrap-theme.min.css" />

<html>
  <head>
    <title>广告系列状态</title>
  </head>
  <body>

  <%
    Object object = session.getAttribute("isAdmin");
    if (object == null) {
      response.sendRedirect("login.jsp");
    }
  %>

  <div class="container-fluid">
    <div class="panel panel-default" style="margin-top: 10px">
      <div class="panel-heading">
        <span id="todayResult"></span>
      </div>
    </div>

    <div class="panel panel-default">
      <table class="table">
        <thead>
        <tr><th>网络</th><th>序号</th><th>系列名称</th><th>失败次数</th><th>错误信息</th></tr>
        </thead>
        <tbody>
        </tbody>
      </table>
    </div>
  </div>

  <script src="js/jquery.js"></script>
  <script src="bootstrap/js/bootstrap.min.js"></script>

  <script>
    function fetchData() {
      $.post('campaign/query_status', {
      }, function(data) {
        if (data && data.ret == 1) {
          $('#todayResult').text("今日创建系列数量: " + data.today_create_count + ", 昨天创建数量: " + data.yesterdayData.count
                  + ", 安装数: " + data.yesterdayData.total_installed + ", 花费: " + data.yesterdayData.total_spend);

          $('.table tbody tr').remove();
          for (var i = 0; i < data.data.length; i++) {
            var one = data.data[i];
            var tr = $('<tr></tr>');
            var td = $('<td></td>');
            td.text(one.network);
            tr.append(td);
            td = $('<td></td>');
            td.text(one.id);
            tr.append(td);
            td = $('<td></td>');
            td.text(one.campaign_name);
            tr.append(td);
            td = $('<td></td>');
            td.text(one.failed_count);
            tr.append(td);
            td = $('<td></td>');
            td.text(one.last_error_message);
            tr.append(td);
            $('.table tbody').append(tr);
          }
        }
      }, 'json');
    }

    setInterval(function() {
      fetchData();
    }, 1000 * 60);

    fetchData();
  </script>
  </body>
</html>
