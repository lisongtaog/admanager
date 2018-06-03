<%@ page import="com.bestgo.admanager.utils.Utils" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page import="com.bestgo.admanager.servlet.AdAccount" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.google.gson.JsonArray" %>


<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="bootstrap/css/bootstrap.min.css" />
<link rel="stylesheet" href="bootstrap/css/bootstrap-theme.min.css" />
<link rel="stylesheet" href="css/core.css"/>
<link rel="stylesheet" href="css/bootstrap-tagsinput.css"/>
<link rel="stylesheet" href="css/bootstrap-datetimepicker.css"/>
<link rel="stylesheet" href="jqueryui/jquery-ui.css"/>
<link href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.4/css/select2.min.css" rel="stylesheet" />


<html>
  <head>
    <title>应用图片视频关联录入</title>
  </head>
  <body>

  <%
    Object object = session.getAttribute("isAdmin");
    if (object == null) {
      response.sendRedirect("login.jsp");
    }

    List<JSObject> allTags = Tags.fetchAllTags();
    JsonArray array = new JsonArray();
    for (int i = 0; i < allTags.size(); i++) {
      array.add((String) allTags.get(i).get("tag_name"));
    }
  %>

  <div class="container-fluid">
    <%@include file="common/navigationbar.jsp"%>

    <div class="panel panel-default">
      <!-- Default panel contents -->
      <div class="panel-heading">
        <button id="btn_add_new_path" class="btn btn-default glyphicon glyphicon-plus"></button>
        应用名称&nbsp;<input id="inputSearch" class="form-control" style="display: inline; width: auto;" type="text"/>
        <button id="btnSearch" class="btn btn-default glyphicon glyphicon-search"></button></div>

      <table class="table table-hover">
        <thead id="image_result_header">
          <tr>
            <th>ID</th>
            <th>图片路径</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody id="image_results_body">
        </tbody>
      </table>
      <br>
      <table class="table table-hover">
        <thead id="video_result_header">
          <tr>
            <th>ID</th>
            <th>视频路径</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody id="video_results_body">
        </tbody>
      </table>
    </div>
  </div>

  <div id="new_path_dlg" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
          <h4 class="modal-title" id="dlg_title">添加图片路径和视频路径</h4>
        </div>
        <div class="modal-body">
          <form id="modify_form" class="form-horizontal" action="#" autocomplete="off">
            <div class="form-group">
              <label for="inputImagePath" class="col-sm-2 control-label">图片路径</label>
              <div class="col-sm-10">
                <input class="form-control" id="inputImagePath" placeholder="图片路径" autocomplete="off">
              </div>
            </div>
            <div class="form-group">
              <label for="inputVideoPath" class="col-sm-2 control-label">视频路径</label>
              <div class="col-sm-10">
                <input type="text" class="form-control" id="inputVideoPath" placeholder="视频路径" autocomplete="off">
              </div>
            </div>
          </form>
          <p id="delete_message">确认要删除吗?</p>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
          <button type="button" class="btn btn-primary">确定</button>
        </div>
      </div>
    </div>
  </div>

  <jsp:include page="loading_dialog.jsp"></jsp:include>

  <script src="js/jquery.js"></script>
  <script src="bootstrap/js/bootstrap.min.js"></script>
  <script src="js/core.js"></script>
  <script src="js/bootstrap-datetimepicker.js"></script>
  <script src="jqueryui/jquery-ui.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.4/js/select2.min.js"></script>
  <script src="js/country-name-code-dict.js"></script>
  <script src="js/layer/layer.js" ></script>

  <script type="text/javascript">
    var modifyType = 'new';
    $("li[role='presentation']:eq(13)").addClass("active");
    var data = <%=array.toString()%>;
    $("#inputSearch").autocomplete({
        source: data
    });

//    当点击【+】按钮时
    $("#btn_add_new_path").click(function() {
      modifyType = 'new';
      $('#delete_message').hide();
      $('#modify_form').show();
      $("#dlg_title").text("添加图片路径和视频路径");
      $("#new_path_dlg").modal("show");
    });

//    当点击【确定】按钮时
    $("#new_path_dlg .btn-primary").click(function() {
      if (modifyType == 'new') {
          var imagePath = $("#inputImagePath").val();
          var videoPath = $("#inputVideoPath").val();
          var appName = $("#inputSearch").val();
        $.post('app_image_video_rel/create', {
            appName: appName,
            imagePath: imagePath,
            videoPath: videoPath
        }, function(data) {
          if (data && data.ret == 1) {
            $("#new_path_dlg").modal("hide");
            $('#btnSearch').click();
          } else {
            admanager.showCommonDlg("错误", data.message);
          }
        }, 'json');
      }
    });


    $('#btnSearch').click(function() {
      var appName = $("#inputSearch").val();
      $('.table tbody > tr').remove();
      var loadingIndex = layer.load(2,{time: 5000});
      $.post('app_image_video_rel/query_image_and_video_by_app', {
          appName: appName
      }, function(data) {
        layer.close(loadingIndex);
        if (data && data.ret == 1) {
          setData(data);
        } else {
          admanager.showCommonDlg("错误", data.message);
        }
      }, 'json');
    });

    function setData(data) {
      var image_array = data.image_array;
      var video_array = data.video_array;
      for (var i = 0; i < image_array.length; i++) {
        var one = image_array[i];
        var tr = $('<tr></tr>');
        var td = $('<td></td>');
        td.text(one['id']);
        tr.append(td);
        td = $('<td></td>');
        td.text(one['image_path']);
        tr.append(td);
        td = $('<td></td>');
        var btn = $('<input type="button" value="删除">');
        btn.data("id", one['id']);
        btn.click(function(){
            var id = $(this).data("id");
            $.post('app_image_video_rel/delete', {
                  id: id,
                  type:'image_path'
              }, function(data) {
                  if (data && data.ret == 1) {
                      $("#new_path_dlg").modal("hide");
                      $('#btnSearch').click();
                  } else {
                      admanager.showCommonDlg("错误", data.message);
                  }
              }, 'json');
          });
        td.append(btn);
        tr.append(td);
        $('.table #image_results_body').append(tr);
      }

      for (var j = 0; j < video_array.length; j++) {
          var two = video_array[j];
          var tr = $('<tr></tr>');
          var td = $('<td></td>');
          var id = two['id'];
          td.text(id);
          tr.append(td);
          td = $('<td></td>');
          td.text(two['video_path']);
          tr.append(td);
          td = $('<td></td>');
          var btn = $('<input type="button" value="删除">');
          btn.data("id", two['id']);
          btn.click(function(){
              var id = $(this).data("id");
              $.post('app_image_video_rel/delete', {
                  id: id,
                  type:'video_path'
              }, function(data) {
                  if (data && data.ret == 1) {
                      $("#new_path_dlg").modal("hide");
                      $('#btnSearch').click();
                  } else {
                      admanager.showCommonDlg("错误", data.message);
                  }
              }, 'json');
          });
          td.append(btn);
          tr.append(td);
          $('.table #video_results_body').append(tr);
      }
    }
  </script>
  <script src="js/interlaced-color-change.js"></script>
  </body>
</html>
