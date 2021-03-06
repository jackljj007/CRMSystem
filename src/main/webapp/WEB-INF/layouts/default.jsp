<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="sitemesh" uri="http://www.opensymphony.com/sitemesh/decorator" %>  
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html>
<head>
<title>客户管理系统:<sitemesh:title/></title>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<meta http-equiv="Cache-Control" content="no-store" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="0" />

<%-- <link type="image/x-icon" href="${ctx}/static/images/favicon.ico" rel="shortcut icon"> --%>

<link rel="stylesheet" type="text/css" href="${ctx}/static/styles/default.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/static/easyui/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/static/easyui/themes/icon.css" />

<script type="text/javascript" src="${ctx}/static/jquery/jquery-2.0.0.js"></script>
<script type="text/javascript" src="${ctx}/static/easyui/jquery.easyui.min.js"></script>
<script charset="UTF-8" type="text/javascript" src="${ctx}/static/easyui/locale/easyui-lang-zh_CN.js"></script>

<sitemesh:head/>
</head>

<body>
	<div class="container">
<%-- 		<%@ include file="/WEB-INF/layouts/header.jsp"%> --%>
		<div id="content">
			<sitemesh:body/>
		</div>
<%-- 		<%@ include file="/WEB-INF/layouts/footer.jsp"%> --%>
	</div>
<%-- 	<script src="${ctx}/static/bootstrap/2.3.2/js/bootstrap.min.js" type="text/javascript"></script> --%>
</body>
</html>