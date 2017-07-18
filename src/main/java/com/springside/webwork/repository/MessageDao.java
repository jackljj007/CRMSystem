/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.springside.webwork.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.springside.webwork.entity.Message;

public interface MessageDao extends PagingAndSortingRepository<Message, Long>, JpaSpecificationExecutor<Message> {
	
}
