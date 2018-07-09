<%@ page import="com.bestgo.admanager.utils.NumberUtil" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.bestgo.admanager.servlet.Logs" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/rootBase.jsp"%>

<html>
  <head>
    <title>关停记录</title>
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
      <div class="panel-heading">关停记录
        <input id="inputQueryText" type="text"/>
        <button id="btnQuery" class="btn btn-default glyphicon glyphicon-search"></button>
      </div>

      <table class="table">
        <thead>
        <tr><th>序号</th><th>日期</th><th>分类</th><th>子分类</th><th>内容</th></tr>
        </thead>
        <tbody>

        <%
          List<JSObject> data = new ArrayList<>();
          long totalPage = 0;
          long count = Logs.count("system", "campaign_monitor");
          int index = NumberUtil.parseInt(request.getParameter("page_index"), 0);
          int size = NumberUtil.parseInt(request.getParameter("page_size"), 20);
          totalPage = count / size + (count % size == 0 ? 0 : 1);

          int preIndex = index > 0 ? index-1 : 0;
          int nextPage = index < totalPage - 1 ? index+1 : index;

          data = Logs.fetchData("system", "campaign_monitor", index, size);
        %>

        <%
          for (int i = 0; i < data.size(); i++) {
            JSObject one = data.get(i);
        %>
        <tr>
          <td><%=one.get("id")%></td>
          <td><%=one.get("log_time")%></td>
          <td><%=one.get("category")%></td>
          <td><%=one.get("sub_category")%></td>
          <td><%=one.get("content")%></td>
        </tr>
        <% } %>

        </tbody>
      </table>

      <nav aria-label="Page navigation">
        <ul class="pagination">
          <li>
            <a href="campain_monitor_log.jsp?page_index=<%=preIndex%>" aria-label="Previous">
              <span aria-hidden="true">上一页</span>
            </a>
          </li>
          <li>
            <a href="campain_monitor_log.jsp?page_index=<%=nextPage%>" aria-label="Next">
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

  <jsp:include page="loading_dialog.jsp"></jsp:include>

  <script>
    $('#btnQuery').click(function() {
      var query = $('#inputQueryText').val();
      $.post('logs/query', {
        text: query,
        category: "system",
        subCategory: "campaign_monitor"
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
            td.text(one.log_time);
            tr.append(td);
            td = $('<td></td>');
            td.text(one.category);
            tr.append(td);
            td = $('<td></td>');
            td.text(one.sub_category);
            tr.append(td);
            td = $('<td></td>');
            td.text(one.content);
            tr.append(td);
            $('.table tbody').append(tr);
          }
        } else {
          admanager.showCommonDlg("错误", data.message);
        }
      }, 'json');
    });

  </script>
  </body>
</html>
