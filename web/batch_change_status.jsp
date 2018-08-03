
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/rootBase.jsp"%>

<style>
  .red {
    color: red;
  }
  .blue {
    color: #0f0;
  }
  .green{
    color: green;
  }
  .ens{
    color: #bdf7ff;
  }
</style>
<html>
  <head>
    <title>批量修改状态</title>
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
      <div>
        💚💜💙💖<input id="btnSetZero" type="button" value="置零" style="color: #2b542c">💚💜💙💖<input id="btnDelete" type="button" value="删除" style="color: #c08b5f">
      </div>
      <table class="table">
        <thead>
        <tr>
          <th><input type="checkbox" id="allChk"></th><th>网络</th><th>序号</th><th>系列名称</th><th>失败次数</th>
          <th>错误信息
            <input type="text" id="inputLikeLastErrorMessage">
            <button id="btnFiltrateError" class="btn btn-sm"><span class="glyphicon glyphicon-filter">模糊筛选</span></button>
          </th>
        </tr>
        </thead>
        <tbody>
        </tbody>
      </table>
    </div>
  </div>
  <div>
    <nav aria-label="Page navigation">
      <ul class="pagination">
        <li>
                <span id="Page">第
                    <span><input type="text" id="pageNow" style="width:40px"></span>
                    <span>/</span>
                    <span id="totalPage"></span>页
                    <button id="goToPage">go</button>
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <button id="preIndex">上一页</button>
                    <button id="nextIndex">下一页</button>
                </span>
        </li>
      </ul>
    </nav>
  </div>

  <script>
    function fetchData(pageNow) {
      $.post('campaign/query_batch_change_status', {
          pageNow:pageNow
      }, function(data) {
        if (data && data.ret == 1) {
            var totalPage = data.total_page;
            $("#totalPage").text(totalPage);
            $("#pageNow").val(pageNow);
          $('.table tbody tr').remove();
          for (var i = 0; i < data.data.length; i++) {
            var one = data.data[i];
            var tr = $('<tr></tr>');
            var td = $('<td></td>');
            td.html("<input name='subChk' value= '" + one.id + "' type='checkbox'/>");
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


    // setInterval(function() {
    //   fetchData();
    // }, 1000 * 60);

    fetchData(1);

    //分页查询
    $("#Page").on("click","button",function(){
        var pageNow = 1;
        var elementClicked = $(this).attr("id");
        if(elementClicked == "goToPage"){
            pageNow = parseInt($("#pageNow").val());
        }else if(elementClicked == "preIndex"){
            var page = parseInt($("#pageNow").val());
            pageNow = page>1 ? page-1 : page;
        }else if(elementClicked == "nextIndex"){
            var page = parseInt($("#pageNow").val());
            var totalPage = parseInt($("#totalPage").text());
            pageNow = page < totalPage ? page+1 : totalPage;
        }
        fetchData(pageNow);
    });

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
                url: "batch_change_campaign_operator/modified_failed_count_of_batch_change_status",
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
                url: "batch_change_campaign_operator/delete_error_message_of_batch_change_status",
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
