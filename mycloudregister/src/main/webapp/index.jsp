<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>  
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">  
<html>  
  <head>  
    <title>æµ‹è¯•</title>  
  </head>  
  
  <script type="text/javascript">
		$.ajax({
		 url:"/mycloudregister/register.do",
		 data:data,
		 dataType:"json",
		 success:function(data){
                        closediv();
		 	if(!data.isLogin){
		 		alert("请登陆");
		 		window.location.replace("/login");
		 	}
		 	else if(!data.isAuth){
		 		alert("您没有权限");
		 	}
		 	else{
		 		if(!data.isSuccess){
		 			alert(data.message);
		 		}
		 		else{
		 			window.location.replace(replace);
		 		}
		 	}
		 },
		 error:function(data,status){
                      closediv();
		 	alert(status);
		 } 
  </script>
  
<form action="/mycloudregister/register.do" method="get">

	account:<input type="text" name="account"/></br>
	username:<input type="text" name="username"/></br>
	email:<input type="text" name="email"/></br>
	tele:<input type="text" name="teleNum"/></br>
	password:<input type="text" name="password"/></br>
	
	<input type="submit" value="Submit"/>
</form>		
 
</html> 

