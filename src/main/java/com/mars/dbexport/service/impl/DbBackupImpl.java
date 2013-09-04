package com.mars.dbexport.service.impl;

import java.io.File;

import org.slf4j.Logger;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

import com.mars.dbexport.AppContext;
import com.mars.dbexport.bo.MibConst;
import com.mars.dbexport.bo.NetworkElement;
import com.mars.dbexport.bo.enums.OltType;
import com.mars.dbexport.service.DbBackup;
import com.mars.dbexport.service.LogFactory;
import com.mars.dbexport.service.SnmpOperator;
import com.mars.dbexport.utils.IfindexUtils;

public class DbBackupImpl implements DbBackup {
	private Logger logger = AppContext.getLogFactory().getLogger(
			LogFactory.LOG_RESULT);
	private NetworkElement ne = null;
	private SnmpOperator operator = null;

	public DbBackupImpl(NetworkElement ne) {
		this.ne = ne;
		operator = new SnmpOperator(ne);
	}

	@Override
	public boolean exportDb() {
		// TODO Auto-generated method stub
		OltType type = checkNeType();
		if (type == OltType.UNKNOWN)
			return false;
		ne.setType(type);
		if (!configFtpServer())
			return false;
		File file = new File(AppContext.getResourceFactory().dataRoot + "/"
				+ ne.getIpAddress() + "/"
				+ AppContext.getResourceFactory().dbFile);
		if (file.exists())
			file.delete();
		if (!uploadDb())
			return false;
		ne.setDbPath(file.getAbsolutePath());
		return true;
	}

	private OltType checkNeType() {
		OltType type = OltType.UNKNOWN;
		VariableBinding value = null;
		try {
			value = operator.getSingleValue(MibConst.asamMibVersion);
		} catch (Exception ex) {
			return type;
		}
		if (value != null && value.getVariable() != null) {
			type = IfindexUtils.checkOltType(value.getVariable().toString());
		}
		return type;
	}

	private boolean configFtpServer() {
		try {
			operator.setSingleValue(MibConst.fileTransferProtocolSelect,
					new Integer32(MibConst.FTP_MODE));
			boolean exist = operator
					.checkOidExist(MibConst.fileTransferServerID + "."
							+ AppContext.getAppParamters().getFtpIp());
			if (!exist) {
				String[] oids = new String[3];
				Variable[] binds = new Variable[3];
				oids[0] = MibConst.fileTransferServerRowStatus + "."
						+ AppContext.getAppParamters().getFtpIp();
				oids[1] = MibConst.fileTransferServerUsername + "."
						+ AppContext.getAppParamters().getFtpIp();
				oids[2] = MibConst.fileTransferServerPassword + "."
						+ AppContext.getAppParamters().getFtpIp();
				binds[0] = new Integer32(4);
				binds[1] = new OctetString(AppContext.getAppParamters()
						.getFtpUser());
				binds[2] = new OctetString(AppContext.getAppParamters()
						.getFtpPwd());

				operator.setValues(oids, binds);
			}
			exist = operator.checkOidExist(MibConst.fileTransferServerID + "."
					+ AppContext.getAppParamters().getFtpIp());
			return exist;
		} catch (Exception ex) {
			return false;
		}
	}

	public boolean uploadDb() {
		logger.info(ne.getIpAddress() + "\t\tStart Database Backup");
		try {
			operator.setSingleValue(MibConst.asamDbmUploadServerID,
					new IpAddress(AppContext.getAppParamters().getFtpIp()));
			operator.setSingleValue(
					MibConst.asamDbmUploadPath,
					new OctetString(AppContext.getAppParamters().getFtpDir()
							+ ne.getIpAddress() + "/"
							+ AppContext.getResourceFactory().dbFile));

			operator.setSingleValue(MibConst.asamDbmUploadDatabaseSelection,
					new Integer32(MibConst.ACTIVE_DB));
		} catch (Exception ex) {
			logger.info(ne.getIpAddress() + "\t\tDatabase Backup failed");
			return false;
		}

		while (true) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			VariableBinding bind = null;
			try {
				bind = operator.getSingleValue(MibConst.asamDbmUploadProgress,
						5000);
			} catch (Exception ex) {
				return false;
			}
			if (operator.varableIsNull(bind))
				return false;
			int state = bind.getVariable().toInt();
			if (state == MibConst.ongoing) {
				continue;
			} else if (state == MibConst.failed) {
				logger.info(ne.getIpAddress() + "\t\tDatabase Backup failed");
				return false;
			} else if (state == MibConst.succeed) {
				logger.info(ne.getIpAddress() + "\t\tDatabase Backup finished");
				return true;
			}
		}
	}

}
