<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ page import="org.apache.shiro.authc.ExcessiveAttemptsException"%>
<%@ page import="org.apache.shiro.authc.IncorrectCredentialsException"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
	<title>欢迎使用-客户管理系统</title>
	<%@ include file="../layouts/top.jsp"%>
	
	<script type="text/javascript">
		$(document).ready(function() {
			if (parent.window != window) {	//含有父窗体
				parent.window.location.href = "${ctx}/";
			}
			$("#username").focus();
			$("#loginForm").validate();
		});
	</script>
</head>

<body style="padding:100px;" onkeydown="keyCode(event)">
	
	<table height="100%" align="center" valign="middle">
		<tr>
        	<td align="center">
				<p style="font-size:200%; font-weight:bold;">客户管理系统</p>
			</td>
        </tr>
		<tr>
			<td>
				<form id="loginForm" action="${ctx}/login" method="post" class="form-horizontal">
					<table>
						<tr>
							<td>用户名:</td>
							<td>
								<input type='text' id='username' name='username'class="input-medium required"/>
							</td>
						</tr>
						<tr>
							<td>密码:</td>
							<td>
								<input type="password" id="password" name="password" class="input-medium required"/>
							</td>
						</tr>
						<tr>
							<td colspan='2' align="center">
								<input value="登录" type="submit" class="button"/>
								&nbsp;&nbsp;&nbsp;
								<input value="重置" type="reset" class="button" data-dismiss="alert"/>
							</td>
						</tr>
					</table>
					<%
						String error = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
						if (error != null) {
					%>
						<div class="alert alert-error input-medium controls">
							登录失败，请重试.
						</div>
					<%
						}
					%>
				</form>
			</td>
		</tr>
	</table>
</body>
</html>
