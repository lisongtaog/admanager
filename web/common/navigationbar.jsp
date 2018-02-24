<%--
  User: mengjun
  Date: 2018/2/8
  Time: 16:02
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<ul class="nav nav-pills">

    <%-- 下标从0开始 --%>
    <li role="presentation"><a href="index.jsp">首页</a></li>
    <li role="presentation"><a href="campaigns_create.jsp">创建广告</a></li>

        <%-- 2 --%>
    <li role="presentation"><a href="country_analysis_report.jsp">分析报告</a></li>
    <li role="presentation"><a href="released_data_statistics.jsp">投放数据统计</a></li>

        <%-- 4 --%>
    <li role="presentation"><a href="country_active_user_report.jsp">活跃用户报告</a></li>
    <li role="presentation"><a href="adaccounts.jsp">广告账号管理</a></li>

        <%-- 6 --%>
    <li role="presentation"><a href="adaccounts_admob.jsp">广告账号管理(AdMob)</a></li>
    <li role="presentation"><a href="campaigns.jsp">广告系列管理</a></li>

        <%-- 8 --%>
    <li role="presentation"><a href="campaigns_admob.jsp">广告系列管理(AdMob)</a></li>
    <li role="presentation"><a href="tags.jsp">标签管理</a></li>

        <%-- 10 --%>
    <li role="presentation"><a href="rules.jsp">规则</a></li>
    <li role="presentation"><a href="query.jsp">查询</a></li>

        <%-- 12 --%>
    <li role="presentation"><a href="system.jsp">系统管理</a></li>
    <li role="presentation"><a href="advert_insert.jsp">广告存储</a></li>

        <%-- 14 --%>
    <li role="presentation"><a href="country_revenue_spend.jsp">国家收支</a></li>
    <li role="presentation"><a href="summary.jsp">七天汇总</a></li>

        <%-- 16 --%>
    <li role="presentation"><a href="advert_conversions_insert.jsp">转化录入</a></li>
    <li role="presentation"><a href="campaigns_update_daily_log.jsp">日更记录</a></li>
</ul>
</body>
</html>