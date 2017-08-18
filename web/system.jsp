<%@ page import="com.bestgo.admanager.Utils" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.bestgo.common.database.services.DB" %>


<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="bootstrap/css/bootstrap.min.css" />
<link rel="stylesheet" href="bootstrap/css/bootstrap-theme.min.css" />

<html>
  <head>
    <title>系统管理</title>
  </head>
  <body>

  <%
    String[] zhangyifan的外号 = {"胖凡", "张二凡", "张二毛"};
    if (request.getMethod().equals("POST")) {
      request.setCharacterEncoding("utf-8");
      String userName = request.getParameter("inputUserName");
      for (int i = 0; i < zhangyifan的外号.length; i++) {
        if (userName.equals(zhangyifan的外号[i])) {
          session.setAttribute("isZhangYiFan", true);
          break;
        }
      }
    }
    Object isZhangYiFan = session.getAttribute("isZhangYiFan");
    Object object = session.getAttribute("isAdmin");
    if (object == null) {
      response.sendRedirect("login.jsp");
    }

    List<JSObject> list = DB.scan("web_system_config").select("config_key", "config_value").execute();
  %>

  <div class="container-fluid">
    <ul class="nav nav-pills">
      <li role="presentation"><a href="index.jsp">首页</a></li>
      <li role="presentation"><a href="adaccounts.jsp">广告账号管理</a></li>
      <li role="presentation"><a href="campaigns.jsp">广告系列管理</a></li>
      <li role="presentation"><a href="tags.jsp">标签管理</a></li>
      <li role="presentation"><a href="rules.jsp">规则</a></li>
      <li role="presentation"><a href="query.jsp">查询</a></li>
      <li role="presentation" class="active"><a href="#">系统管理</a></li>
    </ul>

    <%
      if (isZhangYiFan == null) {
    %>
    <form method="POST" action="system.jsp" enctype="application/x-www-form-urlencoded">
      <div class="form-group">
        <label for="inputUserName" class="col-sm-2 control-label">请输入张一凡的外号</label>
        <div class="col-sm-10">
          <input class="form-control" name="inputUserName" id="inputUserName" placeholder="张一凡的外号">
        </div>
      </div>
      <div class="form-group">
        <div class="col-sm-offset-2 col-sm-10">
          <button type="submit" class="btn btn-primary" id="btnLogin">确定</button>
        </div>
      </div>
    </form>
    <%
      } else {
    %>
    你成功的认识了张一凡
    <div class="panel panel-default">
      <!-- Default panel contents -->
      <div class="panel-heading">
        参数列表
      </div>

      <table class="table">
        <thead>
        <tr><th>参数名称</th><th>参数值</th><th>操作</th></tr>
        </thead>
        <tbody>
        <%for (int i = 0; i < list.size(); i++) {%>
        <tr>
          <td><%=list.get(i).get("config_key")%></td>
          <td><input style="width: 100%;" class=".keyValue" value="<%=list.get(i).get("config_value")%>" /></td>
          <td><a class="link_modify" href="javascript:void(0)">更新</a></td>
        </tr>
        <% } %>
        </tbody>
      </table>
    </div>
    <% } %>

  <jsp:include page="loading_dialog.jsp"></jsp:include>

  <script src="js/jquery.js"></script>
  <script src="bootstrap/js/bootstrap.min.js"></script>
  <script src="js/core.js"></script>

  <script type="text/javascript">
    function bindOp() {
      $('.link_modify').click(function () {
        var tds = $(this).parents("tr").find('td');
        var key = $(tds.get(0)).text();
        var value = $(tds.get(1)).find("input").val();

        $.post('system/update', {
          configKey: key,
          configValue: value
        }, function(data) {
          if (data && data.ret == 1) {
            alert('更新成功');
            location.reload();
          } else {
            admanager.showCommonDlg("错误", data.message);
          }
        }, 'json');
      });
    }

    bindOp();
  </script>
  </body>
</html>
