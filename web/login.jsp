<%--
  Created by IntelliJ IDEA.
  User: jikai
  Date: 5/16/17
  Time: 8:04 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Login</title>
    <link rel="stylesheet" href="bootstrap/css/bootstrap.min.css" />
    <link rel="stylesheet" href="bootstrap/css/bootstrap-theme.min.css" />
    <link href="bootstrap/css/signin.css" rel="stylesheet">
</head>
<body>

<div class="container">

    <form class="form-signin">
        <h2 class="form-signin-heading">投放项目后台管理中心</h2>
        <label for="inputUserName" class="sr-only">用户名</label>
        <input type="text" id="inputUserName" class="form-control" placeholder="用户名" required autofocus>
        <label for="inputPassword" class="sr-only">密码</label>
        <input type="password" id="inputPassword" class="form-control" placeholder="密码" required>

        <button class="btn btn-lg btn-primary btn-block" type="submit" id="btnLogin">登录</button>
    </form>

    <jsp:include page="loading_dialog.jsp" ></jsp:include>
</div>


<script src="js/jquery.js"></script>
<script src="bootstrap/js/bootstrap.min.js"></script>
<script src="js/core.js" ></script>

<script>
    $('#btnLogin').click(function() {
//        location.href = "https://www.facebook.com/v2.9/dialog/oauth?client_id=1353267041422321&redirect_uri=http://suijide.info:8080/admanager/facebook_callback.jsp&response_type=token+code&scope=ads_management";
        var userName = $('#inputUserName').val();
        var userPass = $('#inputPassword').val();

        $.post("login", {
            user: userName,
            pass: userPass
        }, function (data) {
            if (data && data.ret == 1) {
                location.href = "index.jsp";
            } else {
                admanager.showCommonDlg("提示", "用户名密码错误");
            }
        }, "json");
        return false;
    });
</script>

</body>
</html>
