/**
 * 
 */
package com.mars.dbexporter.bo;

import com.mars.dbexporter.bo.enums.DataType;
import com.mars.dbexporter.utils.GenericUtils;

/**
 * @author Yao Liqiang
 * @data Aug 5, 2013
 * @description
 */
public class DbData implements Comparable<DbData> {
	private String name = "";
	private DataType type = DataType.UNDEFINE;
	private String value = "";
	private String defaultValue = "";
	private int length = -1;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public DataType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(DataType type) {
		this.type = type;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @param defaultValue
	 *            the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @param length
	 *            the length to set
	 */
	public void setLength(int length) {
		this.length = length;
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
		sb.append("Name=");
		sb.append(this.name);
		sb.append(",");
		sb.append("Type=");
		sb.append(this.type);
		sb.append(",");
		sb.append("Length=");
		sb.append(this.length);
		sb.append(",");
		sb.append("Default=");
		sb.append(this.defaultValue);
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(DbData obj) {
		// TODO Auto-generated method stub
		if (this.type != obj.type)
			return 0;
		String v1 = GenericUtils.parseCommData(this);
		String v2 = GenericUtils.parseCommData(obj);
		if (type == DataType.UNSIGNED_LONG || type == DataType.UNSIGNED_SHORT
				|| type == DataType.UNSIGNED_CHAR) {
			int iv1 = Integer.parseInt(v1);
			int iv2 = Integer.parseInt(v2);
			if (iv1 > iv2)
				return 1;
			else if (iv1 < iv2)
				return -1;
			else
				return 0;
		}

		return 0;
	}
}
