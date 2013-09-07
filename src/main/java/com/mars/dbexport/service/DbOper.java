package com.mars.dbexport.service;

import java.util.List;

import com.mars.dbexport.bo.DbEntry;

public interface DbOper {
	public List<DbEntry> getDbTable(String tableName);
}
