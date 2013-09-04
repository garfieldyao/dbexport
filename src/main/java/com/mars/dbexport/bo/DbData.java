/**
 * 
 */
package com.mars.dbexport.bo;

import com.mars.dbexport.bo.enums.DataType;
import com.mars.dbexport.utils.GenericUtils;

/**
 * @author Yao Liqiang
 * @data Aug 5, 2013
 * @description
 */
public class DbData implements Comparable<DbData> {
	private String name = "";
	private DataType type = DataType.UNDEFINE;
	private String value = "";
	private int len = 0;
	private int offset = 0;

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
		sb.append("Len=");
		sb.append(this.len);
		sb.append(",");
		sb.append("Value=");
		sb.append(this.value);
		sb.append(",");
		sb.append("Offset=");
		sb.append(this.offset);
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
		if (type != DataType.STRING && type != DataType.UNDEFINE) {
			long iv1 = Long.parseLong(v1);
			long iv2 = Long.parseLong(v2);
			if (iv1 > iv2)
				return 1;
			else if (iv1 < iv2)
				return -1;
			else
				return 0;
		}

		return 0;
	}

	public int getLen() {
		return len;
	}

	public void setLen(int len) {
		this.len = len;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
}
