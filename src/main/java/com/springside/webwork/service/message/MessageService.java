/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.springside.webwork.service.message;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.persistence.DynamicSpecifications;
import org.springside.modules.persistence.SearchFilter;

import com.cloopen.rest.sdk.CCPRestSmsSDK;
import com.springside.webwork.entity.Custom;
import com.springside.webwork.entity.Message;
import com.springside.webwork.entity.Template;
import com.springside.webwork.entity.Trip;
import com.springside.webwork.repository.MessageDao;
import com.springside.webwork.service.CommonService;

/**
 * 信息管理类.
 * 
 * @author ljj
 */
// Spring Service Bean的标识.
@Component
@Transactional
public class MessageService {

	private MessageDao messageDao;
	
	@Autowired
	private CommonService commonSve;
	
	private static List<Template> templateList = new ArrayList<Template>();
	
	public MessageService() {
		super();
		
		String[] template1 = {"您好", "起飞", "抵达，", "航班。票已出好，请准时登机，谢谢，嘉信商务400-880-3331"};
		String[] template2 = {"您好", "开)", "到)", "，票已出好，请提前到车站取票候车谢谢，嘉信商务400-880-3331"};
		String[] template3 = {"您好", "已更改为：", "起飞", "抵达，", "航班。票已出好，请准时登机，谢谢，嘉信商务400-880-3331"};
		String[] template4 = {"您好", "起飞", "抵达，", "航班。", "起飞", "抵达，", "航班。票已出好，请准时登机，谢谢，嘉信商务400-880-3331"};
		String[] template5 = {"您好", "起飞", "抵达，", "航班，", "舱。票已出好，请准时登机，谢谢，嘉信商务400-880-3331"};
		String[] template6 = {"您好", "已更改为：", "起飞", "抵达，", "航班，", "舱。票已出好，请准时登机，谢谢，嘉信商务400-880-3331"};
		String[] template7 = {"您好", "起飞", "抵达，", "航班。", "起飞", "抵达，", "航班，", "舱。票已出好，请准时登机，谢谢，嘉信商务400-880-3331"};
		String[] template8 = {"您好", "起飞", "抵达，", "航班。请准时登机，谢谢，嘉信商务400-880-3331"};
		String[] template9 = {"您好", "开)", "到)", "，请提前到车站取票候车谢谢，嘉信商务400-880-3331"};
		String[] template10 = {"您好", "已更改为：", "起飞", "抵达，", "航班。请准时登机，谢谢，嘉信商务400-880-3331"};
		String[] template11 = {"您好", "起飞", "抵达，", "航班。", "起飞", "抵达，", "航班。请准时登机，谢谢，嘉信商务400-880-3331"};
		String[] template12 = {"您好", "起飞", "抵达，", "航班，", "舱。请准时登机，谢谢，嘉信商务400-880-3331"};
		String[] template13 = {"您好", "已更改为：", "起飞", "抵达，", "航班，", "舱。请准时登机，谢谢，嘉信商务400-880-3331"};
		String[] template14 = {"您好", "起飞", "抵达，", "航班。", "起飞", "抵达，", "航班，", "舱。请准时登机，谢谢，嘉信商务400-880-3331"};
		
		Template tmp = new Template();
		tmp.setTemplateId("40304");
		tmp.setTemplateMsg(template1);
		templateList.add(tmp);
		tmp = new Template();
		tmp.setTemplateId("40306");
		tmp.setTemplateMsg(template2);
		templateList.add(tmp);
		tmp = new Template();
		tmp.setTemplateId("40307");
		tmp.setTemplateMsg(template3);
		templateList.add(tmp);
		tmp = new Template();
		tmp.setTemplateId("40308");
		tmp.setTemplateMsg(template4);
		templateList.add(tmp);
		tmp = new Template();
		tmp.setTemplateId("40309");
		tmp.setTemplateMsg(template5);
		templateList.add(tmp);
		tmp = new Template();
		tmp.setTemplateId("40310");
		tmp.setTemplateMsg(template6);
		templateList.add(tmp);
		tmp = new Template();
		tmp.setTemplateId("40311");
		tmp.setTemplateMsg(template7);
		templateList.add(tmp);
		tmp.setTemplateId("40607");
		tmp.setTemplateMsg(template8);
		templateList.add(tmp);
		tmp = new Template();
		tmp.setTemplateId("40608");
		tmp.setTemplateMsg(template9);
		templateList.add(tmp);
		tmp = new Template();
		tmp.setTemplateId("40609");
		tmp.setTemplateMsg(template10);
		templateList.add(tmp);
		tmp = new Template();
		tmp.setTemplateId("40610");
		tmp.setTemplateMsg(template11);
		templateList.add(tmp);
		tmp = new Template();
		tmp.setTemplateId("40896");
		tmp.setTemplateMsg(template12);
		templateList.add(tmp);
		tmp = new Template();
		tmp.setTemplateId("40897");
		tmp.setTemplateMsg(template13);
		templateList.add(tmp);
		tmp = new Template();
		tmp.setTemplateId("40898");
		tmp.setTemplateMsg(template14);
		templateList.add(tmp);
		
		Collections.reverse(templateList);
	}
	
	
	
