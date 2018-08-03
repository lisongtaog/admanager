<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.bestgo.common.database.services.DB" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/rootBase.jsp"%>

<html>
<head>
    <title>项目组/应用品类管理</title>
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

    <div class="panel panel-default col-sm-4" style="height:87%">
        <!-- Default panel contents -->
        <div class="panel-heading">
            <button id="new_team" class="btn btn-default"><span class="glyphicon glyphicon-plus"></span>项目组</button>
        </div>
        <div class="panel-body" style="overflow: scroll;max-height:90%">
            <table class="table-condensed subTab" id="team_management" style="width:100%">
                <thead>
                <tr><th>项目组ID</th><th>项目组名称</th><th>操作</th></tr>
                </thead>
                <tbody>

                <%
                    List<JSObject> data = new ArrayList<>();
                    data = DB.scan("web_ad_category_team").select("id").select("team_name").execute();
                %>

                <%
                    for (int i = 0; i < data.size(); i++) {
                        JSObject one = data.get(i);
                %>
                <tr>
                    <td><%=one.get("id")%></td>
                    <td><%=one.get("team_name")%></td>
                    <td><a class="glyphicon glyphicon-pencil" href="#" onclick="modifyTeam(this)"></a>
                        <a class="glyphicon glyphicon-remove" href="#" onclick="deleteTeam(this)"></a>
                    </td>
                </tr>
                <% } %>

                </tbody>
            </table>
        </div>
    </div>
    <div class="panel panel-default col-sm-8" style="height:87%">
        <div class="panel-heading">
            <button id="new_category" class="btn btn-default"><span class="glyphicon glyphicon-plus"></span>应用品类</button>
        </div>
        <div class="panel-body" style="overflow: scroll;max-height:90%">
            <table class="table-condensed subTab" id="category_management" style="width:100%">
                <thead>
                <tr><th>品类ID</th><th>品类名称</th><th>相关组</th><th>操作</th></tr>
                </thead>
                <tbody>

                <%
                    String sql= "select a.id,a.category_name,a.team_id,b.team_name from web_ad_tag_category a,web_ad_category_team b"+
                            " where a.team_id = b.id";
                    data = DB.findListBySql(sql);
                    for (int i = 0; i < data.size(); i++) {
                        JSObject one = data.get(i);
                %>
                <tr>
                    <td><%=one.get("id")%></td>
                    <td><%=one.get("category_name")%></td>
                    <td><%=one.get("team_name")%></td>
                    <td><a class="glyphicon glyphicon-pencil" href="#" onclick="modifyCategory(this)"></a>
                        <a class="glyphicon glyphicon-remove" href="#" onclick="deleteCategory(this)"></a>
                    </td>
                </tr>
                <% } %>

                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="loading_dialog.jsp"></jsp:include>

