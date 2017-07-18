var url = null;
		
$(function() {
	query();
			
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
	$('#dg').datagrid({
		url : "${ctx}/custom/list",
		pageNumber : 1,
		queryParams : {
			cmd : str
		}
	});
}

function formatAction(val, row, index) {
	var ret = "";
	ret += createLinkElement('修改', 'edit(\'' + row.id + '\')', 'icon-edit');
	ret += createLinkElement('行程', 'trip(\'' + row.id + '\',\'' + row.customerName + '\',\'' + row.telephone + '\')', 'icon-edit');
	ret += createLinkElement('删除', 'del(\'' + row.id + '\')', 'icon-cancel');
	ret += createLinkElement('直接发送', 'saveSend(\'' + row.id + '\',\'' + row.customerName + '\',\'' + row.telephone + '\')', 'icon-add');
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

function del(id) {
	$.messager.confirm('系统提示', '确定删除此客户吗?', function(r){
		if (r) {
			$.post(
				url = "${ctx}/custom/delCustom",
				{id : id},
				function(response) {
					if (response.toLowerCase() == "success") {
						$.messager.alert('系统提示','提交修改成功！');  
					} else {
						$.messager.alert('系统提示', '出错！');
					}
					$('#dg').datagrid('reload');
				}
			);
		}
	});
}

function add() {
	clear();
	$("#companyName").focus();
	//为inputForm注册validate函数
	$("#inputForm").validate();
	$('#dlg').dialog({modal:true}).dialog('open').dialog('setTitle', '增加客户');
	url = "${ctx}/custom/add";
}

function edit(id) {
	clear();
	//为inputForm注册validate函数
	$("#inputForm").validate();
	
	getCustom(id);
	
	$('#dlg').dialog({modal:true}).dialog('open').dialog('setTitle', '修改客户');
	url = "${ctx}/custom/update";
	
	$('#companyName').attr("readonly", true);
	$('#customName').attr("readonly", true);
}

function saveCustom() {
	$('#dlg').mask("正在处理...");
	var id = $("#id").val();
	var companyName = $("#companyName").val();
	if (companyName.length == 0) {
		$.messager.alert('系统提示', '公司名不能为空！');
		$('#dlg').unmask();
		return;
	}
	var customName = $("#customName").val();
	if (customName.length == 0) {
		$.messager.alert('系统提示', '客户名不能为空！');
		$('#dlg').unmask();
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
			$('#dg').datagrid('reload');
			$('#dlg').dialog('close');
		} else if (response.toLowerCase() == "exist") {
			$.messager.alert('系统提示', '该客户已存在！');
		} else {
			$.messager.alert('系统提示', '保存出错！');
		}
		$('#dlg').unmask();
	});
}

