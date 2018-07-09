<%@ page import="com.bestgo.admanager.utils.Utils" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/rootBase.jsp" %>

<html>
  <head>
    <title>查询</title>
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

    <%
      String accessToken = Utils.getAccessToken();
      String accountId = Utils.getFirstAdAccountId();
    %>
    <span id="accessToken" style="display: none;"><%=accessToken%></span>
    <span id="accountId" style="display: none;"><%=accountId%></span>

    <div class="panel panel-default">
      <!-- Default panel contents -->
      <div class="panel-heading">
        <input id="inputQueryText" type="text"/>
        <button id="btnQuery" class="btn btn-default glyphicon glyphicon-search"></button>
      </div>

      <table class="table">
        <thead>
        <tr><th>ID</th><th>名称</th><th>路径</th><th>类型</th><th>受众范围</th></tr>
        </thead>
        <tbody>
        <tr>
        </tr>
        </tbody>
      </table>
    </div>
  </div>


  <jsp:include page="loading_dialog.jsp"></jsp:include>

  <script type="text/javascript">
      $("li[role='presentation']:eq(5)").addClass("active");
    function targetSearch(q) {
      var accountId = $('#accountId').text().trim();
      var accessToken = $('#accessToken').text().trim();
      var url = "https://graph.facebook.com/v2.10/act_" + accountId + "/targetingsearch?q=" + encodeURIComponent(q);
      url += ("&access_token=" + accessToken);
      $.get(url, function(data) {
        $('.table tbody tr').remove();
        for (var i = 0; i < data.data.length; i++) {
          var one = data.data[i];
          var tr = $('<tr></tr>');
          var td = $('<td></td>');
          td.text(one.id);
          tr.append(td);
          td = $('<td></td>');
          td.text(one.name);
          tr.append(td);
          td = $('<td></td>');
          td.text(one.path.join(' -> '));
          tr.append(td);
          td = $('<td></td>');
          td.text(one.type);
          tr.append(td);
          td = $('<td></td>');
          td.text(formatNumber(one.audience_size));
          tr.append(td);
          $('.table tbody').append(tr);
        }
      }, 'json');
    }

    function bindOp() {
      $("#btnQuery").click(function() {
        var q = $('#inputQueryText').val();
        targetSearch(q);
      });

      $("#inputQueryText").change(function(e) {
        var q = $('#inputQueryText').val();
        targetSearch(q);
      });
    }

    function formatNumber(num, precision, separator) {
      var parts;
      // 判断是否为数字
      if (!isNaN(parseFloat(num)) && isFinite(num)) {
        // 把类似 .5, 5. 之类的数据转化成0.5, 5, 为数据精度处理做准, 至于为什么
        // 不在判断中直接写 if (!isNaN(num = parseFloat(num)) && isFinite(num))
        // 是因为parseFloat有一个奇怪的精度问题, 比如 parseFloat(12312312.1234567119)
        // 的值变成了 12312312.123456713
        num = Number(num);
        // 处理小数点位数
        num = (typeof precision !== 'undefined' ? num.toFixed(precision) : num).toString();
        // 分离数字的小数部分和整数部分
        parts = num.split('.');
        // 整数部分加[separator]分隔, 借用一个著名的正则表达式
        parts[0] = parts[0].toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1' + (separator || ','));

        return parts.join('.');
      }
      return NaN;
    }

    bindOp();
  </script>
  </body>
</html>
