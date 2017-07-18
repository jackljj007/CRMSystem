/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.springside.webwork.web.account;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springside.webwork.entity.User;
import com.springside.webwork.service.account.AccountService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 管理员管理用户的Controller.
 * 
 * @author calvin
 */
@Controller
@RequestMapping(value = "/admin/user")
public class UserAdminController {

	@Autowired
	private AccountService accountService;

	@RequestMapping(method = RequestMethod.GET)
	public String list(Model model) {
		List<User> users = accountService.getAllUser();
		model.addAttribute("users", users);

		return "account/adminUserList";
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
		searchParams.put("LIKE_loginName", cmdStr);
		searchParams.put("LIKE_name", cmdStr);
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("total", accountService.count(searchParams));
		ret.put("rows", accountService.query(searchParams, page, rows, Direction.DESC));
		return new ResponseEntity<String>(JSONObject.fromObject(ret).toString(), responseHeaders, HttpStatus.CREATED);
	}

	@RequestMapping(value = "update/{id}", method = RequestMethod.GET)
	public String updateForm(@PathVariable("id") Long id, Model model) {
		model.addAttribute("user", accountService.getUser(id));
		return "account/adminUserForm";
	}

	@RequestMapping(value = "update", method = RequestMethod.POST)
	public String update(@Valid @ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
		accountService.updateUser(user);
		redirectAttributes.addFlashAttribute("message", "更新用户" + user.getLoginName() + "成功");
		return "redirect:/admin/user";
	}

	@RequestMapping(value = "delete/{id}")
	public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
		User user = accountService.getUser(id);
		accountService.deleteUser(id);
		redirectAttributes.addFlashAttribute("message", "删除用户" + user.getLoginName() + "成功");
		return "redirect:/admin/user";
	}

	/**
	 * 所有RequestMapping方法调用前的Model准备方法, 实现Struts2 Preparable二次部分绑定的效果,先根据form的id从数据库查出User对象,再把Form提交的内容绑定到该对象上。
	 * 因为仅update()方法的form中有id属性，因此仅在update时实际执行.
	 */
	@ModelAttribute
	public void getUser(@RequestParam(value = "id", defaultValue = "-1") Long id, Model model) {
		if (id != -1) {
			model.addAttribute("user", accountService.getUser(id));
		}
	}
	
	@RequestMapping(value="getUser")
	@ResponseBody
	public ResponseEntity<String> getUser(@RequestParam("id")long id) throws Exception {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		User user = accountService.getUser(id);
		return new ResponseEntity<String>(JSONObject.fromObject(user).toString(), responseHeaders, HttpStatus.CREATED);
	}
	
	@RequestMapping(value="delUser")
	@ResponseBody
	public String delUser(@RequestParam("id")long id) {
		try {
			accountService.deleteUser(id);
		} catch (Exception e) {
			return "fail";
		}
		return "success";
	}
	
	@RequestMapping(value="updateUser")
	@ResponseBody
	public String updateUser(
			@RequestParam("id") long id,
			@RequestParam("loginName") String loginName,
			@RequestParam("name") String name,
			@RequestParam("plainPassword") String plainPassword,
			@RequestParam("confirmPassword") String confirmPassword,
			@RequestParam("salt") String salt,
			@RequestParam("roles") String roles,
			@RequestParam("registerDate") String registerDate) {
		try {
			User user = new User();
			user.setId(id);
			user.setLoginName(loginName);
			user.setName(name);
			user.setPlainPassword(plainPassword);
			user.setPassword(confirmPassword);
			user.setSalt(salt);
			user.setRoles(roles);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = sdf.parse(registerDate);
			user.setRegisterDate(date);
			accountService.updateUser(user);
		} catch (Exception e) {
			return "fail";
		}
		return "success";
	}
	
	@RequestMapping(value="addUser")
	@ResponseBody
	public String addUser(
			@RequestParam("id") long id,
			@RequestParam("loginName") String loginName,
			@RequestParam("name") String name,
			@RequestParam("plainPassword") String plainPassword,
			@RequestParam("confirmPassword") String confirmPassword,
			@RequestParam("salt") String salt,
			@RequestParam("roles") String roles,
			@RequestParam("registerDate") String registerDate) {
		try {
			Map<String, Object> searchParams = new HashMap<String, Object>();
			searchParams.put("EQ_loginName", loginName);
			searchParams.put("EQ_name", name);
			if (accountService.exist(searchParams)) {
				return "exist";
			};
			
			User user = new User();
			user.setLoginName(loginName);
			user.setName(name);
			user.setPlainPassword(plainPassword);
			user.setPassword(confirmPassword);
			user.setRoles(roles);
			user.setRegisterDate(new Date());
			accountService.registerUser(user);
		} catch (Exception e) {
			return "fail";
		}
		return "success";
	}
	
	@RequestMapping(value = "/queryInfo")
	@ResponseBody
	public ResponseEntity<String> queryInfo(@RequestParam("searchContent") String searchContent) throws Exception {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		Set<String> ret = new HashSet<String>();

		searchContent = searchContent == null ? "" : searchContent;
		List<User> list = new ArrayList<User>();
		Map<String, Object> searchParams = new HashMap<String, Object>();
		searchParams.put("LIKE_loginName", searchContent);
		list = accountService.query(searchParams, 1, 5, Direction.DESC);
		for (User user : list) {
			ret.add(user.getLoginName());
		}
		searchParams = new HashMap<String, Object>();
		searchParams.put("LIKE_name", searchContent);
		list = accountService.query(searchParams, 1, 5, Direction.DESC);
		for (User user : list) {
			ret.add(user.getName());
		}
		return new ResponseEntity<String>(JSONArray.fromObject(ret).toString(), responseHeaders, HttpStatus.CREATED);
	}
	
}
