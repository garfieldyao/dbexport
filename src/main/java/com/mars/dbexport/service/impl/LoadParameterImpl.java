package com.mars.dbexport.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;

import com.mars.dbexport.AppContext;
import com.mars.dbexport.bo.AppParamters;
import com.mars.dbexport.bo.CLIAttribute;
import com.mars.dbexport.bo.CLICommand;
import com.mars.dbexport.bo.DbIndex;
import com.mars.dbexport.bo.NetworkElement;
import com.mars.dbexport.bo.enums.OltType;
import com.mars.dbexport.service.LoadParameter;
import com.mars.dbexport.service.LogFactory;
import com.mars.dbexport.service.ResourceFactory;
import com.mars.dbexport.utils.FileUtils;

public class LoadParameterImpl implements LoadParameter {
	private Logger logger = AppContext.getLogFactory().getLogger(
			LogFactory.LOG_ERROR);

	@Override
	public boolean initSystemParam(String[] args) {
		// error msg
		StringBuilder errMsg = new StringBuilder();
		errMsg.append("java -jar dbexporter.jar [-auto] [-ip (ne ip)] [-in (database file)] [-(7360|7360l|7342epon|7342gpon)] [-skipcli] [-nofilter] [-trace]");
		errMsg.append("\r\n");
		errMsg.append("-auto : AUTO Mode");
		errMsg.append("\r\n");
		errMsg.append("-ip : NE IP address");
		errMsg.append("\r\n");
		errMsg.append("-in : Databse file");
		errMsg.append("\r\n");
		errMsg.append("(type) : Device type");
		errMsg.append("\r\n");
		errMsg.append("-skipcli : skip CLI");
		errMsg.append("\r\n");
		errMsg.append("-nofilter : disable filters");
		errMsg.append("\r\n");
		errMsg.append("\r\n");
		errMsg.append("java -jar dbexporter.jar -g");
		errMsg.append("\r\n");
		errMsg.append("-g : GUI mode");
		errMsg.append("\r\n");

		if (ArrayUtils.isEmpty(args)) {
			System.out.println((errMsg.toString()));
			return false;
		}

		String regx = null;
		Pattern pat = null;
		Matcher matcher = null;

		AppParamters appParameters = new AppParamters();
		AppContext.setAppParameters(appParameters);

		loadGlobalParam();
		loadCLIMapping();

		StringBuilder sb = new StringBuilder(" ");
		for (String arg : args) {
			sb.append(arg.trim());
			sb.append(" ");
		}
		String str = sb.toString();

		if (str.trim().contains("-g")) {
			appParameters.setGuiMode(true);
			return true;
		}

		regx = " -nofilter ";
		pat = Pattern.compile(regx);
		matcher = pat.matcher(str);
		appParameters.setNofilter(matcher.find());

		regx = " -skipcli ";
		pat = Pattern.compile(regx);
		matcher = pat.matcher(str);
		appParameters.setSkipcli(matcher.find());

		regx = " -auto ";
		pat = Pattern.compile(regx);
		matcher = pat.matcher(str);
		appParameters.setAuto(matcher.find());

		regx = " -trace ";
		pat = Pattern.compile(regx);
		matcher = pat.matcher(str);
		appParameters.setTrace(matcher.find());

		if (!appParameters.isSkipcli())
			loadInfoList();

		if (appParameters.isAuto()) {
			AppContext.setNeList(loadNeList());
		} else {
			NetworkElement ne = new NetworkElement();
			Map<String, NetworkElement> neList = new HashMap<String, NetworkElement>();
			AppContext.setNeList(neList);

			ne.setCommunity(appParameters.getCommunity());
			ne.setUserName(appParameters.getCliUser());
			ne.setPassword(appParameters.getCliPwd());

			regx = " -ip[ ]?[^ ]+ ";
			pat = Pattern.compile(regx);
			matcher = pat.matcher(str);
			if (matcher.find()) {
				String group = matcher.group().trim();
				ne.setIpAddress(group.substring(3).trim());
			}

			regx = " -in[ ]?[^ ]+ ";
			pat = Pattern.compile(regx);
			matcher = pat.matcher(str);
			if (matcher.find()) {
				String group = matcher.group().trim();
				ne.setDbPath(group.substring(3).trim());
			}

			if (StringUtils.isEmpty(ne.getIpAddress())
					&& StringUtils.isEmpty(ne.getDbPath())) {
				System.out.println((errMsg.toString()));
				return false;
			}

			if (StringUtils.isEmpty(ne.getDbPath())) {
				ne.setDbPath(AppContext.getResourceFactory().dataRoot
						+ ne.getIpAddress() + "/"
						+ AppContext.getResourceFactory().dbFile);
			}

			regx = "( -7342epon )|( -7342gpon )|( -7360 )|( -7360l )";
			pat = Pattern.compile(regx);
			matcher = pat.matcher(str);
			if (matcher.find()) {
				String group = matcher.group().toLowerCase().trim();
				if (group.contains("7342epon")) {
					ne.setType(OltType.EPON7342);
				} else if (group.contains("7342gpon")) {
					ne.setType(OltType.GPON7342);
				} else if (group.contains("7360l")) {
					ne.setType(OltType.FX7360L);
				} else if (group.contains("7360")) {
					ne.setType(OltType.FX7360);
				}
			}
			neList.put(ne.getIpAddress(), ne);
			FileUtils.createIpFolder(ne.getIpAddress());
			if (AppContext.debug) {
				System.out.println(ne.toString());
			}
		}

		if (AppContext.debug) {
			System.out.println(appParameters.toString());
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	private Map<String, NetworkElement> loadNeList() {
		ResourceFactory srcFactory = AppContext.getResourceFactory();
		Map<String, NetworkElement> neList = new HashMap<String, NetworkElement>();
		// TODO
		File file = srcFactory.getConfigFile(srcFactory.neFile);
		if (file == null) {
			logger.info("NE list is empty");
			return neList;
		}

		SAXReader reader = new SAXReader();
		try {
			Element rootNode = reader.read(file).getRootElement();
			List<Element> neNodes = rootNode.elements("ne");
			for (Element neNode : neNodes) {
				Element ipN = neNode.element("ip");
				if (ipN == null)
					continue;
				String ipV = ipN.getTextTrim();
				if (StringUtils.isEmpty(ipV))
					continue;
				NetworkElement ne = new NetworkElement();
				ne.setIpAddress(ipV);
				Element commN = neNode.element("community");
				if (commN != null)
					ne.setCommunity(commN.getText());
				Element cliuN = neNode.element("username");
				if (cliuN != null)
					ne.setUserName(cliuN.getText());
				Element pwdN = neNode.element("password");
				if (pwdN != null)
					ne.setPassword(pwdN.getText());
				ne.setDbPath(srcFactory.dataRoot + ne.getIpAddress() + "/"
						+ srcFactory.dbFile);
				ne.setType(OltType.FX7360);
				neList.put(ne.getIpAddress(), ne);
				FileUtils.createIpFolder(ipV);
				if (AppContext.debug) {
					System.out.println(ne.toString());
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("load ne list : " + e.getLocalizedMessage());
		}

		return neList;
	}

	private void loadGlobalParam() {
		// TODO
		AppParamters param = AppContext.getAppParamters();
		URL url = null;
		try {
			url = AppContext.getResourceFactory()
					.getConfigFile(AppContext.getResourceFactory().configFile)
					.toURI().toURL();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			logger.error("init params :" + e.getLocalizedMessage());
			return;
		}
		Properties prop = new Properties();
		try {
			prop.load(url.openStream());
		} catch (Exception ex) {
			logger.error("init params :" + ex.getLocalizedMessage());
			return;
		}

		String ftpIp = prop.getProperty("ftpIp");
		String ftpport = prop.getProperty("ftpport");
		String ftpuser = prop.getProperty("ftpuser");
		String ftppwd = prop.getProperty("ftppwd");
		String ftpdir = prop.getProperty("ftpdir");

		String cliuser = prop.getProperty("cliuser");
		String clipwd = prop.getProperty("clipwd");
		String snmpcommunity = prop.getProperty("snmpcommunity");
		String maxthread = prop.getProperty("maxthread");

		param.setFtpIp(ftpIp);
		param.setFtpUser(ftpuser);
		param.setFtpPwd(ftppwd);
		param.setFtpDir(ftpdir);

		param.setCliUser(cliuser);
		param.setCliPwd(clipwd);
		param.setCommunity(snmpcommunity);
		try {
			param.setMaxThread(Integer.parseInt(maxthread));
		} catch (Exception ex) {
			logger.error("init param MAXTHREAD :" + ex.getLocalizedMessage());
		}
		try {
			param.setFtpPort(Integer.parseInt(ftpport));
		} catch (Exception ex) {
			logger.error("init param FTPPORT :" + ex.getLocalizedMessage());
		}
	}

	@SuppressWarnings("unchecked")
	private void loadCLIMapping() {
		AppContext.getCliCommands().clear();

		List<File> mapFiles = AppContext.getResourceFactory().getMappingFiles();
		if (CollectionUtils.isEmpty(mapFiles))
			return;
		SAXReader reader = new SAXReader();
		for (File mapFile : mapFiles) {
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
						List<Element> attrNs = mappingRule
								.elements("attribute");
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
								cliattr.setDbAttribute(dbAttributeN
										.getTextTrim());
								if (parserN != null)
									cliattr.setParser(parserN.getTextTrim());
								if (defaultN != null)
									cliattr.setDefaultValue(defaultN
											.getTextTrim());
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
	}

	private void loadInfoList() {
		List<String> infoList = new ArrayList<String>();
		AppContext.setInfoList(infoList);
		File file = AppContext.getResourceFactory().getConfigFile(
				AppContext.getResourceFactory().infoFile);
		if (file == null || !file.isFile())
			return;
		BufferedReader reader = null;
		InputStreamReader sreader = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			sreader = new InputStreamReader(fis);
			reader = new BufferedReader(sreader);
			String line = reader.readLine();
			while (line != null) {
				String trim = line.trim();
				if (trim.startsWith("info")) {
					infoList.add(trim);
					if (AppContext.debug) {
						System.out.println(trim);
					}
				}
				line = reader.readLine();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Load CLI info list: " + e.getLocalizedMessage());
		} finally {
			try {
				fis.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				sreader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
