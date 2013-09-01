package com.mars.dbexport.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import com.mars.dbexport.AppContext;
import com.mars.dbexport.bo.NetworkElement;
import com.mars.dbexport.bo.SnmpException;

/**
 * <p>
 * Title: SnmpOperator
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author Yao Liqiang
 * @created 2011-11-13 ����7:03:42
 * @modified [who date description]
 * @check [who date description]
 */
public class SnmpOperator {
	private Logger logger = AppContext.getLogFactory().getLogger(
			LogFactory.LOG_ERROR);

	private final int MAX_SET_NUM = 10;
	private final int MAX_OID_NUM = 30;
	private final int MAX_RETRIES = 3;

	private NetworkElement ne = new NetworkElement();

	public SnmpOperator(NetworkElement ne) {
		this.ne = ne;
	}

	private CommunityTarget getSnmpSession() {
		CommunityTarget target = new CommunityTarget();
		target.setTimeout(ne.getTimeout());
		target.setAddress(GenericAddress.parse("udp:" + ne.getIpAddress() + "/"
				+ ne.getPort()));
		target.setRetries(MAX_RETRIES);
		target.setVersion(ne.getSnmpVersion());
		target.setCommunity(new OctetString(ne.getCommunity()));
		return target;
	}

	public VariableBinding getSingleValue(String oid, int timeout) {
		VariableBinding var = null;
		CommunityTarget session = getSnmpSession();
		session.setTimeout(timeout);
		DefaultUdpTransportMapping transport = null;
		Snmp snmp = null;
		try {
			transport = new DefaultUdpTransportMapping();
			transport.listen();
			snmp = new Snmp(transport);
			PDU pdu = new PDU();
			pdu.add(new VariableBinding(new OID(oid)));
			ResponseEvent responseEvent = snmp.get(pdu, session);
			PDU response = responseEvent.getResponse();
			if (response == null)
				throw new SnmpException(ne.getIpAddress(),
						SnmpException.SNMP_TIME_OUT);
			if (response.getErrorStatus() > 0)
				throw new SnmpException(ne.getIpAddress(),
						SnmpException.SNMP_GET_ERROR);
			VariableBinding[] bindings = response.toArray();
			if (ArrayUtils.isEmpty(bindings))
				throw new SnmpException(ne.getIpAddress(),
						SnmpException.SNMP_GET_ERROR);
			var = bindings[0];
		} catch (IOException ex) {
			logger.error(ne.getIpAddress(), ex);
		} finally {
			if (snmp != null) {
				try {
					snmp.close();
				} catch (IOException e) {
					snmp = null;
				}
			}

			if (transport != null) {
				try {
					transport.close();
				} catch (IOException e) {
					transport = null;
				}
			}

			session = null;

		}

		return var;
	}

	public VariableBinding getSingleValue(String oid) {
		return getSingleValue(oid, ne.getTimeout());
	}

	public VariableBinding[] getValues(String[] oids, int timeout) {
		VariableBinding[] vars = new VariableBinding[0];
		if (ArrayUtils.isEmpty(oids))
			return vars;
		int len = oids.length;
		if (len <= MAX_OID_NUM) {
			return getValue(oids, timeout);
		} else {
			List<VariableBinding> result = new ArrayList<VariableBinding>();
			int cnum = len / MAX_OID_NUM;
			int remainder = len - MAX_OID_NUM * cnum;
			String[][] tmpoids = remainder == 0 ? new String[cnum][]
					: new String[cnum + 1][];
			for (int k = 0; k < tmpoids.length; k++) {
				if (k < cnum) {
					tmpoids[k] = new String[MAX_OID_NUM];
					for (int j = 0; j < MAX_OID_NUM; j++) {
						tmpoids[k][j] = oids[MAX_OID_NUM * k + j];
					}
				} else {
					if (remainder == 0)
						continue;
					tmpoids[k] = new String[remainder];
					for (int j = 0; j < remainder; j++) {
						tmpoids[k][j] = oids[MAX_OID_NUM * k + j];
					}
				}
			}

			for (int k = 0; k < tmpoids.length; k++) {
				String[] toids = tmpoids[k];
				VariableBinding[] bindings = getValue(toids, timeout);
				for (VariableBinding bind : bindings) {
					result.add(bind);
				}
			}

			return result.toArray(new VariableBinding[result.size()]);
		}
	}

	public VariableBinding[] getValues(String[] oids) {
		return getValues(oids, ne.getTimeout());
	}