	public Message getMessage(Long id) {
		return messageDao.findOne(id);
	}
	
	public void delMessage(Long id) {
		messageDao.delete(id);
	}
	
	public void saveMessage(Message message) {
		messageDao.save(message);
	}
	
	public long count(Map<String, Object> searchParams) {
		if (searchParams.size() > 0) {
			return messageDao.count(this.buildSpecificationOr(searchParams));
		} else {
			return 0;
		}
	}
	
	public List<Message> getCustomMessage(long customId, String message) {
		Map<String, Object> searchParams = new HashMap<String, Object>();
		searchParams.put("EQ_customId", String.valueOf(customId));
		searchParams.put("EQ_message", String.valueOf(message));
		if (searchParams.size() > 0) {
			Specification<Message> spec = this.buildSpecificationAnd(searchParams);
			return messageDao.findAll(spec);
		} else {
			return new ArrayList<Message>();
		}
	}
	
	public List<Message> query(Map<String, Object> searchParams, int pageNumber, int pageSize, Direction sortType) {
		if (searchParams.size() > 0) {
			PageRequest pageRequest = commonSve.buildPageRequest(pageNumber, pageSize, sortType);
			Specification<Message> spec = this.buildSpecificationOr(searchParams);
			
			Page<Message> page = messageDao.findAll(spec, pageRequest);
			return page.getContent();
		} else {
			return new ArrayList<Message>();
		}
	}

	/**
	 * 创建动态查询条件组合.
	 */
	private Specification<Message> buildSpecificationOr(Map<String, Object> searchParams) {
		Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
		Specification<Message> spec = CommonService.bySearchFilter(filters.values(), Message.class);
		return spec;
	}
	private Specification<Message> buildSpecificationAnd(Map<String, Object> searchParams) {
		Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
		Specification<Message> spec = DynamicSpecifications.bySearchFilter(filters.values(), Message.class);
		return spec;
	}
	
