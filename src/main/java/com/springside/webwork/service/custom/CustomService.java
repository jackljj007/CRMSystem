/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.springside.webwork.service.custom;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.persistence.DynamicSpecifications;
import org.springside.modules.persistence.SearchFilter;

import com.springside.webwork.entity.Custom;
import com.springside.webwork.entity.Custom2;
import com.springside.webwork.repository.CustomDao;
import com.springside.webwork.repository.CustomDao2;
import com.springside.webwork.service.CommonService;

import jxl.CellType;
import jxl.Sheet;

/**
 * 客户管理类.
 * 
 * @author ljj
 */
// Spring Service Bean的标识.
@Component
@Transactional
public class CustomService {

	private CustomDao customDao;
	private CustomDao2 customDao2;
	
	@Autowired
	private CommonService commonSve;
	
	public Custom getCustom(Long id) {
		return customDao.findOne(id);
	}
	
	public void delCustom(Long id) {
		customDao.delete(id);
	}
	
	public void saveCustom(Custom custom) {
		customDao.save(custom);
		Custom2 custom2 = new Custom2();
		custom2.setId(custom.getId());
		custom2.setCompanyName(custom.getCompanyName());
		custom2.setCustomerName(custom.getCustomerName());
		custom2.setIdNumber(custom.getIdNumber());
		custom2.setTelephone(custom.getTelephone());
		custom2.setAddress(custom.getAddress());
		custom2.setMembershipNum(custom.getMembershipNum());
		custom2.setMembershipLevel(custom.getMembershipLevel());
		custom2.setAvailableScore(custom.getAvailableScore());
		custom2.setTotalScore(custom.getTotalScore());
		custom2.setIsEnable(Boolean.FALSE);
		customDao2.save(custom2);
	}
	
	public long count(Map<String, Object> searchParams) {
		if (searchParams.size() > 0) {
			return customDao.count(this.buildSpecificationOr(searchParams));
		} else {
			return 0;
		}
	}

	public List<Custom> query(Map<String, Object> searchParams, int pageNumber, int pageSize, Direction sortType) {
		if (searchParams.size() > 0) {
			PageRequest pageRequest = commonSve.buildPageRequest(pageNumber, pageSize, sortType);
			Specification<Custom> spec = this.buildSpecificationOr(searchParams);
			
			Page<Custom> page = customDao.findAll(spec, pageRequest);
			return page.getContent();
		} else {
			return new ArrayList<Custom>();
		}
	}
	
	public List<Custom> query2(Map<String, Object> searchParams, int pageNumber, int pageSize, Direction sortType) {
		if (searchParams.size() > 0) {
			PageRequest pageRequest = commonSve.buildPageRequest(pageNumber, pageSize, sortType);
			Specification<Custom> spec = this.buildSpecificationAnd(searchParams);
			
			Page<Custom> page = customDao.findAll(spec, pageRequest);
			return page.getContent();
		} else {
			return new ArrayList<Custom>();
		}
	}
	
	public Boolean exist(Map<String, Object> searchParams) {
		if (searchParams.size() > 0) {
			Specification<Custom> spec = this.buildSpecificationAnd(searchParams);
			
			List<Custom> list = customDao.findAll(spec);
			if (list.size() > 0) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 创建动态查询条件组合.
	 */
	private Specification<Custom> buildSpecificationOr(Map<String, Object> searchParams) {
		Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
		Specification<Custom> spec = CommonService.bySearchFilter(filters.values(), Custom.class);
		return spec;
	}
	private Specification<Custom> buildSpecificationAnd(Map<String, Object> searchParams) {
		Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
		Specification<Custom> spec = DynamicSpecifications.bySearchFilter(filters.values(), Custom.class);
		return spec;
	}
	
	private String[] BATCH_HEAD = { "公司名称", "客户姓名", "证件号码", "联系方式", "联系地址", "会员卡号", "会员级别", "可用积分", "累计积分" };
	
	public File exportExcel() throws SQLException {
		String fileFolder = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String fileName = sdf.format(Calendar.getInstance().getTime()) + "客户信息.xls";
		List<Object[]> list = new ArrayList<Object[]>();

		List<Custom> customs = (List<Custom>) customDao.findAll();
		for (Custom custom : customs) {
			Object[] tor = new Object[BATCH_HEAD.length];
			tor[0] = custom.getCompanyName() == null ? "" : custom.getCompanyName();
			tor[1] = custom.getCustomerName() == null ? "" : custom.getCustomerName();
			tor[2] = custom.getIdNumber() == null ? "" : custom.getIdNumber();
			tor[3] = custom.getTelephone() == null ? "" : custom.getTelephone();
			tor[4] = custom.getAddress() == null ? "" : custom.getAddress();
			tor[5] = custom.getMembershipNum() == null ? "" : custom.getMembershipNum();
			tor[6] = custom.getMembershipLevel();
			tor[7] = custom.getAvailableScore();
			tor[8] = custom.getTotalScore();
			list.add(tor);
		}
		CellType[] type = new CellType[BATCH_HEAD.length];
		for (int i = 0; i < BATCH_HEAD.length; i++) {
			type[i] = CellType.LABEL;
		}
		type[6] = CellType.NUMBER;
		type[7] = CellType.NUMBER;
		type[8] = CellType.NUMBER;
		
		File file = commonSve.fileCreate(fileFolder, fileName);
		try {
			FileOutputStream os = new FileOutputStream(file);	//创建输出流
			//生成xls文件(保存在服务器机上)
			commonSve.writeExcel(os, BATCH_HEAD, type, list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}
	
	/**
	 * 检查导出文件头表名
	 * @param sheet
	 * @return
	 */
	public boolean checkFileHeader(Sheet sheet) {
		String tmpStr;
		for (int i = 0; i < BATCH_HEAD.length; i++) {
			tmpStr = sheet.getCell(i, 0).getContents().replace(" ", "").trim();
			if (!(tmpStr.toUpperCase().equals(BATCH_HEAD[i].toUpperCase()))) {
				return true;
			}
		}
		return false;
	}
	
	public File exportTemplate() {
		String fileFolder = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		String fileName = "客户信息Template.xls";
		List<Object[]> list = new ArrayList<Object[]>();
		File file = commonSve.fileCreate(fileFolder, fileName);
		CellType[] type = new CellType[BATCH_HEAD.length];
		for (int i = 0; i < BATCH_HEAD.length; i++) {
			type[i] = CellType.LABEL;
		}
		try {
			commonSve.writeExcel(new FileOutputStream(file), BATCH_HEAD, type, list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	@Autowired
	public void setCustomDao(CustomDao customDao) {
		this.customDao = customDao;
	}
	
	@Autowired
	public void setCustomDao2(CustomDao2 customDao2) {
		this.customDao2 = customDao2;
	}
	
}
