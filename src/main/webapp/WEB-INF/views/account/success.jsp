<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%response.setStatus(200);%>

<!DOCTYPE html>
<html>
<head>
	<title>修改成功</title>
	<script type="text/javascript">
		function logout() {
			window.parent.logout();
		}
	</script>
</head>

<body>
	<h2>修改成功,请关闭此窗口.</h2>
	<div><a href="" onclick="logout()">点击此处重新登录</a></div>
</body>
</html>