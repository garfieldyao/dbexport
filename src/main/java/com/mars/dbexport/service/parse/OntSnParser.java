/**
 * 
 */
package com.mars.dbexport.service.parse;

import com.mars.dbexport.bo.DbData;
import com.mars.dbexport.utils.GenericUtils;

/**
 * @author Yao
 * 
 */
public class OntSnParser implements Parser {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mars.dbexporter.service.parse.Parser#parse(com.mars.dbexporter.bo
	 * .DbData)
	 */
	@Override
	public String parse(DbData data) {
		// TODO Auto-generated method stub
		String srcData = data.getValue();
		String vendor = GenericUtils.parseHex2String(srcData.substring(2, 10));
		String sn = srcData.substring(10).toUpperCase();
		return vendor + ":" + sn;
	}

}
