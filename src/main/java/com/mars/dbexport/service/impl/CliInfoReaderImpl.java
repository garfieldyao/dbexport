package com.mars.dbexport.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mars.dbexport.bo.NetworkElement;
import com.mars.dbexport.service.CliInfoReader;

public class CliInfoReaderImpl implements CliInfoReader {

	@Override
	public List<String> readCliInfo(NetworkElement ne) {
		List<String> cliInfo = new ArrayList<String>();
		//TODO
		cliInfo.add("configure equipment slot lt:1/1/1 admin-state up");
		return cliInfo;
	}
}
