/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.springside.webwork.web.custom;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.springside.webwork.entity.Custom;
import com.springside.webwork.service.CommonService;
import com.springside.webwork.service.custom.CustomService;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 主界面.
 * 
 * @author ljj
 */
@Controller
@RequestMapping(value = "/custom")
public class CustomController {
	
	@Autowired
	private CustomService customSve;
	
	@Autowired
	private CommonService commonSve;

	@RequestMapping(method = RequestMethod.GET)
	public String main() {
		return "custom/custom";
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
	
	@RequestMapping(value = "/queryInfo")
	@ResponseBody
	public ResponseEntity<String> queryInfo(@RequestParam("searchContent") String searchContent) throws Exception {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		Set<String> ret = new HashSet<String>();

		searchContent = searchContent == null ? "" : searchContent;
		List<Custom> list = new ArrayList<Custom>();
		Map<String, Object> searchParams = new HashMap<String, Object>();
		searchParams.put("LIKE_companyName", searchContent);
		list = customSve.query(searchParams, 1, 5, Direction.DESC);
		for (Custom custom : list) {
			ret.add(custom.getCompanyName());
		}
		searchParams = new HashMap<String, Object>();
		searchParams.put("LIKE_customerName", searchContent);
		list = customSve.query(searchParams, 1, 5, Direction.DESC);
		for (Custom custom : list) {
			ret.add(custom.getCustomerName());
		}
		return new ResponseEntity<String>(JSONArray.fromObject(ret).toString(), responseHeaders, HttpStatus.CREATED);
	}
	
	@RequestMapping(value="getCustom")
	@ResponseBody
	public ResponseEntity<String> getCustom(@RequestParam("id")long id) throws Exception {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		Custom custom = customSve.getCustom(id);
		return new ResponseEntity<String>(JSONObject.fromObject(custom).toString(), responseHeaders, HttpStatus.CREATED);
	}
	
	@RequestMapping(value="delCustom")
	@ResponseBody
	public String delCustom(@RequestParam("id")long id) {
		try {
			customSve.delCustom(id);
		} catch (Exception e) {
			return "fail";
		}
		return "success";
	}
	
	@RequestMapping(value="update")
	@ResponseBody
	public String updateCustom(
			@RequestParam("id") long id,
			@RequestParam("companyName") String companyName,
			@RequestParam("customName") String customName,
			@RequestParam("idNumber") String idNumber,
			@RequestParam("telephone") String telephone,
			@RequestParam("address") String address,
			@RequestParam("membershipNum") String membershipNum,
			@RequestParam("membershipLevel") int membershipLevel) {
		try {
			Custom custom = new Custom();
			custom.setId(id);
			custom.setCompanyName(companyName);
			custom.setCustomerName(customName);
			custom.setIdNumber(idNumber);
			custom.setTelephone(telephone);
			custom.setAddress(address);
			custom.setMembershipNum(membershipNum);
			custom.setMembershipLevel(membershipLevel);
			custom.setIsEnable(true);
			customSve.saveCustom(custom);
		} catch (Exception e) {
			return "fail";
		}
		return "success";
	}
	
	@RequestMapping(value="add")
	@ResponseBody
	public String addCustom(
			@RequestParam("id") long id,
			@RequestParam("companyName") String companyName,
			@RequestParam("customName") String customName,
			@RequestParam("idNumber") String idNumber,
			@RequestParam("telephone") String telephone,
			@RequestParam("address") String address,
			@RequestParam("membershipNum") String membershipNum,
			@RequestParam("membershipLevel") int membershipLevel) {
		try {
			Map<String, Object> searchParams = new HashMap<String, Object>();
			searchParams.put("EQ_companyName", companyName);
			searchParams.put("EQ_customerName", customName);
			if (customSve.exist(searchParams)) {
				return "exist";
			};
			
			Custom custom = new Custom();
			custom.setCompanyName(companyName);
			custom.setCustomerName(customName);
			custom.setIdNumber(idNumber);
			custom.setTelephone(telephone);
			custom.setAddress(address);
			custom.setMembershipNum(membershipNum);
			custom.setMembershipLevel(membershipLevel);
			custom.setIsEnable(true);
			customSve.saveCustom(custom);
		} catch (Exception e) {
			return "fail";
		}
		return "success";
	}
	
	
	@RequestMapping(value="uploadFile")
	@ResponseBody
	public ResponseEntity<String> upLoadTxtFile(MultipartHttpServletRequest multipartRequest) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		String fileToUploadId = multipartRequest.getParameter("fileToUploadId");//前台上传控件ID

		if (null == fileToUploadId || "".equals(fileToUploadId)) {
			fileToUploadId = "fileToUpload";
		}
	
		String realFileName = "";
		String fileDir = multipartRequest.getParameter("fileDir");

		fileDir = fileDir == null ? "temp" : fileDir;
		
		HttpHeaders responseHeaders = new HttpHeaders();
		
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		Map<String, String> stateMap = new HashMap<String, String>();
		try {
			MultipartFile file = multipartRequest.getFile(fileToUploadId);	//根据前台的name名称得到上传的文件
			realFileName = file.getOriginalFilename().trim();	//相同名字
			realFileName = realFileName.replace(" ", "");	//去除空格
			String ext = realFileName.trim().substring(realFileName.lastIndexOf("."));
			String fn = realFileName.trim().substring(0, realFileName.lastIndexOf("."));
			if (file == null || file.getSize() == 0) {
				stateMap.put("STATE", "empty");
				return new ResponseEntity<String>(JSONObject.fromObject(stateMap).toString(), responseHeaders, HttpStatus.CREATED);
			}
			
			realFileName = fn + "_" + sdf.format(new Date()) + ext;
			//取得服务器真实路径
			String ctxPath = multipartRequest.getSession().getServletContext().getRealPath("/") + "\\uploadFile\\";	// 获取全路径  
			//创建文件
			File dirPath = new File(ctxPath);
			if (!dirPath.exists()) {
				dirPath.mkdir();
			}
			ctxPath += fileDir + "\\";
			dirPath = new File(ctxPath);
			if (!dirPath.exists()) {
				dirPath.mkdir();
			}
			
			File uploadFile = new File(ctxPath + realFileName);
			try {
				FileCopyUtils.copy(file.getBytes(), uploadFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String filePath = "//uploadFile//" + fileDir + "//" + realFileName;
			stateMap.put("FILEPATH", filePath);
			stateMap.put("STATE", "success");
			return new ResponseEntity<String>(JSONObject.fromObject(stateMap).toString(), responseHeaders, HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			stateMap.put("STATE", "error"); 
			return new ResponseEntity<String>(JSONObject.fromObject(stateMap).toString(), responseHeaders, HttpStatus.CREATED);
		}
	}
	
	@RequestMapping(value="exportExcel")
	@ResponseBody
	public String exportExcel(
			@RequestParam(required = false, value = "cmd") String cmdStr,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			File file = customSve.exportExcel();
			commonSve.exportFile(response, file, true);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping(value = "importExcel")
	@ResponseBody
	public ResponseEntity<String> saveBatchCustom(
			@RequestParam("filePath") String filePath,
			HttpServletRequest request) throws Exception {

		Map<String, String> stateMap = new HashMap<String, String>();
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");

		filePath = (filePath == null) ? "" : filePath;
		String ctxPath = request.getSession().getServletContext().getRealPath("/") + filePath; // 获取全路径
		String recordMsg = "";
		int successCount = 0;
		int errorCount = 0;
		try {
			if (!ctxPath.endsWith("xls")) {
				stateMap.put("STATE", "error");
				stateMap.put("errorMsg", "传输失败，请上传Excel 97-2003  的xls格式文件。");
				return new ResponseEntity<String>(JSONObject.fromObject(
						stateMap).toString(), responseHeaders,
						HttpStatus.CREATED);
			}
			
			File file = new File(ctxPath);
			if (!file.exists()) {
				stateMap.put("STATE", "error");
				stateMap.put("errorMsg", "传输失败，请重试。如果问题再次发生请联系管理员。");
				return new ResponseEntity<String>(JSONObject.fromObject(
						stateMap).toString(), responseHeaders,
						HttpStatus.CREATED);
			}

			WorkbookSettings workbooksetting = new WorkbookSettings();
			// 设置编码格式，不写默认为UTF-8，读取特殊符号如德语,会出现乱码，在此进行设置
			workbooksetting.setEncoding("ISO-8859-1");
			workbooksetting.setCellValidationDisabled(true);
			Workbook book = Workbook.getWorkbook(file, workbooksetting);
			// 使用第一张工作表
			Sheet sheet = book.getSheet(0);
			if (customSve.checkFileHeader(sheet)) {
				stateMap.put("STATE", "error");
				stateMap.put("errorMsg", "表头数据错误，请重新下载模板，正确填写数据后再上传。");
				return new ResponseEntity<String>(JSONObject.fromObject(
						stateMap).toString(), responseHeaders,
						HttpStatus.CREATED);
			}
			int rows = sheet.getRows(); // 总行数
			for (int i = 1; i <= rows - 1; i++) {
				int n = 0;
				String companyName = sheet.getCell(n++, i).getContents();// 列 行
				String customName = sheet.getCell(n++, i).getContents();// 列 行
				String idNumber = sheet.getCell(n++, i).getContents();// 列 行
				String telephone = sheet.getCell(n++, i).getContents();// 列 行
				String address = sheet.getCell(n++, i).getContents();// 列 行
				String membershipNum = sheet.getCell(n++, i).getContents();// 列 行
				String membershipLevel = sheet.getCell(n++, i).getContents();// 列 行
				String availableScore = sheet.getCell(n++, i).getContents();// 列 行
				String totalScore = sheet.getCell(n++, i).getContents();	// 列 行
				
				try {
					if (companyName == null || companyName.equals("")) {
						errorCount += 1;
						recordMsg += "错误记录：第" + i + "行公司名不能为空<br/>";
					} else if (customName == null || customName.equals("")) {
						errorCount += 1;
						recordMsg += "错误记录：第" + i + "行客户名不能为空<br/>";
					} else if (idNumber == null || idNumber.equals("")) {
						errorCount += 1;
						recordMsg += "错误记录：第" + i + "行证件号码不能为空<br/>";
					}
					Custom custom;
					Map<String, Object> searchParams = new HashMap<String, Object>();
					searchParams.put("EQ_companyName", companyName);
					searchParams.put("EQ_customerName", customName);
					searchParams.put("EQ_idNumber", idNumber);
					List<Custom> customList = customSve.query2(searchParams, 1, 10, Direction.ASC);
					if (customList != null && customList.size() > 0) {
						custom = customList.get(0);
					} else {
						custom = new Custom();
						custom.setCompanyName(companyName);
						custom.setCustomerName(customName);
						custom.setIdNumber(idNumber);
					}
					custom.setTelephone(telephone);
					custom.setAddress(address);
					custom.setMembershipNum(membershipNum);
					custom.setMembershipLevel(Integer.parseInt(commonSve.stringParse(membershipLevel)));
					custom.setAvailableScore(Integer.parseInt(commonSve.stringParse(availableScore)));
					custom.setTotalScore(Integer.parseInt(commonSve.stringParse(totalScore)));
					custom.setIsEnable(Boolean.TRUE);
					customSve.saveCustom(custom);
					successCount += 1;
				} catch (Exception e) {
					errorCount += 1;
					recordMsg += "错误记录：" + companyName + "-" + customName + "<br/>错误原因：" + e.getMessage() + "<br/>";
				}
			}
			book.close();
			stateMap.put("STATE", "success");
			stateMap.put("totalCount", rows - 1 + "");
			stateMap.put("successCount", successCount + "");
			stateMap.put("errorCount", errorCount + "");
			stateMap.put("recordMsg", recordMsg);

		} catch (Exception e) {
			stateMap.put("STATE", "error");
			stateMap.put("errorMsg", e.getMessage());
		}
		return new ResponseEntity<String>(JSONObject.fromObject(stateMap).toString(), responseHeaders, HttpStatus.CREATED);
	}
	
	@RequestMapping(value = "downlodFile")
	public String downlodFile(HttpServletResponse response) {
		try {
			File file = customSve.exportTemplate();
			commonSve.exportFile(response, file, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
