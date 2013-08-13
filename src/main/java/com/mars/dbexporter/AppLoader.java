/**
 * 
 */
package com.mars.dbexporter;

import com.mars.dbexporter.bo.enums.ErrorCode;
import com.mars.dbexporter.service.ExecTaskCmd;
import com.mars.dbexporter.service.impl.ExecTaskCmdImpl;
import com.mars.dbexporter.utils.PlatformUtils;

/**
 * @author Yao Liqiang
 * @data Aug 4, 2013
 * @description
 */
public class AppLoader {

	public static void main(String[] args) {
		if (!AppContext.initAppParameters(args))
			return;

		// lock running state
		if (!PlatformUtils.lockRunningState()) {
			System.out.println(ErrorCode.AppIsRunning.getDesc());
			return;
		}

		if (AppContext.getAppParamters().isGuiMode()) {
			// TODO
		} else {
			ExecTaskCmd cmd = new ExecTaskCmdImpl();
			cmd.execTask();
		}
	}
}
