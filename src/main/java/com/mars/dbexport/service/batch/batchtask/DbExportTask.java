/*
 * $Id: BatchShutDownManager.java, 2011-11-11 ����12:28:31 Yao Exp $
 * 
 * Copyright (c) 2011 Alcatel-Lucent Shanghai Bell Co., Ltd 
 * All rights reserved.
 * 
 * This software is copyrighted and owned by ASB or the copyright holder
 * specified, unless otherwise noted, and may not be reproduced or distributed
 * in whole or in part in any form or medium without express written permission.
 */
package com.mars.dbexport.service.batch.batchtask;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import com.mars.dbexport.AppContext;
import com.mars.dbexport.bo.NetworkElement;
import com.mars.dbexport.service.CliInfoReader;
import com.mars.dbexport.service.DbConvertor;
import com.mars.dbexport.service.DbBackup;
import com.mars.dbexport.service.batch.BatchManager;
import com.mars.dbexport.service.batch.BatchResult;
import com.mars.dbexport.service.impl.CliInfoReaderImpl;
import com.mars.dbexport.service.impl.DbConvertorImpl;
import com.mars.dbexport.service.impl.DbBackupImpl;
import com.mars.dbexport.utils.FileUtils;
import com.mars.dbexport.utils.GenericUtils;

/**
 * <p>
 * Title: BatchShutDownManager
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author Yao Liqiang
 * @created 2011-11-11 ����12:28:31
 * @modified [who date description]
 * @check [who date description]
 */
public class DbExportTask extends BatchManager {
	/**
	 * @param iplist
	 */
	public DbExportTask(Set<String> iplist) {
		super(iplist);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.alu.xdslpst.task.batch.BatchManager#runTask(java.lang.String)
	 */
	@Override
	protected BatchResult runTask(String ip) {
		NetworkElement ne = AppContext.getNeList().get(ip);
		BatchResult result = new BatchResult();
		result.setIpAddr(ip);
		if (ne == null)
			return result;
		File db = new File(ne.getDbPath());
		if (!db.exists()) {
			DbBackup dbback = new DbBackupImpl();
			if (!dbback.exportDb(ne)) {
				result.setSucceed(false);
				logger.error(ip + " " + "db backup failed");
				return result;
			}
		}
		DbConvertor convertor = new DbConvertorImpl(ne);
		List<String> commands = convertor.genrateCLICommands();
		if (!AppContext.getAppParamters().isSkipcli()) {
			commands.add("");
			CliInfoReader cliReader = new CliInfoReaderImpl();
			List<String> cliInfos = cliReader.readCliInfo(ne);
			commands.addAll(cliInfos);
		}
		save2file(ip, commands);
		return result;
	}

	private void save2file(String neIp, List<String> cliCommands) {
		FileUtils.createIpFolder(neIp);

		File dir = new File(AppContext.getResourceFactory().dataRoot + neIp);

		Calendar calendar = Calendar.getInstance();
		StringBuilder sb = new StringBuilder();
		sb.append(dir.getAbsolutePath());
		sb.append("/");
		sb.append("CMD");
		sb.append(GenericUtils.completeString(calendar.get(Calendar.YEAR) + "",
				4, '0', false));
		sb.append(GenericUtils.completeString(
				calendar.get(Calendar.MONTH) + "", 2, '0', false));
		sb.append(GenericUtils.completeString(
				calendar.get(Calendar.DAY_OF_MONTH) + "", 2, '0', false));
		sb.append(GenericUtils.completeString(
				calendar.get(Calendar.HOUR_OF_DAY) + "", 2, '0', false));
		sb.append(GenericUtils.completeString(calendar.get(Calendar.MINUTE)
				+ "", 2, '0', false));
		sb.append(GenericUtils.completeString(calendar.get(Calendar.SECOND)
				+ "", 2, '0', false));
		sb.append(".txt");

		File file = new File(sb.toString());
		FileOutputStream fout = null;
		BufferedWriter writer = null;
		try {
			fout = new FileOutputStream(file);
			writer = new BufferedWriter(new OutputStreamWriter(fout));
			for (String cmd : cliCommands) {
				writer.write(cmd + "\r\n");
			}
			System.out.println("Convertion complete : "
					+ file.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				writer.close();
				fout.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
