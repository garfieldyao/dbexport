/**
 * 
 */
package com.mars.dbexporter.bo.enums;

import java.io.File;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author Yao Liqiang
 * @data Aug 3, 2013
 * @description
 */

public enum OsType {
	LINUX_I386("MJSH_l"), SOLARIS_I386("MJSH_i386"), SOLARIS_SPARC("MJSH"), WINDOWS(
			""), UNKNOWN("");

	private String exec;

	private OsType(String exec) {
		this.setExec(exec);
	}

	/**
	 * @return the exec
	 */
	public String getExec() {
		return exec;
	}

	/**
	 * @param exec
	 *            the exec to set
	 */
	public void setExec(String exec) {
		this.exec = exec;
	}

	public static boolean isConvertTool(File file) {
		for (OsType type : OsType.values()) {
			if (StringUtils.isEmpty(type.getExec()))
				continue;
			if (type.getExec().equals(file.getName())) {
				return true;
			}
		}
		return false;
	}
}
