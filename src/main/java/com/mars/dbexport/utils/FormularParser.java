package com.mars.dbexport.utils;

import java.util.ArrayList;
import java.util.List;
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
		return 0;
	}

	@SuppressWarnings("unused")
	private static int processCellFragment(String frag) {
		return 0x80000000;
	}

	public static int findNearPair(String src, int local) {
		if (src.length() <= local)
			return -1;
		char sc = src.charAt(local);
		char ec = 0;
		if (sc == '(') {
			ec = ')';
		} else if (sc == '[') {
			ec = ']';
		} else if (sc == '{') {
			ec = '}';
		} else {
			return -1;
		}

		int cs = 0;
		int ce = 0;
		int idx = 0;
		boolean find = false;
		for (idx = local; idx < src.length(); idx++) {
			char tc = src.charAt(idx);
			if (tc == sc) {
				cs++;
			} else if (tc == ec) {
				ce++;
			}

			if (cs == ce) {
				find = true;
				break;
			}
		}

		return find ? idx : -1;
	}

	public static String findNearPairString(String src, int local) {
		int pair = findNearPair(src, local);
		return pair > 0 ? src.substring(local + 1, pair) : "";
	}

	public static List<String> findPairs(String src) {
		List<String> pairs = new ArrayList<String>();
		int sc = 0;
		int fc = -1;
		while (true) {
			fc = findNearPair(src, sc);
			if (fc < 0)
				break;
			pairs.add(src.substring(sc + 1, fc));
			sc = fc + 1;
		}
		return pairs;
	}

	public static void main(String[] args) {
		String str = "(1{22}23)(4[111]56)(78(99))";
		List<String> pairs = findPairs(str);
		for(String pa : pairs){
			System.out.println(pa);
		}
	}
}
