/*
 * $Id: NetworkElement.java, 2013-4-16 ����10:19:55 Yao Exp $
 * 
 * Copyright (c) 2011 Alcatel-Lucent Shanghai Bell Co., Ltd 
 * All rights reserved.
 * 
 * This software is copyrighted and owned by ASB or the copyright holder
 * specified, unless otherwise noted, and may not be reproduced or distributed
 * in whole or in part in any form or medium without express written permission.
 */
package com.mars.dbexport.bo;

import java.io.Serializable;

import org.snmp4j.mp.SnmpConstants;

import com.mars.dbexport.bo.enums.OltType;

/**
 * <p>
 * Title: NetworkElement
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author Yao Liqiang
 * @created 2013-4-16 ����10:19:55
 * @modified [who date description]
 * @check [who date description]
 */
public class NetworkElement implements Serializable {
	private static final long serialVersionUID = 7274766274937998281L;

	private OltType type = OltType.FX7360;
	private String ipAddress = "";
	private String community = "public";
	private int port = 161;
	private int timeout = 3000;
	private int snmpVersion = SnmpConstants.version2c;
	private boolean online = true;
	private String userName = "isadmin";
	private String password = "ans#150";
	private String dbPath = "";

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @param ipAddress
	 *            the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * @return the community
	 */
	public String getCommunity() {
		return community;
	}

	/**
	 * @param community
	 *            the community to set
	 */
	public void setCommunity(String community) {
		this.community = community;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the timeout
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * @param timeout
	 *            the timeout to set
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * @return the snmpVersion
	 */
	public int getSnmpVersion() {
		return snmpVersion;
	}

	/**
	 * @param snmpVersion
	 *            the snmpVersion to set
	 */
	public void setSnmpVersion(int snmpVersion) {
		this.snmpVersion = snmpVersion;
	}

	/**
	 * @return the online
	 */
	public boolean isOnline() {
		return online;
	}

	/**
	 * @param online
	 *            the online to set
	 */
	public void setOnline(boolean online) {
		this.online = online;
	}

	public OltType getType() {
		return type;
	}

	public void setType(OltType type) {
		this.type = type;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDbPath() {
		return dbPath;
	}

	public void setDbPath(String dbPath) {
		this.dbPath = dbPath;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder("NE:");
		sb.append(" ip=");
		sb.append(ipAddress);
		sb.append(" type=");
		sb.append(type);
		sb.append(" community=");
		sb.append(community);
		sb.append(" userName=");
		sb.append(userName);
		sb.append(" password=");
		sb.append(password);
		sb.append(" dbPath=");
		sb.append(dbPath);
		return sb.toString();
	}
}
