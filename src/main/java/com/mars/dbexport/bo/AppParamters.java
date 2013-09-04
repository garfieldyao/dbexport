/**
 * 
 */
package com.mars.dbexport.bo;


/**
 * @author Yao Liqiang
 * @data Aug 5, 2013
 * @description
 */
public class AppParamters {
	private boolean guiMode = false;

	private boolean trace = false;

	private boolean nofilter = false;

	private boolean auto = false;

	private boolean skipcli = false;

	private String ftpIp = "127.0.0.1";
	private String ftpUser = "user";
	private String ftpPwd = "user";
	private int ftpPort = 21;
	public String ftpDir = "dm_complete.tar";

	private String community = "public";
	private String cliUser = "isadmin";
	private String cliPwd = "ans#150";
	private int cliPort = 23;

	private int maxThread = 10;

	/**
	 * @return the guiMode
	 */
	public boolean isGuiMode() {
		return guiMode;
	}

	/**
	 * @param guiMode
	 *            the guiMode to set
	 */
	public void setGuiMode(boolean guiMode) {
		this.guiMode = guiMode;
	}

	/**
	 * @return the nofilter
	 */
	public boolean isNofilter() {
		return nofilter;
	}

	/**
	 * @param nofilter
	 *            the nofilter to set
	 */
	public void setNofilter(boolean nofilter) {
		this.nofilter = nofilter;
	}

	/**
	 * @return the trace
	 */
	public boolean isTrace() {
		return trace;
	}

	/**
	 * @param trace
	 *            the trace to set
	 */
	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	public boolean isAuto() {
		return auto;
	}

	public void setAuto(boolean auto) {
		this.auto = auto;
	}

	public boolean isSkipcli() {
		return skipcli;
	}

	public void setSkipcli(boolean skipcli) {
		this.skipcli = skipcli;
	}

	public String getFtpIp() {
		return ftpIp;
	}

	public void setFtpIp(String ftpIp) {
		this.ftpIp = ftpIp;
	}

	public String getFtpUser() {
		return ftpUser;
	}

	public void setFtpUser(String ftpUser) {
		this.ftpUser = ftpUser;
	}

	public String getFtpPwd() {
		return ftpPwd;
	}

	public void setFtpPwd(String ftpPwd) {
		this.ftpPwd = ftpPwd;
	}

	public int getFtpPort() {
		return ftpPort;
	}

	public void setFtpPort(int ftpPort) {
		this.ftpPort = ftpPort;
	}

	public String getFtpDir() {
		return ftpDir;
	}

	public void setFtpDir(String ftpDir) {
		this.ftpDir = ftpDir;
	}

	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public String getCliUser() {
		return cliUser;
	}

	public void setCliUser(String cliUser) {
		this.cliUser = cliUser;
	}

	public String getCliPwd() {
		return cliPwd;
	}

	public void setCliPwd(String cliPwd) {
		this.cliPwd = cliPwd;
	}

	public int getMaxThread() {
		return maxThread;
	}

	public void setMaxThread(int maxThread) {
		this.maxThread = maxThread;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder("AppParameters:");
		sb.append(" guiMode=");
		sb.append(guiMode ? "true" : "false");
		sb.append(" trace=");
		sb.append(trace ? "true" : "false");
		sb.append(" nofilter=");
		sb.append(nofilter ? "true" : "false");
		sb.append(" auto=");
		sb.append(auto ? "true" : "false");
		sb.append(" skipcli=");
		sb.append(skipcli ? "true" : "false");
		sb.append(" ftpIp=");
		sb.append(ftpIp);
		sb.append(" ftpUser=");
		sb.append(ftpUser);
		sb.append(" ftppwd=");
		sb.append(ftpPwd);
		sb.append(" ftpPort=");
		sb.append(ftpPort);
		sb.append(" ftpDir=");
		sb.append(ftpDir);
		sb.append(" ftpIp=");
		sb.append(ftpIp);
		sb.append(" community=");
		sb.append(community);
		sb.append(" cliUser=");
		sb.append(cliUser);
		sb.append(" clipwd=");
		sb.append(cliPwd);
		sb.append(" maxThread=");
		sb.append(maxThread);
		return sb.toString();
	}

	public int getCliPort() {
		return cliPort;
	}

	public void setCliPort(int cliPort) {
		this.cliPort = cliPort;
	}
}