function getCustom(id) {
	$.post(
		url = "${ctx}/custom/getCustom",
		{id : id},
		function(typesString) {
			var custom = JSON.parse(typesString, function(key, value) {
				return value;
			});
			initCustom(custom);
		}
	);
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
	$('#tripDlg').dialog({modal:true}).dialog('open').dialog('setTitle', '客户' + custom + '行程信息');
	$("#id").val(id);
	$("#customName").val(custom);
	$("#telephone").val(telephone);
	$('#dg2').datagrid({
		url : "${ctx}/trip/list",
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

function formatAction2(val, row, index) {
	var ret = "";
	ret += createLinkElement('修改', 'editTrip(\'' + row.id + '\')', 'icon-edit');
	ret += createLinkElement('删除', 'delTrip(\'' + row.id + '\')', 'icon-cancel');
	ret += createLinkElement('发送信息', 'sendMessage(\'' + row.id + '\')', 'icon-add');
	return ret;
}

function clear2() {
	$("#id2").val("");
	var id = $("#id").val();
	$("#customId").val(id);
	$("#tripId").val("");
	//$("#tripName").val("");
	$("#price").val("");
	//$("#tripDate").datebox('setValue', "");
	$("#tripSource").val("");
	$("#tripTarget").val("");
	$("#targetAddress").val("");
	$("#startTime").datetimebox('setValue', "");
	$("#endTime").datetimebox('setValue', "");
}

function delTrip(id) {
	$.messager.confirm('系统提示', '确定删除此行程吗?', function(r){
		if (r) {
			$.post(
				url = "${ctx}/trip/delTrip",
				{id : id},
				function(response) {
					if (response.toLowerCase() == "success") {
						$.messager.alert('系统提示','提交修改成功！');  
					} else {
						$.messager.alert('系统提示', '出错！');
					}
					$('#dg2').datagrid('reload');
				}
			);
		}
	});
}

function addTrip() {
	clear2();
	//$("#tripName").focus();
	//为inputForm注册validate函数
	$("#inputForm2").validate();
	$('#dlg2').dialog({modal:true}).dialog('open').dialog('setTitle', '增加行程');
	url = "${ctx}/trip/add";
}

function editTrip(id) {
	clear2();
	//聚焦第一个输入框
	//$("#tripName").focus();
	//为inputForm注册validate函数
	$("#inputForm2").validate();
	
	getTrip(id);
	
	$('#dlg2').dialog({modal:true}).dialog('open').dialog('setTitle', '修改行程');
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
	//var tripName = $("#tripName").val();
	var price = $("#price").val();
	//var tripDate = $("#tripDate").datebox("getValue");
	var tripSource = $("#tripSource").val();
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
		//tripName : tripName.length > 0 ? tripName : null,
		price : price.length > 0 ? new Number(price) : 0,
		//tripDate : tripDate.length > 0 ? tripDate : null,
		tripSource : tripSource.length > 0 ? tripSource : null,
		tripTarget : tripTarget.length > 0 ? tripTarget : null,
		targetAddress : targetAddress.length > 0 ? targetAddress : null,
		startTime : startTime.length > 0 ? startTime : null,
		endTime : endTime.length > 0 ? endTime : null
	}, function(response) {
		if (response.toLowerCase() == "success") {
			$.messager.alert('系统提示', '保存成功！');
			$('#dg2').datagrid('reload');
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
	//$("#tripName").val(trip.tripName);
	$("#price").val(trip.price);
	//d = new Date(trip.tripDate);
	//dateStr = d.format("yyyy-MM-dd");
	//$('#tripDate').datebox('setValue', dateStr);
	$("#tripSource").val(trip.tripSource);
	$("#tripTarget").val(trip.tripTarget);
	$("#targetAddress").val(trip.targetAddress);
	d = new Date(trip.startTime.time);
	dateStr = d.format("yyyy-MM-dd hh:mm:ss");
	$("#startTime").datetimebox('setValue', dateStr);
	d = new Date(trip.endTime.time);
	dateStr = d.format("yyyy-MM-dd hh:mm:ss");
	$("#endTime").datetimebox('setValue', dateStr);
}

function exportExcel() {
	$.messager.confirm('系统提示', '确定导出excel吗?', function(r){
		if (r) {
			$("#exportForm").attr("action", "${ctx}/custom/exportExcel");
			$("#exportForm").submit();
		}
	});
}

//上传文件
function uploadBath() {
	$.ajaxFileUpload({
		url : "${ctx}/custom/uploadFile",
		secureuri : false,
		fileElementId : 'fileToUpload',
		dataType : 'json',
		data : {fileToUploadId:'fileToUpload', name:'i22222d'},
		success : function(data, status) {
			var msg = JSON.parse(data, function(key, value) {
				return value;
			});

			if (msg.STATE == "NotFolder") {
				alert("服务器未指定路径!请联系管理员");
			}
			if (msg.STATE == "empty") {
				alert("不能上传空文件");
			}
			if (msg.STATE == "error") {
				alert("上传失败");
			}
			if (msg.STATE == "success") {
				$('#uploadMsg').html('上传成功,点击"导入"按钮 开始执行');
				$('#filePathId').val(msg.FILEPATH);
				$('#InductionButtons').linkbutton({
					disabled : false
				});
			}
		},
		error : function(data, status, e) {
			alert("is error");
		}
	});
	return false;
}

//导入数据
function saveBatch() {
	var filePath = $('#filePathId').val();
	if (filePath == "") {
		$.messager.alert('系统提示', '请先上传数据文件');
		return;
	}
	$('#recordMsg').html('<img src="images/loading.gif"/>请不要关闭窗口，正在处理中...');
	$('#batchDlg').mask("正在处理...");
	$.post("${ctx}/custom/importExcel", {
		filePath : filePath
	}, function(response) {
		var msg = JSON.parse(response, function(key, value) {
			return value;
		});
		if (msg.STATE.toLowerCase() == "success") {
			$.messager.alert('系统提示', '保存成功！');
			$('#uploadMsg').html("上传成功！");

			var resultMsg = '总共' + msg.totalCount + '条 成功:' + msg.successCount + ' 失败:' + msg.errorCount;
			resultMsg = resultMsg + '<a href="#" onclick="openMsgDlg()" > 查看</a>';

			$('#resultMsg').html(resultMsg);
			$('#recordMsg').html(msg.recordMsg);
			$('#filePathId').val(null);
			$('#dg').datagrid('reload');

			$('#InductionButtons').linkbutton({
				disabled : true
			});
		} else {
			$.messager.alert('导入出错', msg.errorMsg);
		}
		$('#batchDlg').unmask();
	});
}

/**
 *  批量导入
 */
function importExcel() {
	$('#filePathId').val(null);
	$('#uploadMsg').html("请先上传EXCEL文件");
	$('#InductionButtons').linkbutton({
		disabled : true
	});
	$('#batchDlg').dialog({
		modal : true
	}).dialog('open').dialog('setTitle', '批量导入');
}

function checkType() {
	var file = $('#fileInput');
	var val = file.val();
	if (val) {
		var suffix = val.substring(val.lastIndexOf("."));
		if (suffix != ".xls") {
			$.messager.alert('系统提示', '格式不正确,请选择xls文件！');
			file.after(file.clone().val(""));
			file.remove();
			return false;
		}
		return true;
	} else if (val == "") {
		return true;
	}
}

function openMsgDlg() {
	$('#msgDlg').dialog({
		modal : true
	}).dialog('open').dialog('setTitle', '错误记录');
}

function downlodFile() {
	$("#exportForm").attr("action", getHttpRequestPath("/custom/downlodFile"));
	$("#exportForm").submit();
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
	var id = $('#tripId').val();
	var customId = $('#id').val();
	var telephone = $('#telephone').val();
	if (telephone.length != 11) {
		$.messager.alert('系统提示', '客户手机号有误，请修改后再发送');
	} else {
		$.post(
			url = "${ctx}/trip/sendMessage", {
				tripId : id,
				customId : customId
			}, function(response) {
				if (response.toLowerCase() == "success") {
					$.messager.alert('系统提示','发送信息成功!');
					$('#messageDlg').dialog('close');
				} else {
					$.messager.alert('系统提示', '出错!');
				}
				$('#dg2').datagrid('reload');
			}
		);
	}
}

//function saveSend() {
function saveSend(customId, customName, telephone) {
	$('#message').val("");
	$('#message').attr("readonly", false);
	$('#msgBtn1').show();
	$('#msgBtn2').hide();
	//var customId = $('#id').val();
	//var customName = $('#customName').val();
	//var telephone = $('#telephone').val();
	$('#messageDlg').dialog({modal:true}).dialog('open').dialog('setTitle', '确定不保存行程信息直接向客户' + customName + '(' + telephone + ')发送此行程信息吗?');
}

function saveMessage() {
	var msgStr = $('#message').val();
	if (msgStr.trim().length < 1) {
		$.messager.alert('系统提示', '内容不能为空');
		return;
	}
	$.post(
		url = "${ctx}/trip/saveSend", {
			message : msgStr,
			customId : $('#id').val()
		}, function(response) {
			if (response.toLowerCase() == "success") {
				$.messager.alert('系统提示', '发送信息成功!');
				$('#messageDlg').dialog('close');
			} else if (response.toLowerCase() == "different") {
				$.messager.alert('系统提示', '客户信息不一致,请检查信息后重发');
			} else {
				$.messager.alert('系统提示', '出错!');
			}
			$('#dg2').datagrid('reload');
		}
	);
}