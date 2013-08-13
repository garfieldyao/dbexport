/**
 * 
 */
package com.mars.dbexporter.service.parse;

import com.mars.dbexporter.bo.DbData;

/**
 * @author Yao
 * 
 *         Notes that the defination of ifIndex in DB is quit different from MIB
 * 
 */
public interface Parser {
	public String parse(DbData data);
}
