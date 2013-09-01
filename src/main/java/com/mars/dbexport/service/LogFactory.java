/*
 * $Id: LogFactory.java, 2011-12-8 ����1:20:13 Yao Exp $
 * 
 * Copyright (c) 2011 Alcatel-Lucent Shanghai Bell Co., Ltd 
 * All rights reserved.
 * 
 * This software is copyrighted and owned by ASB or the copyright holder
 * specified, unless otherwise noted, and may not be reproduced or distributed
 * in whole or in part in any form or medium without express written permission.
 */
package com.mars.dbexport.service;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

import com.mars.dbexport.AppContext;

/**
 * <p>
 * Title: LogFactory
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author Yao Liqiang
 * @created 2011-12-8 ����1:20:13
 * @modified [who date description]
 * @check [who date description]
 */
public class LogFactory {
	public static final String LOG_RESULT = "result";
	public static final String LOG_ERROR = "error";
	private final String configFile = "logback.xml";
	private LoggerContext logContext;

	public LogFactory() {
		try {
			URL url = AppContext.getResourceFactory().getConfigFile(configFile)
					.toURI().toURL();
			logContext = (LoggerContext) LoggerFactory.getILoggerFactory();
			try {
				JoranConfigurator configurator = new JoranConfigurator();
				configurator.setContext(logContext);
				logContext.reset();
				configurator.doConfigure(url);
			} catch (JoranException je) {
				StatusPrinter.print(logContext.getStatusManager());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * �õ�logger
	 * 
	 * @param logModule
	 *            �ο�ModuleConstants
	 * @return
	 */
	public Logger getLogger(String logModule) {
		return logContext.getLogger(logModule);
	}

	/**
	 * ֹͣ��־
	 */
	public void destroy() {
		logContext.stop();
	}

}