	/**
	 * 中国网建发短线
	 * @param custom
	 * @param trip
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public boolean sendMessage1(Custom custom, Trip trip) throws HttpException, IOException {
		HttpClient client = new HttpClient();
		PostMethod post = new PostMethod("http://utf8.sms.webchinese.cn");
		post.addRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=utf-8");//在头文件中设置转码
		NameValuePair[] data = { 
				new NameValuePair("Uid", "jackljj"),
				new NameValuePair("Key", "bcb916dc45f83988f8e0"),
				new NameValuePair("smsMob", custom.getTelephone()),
				new NameValuePair("smsText", "test")
		};
		post.setRequestBody(data);
	
		client.executeMethod(post);
		Header[] headers = post.getResponseHeaders();
		
		int statusCode = post.getStatusCode();
		System.out.println("statusCode:" + statusCode);
		for (Header h : headers) {
			System.out.println(h.toString());
		}
		String result = new String(post.getResponseBodyAsString().getBytes("utf-8")); 
		System.out.println(result);	//打印返回消息状态
	
		post.releaseConnection();
		
		return true;
	}
	
	/**
	 * 云通信发送短信
	 * @param custom
	 * @param trip
	 * @return
	 */
	public boolean sendMessage2(Custom custom, String message) {
		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH:mm");
		
		message = message.replace(" ", "").replace(".", "。").replace(",", "，").replace(":", "：")
				.replace("（", "(").replace("）", ")").replace("【", "[").replace("】", "]").trim();
		
		Template tmp = this.convertMessage(message);
		if (tmp == null) {
			return false;
		}
		
		HashMap<String, Object> result = null;

		//初始化SDK
		CCPRestSmsSDK restAPI = new CCPRestSmsSDK();
		
		//******************************注释*********************************************
		//*初始化服务器地址和端口                                                       *
		//*沙盒环境（用于应用开发调试）：restAPI.init("sandboxapp.cloopen.com", "8883");*
		//*生产环境（用户应用上线使用）：restAPI.init("app.cloopen.com", "8883");       *
		//*******************************************************************************
		restAPI.init("app.cloopen.com", "8883");
		
		//******************************注释*********************************************
		//*初始化主帐号和主帐号令牌,对应官网开发者主账号下的ACCOUNT SID和AUTH TOKEN     *
		//*ACOUNT SID和AUTH TOKEN在登陆官网后，在“应用-管理控制台”中查看开发者主账号获取*
		//*参数顺序：第一个参数是ACOUNT SID，第二个参数是AUTH TOKEN。                   *
		//*******************************************************************************
		restAPI.setAccount("aaf98f894ebe0e7e014ec5b9d3ed0965", "d4f4f6f1836b47d89aeb3cb14ec5a4cf");
		
		//******************************注释*********************************************
		//*初始化应用ID                                                                 *
		//*测试开发可使用“测试Demo”的APP ID，正式上线需要使用自己创建的应用的App ID     *
		//*应用ID的获取：登陆官网，在“应用-应用列表”，点击应用名称，看应用详情获取APP ID*
		//*******************************************************************************
		restAPI.setAppId("aaf98f894ebe0e7e014ec5bc06970975");
		
		//******************************注释****************************************************************
		//*调用发送模板短信的接口发送短信                                                                  *
		//*参数顺序说明：                                                                                  *
		//*第一个参数:是要发送的手机号码，可以用逗号分隔，一次最多支持100个手机号                          *
		//*第二个参数:是模板ID，在平台上创建的短信模板的ID值；测试的时候可以使用系统的默认模板，id为1。    *
		//*系统默认模板的内容为“【云通讯】您使用的是云通讯短信模板，您的验证码是{1}，请于{2}分钟内正确输入”*
		//*第三个参数是要替换的内容数组。																														       *
		//**************************************************************************************************
		
		//**************************************举例说明***********************************************************************
		//*假设您用测试Demo的APP ID，则需使用默认模板ID 1，发送手机号是13800000000，传入参数为6532和5，则调用方式为           *
		//*result = restAPI.sendTemplateSMS("13800000000","1" ,new String[]{"6532","5"});																		  *
		//*则13800000000手机号收到的短信内容是：【云通讯】您使用的是云通讯短信模板，您的验证码是6532，请于5分钟内正确输入     *
		//*********************************************************************************************************************
		result = restAPI.sendTemplateSMS(
			custom.getTelephone(),
			tmp.getTemplateId(),
//			new String[] {
//					"(" + custom.getCustomerName() + "[" + custom.getIdNumber() + "])" + trip.getTripSource() + "-" + trip.getTripTarget() + "[" + trip.getTargetAddress() + "]",
//					sdf.format(trip.getStartTime()),
//					sdf.format(trip.getEndTime()),
//					trip.getTripId()
//				}
			tmp.getTemplateMsg()
			);
		
		System.out.println("SDKTestGetSubAccounts result=" + result);
		if ("000000".equals(result.get("statusCode"))) {
			//正常返回输出data包体信息（map）
			HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
			Set<String> keySet = data.keySet();
			for (String key : keySet) {
				Object object = data.get(key);
				System.out.println(key + " = " + object);
			}
			
			List<Message> msgList = this.getCustomMessage(custom.getId(), message);
			if (msgList == null || msgList.size() < 1) {
				Message msg = new Message();
				msg.setCustomId(custom.getId());
				msg.setMessage(message);
				this.saveMessage(msg);
			}
			
			return true;
		} else {
			//异常返回输出错误码和错误信息
			System.out.println("错误码=" + result.get("statusCode") +" 错误信息=" + result.get("statusMsg"));
			return false;
		}
	}
	
	public String reviewMessage(Custom custom, Trip trip) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH:mm");
		
		String message = "您好(" + custom.getCustomerName();
		if (custom.getIdNumber() != null && !custom.getIdNumber().equals("")) {
			message += "[" + custom.getIdNumber()+ "]";
		}
		message += ")" + trip.getTripSource();
		if (trip.getSourceAddress() != null && !trip.getSourceAddress().equals("")) {
			message += "[" + trip.getSourceAddress() + "]";
		}
		message += "-" + trip.getTripTarget();
		if (trip.getTargetAddress() != null && !trip.getTargetAddress().equals("")) {
			message += "[" + trip.getTargetAddress() + "]";
		}
		message += sdf.format(trip.getStartTime())
				+ "起飞" + sdf.format(trip.getEndTime())
				+ "抵达，" + trip.getTripId()
				+ "航班。票已出好，请准时登机，谢谢，嘉信商务400-880-3331";
		return message;
	}
	
	private Template convertMessage(final String message) {
		for (Template tmp : templateList) {
			String msg = message;
			Boolean isMatch = false;
			for (String str : tmp.getTemplateMsg()) {
				if (message.indexOf(str) > -1) {
					isMatch = true;
					continue;
				} else {
					isMatch = false;
					break;
				}
			}
			if (isMatch) {
				Template ret = new Template();
				ret.setTemplateId(tmp.getTemplateId());
				int size = tmp.getTemplateMsg().length - 1;
				String[] strs = new String[size];
				try {
					for (int i = 0; i < size; i++) {
						String msgx = tmp.getTemplateMsg()[i];
						String msgy = tmp.getTemplateMsg()[i + 1];
						if (i == 0) {
							strs[i] = msg.substring(msgx.length(), msg.indexOf(msgy));
						} else {
							strs[i] = msg.substring(msg.indexOf(msgx) + msgx.length(), msg.indexOf(msgy));
						}
						msg = msg.substring(msg.indexOf(msgy));
					}
				} catch(Exception e) {
					continue;
				}
				ret.setTemplateMsg(strs);
				return ret;
			}
		}
		return null;
	}
	
