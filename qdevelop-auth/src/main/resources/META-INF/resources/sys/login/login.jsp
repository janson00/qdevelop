<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>登陆页</title>
<!-- Tell the browser to be responsive to screen width -->
<meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
<!-- Bootstrap 3.3.6 -->
<link rel="stylesheet" href="<%=request.getContextPath()%>/AdminLTE/bootstrap/css/bootstrap.min.css">
<!-- Font Awesome -->
<link rel="stylesheet" href="<%=request.getContextPath()%>/AdminLTE/fonts/font-awesome.min.css">
<!-- Ionicons -->
<link rel="stylesheet" href="<%=request.getContextPath()%>/AdminLTE/fonts/ionicons.min.css">
<!-- Theme style -->
<link rel="stylesheet" href="<%=request.getContextPath()%>/AdminLTE/dist/css/AdminLTE.min.css">
<!-- iCheck -->
<%-- <link rel="stylesheet" href="<%=request.getContextPath()%>/AdminLTE/plugins/iCheck/square/blue.css">
 --%>
 </head>
<body class="hold-transition login-page">
	<div class="login-box">

		<!-- /.login-logo -->
		<div class="login-box-body">
			<p class="login-box-msg">请输入你的账号和密码进行登陆</p>
			<form action="<%=request.getContextPath()%>/AdminLTE/index2.html"
				method="post">
				<div class="form-group has-feedback">
					<input type="text" id="loginName" class="form-control"
						placeholder="登陆名"> <span
						class="glyphicon glyphicon-envelope form-control-feedback"></span>
				</div>
				<div class="form-group has-feedback">
					<input type="password" id="passwd" class="form-control"
						placeholder="密码"> <span
						class="glyphicon glyphicon-lock form-control-feedback"></span>
				</div>
				<div class="row">
					<div class="col-xs-8">
						<div class="checkbox icheck">
							<label> <input type="checkbox">记住我</label>
						</div>
					</div>
					<!-- /.col -->
					<div class="col-xs-4">
						<div type="submit" id="submit" class="btn btn-primary btn-block btn-flat">登陆</div>
					</div>
					<!-- /.col -->
				</div>
			</form>

			<!-- <div class="social-auth-links text-center">
      <p>- OR -</p>
      <a href="#" class="btn btn-block btn-social btn-facebook btn-flat"><i class="fa fa-facebook"></i> Sign in using
        Facebook</a>
      <a href="#" class="btn btn-block btn-social btn-google btn-flat"><i class="fa fa-google-plus"></i> Sign in using
        Google+</a>
    </div>
    /.social-auth-links

    <a href="#">I forgot my password</a><br>
    <a href="register.html" class="text-center">Register a new membership</a> -->

		</div>
		<!-- /.login-box-body -->
	</div>
	<!-- /.login-box -->

	<!-- jQuery 2.2.3 -->
	<script
		src="<%=request.getContextPath()%>/AdminLTE/plugins/jQuery/jquery-2.2.3.min.js"></script>
	<!-- Bootstrap 3.3.6 -->
	<script
		src="<%=request.getContextPath()%>/AdminLTE/bootstrap/js/bootstrap.min.js"></script>
	<!-- iCheck -->
	<script src="<%=request.getContextPath()%>/AdminLTE/plugins/iCheck/icheck.min.js"></script>

	<script src="./common.js"></script>
	<script>
	
  $(function () {
    $('input').iCheck({
      checkboxClass: 'icheckbox_square-blue',
      radioClass: 'iradio_square-blue',
      increaseArea: '20%' // optional
    });
    $('#submit').click(function(){
    	var val = $('#passwd').val().toString();
    	if(typeof val == 'string' && val.length != 32){
    		$('#passwd').val(val.MD5());	
    	}
    	
    	var params={};
    	params["username"] = $('#loginName').val();
    	params["passwd"] = $('#passwd').val();
    	$.ajax({
            type: "post",
            url: '<%=request.getContextPath()%>/sys/auth/login',
            cache: false,
            data: params,
            dataType: "json",
            success : function (result) {
            	//console.log(result);
            	if(result.tag==0){
            		var url = getQueryString("from");
            		if(url!=null){
            			window.location.href = url;
            		}else{
            			window.location.href = "<%=request.getContextPath()%>/index.html"
            		}
            	}else{
            		alert(result.errMsg);
            	}
            }
        });

    });
  });
</script>
</body>
</html>
