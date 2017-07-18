/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.springside.webwork.service.account;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.persistence.DynamicSpecifications;
import org.springside.modules.persistence.SearchFilter;
import org.springside.modules.security.utils.Digests;
import org.springside.modules.utils.Clock;
import org.springside.modules.utils.Encodes;

import com.springside.webwork.entity.User;
import com.springside.webwork.repository.TaskDao;
import com.springside.webwork.repository.UserDao;
import com.springside.webwork.service.CommonService;
import com.springside.webwork.service.ServiceException;
import com.springside.webwork.service.account.ShiroDbRealm.ShiroUser;

/**
 * 用户管理类.
 * 
 * @author calvin
 */
// Spring Service Bean的标识.
@Component
@Transactional
public class AccountService {

	public static final String HASH_ALGORITHM = "SHA-1";
	public static final int HASH_INTERATIONS = 1024;
	private static final int SALT_SIZE = 8;

	private static Logger logger = LoggerFactory.getLogger(AccountService.class);

	private UserDao userDao;
	private TaskDao taskDao;
	private Clock clock = Clock.DEFAULT;
	
	@Autowired
	private CommonService commonSve;

	public List<User> getAllUser() {
		return (List<User>) userDao.findAll();
	}

	public User getUser(Long id) {
		return userDao.findOne(id);
	}

	public User findUserByLoginName(String loginName) {
		return userDao.findByLoginName(loginName);
	}

	public void registerUser(User user) {
		entryptPassword(user);
		user.setRegisterDate(clock.getCurrentDate());

		userDao.save(user);
	}
	
	public Boolean exist(Map<String, Object> searchParams) {
		if (searchParams.size() > 0) {
			Specification<User> spec = this.buildSpecificationAnd(searchParams);
			
			Page<User> page = userDao.findAll(spec, new PageRequest(1, 10));
			if (page.getContent().size() > 0) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public void updateUser(User user) {
		if (StringUtils.isNotBlank(user.getPlainPassword())) {
			entryptPassword(user);
		}
		userDao.save(user);
	}

	public void deleteUser(Long id) {
		if (isSupervisor(id)) {
			logger.warn("操作员{}尝试删除超级管理员用户", getCurrentUserName());
			throw new ServiceException("不能删除超级管理员用户");
		}
		userDao.delete(id);
		taskDao.deleteByUserId(id);

	}

	/**
	 * 判断是否超级管理员.
	 */
	private boolean isSupervisor(Long id) {
		return id == 1;
	}

	/**
	 * 取出Shiro中的当前用户LoginName.
	 */
	private String getCurrentUserName() {
		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		return user.loginName;
	}

	/**
	 * 设定安全的密码，生成随机的salt并经过1024次 sha-1 hash
	 */
	private void entryptPassword(User user) {
		byte[] salt = Digests.generateSalt(SALT_SIZE);
		user.setSalt(Encodes.encodeHex(salt));

		byte[] hashPassword = Digests.sha1(user.getPlainPassword().getBytes(), salt, HASH_INTERATIONS);
		user.setPassword(Encodes.encodeHex(hashPassword));
	}
	
	public long count(Map<String, Object> searchParams) {
		if (searchParams.size() > 0) {
			return userDao.count(this.buildSpecificationOr(searchParams));
		} else {
			return 0;
		}
	}

	public List<User> query(Map<String, Object> searchParams, int pageNumber, int pageSize, Direction sortType) {
		if (searchParams.size() > 0) {
			PageRequest pageRequest = commonSve.buildPageRequest(pageNumber, pageSize, sortType);
			Specification<User> spec = this.buildSpecificationOr(searchParams);
			
			Page<User> page = userDao.findAll(spec, pageRequest);
			return page.getContent();
		} else {
			return new ArrayList<User>();
		}
	}
	
	/**
	 * 创建动态查询条件组合.
	 */
	private Specification<User> buildSpecificationOr(Map<String, Object> searchParams) {
		Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
		Specification<User> spec = CommonService.bySearchFilter(filters.values(), User.class);
		return spec;
	}
	private Specification<User> buildSpecificationAnd(Map<String, Object> searchParams) {
		Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
		Specification<User> spec = DynamicSpecifications.bySearchFilter(filters.values(), User.class);
		return spec;
	}

	@Autowired
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	@Autowired
	public void setTaskDao(TaskDao taskDao) {
		this.taskDao = taskDao;
	}

	public void setClock(Clock clock) {
		this.clock = clock;
	}
}
