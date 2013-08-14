/**
 * 
 */
package com.mars.dbexport.bo;

import com.mars.dbexport.bo.enums.OltType;

/**
 * @author Yao Liqiang
 * @data Aug 5, 2013
 * @description
 */
public class AppParamters {
	private boolean guiMode = false;
	private OltType oltType = OltType.FX7360;
	// skip DB file convertion
	private boolean skipDb = false;
	// skip IHUB
	private boolean skipLanx = false;
	
	private boolean trace = false;
	
	private boolean nofilter = false;
	
	private String dbPath = null;
	private String dstPath = null;

	/**
	 * @return the guiMode
	 */
	public boolean isGuiMode() {
		return guiMode;
	}

	/**
	 * @param guiMode
	 *            the guiMode to set
	 */
	public void setGuiMode(boolean guiMode) {
		this.guiMode = guiMode;
	}

	/**
	 * @return the dbPath
	 */
	public String getDbPath() {
		return dbPath;
	}

	/**
	 * @param dbPath
	 *            the dbPath to set
	 */
	public void setDbPath(String dbPath) {
		this.dbPath = dbPath;
	}

	/**
	 * @return the dstPath
	 */
	public String getDstPath() {
		return dstPath;
	}

	/**
	 * @param dstPath
	 *            the dstPath to set
	 */
	public void setDstPath(String dstPath) {
		this.dstPath = dstPath;
	}

	/**
	 * @return the oltType
	 */
	public OltType getOltType() {
		return oltType;
	}

	/**
	 * @param oltType
	 *            the oltType to set
	 */
	public void setOltType(OltType oltType) {
		this.oltType = oltType;
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
		sb.append("GuiMode=");
		sb.append(this.guiMode);
		sb.append(",");
		sb.append("oltType=");
		sb.append(this.oltType);
		sb.append(",");
		sb.append("dbPath=");
		sb.append(this.dbPath);
		sb.append(",");
		sb.append("dstPath=");
		sb.append(this.dstPath);
		sb.append(",");
		sb.append("skipDB=");
		sb.append(this.skipDb);
		sb.append(",");
		sb.append("skipLanx=");
		sb.append(this.skipLanx);
		return sb.toString();
	}

	/**
	 * @return the skipDb
	 */
	public boolean isSkipDb() {
		return skipDb;
	}

	/**
	 * @param skipDb
	 *            the skipDb to set
	 */
	public void setSkipDb(boolean skipDb) {
		this.skipDb = skipDb;
	}

	/**
	 * @return the skipLanx
	 */
	public boolean isSkipLanx() {
		return skipLanx;
	}

	/**
	 * @param skipLanx the skipLanx to set
	 */
	public void setSkipLanx(boolean skipLanx) {
		this.skipLanx = skipLanx;
	}

	/**
	 * @return the nofilter
	 */
	public boolean isNofilter() {
		return nofilter;
	}

	/**
	 * @param nofilter the nofilter to set
	 */
	public void setNofilter(boolean nofilter) {
		this.nofilter = nofilter;
	}

	/**
	 * @return the trace
	 */
	public boolean isTrace() {
		return trace;
	}

	/**
	 * @param trace the trace to set
	 */
	public void setTrace(boolean trace) {
		this.trace = trace;
	}
}
