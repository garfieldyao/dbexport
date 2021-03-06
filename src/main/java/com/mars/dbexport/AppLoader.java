/**
 * 
 */
package com.mars.dbexport;

import com.mars.dbexport.bo.enums.ErrorCode;
import com.mars.dbexport.gui.AppMainFrame;
import com.mars.dbexport.service.LoadParameter;
import com.mars.dbexport.service.batch.BatchManager;
import com.mars.dbexport.service.batch.batchtask.DbExportTask;
import com.mars.dbexport.service.impl.LoadParameterImpl;
import com.mars.dbexport.utils.ClientUtils;
import com.mars.dbexport.utils.PlatformUtils;

/**
 * @author Yao Liqiang
 * @data Aug 4, 2013
 * @description
 */
public class AppLoader {

	public static void main(String[] args) {
		LoadParameter loadParam = new LoadParameterImpl();
		if (!loadParam.initSystemParam(args))
			return;

		// lock running state
		if (!PlatformUtils.lockRunningState()) {
			System.out.println(ErrorCode.AppIsRunning);
			if (AppContext.getAppParamters().isGuiMode()) {
				ClientUtils.showErrorDailog(ErrorCode.AppIsRunning.toString());
			}
			return;
		}

		if (AppContext.getAppParamters().isGuiMode()) {
			// TODO
			AppMainFrame mainFrame = new AppMainFrame();
			AppContext.setMainFrame(mainFrame);
			mainFrame.setVisible(true);
		} else {
			BatchManager manager = new DbExportTask(AppContext.getNeList()
					.keySet());
			manager.execute();
			System.exit(1);
		}
	}
}
