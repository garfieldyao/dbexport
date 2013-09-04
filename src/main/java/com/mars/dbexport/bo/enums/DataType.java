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
	UNSIGNED_CHAR, CHAR, SHORT, UNSIGNED_SHORT, LONG, UNSIGNED_LONG, STRING, UNDEFINE;

	public static DataType getType(String src) {
		for (DataType type : DataType.values()) {
			if (type.name().equals(src))
				return type;
		}
		return DataType.UNDEFINE;
	}

	public static DataType getType(int src) {
		switch (src) {
		case 0:
			return DataType.UNSIGNED_CHAR;
		case 1:
			return DataType.CHAR;
		case 2:
			return DataType.SHORT;
		case 3:
			return DataType.UNSIGNED_SHORT;
		case 4:
		case 5:
			return DataType.LONG;
		case 6:
			return DataType.UNSIGNED_LONG;
		case 7:
			return DataType.LONG;
		case 8:
			return DataType.STRING;
		case 9:
		case 10:
		}
		return DataType.UNDEFINE;
	}
}
