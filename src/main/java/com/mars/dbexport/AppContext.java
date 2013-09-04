/**
 * 
 */
package com.mars.dbexport;

import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.lang.ArrayUtils;

import com.mars.dbexport.bo.AppParamters;
import com.mars.dbexport.bo.CLICommand;
import com.mars.dbexport.bo.NetworkElement;
import com.mars.dbexport.gui.AppMainFrame;
import com.mars.dbexport.service.LogFactory;
import com.mars.dbexport.service.ResourceFactory;
import com.mars.dbexport.service.parse.Parser;

/**
 * @author Yao Liqiang
 * @data Aug 3, 2013
 * @description
 */
public class AppContext {
	public static boolean debug = false;
	private static ResourceFactory resourceFactory = null;
	private static AppParamters appParameters = null;
	private static List<CLICommand> cliCommands;
	private static List<String> infoList;
	private static Map<String, NetworkElement> neList;
	private static Map<String, Parser> parsers;

	private static AppMainFrame mainFrame;
	private static Map<String, Icon> iconList;
	private static LogFactory logFactory;
	private static int SnmpMaxRepetitions = 40;

	public static ResourceFactory getResourceFactory() {
		if (resourceFactory == null)
			resourceFactory = new ResourceFactory();
		return resourceFactory;
	}

	public static AppParamters getAppParamters() {
		if (appParameters == null)
			appParameters = new AppParamters();
		return appParameters;
	}

	public static void setAppParameters(AppParamters appParameters) {
		AppContext.appParameters = appParameters;
	}

	public static Map<String, Parser> getParsers() {
		if (parsers == null) {
			parsers = new ConcurrentHashMap<String, Parser>();
		}
		return parsers;
	}

	public static AppMainFrame getMainFrame() {
		return mainFrame;
	}

	public static void setMainFrame(AppMainFrame mainFrame) {
		AppContext.mainFrame = mainFrame;
	}

	public static String getI18nString(String key) {
		return getResourceFactory().getI18NString(key);
	}

	public static String[] getI18nString(String... keys) {
		if (ArrayUtils.isEmpty(keys))
			return new String[0];
		List<String> values = new ArrayList<String>();
		for (String key : keys) {
			values.add(getI18nString(key));
		}
		return values.toArray(new String[0]);
	}

	public static Map<String, Icon> getIconList() {
		if (iconList == null)
			iconList = new HashMap<String, Icon>();
		return iconList;
	}

	public static Icon getIcon(String iconName) {
		if (getIconList().containsKey(iconName))
			return iconList.get(iconName);
		Icon icon = new ImageIcon(getResourceFactory().getImageSource(iconName));
		if (icon != null)
			iconList.put(iconName, icon);
		return icon;
	}

	public static Image getImage(String name) {
		Icon icon = getIcon(name);
		if (icon == null)
			return null;
		return ((ImageIcon) icon).getImage();
	}

	public static Image getLogoIcon() {
		String name = "alu.png";
		Icon icon = getIcon(name);
		if (icon == null)
			return null;
		return ((ImageIcon) icon).getImage();
	}

	public static String getLanguage() {
		// TODO Auto-generated method stub
		return "en";
	}

	public static int getMaxRepetitions() {
		// TODO Auto-generated method stub
		return SnmpMaxRepetitions;
	}

	public static LogFactory getLogFactory() {
		if (logFactory == null)
			logFactory = new LogFactory();
		return logFactory;
	}

	public static List<CLICommand> getCliCommands() {
		if (cliCommands == null)
			cliCommands = new ArrayList<CLICommand>();
		return cliCommands;
	}

	public static Map<String, NetworkElement> getNeList() {
		if (neList == null)
			neList = new HashMap<String, NetworkElement>();
		return neList;
	}

	public static void setNeList(Map<String, NetworkElement> neList) {
		AppContext.neList = neList;
	}

	public static List<String> getInfoList() {
		if (infoList == null)
			infoList = new ArrayList<String>();
		return infoList;
	}

	public static void setInfoList(List<String> infoList) {
		AppContext.infoList = infoList;
	}
}