	public VariableBinding[] getValues(String[] oids, boolean step, int timeout) {
		if (!step) {
			return getValues(oids, timeout);
		} else {
			if (ArrayUtils.isEmpty(oids))
				return new VariableBinding[0];
			VariableBinding[] values = new VariableBinding[oids.length];
			for (int i = 0; i < oids.length; i++) {
				values[i] = getSingleValue(oids[i], timeout);
			}
			return values;
		}
	}

	private VariableBinding[] getValue(String[] oids, int timeout) {
		VariableBinding[] vars = new VariableBinding[0];
		if (ArrayUtils.isEmpty(oids))
			return vars;
		CommunityTarget session = getSnmpSession();
		session.setTimeout(timeout);
		DefaultUdpTransportMapping transport = null;
		Snmp snmp = null;

		try {
			transport = new DefaultUdpTransportMapping();
			transport.listen();
			snmp = new Snmp(transport);
			PDU pdu = new PDU();
			for (String oid : oids)
				pdu.add(new VariableBinding(new OID(oid)));
			ResponseEvent responseEvent = snmp.get(pdu, session);
			PDU response = responseEvent.getResponse();
			if (response == null)
				throw new SnmpException(ne.getIpAddress(),
						SnmpException.SNMP_TIME_OUT);
			if (response.getErrorStatus() > 0)
				throw new SnmpException(ne.getIpAddress(),
						SnmpException.SNMP_GET_ERROR);
			vars = response.toArray();
		} catch (IOException ex) {
			logger.error(ne.getIpAddress(), ex);
		} finally {
			if (snmp != null) {
				try {
					snmp.close();
				} catch (IOException e) {
					snmp = null;
				}
			}

			if (transport != null) {
				try {
					transport.close();
				} catch (IOException e) {
					transport = null;
				}
			}

			session = null;

		}

		return vars;
	}

	public void setSingleValue(String oid, Variable var) {
		CommunityTarget session = getSnmpSession();
		DefaultUdpTransportMapping transport = null;
		Snmp snmp = null;

		try {
			transport = new DefaultUdpTransportMapping();
			transport.listen();
			snmp = new Snmp(transport);
			PDU pdu = new PDU();
			pdu.add(new VariableBinding(new OID(oid), var));
			ResponseEvent responseEvent = snmp.set(pdu, session);
			PDU response = responseEvent.getResponse();
			if (response == null)
				throw new SnmpException(ne.getIpAddress(),
						SnmpException.SNMP_TIME_OUT);
			if (response.getErrorStatus() > 0)
				throw new SnmpException(ne.getIpAddress(),
						SnmpException.SNMP_SET_ERROR);
		} catch (IOException ex) {
			logger.error(ne.getIpAddress(), ex);
		} finally {
			if (snmp != null) {
				try {
					snmp.close();
				} catch (IOException e) {
					snmp = null;
				}
			}

			if (transport != null) {
				try {
					transport.close();
				} catch (IOException e) {
					transport = null;
				}
			}

			session = null;

		}
	}

	public void setValues(String[] oids, Variable[] vars) {
		if (ArrayUtils.isEmpty(oids) || ArrayUtils.isEmpty(vars)
				|| oids.length != vars.length)
			return;
		CommunityTarget session = getSnmpSession();
		DefaultUdpTransportMapping transport = null;
		Snmp snmp = null;

		try {
			transport = new DefaultUdpTransportMapping();
			transport.listen();
			snmp = new Snmp(transport);
			PDU pdu = new PDU();
			for (int k = 0; k < oids.length; k++) {
				pdu.add(new VariableBinding(new OID(oids[k]), vars[k]));
			}
			ResponseEvent responseEvent = snmp.set(pdu, session);
			PDU response = responseEvent.getResponse();
			if (response == null)
				throw new SnmpException(ne.getIpAddress(),
						SnmpException.SNMP_TIME_OUT);
			if (response.getErrorStatus() > 0)
				throw new SnmpException(ne.getIpAddress(),
						SnmpException.SNMP_SET_ERROR);
		} catch (IOException ex) {
			logger.error(ne.getIpAddress(), ex);
		} finally {
			if (snmp != null) {
				try {
					snmp.close();
				} catch (IOException e) {
					snmp = null;
				}
			}

			if (transport != null) {
				try {
					transport.close();
				} catch (IOException e) {
					transport = null;
				}
			}

			session = null;

		}
	}

