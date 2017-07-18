/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.springside.webwork.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.springside.webwork.service.custom.CustomService;

import net.sf.json.JSONObject;

/**
 * 主界面.
 * 
 * @author ljj
 */
@Controller
@RequestMapping(value = "/main")
public class MainController {
	
	@Autowired
	private CustomService customSve;

	@RequestMapping(method = RequestMethod.GET)
	public String main() {
		return "main";
	}
	
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
		searchParams.put("LIKE_companyName", cmdStr);
		searchParams.put("LIKE_customerName", cmdStr);
		searchParams.put("EQ_telephone", cmdStr);
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("total", customSve.count(searchParams));
		ret.put("rows", customSve.query(searchParams, page, rows, Direction.DESC));
		return new ResponseEntity<String>(JSONObject.fromObject(ret).toString(), responseHeaders, HttpStatus.CREATED);
	}
	
	@RequestMapping(value = "/list2")
	@ResponseBody
	public ResponseEntity<String> getList2(
			@RequestParam(value = "page") int page,
			@RequestParam(value = "rows") int rows,
			@RequestParam(required = false, value = "cmd") String cmdStr)
					throws Exception {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		
		Map<String, Object> searchParams = new HashMap<String, Object>();
		searchParams.put("EQ_id", cmdStr);
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("total", customSve.count(searchParams));
		ret.put("rows", customSve.query(searchParams, page, rows, Direction.DESC));
		return new ResponseEntity<String>(JSONObject.fromObject(ret).toString(), responseHeaders, HttpStatus.CREATED);
	}
	
}
