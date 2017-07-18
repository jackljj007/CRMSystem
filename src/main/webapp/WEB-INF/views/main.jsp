<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
	<title>主界面</title>
	<%@ include file="../layouts/top.jsp"%>
	
	<link rel="stylesheet" type="text/css" href="${ctx}/static/jquery/jquery.autocomplete.css" />
	
	<script charset="UTF-8" type="text/javascript" src="${ctx}/static/jquery/jquery-migrate-1.2.1.js"></script>
	<script charset="UTF-8" type="text/javascript" src="${ctx}/static/easyui/plugins/jquery.datagrid.js"></script>
	<script charset="UTF-8" type="text/javascript" src="${ctx}/static/jquery/jquery.autocomplete.js"></script>
	
	<script type="text/javascript">
		var url = null;
		
		$(function() {
			$("#info_query").autocomplete(
				"${ctx}/custom/queryInfo", {
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
			if (str != '') {
				$('#dg').datagrid({
					url : "${ctx}/main/list",
					pageNumber : 1,
					queryParams : {
						cmd : str
					}
				});
			}
		}

		function formatAction(val, row, index) {
			var ret = "";
			ret += createLinkElement('查看', 'look(\'' + row.id + '\')', 'icon-search');
			return ret;
		}
		
		function look(id) {
			$('#dlg').dialog({modal:true,top:'10px'}).dialog('open').dialog('setTitle', '客户信息');
			$('#dg2').datagrid({
				url : "${ctx}/main/list2",
				pageNumber : 1,
				queryParams : {
					cmd : id
				}
			});
		}
		
		function formatAction2(val, row, index) {
			var ret = "";
			ret += createLinkElement('修改', 'edit(\'' + row.id + '\')', 'icon-edit');
			ret += createLinkElement('行程', 'trip(\'' + row.id + '\',\'' + row.customerName + '\',\'' + row.telephone + '\')', 'icon-edit');
			ret += createLinkElement('直接发送', 'sendRecord(\'' + row.id + '\',\'' + row.customerName + '\',\'' + row.telephone + '\')', 'icon-add');
			return ret;
		}
		
		function clear() {
			$("#id").val("");
			$("#companyName").val("");
			$("#customName").val("");
			$("#idNumber").val("");
			$("#telephone").val("");
			$("#address").val("");
			$("#membershipNum").val("");
			$("#membershipLevel").combobox('setValue', "");
			
			$('#companyName').attr("readonly", false);
			$('#customName').attr("readonly", false);
		}
		
		function edit(id) {
			clear();
			//聚焦第一个输入框
			//为inputForm注册validate函数
			$("#inputForm").validate();
			
			getCustom(id);
			
			$('#dlg1').dialog({modal:true}).dialog('open').dialog('setTitle', '修改客户');
			url = "${ctx}/custom/update";
			
			$('#companyName').attr("readonly", true);
			$('#customName').attr("readonly", true);
		}
		
		function saveCustom() {
			$('#dlg1').mask("正在处理...");
			var id = $("#id").val();
			var companyName = $("#companyName").val();
			if (companyName.length == 0) {
				$.messager.alert('系统提示', '公司名不能为空！');
				$('#dlg1').unmask();
				return;
			}
			var customName = $("#customName").val();
			if (customName.length == 0) {
				$.messager.alert('系统提示', '客户名不能为空！');
				$('#dlg1').unmask();
				return;
			}
			var idNumber = $("#idNumber").val();
			var telephone = $("#telephone").val();
			var address = $("#address").val();
			var membershipNum = $("#membershipNum").val();
			var membershipLevel = $("#membershipLevel").combobox('getValue');
			$.post(url, {
				id : id.length > 0 ? new Number(id) : 0,
				companyName : companyName.length > 0 ? companyName : null,
				customName : customName.length > 0 ? customName : null,
				idNumber : idNumber.length > 0 ? idNumber : null,
				telephone : telephone.length > 0 ? telephone : null,
				address : address.length > 0 ? address : null,
				membershipNum : membershipNum.length > 0 ? membershipNum : null,
				membershipLevel : membershipLevel.length > 0 ? new Number(membershipLevel) : 0
			}, function(response) {
				if (response.toLowerCase() == "success") {
					$.messager.alert('系统提示', '保存成功！');
					$('#dg2').datagrid('reload');
					$('#dlg1').dialog('close');
				} else {
					$.messager.alert('系统提示', '保存出错！');
				}
				$('#dlg1').unmask();
			});
		}

		function getCustom(id) {
			$.post(url = "${ctx}/custom/getCustom", {
				id : id
			}, function(typesString) {
				var custom = JSON.parse(typesString, function(key, value) {
					return value;
				});
				initCustom(custom);
			});
		}

		function initCustom(custom) {
			$("#id").val(custom.id);
			$("#companyName").val(custom.companyName);
			$("#customName").val(custom.customerName);
			$("#idNumber").val(custom.idNumber);
			$("#telephone").val(custom.telephone);
			$("#address").val(custom.address);
			$("#membershipNum").val(custom.membershipNum);
			$("#membershipLevel").combobox('setValue', custom.membershipLevel);
		}

		function trip(id, custom, telephone) {
			$('#tripDlg').dialog({
				modal : true
			}).dialog('open').dialog('setTitle', '客户' + custom + '行程信息');
			$("#id").val(id);
			$("#customName").val(custom);
			$("#telephone").val(telephone);
			$('#dg3').datagrid({
				url : "${ctx}/trip/list",
				pageNumber : 1,
				queryParams : {
					cmd : id
				}
			});
		}
		
		function sendRecord(id, custom, telephone) {
			$('#sendDlg').dialog({modal:true}).dialog('open').dialog('setTitle', '客户' + custom + '已发信息');
			$("#id").val(id);
			$("#customName").val(custom);
			$("#telephone").val(telephone);
			$('#dg4').datagrid({
				url : "${ctx}/message/list",
				pageNumber : 1,
				queryParams : {
					cmd : id
				}
			});
		}
		
		function formatTripDate(val, row, index) {
			var ret = "";
			d = new Date(row.tripDate.time);
			dateStr = d.format("yyyy-MM-dd");
			ret += dateStr;
			return ret;
		}
		
		function formatStartTime(val, row, index) {
			var ret = "";
			d = new Date(row.startTime.time);
			dateStr = d.format("yyyy-MM-dd hh:mm:ss");
			ret += dateStr;
			return ret;
		}
		
		function formatEndTime(val, row, index) {
			var ret = "";
			d = new Date(row.endTime.time);
			dateStr = d.format("yyyy-MM-dd hh:mm:ss");
			ret += dateStr;
			return ret;
		}

		function formatAction3(val, row, index) {
			var ret = "";
			ret += createLinkElement('修改', 'editTrip(\'' + row.id + '\')', 'icon-edit');
			ret += createLinkElement('发送信息', 'sendMessage(\'' + row.id + '\')', 'icon-add');
			return ret;
		}
		
		function formatAction4(val, row, index) {
			var ret = "";
			ret += createLinkElement('重新发送', 'sendAgain(\'' + row.id + '\')', 'icon-add');
			return ret;
		}

		function clear2() {
			$("#id2").val("");
			var id = $("#id").val();
			$("#customId").val(id);
			$("#tripId").val("");
			$("#price").val("");
			$("#tripSource").val("");
			$("#sourceAddress").val("");
			$("#tripTarget").val("");
			$("#targetAddress").val("");
			$("#startTime").datetimebox('setValue', "");
			$("#endTime").datetimebox('setValue', "");
		}

		function delTrip(id) {
			$.messager.confirm('系统提示', '确定删除此行程吗?', function(r) {
				if (r) {
					$.post(url = "${ctx}/trip/delTrip", {
						id : id
					}, function(response) {
						if (response.toLowerCase() == "success") {
							$.messager.alert('系统提示', '提交修改成功！');
						} else {
							$.messager.alert('系统提示', '出错！');
						}
						$('#dg3').datagrid('reload');
					});
				}
			});
		}

		function addTrip() {
			clear2();
			//为inputForm注册validate函数
			$("#inputForm2").validate();
			$('#dlg2').dialog({
				modal : true
			}).dialog('open').dialog('setTitle', '增加行程');
			url = "${ctx}/trip/add";
		}

		function editTrip(id) {
			clear2();
			//聚焦第一个输入框
			//为inputForm注册validate函数
			$("#inputForm2").validate();

			getTrip(id);

			$('#dlg2').dialog({
				modal : true
			}).dialog('open').dialog('setTitle', '修改行程');
			url = "${ctx}/trip/update";
		}

		function saveTrip() {
			$('#dlg2').mask("正在处理...");
			var id = $("#id2").val();
			var customId = $("#customId").val();
			var tripId = $("#tripId").val();
			if (tripId.length == 0) {
				$.messager.alert('系统提示', '航班号不能为空！');
				$('#dlg2').unmask();
				return;
			}
			var price = $("#price").val();
			var tripSource = $("#tripSource").val();
			var sourceAddress = $("#sourceAddress").val();
			var tripTarget = $("#tripTarget").val();
			var targetAddress = $("#targetAddress").val();
			var startTime = $("#startTime").datetimebox("getValue");
			if (startTime.length == 0) {
				$.messager.alert('系统提示', '起飞时间不能为空！');
				$('#dlg2').unmask();
				return;
			}
			var endTime = $("#endTime").datetimebox("getValue");
			if (endTime.length == 0) {
				$.messager.alert('系统提示', '到达时间不能为空！');
				$('#dlg2').unmask();
				return;
			}
			$.post(url, {
				id : id.length > 0 ? new Number(id) : 0,
				customId : customId.length > 0 ? new Number(customId) : 0,
				tripId : tripId.length > 0 ? tripId : null,
				price : price.length > 0 ? new Number(price) : 0,
				tripSource : tripSource.length > 0 ? tripSource : null,
				sourceAddress : sourceAddress.length > 0 ? sourceAddress : null,
				tripTarget : tripTarget.length > 0 ? tripTarget : null,
				targetAddress : targetAddress.length > 0 ? targetAddress : null,
				startTime : startTime.length > 0 ? startTime : null,
				endTime : endTime.length > 0 ? endTime : null
			}, function(response) {
				if (response.toLowerCase() == "success") {
					$.messager.alert('系统提示', '保存成功！');
					$('#dg3').datagrid('reload');
					$('#dlg2').dialog('close');
				} else {
					$.messager.alert('系统提示', '保存出错！');
				}
				$('#dlg2').unmask();
			});
		}

		function getTrip(id) {
			$.post(url = "${ctx}/trip/getTrip", {
				id : id
			}, function(typesString) {
				var trip = JSON.parse(typesString, function(key, value) {
					return value;
				});
				initTrip(trip);
			});
		}

		function initTrip(trip) {
			$("#id2").val(trip.id);
			$("#customId").val(trip.customId);
			$("#tripId").val(trip.tripId);
			$("#price").val(trip.price);
			$("#tripSource").val(trip.tripSource);
			$("#sourceAddress").val(trip.sourceAddress);
			$("#tripTarget").val(trip.tripTarget);
			$("#targetAddress").val(trip.targetAddress);
			d = new Date(trip.startTime.time);
			dateStr = d.format("yyyy-MM-dd hh:mm:ss");
			$("#startTime").datetimebox('setValue', dateStr);
			d = new Date(trip.endTime.time);
			dateStr = d.format("yyyy-MM-dd hh:mm:ss");
			$("#endTime").datetimebox('setValue', dateStr);
		}
		
		function review(customId, tripId) {
			$.post(
				url = "${ctx}/trip/reviewMessage", {
					customId : customId,
					tripId : tripId
				}, function(message) {
					$.messager.alert('系统提示', message);
				}
			);
		}
		
		function sendMessage(id) {
			$('#message').val();
			$('#message').attr("readonly", true);
			$('#msgBtn1').hide();
			$('#msgBtn2').show();
			var customId = $('#id').val();
			var customName = $('#customName').val();
			var telephone = $('#telephone').val();
			$('#tripId').val(id);
			$('#messageDlg').dialog({modal:true}).dialog('open').dialog('setTitle', '确定向客户' + customName + '(' + telephone + ')发送此行程信息吗?');
			$.post(
				url = "${ctx}/trip/reviewMessage", {
					customId : customId,
					tripId : id
				}, function(message) {
					$('#message').val(message);
				}
			);
		}
		
		function confirmSend() {
			var customId = $('#id').val();
			var telephone = $('#telephone').val();
			if (telephone.length != 11) {
				$.messager.alert('系统提示', '客户手机号有误，请修改后再发送');
			} else {
				$.post(
					url = "${ctx}/trip/sendMessage", {
						customId : customId,
						message : $('#message').val()
					}, function(response) {
						if (response.toLowerCase() == "success") {
							$.messager.alert('系统提示', '发送信息成功!');
							$('#messageDlg').dialog('close')
						} else {
							$.messager.alert('系统提示', '出错!');
						}
						$('#dg3').datagrid('reload');
					}
				);
			}
		}
		
		function sendAgain(id) {
			$('#message').val("");
			$('#message').attr("readonly", true);
			$('#msgBtn1').show();
			$('#msgBtn2').hide();
			var customId = $('#id').val();
			var customName = $('#customName').val();
			var telephone = $('#telephone').val();
			$('#messageDlg').dialog({modal:true}).dialog('open').dialog('setTitle', '确定不保存行程信息直接向客户' + customName + '(' + telephone + ')发送此行程信息吗?');
			$.post(
				url = "${ctx}/message/getMessage", {
					id : id
				}, function(typesString) {
					var message = JSON.parse(typesString, function(key, value) {
						return value;
					});
					$('#message').val(message.message);
				}
			);
		}
		
		function saveSend() {
			$('#message').val("");
			$('#message').attr("readonly", false);
			$('#msgBtn1').show();
			$('#msgBtn2').hide();
			var customId = $('#id').val();
			var customName = $('#customName').val();
			var telephone = $('#telephone').val();
			$('#messageDlg').dialog({modal:true}).dialog('open').dialog('setTitle', '确定不保存行程信息直接向客户' + customName + '(' + telephone + ')发送此行程信息吗?');
		}
		
		function saveMessage() {
			var msgStr = $('#message').val();
			if (msgStr.trim().length < 1) {
				$.messager.alert('系统提示', '内容不能为空');
				return;
			}
			$.post(
				url = "${ctx}/trip/sendMessage", {
					customId : $('#id').val(),
					message : msgStr
				}, function(response) {
					if (response.toLowerCase() == "success") {
						$.messager.alert('系统提示', '发送信息成功!');
						$('#messageDlg').dialog('close');
					} else if (response.toLowerCase() == "different") {
						$.messager.alert('系统提示', '客户信息不一致,请检查信息后重发');
					} else {
						$.messager.alert('系统提示', '出错!');
					}
					$('#dg4').datagrid('reload');
				}
			);
		}
	</script>
</head>

<body>
<!-- 查询框 -->
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
<!-- 客户粗略信息表格 -->
	<table id="dg" class="easyui-datagrid" nowrap="false"
		style="width:auto; height:auto;" rownumbers="true"
		fitColumns="true" singleSelect="false" pagination="true" idField="id">
		<thead>
			<tr>
				<th field="companyName" width="50">公司名称</th>
				<th field="customerName" width="50">客户姓名</th>
				<th field="action" formatter="formatAction" width="50">操作</th>
			</tr>
		</thead>
	</table>
<!-- 客户详细信息表格 -->
	<div id="dlg" class="easyui-dialog"
		style="width:1080px; height:auto; padding: 1px 1px;" closed="true">
		<div align="left">
			<table id="dg2" class="easyui-datagrid" nowrap="false"
				style="width:auto; height:auto;" rownumbers="true"
				fitColumns="true" singleSelect="false" pagination="true" idField="id">
				<thead>
					<tr>
						<th field="companyName" width="130">公司名称</th>
						<th field="customerName" width="35">客户姓名</th>
						<th field="idNumber" width="80">证件号码</th>
						<th field="telephone" width="50">联系方式</th>
						<th field="address" width="150">联系地址</th>
						<th field="membershipNum" width="35">会员卡号</th>
						<th field="membershipLevel" width="35">会员级别</th>
<!-- 						<th field="availableScore" width="30">可用积分</th> -->
<!-- 						<th field="totalScore" width="30">累计积分</th> -->
						<th field="action" formatter="formatAction2" width="80">操作</th>
					</tr>
				</thead>
			</table>
		</div>
	</div>
<!-- 旅程表格 -->
	<div id="tripDlg" class="easyui-dialog"
		style="width:850px; height:auto; padding: 10px 10px;" closed="true">
		<a href="#" class="easyui-linkbutton" onclick="addTrip()" iconCls="icon-add">增加</a>
		<table id="dg3" class="easyui-datagrid" nowrap="false"
			style="width:auto; height:auto;" rownumbers="true"
			fitColumns="true" singleSelect="false" pagination="true" idField="id">
			<thead>
				<tr>
					<th field="tripId" width="50">航班号</th>
					<th field="tripSource" width="50">始发地</th>
					<th field="tripTarget" width="50">目的地</th>
					<th field="startTime" formatter="formatStartTime" width="50">起飞时间</th>
					<th field="endTime" formatter="formatEndTime" width="50">到达时间</th>
					<th field="price" width="50">票价</th>
					<th field="action" formatter="formatAction3" width="100">操作</th>
				</tr>
			</thead>
		</table>
	</div>
<!-- 发送短信表格 -->
	<div id="sendDlg" class="easyui-dialog"
		style="width:850px; height:auto; padding: 10px 10px;" closed="true">
		<a href="#" class="easyui-linkbutton" onclick="saveSend()" iconCls="icon-add">直接发送</a>
		<table id="dg4" class="easyui-datagrid" nowrap="false"
			style="width:auto; height:auto;" rownumbers="true"
			fitColumns="true" singleSelect="false" pagination="true" idField="id">
			<thead>
				<tr>
					<th field="message" width="500">已发信息</th>
					<th field="action" formatter="formatAction4" width="100">操作</th>
				</tr>
			</thead>
		</table>
	</div>
<!-- 客户信息框 -->
	<div id="dlg1" class="easyui-dialog"
		style="width:800px; height:auto; padding: 10px 10px;" closed="true"
		buttons="#dlg1-buttons">
		<div align="center">
			<form id="inputForm" method="post" class="form-horizontal">
				<input type="hidden" id="id" />
				<table class="formInfoTable" width="100%">
					<tr>
						<td class="formInfoTable_LableCol">公司名称</td>
						<td class="formInfoTable_InfoCol">
							<input type="text" id="companyName" class="input-large required"/>
						</td>
						<td class="formInfoTable_LableCol">客户姓名</td>
						<td class="formInfoTable_InfoCol">
							<input type="text" id="customName" class="input-large required"/>
						</td>
					</tr>
					<tr>
						<td class="formInfoTable_LableCol">证件号码</td>
						<td class="formInfoTable_InfoCol">
							<input type="text" id="idNumber" class="input-large required"/>
						</td>
						<td class="formInfoTable_LableCol">联系方式</td>
						<td class="formInfoTable_InfoCol">
							<input type="text" id="telephone" class="input-large required"/>
						</td>
					</tr>
					<tr>
						<td class="formInfoTable_LableCol">联系地址</td>
						<td class="formInfoTable_InfoCol" colspan="3">
							<input type="text" id="address" class="input-large required"/>
						</td>
					</tr>
					<tr>
						<td class="formInfoTable_LableCol">会员卡号</td>
						<td class="formInfoTable_InfoCol">
							<input type="text" id="membershipNum" class="input-large required"/>
						</td>
						<td class="formInfoTable_LableCol">会员级别</td>
						<td class="formInfoTable_InfoCol">
							<select id="membershipLevel" class="easyui-combobox">
								<option value="1" selected>Level 1</option>
								<option value="2">Level 2</option>
								<option value="3">Level 3</option>
							</select>
						</td>
					</tr>
				</table>
			</form>
		</div>
	</div>
	<div id="dlg1-buttons">
		<a href="#" class="easyui-linkbutton" iconCls="icon-ok" onclick="saveCustom()">保存</a>
		<a href="#" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#dlg1').dialog('close')">取消</a>
	</div>
<!-- 旅程框 -->
	<div id="dlg2" class="easyui-dialog"
		style="width:800px; height:auto; padding: 10px 10px;" closed="true"
		buttons="#dlg2-buttons">
		<div align="center">
			<form id="inputForm2" method="post" class="form-horizontal">
				<input type="hidden" id="id2" />
				<input type="hidden" id="customId" />
				<table class="formInfoTable" width="100%">
					<tr>
						<td class="formInfoTable_LableCol">航班号</td>
						<td class="formInfoTable_InfoCol">
							<input type="text" id="tripId" class="input-large required"/>
						</td>
						<td class="formInfoTable_LableCol">票价</td>
						<td class="formInfoTable_InfoCol">
							<input id="price" class="easyui-numberbox" precision="2" />
						</td>
					</tr>
					<tr>
						<td class="formInfoTable_LableCol">始发地</td>
						<td class="formInfoTable_InfoCol">
							<input type="text" id="tripSource" class="input-large required"/>
						</td>
						<td class="formInfoTable_LableCol">目的地</td>
						<td class="formInfoTable_InfoCol">
							<input type="text" id="tripTarget" class="input-large required"/>
						</td>
					</tr>
					<tr>
						<td class="formInfoTable_LableCol">始发地址</td>
						<td class="formInfoTable_InfoCol">
							<input type="text" id="sourceAddress" class="input-large required"/>
						</td>
						<td class="formInfoTable_LableCol">目的地址</td>
						<td class="formInfoTable_InfoCol">
							<input type="text" id="targetAddress" class="input-large required"/>
						</td>
					</tr>
					<tr>
						<td class="formInfoTable_LableCol">起飞时间</td>
						<td class="formInfoTable_InfoCol">
							<input id="startTime" class="easyui-datetimebox"/>
						</td>
						<td class="formInfoTable_LableCol">到达时间</td>
						<td class="formInfoTable_InfoCol">
							<input id="endTime" class="easyui-datetimebox"/>
						</td>
					</tr>
				</table>
			</form>
		</div>
	</div>
	<div id="dlg2-buttons">
		<a href="#" class="easyui-linkbutton" iconCls="icon-ok" onclick="saveTrip()">保存</a>
		<a href="#" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#dlg2').dialog('close')">取消</a>
	</div>
<!-- 短信框 -->
	<div id="messageDlg" class="easyui-dialog" buttons="#messageDlg-buttons"
		style="width:550px; height:auto; padding: 10px 10px;" closed="true">
		<div align="center">
			<table>
				<tr>
					<td>
						<textarea id="message" style="width:500px;height:50px;"></textarea>
					</td>
				</tr>
			</table>
		</div>
	</div>
	<div id="messageDlg-buttons">
		<a href="#" class="easyui-linkbutton" id="msgBtn1" iconCls="icon-ok" onclick="saveMessage()">直接发送</a>
		<a href="#" class="easyui-linkbutton" id="msgBtn2" iconCls="icon-ok" onclick="confirmSend()">确认发送</a>
		<a href="#" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#messageDlg').dialog('close')">取消</a>
	</div>
</body>
</html>
