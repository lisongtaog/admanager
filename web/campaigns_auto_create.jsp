<%@ page import="com.bestgo.admanager.utils.Utils" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.bestgo.admanager.servlet.AutoCreateCampaign" %>
<%@ page import="java.lang.reflect.Array" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.google.gson.JsonArray" %>


<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="bootstrap/css/bootstrap.min.css"/>
<link rel="stylesheet" href="bootstrap/css/bootstrap-theme.min.css"/>
<link rel="stylesheet" href="css/core.css"/>
<link rel="stylesheet" href="css/bootstrap-tagsinput.css"/>
<link rel="stylesheet" href="css/bootstrap-datetimepicker.css"/>
<link rel="stylesheet" href="jqueryui/jquery-ui.css"/>
<link href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.4/css/select2.min.css" rel="stylesheet" />

<html>
  <head>
    <title>自动创建系列管理</title>
  </head>
  <body>

  <%
    Object object = session.getAttribute("isAdmin");
    if (object == null) {
      response.sendRedirect("login.jsp");
    }
  %>

  <div class="container-fluid">
    <div class="panel panel-default">
      <!-- Default panel contents -->
      <div class="panel-heading"><label>
        <input type="radio" name="optionsRadios" id="checkFacebook" checked>
        Facebook 广告
        </label>
        <label>
          <input type="radio" name="optionsRadios" id="checkAdwords">
          AdWords 广告
        </label>
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <label for="tagName">标签</label>
        <input id="tagName" form-control style="display: inline; width: auto;" type="text">
        <label for="inputCampaignName">模糊系列名</label>
        <input id="inputCampaignName" class="form-control" style="display: inline; width: auto;" type="text" />
        <label for="inputCountry">国家</label>
        <input id="inputCountry" form-control style="display: inline; width: auto;" type="text">
        <button id="btnSearch" class="btn btn-default glyphicon glyphicon-search"></button></div>

      <%
        //先在首页显示一段信息
        List<JSObject> data = new ArrayList<>();
        long totalPage = 0;
        int preIndex = 0;
        int nextPage = 0;
        String network = request.getParameter("network");
        ArrayList<String> networks = new ArrayList<>();
        networks.add("facebook");
        networks.add("adwords");
        if (networks.indexOf(network) == -1) {
          network = "facebook";
        }
        if ("facebook".equals(network)) {
          long count = AutoCreateCampaign.facebookCount();
          int index = Utils.parseInt(request.getParameter("page_index"), 0);
          int size = Utils.parseInt(request.getParameter("page_size"), 20);
          totalPage = count / size + (count % size == 0 ? 0 : 1);

          preIndex = index > 0 ? index - 1 : 0;
          nextPage = index < totalPage - 1 ? index + 1 : index;

          data = AutoCreateCampaign.facebookFetchData(index, size);
        } else {
          long count = AutoCreateCampaign.adwordsCount();
          int index = Utils.parseInt(request.getParameter("page_index"), 0);
          int size = Utils.parseInt(request.getParameter("page_size"), 20);
          totalPage = count / size + (count % size == 0 ? 0 : 1);

          preIndex = index > 0 ? index - 1 : 0;
          nextPage = index < totalPage - 1 ? index + 1 : index;

          data = AutoCreateCampaign.adwordsFetchData(index, size);
        }
        //获取标签信息
        List<JSObject> allTags = Tags.fetchAllTags();
        JsonArray array = new JsonArray();
        for (int i = 0; i < allTags.size(); i++) {
          array.add((String) allTags.get(i).get("tag_name"));
        }
      %>

      <table class="table">
        <thead>
        <tr><th>序号</th><th>应用</th><th>国家</th><th>语言</th><th>系列名称</th><th>预算</th><th>出价</th><th>操作</th>
          <th>开启<input class="all_checkbox_campaign_enable" type= "checkbox" onclick="allChecked()"></th>
        </tr>
        </thead>
        <tbody>
        <%
          for (int i = 0; i < data.size(); i++) {
            JSObject one = data.get(i);
        %>
        <tr>
          <td><%=one.get("id")%></td>
          <td><%=one.get("app_name")%></td>
          <td><%=one.get("country_region")%></td>
          <td><%=one.get("language")%></td>
          <td><%=one.get("campaign_name")%></td>
          <td><%=one.get("bugdet")%></td>
          <td><%=one.get("bidding")%></td>
          <td><a class="link_modify" target="_blank" href="campaigns_create.jsp?type=auto_create&network=<%=network%>&id=<%=one.get("id")%>"><span class="glyphicon glyphicon-pencil"></span></a>&nbsp;&nbsp;<a class="link_delete" href="#"><span class="glyphicon glyphicon-remove"></span></a></td>
          <td><input class="checkbox_campaign_enable" type="checkbox" <% if (one.get("enabled").equals(1)) { %> checked <% }%> /></td>
        </tr>
        <% } %>

        </tbody>
      </table>

      <nav aria-label="Page navigation">
        <ul class="pagination">
          <li>
            <a class="changePage" href="campaigns_auto_create.jsp?network=<%=network%>&page_index=<%=preIndex%>" aria-label="Previous">
              <span aria-hidden="true">上一页</span>
            </a>
          </li>
          <li>
            <a class="changePage" href="campaigns_auto_create.jsp?network=<%=network%>&page_index=<%=nextPage%>" aria-label="Next">
              <span aria-hidden="true">下一页</span>
            </a>
          </li>
          <li id="total_page">
            共<%=totalPage%>页
          </li>
        </ul>
      </nav>
    </div>
  </div>

  <div id="delete_dlg" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
          <h4 class="modal-title">提示</h4>
        </div>
        <div class="modal-body">
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
  <script src="js\country-name-code-dict.js"></script>
  <script src="jqueryui/jquery-ui.min.js"></script>

  <script type="text/javascript">
    var network = "<%=network%>";
    if (network == 'facebook') {
      $('#checkFacebook').prop('checked', true);
    } else {
      $('#checkAdwords').prop('checked', true);
    }
    $('#checkFacebook').click(function () {
      window.location.href = "campaigns_auto_create.jsp?network=facebook";
    });
    $('#checkAdwords').click(function() {
      window.location.href = "campaigns_auto_create.jsp?network=adwords";
    });

    var tags = <%=array.toString()%>;
    $("#tagName").autocomplete({
        source:tags
    });
    var regionArray = new Array();
    regionList.forEach(function(region){
        var countryName = region["name"];
        regionArray.push(countryName);
    });
    $("#inputCountry").autocomplete({
        source:regionArray
    });


    $('#btnSearch').click(function() {
      var query = $("#inputCampaignName").val();
      var country = $("#inputCountry").val();
      var tagName = $("#tagName").val();
      var network = "<%=network%>";
      if(network =="adwords"){
          regionList.forEach(function(a){
              var countryName = a["name"];
              if(countryName == country){
                  country = a["country_code"];
                  return;
              }
          });
      }
      if(tagName == ""){
          alert("标签名不能为空！");
      }else{
          $.post('auto_create_campaign/<%=network%>/query', {
              word: query,
              country:country,
              tagName:tagName
          }, function(data) {
              if (data && data.ret == 1) {
                  $('.table tbody > tr').remove();
                  setData(data.data);
                  bindOp();
              } else {
                  admanager.showCommonDlg("错误", data.message);
              }
          }, 'json');
      }
    });

    function allChecked(){
        var checked = $(".all_checkbox_campaign_enable").prop("checked");
        if(checked){
            //全部“开启”后
            $(".checkbox_campaign_enable").prop("checked",true);
            var list = $("tr:gt(0)");
            var id_batch = "";
            for(var i = 0;i<list.length;i++){
                var temp = $(list[i]);
                var id = temp.children("td").get(0).innerHTML;
                id_batch += id+",";
            }
            id_batch = id_batch.replace(/(.*)(,)/,"$1");
            $.post("auto_create_campaign/<%=network%>/enable",{
                id_batch:id_batch,
                enable:checked
            },function(data){
                if(data.ret==1){
                    admanager.showCommonDlg("提示",data.message);
                }else{
                    admanager.showCommonDlg("提示",data.message);
                }
            });
        }else{
            //取消全部“开启”后
            $(".checkbox_campaign_enable").prop("checked",false);
            var list = $("tr:gt(0)");
            var id_batch = "";
            for(var i = 0;i<list.length;i++){
                var temp = $(list[i]);
                var id = temp.children("td").get(0).innerHTML;
                id_batch += id+",";
            }
            id_batch = id_batch.replace(/(.*)(,)/,"$1");
            $.post("auto_create_campaign/<%=network%>/enable",{
                id_batch:id_batch,
                enable:checked
            },function(data){
                if(data.ret==1){
                    admanager.showCommonDlg("提示",data.message);
                }else{
                    admanager.showCommonDlg("提示",data.message);
                }
            });
        }
    }
    function setData(data) {
      for (var i = 0; i < data.length; i++) {
        var one = data[i];
        var tr = $('<tr></tr>');
        var td = $('<td></td>');
        td.text(one.id);
        tr.append(td);
        td = $('<td></td>');
        td.text(one.app_name);
        tr.append(td);
        td = $('<td></td>');
        td.text(one.country_region);
        tr.append(td);
        td = $('<td></td>');
        td.text(one.language);
        tr.append(td);
        td = $('<td></td>');
        td.text(one.campaign_name);
        tr.append(td);
        td = $('<td></td>');
        td.text(one.bugdet);
        tr.append(td);
        td = $('<td></td>');
        td.text(one.bidding);
        tr.append(td);
        td = $('<td><a class="link_modify" target="_blank" href="campaigns_create.jsp?type=auto_create&network=<%=network%>&id=' + one.id + '">修改</a><a class="link_delete" href="#">删除</a></td>');
        tr.append(td);
        td = $('<td></td>');
        td.html('<input class="checkbox_campaign_enable" type="checkbox" ' + (one.enabled == 1 ? 'checked' : '') + ' />');
        tr.append(td);
        $('.table tbody').append(tr);
      }
      goToPage(1);
      $(".changePage").hide();
    }

    /**
     * @param now 当前页码
     * @param psize 一页显示的数量，在此函数内写死
     */
    function goToPage(now){
        $("nav").empty();
        var psize = 50;
        var totalPage = 0;
        var num = $("tbody tr").length;  //得到总行数
        if((num/psize) > parseInt(num/psize)){
            totalPage = parseInt(num/psize)+1;
        }else{
            totalPage = parseInt(num/psize);
        }
        var currentPage = now;
        var start = (currentPage - 1) * psize;  //开始显示的行数-1
        var end = currentPage * psize;  //结束行
        end = (end > num) ? num : end;
        var initRow = $("tbody tr");
        var startRow = initRow;
        if(start>0){
            var startRow = $("tbody tr:gt("+start+")");
        }
        var endRow = $("tbody tr:gt("+end+")");
        initRow.hide();
        startRow.show();
        endRow.hide();
        var tempStr = "";
        if (currentPage > 1) {
            tempStr += "<a href=\"#\" onClick=\"goToPage(" + (currentPage - 1)+")\">上一页&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>"
            for (var j = 1; j <= totalPage; j++) {
                tempStr += "<a href=\"#\" onClick=\"goToPage(" + j + ")\">" + j + "&nbsp;&nbsp;&nbsp;</a>"
            }
        } else {
            tempStr += "上一页&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
            for (var j = 1; j <= totalPage; j++) {
                tempStr += "<a href=\"#\" onClick=\"goToPage(" + j + ")\">" + j + "&nbsp;&nbsp;&nbsp;</a>"
            }
        }
        if (currentPage < totalPage) {
            tempStr += "<a href=\"#\" onClick=\"goToPage(" + (currentPage + 1)+")\">下一页&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>";
            for (var j = 1; j <= totalPage; j++) {
            }
        } else {
            tempStr += "  下一页&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
        }
        $("nav").append(tempStr);
    }

    // 点击[开启]checkbox后往后台修改表
    function bindOp() {
        $(".checkbox_campaign_enable").click(function() {
            var checkbox = $(this);
            if(checkbox.prop("checked")==false){
                $(".all_checkbox_campaign_enable").prop("checked",false);
            }
            var tr = $(this).parents("tr");
            var tds = tr.find('td');
            var id = $(tds.get(0)).text();

            var checked = $(this).prop('checked');
            $.post('auto_create_campaign/<%=network%>/enable', {
                id: id,
                enable: checked,
            }, function(data) {
                if (data && data.ret == 1) {
                    admanager.showCommonDlg("成功", data.message);
                } else {
                    admanager.showCommonDlg("错误", data.message);
                }
            }, 'json');

        });
        $(".link_delete").click(function() {
            var tr = $(this).parents("tr");
            var tds = tr.find('td');
            var id = $(tds.get(0)).text();

            $("#delete_dlg .btn-primary").unbind('click');
            $("#delete_dlg .btn-primary").click(function() {
                $('#delete_dlg').modal('hide');
                setTimeout(function () {
                    $.post('auto_create_campaign/<%=network%>/delete', {
                        id: id
                    }, function(data) {
                        if (data && data.ret == 1) {
                            tr.remove();
                            admanager.showCommonDlg("成功", data.message);
                        } else {
                            admanager.showCommonDlg("错误", data.message);
                        }
                    }, 'json');
                }, 10);
            });
            $('#delete_dlg').modal('show');
      });
    }
    bindOp();
  </script>
  <script src="js/interlaced-color-change.js"></script>
  </body>
</html>
