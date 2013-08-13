/**
 * 
 */
package com.mars.dbexporter.bo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yao
 *
 */
public class DbIndex {
	private String dbTable = null;
	private List<String> dbIndex = new ArrayList<String>();
	private List<String> dbIndexParser = new ArrayList<String>();
	/**
	 * @return the dbTable
	 */
	public String getDbTable() {
		return dbTable;
	}
	/**
	 * @param dbTable the dbTable to set
	 */
	public void setDbTable(String dbTable) {
		this.dbTable = dbTable;
	}
	/**
	 * @return the dbIndex
	 */
	public List<String> getDbIndex() {
		return dbIndex;
	}
	/**
	 * @param dbIndex the dbIndex to set
	 */
	public void setDbIndex(List<String> dbIndex) {
		this.dbIndex = dbIndex;
	}
	/**
	 * @return the dbIndexParser
	 */
	public List<String> getDbIndexParser() {
		return dbIndexParser;
	}
	/**
	 * @param dbIndexParser the dbIndexParser to set
	 */
	public void setDbIndexParser(List<String> dbIndexParser) {
		this.dbIndexParser = dbIndexParser;
	}
	
	
}
