package com.mars.dbexport.bo.prv;

public class IndexMatcher {
	private String rawAttr = "";
	private int rawStart = 0;
	private int rawLen = 0;
	private String targetAttr = "";
	private int targetStart = 0;
	private int targetLen = 0;

	public int getRawStart() {
		return rawStart;
	}

	public void setRawStart(int rawStart) {
		this.rawStart = rawStart;
	}

	public int getRawLen() {
		return rawLen;
	}

	public void setRawLen(int rawLen) {
		this.rawLen = rawLen;
	}

	public String getTargetAttr() {
		return targetAttr;
	}

	public void setTargetAttr(String targetAttr) {
		this.targetAttr = targetAttr;
	}

	public int getTargetStart() {
		return targetStart;
	}

	public void setTargetStart(int targetStart) {
		this.targetStart = targetStart;
	}

	public int getTargetLen() {
		return targetLen;
	}

	public void setTargetLen(int targetLen) {
		this.targetLen = targetLen;
	}

	public String getRawAttr() {
		return rawAttr;
	}

	public void setRawAttr(String rawAttr) {
		this.rawAttr = rawAttr;
	}
}
