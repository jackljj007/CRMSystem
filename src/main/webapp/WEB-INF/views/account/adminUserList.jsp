<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
	<title>用户信息</title>
	<%@ include file="../../layouts/top.jsp"%>
	<link rel="stylesheet" type="text/css" href="${ctx}/static/jquery/jquery.autocomplete.css" />
	
	<script charset="UTF-8" type="text/javascript" src="${ctx}/static/jquery/jquery-migrate-1.2.1.js"></script>
	<script charset="UTF-8" type="text/javascript" src="${ctx}/static/jquery/jquery.autocomplete.js"></script>
	<script type="text/javascript">
		var url = null;
		
		$(function() {
			query();
			
			$("#info_query").autocomplete(
				"${ctx}/admin/user/queryInfo", {
				minChars : 2,	//最小输入字数
				max : 10,	//最大显示条数
				//scroll: true,	//最多可以显示150个结果
				autoFill : true,
				dataType : "json",	//指定数据类型的渲染方式
				extraParams : {
					searchContent : function() {
						return $("#info_query").val();	//url的参数传递
					}
				},
				parse : function(data) {
					var rows = [];
					var d = data;
					for (var i = 0; i < d.length; i++) {
						rows[rows.length] = {data:d[i], value:d[i], result:d[i]};
					}
					return rows;
				},
				formatItem : function(row, i, n) {
					return row;
				}
			}).result(function(event, data, formatted) {
				$("#info_query").val(data);
			});
		});
		
		function query() {
			var str = $("#info_query").val();
			$('#dg').datagrid({
				url : "${ctx}/admin/user/list",
				pageNumber : 1,
				queryParams : {
					cmd : str
				}
			});
		}
		
		function formatTime(val, row, index) {
			var ret = "";
			d = new Date(row.registerDate.time);
			dateStr = d.format("yyyy-MM-dd");
			ret += dateStr;
			return ret;
		}
	
		function formatAction(val, row, index) {
			var ret = "";
			ret += createLinkElement('修改', 'edit(\'' + row.id + '\')', 'icon-edit');
			ret += createLinkElement('删除', 'del(\'' + row.id + '\')', 'icon-del');
			return ret;
		}
		
		function clear() {
			$("#id").val("");
			$("#loginName").val("");
			$("#name").val("");
			$("#plainPassword").val("");
			$("#confirmPassword").val("");
			$("#salt").val("");
			$("#roles").combobox('setValue', "");
			$('#registerDate').datebox('setValue', "");
			
			$('#registerDate').datebox("enable");
			$('#loginName').attr("readonly", false);
			$('#name').attr("readonly", false);
		}

		function del(id) {
			$.messager.confirm('系统提示', '确定删除此用户吗?', function(r) {
				if (r) {
					$.post(url = "${ctx}/admin/user/delUser", {
						id : id
					}, function(response) {
						if (response.toLowerCase() == "success") {
							$.messager.alert('系统提示', '提交修改成功！');
						} else {
							$.messager.alert('系统提示', '出错！');
						}
						$('#dg').datagrid('reload');
					});
				}
			});
		}

		function add() {
			clear();
			$("#loginName").focus();
			//为inputForm注册validate函数
			$("#inputForm").validate({
				rules : {
					loginName : {
						remote : "${ctx}/register/checkLoginName"
					}
				},
				messages : {
					loginName : {
						remote : "用户登录名已存在"
					}
				}
			});
			$('#dlg').dialog({
				modal : true
			}).dialog('open').dialog('setTitle', '增加用户');
			url = "${ctx}/admin/user/addUser";
			
			$('#registerDate').datebox("disable");
		}

		function edit(id) {
			clear();
			//为inputForm注册validate函数
			$("#inputForm").validate();

			getUser(id);

			$('#dlg').dialog({
				modal : true
			}).dialog('open').dialog('setTitle', '修改用户');
			url = "${ctx}/admin/user/updateUser";
			$('#loginName').attr("readonly", true);
			$('#name').attr("readonly", true);
		}

		function saveUser() {
			$('#dlg').mask("正在处理...");
			var id = $("#id").val();
			var loginName = $("#loginName").val();
			if (loginName.length == 0) {
				$.messager.alert('系统提示', '登录名不能为空！');
				$('#dlg').unmask();
				return;
			}
			var name = $("#name").val();
			if (name.length == 0) {
				$.messager.alert('系统提示', '用户名不能为空！');
				$('#dlg').unmask();
				return;
			}
			var plainPassword = $("#plainPassword").val();
			if (plainPassword.length == 0) {
				$.messager.alert('系统提示', '密码不能为空！');
				$('#dlg').unmask();
				return;
			}
			var confirmPassword = $("#confirmPassword").val();
			if (plainPassword != confirmPassword) {
				$.messager.alert('系统提示', '请输入相同密码！');
				$('#dlg').unmask();
				return;
			}
			var salt = $("#salt").val();
			var roles = $("#roles").combobox('getValue');
			var registerDate = $("#registerDate").datebox("getValue");
			$.post(
				url, {
					id : id.length > 0 ? new Number(id) : 0,
					loginName : loginName.length > 0 ? loginName : null,
					name : name.length > 0 ? name : null,
					plainPassword : plainPassword.length > 0 ? plainPassword : null,
					confirmPassword : confirmPassword.length > 0 ? confirmPassword : null,
					salt : salt.length > 0 ? salt : null,
					roles : roles.length > 0 ? roles : null,
					registerDate : registerDate.length > 0 ? registerDate : null
				}, function(response) {
					if (response.toLowerCase() == "success") {
						$.messager.alert('系统提示', '保存成功！');
						$('#dg').datagrid('reload');
						$('#dlg').dialog('close');
					} else if (response.toLowerCase() == "exist") {
						$.messager.alert('系统提示', '该用户已存在！');
					} else {
						$.messager.alert('系统提示', '保存出错！');
					}
					$('#dlg').unmask();
				}
			);
		}

		function getUser(id) {
			$.post(url = "${ctx}/admin/user/getUser", {
				id : id
			}, function(typesString) {
				var user = JSON.parse(typesString, function(key, value) {
					return value;
				});
				initUser(user);
			});
		}

		function initUser(user) {
			$("#id").val(user.id);
			$("#loginName").val(user.loginName);
			$("#name").val(user.name);
			$("#plainPassword").val(user.plainPassword);
			$("#confirmPassword").val(user.confirmPassword);
			$("#salt").val(user.salt);
			$("#roles").combobox('setValue', user.roles);
			d = new Date(user.registerDate);
			dateStr = d.format("yyyy-MM-dd");
			$('#registerDate').datebox('setValue', dateStr);
		}
	</script>
