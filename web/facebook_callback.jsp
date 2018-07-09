<%@ page import="java.util.Calendar" %><%--
  Created by IntelliJ IDEA.
  User: jikai
  Date: 5/16/17
  Time: 9:24 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/rootBase.jsp"%>
<html>
<head>
    <title>Success</title>
</head>
<body>

<%
    String access_token = request.getParameter("access_token");
    String expires_in = request.getParameter("expires_in");
    String code = request.getParameter("code");
    if (access_token != null && expires_in != null && code != null) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 0);
        session.setAttribute("expire_time", calendar.getTime().getTime() + Integer.parseInt(expires_in));
        session.setAttribute("access_token", access_token);
        session.setAttribute("code", code);
        session.setAttribute("isLogin", true);

        response.sendRedirect("index.jsp");
    }
%>

<script>
    function reload() {
        var hash = location.hash;
        if (hash) {
            if (hash.length > 0) {
                hash = hash.substring(1);
                location.href = "facebook_callback.jsp?" + hash;
            }
        }
    }

    reload();
</script>

</body>
</html>
