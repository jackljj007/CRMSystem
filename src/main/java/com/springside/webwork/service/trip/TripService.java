/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.springside.webwork.service.trip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.persistence.SearchFilter;

import com.springside.webwork.entity.Custom;
import com.springside.webwork.entity.Trip;
import com.springside.webwork.repository.TripDao;
import com.springside.webwork.service.CommonService;
import com.springside.webwork.service.custom.CustomService;
import com.springside.webwork.service.message.MessageService;

/**
 * 行程管理类.
 * 
 * @author ljj
 */
// Spring Service Bean的标识.
@Component
@Transactional
public class TripService {

	private TripDao tripDao;
	
	@Autowired
	private CommonService commonSve;
	
	@Autowired
	private CustomService customSve;
	
	@Autowired
	private MessageService messageSve;
	
	
	
	public Trip getTrip(Long id) {
		return tripDao.findOne(id);
	}
	
	public void delTrip(Long id) {
		tripDao.delete(id);
	}
	
	public void saveTrip(Trip trip) {
		tripDao.save(trip);
	}
	
	public long count(Map<String, Object> searchParams) {
		if (searchParams.size() > 0) {
			return tripDao.count(this.buildSpecification(searchParams));
		} else {
			return 0;
		}
	}
	
	public List<Trip> getCustomTrip(long customId) {
		Map<String, Object> searchParams = new HashMap<String, Object>();
		searchParams.put("EQ_customId", String.valueOf(customId));
		if (searchParams.size() > 0) {
			Specification<Trip> spec = this.buildSpecification(searchParams);
			return tripDao.findAll(spec);
		} else {
			return new ArrayList<Trip>();
		}
	}
	
	/**
	 * 更新客户积分
	 * @param customId
	 */
	public void updateCustomScore(long customId) {
		List<Trip> tripList = this.getCustomTrip(customId);
		int price = 0;
		for (Trip trip : tripList) {
			price += trip.getPrice();
		}
		int score = price / 100;
		Custom custom = customSve.getCustom(customId);
		custom.setAvailableScore(score);
		custom.setTotalScore(score);
		customSve.saveCustom(custom);
	}

	public List<Trip> query(Map<String, Object> searchParams, int pageNumber, int pageSize, Direction sortType) {
		if (searchParams.size() > 0) {
			PageRequest pageRequest = commonSve.buildPageRequest(pageNumber, pageSize, sortType);
			Specification<Trip> spec = this.buildSpecification(searchParams);
			
			Page<Trip> page = tripDao.findAll(spec, pageRequest);
			return page.getContent();
		} else {
			return new ArrayList<Trip>();
		}
	}

	/**
	 * 创建动态查询条件组合.
	 */
	private Specification<Trip> buildSpecification(Map<String, Object> searchParams) {
		Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
		Specification<Trip> spec = CommonService.bySearchFilter(filters.values(), Trip.class);
		return spec;
	}
	
	public boolean sendMessage(long customId, String message) {
		Custom custom = customSve.getCustom(customId);
//		return messageSve.sendMessage1(custom, trip);
		return messageSve.sendMessage2(custom, message);
	}

//	public String saveSend(String message, long customId) throws ParseException {
//		Custom custom = customSve.getCustom(customId);
//		return messageSve.saveSend(message, custom);
//	}

	public String reviewMessage(long customId, long tripId) {
		Trip trip = this.getTrip(tripId);
		Custom custom = customSve.getCustom(customId);
		return messageSve.reviewMessage(custom, trip);
	}
	
	
	
	@Autowired
	public void setTripDao(TripDao tripDao) {
		this.tripDao = tripDao;
	}

}
