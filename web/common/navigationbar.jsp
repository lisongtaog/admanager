<%--
  User: mengjun
  Date: 2018/2/8
  Time: 16:02
  Desc: 共有的导航栏部分
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div id="navbar" class="navbar-collapse collapse">
    <ul class="nav navbar-nav navbar-right">
        <li style="padding-top:8px;">
            <div class="btn-group">
                <button type="button" class="btn btn-default btn-success dropdown-toggle" data-toggle="dropdown">
                    <i class="glyphicon glyphicon-user"></i> ${sessionScope.loginUser.nickname} <span class="caret"></span>
                </button>
                <ul class="dropdown-menu" role="menu">
                    <li><a href="#"><i class="glyphicon glyphicon-cog"></i> 个人设置</a></li>
                    <li><a href="#" id="type${sessionScope.loginUser.userType}"><i class="glyphicon glyphicon-comment"></i> 消息</a></li>
                    <li class="divider"></li>
                    <li><a href="login.jsp"><i class="glyphicon glyphicon-off"></i> 退出系统</a></li>
                </ul>
            </div>
        </li>
        <li style="margin-left:10px;padding-top:8px;">
            <button type="button" class="btn btn-default btn-danger">
                <span class="glyphicon glyphicon-question-sign"></span> 帮助
            </button>
        </li>
    </ul>
</div>

<ul class="nav nav-pills" style="font-weight:bold;font-size:15px">

    <%-- 下标从0开始 --%>
        <li role="presentation"><a href="index.jsp">首页</a></li>
        <li role="presentation"><a href="campaigns_create.jsp">创建广告</a></li>

        <%-- 2 --%>
        <li role="presentation" class="dropdown">
            <a  href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true">
                分析报告
                <span class="caret"></span>
            </a>
            <ul class="dropdown-menu">
                <li><a href="country_analysis_report.jsp">国家分析报告</a></li>
                <li><a href="time_analysis_report.jsp">时间分析报告</a></li>
                <li><a href="released_data_statistics.jsp">投放数据统计</a></li>
                <li><a href="campaign_active_user_analysis.jsp">活跃用户报告</a></li>
                <li><a href="campaign_impressions_revenue_analysis.jsp">广告收益报告</a></li>
                <li><a href="material_analysis_report.jsp">素材分析报告</a></li>
            </ul>
        </li>
        <%-- 3 --%>
        <li role="presentation" class="dropdown">
            <a  href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true">
                系统管理
                <span class="caret"></span>
            </a>
            <ul class="dropdown-menu">
                <li><a href="system.jsp" >系统管理</a></li>
                <li><a href="campaigns_adUnit_admob.jsp" >广告单元(开发中)</a></li>
                <li class="divider"></li>
                <li><a href="adaccounts.jsp" target="_blank">Facebook广告账号管理</a></li>
                <li><a href="adaccounts_admob.jsp" target="_blank">Admob广告账号管理</a></li>
                <li><a href="campaigns.jsp" target="_blank">Facebook广告系列管理</a></li>
                <li><a href="campaigns_admob.jsp" target="_blank">Admob广告系列管理</a></li>
                <li><a href="tags.jsp" target="_blank">标签管理</a></li>
                <li><a href="team_category_management.jsp" target="_blank">项目组/应用品类管理</a></li>
            </ul>
        </li>

        <%-- 4 --%>
        <li role="presentation"><a href="rules.jsp">规则</a></li>
        <li role="presentation"><a href="query.jsp">查询</a></li>
        <li role="presentation"><a href="advert_insert.jsp">广告存储</a></li>

        <%-- 7 --%>
        <li role="presentation"><a href="advert_conversions_insert.jsp">转化录入</a></li>
        <li role="presentation"><a href="app_activity_daily.jsp">应用日更记录</a></li>

        <%-- 9 --%>
        <li role="presentation"><a href="project_team_total_ revenue_and_total_cost.jsp">项目组总收支</a></li>
        <li role="presentation"><a href="app_image_video_rel_insert.jsp">应用图片视频关联录入</a></li>

</ul>

<script>
    /*var privilege = document.getElementById("type1");
    if(privilege){
        $.post("login/applicationCheck",function(data){
            if(data && (data.count > 0)){
                var a = $(privilege);
                a.append("<span class='badge'>"+data.count+"</span>");
            }
        },"json");
        privilege.setAttribute("href","system_management.jsp");
        privilege.setAttribute("target","_blank");
    }*/
</script>

