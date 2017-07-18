<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
	<title>客户管理系统</title>
	<%@ include file="../layouts/top.jsp"%>
	
	<script type="text/javascript">
		$(document).ready(function() {
			if (parent.window != window) {	//含有父窗体
				parent.window.location.href = "${ctx}/";
			}
		});
	
		$(function() {
			$('#tabs').tabs('add',{
				title:'欢迎使用',
				content:createFrame('main')
			});
			
			$('li a').click(function() {
				var tabTitle = $(this).children('.nav').text();
	
				var url = $(this).attr("rel");
				addTab(tabTitle, url, '');
				$('li div').removeClass("selected");
				$(this).parent().addClass("selected");
			}).hover(function() {
				$(this).parent().addClass("hover");
			}, function() {
				$(this).parent().removeClass("hover");
			});
	
		});
	
		function addTab(subtitle, url, icon) {
			if (!$('#tabs').tabs('exists', subtitle)) {
				$('#tabs').tabs('add', {
					title : subtitle,
					content : createFrame(url),
					closable : true,
					icon : icon
				});
			} else {
				$('#tabs').tabs('select', subtitle);
			}
		}
	
		function createFrame(url) {
			var s = '<iframe scrolling="auto" frameborder="0" src="' + url
					+ '" style="width:100%;height:100%;"></iframe>';
			return s;
		}
		
		function editPwd() {
			$('#dlg').dialog({modal:true}).dialog('open').dialog('setTitle', '修改密码');
			$("#pwdFrame").attr("src", "${ctx}/profile");
		}
		
		function logout() {
			window.location.href = "${ctx}/logout";
		}
	</script>
</head>

<body class="easyui-layout" style="overflow-y: hidden" scroll="no">
	<div region="north" split="true" border="false"
		style="overflow: hidden; height: 75px; background-color: #E0ECFF;">
		<div id="pwin"></div>
		<table width="100%" class="default">
			<tr>
				<td width="150" rowspan="4" align="center" valign="bottom">
					<img src="" />
				</td>
			</tr>
			<tr>
				<td height="38" colspan="4" algin="center">
					<strong style="font-size: 20px;">客户管理系统</strong>
				</td>
			</tr>
			<tr>
				<td colspan="4">
					<span style="float: right; padding-right: 50px;" class="head">
						您好！<strong><shiro:user>
						<shiro:principal property="name"/>
						</shiro:user></strong>
						&nbsp;&nbsp;&nbsp;&nbsp;
						<a href="#" onclick="editPwd()">修改密码</a>
						<a href="${ctx}/logout">退出登录</a>
					</span>
				</td>
			</tr>
		</table>
	</div>

	<div region="south" split="true" style="height: 30px; background: #D2E0F2;">
		<div class="footer" style="text-align: center;"></div>
	</div>
	<shiro:hasRole name="admin">
		<div region="west" hide="true" split="true" title="后台管理"
			style="width: 180px;" id="west">
			<ul>
				<li>
					<div>
						<a ref="0" href="#" rel="${ctx}/custom">
							<span class="icon-add"></span>
							<span class="nav">客户管理</span>
						</a>
					</div>
				</li>
				<li>
					<div>
						<a ref="1" href="#" rel="${ctx}/admin/user">
							<span class="icon-edit"></span>
							<span class="nav">用户管理</span>
						</a>
					</div>
				</li>
			</ul>
		</div>
	</shiro:hasRole>
	<div id="mainPanle" region="center" style="overflow-y: hidden">
		<!--内容页-->
		<div id="tabs" class="easyui-tabs" fit="true" border="false"></div>
	</div>
	
	<div id="dlg" class="easyui-dialog" closed="true"
		style="width:800px; height:300px; padding: 10px 10px;">
		<iframe scrolling="auto" frameborder="0" id="pwdFrame" src="" style="width:100%;height:100%;"></iframe>
	</div>
</body>
</html>