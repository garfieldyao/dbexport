/**
 * 
 */
package com.mars.dbexport.service.parse;

import com.mars.dbexport.AppContext;
import com.mars.dbexport.bo.DbData;
import com.mars.dbexport.utils.GenericUtils;
import com.mars.dbexport.utils.IfindexUtils;

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
