/**
 * 
 */
package com.mars.dbexporter.service.parse;

import com.mars.dbexporter.AppContext;
import com.mars.dbexporter.bo.DbData;
import com.mars.dbexporter.utils.GenericUtils;
import com.mars.dbexporter.utils.IfindexUtils;

/**
 * @author Yao
 * 
 */
public class OntIfindexParser implements Parser {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mars.dbexporter.task.parse.parser#parse(java.lang.Object)
	 */
	@Override
	public String parse(DbData data) {
		// TODO Auto-generated method stub
		String result = "";
		try {
			int index = Integer.parseInt(GenericUtils.parseCommData(data));
			if (index > 0) {
				return IfindexUtils.parseOntInterface(index, AppContext
						.getAppParamters().getOltType());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return result;
		}
		return result;
	}
}
