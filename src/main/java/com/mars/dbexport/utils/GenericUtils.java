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
import com.mars.dbexport.bo.prv.IndexMatcher;
import com.mars.dbexport.bo.prv.ValueMatcher;
import com.mars.dbexport.service.DbOper;
import com.mars.dbexport.service.parse.Parser;

/**
 * @author Yao
 * 
 */
public class GenericUtils {
	public static String parseDataValue(String dbParser, DbData dbData,
			Object... objs) {
		if (dbData == null)
			return "";

		String result = dbData.getValue();
		if (dbParser.startsWith("raw:")) {
			return dbParser.substring(4);
		} else if (dbParser.startsWith("bool")) {
			return parserBooleanData(dbData, dbParser) ? "true" : "false";
		} else if (dbParser.startsWith("auto")) {
			result = parseAutoData(dbData, dbParser);
		} else if (dbParser.equals("reverse")) {
			result = parseReverseData(dbData);
		} else if (dbParser.startsWith("class:")) {
			String className = dbParser.substring(6);
			result = parseClassData(dbData, className, (DbOper) objs[0],
					(DbEntry) objs[1]);

		} else if (dbParser.startsWith("regx")) {
			result = parseRegxData(dbData, dbParser);
		} else if (dbParser.startsWith("enum")) {
			result = parseEnumData(dbData, dbParser);
		} else if (dbParser.startsWith("ref")) {
			result = parseRefData(dbData, dbParser, (DbOper) objs[0]);
		} else if (dbParser.startsWith("xref")) {
			result = parseXrefData(dbData, dbParser, (DbOper) objs[0],
					(DbEntry) objs[1]);
		} else if (dbParser.startsWith("select")) {
			result = parseSelectData(dbData, dbParser, (DbOper) objs[0],
					(DbEntry) objs[1]);
		} else if (dbParser.startsWith("multi")) {
			result = parseMultiData(dbData, dbParser, (DbOper) objs[0],
					(DbEntry) objs[1]);
		} else if (dbParser.startsWith("route")) {
			result = parseRouteData(dbData, dbParser, (DbOper) objs[0],
					(DbEntry) objs[1]);
		} else {
			result = dbData.getValue();
		}
		return result;
	}

	public static String parseCommData(DbData dbData) {
		return parseCommData(dbData, false);
	}

	public static String parseCommData(DbData dbData, boolean reverse) {
		String srcData = dbData.getValue();
		if (srcData != null) {
			srcData = srcData.trim();
		} else {
			return dbData.getValue();
		}
		if (StringUtils.isEmpty(srcData)) {
			return dbData.getValue();
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
			return parseHex2String(trimHexString(value));
		} else if (type == DataType.UNSIGNED_LONG || type == DataType.LONG) {
			return Long.parseLong(value, 16) + "";
		} else {
			return Integer.parseInt(value, 16) + "";
		}
	}

