/**
 * 
 */
package com.mars.dbexport.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;

import com.mars.dbexport.AppContext;
import com.mars.dbexport.bo.CLIAttribute;
import com.mars.dbexport.bo.CLICommand;
import com.mars.dbexport.bo.DbData;
import com.mars.dbexport.bo.DbEntry;
import com.mars.dbexport.bo.DbIndex;
import com.mars.dbexport.bo.NetworkElement;
import com.mars.dbexport.bo.enums.DataType;
import com.mars.dbexport.bo.enums.ErrorCode;
import com.mars.dbexport.bo.enums.OsType;
import com.mars.dbexport.service.DbConvertor;
import com.mars.dbexport.service.LogFactory;
import com.mars.dbexport.utils.GenericUtils;
import com.mars.dbexport.utils.PlatformUtils;

/**
 * 
 * @author Yao Liqiang
 * @data Aug 3, 2013
 * @description
 */

public class DbConvertorImpl implements DbConvertor {
	private Logger logger = AppContext.getLogFactory().getLogger(
			LogFactory.LOG_RESULT);
	private final String indicator = "xml database generated";
	private NetworkElement ne = null;
	public Map<String, List<DbEntry>> dbDatas = new HashMap<String, List<DbEntry>>();
	private int milestone = 0;
	private TarArchiveInputStream taris = null;

