package com.mars.dbexport.utils;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * Simple formular parser, support : + - × ÷ & | >> << and parentheses Just
 * support integers at present
 * 
 * @author Yao
 * 
 */
public class FormularParser {
	final static String add = "+";
	final static String sub = "-";
	final static String mul = "*";
	final static String div = "/";
	final static String and = "&";
	final static String or = "/";
	final static String left = "(";
	final static String right = ")";

	private static boolean validFormular(String formular) {
		if (StringUtils.isEmpty(formular))
			return false;
		if (formular.split(left).length != formular.split(right).length)
			return false;
		return true;
	}

	public static int calculateFormularValue(String formular,
			Map<String, Integer> params) {
		if (!validFormular(formular))
			return 0x80000000;
		for (char cc : formular.toCharArray()) {
			
		}
		return 0;
	}

	private static int processCellFragment(String frag) {
		return 0x80000000;
	}

	public static void main(String[] args) {
		int i = 0x80000000;
		System.out.println(i);
	}
}
