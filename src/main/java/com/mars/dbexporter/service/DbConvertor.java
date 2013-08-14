/**
 * 
 */
package com.mars.dbexporter.service;

import java.util.List;

/**
 * @author Yao
 * 
 */
public interface DbConvertor {
	public void exportDb2xml(String filePath) throws Exception;

	public void loadCLIMapping();

	public List<String> genrateCLICommands();
}
