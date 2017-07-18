/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.springside.webwork.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 主界面.
 * 
 * @author ljj
 */
@Controller
@RequestMapping(value = "/desktop")
public class DesktopController {

	@RequestMapping(method = RequestMethod.GET)
	public String desktop() {
		return "desktop";
	}
	
}
