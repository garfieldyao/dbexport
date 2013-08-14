/**
 * 
 */
package com.mars.dbexport.bo.enums;

/**
 * 
 * @author Yao Liqiang
 * @data Aug 3, 2013
 * @description
 */

public enum DataType {
	UNSIGNED_LONG, UNSIGNED_SHORT, UNSIGNED_CHAR, STRING, UNDEFINE;

	public static DataType getType(String src) {
		for (DataType type : DataType.values()) {
			if (type.name().equals(src))
				return type;
		}
		return DataType.UNDEFINE;
	}
}