	public void batchSetValues(String[] oids, Variable[] vars) {
		if (ArrayUtils.isEmpty(oids) || ArrayUtils.isEmpty(vars)
				|| oids.length != vars.length)
			return;
		int pnum = 0;
		while (pnum < oids.length) {
			String[] tmpoids = null;
			Variable[] tmpvars = null;
			if ((oids.length - pnum) < MAX_SET_NUM) {
				tmpoids = new String[oids.length - pnum];
				tmpvars = new Variable[oids.length - pnum];
			} else {
				tmpoids = new String[MAX_SET_NUM];
				tmpvars = new Variable[MAX_SET_NUM];
			}
			for (int cnum = 0; cnum < MAX_SET_NUM; cnum++) {
				if (pnum >= oids.length)
					break;
				tmpoids[cnum] = oids[pnum];
				tmpvars[cnum] = vars[pnum];
				pnum++;
			}
			setValues(tmpoids, tmpvars);
		}
	}

	/**
	 * Get bulk
	 * 
	 * @param oid
	 * @return
	 */
	public VariableBinding[] getTableColumnValues(String oid) {
		VariableBinding[] vars = new VariableBinding[0];
		CommunityTarget session = getSnmpSession();
		OID entryOid = new OID(oid);
		OID nextOid = entryOid;
		boolean complete = false;
		int MaxRepetitions = AppContext.getMaxRepetitions();
		List<VariableBinding> result = new ArrayList<VariableBinding>();

		DefaultUdpTransportMapping transport = null;
		Snmp snmp = null;

		try {
			transport = new DefaultUdpTransportMapping();
			transport.listen();
			snmp = new Snmp(transport);

			while (!complete) {
				PDU pdu = new PDU();
				pdu.add(new VariableBinding(nextOid));
				pdu.setMaxRepetitions(MaxRepetitions);
				ResponseEvent responseEvent = snmp.getBulk(pdu, session);
				PDU response = responseEvent.getResponse();
				if (response == null)
					throw new SnmpException(ne.getIpAddress(),
							SnmpException.SNMP_TIME_OUT);
				if (response.getErrorStatus() > 0)
					throw new SnmpException(ne.getIpAddress(),
							SnmpException.SNMP_GET_ERROR);
				VariableBinding[] bindings = response.toArray();
				if (ArrayUtils.isEmpty(bindings)) {
					complete = true;
					continue;
				}
				for (VariableBinding bind : bindings) {
					if (bind.getOid().startsWith(entryOid)) {
						result.add(bind);
						nextOid = bind.getOid();
					} else {
						complete = true;
						break;
					}
				}
			}

			vars = result.toArray(new VariableBinding[result.size()]);

		} catch (IOException ex) {
			logger.error(ne.getIpAddress(), ex);
		} finally {
			if (snmp != null) {
				try {
					snmp.close();
				} catch (IOException e) {
					snmp = null;
				}
			}

			if (transport != null) {
				try {
					transport.close();
				} catch (IOException e) {
					transport = null;
				}
			}

			session = null;

		}

		return vars;
	}

	/**
	 * @param indexList
	 * @param adminPortOid
	 * @return
	 */
	public VariableBinding[] getColumnByIndex(List<String> indexes, String oid) {
		return getColumnByIndex(indexes, oid, ne.getTimeout());
	}

	public VariableBinding[] getColumnByIndex(List<String> indexes, String oid,
			int timeout) {
		if (CollectionUtils.isEmpty(indexes))
			return new VariableBinding[0];
		String[] oids = new String[indexes.size()];
		for (int i = 0; i < indexes.size(); i++) {
			oids[i] = oid + indexes.get(i);
		}
		return getValues(oids, timeout);
	}

	public VariableBinding[][] getColumnsByIndex(List<String> indexes,
			String... columnOids) {
		if (CollectionUtils.isEmpty(indexes) || ArrayUtils.isEmpty(columnOids))
			return new VariableBinding[0][0];
		int size = indexes.size();
		int columnSize = columnOids.length;
		VariableBinding[][] binds = new VariableBinding[size][columnSize];
		List<VariableBinding[]> bindList = new ArrayList<VariableBinding[]>();
		for (String columnOid : columnOids) {
			bindList.add(getColumnByIndex(indexes, columnOid));
		}
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < columnSize; j++) {
				binds[i][j] = bindList.get(j)[i];
			}
		}
		return binds;
	}
}
