/**
 * 
 */
package com.mars.dbexport.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mars.dbexport.bo.enums.OltType;

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

	public static OltType checkOltType(String mibVer) {
		OltType type = OltType.UNKNOWN;
		if (mibVer.startsWith("3FE21961")) {
			String regx = "_V[0-9]+[.][0-9]+";
			Pattern pat = Pattern.compile(regx);
			Matcher mat = pat.matcher(mibVer);
			if (mat.find()) {
				String group = mat.group();
				float ver = Float.parseFloat(group.substring(2));
				if (ver >= 4.4) {
					type = OltType.FX7360;
				} else {
					type = OltType.FX7360L;
				}
			}
		}
		return type;
	}
}
