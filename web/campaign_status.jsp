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
      <div class="panel-heading">
        <ul id="reduceResult"></ul>
      </div>
    </div>

    <div class="panel panel-default">
      <div>
        💚💜💙💖<input id="btnSetZero" type="button" value="置零" style="color: #2b542c">💚💜💙💖<input id="btnDelete" type="button" value="删除" style="color: #c08b5f">
        💚💜💙💖<input type="text" id="inputLikeLastErrorMessage" /><input id="btnFiltrateError" type="button" value="模糊筛选" style="color: #4385c0">💚💜💙💖
      </div>
      <table class="table">
        <thead>
        <tr><th>☆</th><th>网络</th><th>序号</th><th>系列名称</th><th>失败次数</th><th>错误信息</th></tr>
        <tr><th><input type="checkbox" id="allChk"/></th><th>全选</th><th></th><th></th><th></th><th></th></tr>
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
//          $('#todayResult').text("今日创建系列数量: " + data.today_create_count + ", 昨天创建数量: " + data.yesterdayData.count
//                  + ", 安装数: " + data.yesterdayData.total_installed + ", 花费: " + data.yesterdayData.total_spend);
//          $('#reduceResult').html("");
//          for (var i = 0; i < data.reduceArr.length; i++) {
//            var one = data.reduceArr[i];
//            $('#reduceResult').append($("<li>" + one.appName + " : " + one.cost  + "</li>"));
//          }

          $('.table tbody tr').remove();
          for (var i = 0; i < data.data.length; i++) {
            var one = data.data[i];
            var tr = $('<tr></tr>');
            var td = $('<td></td>');
            td.html("<input name='subChk' value= '" + one.id + "-"+ one.network + "' type='checkbox'/>");
            tr.append(td);
            td = $('<td></td>');
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

    $("#btnFiltrateError").click(function(){
        var likeLastErrorMessage = $("#inputLikeLastErrorMessage").val();
        if(likeLastErrorMessage == ""){
            $(".table tbody tr").each(function() {
                var last_error_message = $(this).children('td').eq(5).html();
                if(last_error_message == ""){
                    $(this).show();
                }else{
                    $(this).hide();
                }
            });
        }else{
            $(".table tbody tr").each(function() {
                var last_error_message = $(this).children('td').eq(5).html();
                if(last_error_message == ""){
                    $(this).hide();
                }else if(last_error_message.indexOf(likeLastErrorMessage) == -1){
                    $(this).hide();
                }else{
                    $(this).show();
                }
            });
        }

    });

    $("#btnSetZero").click(function(){
        // 判断是否至少选择一项
        var checkedNum = $("input[name='subChk']:checked").length;
        if(checkedNum == 0) {
            alert("请选择至少一项！");
            return;
        }
        // 批量选择
        if(confirm("确定要修改所选系列？")) {
            var checkedList = new Array();
            $("input[name='subChk']:checked").each(function() {
                checkedList.push($(this).val());
            });
            $.ajax({
                type: "POST",
                url: "create_campaign_operator/modified_failed_count_of_campaign",
                data: {'modifiedms':checkedList.toString()},
                success: function(result) {
                    if (result && result.ret == 1) {
                        fetchData();
                    }
                }
            });
        }
    });

    $("#btnDelete").click(function(){
        // 判断是否至少选择一项
        var checkedNum = $("input[name='subChk']:checked").length;
        if(checkedNum == 0) {
            alert("请选择至少一项！");
            return;
        }
        // 批量选择
        if(confirm("确定要删除所选系列？")) {
            var checkedList = new Array();
            $("input[name='subChk']:checked").each(function() {
                checkedList.push($(this).val());
            });
            $.ajax({
                type: "POST",
                url: "create_campaign_operator/delete_error_message_of_campaign",
                data: {'delitems':checkedList.toString()},
                success: function(result) {
                    if (result && result.ret == 1) {
//                        $("input[name='subChk']:checked").remove();
                        fetchData();
                    }
                }
            });
        }
    });

    // 全选
    $("#allChk").click(function() {
        $("input[name='subChk']").prop("checked",this.checked);
        $("input[name='subChk']:hidden").prop("checked",false);
    });
  </script>
  </body>
</html>
