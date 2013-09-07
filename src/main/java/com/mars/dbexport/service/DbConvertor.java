/**
 * 
 */
package com.mars.dbexport.service;

import java.util.List;

/**
 * @author Yao
 * 
 */
public interface DbConvertor {
	/**
	 * Java native code will be used to read database binary file instead of
	 * invoke migation tool
	 * 
	 * @deprecated
	 * @param filePath
	 * @throws Exception
	 */
	public void exportDb2xml(String filePath) throws Exception;

	/**
	 * Generate CLI commands, loadCLIMapping must be finished before
	 * 
	 * @return
	 */
	public List<String> genrateCLICommands();
}
