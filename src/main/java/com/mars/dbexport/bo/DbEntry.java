/**
 * 
 */
package com.mars.dbexport.bo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yao Liqiang
 * @data Aug 5, 2013
 * @description
 */
public class DbEntry {
	private String tableName = "";
	private Map<String, DbData> dbDatas = new HashMap<String, DbData>();

	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @param tableName
	 *            the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * @return the dbDatas
	 */
	public Map<String, DbData> getDbDatas() {
		return dbDatas;
	}

	/**
	 * @param dbDatas
	 *            the dbDatas to set
	 */
	public void setDbDatas(Map<String, DbData> dbDatas) {
		this.dbDatas = dbDatas;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		for (DbData data : this.dbDatas.values()) {
			sb.append("Table ");
			sb.append(this.getTableName());
			sb.append(": ");
			sb.append("name=");
			sb.append(data.getName());
			sb.append(",");
			sb.append("value=");
			sb.append(data.getValue());
			sb.append(",");
			sb.append("type=");
			sb.append(data.getType());
			sb.append("\r\n");

		}
		return sb.toString();
	}
}