//	public String saveSend(String message, Custom custom) throws ParseException {
//		
//		message = message.replace(" ", "").replace(".", "。").replace(",", "，").replace("：", ":")
//				.replace("（", "(").replace("）", ")").replace("【", "[").replace("】", "]");
//		String customName = message.substring(msg1.length(), message.indexOf(msg2));
//		message = message.substring(message.indexOf(msg2));
//		String customIdNum = message.substring(message.indexOf(msg2) + msg2.length(), message.indexOf(msg3));
//		message = message.substring(message.indexOf(msg3));
//		String tripSource = message.substring(message.indexOf(msg3) + msg3.length(), message.indexOf(msg4));
//		message = message.substring(message.indexOf(msg4));
//		String tripTarget = message.substring(message.indexOf(msg4) + msg4.length(), message.indexOf(msg5));
//		message = message.substring(message.indexOf(msg5));
//		String targetAddress = message.substring(message.indexOf(msg5) + msg5.length(), message.indexOf(msg6));
//		message = message.substring(message.indexOf(msg6));
//		String startTime = message.substring(message.indexOf(msg6) + msg6.length(), message.indexOf(msg7));
//		message = message.substring(message.indexOf(msg7));
//		String endTime = message.substring(message.indexOf(msg7) + msg7.length(), message.indexOf(msg8));
//		message = message.substring(message.indexOf(msg8));
//		String tripName = message.substring(message.indexOf(msg8) + msg8.length(), message.indexOf(msg9));
//		
//		if (!custom.getCustomerName().equals(customName) || !custom.getIdNumber().equals(customIdNum)) {
//			return "different";
//		}
//		
//		Trip trip = new Trip();
//		trip.setCustomId(custom.getId());
//		trip.setTripId(tripName);
////		trip.setTripName(tripName);
//		trip.setTripSource(tripSource);
//		trip.setTripTarget(tripTarget);
//		trip.setTargetAddress(targetAddress);
//		trip.setTripDate(new Date());
//		
//		Calendar calendar = Calendar.getInstance();
//		Date startDate = this.convertDate(startTime, calendar);
//		trip.setStartTime(startDate);
//		
//		calendar.setTime(startDate);
//		Date endDate = this.convertDate(endTime, calendar);
//		trip.setEndTime(endDate);
//		trip.setPrice(0);
//		tripSve.saveTrip(trip);
//		
//		this.sendMessage2(custom, trip);
//		
//		return "success";
//	}
	
//	private Date convertDate(String time, Calendar calendar) throws ParseException {
//		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月dd日HH:mm");
//		SimpleDateFormat sdf2 = new SimpleDateFormat("MM月dd日HH:mm");
//		SimpleDateFormat sdf3 = new SimpleDateFormat("dd日HH:mm");
//		SimpleDateFormat sdf4 = new SimpleDateFormat("HH:mm");
//		
//		Date date;
//		Calendar c = Calendar.getInstance();
//		if (time.indexOf("年") > -1) {
//			date = sdf1.parse(time);
//		} else if (time.indexOf("月") > -1) {
//			date = sdf2.parse(time);
//		} else if (time.indexOf("日") > -1) {
//			date = sdf3.parse(time);
//		} else {
//			date = sdf4.parse(time);
//		}
//		c.setTime(date);
//		if (time.indexOf("年") < 0) {
//			c.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
//		}
//		if (time.indexOf("月") < 0) {
//			c.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
//		}
//		if (time.indexOf("日") < 0) {
//			c.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
//		}
//		return c.getTime();
//	}
	
	@Autowired
	public void setMessageDao(MessageDao messageDao) {
		this.messageDao = messageDao;
	}
	
}
