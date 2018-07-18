<%--
  Created by IntelliJ IDEA.
  User: jikai
  Date: 5/16/17
  Time: 8:04 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/rootBase.jsp"%>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Login</title>
    <link href="bootstrap/css/signin.css" rel="stylesheet">
</head>
<body>

<div class="container">
    <form class="form-signin">
        <h2 class="form-signin-heading">投放项目后台管理平台</h2>
        <div class="form-group has-success has-feedback">
            <input type="text" id="inputUserName" class="form-control" placeholder="用户名" required autofocus>
            <span class="glyphicon glyphicon-user form-control-feedback"></span>
        </div>
        <div class="form-group has-success has-feedback">
            <input type="password" id="inputPassword" class="form-control" placeholder="密码" required>
            <span class="glyphicon glyphicon-lock form-control-feedback"></span>
        </div>
        <button class="btn btn-lg btn-primary btn-block" type="submit" id="btnLogin">登录</button>
        <%--<button class="btn btn-lg btn-success btn-block" id="btnRegister">注册(开发中)</button>--%>
    </form>

    <jsp:include page="loading_dialog.jsp" ></jsp:include>
</div>

<div class="modal fade" id="register" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">

            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span class="glyphicon glyphicon-remove"></span></button>
                <h4 class="modal-title">注册新用户</h4>
            </div>

            <div class="modal-body">
                <form id="new_user" class="form-horizontal">
                    <div class="form-group">
                        <label for="user_name" class="control-label col-md-2">用户名</label>
                        <div class="col-md-10">
                            <input type="text" id="user_name" name="user_name" class="form-control"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="user_password" class="control-label col-md-2">登陆密码</label>
                        <div class="col-md-10">
                            <input type="password" id="user_password" name="user_password" class="form-control"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="password_confirm" class="control-label col-md-2">密码确认</label>
                        <div class="col-md-10">
                            <input type="password" id="password_confirm" name="password_confirm" class="form-control"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="email" class="control-label col-md-2">验证邮箱</label>
                        <div class="col-md-10">
                            <input type="email" id="email" name="email" class="form-control"/>
                        </div>
                    </div>
                </form>
            </div>

            <div class="modal-footer">
                <button type="button" id="submit" class="btn btn-success" data-dismiss="modal">
                    <span class="glyphicon glyphicon-bell" aria-hidden="true">提交</span>
                </button>
            </div>
        </div>
    </div>
</div>

<script>
    $('#btnLogin').click(function() {
//        location.href = "https://www.facebook.com/v2.9/dialog/oauth?client_id=1353267041422321&redirect_uri=http://suijide.info:8080/admanager/facebook_callback.jsp&response_type=token+code&scope=ads_management";
        var userName = $('#inputUserName').val();
        if($.trim(userName) == ""){
            layer.msg("用户名不能为空！",{time:2000,icon:5,shift:6},function () {
                $('#inputUserName').focus();
            });
        }else{
            var userPass = $('#inputPassword').val();
            if($.trim(userPass) == ""){
                layer.msg("密码不能为空！",{time:2000,icon:5,shift:6},function () {
                    $('#inputPassword').focus();
                });
            }else{
                $.post("login/login", {
                    user: userName,
                    pass: userPass
                }, function (data) {
                    if (data && data.ret == 1) {
                        location.href = "index.jsp";
                        //location.href = "index3.jsp";
                    } else {
                        layer.msg("用户名或密码错误！",{time:2000,icon:5,shift:6},function () {
                            $('#inputUserName').focus();
                        });
                    }
                }, "json");
            }
        }
        return false;
    });

    $("#btnRegister").click(function(){
        document.getElementById("new_user").reset();
        $("#register").find("label[class='error']").remove();
        $(".error").removeClass("error");
        $("#register").modal("show");
        return false;
    });

    /**
     * 新用户注册验证
    */

    $("#new_user").validate({
        rules: {
            user_name: {
                required:true
            },
            user_password: {
                required: true,
                minlength: 5
            },
            password_confirm: {
                required:true,
                minlength: 5,
                equalTo:"#user_password"
            },
            email: {
                required: true,
                email: true
            }
        },
        messages: {
            user_name: "warning:请输入用户名",
            user_password: {
                required: "warning:请输入密码",
                minlength: $.validator.format( "warning:输入密码不能小于{0}个字符" )
            },
            password_confirm: {
                required: "warning:密码不一致",
                minlength:  $.validator.format( "warning:输入密码不能小于{0}个字符" ),
                equalTo:"warning:密码不一致"
            },email: {
                required: "warning:请输入Email地址",
                email: "warning:请输入正确的email地址"
            }
        }
    });
    $("#submit").click(function(){
        var user_name = $("#user_name").val();
        var password = $("#password_confirm").val();
        var email = $("#email").val();

        if($("#new_user").valid()){
            $.post("login/register",{
                user_name:user_name,
                password:password,
                email:email
            },function(data){
                if(data && data.ret == 1){
                    alert(data.message);
                }else{
                    alert(data.message);
                }
            },"json");
        }
    });
</script>

</body>
</html>
