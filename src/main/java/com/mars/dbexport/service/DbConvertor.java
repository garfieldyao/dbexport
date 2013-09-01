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
	 * Invoke migration tools, had been relaced by java native code
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
