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
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.mars.dbexport.AppContext;
import com.mars.dbexport.bo.CLIAttribute;
import com.mars.dbexport.bo.DbData;
import com.mars.dbexport.bo.DbEntry;
import com.mars.dbexport.bo.DbIndex;
import com.mars.dbexport.bo.enums.DataType;
import com.mars.dbexport.service.impl.DbConvertorImpl;
import com.mars.dbexport.service.parse.Parser;

/**
 * @author Yao
 * 
 */
public class GenericUtils {

	public static String parseCommData(DbData dbData) {
		return parseCommData(dbData, false);
	}

	public static String parseCommData(DbData dbData, boolean reverse) {
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
		String value = srcData.substring(2);
		if (reverse) {
			StringBuilder sb = new StringBuilder();
			for (int k = value.length() - 1; k >= 0; k--) {
				sb.append(value.charAt(k));
			}
			value = sb.toString();
		}

		if (type == DataType.STRING) {
			return parseHex2String(value);
		} else if (type == DataType.UNSIGNED_LONG || type == DataType.LONG) {
			return "" + Long.parseLong(value, 16);
		} else {
			return "" + Integer.parseInt(value, 16);
		}
	}

	public static String parseHex2String(String srcData) {
		int len = srcData.length();
		if (len <= 0 || (len % 2) != 0) {
			return "";
		}
		byte[] bytes = new byte[len / 2];
		for (int i = 0; i < len / 2; i++) {
			String data = srcData.substring(i * 2, i * 2 + 2);
			if ("00".equals(data))
				break;
			bytes[i] = parseInt2Byte(Integer.parseInt(data, 16));
		}

		String result = new String(bytes).trim();
		try {
			result = new String(bytes, "GB2312").trim();
		} catch (UnsupportedEncodingException e) {
		}
		return result;
	}

	public static String parseBufferString(byte[] buffer) {
		if (ArrayUtils.isEmpty(buffer))
			return "";
		int len = buffer.length;
		byte[] bytes = new byte[len];
		for (int i = 0; i < len; i++) {
			if (buffer[i] == 0)
				break;
			bytes[i] = buffer[i];
		}

		return new String(bytes);
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
		long value = Long.parseLong(srcData.trim());
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

	public static String parseReverseData(DbData srcData) {
		return parseCommData(srcData, true);
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
		String name = dbData.getName();
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

	public static String parseDataValue(String dbParser, DbData dbData,
			Object... objs) {
		String result = "";
		if (dbParser.startsWith("raw:")) {
			return dbParser.substring(4).trim();
		} else if (dbParser.startsWith("bool")) {
			return parserBooleanData(dbData, dbParser) ? "true" : "false";
		} else if (dbParser.startsWith("auto")) {
			result = parseAutoData(dbData, dbParser);
		} else if (dbParser.equals("reverse")) {
			result = parseReverseData(dbData);
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
		} else if (dbParser.startsWith("ref:")) {
			result = GenericUtils.parseRefData(dbData, dbParser,
					(DbConvertorImpl) objs[0]);
		} else {
			result = dbData.getValue();
		}
		return result;
	}

	public static String parseRefData(DbData dbData, String dbParser,
			DbConvertorImpl convertor) {
		String regx = "ref:([^ ]+:[^ ]+:[^ ]+)";
		Pattern pat = Pattern.compile(regx);
		Matcher mat = pat.matcher(dbParser);
		if (!mat.matches())
			return "NULL";
		String[] split = dbParser.substring(5, dbParser.length() - 1)
				.split(":");
		String table = split[0];
		String index = split[1];
		String name = split[2];
		List<DbEntry> list = convertor.dbDatas.get(table);
		if (list == null) {
			list = convertor.readDbFile(table);
			convertor.dbDatas.put(table, list);
		}

		for (DbEntry entry : list) {
			Map<String, DbData> dbDatas = entry.getDbDatas();
			if (dbData.getValue().equals(dbDatas.get(index).getValue())) {
				return parseCommData(dbDatas.get(name));
			}
		}
		return "NULL";
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
			datas[src.length - i - 1] = src[i];
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

	public static String bytes2hex(byte[] src) {
		byte[] datas = new byte[src.length];
		for (int k = 0; k < src.length; k++) {
			datas[src.length - 1 - k] = src[k];
		}
		StringBuilder sb = new StringBuilder();
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
		String raw = sb.toString();
		int sx = 0;
		for (int idx = 0; idx < raw.length(); idx++) {
			char car = raw.charAt(idx);
			if (car != '0')
				break;
			sx++;
		}
		raw = raw.substring(sx);
		if (StringUtils.isEmpty(raw))
			raw = "0";

		return "0x" + raw;
	}

	// validate IP address
	public static boolean isValidAddress(String ip) {
		String regx = "[0-9]+[.][0-9]+[.][0-9]+[.][0-9]+";
		Pattern pat = Pattern.compile(regx);
		Matcher mat = pat.matcher(ip);
		if (!mat.matches())
			return false;
		String[] split = ip.split(".");
		for (String str : split) {
			int value = Integer.parseInt(str);
			if (value < 0 || value > 255)
				return false;
		}
		return true;
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
