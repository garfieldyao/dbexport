package com.mars.dbexport.service;

import java.util.List;
import java.util.Map;

import com.mars.dbexport.bo.DbEntry;

public interface DbOper {
	public Map<String, List<DbEntry>> getDbDatas();

	public List<DbEntry> readDbFile(String tableName);
}
