<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<html>
<head>
	<title>资料修改</title>
	<%@ include file="../../layouts/top.jsp"%>
</head>

<body>
	<form id="inputForm" action="${ctx}/profile" method="post" onsubmit="return check();" class="form-horizontal">
		<input type="hidden" name="id" value="${user.id}"/>
		<fieldset>
			<legend><small>资料修改</small></legend>
			<div class="control-group">
				<label for="name" class="control-label">用户名:</label>
				<div class="controls">
					<input type="text" id="name" name="name" value="${user.name}" class="input-large required" readonly="true" />
				</div>
			</div>
			<div class="control-group">
				<label for="plainPassword" class="control-label">密码:</label>
				<div class="controls">
					<input type="password" id="plainPassword" name="plainPassword" class="input-large" placeholder="...Leave it blank if no change"/>
				</div>
			</div>
			<div class="control-group">
				<label for="confirmPassword" class="control-label">确认密码:</label>
				<div class="controls">
					<input type="password" id="confirmPassword" name="confirmPassword" class="input-large" equalTo="#plainPassword" />
				</div>
			</div>
			<div class="form-actions">
				<input id="submit_btn" class="btn btn-primary" type="submit" value="提交"/>&nbsp;
<!-- 				<input id="cancel_btn" class="btn" type="button" value="返回" onclick="history.back()"/> -->
				<input id="reset_btn" class="btn btn-primary" type="reset" value="重置"/>&nbsp;
			</div>
		</fieldset>
	</form>
	
	<script>
		$(document).ready(function() {
			//聚焦第一个输入框
			$("#name").focus();
			//为inputForm注册validate函数
			$("#inputForm").validate();
		});
		
		function check() {
			if (plainPassword.length == 0) {
				$.messager.alert('系统提示', '密码不能为空！');
				return false;
			} else if (plainPassword != confirmPassword) {
				$.messager.alert('系统提示', '请输入相同密码！');
				return false;
			}
		}
	</script>
</body>
</html>