</head>

<body>
	<div id="toolbar" class="search_bar" style="height:auto">
		<table style="width:100%">
			<tbody>
				<tr class="line_0">
					<td class="search_title">
						<input type="text" id="info_query" class="input-medium" />
						<a href="#" class="easyui-linkbutton" onclick="query()" iconCls="icon-search">查询</a>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
	<a href="#" class="easyui-linkbutton" onclick="add()" iconCls="icon-add">增加</a>
	<table id="dg" class="easyui-datagrid" nowrap="false"
		style="width:auto; height:auto;" rownumbers="true"
		fitColumns="true" singleSelect="false" pagination="true" idField="id">
		<thead>
			<tr>
				<th field="loginName" width="50">登录名</th>
				<th field="name" width="50">用户名</th>
				<th field="roles" width="50">角色</th>
				<th field="registerDate" formatter="formatTime" width="50">注册时间</th>
				<th field="action" formatter="formatAction" width="50">操作</th>
			</tr>
		</thead>
	</table>
	
	<div id="dlg" class="easyui-dialog"
		style="width:800px; height:auto; padding: 10px 10px;" closed="true"
		buttons="#dlg-buttons">
		<div align="center">
			<form id="inputForm" method="post" class="form-horizontal">
				<input type="text" id="id" hidden="true" />
				<input type="text" id="salt" hidden="true" />
				<table class="formInfoTable" width="100%">
					<tr>
						<td class="formInfoTable_LableCol">登录名</td>
						<td class="formInfoTable_InfoCol">
							<input type="text" id="loginName" class="input-large required"/>
						</td>
						<td class="formInfoTable_LableCol">用户名</td>
						<td class="formInfoTable_InfoCol">
							<input type="text" id="name" class="input-large required"/>
						</td>
					</tr>
					<tr>
						<td class="formInfoTable_LableCol">密码</td>
						<td class="formInfoTable_InfoCol">
							<input type="password" id="plainPassword" class="input-large" placeholder="...请输入密码"/>
						</td>
						<td class="formInfoTable_LableCol">确认密码</td>
						<td class="formInfoTable_InfoCol">
							<input type="password" id="confirmPassword" class="input-large" equalTo="#plainPassword" />
						</td>
					</tr>
					<tr>
						<td class="formInfoTable_LableCol">角色</td>
						<td class="formInfoTable_InfoCol">
							<select id="roles" class="easyui-combobox">
								<option value="admin">管理员</option>
								<option value="user" selected>普通用户</option>
							</select>
						</td>
						<td class="formInfoTable_LableCol">注册日期</td>
						<td class="formInfoTable_InfoCol">
							<input id="registerDate" class="easyui-datebox"/>
						</td>
					</tr>
				</table>
			</form>
		</div>
	</div>
	<div id="dlg-buttons">
		<a href="#" class="easyui-linkbutton" iconCls="icon-ok" onclick="saveUser()">保存</a>
		<a href="#" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#dlg').dialog('close')">取消</a>
	</div>
</body>
</html>
