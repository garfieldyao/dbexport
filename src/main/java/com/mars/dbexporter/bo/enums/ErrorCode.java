/**
 * 
 */
package com.mars.dbexporter.bo.enums;

/**
 * @author Yao Liqiang
 * @data Aug 4, 2013
 * @description
 */
public enum ErrorCode {
	AppIsRunning(1, "Application is running"), FileNotFound(2, "File not exist"), PlatformNotSupport(
			3, "This application not support your OS"), ProcError(254, "Procce Error");

	private int id;
	private String desc;

	private ErrorCode(int id, String desc) {
		this.setId(id);
		this.setDesc(desc);
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * @param desc the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.getDesc();
	}
}
