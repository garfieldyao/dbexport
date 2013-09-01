package com.mars.dbexport.service;

import java.util.List;

import com.mars.dbexport.bo.NetworkElement;

public interface CliInfoReader {
	public List<String> readCliInfo(NetworkElement ne);
}
