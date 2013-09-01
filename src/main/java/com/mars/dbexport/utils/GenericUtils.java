/**
 * 
 */
package com.mars.dbexport.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.mars.dbexport.AppContext;
import com.mars.dbexport.bo.CLIAttribute;
import com.mars.dbexport.bo.DbData;
import com.mars.dbexport.bo.DbEntry;
import com.mars.dbexport.bo.DbIndex;
import com.mars.dbexport.bo.enums.DataType;
import com.mars.dbexport.service.parse.Parser;

/**
 * @author Yao
 * 
 */
public class GenericUtils {
	public static String parseCommData(DbData dbData) {
		String srcData = dbData.getValue();
		if (srcData != null) {
			srcData = srcData.trim();
		} else {
			return "";
		}
		if (StringUtils.isEmpty(srcData)) {
			return "";
		}
		DataType type = dbData.getType();
		if (type == DataType.STRING) {
			if (srcData.startsWith("0x")) {
				return parseHex2String(srcData.substring(2));
			} else {
				return srcData;
			}
		} else if (type == DataType.UNSIGNED_CHAR
				|| type == DataType.UNSIGNED_SHORT) {
			if (srcData.startsWith("0x")) {
				return "" + Integer.parseInt(srcData.substring(2), 16);
			} else {
				return srcData;
			}
		} else if (type == DataType.UNSIGNED_LONG) {
			if (srcData.startsWith("0x")) {
				return "" + Long.parseLong(srcData.substring(2), 16);
			} else {
				return srcData;
			}
		}

		return "";
	}

	public static String parseHex2String(String srcData) {
		int len = srcData.length();
		if (len <= 0 || (len % 2) != 0) {
			return "";
		}
		byte[] bytes = new byte[len / 2];
		for (int i = 0; i < len / 2; i++) {
			String data = srcData.substring(i * 2, i * 2 + 2);
			bytes[i] = parseInt2Byte(Integer.parseInt(data, 16));
		}

		String result = new String(bytes).trim();
		try {
			result = new String(bytes, "GB2312").trim();
		} catch (UnsupportedEncodingException e) {
		}
		return result;
	}

	public static byte parseInt2Byte(int src) {
		if (src > 0 && src <= 127) {
			return (byte) src;
		} else if (src > 127 && src <= 255) {
			return (byte) (src - 256);
		} else {
			return 0;
		}
	}

	public static String parseRegxData(DbData dbData, String parser) {
		String srcData = parseCommData(dbData);
		int value = Integer.parseInt(srcData.trim());
		String regx = "regx:[^: ]*:([(][0-9]+,[0-9]+,[-]?[0-9]+[)])+";
		Pattern pat = Pattern.compile(regx);
		Matcher mat = pat.matcher(parser);
		if (!mat.matches()) {
			return "";
		}

		String prefix = parser.substring(parser.indexOf(":") + 1,
				parser.lastIndexOf(":"));
		List<int[]> matslist = new ArrayList<int[]>();
		regx = "[(][0-9]+,[0-9]+,[-]?[0-9]+[)]";
		pat = Pattern.compile(regx);
		mat = pat.matcher(parser);
		String group;
		while (mat.find()) {
			group = mat.group();
			group = group.substring(1, group.length() - 1);
			int[] mats = new int[3];
			String[] split = group.split(",");
			mats[0] = Integer.parseInt(split[0].trim());
			mats[1] = Integer.parseInt(split[1].trim());
			mats[2] = Integer.parseInt(split[2].trim());

			matslist.add(mats);
		}

		StringBuilder sb = new StringBuilder();
		sb.append(prefix);
		for (int idx = 0; idx < matslist.size(); idx++) {
			int[] arr = matslist.get(idx);
			sb.append(((value >> arr[0]) & ((int) Math.pow(2, arr[1]) - 1))
					+ arr[2]);
			sb.append("/");
		}
		sb.deleteCharAt(sb.length() - 1);

		return sb.toString();
	}

	public static String parseEnumData(DbData dbData, String parser) {
		String srcData = dbData.getValue().trim();
		if (!parser.startsWith("enum:"))
			return "";
		String regx = "[(][^,]+[,][^,]+[)]";
		Pattern pat = Pattern.compile(regx);
		Matcher mat = pat.matcher(parser);
		String group;
		String[] tmp;
		Map<String, String> map = new HashMap<String, String>();
		while (mat.find()) {
			group = mat.group();
			tmp = group.substring(1, group.length() - 1).split(",");
			map.put(tmp[0], tmp[1]);
		}

		return map.get(srcData.trim());
	}

	public static String parseAutoData(DbData srcData, String parser) {
		if (!parser.startsWith("auto"))
			return "";
		String regx = "[(][^,]+[,][^,]+[)]";
		Pattern pat = Pattern.compile(regx);
		Matcher mat = pat.matcher(parser);
		String group;
		String[] tmp;
		Map<String, String> map = new HashMap<String, String>();
		while (mat.find()) {
			group = mat.group();
			tmp = group.substring(1, group.length() - 1).split(",");
			map.put(tmp[0], tmp[1]);
		}

		if (map.containsKey(srcData.getValue())) {
			return map.get(srcData.getValue());
		} else {
			return parseCommData(srcData);
		}
	}

