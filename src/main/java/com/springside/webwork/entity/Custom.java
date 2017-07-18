/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.springside.webwork.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

//JPA标识
@Entity
@Table(name = "t_custom")
public class Custom extends IdEntity {

	private String companyName;
	private String customerName;
	private String idNumber;
	private String telephone;
	private String address;
	private String membershipNum;
	private int membershipLevel;
	private int availableScore;
	private int totalScore;
	private Boolean isEnable;
	
	

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMembershipNum() {
		return membershipNum;
	}

	public void setMembershipNum(String membershipNum) {
		this.membershipNum = membershipNum;
	}

	public int getMembershipLevel() {
		return membershipLevel;
	}

	public void setMembershipLevel(int membershipLevel) {
		this.membershipLevel = membershipLevel;
	}

	public int getAvailableScore() {
		return availableScore;
	}

	public void setAvailableScore(int availableScore) {
		this.availableScore = availableScore;
	}

	public int getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}
	
	public Boolean getIsEnable() {
		return isEnable;
	}
	
	public void setIsEnable(Boolean isEnable) {
		this.isEnable = isEnable;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
