package com.mars.dbexport.service.parse;

import com.mars.dbexport.bo.DbData;
import com.mars.dbexport.utils.GenericUtils;

public class VlanIdParser implements Parser {

	@Override
	public String parse(DbData data) {
		// TODO Auto-generated method stub
		int index = Integer.parseInt(GenericUtils.parseCommData(data));
		if (index < 0)
			return "-1";
		if (index == 0)
			return "0";
		int cvlan = index & 4095;
		int svlan = (index - cvlan) >> 12;
		StringBuilder sb = new StringBuilder();
		if (svlan > 0) {
			sb.append("stacked");
			sb.append(":");
			sb.append(svlan);
			sb.append(":");
		}
		sb.append(cvlan);
		return sb.toString();
	}
}