<script type="text/javascript">
    $("li[role='presentation']:eq(3)").addClass("active");

    // 添加新组
    $("#new_team").click(function(){
        var tr = $("<tr><td>(自动生成)</td><td><input type='text' style='width:83.5%'></td>" +
            "<td><a class='glyphicon glyphicon-ok' onclick='team_addition(this)'></a>" +
            "&nbsp;<a class='glyphicon glyphicon-remove' href='#' onclick='delete_itself(this)'></a><td></tr>");
        $("#team_management tbody").prepend(tr);
    });
    function team_addition(thizz){
        var team = $(thizz).parents("tr").find("input").val().trim();
        var check = confirm("确认添加？");
        if(check){
            if(team){
                $.post("team_category_management/add_new_team",{
                    team_name:team
                },function(data){
                    if(data && data.ret==1){
                        location.reload();
                    }else{
                        admanager.showCommonDlg("错误",data.message);
                    }
                },"json")
            }else{
                admanager.showCommonDlg("warning","非法输入！");
                setTimeout(function(){
                    $("#common_message_dialog").modal("hide");
                },1500)
            }
        }
    }

    function delete_itself(thizz){
        $(thizz).parents("tr").remove();
    }

    //修改项目组
    function modifyTeam(thizz){
        var tr = $(thizz).parents("tr");
        var td = $(thizz).parent();
        $(thizz).remove();
        var name = tr.find("td:eq(1)").text();
        tr.find("td:eq(1)").empty().append("<input type='text' style='100%' value='"+name+"'>");
        td.prepend("<a class='glyphicon glyphicon-ok' href='#' onclick='team_update(this)'></a>");
    }
    function team_update(thizz){
        var team = $(thizz).parents("tr").find("input").val().trim();
        var id = $(thizz).parents("tr").find("td:eq(0)").text();
        var td = $(thizz).parents("tr").find("td:eq(1)");
        $.post("team_category_management/update_team",{
            team_name:team,
            id:id
        },function(data){
            if(data && data.ret == 1){
                location.reload();
            }else{
                admanager.showCommonDlg("提示",data.message);
            }
        },"json")
    }

    // 删除项目组
    function deleteTeam(thizz){
        var id = $(thizz).parents("tr").find("td:eq(0)").text();
        var tr = $(thizz).parents("tr");
        var check = confirm("确认删除？");
        if(check){
            $.post("team_category_management/delete_team",{
                id:id
            },function(data){
                if(data && data.ret==1){
                    tr.remove();
                }else{
                    admanager.showCommonDlg("提示",data.message);
                }
            },"json")
        }
    }

    //添加新品类
    $("#new_category").click(function(){
        var tr = $("<tr><td>(自动生成)</td><td><input type='text' style='width:83.5%'></td><td><select></select></td>" +
            "<td><a class='glyphicon glyphicon-ok' href='#' onclick='category_addition(this)'></a>" +
            "&nbsp;<a class='glyphicon glyphicon-remove' href='#' onclick='delete_itself(this)'></a></td></tr>");
        $("#category_management").prepend(tr);
        var team = $("#team_management .glyphicon-pencil").parents("tr");
        team.each(function(e){
            var id = $(this).find("td:eq(0)").text();
            var name = $(this).find("td:eq(1)").text();
            var option = $("<option value='"+id+"'>"+name+"</option>");
            tr.find("select").append(option);
        });
    });
    function category_addition(thizz){
        var category = $(thizz).parents("tr").find("input").val().trim();
        var team_id = $(thizz).parents("tr").find("option:selected").val();
        var check = confirm("确认添加吗？");
        if(check){
            if(category){
                $.post("team_category_management/add_category",{
                    category:category,
                    team_id:team_id,
                },function(data){
                    if(data && data.ret==1){
                        location.reload();
                    }else{
                        admanager.showCommonDlg("提示",data.message);
                    }
                },"json")
            }else{
                admanager.showCommonDlg("warning","非法输入！");
                setTimeout(function(){
                    $("#common_message_dialog").modal("hide");
                },1500)
            }
        }
    }

    //修改品类
    function modifyCategory(thizz){
        var tr = $(thizz).parents("tr");
        $(thizz).parent().prepend("<a class='glyphicon glyphicon-ok' href='#' onclick='category_update(this)'></a>");
        $(thizz).remove();
        var category = tr.find("td:eq(1)").text();
        var team_name = tr.find("td:eq(2)").text();
        tr.find("td:eq(1)").empty().append("<input type='text' style='width:100%' value='"+category+"'>");
        tr.find("td:eq(2)").empty().append("<select></select>");
        var team = $("#team_management .glyphicon-pencil").parents("tr");
        team.each(function(e){
            var id = $(this).find("td:eq(0)").text();
            var name = $(this).find("td:eq(1)").text();
            var option = $("<option value='"+id+"'>"+name+"</option>");
            tr.find("select").append(option);
        });
        tr.find("option").each(function(idx){
            if(team_name == $(this).text() ){
                tr.find("select").val($(this).val());
            }
        });
    }
    function category_update(thizz){
        var category_id = $(thizz).parents("tr").find("td:eq(0)").text();
        var category = $(thizz).parents("tr").find("input").val();
        var team_id = $(thizz).parents("tr").find("option:selected").val();
        $.post("team_category_management/update_category",{
            category_id:category_id,
            category:category,
            team_id:team_id
        },function(data){
            if(data && data.ret==1){
                location.reload();
            }else{
                admanager.showCommonDlg("提示",data.message);
            }
        },"json");
    }

    //删除品类
    function deleteCategory(thizz){
        var category_id = $(thizz).parents("tr").find("td:eq(0)").text();
        var check = confirm("确认删除？");
        if(check){
            $.post("team_category_management/delete_category",{
                category_id:category_id
            },function(data){
                if(data && data.ret==1){
                    location.reload();
                }else{
                    admanager.showCommonDlg("提示",data.message);
                }
            },"json");
        }

    }

</script>
<script src="js/interlaced-color-change.js"></script>
</body>
</html>
