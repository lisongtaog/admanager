<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page import="com.bestgo.common.database.services.DB" %>
<%@ page import="com.bestgo.admanager.utils.LoginUserSessionCacheUtil" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/rootBase.jsp"%>

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
    Object isZhangYiFan = new Object();
    LoginUserSessionCacheUtil.loadSessionFromCache(application, session);
    Object object = session.getAttribute("isAdmin");
    if (object == null) {
      response.sendRedirect("login.jsp");
    }

    List<JSObject> list = DB.scan("web_system_config").select("config_key", "config_value").execute();
  %>

  <div class="container-fluid">
    <%@include file="common/navigationbar.jsp"%>

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
    <%--你成功的认识了张一凡--%>
    <div class="panel panel-default">
      <!-- Default panel contents -->
      <div class="panel-heading">
        参数列表
      </div>

      <table class="table" id="tableParamters">
        <thead>
        <tr><th>参数名称</th><th>参数值</th><th>操作</th></tr>
        </thead>
        <tbody>
        <%for (int i = 0; i < list.size(); i++) {%>
        <tr>
          <td><%=list.get(i).get("config_key")%></td>
          <td><input style="width: 100%;" class=".keyValue" value="<%=list.get(i).get("config_value")%>" /></td>
          <td><a class="link_modify glyphicon glyphicon-refresh" href="javascript:void(0)"></a></td>
          <%--<td><a class="link_modify" href="javascript:void(0)">更新</a></td>--%>
        </tr>
        <% } %>
        </tbody>
      </table>
    </div>
    <% } %>


    <div class="panel panel-default">
      <!-- Default panel contents -->
      <div class="panel-heading">
        Facebook ID对应关系表
        <button id="btn_add_new_relation" class="btn btn-default">添加</button>
        <input id="inputFBAppRelSearch" class="form-control" style="display: inline; width: auto;" type="text" />
        <button id="btnFBAppRelSearch" class="btn btn-default glyphicon glyphicon-search"></button>
      </div>

      <table class="table" id="tableFBAppRel">
        <thead>
        <tr><th>ID</th><th>标签</th><th>广告账号</th><th>FB应用ID</th><th>FB主页ID</th><th>应用包名</th><th>Firebase工程ID</th><th>操作</th></tr>
        </thead>
        <tbody>
        <%--<%
          List<JSObject> data = new ArrayList<>();
          long totalPage = 0;
          long count = com.bestgo.admanager.servlet.System.countFacebookAppRelation();
          int index = Utils.parseInt(request.getParameter("page_index"), 0);
          int size = Utils.parseInt(request.getParameter("page_size"), 20);
          totalPage = count / size + (count % size == 0 ? 0 : 1);

          int preIndex = index > 0 ? index-1 : 0;
          int nextPage = index < totalPage - 1 ? index+1 : index;

          data = com.bestgo.admanager.servlet.System.fetchFacebookAppRelationData(index, size);
        %>

        <%
          for (int i = 0; i < data.size(); i++) {
            JSObject one = data.get(i);
        %>
        <tr>
          <td><%=one.get("id")%></td>
          <td><%=one.get("tag_name")%></td>
          <td><%=one.get("account_id")%></td>
          <td><%=one.get("fb_app_id")%></td>
          <td><%=one.get("page_id")%></td>
          <td><%=one.get("google_package_id")%></td>
          <td><%=one.get("firebase_project_id")%></td>
          <td><a class="link_modify glyphicon glyphicon-pencil" href="#" fbpages = "<%=one.get("fbPages")%>"></a><a class="link_delete glyphicon glyphicon-remove" href="#"></a></td>
        </tr>
        <% } %>--%>
        </tbody>
      </table>

      <nav aria-label="Page navigation">
        <ul class="pagination">
          <li>
            <a href="javascript:void(0)" aria-label="Previous" id="prevPage">
              <span aria-hidden="true">上一页</span>
            </a>
          </li>
          <li>
            <a href="javascript:void(0)" aria-label="Next" id="nextPage">
              <span aria-hidden="true">下一页</span>
            </a>
          </li>
        </ul>
      </nav>
    </div>

    <div class="panel panel-default">
      <!-- Default panel contents -->
      <div class="panel-heading">
        广告语列表
        <button id="btn_add_new_message" class="btn btn-default">添加</button>
        <input id="inputSearch" class="form-control" style="display: inline; width: auto;" type="text" />
        <button id="btnSearch" class="btn btn-default">查找</button>
      </div>

      <table class="table">
        <thead>
        <tr><th>ID</th><th>主题</th><th>内容</th></tr>
        </thead>
        <tbody>
        </tbody>
      </table>
    </div>

    <div id="new_fb_app_rel_dlg" class="modal fade" tabindex="-1" role="dialog">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
            <h4 class="modal-title" id="dlg_fb_app_rel_title">添加Facebook对应关系</h4>
          </div>
          <div class="modal-body">
            <form id="modify_rel_form" class="form-horizontal" action="#" autocomplete="off">
              <div class="form-group">
                <label for="inputTagName" class="col-sm-2 control-label">标签</label>
                <div class="col-sm-10">
                  <input class="form-control" id="inputTagName" placeholder="标签" autocomplete="off">
                </div>
              </div>
              <div class="form-group">
                <label for="inputAccountId" class="col-sm-2 control-label">广告账号</label>
                <div class="col-sm-10">
                  <input type="text" class="form-control" id="inputAccountId" placeholder="广告账号" autocomplete="off">
                </div>
              </div>
              <div class="form-group">
                <label for="inputFBAppId" class="col-sm-2 control-label">FB应用ID</label>
                <div class="col-sm-10">
                  <input type="text" class="form-control" id="inputFBAppId" placeholder="FB应用ID" autocomplete="off">
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-2 control-label">
                  <button type="button" class="btn btn-info" onclick="add_fb_page()">添加</button>
                </label>
                <div class="col-sm-10">
                  <table id="fb_page" style="border:#4a98ff 1px solid">
                    <tr>
                      <th>facebook主页ID</th>
                      <th>facebook主页NAME</th>
                      <th>操作</th>
                    </tr>
                  </table>
                </div>
              </div>
              <div class="form-group">
                <label for="inputGPPackageId" class="col-sm-2 control-label">应用包名</label>
                <div class="col-sm-10">
                  <input type="text" class="form-control" id="inputGPPackageId" placeholder="应用包名" autocomplete="off">
                </div>
              </div>
              <div class="form-group">
                <label for="inputFirebaseProjectId" class="col-sm-2 control-label">Firebase工程ID</label>
                <div class="col-sm-10">
                  <input type="text" class="form-control" id="inputFirebaseProjectId" placeholder="Firebase工程ID" autocomplete="off">
                </div>
              </div>
            </form>
            <p id="delete_rel_message">确认要删除吗?</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
            <button type="button" class="btn btn-primary">确定</button>
          </div>
        </div>
      </div>
    </div>

  <jsp:include page="loading_dialog.jsp"></jsp:include>

  <script type="text/javascript">
    var modifyType;
    var id;
    var _tagName;
    $("li[role='presentation']:eq(3)").addClass("active");
    function bindTableFBOp() {
      $('#tableFBAppRel .link_modify').click(function () {
        clearRelationForm();//清空表单数据
        var tds = $(this).parents("tr").find('td');
        id = $(tds.get(0)).text();
        var tagName = $(tds.get(1)).text();
        var accountId = $(tds.get(2)).text();
        var fbAppId = $(tds.get(3)).text();
        var fbPageId = $(tds.get(4)).text();
        var gpPackageId = $(tds.get(5)).text();
        var firebaseProjectId = $(tds.get(6)).text();

        //facebook首页信息
        var fbPages =  $.parseJSON($(this).prev().text());

        $('#inputTagName').val(tagName);
        $('#inputAccountId').val(accountId);
        $('#inputFBAppId').val(fbAppId);
        $('#inputGPPackageId').val(gpPackageId);
        $('#inputFirebaseProjectId').val(firebaseProjectId);
        for(var i = 0 ; i < fbPages.length ;i++){//修改时 将facebook主页信息回显
            var page = fbPages[i].data;
            add_fb_page(page.id,page.page_id,page.page_name);
        }

        modifyType = 'update';
        $('#delete_rel_message').hide();
        $('#modify_rel_form').show();
        $("#dlg_fb_app_rel_title").text("修改关系");
        $("#new_fb_app_rel_dlg").modal("show");
      });

      $('#tableFBAppRel .link_delete').click(function () {
        var tds = $(this).parents("tr").find('td');
        id = $(tds.get(0)).text();
      _tagName = $(tds.get(1)).text();
        modifyType = 'delete';
        $('#delete_rel_message').show();
        $('#modify_rel_form').hide();
        $("#dlg_fb_app_rel_title").text("删除关系");
        $("#new_fb_app_rel_dlg").modal("show");
      });
    }

    function bindOp() {
      nextPage();

      $('#tableParamters .link_modify').click(function () {
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

      bindTableFBOp();

      $('#btnFBAppRelSearch').click(function() {
        var word = $('#inputFBAppRelSearch').val();
        $.post('system/fb_app_id_rel/query', {
          word: word,
        }, function(data) {
          if (data && data.ret == 1) {
            $('#tableFBAppRel tbody > tr').remove();
            setData(data.data);
            bindTableFBOp();
          } else {
            admanager.showCommonDlg("错误", data.message);
          }
        }, 'json');
      });

      var currPageIndex = 0;
        /**
         *上一页
         * */
        function prevPage() {
            $.post('system/fb_app_id_rel/query', {
                page_index: currPageIndex > 0 ? --currPageIndex : 0,
            }, function(data) {
                if (data && data.ret == 1) {
                    $('#tableFBAppRel tbody > tr').remove();
                    setData(data.data);
                    bindTableFBOp();
                } else {
                    admanager.showCommonDlg("错误", data.message);
                }
            }, 'json');
        }
        /**
         *下一页
         * */
        function nextPage() {
            $.post('system/fb_app_id_rel/query', {
                page_index: ++currPageIndex,
            }, function(data) {
                if (data && data.ret == 1) {
                    $('#tableFBAppRel tbody > tr').remove();
                    setData(data.data);
                    bindTableFBOp();
                } else {
                    admanager.showCommonDlg("错误", data.message);
                }
            }, 'json');
        }
      $('#prevPage').click(prevPage);
      $('#nextPage').click(nextPage);


      function setData(data) {
        for (var i = 0; i < data.length; i++) {
          var one = data[i];
          var tr = $('<tr></tr>');
          var td = $('<td></td>');
          td.text(one.id);
          tr.append(td);
          td = $('<td></td>');
          td.text(one.tag_name);
          tr.append(td);
          td = $('<td></td>');
          td.text(one.account_id);
          tr.append(td);
          td = $('<td></td>');
          td.text(one.fb_app_id);
          tr.append(td);
          td = $('<td></td>');
          td.text(one.page_id);
          tr.append(td);
          td = $('<td></td>');
          td.text(one.google_package_id);
          tr.append(td);
            td = $('<td></td>');
            td.text(one.firebase_project_id);
            tr.append(td);
          td = $('<td><span style="display:none">'+one.fbPages+'</span><a class="link_modify glyphicon glyphicon-pencil" href="#"></a>&nbsp;&nbsp;<a class="link_delete glyphicon glyphicon-remove" href="#"></a></td>');
          tr.append(td);
          $('#tableFBAppRel tbody').append(tr);
        }
      }

      $('#btn_add_new_relation').click(function() {
        modifyType = 'new';
        clearRelationForm();//清空表单数据
        add_fb_page();//默认添加一行 FB主页信息
        $('#delete_rel_message').hide();
        $('#modify_rel_form').show();
        $("#dlg_fb_app_rel_title").text("新建关系");
        $("#new_fb_app_rel_dlg").modal("show");
      });

      $("#new_fb_app_rel_dlg .btn-primary").click(function() {
        if (modifyType == 'new') {
          var tagName = $('#inputTagName').val();
          var accountId = $('#inputAccountId').val();
          var fbAppId = $('#inputFBAppId').val();
          var gpPackageId = $('#inputGPPackageId').val();
          var firebaseProjectId = $('#inputFirebaseProjectId').val();

          var fbPages = genFBPageJSON();

          $.post('system/fb_app_id_rel/create', {
              tagName: tagName,
              accountId: accountId,
              fbAppId: fbAppId,
              fbPages:JSON.stringify(fbPages),
              gpPackageId: gpPackageId,
              firebaseProjectId: firebaseProjectId
          }, function(data) {
            if (data && data.ret == 1) {
              $("#new_fb_app_rel_dlg").modal("hide");
              location.reload();
            } else {
              admanager.showCommonDlg("错误", data.message);
            }
          }, 'json');
        } else if (modifyType == 'update') {
          var tagName = $('#inputTagName').val();
          var accountId = $('#inputAccountId').val();
          var fbAppId = $('#inputFBAppId').val();
          var gpPackageId = $('#inputGPPackageId').val();
          var firebaseProjectId = $('#inputFirebaseProjectId').val();
          var fbPages = genFBPageJSON();
          $.post('system/fb_app_id_rel/update', {
              id: id,
              tagName: tagName,
              accountId: accountId,
              fbAppId: fbAppId,
              fbPages:JSON.stringify(fbPages),
              gpPackageId: gpPackageId,
              firebaseProjectId: firebaseProjectId
          }, function(data) {
            if (data && data.ret == 1) {
              $("#new_fb_app_rel_dlg").modal("hide");
              location.reload();
            } else {
              admanager.showCommonDlg("错误", data.message);
            }
          }, 'json');
        } else if (modifyType == 'delete') {
          $.post('system/fb_app_id_rel/delete', {
            id: id,
            tagName: _tagName
          }, function(data) {
            if (data && data.ret == 1) {
              $("#new_fb_app_rel_dlg").modal("hide");
              location.reload();
            } else {
              admanager.showCommonDlg("错误", data.message);
            }
          }, 'json');
        }
      });
    }

    /**
     * 添加FB主页记录
     */
    function clearRelationForm(){
        $('#inputTagName').val("");
        $('#inputAccountId').val("");
        $('#inputFBAppId').val("");
        $("input[name='page_id']").val("");
        $('#inputGPPackageId').val("");
        $('#inputFirebaseProjectId').val("");
        $("#fb_page tr:not(:first)").remove();
    }

    /**
     * 删除FB主页记录
     * @param thizz
     */
    function del_fb_page(thizz) {
        var tr = $(thizz).parent().parent();
        var table = tr.parent();
        var countTr = $("#fb_page tr").length;
        //仅保留 表头和第一条数据，至少保留一条数据
        if(countTr > 2){
            tr.remove();
        }else(
            alert("至少保留一条FB主页信息")
        )
    }

    /**
     * 添加FB主页记录
     */
    function add_fb_page(id,pageId,pageName) {
        if(id === undefined) id = "";
        if(pageId === undefined) pageId = "";
        if(pageName === undefined) pageName = "";

        var tr = $("<tr>" +
          "<input type=\"hidden\" name=\"id\" value=\""+ id +"\" placeholder=\"数据库表主键\"/>" +
          "<td><input type=\"text\" name=\"page_id\" value=\""+ pageId +"\" placeholder=\"FB主页ID\"/></td>" +
          "<td><input type=\"text\" name=\"page_name\" value=\""+ pageName +"\" placeholder=\"FB主页NAME\"/></td>"  +
          "<td><button type=\"button\" class=\"btn btn-link\" onclick=\"del_fb_page(this)\">删除</button></td>" +
          "</tr>"
        );
        $("#fb_page").append(tr);
    }
    /**
     * FB首页信息 组织为JSON
     */
    function genFBPageJSON(){
        var fbIds = [];
        var fbPageIds = [];
        var fbPageNames = [];

        $("#fb_page :input[name='id']").each(function(){
            var id = $(this).val();
            //fbIds.push(id);
            //使用时间戳占位，后台暂时没有用到该属性;修改时 使用先删除 后插入的逻辑
            fbIds.push(""+ new Date().getTime());
        });

        $("#fb_page :input[name='page_id']").each(function(){
            var pageId = $(this).val();
            fbPageIds.push(pageId);
        });

        $("#fb_page :input[name='page_name']").each(function(){
            var pageName = $(this).val();
            fbPageNames.push(pageName);
        });

        var fbPages = [];
        var fbPageItem;
        for(var i=0;i< fbPageIds.length;i++){
            fbPageItem ={};
            fbPageItem.id = fbIds[i];
            fbPageItem.page_id = fbPageIds[i];
            fbPageItem.page_name = fbPageNames[i];
            fbPages.push(fbPageItem);
        }

        return fbPages;
    }

    bindOp();
  </script>
    <script src="js/interlaced-color-change.js"></script>
  </body>
</html>
