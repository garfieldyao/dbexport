/**
 * 
 */
package com.mars.dbexport.service.parse;

import com.mars.dbexport.bo.DbData;

/**
 * @author Yao
 * 
 *         Notes that the defination of ifIndex in DB is quit different from MIB
 * 
 */
public interface Parser {
	public String parse(DbData data);
}