	public static boolean parserBooleanData(DbData srcData, String parser) {
		if (!parser.startsWith("bool"))
			return false;
		String regx = "[0-9]+";
		Pattern pat = Pattern.compile(regx);
		Matcher mat = pat.matcher(parser);
		if (mat.find()) {
			String group = mat.group();
			int pos = Integer.parseInt(group);
			int value = Integer.parseInt(parseCommData(srcData));
			return ((value >> pos) & 1) > 0;
		}
		return true;
	}

	public static void sortDbEntry(CLIAttribute cmd, List<DbEntry> entryList) {
		if (CollectionUtils.isEmpty(entryList))
			return;
		// TODO
		DbData dbData = entryList.get(0).getDbDatas().get(cmd.getDbAttribute());
		if (dbData == null)
			return;
		DataType type = dbData.getType();
		String name = dbData.getName();
		if (type == DataType.UNSIGNED_LONG || type == DataType.UNSIGNED_SHORT
				|| type == DataType.UNSIGNED_CHAR) {
			DbEntry temp = null;
			for (int i = entryList.size() - 1; i > 0; --i) {
				for (int j = 0; j < i; ++j) {
					if (entryList.get(j + 1).getDbDatas().get(name)
							.compareTo(entryList.get(j).getDbDatas().get(name)) < 0) {
						temp = entryList.get(j);
						entryList.set(j, entryList.get(j + 1));
						entryList.set(j + 1, temp);
					}
				}
			}
		}
	}

	public static String parseDataValue(String dbParser, DbData dbData) {
		String result = "";
		if (dbParser.startsWith("raw:")) {
			return dbParser.substring(4).trim();
		} else if (dbParser.startsWith("bool")) {
			return parserBooleanData(dbData, dbParser) ? "true" : "false";
		} else if (dbParser.startsWith("auto")) {
			result = parseAutoData(dbData, dbParser);
		} else if (dbParser.startsWith("class")) {
			String className = dbParser.substring(6);
			Parser parser = AppContext.getParsers().get(className);
			if (parser == null) {
				try {
					parser = (Parser) Class.forName(className).newInstance();
					AppContext.getParsers().put(className, parser);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			result = parser.parse(dbData);
		} else if (dbParser.startsWith("regx:")) {
			// TODO
			result = GenericUtils.parseRegxData(dbData, dbParser);
		} else if (dbParser.startsWith("enum:")) {
			result = GenericUtils.parseEnumData(dbData, dbParser);
		} else {
			result = dbData.getValue();
		}
		return result;
	}

	public static String parseDbEntryIndex(CLIAttribute attr, DbEntry entry) {
		StringBuilder sb = new StringBuilder();
		DbIndex dbIndex = attr.getIndex();
		for (int k = 0; k < dbIndex.getDbIndex().size(); k++) {
			if (k > 0)
				sb.append("/");
			String dbidx = dbIndex.getDbIndex().get(k);
			String dbpaser = dbIndex.getDbIndexParser().get(k);
			DbData dbData = entry.getDbDatas().get(dbidx);
			sb.append(parseDataValue(dbpaser, dbData));
		}
		return sb.toString();
	}

	public static String completeString(String raw, int len, char prefix,
			boolean end) {
		if (raw == null)
			raw = "";
		int gap = len - raw.length();
		if (gap <= 0)
			return raw;
		StringBuilder sb = new StringBuilder();
		if (end)
			sb.append(raw);
		for (int k = 0; k < gap; k++) {
			sb.append(prefix);
		}
		if (!end)
			sb.append(raw);
		return sb.toString();
	}

	public static int bytes2int(byte[] src) {
		byte[] datas = new byte[4];
		for (int i = 0; i < src.length && i < 4; i++) {
			datas[i] = src[i];
		}
		int s0 = datas[0] & 0xFF;
		int s1 = datas[1] & 0xFF;
		int s2 = datas[2] & 0xFF;
		int s3 = datas[3] & 0xFF;

		s1 <<= 8;
		s2 <<= 16;
		s3 <<= 24;

		int s = s0 | s1 | s2 | s3;
		return s;
	}

	public static String bytes2hex(byte[] datas) {
		StringBuilder sb = new StringBuilder();
		sb.append("0x");
		for (int i = datas.length - 1; i >= 0; i--) {
			byte data = datas[i];
			Integer d = 0;
			if (data >= 0)
				d = (int) data;
			else
				d = data + 256;
			String hex = Integer.toHexString(d);
			if (hex.length() < 2)
				sb.append(0);
			sb.append(hex);
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		byte[] data = new byte[4];
		data[0] = 1;
		data[1] = 2;
		data[2] = 3;
		data[3] = -1;
		System.out.println(bytes2hex(data));
	}
}