	public DbConvertorImpl(NetworkElement ne) {
		this.ne = ne;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mars.dbexporter.service.DbConvertor#genrateCLICommands()
	 */
	@Override
	public List<String> genrateCLICommands() {
		// TODO Auto-generated method stub
		logger.info(ne.getIpAddress() + "\t\tStart Convert database file\t"
				+ ne.getDbPath());
		List<String> commands = new ArrayList<String>();
		String lastprefix = "";
		for (CLICommand comm : AppContext.getCliCommands()) {
			List<CLIAttribute> attributes = comm.getAttributes();
			if (CollectionUtils.isEmpty(attributes))
				continue;
			CLIAttribute indexattr = null;
			for (CLIAttribute tmpattr : attributes) {
				DbIndex tmpindex = tmpattr.getIndex();
				if (tmpindex.getDbIndex().contains(tmpattr.getDbAttribute())) {
					indexattr = tmpattr;
					break;
				}
			}

			// sort
			for (CLIAttribute tmpattr : attributes) {
				String tbName = tmpattr.getDbTable();
				List<DbEntry> entryList = dbDatas.get(tbName);
				if (entryList == null) {
					entryList = readDbFile(tbName);
					GenericUtils.sortDbEntry(tmpattr, entryList);
					dbDatas.put(tbName, entryList);
				}
			}

			List<DbEntry> indexList = new ArrayList<DbEntry>();
			if (indexattr == null) {
				indexattr = attributes.get(0);
			}
			indexList = dbDatas.get(indexattr.getDbTable());

			if (CollectionUtils.isEmpty(indexList))
				continue;
			StringBuilder sb = new StringBuilder();
			int count = 0;
			for (int idex = 0; idex < indexList.size(); idex++) {
				count = 0;
				sb = new StringBuilder();
				sb.append(comm.getPrex());
				sb.append(" ");
				for (CLIAttribute attr : attributes) {
					if (StringUtils.isEmpty(attr.getCliName())
							|| StringUtils.isEmpty(attr.getDbAttribute())
							|| StringUtils.isEmpty(attr.getDbTable())
							|| StringUtils.isEmpty(attr.getParser()))
						continue;
					DbData dbData = null;
					if (attr.getDbTable().equals(indexattr.getDbTable())) {
						dbData = dbDatas.get(attr.getDbTable()).get(idex)
								.getDbDatas().get(attr.getDbAttribute());
					} else {
						// TODO
						DbEntry tmpIndexEntry = indexList.get(idex);
						String tmpDbEntryIndex = GenericUtils
								.parseDbEntryIndex(indexattr, tmpIndexEntry);
						for (DbEntry tmpDbEntry : dbDatas
								.get(attr.getDbTable())) {
							if (GenericUtils
									.parseDbEntryIndex(attr, tmpDbEntry)
									.equals(tmpDbEntryIndex)) {
								dbData = tmpDbEntry.getDbDatas().get(
										attr.getDbAttribute());
								break;
							}
						}
					}

					if (!attr.getParser().startsWith("raw:")
							&& !attr.isNovalue() && dbData == null)
						continue;

					String dataValue = GenericUtils.parseDataValue(
							attr.getParser(), dbData, this);
					if (!AppContext.getAppParamters().isNofilter()) {
						if (attr.getDefaultValue() != null) {
							if (attr.getDefaultValue().equals(dataValue))
								continue;
						}
					}
					if (attr.isNovalue()) {
						if ("true".equals(dataValue)) {
							sb.append(attr.getCliName());
							sb.append(" ");
						} else if (AppContext.getAppParamters().isNofilter()) {
							sb.append("no ");
							sb.append(attr.getCliName());
							sb.append(" ");
						}
					} else if (StringUtils.isEmpty(dataValue)) {
						if (AppContext.getAppParamters().isNofilter()) {
							sb.append("no ");
							sb.append(attr.getCliName());
							sb.append(" ");
						}
					} else {
						sb.append(attr.getCliName());
						sb.append(" ");
						sb.append(dataValue);
						sb.append(" ");
					}
					count++;
				}

				boolean trace = AppContext.getAppParamters().isTrace();
				if (count >= comm.getMiniAttributes()) {
					String cmd = sb.toString().trim();
					if (!comm.getPrex().equals(lastprefix)) {
						if (!"".equals(lastprefix)) {
							commands.add("");
							if (trace)
								System.out.println();
						}
						lastprefix = comm.getPrex();
					}
					commands.add(cmd);
					if (trace)
						System.out.println(cmd);
				}
			}
		}
		logger.info(ne.getIpAddress() + "\t\tConvert database file finished");
		return commands;
	}

	public List<DbEntry> readDbFile(String tableName) {
		List<DbEntry> datas = new ArrayList<DbEntry>();
		// TODO
		File file = new File(ne.getDbPath());
		FileInputStream fis = null;
		BufferedInputStream bfin = null;
		try {
			fis = new FileInputStream(file);
			bfin = new BufferedInputStream(fis);
			taris = new TarArchiveInputStream(bfin);
			ArchiveEntry entry = taris.getNextEntry();
			boolean found = false;
			while (entry != null) {
				if (entry.getName().endsWith(tableName)) {
					found = true;
					break;
				}
				entry = taris.getNextEntry();
			}
			if (!found) {
				logger.error(ne.getIpAddress() + ":" + tableName + " not found");
				return datas;
			}

			milestone = 0;
			byte[] buffer = null;

			List<DbData> dbDef = new ArrayList<DbData>();

			// dummy
			read(2);

			// table id
			read(2);
			// table offset
			buffer = read(4);
			int toffset = GenericUtils.bytes2int(buffer);

			read(8);
			// Table Name
			read(16);
			// Record len
			buffer = read(4);
			int recordLen = GenericUtils.bytes2int(buffer);

			// Table info
			buffer = read(12);
			while (true) {
				buffer = read(4);
				if (buffer == null)
					break;

				// Attribute
				buffer = read(8);
				String attrName = GenericUtils.parseBufferString(buffer).trim();
				if ("__oi__".equals(attrName)) {
					read(20);
					continue;
				}
				if ("__sc__".equals(attrName))
					break;
				DbData dbdata = new DbData();
				dbdata.setName(attrName);
				dbDef.add(dbdata);

				// type
				buffer = read(4);
				int dtype = GenericUtils.bytes2int(buffer);

				// length
				buffer = read(4);
				dbdata.setLen(GenericUtils.bytes2int(buffer));

				DataType type = DataType.getType(dtype);
				dbdata.setType(type);

				// use init
				buffer = read(1);

				buffer = read(3);

				// init value
				buffer = read(4);

				// offset
				buffer = read(2);
				int currOffset = GenericUtils.bytes2int(buffer);
				dbdata.setOffset(currOffset);

				buffer = read(2);

				if (AppContext.debug)
					System.out.println(dbdata.toString() + "    coffset="
							+ currOffset);
			}

			read(toffset - milestone);

			while (true) {
				buffer = read(recordLen);
				if (buffer == null)
					break;

				byte[] sub = readbyte(buffer, 2, 2);
				String recoadId = GenericUtils.bytes2hex(sub);
				if ("0xffff".equals(recoadId))
					break;

				DbEntry tmpentry = new DbEntry();
				tmpentry.setTableName(tableName);
				datas.add(tmpentry);
				for (DbData dbdef : dbDef) {
					sub = readbyte(buffer, dbdef.getOffset(), dbdef.getLen());
					DbData tmpdata = new DbData();
					tmpdata.setName(dbdef.getName());
					tmpdata.setType(dbdef.getType());
					tmpdata.setLen(dbdef.getLen());
					tmpdata.setValue(GenericUtils.bytes2hex(sub));
					if (AppContext.debug)
						System.out.println(tmpdata.toString());
					tmpentry.getDbDatas().put(tmpdata.getName(), tmpdata);
				}
			}

		} catch (Exception ex) {
			logger.error(ne.getIpAddress() + ":" + "Read db failed : "
					+ ne.getDbPath());
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				bfin.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				taris.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return datas;
	}

	private byte[] read(int step) throws Exception {
		byte[] tmpbuffer = new byte[step];
		int read = taris.read(tmpbuffer, 0, step);
		if (read < 0)
			return null;
		milestone += step;
		return tmpbuffer;
	}

	private byte[] readbyte(byte[] buffer, int start, int len) {
		byte[] datas = new byte[len];
		for (int idx = 0; idx < len; idx++) {
			datas[idx] = buffer[start + idx];
		}
		return datas;
	}

	/*
	 * @see com.mars.dbexporter.task.DbConvertor#exportDb2xml(java.lang.String)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void exportDb2xml(String filePath) throws Exception {
		File srcFile = new File(filePath);
		if (!srcFile.exists()) {
			throw new FileNotFoundException("input file");
		}

		OsType osType = PlatformUtils.getPlatformType();
		if (StringUtils.isEmpty(osType.getExec())) {
			throw new UnsupportedOperationException(
					ErrorCode.PlatformNotSupport.toString());
		}

		File execFile = AppContext.getResourceFactory().getBinFile(
				osType.getExec());
		if (execFile == null) {
			throw new FileNotFoundException(ErrorCode.FileNotFound.toString());
		}

		File parent = execFile.getParentFile();

		// pre-process
		if (parent.isDirectory()) {
			for (File tmpfile : parent.listFiles()) {
				if (OsType.isConvertTool(tmpfile)) {
					if (!tmpfile.canExecute())
						tmpfile.setExecutable(true);
				} else if (tmpfile.getName().startsWith("baseDir_")
						|| tmpfile.getName().startsWith("prozone_")
						|| "xml".equals(tmpfile.getName())) {
					PlatformUtils.removeFile(tmpfile);
				}
			}
		}

		// change directory and execute PBMT tool
		StringBuilder sb = new StringBuilder();
		sb.append("cd ");
		sb.append(parent.getAbsolutePath());
		sb.append("/");
		sb.append("&&");
		sb.append("echo dm db2xml /x");
		sb.append("|");
		sb.append("./");
		sb.append(osType.getExec());
		sb.append(" -in ");
		sb.append(filePath);
		sb.append(" -dbg");
		String cmd = sb.toString();

		String[] executeCmd = new String[] { "/bin/sh", "-c", cmd };

		Process execConvert = Runtime.getRuntime().exec(executeCmd);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				execConvert.getInputStream()));
		String line = null;
		boolean found = false;
		while ((line = reader.readLine()) != null) {
			if (line.contains(indicator)) {
				found = true;
				break;
			}
		}

		try {
			reader.close();
			execConvert.destroy();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (!found) {
			throw new RuntimeException(ErrorCode.ProcError.toString());
		}
	}

	/**
	 * @deprecated
	 * @param fileName
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	private List<DbEntry> readDbXml(String fileName) {
		List<DbEntry> datas = new ArrayList<DbEntry>();
		File defineFile = AppContext.getResourceFactory().getDbDefineFile(
				fileName);
		File dataFile = AppContext.getResourceFactory().getDbDataFile(fileName);
		if (dataFile == null || defineFile == null) {
			return datas;
		}
		SAXReader reader = new SAXReader();
		Map<String, DbData> defMap = new HashMap<String, DbData>();
		try {
			Document defDoc = reader.read(defineFile);
			Element defRoot = defDoc.getRootElement();
			List<Element> defNods = defRoot.element("structure")
					.element("fieldList").elements("field");
			for (Element defNode : defNods) {
				Element dbNameN = defNode.element("dbName");
				Element typeN = defNode.element("type");
				Element lengthN = defNode.element("length");
				Element initN = defNode.element("init");
				if (dbNameN != null && typeN != null) {
					DbData dbData = new DbData();
					dbData.setName(dbNameN.getTextTrim());
					dbData.setType(DataType.getType(typeN.getTextTrim()));
					defMap.put(dbData.getName(), dbData);
				}
			}

			Document dataDoc = reader.read(dataFile);
			Element dataRoot = dataDoc.getRootElement();
			List<Element> recordList = dataRoot.element("recordList").elements(
					"record");
			for (Element record : recordList) {
				DbEntry entry = new DbEntry();
				entry.setTableName(fileName);
				Map<String, DbData> dbDataList = new HashMap<String, DbData>();
				entry.setDbDatas(dbDataList);
				datas.add(entry);
				List<Element> fields = record.elements("field");
				for (Element field : fields) {
					DbData dbData = new DbData();
					Element dbNameN = field.element("dbName");
					Element valueN = field.element("value");
					if (dbNameN != null && valueN != null) {
						String dbName = dbNameN.getTextTrim();
						DbData dbMapData = defMap.get(dbName);
						if (dbMapData != null) {
							dbData.setName(dbName);
							dbData.setValue(valueN.getTextTrim());
							dbData.setType(dbMapData.getType());
							dbDataList.put(dbName, dbData);
						}
					}
				}
			}

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return datas;
	}
}
