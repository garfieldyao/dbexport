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
	UNSIGNED_CHAR(0), UNSIGNED_SHORT(50331648), UNSIGNED_LONG(100663296), STRING(
			134217728), CHAR(16777216), LONG(83886080), UNDEFINE(255);

	private DataType(int value) {
		// TODO Auto-generated constructor stub
		this.value = value;
	}

	private int value;

	public static DataType getType(String src) {
		for (DataType type : DataType.values()) {
			if (type.name().equals(src))
				return type;
		}
		return DataType.UNDEFINE;
	}

	public static DataType getType(int src) {
		for (DataType type : DataType.values()) {
			if (type.getValue() == src)
				return type;
		}
		return DataType.UNDEFINE;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
