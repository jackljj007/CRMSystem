/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.springside.webwork.web.trip;

import java.text.SimpleDateFormat;
import java.util.Date;
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

import com.springside.webwork.entity.Trip;
import com.springside.webwork.service.trip.TripService;

import net.sf.json.JSONObject;

/**
 * 主界面.
 * 
 * @author ljj
 */
@Controller
@RequestMapping(value = "/trip")
public class TripController {
	
	@Autowired
	private TripService tripSve;

	
	
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
		ret.put("total", tripSve.count(searchParams));
		ret.put("rows", tripSve.query(searchParams, page, rows, Direction.DESC));
		return new ResponseEntity<String>(JSONObject.fromObject(ret).toString(), responseHeaders, HttpStatus.CREATED);
	}
	
	@RequestMapping(value="getTrip")
	@ResponseBody
	public ResponseEntity<String> getTrip(@RequestParam("id")long id) throws Exception {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		Trip trip = tripSve.getTrip(id);
		return new ResponseEntity<String>(JSONObject.fromObject(trip).toString(), responseHeaders, HttpStatus.CREATED);
	}
	
	@RequestMapping(value="delTrip")
	@ResponseBody
	public String delTrip(@RequestParam("id")long id) {
		try {
			tripSve.delTrip(id);
			
			Trip trip = tripSve.getTrip(id);
			long customId = trip.getCustomId();
			tripSve.updateCustomScore(customId);
		} catch (Exception e) {
			return "fail";
		}
		return "success";
	}
	
	@RequestMapping(value="update")
	@ResponseBody
	public String updateTrip(
			@RequestParam("id") long id,
			@RequestParam("customId") long customId,
			@RequestParam("tripId") String tripId,
			@RequestParam("price") float price,
			@RequestParam("tripSource") String tripSource,
			@RequestParam("sourceAddress") String sourceAddress,
			@RequestParam("tripTarget") String tripTarget,
			@RequestParam("targetAddress") String targetAddress,
			@RequestParam("startTime") String startTime,
			@RequestParam("endTime") String endTime) {
		try {
			Trip trip = new Trip();
			trip.setId(id);
			trip.setCustomId(customId);
			trip.setTripId(tripId);
			trip.setTripSource(tripSource);
			trip.setSourceAddress(sourceAddress);
			trip.setTripTarget(tripTarget);
			trip.setTargetAddress(targetAddress);
			trip.setTripDate(new Date());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date startDate = sdf.parse(startTime);
			trip.setStartTime(startDate);
			Date endDate = sdf.parse(endTime);
			trip.setEndTime(endDate);
			trip.setPrice(price);
			tripSve.saveTrip(trip);
			
			tripSve.updateCustomScore(customId);
		} catch (Exception e) {
			return "fail";
		}
		return "success";
	}
	
	@RequestMapping(value="add")
	@ResponseBody
	public String addTrip(
			@RequestParam("id") long id,
			@RequestParam("customId") long customId,
			@RequestParam("tripId") String tripId,
			@RequestParam("price") float price,
			@RequestParam("tripSource") String tripSource,
			@RequestParam("sourceAddress") String sourceAddress,
			@RequestParam("tripTarget") String tripTarget,
			@RequestParam("targetAddress") String targetAddress,
			@RequestParam("startTime") String startTime,
			@RequestParam("endTime") String endTime) {
		try {
			Trip trip = new Trip();
			trip.setCustomId(customId);
			trip.setTripId(tripId);
			trip.setTripSource(tripSource);
			trip.setSourceAddress(sourceAddress);
			trip.setTripTarget(tripTarget);
			trip.setTargetAddress(targetAddress);
			trip.setTripDate(new Date());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date startDate = sdf.parse(startTime);
			trip.setStartTime(startDate);
			Date endDate = sdf.parse(endTime);
			trip.setEndTime(endDate);
			trip.setPrice(price);
			tripSve.saveTrip(trip);
			
			tripSve.updateCustomScore(customId);
		} catch (Exception e) {
			return "fail";
		}
		return "success";
	}
	
	@RequestMapping(value="sendMessage")
	@ResponseBody
	public String sendMessage(
			@RequestParam("customId") long customId,
			@RequestParam("message") String message) {
		try {
			if (!tripSve.sendMessage(customId, message)) {
				return "fail";
			}
		} catch (Exception e) {
			return "fail";
		}
		return "success";
	}
	
//	@RequestMapping(value="saveSend")
//	@ResponseBody
//	public String saveSend(
//			@RequestParam("message") String message,
//			@RequestParam("customId") long customId) {
//		String ret = "success";
//		try {
//			ret = tripSve.saveSend(message, customId);
//		} catch (Exception e) {
//			return "fail";
//		}
//		return ret;
//	}
	
	@RequestMapping(value="reviewMessage")
	@ResponseBody
	public String reviewMessage(
			@RequestParam("customId") long customId,
			@RequestParam("tripId") long tripId) {
		return tripSve.reviewMessage(customId, tripId);
	}
	
}
