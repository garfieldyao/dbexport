/**
 * 
 */
package com.mars.dbexporter.utils;

import com.mars.dbexporter.bo.enums.OltType;

/**
 * @author Yao
 * 
 */
public class IfindexUtils {
	public static String parseOntInterface(int ifindex, OltType olt) {
		if (ifindex <= 0)
			return "";
		int rack = 1;
		int shelf = 1;
		int slot = 0;
		int pon = 0;
		int ont = 0;

		if (olt == OltType.FX7360) {
			rack = 1;
			shelf = 1;
			slot = ((ifindex >> 16) & 255) - 2;
			pon = ((ifindex >> 7) & 511) + 1;
			ont = ((ifindex) & 127) + 1;
		} else {
			// TODO
			rack = 1;
			shelf = 1;
			slot = ((ifindex >> 16) & 255) - 2;
			pon = ((ifindex >> 7) & 511) + 1;
			ont = ((ifindex) & 127) + 1;
		}

		StringBuilder sb = new StringBuilder();
		sb.append(rack);
		sb.append("/");
		sb.append(shelf);
		sb.append("/");
		sb.append(slot);
		sb.append("/");
		sb.append(pon);
		sb.append("/");
		sb.append(ont);

		return sb.toString();
	}
}
