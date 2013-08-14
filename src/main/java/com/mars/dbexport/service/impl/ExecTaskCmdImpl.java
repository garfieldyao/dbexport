/**
 * 
 */
package com.mars.dbexport.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import com.mars.dbexport.AppContext;
import com.mars.dbexport.service.DbConvertor;
import com.mars.dbexport.service.ExecTaskCmd;

/**
 * @author Yao
 * 
 */
public class ExecTaskCmdImpl implements ExecTaskCmd {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mars.dbexporter.service.ExecTaskCmd#execTask()
	 */
	@Override
	public void execTask() {
		// TODO Auto-generated method stub
		if (AppContext.getAppParamters().isGuiMode())
			return;
		DbConvertor convetor = new DbConvertorImpl();
		if (!AppContext.getAppParamters().isSkipDb()) {
			try {
				convetor.exportDb2xml(AppContext.getAppParamters().getDbPath());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}

		convetor.loadCLIMapping();
		List<String> cliCommands = convetor.genrateCLICommands();
		File file = new File(AppContext.getAppParamters().getDstPath());
		FileOutputStream fout = null;
		BufferedWriter writer = null;
		try {
			fout = new FileOutputStream(file);
			writer = new BufferedWriter(new OutputStreamWriter(fout));
			for (String cmd : cliCommands) {
				writer.write(cmd + "\r\n");
			}
			System.out.println("Convertion complete : "
					+ AppContext.getAppParamters().getDstPath());
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
