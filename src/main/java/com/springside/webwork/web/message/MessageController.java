/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.springside.webwork.web.message;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.springside.webwork.entity.Message;
import com.springside.webwork.service.message.MessageService;

import net.sf.json.JSONObject;

/**
 * 主界面.
 * 
 * @author ljj
 */
@Controller
@RequestMapping(value = "/message")
public class MessageController {
	
	@Autowired
	private MessageService messageSve;

	
	
	@RequestMapping(value = "/list")
	@ResponseBody
	public ResponseEntity<String> getList(
			@RequestParam(value = "page") int page,
			@RequestParam(value = "rows") int rows,
			@RequestParam(required = false, value = "cmd") String cmdStr)
					throws Exception {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		
		Map<String, Object> searchParams = new HashMap<String, Object>();
		searchParams.put("EQ_customId", cmdStr);
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("total", messageSve.count(searchParams));
		ret.put("rows", messageSve.query(searchParams, page, rows, Direction.DESC));
		return new ResponseEntity<String>(JSONObject.fromObject(ret).toString(), responseHeaders, HttpStatus.CREATED);
	}
	
	@RequestMapping(value="getMessage")
	@ResponseBody
	public ResponseEntity<String> getMessage(@RequestParam("id")long id) throws Exception {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		Message message = messageSve.getMessage(id);
		return new ResponseEntity<String>(JSONObject.fromObject(message).toString(), responseHeaders, HttpStatus.CREATED);
	}
	
}
