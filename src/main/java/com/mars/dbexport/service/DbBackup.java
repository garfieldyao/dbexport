package com.mars.dbexport.service;


public interface DbBackup {
	/**
	 * Export DB to FTP server
	 * Thread will be blocked until db export finished
	 * @param ne
	 * @return
	 */
	public boolean exportDb();
}
