/**
 * 
 */
package com.mars.dbexporter.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.mars.dbexporter.AppContext;
import com.mars.dbexporter.bo.CLIAttribute;
import com.mars.dbexporter.bo.CLICommand;
import com.mars.dbexporter.bo.DbData;
import com.mars.dbexporter.bo.DbEntry;
import com.mars.dbexporter.bo.DbIndex;
import com.mars.dbexporter.bo.enums.DataType;
import com.mars.dbexporter.bo.enums.ErrorCode;
import com.mars.dbexporter.bo.enums.OsType;
import com.mars.dbexporter.service.DbConvertor;
import com.mars.dbexporter.utils.GenericUtils;
import com.mars.dbexporter.utils.PlatformUtils;

/**
 * 
 * @author Yao Liqiang
 * @data Aug 3, 2013
 * @description
 */

public class DbConvertorImpl implements DbConvertor {
	private final String indicator = "xml database generated";
	private final String dbTableFile = "DbTables";
	private final String mappingFile = "DataMapping.xml";

	public DbConvertorImpl() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mars.dbexporter.task.DbConvertor#exportDb2xml(java.lang.String)
	 */
	@Override
	public void exportDb2xml(String filePath) throws Exception {
		// TODO Auto-generated method stub
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
			throw new RuntimeException(ErrorCode.ProcError.getDesc());
		}
	}

	@SuppressWarnings("unchecked")
	public void loadCLIMapping() {
		AppContext.getCliCommands().clear();
		File mapFile = AppContext.getResourceFactory().getMappingFile(
				mappingFile);
		if (mapFile == null)
			return;
		SAXReader reader = new SAXReader();
		try {
			Element rootNode = reader.read(mapFile).getRootElement();
			List<Element> mappingList = rootNode.elements("mapping");
			for (Element mappingEntry : mappingList) {
				List<Element> mappingRules = mappingEntry
						.elements("attributes");
				for (Element mappingRule : mappingRules) {
					Element prefixN = mappingRule.element("prefix");
					if (prefixN == null)
						continue;
					Element miniAttributesN = mappingRule
							.element("miniAttributes");
					CLICommand command = new CLICommand();
					command.setPrex(prefixN.getTextTrim());
					if (miniAttributesN != null) {
						command.setMiniAttributes(Integer
								.parseInt(miniAttributesN.getTextTrim()));
					}
					List<CLIAttribute> cliattrs = new ArrayList<CLIAttribute>();
					command.setAttributes(cliattrs);
					AppContext.getCliCommands().add(command);
					List<Element> attrNs = mappingRule.elements("attribute");
					for (Element attrN : attrNs) {
						Element clinameN = attrN.element("cliname");
						Element dbTableN = attrN.element("dbTable");
						Element dbAttributeN = attrN.element("dbAttribute");
						Element parserN = attrN.element("parser");
						Element indexN = attrN.element("index");
						Element novalueN = attrN.element("novalue");
						Element defaultN = attrN.element("default");
						CLIAttribute cliattr = new CLIAttribute();
						if (clinameN != null && dbTableN != null
								&& dbAttributeN != null) {
							cliattr.setCliName(clinameN.getTextTrim());
							cliattr.setDbTable(dbTableN.getTextTrim());
							cliattr.setDbAttribute(dbAttributeN.getTextTrim());
							if (parserN != null)
								cliattr.setParser(parserN.getTextTrim());
							if (defaultN != null)
								cliattr.setDefaultValue(defaultN.getTextTrim());
							if (indexN != null) {
								DbIndex dbIndex = new DbIndex();
								dbIndex.setDbTable(cliattr.getDbTable());
								cliattr.setIndex(dbIndex);
								String[] split = indexN.getTextTrim()
										.split(";");
								for (String tmpix : split) {
									tmpix = tmpix.trim();
									dbIndex.getDbIndex().add(
											tmpix.substring(0,
													tmpix.indexOf("(")));
									dbIndex.getDbIndexParser().add(
											tmpix.substring(
													tmpix.indexOf("(") + 1,
													tmpix.length() - 1));
								}
							}
							if (novalueN != null)
								cliattr.setNovalue("true".equals(novalueN
										.getTextTrim()));
							cliattrs.add(cliattr);
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void loadDbTables() {
		AppContext.getDbTables().clear();
		File file = AppContext.getResourceFactory().getMappingFile(dbTableFile);
		if (file == null)
			return;
		FileInputStream fin = null;
		BufferedReader reader = null;
		try {
			fin = new FileInputStream(file);
			reader = new BufferedReader(new InputStreamReader(fin));
			String line = null;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (StringUtils.isNotEmpty(line)) {
					AppContext.getDbTables().add(line);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				reader.close();
				fin.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mars.dbexporter.service.DbConvertor#loadDbData()
	 */
	@Override
	public void loadDbData() {
		// TODO Auto-generated method stub
		AppContext.getDbDatas().clear();
		for (String tableName : AppContext.getDbTables()) {
			AppContext.getDbDatas().put(tableName, readDbXml(tableName));
		}
	}

	@SuppressWarnings("unchecked")
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
					if (lengthN != null)
						dbData.setLength(Integer.parseInt(lengthN.getTextTrim()));
					if (initN != null)
						dbData.setDefaultValue(initN.getTextTrim().replaceAll(
								"=", ""));
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
							dbData.setLength(dbMapData.getLength());
							dbData.setDefaultValue(dbMapData.getDefaultValue());
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mars.dbexporter.service.DbConvertor#genrateCLICommands()
	 */
	@Override
	public List<String> genrateCLICommands() {
		// TODO Auto-generated method stub
		List<String> commands = new ArrayList<String>();
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
				GenericUtils.sortDbEntry(tmpattr,
						AppContext.getDbDatas().get(tmpattr.getDbTable()));
			}

			List<DbEntry> indexList = new ArrayList<DbEntry>();
			if (indexattr == null) {
				indexattr = attributes.get(0);
			}
			indexList = AppContext.getDbDatas().get(indexattr.getDbTable());

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
						dbData = AppContext.getDbDatas().get(attr.getDbTable())
								.get(idex).getDbDatas()
								.get(attr.getDbAttribute());
					} else {
						// TODO
						DbEntry tmpIndexEntry = indexList.get(idex);
						String tmpDbEntryIndex = GenericUtils
								.parseDbEntryIndex(indexattr, tmpIndexEntry);
						for (DbEntry tmpDbEntry : AppContext.getDbDatas().get(
								attr.getDbTable())) {
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
							attr.getParser(), dbData);
					if (!AppContext.getAppParamters().isNofilter()) {
						if (attr.getDefaultValue() != null) {
							if (attr.getDefaultValue().equals(dataValue))
								continue;
						} else if (dbData != null) {
							if (dataValue.equals(dbData.getDefaultValue()))
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
						sb.append("no ");
						sb.append(attr.getCliName());
						sb.append(" ");
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
					commands.add(cmd);
					if (trace)
						System.out.println(cmd);
				}
			}
		}
		return commands;
	}
}
