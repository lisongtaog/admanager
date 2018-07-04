<%@ page import="java.util.Date" %><%--
  Created by IntelliJ IDEA.
  User: jikai
  Date: 5/16/17
  Time: 2:52 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/rootBase.jsp"%>

<html>
  <head>
    <title>Home</title>
  </head>
  <body>

  <%
    session.setMaxInactiveInterval(-1);
    Object object = session.getAttribute("isLogin");
    if (object == null) {
      response.sendRedirect("login.jsp");
    } else {
      Long expire_time = (Long)session.getAttribute("expire_time");
      String access_token = (String)session.getAttribute("access_token");

      if (expire_time != null && access_token != null) {
        if (expire_time > new Date().getTime()) {

        } else {
          response.sendRedirect("login.jsp");
        }
      } else {
        response.sendRedirect("login.jsp");
      }
    }
  %>

  <div class="container-fluid">
    <div class="dropdown">
      <button id="adcount_selector" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
        Select Your Ad Account
        <span class="caret"></span>
      </button>
      <ul id="adaccount_list" class="dropdown-menu" aria-labelledby="adcount_selector">
      </ul>
    </div>

    <div class="panel panel-default" style="margin-top: 10px">
      <div class="panel-heading" id="panel_title">Title</div>
    <table class="table table-hover">
      <thead>
      <tr>
        <th>Campaign Name</th>
        <th>Campaign Id</th>
        <th>Clicks</th>
        <th>Impressions</th>
        <th>CPC</th>
        <th>CTR</th>
        <th>CPM</th>
        <th>Spend</th>
        <th>Cost per Result</th>
        <th>Result</th>
      </tr>
      </thead>
      <tbody id="results_body">
      </tbody>
    </table>
      </div>


    <div id="loading_dlg" class="modal fade" tabindex="-1" role="dialog">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
            <h4 class="modal-title">Please wait</h4>
          </div>
          <div class="modal-body">
            <p>Querying data...</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
          </div>
        </div><!-- /.modal-content -->
      </div><!-- /.modal-dialog -->
    </div><!-- /.modal -->

  </div>


  <script>
    $('#loading_dlg').modal('show');

    $.get("query?method=get_accounts", function(data) {
      try {
        for (var i = 0; i < data.length; i++) {
          var one = data[i];
          var li = $('<li></li>');
          var a = $('<a href="#"></a>');
          a.text(one.name + "-" + one.id);
          li.append(a);
          li.attr("account_id", one.id);
          li.attr("account_name", one.name);
          li.click(function() {
            queryAccountCampaignData($(this).attr('account_id'), $(this).attr('account_name'));
          });
          $('#adaccount_list').append(li);

          $('#loading_dlg').modal('hide');
        }
      } catch (ex) {
      }
    });

    function queryAccountCampaignData(accountId, accountName) {
      $('#loading_dlg').modal('show');

      $('#panel_title').text(accountName);
      $.get("query?method=get_campaigns&account_id=" + accountId, function(data) {
        try {
          $('#loading_dlg').modal('hide');
          $('#results_body > tr').remove();
          for (var i = 0; i < data.length; i++) {
            var one = data[i];
            var str = "<tr><td>{campaign_name}</td><td>{campaign_id}</td><td>{clicks}</td><td>{impressions}</td><td>{cpc}</td><td>{ctr}</td><td>{cpm}</td><td>{spend}</td><td>{cost_per_result}</td><td>{result}</td></tr>";
            for (var key in one) {
              str = str.replace("{" + key + "}", one[key]);
            }
            $('#results_body').append($(str));
          }
        } catch (ex) {
        }
      });
    }
  </script>
<%--
  <fb:login-button scope="public_profile,email,ads_management" onlogin="checkLoginState();">
  </fb:login-button>

  <script>
    window.fbAsyncInit = function() {
      FB.init({
        appId: '1353267041422321',
        xfbml: true,
        version: 'v2.9'
      });
      FB.AppEvents.logPageView();

      FB.getLoginStatus(function(response) {
        if (response.status === 'connected') {
          console.log(response.authResponse.accessToken);
          FB.api('/me', function(response) {
            console.log(JSON.stringify(response));
          });
        }
      });
    };

    (function(d, s, id){
      var js, fjs = d.getElementsByTagName(s)[0];
      if (d.getElementById(id)) {return;}
      js = d.createElement(s); js.id = id;
      js.src = "//connect.facebook.net/en_US/sdk.js";
      fjs.parentNode.insertBefore(js, fjs);
    }(document, 'script', 'facebook-jssdk'));
  </script>--%>

  </body>
</html>