	private static String parseClassData(DbData srcData, String className,
			DbOper dbOper, DbEntry dbEntry) {
		String result = "";
		Parser parser = AppContext.getParsers().get(className);
		if (parser == null) {
			try {
				parser = (Parser) Class.forName(className).newInstance();
				AppContext.getParsers().put(className, parser);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (parser != null)
			result = parser.parse(srcData, dbOper, dbEntry);

		return result;
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

	private static String parseRegxData(DbData dbData, String parser) {
		if (!parser.startsWith("regx"))
			return dbData.getValue();

		long value = parseHex2Long(dbData);

		String prefix = parser.substring(parser.indexOf("|") + 1,
				parser.lastIndexOf("|"));
		List<ValueMatcher> matslist = new ArrayList<ValueMatcher>();
		String regx = "[(][^()]+[)]";
		Pattern pat = Pattern.compile(regx);
		Matcher mat = pat.matcher(parser);
		String group;
		while (mat.find()) {
			group = mat.group();
			group = group.substring(1, group.length() - 1);
			ValueMatcher matcher = new ValueMatcher();
			if (group.startsWith("raw:")) {
				matcher.setRaw(true);
				matcher.setData(group.substring(4));
				matslist.add(matcher);
			} else {
				int[] mats = new int[3];
				String[] split = group.split(",");
				if (split.length != 3)
					return dbData.getValue();
				mats[0] = Integer.parseInt(split[0].trim());
				mats[1] = Integer.parseInt(split[1].trim());
				mats[2] = Integer.parseInt(split[2].trim());
				matcher.setPoint(mats[0]);
				matcher.setMask(mats[1]);
				matcher.setOffset(mats[2]);
				matslist.add(matcher);
			}
		}

		StringBuilder sb = new StringBuilder();
		sb.append(prefix);
		for (ValueMatcher tmpmat : matslist) {
			if (tmpmat.isRaw()) {
				sb.append(tmpmat.getData());
			} else if (tmpmat.getMask() == 0) {
				sb.append(value + tmpmat.getOffset());
			} else {
				sb.append(((value >> tmpmat.getPoint()) & ((int) Math.pow(2,
						tmpmat.getMask()) - 1)) + tmpmat.getOffset());
			}
		}

		return sb.toString();
	}

	public static long parseHex2Long(DbData data) {
		DataType type = data.getType();
		if (type != DataType.STRING) {
			return Long.parseLong(parseCommData(data));
		} else {
			return Long.parseLong(data.getValue().substring(2), 16);
		}
	}

	private static String parseEnumData(DbData dbData, String parser) {
		String srcData = dbData.getValue().trim();
		if (!parser.startsWith("enum:"))
			return dbData.getValue();
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

	private static String parseAutoData(DbData dbdata, String parser) {
		if (!parser.startsWith("auto"))
			return dbdata.getValue();
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

		if (map.containsKey(dbdata.getValue())) {
			return map.get(dbdata.getValue());
		} else {
			return parseCommData(dbdata);
		}
	}

	private static String parseReverseData(DbData srcData) {
		return parseCommData(srcData, true);
	}

	private static boolean parserBooleanData(DbData srcData, String parser) {
		if (!parser.startsWith("bool:"))
			return false;
		String regx = "[-]?[0-9]+";
		Pattern pat = Pattern.compile(regx);
		Matcher mat = pat.matcher(parser);
		boolean flag = true;
		while (mat.find()) {
			String group = mat.group();
			boolean reverse = group.startsWith("-");
			long value = Long.parseLong(parseCommData(srcData));
			int pos = 0;
			if (reverse) {
				pos = Integer.parseInt(group.substring(1));
				flag &= (((value >> pos) & 1) == 0);
			} else {
				pos = Integer.parseInt(group);
				flag &= (((value >> pos) & 1) > 0);
			}
		}
		return flag;
	}

	public static void sortDbEntry(CLIAttribute cmd, List<DbEntry> entryList) {
		if (CollectionUtils.isEmpty(entryList))
			return;
		List<String> names = cmd.getIndex().getDbIndex();
		if (CollectionUtils.isEmpty(names))
			return;
		List<String> pres = new ArrayList<String>();
		for (String name : names) {
			sortDbEntry(name, entryList, pres);
			pres.add(name);
		}
	}

	private static void sortDbEntry(String name, List<DbEntry> tmpList,
			List<String> pres) {
		DbEntry temp = null;
		boolean flag = false;
		for (int i = tmpList.size() - 1; i > 0; --i) {
			for (int j = 0; j < i; ++j) {
				flag = false;
				for (String pre : pres) {
					if (tmpList.get(j + 1).getDbDatas().get(pre)
							.compareTo(tmpList.get(j).getDbDatas().get(pre)) != 0) {
						flag = true;
						break;
					}
				}
				if (flag)
					continue;
				if (tmpList.get(j + 1).getDbDatas().get(name)
						.compareTo(tmpList.get(j).getDbDatas().get(name)) < 0) {
					temp = tmpList.get(j);
					tmpList.set(j, tmpList.get(j + 1));
					tmpList.set(j + 1, temp);
				}
			}
		}
	}

	private static String parseRefData(DbData dbData, String dbParser,
			DbOper oper) {
		if (!dbParser.startsWith("ref"))
			return dbData.getValue();
		String prefix = "";
		if (dbParser.indexOf("|") < dbParser.lastIndexOf("|"))
			prefix = dbParser.substring(dbParser.indexOf("|") + 1,
					dbParser.lastIndexOf("|"));
		String[] split = dbParser.substring(dbParser.lastIndexOf("|") + 2,
				dbParser.length() - 1).split(":");
		String table = split[0];
		String index = split[1];
		String name = split[2];
		List<DbEntry> list = oper.getDbTable(table);
		if (CollectionUtils.isEmpty(list))
			return dbData.getValue();

		for (DbEntry entry : list) {
			Map<String, DbData> dbDatas = entry.getDbDatas();
			if (dbData.getValue().equals(dbDatas.get(index).getValue())) {
				return prefix + parseCommData(dbDatas.get(name));
			}
		}
		return "";
	}

	private static String parseSelectData(DbData dbData, String dbParser,
			DbOper dbOper, DbEntry dbEntry) {
		if (!dbParser.startsWith("select"))
			return dbData.getValue();
		String selector = dbParser.substring(7, dbParser.indexOf("?") - 1);
		String parserpart = dbParser.substring(dbParser.indexOf("?") + 2,
				dbParser.length() - 1);
		String[] split1 = selector.split("\\|");
		boolean local = false;
		String table = "";
		String matspart = "";
		String secattr = "";
		if (split1.length == 3) {
			local = false;
			table = split1[0];
			matspart = split1[1];
			secattr = split1[2];
		} else if (split1.length == 1) {
			local = true;
			secattr = selector;
		} else {
			return "";
		}

		boolean isNull = false;

		DbEntry target = null;
		if (!isNull) {
			if (!local) {
				List<DbEntry> dbTable = dbOper.getDbTable(table);
				if (CollectionUtils.isEmpty(dbTable))
					isNull = true;

				String regx = "[(][^()]+[)]";
				Pattern pat = Pattern.compile(regx);
				Matcher mat = null;
				List<IndexMatcher> indexMats = new ArrayList<IndexMatcher>();
				mat = pat.matcher(matspart);
				while (mat.find()) {
					String group = mat.group();
					group = group.substring(1, group.length() - 1);
					String[] tmpSplit = group.split(":");
					if (tmpSplit.length != 2)
						return "";
					String[] tmpSplit1 = tmpSplit[0].split(",");
					String[] tmpSplit2 = tmpSplit[1].split(",");
					if (tmpSplit1.length != 3 || tmpSplit2.length != 3)
						return "";
					IndexMatcher matcher = new IndexMatcher();
					matcher.setRawAttr(tmpSplit1[0]);
					matcher.setRawStart(Integer.parseInt(tmpSplit1[1]));
					matcher.setRawLen(Integer.parseInt(tmpSplit1[2]));
					matcher.setTargetAttr(tmpSplit2[0]);
					matcher.setTargetStart(Integer.parseInt(tmpSplit2[1]));
					matcher.setTargetLen(Integer.parseInt(tmpSplit2[2]));
					indexMats.add(matcher);
				}

				for (DbEntry entry : dbTable) {
					boolean flag = true;
					for (IndexMatcher imat : indexMats) {
						DbData targetdata = entry.getDbDatas().get(
								imat.getTargetAttr());
						DbData rawdata = dbEntry.getDbDatas().get(
								imat.getRawAttr());
						String targetdatavalue = completeHexString(
								targetdata.getValue(), imat.getTargetStart()
										+ imat.getTargetLen());
						String rawdatavalue = completeHexString(
								rawdata.getValue(),
								imat.getRawStart() + imat.getRawLen());
						String sb1 = rawdatavalue.substring(imat.getRawStart(),
								imat.getRawStart() + imat.getRawLen());
						String sb2 = targetdatavalue.substring(
								imat.getTargetStart(), imat.getTargetStart()
										+ imat.getTargetLen());
						flag &= sb1.equals(sb2);
					}
					if (flag) {
						target = entry;
						break;
					}
				}
			}
			if (target == null) {
				isNull = true;
			}
		} else {
			target = dbEntry;
		}

		if (!isNull && target == null) {
			return "";
		}

		String secstr = null;
		if (target != null)
			secstr = target.getDbDatas().get(secattr).getValue();
		List<String> tmpsplit3 = FormularParser.findPairs(parserpart);
		for (String tmpparser : tmpsplit3) {
			if (!tmpparser.contains("?"))
				continue;
			String tmat = tmpparser.substring(0, tmpparser.indexOf("?"));
			String tpart = tmpparser.substring(tmpparser.indexOf("?") + 1);
			String newparser = "";
			DbData newdata = null;
			if (tpart.contains(">>>")) {
				newparser = tpart.substring(tpart.indexOf(">>>") + 3);
				newdata = dbEntry.getDbDatas().get(
						tpart.substring(0, tpart.indexOf(">>>")));
			} else {
				newparser = tpart;
				newdata = dbData;
			}
			if ("*".equals(tmat) || (secstr != null && secstr.equals(tmat))) {
				String result = parseDataValue(newparser, newdata, dbOper,
						dbEntry);
				if (StringUtils.isNotEmpty(result))
					return result;
			}
		}

		return "";
	}

	private static String parseMultiData(DbData dbData, String dbParser,
			DbOper dbOper, DbEntry dbEntry) {
		if (!dbParser.startsWith("multi"))
			return dbData.getValue();
		String[] split = dbParser.substring(6, dbParser.length() - 1)
				.split(";");
		for (String parser : split) {
			String data = parseXrefData(dbData, parser, dbOper, dbEntry);
			if (StringUtils.isNotEmpty(data))
				return data;
		}
		return "";
	}

	private static String parseRouteData(DbData dbData, String dbParser,
			DbOper dbOper, DbEntry srcEntry) {
		if (!dbParser.startsWith("route"))
			return dbData.getValue();
		String rpart = dbParser.substring(5);
		List<String> split1 = FormularParser.findPairs(rpart);
		if (split1.size() < 2)
			return "";
		String parserPart = split1.get(split1.size() - 1);
		String parser = "";
		String targetAttr = "";
		if (parserPart.contains(">>")) {
			targetAttr = parserPart.substring(0, parserPart.indexOf(">>"));
			parser = parserPart.substring(parserPart.indexOf(">>") + 2).trim();
		} else {
			parser = parserPart.trim();
		}

		String regx = "[(][^()]+[)]";
		Pattern pat = Pattern.compile(regx);
		Matcher mat = null;
		DbEntry rawentry = srcEntry;
		DbEntry target = null;
		String frontinfo = "";
		String endinfo = "";
		for (int i = 0; i < split1.size() - 1; i++) {
			String routepart = split1.get(i);
			String tablename = "";
			if (routepart.startsWith("(")) {
				int infopos = FormularParser.findNearPair(routepart, 0);
				String infopart = routepart.substring(1, infopos);
				routepart = routepart.substring(infopos + 1);
				String addparser = infopart.substring(
						infopart.indexOf("(") + 1, infopart.lastIndexOf(")"));
				String addattr = infopart.substring(infopart.indexOf(":") + 1,
						infopart.indexOf("("));
				DbData infodata = rawentry.getDbDatas().get(addattr);
				String addvalue = parseDataValue(addparser, infodata, dbOper,
						rawentry);
				if (infopart.startsWith("front")) {
					frontinfo += addvalue;
				} else if (infopart.startsWith("end")) {
					endinfo += addvalue;
				}
			}

			tablename = routepart.substring(0, routepart.indexOf("?")).trim();

			List<DbEntry> dbTable = dbOper.getDbTable(tablename);
			if (CollectionUtils.isEmpty(dbTable))
				return "";
			List<IndexMatcher> indexMats = new ArrayList<IndexMatcher>();
			mat = pat.matcher(routepart);
			while (mat.find()) {
				String group = mat.group();
				group = group.substring(1, group.length() - 1);
				String[] tmpSplit = group.split(":");
				if (tmpSplit.length != 2)
					return "";
				String[] tmpSplit1 = tmpSplit[0].split(",");
				String[] tmpSplit2 = tmpSplit[1].split(",");
				if (tmpSplit1.length != 3 || tmpSplit2.length != 3)
					return "";
				IndexMatcher matcher = new IndexMatcher();
				matcher.setRawAttr(tmpSplit1[0]);
				matcher.setRawStart(Integer.parseInt(tmpSplit1[1]));
				matcher.setRawLen(Integer.parseInt(tmpSplit1[2]));
				matcher.setTargetAttr(tmpSplit2[0]);
				matcher.setTargetStart(Integer.parseInt(tmpSplit2[1]));
				matcher.setTargetLen(Integer.parseInt(tmpSplit2[2]));
				indexMats.add(matcher);
			}

			for (DbEntry tentry : dbTable) {
				boolean flag = true;
				for (IndexMatcher imat : indexMats) {
					DbData targetdata = tentry.getDbDatas().get(
							imat.getTargetAttr());
					DbData rawdata = rawentry.getDbDatas().get(
							imat.getRawAttr());
					String targetdatavalue = completeHexString(
							targetdata.getValue(),
							imat.getTargetStart() + imat.getTargetLen());
					String rawdatavalue = completeHexString(rawdata.getValue(),
							imat.getRawStart() + imat.getRawLen());
					String sb1 = rawdatavalue.substring(imat.getRawStart(),
							imat.getRawStart() + imat.getRawLen());
					String sb2 = targetdatavalue.substring(
							imat.getTargetStart(),
							imat.getTargetStart() + imat.getTargetLen());
					flag &= sb1.equals(sb2);
				}
				if (flag) {
					target = tentry;
					break;
				}
			}

			if (target == null) {
				return "";
			}

			rawentry = target;
		}

		if (StringUtils.isNotEmpty(targetAttr)) {
			dbData = target.getDbDatas().get(targetAttr);
		}
		String result = parseDataValue(parser, dbData, dbOper, target);
		if (StringUtils.isNotEmpty(frontinfo)) {
			result = frontinfo + result;
		}
		if (StringUtils.isNotEmpty(endinfo)) {
			result += endinfo;
		}
		return result;
	}

	private static String parseXrefData(DbData dbData, String dbParser,
			DbOper dbOper, DbEntry dbEntry) {
		if (!dbParser.startsWith("xref"))
			return dbData.getValue();
		String[] split = dbParser.split("\\|");
		if (split.length != 5)
			return "";
		String prefixPart = split[1];
		String tablePart = split[2].trim();
		String indexPart = split[3];
		String valuePart = split[4];
		List<DbEntry> entryList = dbOper.getDbTable(tablePart);
		if (CollectionUtils.isEmpty(entryList))
			return "";
		String regx = "[(][^()]+[)]";
		Pattern pat = Pattern.compile(regx);
		Matcher mat = null;
		List<IndexMatcher> indexMats = new ArrayList<IndexMatcher>();
		mat = pat.matcher(indexPart);
		while (mat.find()) {
			String group = mat.group();
			group = group.substring(1, group.length() - 1);
			String[] tmpSplit = group.split(":");
			if (tmpSplit.length != 2)
				return "";
			String[] tmpSplit1 = tmpSplit[0].split(",");
			String[] tmpSplit2 = tmpSplit[1].split(",");
			if (tmpSplit1.length != 3 || tmpSplit2.length != 3)
				return "";
			IndexMatcher matcher = new IndexMatcher();
			matcher.setRawAttr(tmpSplit1[0]);
			matcher.setRawStart(Integer.parseInt(tmpSplit1[1]));
			matcher.setRawLen(Integer.parseInt(tmpSplit1[2]));
			matcher.setTargetAttr(tmpSplit2[0]);
			matcher.setTargetStart(Integer.parseInt(tmpSplit2[1]));
			matcher.setTargetLen(Integer.parseInt(tmpSplit2[2]));
			indexMats.add(matcher);
		}

		List<ValueMatcher> valueMats = new ArrayList<ValueMatcher>();
		mat = pat.matcher(valuePart);
		while (mat.find()) {
			String group = mat.group();
			group = group.substring(1, group.length() - 1);
			ValueMatcher matcher = new ValueMatcher();
			if (group.startsWith("raw:")) {
				matcher.setRaw(true);
				matcher.setData(group.substring(4));
				valueMats.add(matcher);
			} else {
				String[] tmpSplit = group.split(",");
				if (tmpSplit.length != 4)
					return "";
				matcher.setAttr(tmpSplit[0]);
				matcher.setPoint(Integer.parseInt(tmpSplit[1]));
				matcher.setMask(Integer.parseInt(tmpSplit[2]));
				matcher.setOffset(Integer.parseInt(tmpSplit[3]));
				valueMats.add(matcher);
			}
		}

		DbEntry target = null;
		for (DbEntry entry : entryList) {
			boolean flag = true;
			for (IndexMatcher imat : indexMats) {
				DbData targetdata = entry.getDbDatas()
						.get(imat.getTargetAttr());
				DbData rawdata = dbEntry.getDbDatas().get(imat.getRawAttr());
				String targetdatavalue = completeHexString(
						targetdata.getValue(),
						imat.getTargetStart() + imat.getTargetLen());
				String rawdatavalue = completeHexString(rawdata.getValue(),
						imat.getRawStart() + imat.getRawLen());
				String sb1 = rawdatavalue.substring(imat.getRawStart(),
						imat.getRawStart() + imat.getRawLen());
				String sb2 = targetdatavalue.substring(imat.getTargetStart(),
						imat.getTargetStart() + imat.getTargetLen());
				flag &= sb1.equals(sb2);
			}
			if (flag) {
				target = entry;
				break;
			}
		}

		if (target == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(prefixPart);
		for (ValueMatcher vmat : valueMats) {
			if (vmat.isRaw()) {
				sb.append(vmat.getData());
			} else {
				long value = parseHex2Long(target.getDbDatas().get(
						vmat.getAttr()));
				if (vmat.getMask() == 0) {
					sb.append(value + vmat.getOffset());
				} else {
					sb.append(((value >> vmat.getPoint()) & ((int) Math.pow(2,
							vmat.getMask()) - 1)) + vmat.getOffset());
				}
			}
		}

		return sb.toString();
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
		raw = trimHexString(raw);
		if (StringUtils.isEmpty(raw))
			raw = "0";

		return "0x" + raw;
	}

	public static String trimHexString(String src) {
		if (StringUtils.isEmpty(src))
			return "";
		boolean flag = src.startsWith("0x");
		if (flag)
			src = src.substring(2);
		int sx = 0;
		for (int idx = 0; idx < src.length(); idx++) {
			char car = src.charAt(idx);
			if (car != '0')
				break;
			sx++;
		}
		src = src.substring(sx);
		if (flag)
			src = "0x" + src;
		return src;
	}

	public static String completeHexString(String src, int len) {
		if (src == null)
			src = "";
		if (src.length() >= len) {
			return src;
		}
		boolean flag = src.startsWith("0x");
		if (flag) {
			src = src.substring(2);
			len -= 2;
		}
		StringBuilder sb = new StringBuilder();
		if (flag)
			sb.append("0x");
		for (int l = 0; l < len - src.length(); l++) {
			sb.append("0");
		}
		sb.append(src);
		return sb.toString();
	}

	public static String parseDefaultValue(String src, DbOper oper,
			DbEntry entry) {
		if (src.startsWith("parser:")) {
			String regx = "[(][^()]+[)]";
			Pattern pat = Pattern.compile(regx);
			Matcher mat = pat.matcher(src);
			while (mat.find()) {
				String group = mat.group();
				group = group.substring(1, group.length() - 1);
				String[] split = group.split(":");
				if (split.length != 3)
					continue;
				DbData data = entry.getDbDatas().get(split[0]);
				if (data == null)
					return "";
				if (data.getValue().equals(split[1]))
					return split[2];
			}
		} else {
			return src;
		}

		return "";
	}

	public static boolean isDefaultValue(String data, String src, DbOper oper,
			DbEntry entry) {
		if (src.startsWith("parser:")) {
			String regx = "[(][^()]+[)]";
			Pattern pat = Pattern.compile(regx);
			Matcher mat = pat.matcher(src);
			while (mat.find()) {
				String group = mat.group();
				group = group.substring(1, group.length() - 1);
				String[] split = group.split(":");
				if (split.length != 3)
					continue;
				DbData dbdata = entry.getDbDatas().get(split[0]);
				if (dbdata == null)
					return false;
				if (dbdata.getValue().equals(split[1])) {
					String[] tmpsplit = split[2].split(",");
					for (String tmp : tmpsplit) {
						if (tmp.equals(data))
							return true;
					}
				}
			}
		} else {
			return src.equals(data);
		}

		return false;
	}

	public static void main(String[] args) {
		String str = "123>>456>7>>567";
		for (String tmp : str.split(">>")) {
			System.out.println(tmp);
		}
	}
}
