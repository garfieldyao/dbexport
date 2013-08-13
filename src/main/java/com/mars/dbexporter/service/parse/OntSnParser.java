/**
 * 
 */
package com.mars.dbexporter.service.parse;

import com.mars.dbexporter.bo.DbData;
import com.mars.dbexporter.utils.GenericUtils;

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
		String sn = srcData.substring(10);
		return vendor + ":" + sn;
	}

}
