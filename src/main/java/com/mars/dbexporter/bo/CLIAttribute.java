/**
 * 
 */
package com.mars.dbexporter.bo;

/**
 * @author Yao Liqiang
 * @data Aug 5, 2013
 * @description
 */
public class CLIAttribute {
	private String cliName = "";
	private String dbTable = "";
	private String dbAttribute = "";
	private String parser = "auto";
	private DbIndex index = new DbIndex();
	private boolean novalue = false;
	private String defaultValue = null;
	/**
	 * @return the cliName
	 */
	public String getCliName() {
		return cliName;
	}
	/**
	 * @param cliName the cliName to set
	 */
	public void setCliName(String cliName) {
		this.cliName = cliName;
	}
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
	 * @return the dbAttribute
	 */
	public String getDbAttribute() {
		return dbAttribute;
	}
	/**
	 * @param dbAttribute the dbAttribute to set
	 */
	public void setDbAttribute(String dbAttribute) {
		this.dbAttribute = dbAttribute;
	}
	/**
	 * @return the parser
	 */
	public String getParser() {
		return parser;
	}
	/**
	 * @param parser the parser to set
	 */
	public void setParser(String parser) {
		this.parser = parser;
	}
	/**
	 * @return the novalue
	 */
	public boolean isNovalue() {
		return novalue;
	}
	/**
	 * @param novalue the novalue to set
	 */
	public void setNovalue(boolean novalue) {
		this.novalue = novalue;
	}
	/**
	 * @return the index
	 */
	public DbIndex getIndex() {
		return index;
	}
	/**
	 * @param index the index to set
	 */
	public void setIndex(DbIndex index) {
		this.index = index;
	}
	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}
	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	
}
