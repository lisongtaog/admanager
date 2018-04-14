<%--
  User: mengjun
  Date: 2018/2/8
  Time: 16:02
  Desc: 共有的导航栏部分
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
    <li role="presentation"><a href="country_analysis_report.jsp">国家分析报告</a></li>
    <li role="presentation"><a href="time_analysis_report.jsp">时间分析报告</a></li>

        <%-- 4 --%>
    <li role="presentation"><a href="released_data_statistics.jsp">投放数据统计</a></li>
    <li role="presentation"><a href="country_active_user_report.jsp">活跃用户报告</a></li>

        <%-- 6 --%>
    <li role="presentation"><a href="rules.jsp">Facebook规则</a></li>
    <li role="presentation"><a href="query.jsp">查询</a></li>

        <%-- 8 --%>
    <li role="presentation"><a href="system.jsp">系统管理</a></li>
    <li role="presentation"><a href="advert_insert.jsp">广告存储</a></li>

        <%-- 10 --%>
    <li role="presentation"><a href="country_revenue_spend.jsp">国家收支</a></li>

        <%-- 11 --%>
    <li role="presentation"><a href="advert_conversions_insert.jsp">转化录入</a></li>
    <li role="presentation"><a href="app_activity_daily.jsp">应用日更记录</a></li>

        <%-- 13 --%>
    <li role="presentation"><a href="project_team_total_ revenue_and_total_cost.jsp">项目组总收支</a></li>
    <li role="presentation"><a href="app_image_video_rel_insert.jsp">应用图片视频关联录入</a></li>

        <%-- 15 --%>
    <li role="presentation"><a href="material_analysis_report.jsp">素材分析报告</a></li>
    <li role="presentation"><a href="rules_admob.jsp">Admob规则</a></li>
</ul>
</body>
</html>
