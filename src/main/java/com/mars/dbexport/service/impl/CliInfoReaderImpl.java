package com.mars.dbexport.service.impl;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;

import com.mars.dbexport.AppContext;
import com.mars.dbexport.bo.NetworkElement;
import com.mars.dbexport.service.CliInfoReader;
import com.mars.dbexport.service.LogFactory;

public class CliInfoReaderImpl implements CliInfoReader {
	private Logger logger = AppContext.getLogFactory().getLogger(
			LogFactory.LOG_RESULT);
	private final static String cliind = "configure";
	private final static String endind1 = "# ";
	private final static String endind2 = "#---";
	private final static String loginind = "login: ";
	private final static String passwordind = "password: ";
	private final static String failind1 = "incorrect";
	private final static String failind2 = "invalid token";
	private final static String precmd = "environment inhibit-alarms";

	private NetworkElement ne = null;
	private TelnetClient client = new TelnetClient();
	private PrintStream output = null;
	private InputStream input = null;

	public CliInfoReaderImpl(NetworkElement ne) {
		this.ne = ne;
	}

	@Override
	public List<String> readCliInfo() {
		List<String> cliInfo = new ArrayList<String>();
		logger.info(ne.getIpAddress() + "\t\tStart Retrive CLI");
		if (!login()) {
			logger.info(ne.getIpAddress() + "\t\tCLI login falied");
			logout();
			return cliInfo;
		}
		List<List<String>> tmpinfos = new ArrayList<List<String>>();
		try {
			write(precmd);
			readUntil(endind1, null);
		} catch (Exception e1) {
		}
		for (String xcmd : AppContext.getInfoList()) {
			try {
				tmpinfos.add(runCommand(xcmd, endind2));
			} catch (Exception e) {
			}
		}
		logout();
		boolean trace = AppContext.getAppParamters().isTrace();
		for (List<String> tmplist : tmpinfos) {
			if (CollectionUtils.isNotEmpty(tmplist)) {
				boolean flag = true;
				for (String tmpstr : tmplist) {
					if (StringUtils.isNotEmpty(tmpstr)
							&& tmpstr.startsWith(cliind)) {
						if (flag) {
							cliInfo.add("");
							flag = false;
						}
						cliInfo.add(tmpstr);
						if (trace) {
							System.out.println(tmpstr);
						}
					}
				}
			}
		}
		logger.info(ne.getIpAddress() + "\t\tRetrieve CLI finished");
		return cliInfo;
	}

	private boolean login() {
		try {
			client.connect(ne.getIpAddress(), 23);
			input = client.getInputStream();
			output = new PrintStream(client.getOutputStream());
			if (!readUntil(loginind, failind1))
				return false;
			if (!write(ne.getUserName()))
				return false;
			if (!readUntil(passwordind, failind1))
				return false;
			if (!write(ne.getPassword()))
				return false;
			if (!readUntil(endind1, failind1))
				return false;
		} catch (Exception ex) {
			return false;
		}
		return true;
	}

	private boolean readUntil(String indicator, String failed) throws Exception {
		StringBuilder sb = new StringBuilder();
		int read = input.read();
		while (read != -1) {
			sb.append((char) read);
			String str = sb.toString();
			if (str.endsWith(indicator)) {
				return true;
			}
			if (StringUtils.isNotEmpty(failed) && str.endsWith(failed)) {
				return false;
			}
			read = input.read();
		}
		return false;
	}

	private List<String> runCommand(String cmd, String ending) throws Exception {
		List<String> result = new ArrayList<String>();
		boolean write = write(cmd);
		if (!write)
			return result;
		StringBuilder sb = new StringBuilder();
		int read = input.read();
		while (read != -1) {
			String rst = sb.toString();
			if (read == '\n') {
				result.add(rst.trim());
				sb = new StringBuilder();
			} else {
				sb.append((char) read);
			}
			// System.out.println(rst);
			if (rst.contains(ending) || rst.contains(failind2))
				break;
			read = input.read();
		}
		return result;
	}

	private boolean write(String cmd) {
		output.println(cmd);
		output.flush();
		return true;
	}

	private void logout() {
		try {
			client.disconnect();
		} catch (Exception e) {
		}
		try {
			input.close();
			output.close();
		} catch (Exception e) {
		}
	}

	public static void main(String[] args) {
		NetworkElement ne = new NetworkElement();
		ne.setIpAddress("135.251.199.28");
		CliInfoReaderImpl cli = new CliInfoReaderImpl(ne);
		cli.login();
	}
}
