<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="ctx" value="${pageContext.request.contextPath}" />


<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<meta http-equiv="Cache-Control" content="no-store" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="0" />

<%-- <link type="image/x-icon" href="${ctx}/static/images/favicon.ico" rel="shortcut icon"> --%>

<link rel="stylesheet" type="text/css" href="${ctx}/static/styles/default.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/static/easyui/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/static/easyui/themes/icon.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/static/jquery/jquery.loadmask.css" />
<link href="${ctx}/static/jquery-validation/1.11.1/validate.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="${ctx}/static/jquery/jquery-2.0.0.js"></script>
<script type="text/javascript" src="${ctx}/static/easyui/jquery.easyui.min.js"></script>
<script charset="UTF-8" type="text/javascript" src="${ctx}/static/easyui/locale/easyui-lang-zh_CN.js"></script>
<script charset="UTF-8" type="text/javascript" src="${ctx}/static/jquery/jquery.loadmask.min.js"/></script>
<script type="text/javascript" src="${ctx}/static/json2.js"></script>
<script src="${ctx}/static/jquery-validation/1.11.1/jquery.validate.min.js" type="text/javascript"></script>
<script src="${ctx}/static/jquery-validation/1.11.1/messages_bs_zh.js" type="text/javascript"></script>
<script charset="UTF-8" type="text/javascript" src="${ctx}/static/easyui/plugins/jquery.datagrid.js"></script>

<script type="text/javascript">

	function getHttpRequestPath(suffix) {
		return "${ctx}/" + suffix;
	}
	
	function keyCode(event) {
		event = event || window.event;
		if (event.keyCode == 13) {
			submit();
		}
	}
	
	$(function() {
		//查询工具条内按键
		$(".search_bar").keydown(searchConditionInput);
	});

	function searchConditionInput(event) {
		event = event || window.event;
		if (event.keyCode == 13) {
			query();
		}
	}
	
	function _error(error) {
		if (401 == error.status)
			_timeout();
	}

	function _timeout() {
		window.parent.loginAgain();
	}

	String.prototype.trim = function() {
		return this.replace(/(^[\s\u3000]*)|([\s\u3000]*$)/g, "");
	};
	
	/**
	 * 时间对象的格式化; eg:format="YYYY-MM-dd hh:mm:ss";
	 */
	Date.prototype.format = function(format) {
		var o = {
			"M+" : this.getMonth() + 1,	//month
			"d+" : this.getDate(),	//day
			"h+" : this.getHours(),	//hour
			"m+" : this.getMinutes(),	//minute
			"s+" : this.getSeconds(),	//second
			"q+" : Math.floor((this.getMonth() + 3) / 3),	//quarter
			"S" : this.getMilliseconds()	//millisecond
		};
		if (/(y+)/.test(format)) {
			format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
		}

		for (var k in o) {
			if (new RegExp("(" + k + ")").test(format)) {
				format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
			}
		}
		return format;
	};

	function createLinkElement(title, event, icon) {
		var str = '<a href="###" class="easyui-linkbutton l-btn l-btn-plain" plain="true" ';
		str = str + 'onclick="' + event + '">';
		str = str + '<span class="l-btn-text ' + icon + '" style="padding-left: 20px; ">';
		str = str + title + '</span></a>';
		return str;
	}
	
	function getMillSecond(dateBox_id) {
		var dateStr = $('#' + dateBox_id).datebox("getValue");
		if (null == dateStr || dateStr == "")
			return 0;
		var yy = dateStr.substring(0, 4);
		var mm = new Number(dateStr.substring(5, 7)) - 1;
		var dd = dateStr.substring(8, 10);
		var dt = new Date(yy, mm, dd, "00", "00", "00");
		return dt.getTime();
	}

</script>