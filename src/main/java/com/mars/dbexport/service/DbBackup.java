package com.mars.dbexport.service;

import com.mars.dbexport.bo.NetworkElement;

public interface DbBackup {
	/**
	 * Export DB to FTP server
	 * Thread will be blocked until db export finished
	 * @param ne
	 * @return
	 */
	public boolean exportDb(NetworkElement ne);
}
