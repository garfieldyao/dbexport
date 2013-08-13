/**
 * 
 */
package com.mars.dbexporter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;

import com.mars.dbexporter.bo.AppParamters;
import com.mars.dbexporter.bo.CLICommand;
import com.mars.dbexporter.bo.DbEntry;
import com.mars.dbexporter.bo.enums.OltType;
import com.mars.dbexporter.service.ResourceFactory;
import com.mars.dbexporter.service.parse.Parser;

/**
 * @author Yao Liqiang
 * @data Aug 3, 2013
 * @description
 */
public class AppContext {
	private static ResourceFactory resourceFactory = null;
	private static AppParamters appParameters = null;
	private static List<String> dbTables;
	private static Map<String, List<DbEntry>> dbDatas;
	private static Set<String> dbSort;
	private static List<CLICommand> cliCommands;
	private static Map<String, Parser> parsers;

	public static ResourceFactory getResourceFactory() {
		if (resourceFactory == null)
			resourceFactory = new ResourceFactory();
		return resourceFactory;
	}

	public static boolean initAppParameters(String[] args) {
		// error msg
		StringBuilder errMsg = new StringBuilder();
		errMsg.append("java -jar dbexporter.jar -in (input) [-out (output)] [-(7360|7360l|7342epon|7342gpon)] [-skipdb] [-skiplanx] [-nofilter] [-trace]");
		errMsg.append("\r\n");
		errMsg.append("-in : Databse file");
		errMsg.append("\r\n");
		errMsg.append("-out : output file");
		errMsg.append("\r\n");
		errMsg.append("(type) : Device type");
		errMsg.append("\r\n");
		errMsg.append("-skipdb : skip Database convertion");
		errMsg.append("\r\n");
		errMsg.append("-skiplanx : skip IHUB");
		errMsg.append("\r\n");
		errMsg.append("-nofilter : disable the filter");
		errMsg.append("\r\n");
		errMsg.append("\r\n");
		errMsg.append("java -jar dbexporter.jar -g");
		errMsg.append("\r\n");
		errMsg.append("-g : GUI mode");
		errMsg.append("\r\n");

		String regx = null;
		Pattern pat = null;
		Matcher matcher = null;

		appParameters = new AppParamters();

		if (ArrayUtils.isEmpty(args)) {
			System.out.println(errMsg.toString());
			return false;
		}

		StringBuilder sb = new StringBuilder(" ");
		for (String arg : args) {
			sb.append(arg.trim());
			sb.append(" ");
		}
		String str = sb.toString();

		if ("-g".equals(str.trim())) {
			appParameters.setGuiMode(true);
			return true;
		}

		regx = " -in[ ]?[^ ]+ ";
		pat = Pattern.compile(regx);
		matcher = pat.matcher(str);
		if (matcher.find()) {
			String group = matcher.group().trim();
			appParameters.setGuiMode(false);
			appParameters.setDbPath(group.substring(3).trim());
		} else {
			System.out.println(errMsg.toString());
			return false;
		}

		regx = "( -7342epon )|( -7342gpon )|( -7360 )|( -7360l )";
		pat = Pattern.compile(regx);
		matcher = pat.matcher(str);
		if (matcher.find()) {
			String group = matcher.group().toLowerCase().trim();
			if (group.contains("7342epon")) {
				appParameters.setOltType(OltType.EPON7342);
			} else if (group.contains("7342gpon")) {
				appParameters.setOltType(OltType.GPON7342);
			} else if (group.contains("7360l")) {
				appParameters.setOltType(OltType.FX7360L);
			} else if (group.contains("7360")) {
				appParameters.setOltType(OltType.FX7360);
			}
		}

		regx = " -skipdb ";
		pat = Pattern.compile(regx);
		matcher = pat.matcher(str);
		appParameters.setSkipDb(matcher.find());

		regx = " -skiplanx ";
		pat = Pattern.compile(regx);
		matcher = pat.matcher(str);
		appParameters.setSkipLanx(matcher.find());

		regx = " -nofilter ";
		pat = Pattern.compile(regx);
		matcher = pat.matcher(str);
		appParameters.setNofilter(matcher.find());
		
		regx = " -trace ";
		pat = Pattern.compile(regx);
		matcher = pat.matcher(str);
		appParameters.setTrace(matcher.find());

		regx = " -out[ ]?[^ ]+ ";
		pat = Pattern.compile(regx);
		matcher = pat.matcher(str);
		if (matcher.find()) {
			String group = matcher.group().trim();
			appParameters.setDstPath(group.substring(4).trim());
		} else {
			appParameters.setDstPath(appParameters.getDbPath().substring(0,
					appParameters.getDbPath().length() - 4)
					+ ".sh");
		}

		return true;
	}

	public static Map<String, List<DbEntry>> getDbDatas() {
		if (dbDatas == null) {
			dbDatas = new HashMap<String, List<DbEntry>>();
		}

		return dbDatas;
	}

	public static AppParamters getAppParamters() {
		if (appParameters == null)
			appParameters = new AppParamters();
		return appParameters;
	}

	/**
	 * @return the dbTables
	 */
	public static List<String> getDbTables() {
		if (dbTables == null) {
			dbTables = new ArrayList<String>();
		}
		return dbTables;
	}

	/**
	 * @return the cliCommands
	 */
	public static List<CLICommand> getCliCommands() {
		if (cliCommands == null) {
			cliCommands = new ArrayList<CLICommand>();
		}
		return cliCommands;
	}

	public static Map<String, Parser> getParsers() {
		if (parsers == null) {
			parsers = new HashMap<String, Parser>();
		}
		return parsers;
	}

	/**
	 * @return the dbSort
	 */
	public static Set<String> getDbSort() {
		if (dbSort == null) {
			dbSort = new HashSet<String>();
		}
		return dbSort;
	}
}
